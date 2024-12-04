package ruon.android.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;

import com.ruon.app.R;
import com.ruon.app.databinding.ActivityLoginBinding;

import java.util.Calendar;

import ruon.android.model.MyPreferenceManager;
import ruon.android.model.NetworkResult;
import ruon.android.model.WindowInsetsManager;
import ruon.android.net.FcmRegisterWS;
import ruon.android.net.LoginWS;
import ruon.android.net.NetworkTask;
import ruon.android.util.InfoDialog;
import ruon.android.util.NetworkUtils;
import ruon.android.util.UserLog;

public class LoginActivity extends WorkerActivity implements NetworkTask.NetworkTaskListener {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private NetworkTask mTask;
    private Handler mHandler;
    private String mToken;
    private int mFcmWaitCounter;

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        new WindowInsetsManager().handleWindowInsets(this, binding.container);

        updateViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIsAlreadyLoggedIn();
    }

    @Override
    protected void onPause() {
        if (mTask != null) {
            hideProgress();
            mTask.cancel(true);
        }
        if (mHandler != null) {
            hideProgress();
            mHandler.removeCallbacks(mGcmChecker);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void OnResult(NetworkResult result, Object o) {
        mToken = (String) o;
        if (result == NetworkResult.ERROR) {
            hideProgress();
            binding.alertMessage.setText(mToken);
        } else {
            UserLog.i(TAG, "Token - " + mToken);

            String username = binding.email.getText().toString().trim();
            String password = binding.password.getText().toString().trim();
            String token = MyPreferenceManager.getFcmToken(this);
            if (token != null && !MyPreferenceManager.isFcmRegisteredOnOurServer(this)) {
                UserLog.i(TAG, "Should register on Server!! - " + username);
                FcmRegisterWS task = new FcmRegisterWS(username, password, android.os.Build.MODEL, token, (result1, o1) -> {
                    UserLog.i(TAG, "Server register response! - " + result1);
                    if (result1 == NetworkResult.OK) {
                        UserLog.i(TAG, "Successfully registered on server");
                        MyPreferenceManager.setFcmRegisteredOnServer(this, true);
                    }
                });
                task.execute();
            }

            mHandler = new Handler();
            mHandler.postDelayed(mGcmChecker, 2 * DateUtils.SECOND_IN_MILLIS);
        }
    }

    /**
     * Since we do not want to save username and password and we don't want to use a token
     * for GCMRegister API, we need to handle this right after successful login and we need
     * to wait the result here.. If GCM registration fails, we will show message "Authentication failed"
     */
    private Runnable mGcmChecker = new Runnable() {
        @Override
        public void run() {
            boolean isRegistered = MyPreferenceManager.isFcmRegisteredOnOurServer(LoginActivity.this);
            mFcmWaitCounter++;
            if (isRegistered) {
                hideProgress();
                MyPreferenceManager.saveToken(LoginActivity.this, mToken);
                Intent mainScreen = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainScreen);
                finish();
            } else {
                if (mFcmWaitCounter < 13) {
                    mHandler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);
                } else {
                    hideProgress();
                    binding.alertMessage.setText(getString(R.string.authentication_error));
                }
            }
        }
    };

    private void login() {
        UserLog.i(TAG, "DoLogin");

        mFcmWaitCounter = 0;
        binding.alertMessage.setText("");
        try {
            pleaseabilityCheck();
            showProgress();
            mTask = new LoginWS(binding.email.getText().toString(), binding.password.getText().toString(), this);
            mTask.execute();
        } catch (IllegalArgumentException e) {
            InfoDialog.showDialog(this, e.getMessage());
        }
    }

    private void pleaseabilityCheck() throws IllegalArgumentException {
        if (!NetworkUtils.isNetworkAvailable(this)) {
            throw new IllegalArgumentException(getString(R.string.network_error));
        } else if (TextUtils.isEmpty(binding.email.getText())) {
            throw new IllegalArgumentException(getString(R.string.empty_email_title));
        } else if (TextUtils.isEmpty(binding.password.getText())) {
            throw new IllegalArgumentException(getString(R.string.empty_password_title));
        }
    }

    private void checkIsAlreadyLoggedIn() {
        String token = MyPreferenceManager.getToken(this);
        String pushToken = MyPreferenceManager.getFcmToken(this);
        UserLog.i(TAG, "Token - " + token);
        UserLog.i(TAG, "PushToken - " + pushToken);
        if (!TextUtils.isEmpty(token)) {
            Intent mainScreen = new Intent(this, MainActivity.class);
            startActivity(mainScreen);
            finish();
        }
    }

    private void updateViews() {
        binding.loginBtn.setOnClickListener(v -> login());

        String raw = getString(R.string.open_account_hint);
        SpannableString spannablecontent = new SpannableString(raw);
        spannablecontent.setSpan(
                new ClickableSpan() {
                    @Override
                    public void onClick(View view) {
                        String url = "https://www.r-u-on.com/";
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    }
                },
                25,
                40,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        spannablecontent.setSpan(new ForegroundColorSpan(Color.parseColor("#88b057")),
                25, 40, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        binding.accountHint.setMovementMethod(LinkMovementMethod.getInstance());
        binding.accountHint.setText(spannablecontent);
        binding.versionName.setText(getVersionName());
        binding.copyright.setText(getCopyrightText());
    }

    public String getVersionName() {
        String version = null;
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return getString(R.string.app_name);
        }

        return getString(R.string.app_name) + " " + version;
    }

    public String getCopyrightText() {
        Calendar now = Calendar.getInstance();

        return String.format(getString(R.string.login_copy_right_message), now.get(Calendar.YEAR));
    }
}
