package ruon.android.activities;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.ruon.app.R;

/**
 * Created by Ivan on 6/26/2015.
 */
public abstract class WorkerActivity extends ActionBarActivity {

    public void showProgress(){
        LinearLayout progress = (LinearLayout) findViewById(R.id.progress_bar);
        if(progress != null){
            progress.setVisibility(View.VISIBLE);
        }
    }

    public void hideProgress(){
        LinearLayout progress = (LinearLayout) findViewById(R.id.progress_bar);
        if(progress != null){
            progress.setVisibility(View.GONE);
        }
    }
}
