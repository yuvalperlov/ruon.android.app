package ruon.android.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Ivan on 6/26/2015.
 */
public class MyPreferenceManager {

    private static SharedPreferences sPrefs;
    public static final String TOKEN = "token";
    public static final String GCM_TOKEN = "gcm_token";
    public static final String REGISTERED_ON_SERVER = "REGISTERED_ON_SERVER";


    public static void saveToken(Context context, String token){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(TOKEN, token);
        editor.commit();
    }

    public static String getToken(Context context){
        return getPrefs(context).getString(TOKEN, null);
    }

    public static void saveGcmToken(Context context, String token){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putString(GCM_TOKEN, token);
        editor.commit();
    }

    public static boolean isGcmRegisteredOnOurServer(Context context){
        return getPrefs(context).getBoolean(REGISTERED_ON_SERVER, false);
    }

    public static void setGcmRegisteredOnServer(Context context, boolean state){
        SharedPreferences.Editor editor = getEditor(context);
        editor.putBoolean(REGISTERED_ON_SERVER, state);
        editor.commit();
    }

    public static String getGcmToken(Context context){
        return getPrefs(context).getString(GCM_TOKEN, null);
    }

    private static SharedPreferences getPrefs(Context context){
        if(sPrefs == null){
            sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return sPrefs;
    }

    private static SharedPreferences.Editor getEditor(Context context){
        return getPrefs(context).edit();
    }
}
