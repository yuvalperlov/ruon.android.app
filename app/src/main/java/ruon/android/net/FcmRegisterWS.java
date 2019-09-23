package ruon.android.net;

import android.text.format.DateUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;

import ruon.android.model.NetworkResult;
import ruon.android.util.UserLog;

/**
 * Created by Ivan on 6/26/2015.
 */
public class FcmRegisterWS extends NetworkTask {

public static final String TAG = FcmRegisterWS.class.getSimpleName();
    String email;
    String password;
    String mName;
    String mToken;
    NetworkTaskListener mListener;
    NetworkResult result;
    private static final String UNKNOWN_ERROR = "Unknown error";

    public FcmRegisterWS(String email, String password, String name, String token, NetworkTaskListener listener){
        this.email = email;
        this.password = password;
        this.mName = URLEncoder.encode(name);
        this.mToken = token;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpParams httpParameters = httpClient.getParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, (int) (20 * DateUtils.SECOND_IN_MILLIS));
        HttpConnectionParams.setSoTimeout        (httpParameters, (int) (20 * DateUtils.SECOND_IN_MILLIS));
        try {
            HttpGet httpost = new HttpGet(String.format("https://www.r-u-on.com/androidNotif?email=%s&password=%s&name=%s&device=%s", email, password, mName, mToken));
            UserLog.i(TAG, "New WS URL - " + httpost.getURI());
            httpost.setHeader("Accept", "application/json");
            HttpResponse httpResponse = httpClient.execute(httpost);
            String line = EntityUtils.toString(httpResponse.getEntity());
            UserLog.i(TAG, "Response - " + line);
            UserLog.i(TAG, "Response code - " + httpResponse.getStatusLine().getStatusCode());
            if(httpResponse.getStatusLine().getStatusCode() == 200){
                result = NetworkResult.OK;
            }else{
                result = NetworkResult.ERROR;
            }
            return line;
            // handle response here...
        }catch (Exception ex) {
            // handle exception here
            ex.printStackTrace();
            result = NetworkResult.ERROR;
            return UNKNOWN_ERROR;
        }
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if(!isCancelled()){
            if(mListener != null){
                mListener.OnResult(result, o);
            }
        }
    }
}
