package ruon.android.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ruon.app.R;

import org.json.JSONObject;

import ruon.android.activities.MainActivity;
import ruon.android.util.TheAlarm;
import ruon.android.util.UserLog;

/**
 * Created by Ivan on 6/30/2015.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private static final String ALARM_KEY = "alarm";
    private static final String TITLE_KEY = "title";
    private static final String SOUND_KEY = "sound";

    public static int sNotificationId;

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String message = remoteMessage.getData().get(ALARM_KEY);
        JSONObject rawAlarm = null;
        try{
            rawAlarm = new JSONObject(message);
            String title = rawAlarm.optString(TITLE_KEY).trim();
            String sound = rawAlarm.getString(SOUND_KEY).trim();
            UserLog.i(TAG, "Fcm alarm - " + title);
            /**
             * In some cases it may be useful to show a notification indicating to the user
             * that a message was received.
             */
            sendNotification(title, sound);
        }catch (Exception e){
            Log.e(TAG, "Could not parse Fcm message - " + message);
            e.printStackTrace();
            return ;
        }
    }

    private void sendNotification(String title, String soundRaw) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.notification)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setColor(getResources().getColor(R.color.app_green))
                        .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                        .setContentText(title)
                        .setChannelId(getString(R.string.default_notification_channel_id));
        String sound = getSound(soundRaw);
        if(sound != null){
            mBuilder.setSound(Uri.parse(sound));
        }else{
            mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND);
        }

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        sNotificationId++;

        Notification ntf = mBuilder.build();
        mNotificationManager.notify(sNotificationId, ntf);
        Intent intent = new Intent(MainActivity.NOTIFICATION_EVENT);
        sendBroadcast(intent);
    }

    public String getSound(String soundRaw){
        if(TextUtils.isEmpty(soundRaw)){
            return null;
        }
        String path = null;
        try{
            TheAlarm.Sound sound = TheAlarm.Sound.valueOf(soundRaw);
            switch (sound){
                case dcagd:
                    path = "" + R.raw.dcagd;
                    break;
                case dccnc:
                    path = "" + R.raw.dccnc;
                    break;
                case dcgen:
                    path = "" + R.raw.dcgen;
                    break;
                case dcprd:
                    path = "" + R.raw.dcprd;
                    break;
                case digen:
                    path = "" + R.raw.digen;
                    break;
                case dmacs:
                    path = "" + R.raw.dmacs;
                    break;
                case dmcpu:
                    path = "" + R.raw.dmcpu;
                    break;
                case dmgen:
                    path = "" + R.raw.dmgen;
                    break;
                case dmhdd:
                    path = "" + R.raw.dmhdd;
                    break;
                case dmmem:
                    path = "" + R.raw.dmmem;
                    break;
                case dmswp:
                    path = "" + R.raw.dmswp;
                    break;
                case ecgen:
                    path = "" + R.raw.ecgen;
                    break;
                case eigen:
                    path = "" + R.raw.eigen;
                    break;
                case emagr:
                    path = "" + R.raw.emagr;
                    break;
                case emgen:
                    path = "" + R.raw.emgen;
                    break;
                case ucagd:
                    path = "" + R.raw.ucagd;
                    break;
                case uccnc:
                    path = "" + R.raw.uccnc;
                    break;
                case ucgen:
                    path = "" + R.raw.ucgen;
                    break;
                case ucprd:
                    path = "" + R.raw.ucprd;
                    break;
                case uigen:
                    path = "" + R.raw.uigen;
                    break;
                case umacs:
                    path = "" + R.raw.umacs;
                    break;
                case umcpu:
                    path = "" + R.raw.umcpu;
                    break;
                case umgen:
                    path = "" + R.raw.umgen;
                    break;
                case umhdd:
                    path = "" + R.raw.umhdd;
                    break;
                case ummem:
                    path = "" + R.raw.ummem;
                    break;
                case umswp:
                    path = "" + R.raw.umswp;
                    break;
            }
            if(TextUtils.isEmpty(path)){
                return null;
            }
        } catch (IllegalArgumentException e){
            Log.e(TAG, "Unknown sound");
            return null;
        }
        return "android.resource://" + getPackageName() + "/" + path ;
    }
}
