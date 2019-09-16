package ruon.android.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ruon.app.R;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import ruon.android.util.TheAlarm;
import ruon.rssapi.Alarm;

/**
 * Created by Ivan on 6/26/2015.
 */
public class AlarmAdapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private ArrayList<TheAlarm> items = new ArrayList<TheAlarm>();

    public AlarmAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        TheAlarm alarm = getItem(position);
        if (view == null) {
            view = mInflater.inflate(R.layout.alarm_list_item, null, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.couse.setText(alarm.getResource());
        holder.title.setText(alarm.getAgent());
        holder.snippet.setText(alarm.getDescription());
        holder.updateBG(alarm.getSeverity());
        return view;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public TheAlarm getItem(int i) {
        return items.get(i);
    }

    public void swapData(ArrayList<TheAlarm> items) {
        this.items = items;
    }

    static class ViewHolder {
        @InjectView(R.id.alarm_title)
        TextView title;
        @InjectView(R.id.alarm_cause)
        TextView couse;
        @InjectView(R.id.alarm_short_report)
        TextView snippet;
        @InjectView(R.id.root_element)
        LinearLayout root;

        public ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }

        public void updateBG(Alarm.Severity severity) {
            if (severity == null) {
                return;
            }
            switch (severity) {
                case Minor:
                    root.setBackgroundColor(root.getContext().getResources().getColor(R.color.app_minor));
                    break;
                case Major:
                    root.setBackgroundColor(root.getContext().getResources().getColor(R.color.app_major));
                    break;
                case Critical:
                    root.setBackgroundColor(root.getContext().getResources().getColor(R.color.app_critical));
                    break;
            }
        }
    }

    private LayoutInflater getInflater(View v) {
        if (mInflater == null) {
            mInflater = LayoutInflater.from(v.getContext());
        }
        return mInflater;
    }
}
