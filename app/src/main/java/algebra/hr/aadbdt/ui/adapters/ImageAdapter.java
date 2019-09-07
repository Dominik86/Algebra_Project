package algebra.hr.aadbdt.ui.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import algebra.hr.aadbdt.R;
import algebra.hr.aadbdt.ui.SliderActivity;
import algebra.hr.database.DatabaseOpenHelper;
import algebra.hr.database.Picture;
import algebra.hr.database.Tag;
import algebra.hr.database.User;

import static algebra.hr.aadbdt.TakePictureActivity.new_decode;

public class ImageAdapter extends PagerAdapter{
    Context mContext;
    DatabaseOpenHelper db;
    User user;
    List<Picture> list = new ArrayList<>();
    ProgressDialog progressDialog;
    String updateMsg = "";
    HttpURLConnection conn;
    JSONArray finalResult;
    JSONObject obj;

    public ImageAdapter(Context context) {
        this.mContext = context;

        db = new DatabaseOpenHelper(mContext);
        user = db.getUser();
        list = db.getAllPictures(" WHERE user_id = " + user.getUSER_ID());
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        try {
            Bitmap bmp = ByteArrayToBitmap(list.get(position).getDATA());

            if (bmp == null) {
                bmp = (new_decode(new File(list.get(position).getFLAG())));
            }

            if (bmp != null) {
                imageView.setImageBitmap(bmp);
            }
        } catch (Exception e) {
        }

        ((ViewPager) container).addView(imageView, 0);
        setItemData(list.get(position));
        return imageView;
    }

    private void setItemData(final Picture picture) {
        SliderActivity.tvDate.setText(formatDate(picture.getDATEADDES()));
        SliderActivity.tvTime.setText(picture.getDATEADDES().substring(10, 16));
        SliderActivity.ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureInfo(picture);
            }
        });
        SliderActivity.llUredi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openEditDialog(picture);
            }
        });
    }

    private void openEditDialog(final Picture picture) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Edit picture");
        builder.setIcon(R.drawable.ic_edit);
        final View customLayout = ((Activity)mContext).getLayoutInflater().inflate(R.layout.edit_picture_dialog, null);
        builder.setView(customLayout);

        LinearLayout btnSend = customLayout.findViewById(R.id.btnSend);
        final EditText etDescription = customLayout.findViewById(R.id.etDescription);
        etDescription.setText(picture.getDESCRIPTION());
        final Spinner spinnerResolutions = customLayout.findViewById(R.id.spinnerResolutions);
        final CheckBox cbJpeg = customLayout.findViewById(R.id.cbJpeg);
        final CheckBox cbPng = customLayout.findViewById(R.id.cbPng);
        final CheckBox cbBmp = customLayout.findViewById(R.id.cbBmp);
        final ImageView ivDialogImage = customLayout.findViewById(R.id.ivDialogImage);
        final TextView ivDialogButtonText = customLayout.findViewById(R.id.ivDialogButtonText);

        ivDialogImage.setImageDrawable(mContext.getResources().getDrawable(R.drawable.update_blue));
        ivDialogButtonText.setText("UPDATE");

        List<String> list = new ArrayList<String>();
        list.add("240 x 320");
        list.add("320 x 480");
        list.add("480 x 800");
        list.add("720 x 1280");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mContext,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerResolutions.setAdapter(dataAdapter);
        spinnerResolutions.setSelection(list.indexOf(picture.getWIDTH() + " x " + picture.getHEIGHT()));

        if (picture.getFORMAT().equals("jpeg")) {
            cbJpeg.setChecked(true);
        } else if (picture.getFORMAT().equals("png")) {
            cbPng.setChecked(true);
        } else if (picture.getFORMAT().equals("bmp")) {
            cbBmp.setChecked(true);
        }

        cbJpeg.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    cbJpeg.setChecked(true);
                    cbPng.setChecked(false);
                    cbBmp.setChecked(false);
                }
            }
        });

        cbPng.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    cbJpeg.setChecked(false);
                    cbPng.setChecked(true);
                    cbBmp.setChecked(false);
                }
            }
        });

        cbBmp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    cbJpeg.setChecked(false);
                    cbPng.setChecked(false);
                    cbBmp.setChecked(true);
                }
            }
        });

        final AlertDialog dialog = builder.create();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = etDescription.getText().toString();
                String selectedResolution = String.valueOf(spinnerResolutions.getSelectedItem());
                String[] separated = selectedResolution.split(" x ");
                String width = separated[0];
                String height = separated[1];


                String selectedFormat = "";
                if (cbJpeg.isChecked()) {
                    selectedFormat = "jpeg";
                } else if (cbPng.isChecked()) {
                    selectedFormat = "png";
                } else if (cbBmp.isChecked()) {
                    selectedFormat = "bmp";
                } else {
                    selectedFormat = "";
                }

                new UpdatePictureTask(picture, picture.getID(), description, width, height, selectedFormat).execute();
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    class UpdatePictureTask extends AsyncTask<Void, Void, Boolean> {
        Picture picture;
        int ID;
        String description, width, height, format;

        public UpdatePictureTask(Picture pic, int id, String desc, String w, String h, String f) {
            this.picture = pic;
            this.ID = id;
            this.description = desc;
            this.width = w;
            this.height = h;
            this.format = f;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Updating picture...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (updatePicture(description, width, height, format, picture.getDATEADDES())) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            if (result) {
                picture.setDESCRIPTION(description);
                picture.setWIDTH(Integer.parseInt(width));
                picture.setHEIGHT(Integer.parseInt(height));
                picture.setFORMAT(format);
                db.UpdatePicture(picture);
            }

            Toast.makeText(mContext, updateMsg, Toast.LENGTH_LONG).show();
        }
    }

    private boolean updatePicture(String description, String width, String height, String format, String date) {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/update_picture.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("img_description", description));
            params.add(new BasicNameValuePair("img_width", "" + width));
            params.add(new BasicNameValuePair("img_height", "" + height));
            params.add(new BasicNameValuePair("img_format", format));
            params.add(new BasicNameValuePair("img_date", date));
            params.add(new BasicNameValuePair("user_id", user.getUSER_ID()));
            params.add(new BasicNameValuePair("name_surname", user.getNAME_SURNAME()));

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, HTTP.UTF_8));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            conn.connect();
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                while ((line = br.readLine()) != null) {
                    response += line;
                }

                Log.v("primjer", "response-" + response);

                try {
                    finalResult = new JSONArray(response);
                    obj = finalResult.getJSONObject(0);

                    updateMsg = obj.getString("msg");

                    if (obj.getString("check").equals("1")) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        } catch (Exception e) {
            return false;
        } finally {
            conn.disconnect();
        }
    }

    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    private void showPictureInfo(Picture picture) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Picture details");
        if (picture.getFORMAT().equals("jpeg")) {
            builder.setIcon(R.drawable.jpeg);
        } else if (picture.getFORMAT().equals("png")) {
            builder.setIcon(R.drawable.png);
        } else if (picture.getFORMAT().equals("bmp")) {
            builder.setIcon(R.drawable.bmp);
        }

        builder.setMessage(picture.getDESCRIPTION() + "\nDate: " + formatDateString(picture.getDATEADDES()) + "\n"
                + "Size: " + picture.getWIDTH() + " x " + picture.getHEIGHT() + "\n"
                + "Location: " + picture.getFLAG())
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String formatDate(String old_date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(old_date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MMMM.yyyy.");
            return sdf2.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    private String formatDateString(String old_date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(old_date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy. HH:mm:ss");
            return sdf2.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    public Bitmap ByteArrayToBitmap(byte[] byteArray) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((ImageView) object);
    }

    @Override
    public int getCount() {
        return list.size();
    }
}
