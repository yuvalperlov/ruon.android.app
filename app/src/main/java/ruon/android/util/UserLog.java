package ruon.android.util;

import android.util.Log;

public class UserLog {
	public static final boolean DEBUG = false;

	public static void i(String TAG, String message){
		if(DEBUG){
			Log.i(TAG, message);
		}
	}
	
	public static void v(String TAG, String message){
		if(DEBUG){
			Log.i(TAG, message);
		}
	}
	
	public static void printLongMessage(String tag, String message, String title){
        if(!DEBUG){
            return;
        }
        int step = message.length() / 500;
        for (int i = 0; i < step; i++) {
            UserLog.i(tag, title + " - part - " + (i + 1) * 500 + " - " + message.substring(i * 500, (i + 1) * 500));
        }
        UserLog.i(tag, title + " - part - " + step * 500 + " - " + message.substring(step * 500));
    }
}
