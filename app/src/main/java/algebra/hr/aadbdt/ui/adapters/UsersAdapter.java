package algebra.hr.aadbdt.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

import algebra.hr.aadbdt.R;
import algebra.hr.aadbdt.ui.UsersPictures;
import algebra.hr.database.User;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder> {
    private List<User> vListRecords;
    private Context mContext;

    public UsersAdapter(List<User> vListRecords, Context vCtx) {
        this.vListRecords = vListRecords;
        this.mContext = vCtx;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_status_users, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final User user = vListRecords.get(position);

        holder.tvFullName.setText(user.getUSERNAME());
        holder.tvEmail.setText(user.getEMAIL());
        holder.tvPicturesNumber.setText(user.getPICTURE_NUMBER() + " pic");

        int numberRows = 0;
        if (vListRecords.size() % 2 != 0) {
            numberRows = (vListRecords.size() + 1) / 2;
        } else {
            numberRows = vListRecords.size() / 2;
        }

        int screenHeight = ((getScreenHeight()) - (numberRows * 10));
        int split = screenHeight / numberRows;
        holder.tvHide.setHeight(split - (12 * numberRows));

        if (Integer.parseInt(user.getPICTURE_NUMBER()) == 0) {
            holder.vConstraintLayout.setBackgroundColor(getColorWithAlpha(Color.GRAY, 0.5f));
            holder.vConstraintLayout.setEnabled(false);
            holder.vConstraintLayout.getBackground().setAlpha(128);
        } else {
            if (user.getPACKET_TYPE().equals("FREE")) {
                holder.vConstraintLayout.setBackgroundResource(R.drawable.gradient_background_free);
                holder.vConstraintLayout.setEnabled(true);
            } else if (user.getPACKET_TYPE().equals("PRO")) {
                holder.vConstraintLayout.setBackgroundResource(R.drawable.gradient_background_pro);
                holder.vConstraintLayout.setEnabled(true);
            } else if (user.getPACKET_TYPE().equals("GOLD")) {
                holder.vConstraintLayout.setBackgroundResource(R.drawable.gradient_background_gold);
                holder.vConstraintLayout.setEnabled(true);
            }
        }

        holder.ivPacket.setImageResource(0);
        Drawable draw = null;
        if (user.getPACKET_TYPE().equals("FREE")) {
            draw = mContext.getResources().getDrawable(R.drawable.package1);
        } else if (user.getPACKET_TYPE().equals("PRO")) {
            draw = mContext.getResources().getDrawable(R.drawable.package2);
        } else {
            draw = mContext.getResources().getDrawable(R.drawable.package3);
        }

        draw = (draw);
        holder.ivPacket.setImageDrawable(draw);

        if (Integer.parseInt(user.getPICTURE_NUMBER()) > 0) {
            holder.vConstraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, UsersPictures.class);
                    intent.putExtra("user", user.getUSERNAME());
                    intent.putExtra("user_id", user.getUSER_ID());
                    mContext.startActivity(intent);
                }
            });
        }
    }

    public static int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);

        return newColor;
    }

    private int getScreenHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    @Override
    public int getItemCount() {
        return vListRecords.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ConstraintLayout vConstraintLayout;
        public TextView tvFullName, tvEmail, tvHide, tvPicturesNumber;
        ImageView ivPacket;

        public MyViewHolder(View view) {
            super(view);
            vConstraintLayout = (ConstraintLayout) view.findViewById(R.id.ConstraintLayout_main);
            tvFullName = (TextView) view.findViewById(R.id.tvFullName);
            tvEmail = (TextView) view.findViewById(R.id.tvEmail);
            tvHide = (TextView) view.findViewById(R.id.tvHide);
            ivPacket = (ImageView) view.findViewById(R.id.ivPacket);
            tvPicturesNumber = (TextView) view.findViewById(R.id.tvPicturesNumber);
        }
    }
}