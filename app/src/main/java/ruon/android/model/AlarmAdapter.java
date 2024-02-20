package ruon.android.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ruon.android.util.TheAlarm;
import ruon.rssapi.Alarm;

import com.ruon.app.R;
import com.ruon.app.databinding.AlarmDetailsActivityBinding;
import com.ruon.app.databinding.AlarmListItemBinding;

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

    private AlarmListItemBinding binding;

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        TheAlarm alarm = getItem(position);
        if (view == null) {
            binding = AlarmListItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
            holder = new ViewHolder(binding);
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
        TextView title;
        TextView couse;
        TextView snippet;
        LinearLayout root;

        public ViewHolder(AlarmListItemBinding binding) {
            title = binding.alarmTitle;
            couse = binding.alarmCause;
            snippet = binding.alarmShortReport;
            root = binding.rootElement;
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

}
