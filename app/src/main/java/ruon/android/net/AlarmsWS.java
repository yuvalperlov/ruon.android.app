package ruon.android.net;

import java.util.List;

import ruon.android.model.NetworkResult;
import ruon.android.util.TheAlarm;
import ruon.rssapi.API;
import ruon.rssapi.Alarm;

/**
 * Created by Ivan on 6/26/2015.
 */
public class AlarmsWS extends NetworkTask {

    public static final String TAG = AlarmsWS.class.getSimpleName();
    String token;
    Object rawResult;
    NetworkTaskListener mListener;
    NetworkResult result;

    public AlarmsWS(String token, NetworkTaskListener listener) {
        this.token = token;
        mListener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        API.alarms(token, false, new API.FetchAlarmsListener() {
            @Override
            public void error(Exception e) {
                // Handle alarms fetch error
                System.err.println(e);
                rawResult = e.getMessage();
                result = NetworkResult.ERROR;
            }

            @Override
            protected void alarms(List<Alarm> alarms) {
                rawResult = TheAlarm.convert(alarms);
                result = NetworkResult.OK;
            }
        });
        return rawResult;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        if (!isCancelled()) {
            mListener.OnResult(result, o);
        }
    }
}
