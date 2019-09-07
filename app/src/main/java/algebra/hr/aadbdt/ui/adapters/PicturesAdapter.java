package algebra.hr.aadbdt.ui.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import algebra.hr.aadbdt.R;
import algebra.hr.database.DatabaseOpenHelper;
import algebra.hr.database.Log;
import algebra.hr.database.Picture;
import algebra.hr.database.Tag;
import de.hdodenhof.circleimageview.CircleImageView;

import static algebra.hr.aadbdt.TakePictureActivity.new_decode;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.MyViewHolder> {
    DatabaseOpenHelper db;
    private List<Picture> vListRecords;
    private Context mContext;
    private ArrayList<Picture> arrayList;

    public PicturesAdapter(List<Picture> vListRecords, Context vCtx) {
        this.vListRecords = vListRecords;
        this.mContext = vCtx;
        this.arrayList = new ArrayList<Picture>();
        this.arrayList.addAll(vListRecords);
        db = new DatabaseOpenHelper(mContext);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_status_pictures, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Picture picture = vListRecords.get(position);

        if (db.getPictureTags(picture.getID()).size() > 0) {
            holder.llPictureTags.removeAllViews();
            holder.llPictureTags.setVisibility(View.VISIBLE);

            for (Tag tag : db.getPictureTags(picture.getID())) {
                TextView tvTag = new TextView(mContext);
                tvTag.setText("#" + tag.getTAG_TEXT());
                tvTag.setBackground(mContext.getResources().getDrawable(R.drawable.button_states_blue));
                tvTag.setTextColor(mContext.getResources().getColor(R.color.white));
                holder.llPictureTags.addView(tvTag);
            }
        } else {
            holder.llPictureTags.setVisibility(View.GONE);
        }

        holder.tvDescription.setText(picture.getDESCRIPTION());
        if (picture.getDESCRIPTION().equals("")) {
            holder.tvDescription.setVisibility(View.GONE);
        }
        holder.tvDatum.setText(formatDate(picture.getDATEADDES()));
        holder.tvUser.setText("created by " + picture.getUSERNAME());
        holder.tvPictureWidthHeight.setText(picture.getWIDTH() + " * " + picture.getHEIGHT());

        if (picture.getFORMAT().equals("jpeg")) {
            holder.ivImageType.setImageDrawable(mContext.getResources().getDrawable(R.drawable.jpeg));
        } else if (picture.getFORMAT().equals("png")) {
            holder.ivImageType.setImageDrawable(mContext.getResources().getDrawable(R.drawable.png));
        } else if (picture.getFORMAT().equals("bmp")) {
            holder.ivImageType.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bmp));
        }

        try {
            Bitmap bmp = ByteArrayToBitmap(picture.getDATA());

            if (bmp == null) {
                bmp = (new_decode(new File(picture.getFLAG())));
            }

            if (bmp != null) {
                holder.ivPicture.setImageBitmap(bmp);
            } else {
                holder.ivPicture.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noimage));
            }

        } catch (Exception e) {
        }

        holder.ivPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDetails(picture);
            }
        });
    }

    private void showPictureDetails(Picture p) {
        AlertDialog.Builder alertadd = new AlertDialog.Builder(mContext);
        alertadd.setTitle("Picture details");
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View view = factory.inflate(R.layout.image_details, null);

        ImageView imageView = (ImageView) view.findViewById(R.id.dialog_imageview);
        try {
            Bitmap bmp = ByteArrayToBitmap(p.getDATA());

            if (bmp == null) {
                bmp = (new_decode(new File(p.getFLAG())));
            }

            if (bmp != null) {
                imageView.setImageBitmap(bmp);
            } else {
                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.noimage));
            }

        } catch (Exception e) {
        }

        alertadd.setView(view);
        alertadd.setNeutralButton("Close", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dlg, int sumthin) {
                dlg.dismiss();
            }
        });

        alertadd.show();
    }

    public Bitmap ByteArrayToBitmap(byte[] byteArray) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
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
        TextView tvDatum, tvDescription, tvUser, tvPictureWidthHeight;
        CircleImageView ivPicture;
        ImageView ivImageType;
        LinearLayout llPictureTags;

        public MyViewHolder(View view) {
            super(view);
            ivPicture = (CircleImageView) view.findViewById(R.id.ivPicture);
            ivImageType = (ImageView) view.findViewById(R.id.ivImageType);
            tvDatum = (TextView) view.findViewById(R.id.tvDatum);
            tvDescription = (TextView) view.findViewById(R.id.tvDescription);
            tvUser = (TextView) view.findViewById(R.id.tvUser);
            tvPictureWidthHeight = (TextView) view.findViewById(R.id.tvPictureWidthHeight);
            llPictureTags = (LinearLayout) view.findViewById(R.id.llPicturesTags);
        }
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        vListRecords.clear();

        if (charText.length() == 0) {
            vListRecords.addAll(arrayList);
        } else {
            for (Picture picture : arrayList) {
                for (Tag tag : picture.getLISTA_TAGOVA()) {
                    if (tag.getTAG_TEXT().contains(charText)) {
                        vListRecords.add(picture);
                    }
                }
            }
        }

        notifyDataSetChanged();
    }
}