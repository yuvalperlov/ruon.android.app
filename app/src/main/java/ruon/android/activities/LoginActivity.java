package ruon.android.activities;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
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
import android.widget.EditText;
import android.widget.TextView;

import com.ruon.app.R;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import ruon.android.model.MyPreferenceManager;
import ruon.android.model.NetworkResult;
import ruon.android.net.LoginWS;
import ruon.android.net.NetworkTask;
import ruon.android.services.TokenRegistrator;
import ruon.android.util.InfoDialog;
import ruon.android.util.NetworkUtils;
import ruon.android.util.UserLog;


public class LoginActivity extends WorkerActivity implements NetworkTask.NetworkTaskListener{
    private static final String TAG = LoginActivity.class.getSimpleName();

    @InjectView(R.id.email)
    EditText mEmail;
    @InjectView(R.id.password)
    EditText mPassword;
    @InjectView(R.id.account_hint)
    TextView mAddAcciountHint;
    @InjectView(R.id.alert_message)
    TextView mAlertMessage;
    @InjectView(R.id.version_name)
    TextView mVersionName;
    @InjectView(R.id.copyright)
    TextView mCopyright;

    private NetworkTask mTask;
    private Handler mHandler;
    private String mToken;
    private int mFcmWaitCounter;

    @OnClick(R.id.login_btn)
    public void login() {
        UserLog.i(TAG, "DoLogin");


        mFcmWaitCounter = 0;
        mAlertMessage.setText("");
        try{
            pleaseabilityCheck();
            showProgress();
            mTask = new LoginWS(mEmail.getText().toString(), mPassword.getText().toString(), this);
            mTask.execute();
        } catch (IllegalArgumentException e){
            InfoDialog.showDialog(this, e.getMessage());
        }
    }

    private void pleaseabilityCheck() throws IllegalArgumentException{
        if(!NetworkUtils.isNetworkAvailable(this)){
            throw new IllegalArgumentException(getString(R.string.network_error));
        }else if(TextUtils.isEmpty(mEmail.getText())){
            throw new IllegalArgumentException(getString(R.string.empty_email_title));
        } else if(TextUtils.isEmpty(mPassword.getText())){
            throw new IllegalArgumentException(getString(R.string.empty_password_title));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        updateViews();
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = getString(R.string.default_notification_channel_id);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (mNotificationManager == null) return;
            NotificationChannel channel = mNotificationManager.getNotificationChannel(channelId);
            if (channel != null) return; //no need to recreate notification channel

            String name = getString(R.string.default_channel_name);
            String description = getString(R.string.default_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            mNotificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIsAlreadyLoggedIn();
    }

    private void checkIsAlreadyLoggedIn() {
        String token = MyPreferenceManager.getToken(this);
        String pushToken = MyPreferenceManager.getFcmToken(this);
        UserLog.i(TAG, "Token - " + token);
        UserLog.i(TAG, "PushToken - " + pushToken);
        if(!TextUtils.isEmpty(token)){
            Intent mainScreen = new Intent(this, MainActivity.class);
            startActivity(mainScreen);
            finish();
        }
    }

    private void updateViews() {
        String raw = getString(R.string.open_account_hint);
        SpannableString spannablecontent = new SpannableString(raw);
        spannablecontent.setSpan(new ClickableSpan() {
                                     @Override
                                     public void onClick(View view) {
                                         String url = "https://www.r-u-on.com/";
                                         Intent i = new Intent(Intent.ACTION_VIEW);
                                         i.setData(Uri.parse(url));
                                         startActivity(i);
                                     }
                                 },
                25,40 , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannablecontent.setSpan(new ForegroundColorSpan(Color.parseColor("#88b057")),
                25,40 , Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        mAddAcciountHint.setMovementMethod(LinkMovementMethod.getInstance());
        mAddAcciountHint.setText(spannablecontent);
        mVersionName.setText(getVersionName());
        mCopyright.setText(getCopyrightText());
    }

    @Override
    protected void onPause() {
        if(mTask != null){
            hideProgress();
            mTask.cancel(true);
        }
        if(mHandler != null){
            hideProgress();
            mHandler.removeCallbacks(mFcmChecker);
        }
        super.onPause();
    }

    @Override
    public void OnResult(NetworkResult result, Object o) {
        mToken = (String) o;
        if(result == NetworkResult.ERROR){
            hideProgress();
            mAlertMessage.setText(mToken);
        }else{
            UserLog.i(TAG, "Token - " + mToken);

            String username = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            // Register for push notifications and wait for a result
            new TokenRegistrator(username, password, this).registerFirebaseMessagingToken();
            mHandler = new Handler();
            mHandler.postDelayed(mFcmChecker, 2 * DateUtils.SECOND_IN_MILLIS);
        }
    }

    /**
     * Since we do not want to save username and password and we don't want to use a token
     * for FCMRegister API, we need to handle this right after successful login and we need
     * to wait the result here.. If FCM registration fails, we will show message "Authentication failed"
     */
    private Runnable mFcmChecker = new Runnable() {
        @Override
        public void run() {
            boolean isRegistered = MyPreferenceManager.isFcmRegisteredOnOurServer(LoginActivity.this);
            mFcmWaitCounter++;
            if(isRegistered){
                hideProgress();
                MyPreferenceManager.saveToken(LoginActivity.this, mToken);
                Intent mainScreen = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(mainScreen);
                finish();
            } else{
                if(mFcmWaitCounter < 13){
                    mHandler.postDelayed(this, DateUtils.SECOND_IN_MILLIS);
                }else{
                    hideProgress();
                    mAlertMessage.setText(getString(R.string.authentication_error));
                }
            }
        }
    };

    public String getVersionName() {
        String version = null;
        try{
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        }catch (PackageManager.NameNotFoundException e){
            return getString(R.string.app_name);
        }

        return getString(R.string.app_name) + " " + version;
    }

    public String getCopyrightText() {
        Calendar now = Calendar.getInstance();

        return String.format(getString(R.string.login_copy_right_message), String.valueOf(now.get(Calendar.YEAR)));
    }
}
