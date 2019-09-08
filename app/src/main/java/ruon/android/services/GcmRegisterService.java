package ruon.android.services;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import com.ruon.app.R;
import ruon.android.activities.LoginActivity;
import ruon.android.model.MyPreferenceManager;
import ruon.android.model.NetworkResult;
import ruon.android.net.GcmRegisterWS;
import ruon.android.net.NetworkTask;
import ruon.android.util.UserLog;

/**
 * Created by Ivan on 6/30/2015.
 */
public class GcmRegisterService extends IntentService implements NetworkTask.NetworkTaskListener{
    private static final String TAG = GcmRegisterService.class.getSimpleName();

    private String mPassword;
    private String mUsername;

    public GcmRegisterService(String tag){
        super(tag);
    }

    public GcmRegisterService(){
        super("dfjd");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        UserLog.i(TAG, "Handle intent");
        mPassword = intent.getStringExtra(LoginActivity.PASSWORD);
        mUsername = intent.getStringExtra(LoginActivity.USERNAME);
        UserLog.i(TAG, " Username - " + mUsername);
        String oldToken = MyPreferenceManager.getGcmToken(this);
        if(oldToken != null){
            // We already have token Id
            UserLog.i(TAG, "Gcm old Token - " + oldToken);
            registerOnServerIfNeeded(oldToken);
            return;
        }
        try{
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            UserLog.i(TAG, "newToken  - " + token);
            MyPreferenceManager.saveGcmToken(this, token);
            MyPreferenceManager.setGcmRegisteredOnServer(this, false);
            registerOnServerIfNeeded(token);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void registerOnServerIfNeeded(String token){
        if(!MyPreferenceManager.isGcmRegisteredOnOurServer(this)){
            UserLog.i(TAG, "Should register on Server!! - " + mUsername);
            GcmRegisterWS task = new GcmRegisterWS(mUsername, mPassword, android.os.Build.MODEL, token, this);
            task.execute();
        }
    }

    @Override
    public void OnResult(NetworkResult result, Object o) {
        UserLog.i(TAG, "Server register response! - " + result);
        if(result == NetworkResult.OK){
            UserLog.i(TAG, "Successfully registered on server");
            MyPreferenceManager.setGcmRegisteredOnServer(this, true);
        }
        stopSelf();
    }
}
