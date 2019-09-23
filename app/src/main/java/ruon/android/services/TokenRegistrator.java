package ruon.android.services;

import android.content.Context;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import ruon.android.model.MyPreferenceManager;
import ruon.android.model.NetworkResult;
import ruon.android.net.FcmRegisterWS;
import ruon.android.net.NetworkTask;
import ruon.android.util.UserLog;

/**
 * Created by Ivan on 6/30/2015.
 */
public class TokenRegistrator implements NetworkTask.NetworkTaskListener{
    private static final String TAG = TokenRegistrator.class.getSimpleName();

    private String mPassword;
    private String mUsername;
    private Context context;

    public TokenRegistrator(String username, String password, Context context) {
        this.mUsername = username;
        this.mPassword = password;
        this.context = context;
    }

    public void registerFirebaseMessagingToken() {
        UserLog.i(TAG, "Firebase messaging token registration");
        UserLog.i(TAG, " Username - " + mUsername);
        String oldToken = MyPreferenceManager.getFcmToken(context);
        if(oldToken != null){
            // We already have token Id
            UserLog.i(TAG, "Fcm old Token - " + oldToken);
            registerOnServerIfNeeded(oldToken);
            return;
        }
        try{
            FirebaseInstanceId instanceID = FirebaseInstanceId.getInstance();
            instanceID.getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                @Override
                public void onSuccess(InstanceIdResult instanceIdResult) {
                    String token = instanceIdResult.getToken();
                    UserLog.i(TAG, "newToken  - " + token);
                    MyPreferenceManager.saveFcmToken(context, token);
                    MyPreferenceManager.setFcmRegisteredOnServer(context, false);
                    registerOnServerIfNeeded(token);
                }
            });
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void registerOnServerIfNeeded(String token){
        if(!MyPreferenceManager.isFcmRegisteredOnOurServer(context)){
            UserLog.i(TAG, "Should register on Server!! - " + mUsername);
            FcmRegisterWS task = new FcmRegisterWS(mUsername, mPassword, android.os.Build.MODEL, token, this);
            task.execute();
        }
    }

    @Override
    public void OnResult(NetworkResult result, Object o) {
        UserLog.i(TAG, "Server register response! - " + result);
        if(result == NetworkResult.OK){
            UserLog.i(TAG, "Successfully registered on server");
            MyPreferenceManager.setFcmRegisteredOnServer(context, true);
        }
    }
}
