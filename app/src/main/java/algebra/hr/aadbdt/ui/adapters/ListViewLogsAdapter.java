package algebra.hr.aadbdt.ui.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import algebra.hr.aadbdt.R;
import algebra.hr.database.Log;

public class ListViewLogsAdapter extends RecyclerView.Adapter<ListViewLogsAdapter.MyViewHolder> {
    private List<Log> vListRecords;
    private Context mContext;
    private ArrayList<Log> arrayList;

    public ListViewLogsAdapter(List<Log> vListRecords, Context vCtx) {
        this.vListRecords = vListRecords;
        this.mContext = vCtx;
        this.arrayList = new ArrayList<Log>();
        this.arrayList.addAll(vListRecords);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_view_log_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Log log = vListRecords.get(position);

        holder.tvLogDate.setText(formatDate(log.getDATE_MSG()));
        holder.tvLogMsg.setText(log.getMSG());
        holder.tvLogUsername.setText("User: " + log.getUSERNAME());
        holder.tvLogType.setText("Type:" + log.getTYPE());
    }

    private String formatDate(String old_date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(old_date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
            return sdf2.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    @Override
    public int getItemCount() {
        return vListRecords.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvLogDate, tvLogMsg, tvLogUsername, tvLogType;

        public MyViewHolder(View view) {
            super(view);
            tvLogDate = (TextView) view.findViewById(R.id.tvLogDate);
            tvLogMsg = (TextView) view.findViewById(R.id.tvLogMsg);
            tvLogUsername = (TextView) view.findViewById(R.id.tvLogUsername);
            tvLogType = (TextView) view.findViewById(R.id.tvLogType);
        }
    }

    public void filter(String charText, String filter_type, String filter_text) {
        charText = charText.toLowerCase(Locale.getDefault());
        vListRecords.clear();

        if (filter_text.equals("")) {
            if (charText.length() == 0) {
                vListRecords.addAll(arrayList);
            } else {
                for (Log log : arrayList) {
                    if (log.getMSG().toLowerCase(Locale.getDefault()).contains(charText)) {
                        vListRecords.add(log);
                    }
                }
            }
        } else {
            if (filter_type.equals("dates")) {
                for (Log log : arrayList) {
                    if (log.getMSG().toLowerCase(Locale.getDefault()).contains(charText)
                            && log.getDATE_MSG().startsWith(reformatDate(filter_text))) {
                        vListRecords.add(log);
                    }
                }
            } else if (filter_type.equals("users")) {
                for (Log log : arrayList) {
                    if (log.getMSG().toLowerCase(Locale.getDefault()).contains(charText)
                            && log.getUSERNAME().equals(filter_text)) {
                        vListRecords.add(log);
                    }
                }
            } else if (filter_type.equals("types")) {
                for (Log log : arrayList) {
                    if (log.getMSG().toLowerCase(Locale.getDefault()).contains(charText)
                            && log.getTYPE().equals(filter_text)) {
                        vListRecords.add(log);
                    }
                }
            }
        }

        notifyDataSetChanged();
    }

    private String reformatDate(String old_date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
            Date date = sdf.parse(old_date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
            return sdf2.format(date);
        } catch (Exception e) {
            return "";
        }
    }
}
