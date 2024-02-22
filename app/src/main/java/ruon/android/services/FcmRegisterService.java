//package ruon.android.services;
//
//import android.app.IntentService;
//import android.content.Intent;
//
//import ruon.android.activities.LoginActivity;
//import ruon.android.model.MyPreferenceManager;
//import ruon.android.model.NetworkResult;
//import ruon.android.net.FcmRegisterWS;
//import ruon.android.net.NetworkTask;
//import ruon.android.util.UserLog;
//
///**
// * Created by Ivan on 6/30/2015.
// */
//public class FcmRegisterService extends IntentService implements NetworkTask.NetworkTaskListener {
//    private static final String TAG = FcmRegisterService.class.getSimpleName();
//
//    private String mPassword;
//    private String mUsername;
//
//    public FcmRegisterService(String tag){
//        super(tag);
//    }
//
//    public FcmRegisterService(){
//        super("dfjd");
//    }
//
//    @Override
//    protected void onHandleIntent(Intent intent) {
//        UserLog.i(TAG, "Handle intent");
//        mPassword = intent.getStringExtra(LoginActivity.PASSWORD);
//        mUsername = intent.getStringExtra(LoginActivity.USERNAME);
//        UserLog.i(TAG, " Username - " + mUsername);
//        String oldToken = MyPreferenceManager.getFcmToken(this);
//        if(oldToken != null){
//            // We already have token Id
//            UserLog.i(TAG, "Gcm old Token - " + oldToken);
//            registerOnServerIfNeeded(oldToken);
//            return;
//        }
////        try{
////            InstanceID instanceID = InstanceID.getInstance(this);
//////            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
////                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
////            UserLog.i(TAG, "newToken  - " + token);
//            MyPreferenceManager.saveFirebaseToken(this, "token");
//            MyPreferenceManager.setFcmRegisteredOnServer(this, false);
////            registerOnServerIfNeeded(token);
////        } catch (Exception e){
////            e.printStackTrace();
////        }
//    }
//
//    private void registerOnServerIfNeeded(String token){
//        if(!MyPreferenceManager.isFcmRegisteredOnOurServer(this)){
//            UserLog.i(TAG, "Should register on Server!! - " + mUsername);
//            FcmRegisterWS task = new FcmRegisterWS(mUsername, mPassword, android.os.Build.MODEL, token, this);
//            task.execute();
//        }
//    }
//
//    @Override
//    public void OnResult(NetworkResult result, Object o) {
//        UserLog.i(TAG, "Server register response! - " + result);
////        if(result == NetworkResult.OK){
//            UserLog.i(TAG, "Successfully registered on server");
//            MyPreferenceManager.setFcmRegisteredOnServer(this, true);
////        }
//        stopSelf();
//    }
//}
