package ruon.android.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ruon.app.R;

import org.parceler.Parcels;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ruon.android.util.TheAlarm;
import ruon.android.util.UserLog;

/**
 * Created by Ivan on 6/29/2015.
 */
public class AlarmDetailsActivity extends AppCompatActivity {

    private static final String TAG = AlarmDetailsActivity.class.getSimpleName();

    private TheAlarm mAlarm;

    @InjectView(R.id.alarm_detail)
    TextView mAlarmDetail;
    @InjectView(R.id.alarm_agent)
    TextView mAlarmAgent;
    @InjectView(R.id.alarm_severity)
    TextView mAlarmSeverity;
    @InjectView(R.id.alarm_timestamp)
    TextView mAlarmTimestamp;
    @InjectView(R.id.severity_container)
    LinearLayout mSeverityContainer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_details_activity);
        getAlarmInfo();
        updateViews();
    }

    private void getAlarmInfo() {
        mAlarm = Parcels.unwrap(getIntent().getParcelableExtra(TheAlarm.TAG));
        UserLog.i(TAG, "Alarm - " + mAlarm);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateValues();
    }

    private void updateValues() {
        if(mAlarm == null) {
            finish();
        } else{
            getSupportActionBar().setTitle(mAlarm.getAgent());
            mAlarmSeverity.setText(mAlarm.getSeverity().toString());
            mAlarmTimestamp.setText(mAlarm.getDate());
            mAlarmAgent.setText(mAlarm.getResource());
            mAlarmDetail.setText(mAlarm.getGuid() + "\n\n" + mAlarm.getDescription());
            switch (mAlarm.getSeverity()){
                case Minor:
                    mSeverityContainer.setBackgroundColor(getResources().getColor(R.color.app_minor));
                    break;
                case Major:
                    mSeverityContainer.setBackgroundColor(getResources().getColor(R.color.app_major));
                    break;
                case Critical:
                    mSeverityContainer.setBackgroundColor(getResources().getColor(R.color.app_critical));
                    break;
            }
        }
    }

    private void updateViews() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
