package algebra.hr.aadbdt.ui.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import algebra.hr.aadbdt.R;
import algebra.hr.database.Packet;

public class CustomAdapter extends ArrayAdapter<Packet> {
    private ArrayList<Packet> dataSet;
    Context mContext;

    private static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title, tv_status, tvDescription;
    }

    public CustomAdapter(ArrayList<Packet> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Packet dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
            viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
            viewHolder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
            viewHolder.tvDescription = (TextView) convertView.findViewById(R.id.tvDescription);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        lastPosition = position;

        viewHolder.tv_title.setText(dataModel.getTYPE());

        if (dataModel.getTYPE().equals("FREE")) {
            viewHolder.tv_title.setTextColor(mContext.getResources().getColor(R.color.free));
        } else if (dataModel.getTYPE().equals("PRO")) {
            viewHolder.tv_title.setTextColor(mContext.getResources().getColor(R.color.pro));
        } else if (dataModel.getTYPE().equals("GOLD")) {
            viewHolder.tv_title.setTextColor(mContext.getResources().getColor(R.color.gold));
        }

        viewHolder.tv_status.setText(dataModel.getPRICE() + " $");
        if (dataModel.getTYPE().equals("FREE")) {
            viewHolder.tvDescription.setText("Package FREE allows you to upload max 3 picture per day");
        } else if (dataModel.getTYPE().equals("PRO")) {
            viewHolder.tvDescription.setText("Package PRO allows you to upload max 20 picture per day");
        } else {
            viewHolder.tvDescription.setText("Package GOLD allows you to upload max 50 picture per day");
        }

        viewHolder.iv_icon.setImageResource(0);
        Drawable draw = null;
        if (dataModel.getTYPE().equals("FREE")) {
            draw = mContext.getResources().getDrawable(R.drawable.package1);
        } else if (dataModel.getTYPE().equals("PRO")) {
            draw = mContext.getResources().getDrawable(R.drawable.package2);
        } else {
            draw = mContext.getResources().getDrawable(R.drawable.package3);
        }

        draw = (draw);
        viewHolder.iv_icon.setImageDrawable(draw);
        return convertView;
    }
}