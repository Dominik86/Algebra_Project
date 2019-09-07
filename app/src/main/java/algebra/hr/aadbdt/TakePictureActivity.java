package algebra.hr.aadbdt;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import algebra.hr.database.DatabaseOpenHelper;
import algebra.hr.database.Picture;
import algebra.hr.database.Tag;
import algebra.hr.database.User;

public class TakePictureActivity extends AppCompatActivity {
    DatabaseOpenHelper db;
    User user;
    public static ImageView imageView, ivHashtag, ivSendPicture, ivBack;
    public static LinearLayout llTags;
    public static String str_SaveFolderName;
    private static String str_randomnumber = "";
    private static File wallpaperDirectory;
    static String str_Camera_Photo_ImageName = "";
    static String str_Camera_Photo_ImagePath = "";
    private static File f;
    private static int Take_Photo = 2, Remove_Photo = 3;
    Bitmap bitmap;
    List<Tag> tagList = new ArrayList<>();
    ProgressDialog progressDialog;
    String pictureMsg = "", param = "";
    HttpURLConnection conn;
    JSONArray finalResult;
    JSONObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        setStatusBar();

        db = new DatabaseOpenHelper(this);
        user = db.getUser();

        imageView = (ImageView) this.findViewById(R.id.imageview);
        ivBack = (ImageView) this.findViewById(R.id.ivBack);
        llTags = (LinearLayout) this.findViewById(R.id.llTags);
        findViewById(R.id.scTags).setVisibility(View.INVISIBLE);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TakePictureActivity.super.onBackPressed();
            }
        });

        ivHashtag = (ImageView) this.findViewById(R.id.ivHashtag);
        ivHashtag.setVisibility(View.INVISIBLE);
        ivHashtag.setEnabled(false);
        ivSendPicture = (ImageView) this.findViewById(R.id.ivSendPicture);
        ivSendPicture.setVisibility(View.INVISIBLE);
        ivSendPicture.setEnabled(false);
        Button photoButton = (Button) this.findViewById(R.id.button_image);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                    StrictMode.setVmPolicy(builder.build());

                    str_SaveFolderName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/aadbdt";
                    str_randomnumber = String.valueOf(nextSessionId());
                    wallpaperDirectory = new File(str_SaveFolderName);

                    if (!wallpaperDirectory.exists())
                        wallpaperDirectory.mkdirs();

                    str_Camera_Photo_ImageName = str_randomnumber + ".jpg";
                    str_Camera_Photo_ImagePath = str_SaveFolderName + "/" + str_randomnumber + ".jpg";

                    f = new File(str_Camera_Photo_ImagePath);
                    startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f)), Take_Photo);
                } catch (Exception e) {
                }
            }
        });
        
        ivHashtag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTagDialog();
            }
        });

        ivSendPicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPictureEditDialog();
            }
        });
    }

    private void openPictureEditDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Edit picture");
        builder.setIcon(R.drawable.ic_edit);
        final View customLayout = getLayoutInflater().inflate(R.layout.edit_picture_dialog, null);
        builder.setView(customLayout);

        LinearLayout btnSend = customLayout.findViewById(R.id.btnSend);
        final EditText etDescription = customLayout.findViewById(R.id.etDescription);
        final Spinner spinnerResolutions = customLayout.findViewById(R.id.spinnerResolutions);
        final CheckBox cbJpeg = customLayout.findViewById(R.id.cbJpeg);
        final CheckBox cbPng = customLayout.findViewById(R.id.cbPng);
        final CheckBox cbBmp = customLayout.findViewById(R.id.cbBmp);

        List<String> list = new ArrayList<String>();
        list.add("240 x 320");
        list.add("320 x 480");
        list.add("480 x 800");
        list.add("720 x 1280");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerResolutions.setAdapter(dataAdapter);

        cbJpeg.setChecked(true);
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

                new SendPictureTask(description, width, height, selectedFormat).execute();
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void showTagDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add tag to picture");
        builder.setIcon(R.drawable.hashtag);

        final LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.setMargins(20, 5, 20, 5);
        final EditText input = new EditText(this);
        input.setLayoutParams(lp);
        input.setBackgroundColor(getResources().getColor(R.color.blue));
        input.setTextColor(getResources().getColor(R.color.white));
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        container.addView(input);;
        input.requestFocus();
        builder.setView(container);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String m_Text = input.getText().toString();
                Tag tag = new Tag();
                tag.setTAG_TEXT(m_Text);
                tag.setTAG_DATE(getCurrentDateTime());
                tag.setPICTURE_ID(1);
                tagList.add(tag);
                updateTagList();
            }
        });
        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void updateTagList() {
        llTags.removeAllViews();

        if (tagList.size() > 0) {
            findViewById(R.id.scTags).setVisibility(View.VISIBLE);
        }

        for (Tag tag : tagList) {
            TextView tv = new TextView(TakePictureActivity.this);
            LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            tv.setLayoutParams(layoutparams);
            tv.setText("#" + tag.getTAG_TEXT());
            tv.setTextColor(getResources().getColor(R.color.white));
            tv.setBackground(getResources().getDrawable(R.drawable.button_states_blue));
            llTags.addView(tv);
        }
    }

    private String getCurrentDateTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c);
    }

    public String nextSessionId() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Take_Photo) {
            String filePath = null;
            filePath = str_Camera_Photo_ImagePath;

            if (filePath != null) {
                Bitmap faceView = (new_decode(new File(filePath)));
                imageView.setImageBitmap(faceView);

                ivHashtag.setEnabled(true);
                ivHashtag.setVisibility(View.VISIBLE);
                ivSendPicture.setEnabled(true);
                ivSendPicture.setVisibility(View.VISIBLE);
            } else {
                bitmap = null;
            }
        } else if (requestCode == Remove_Photo) {
            str_Camera_Photo_ImageName = "";
            str_Camera_Photo_ImagePath = "";
            imageView.setImageBitmap(null);
            ivHashtag.setEnabled(false);
            ivHashtag.setVisibility(View.INVISIBLE);
            ivSendPicture.setEnabled(false);
            ivSendPicture.setVisibility(View.INVISIBLE);
        }
    }

    public static Bitmap new_decode(File f) {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        o.inDither = false;

        o.inPurgeable = true;
        o.inInputShareable = true;

        try {
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);
        } catch (FileNotFoundException e1) {
        }

        final int REQUIRED_SIZE = 300;
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        while (true) {
            if (width_tmp / 1.5 < REQUIRED_SIZE && height_tmp / 1.5 < REQUIRED_SIZE)
                break;
            width_tmp /= 1.5;
            height_tmp /= 1.5;
            scale *= 1.5;
        }

        o.inDither = false;
        o.inPurgeable = true;
        o.inInputShareable = true;

        try {
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, null);
            System.out.println(" IW " + width_tmp);
            System.out.println("IHH " + height_tmp);
            int iW = width_tmp;
            int iH = height_tmp;

            return Bitmap.createScaledBitmap(bitmap, iW, iH, true);
        } catch (OutOfMemoryError e) {
            return null;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    class SendPictureTask extends AsyncTask<Void, Void, Boolean> {
        String description, width, height, format;

        public SendPictureTask(String _description, String _width, String _height, String _format) {
            this.description = _description;
            this.width = _width;
            this.height = _height;
            this.format = _format;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(TakePictureActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Sending picture...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (sendPicture(description, width, height, format)) {
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

            if (result || param.equals("2")) {
                if (param.equals("2")) {
                    Toast.makeText(TakePictureActivity.this, pictureMsg, Toast.LENGTH_LONG).show();
                } else {
                    createNotification();
                }
                resetActivity();
            } else {
                Toast.makeText(TakePictureActivity.this, pictureMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.small_camera)
                        .setContentTitle("New notification")
                        .setContentText("User " + user.getUSERNAME() + " add new picture!")
                        .setAutoCancel(true)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setPriority(NotificationCompat.PRIORITY_MAX);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(001, mBuilder.build());
    }

    private void resetActivity() {
        str_Camera_Photo_ImageName = "";
        str_Camera_Photo_ImagePath = "";
        imageView.setImageBitmap(null);
        ivHashtag.setEnabled(false);
        ivHashtag.setVisibility(View.INVISIBLE);
        ivSendPicture.setEnabled(false);
        ivSendPicture.setVisibility(View.INVISIBLE);
        tagList.clear();
        updateTagList();
        findViewById(R.id.scTags).setVisibility(View.INVISIBLE);
    }

    private boolean sendPicture(String description, String width, String height, String format) {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/insert_picture.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if (format.equals("jpeg")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
            } else if (format.equals("png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            }

            byte [] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);

            JSONArray array = new JSONArray();
            for (Tag t : tagList) {
                JSONObject obj = new JSONObject();
                obj.put("text", t.getTAG_TEXT());
                obj.put("date", t.getTAG_DATE());
                array.put(obj);
            }

            params.add(new BasicNameValuePair("img", image_str));
            params.add(new BasicNameValuePair("img_name", str_Camera_Photo_ImageName));
            String img_date = getCurrentDateTime();
            params.add(new BasicNameValuePair("img_date", img_date));
            params.add(new BasicNameValuePair("img_description", description));
            params.add(new BasicNameValuePair("img_width", "" + width));
            params.add(new BasicNameValuePair("img_height", "" + height));
            params.add(new BasicNameValuePair("img_format", format));
            params.add(new BasicNameValuePair("user_id", user.getUSER_ID()));
            params.add(new BasicNameValuePair("name_surname", user.getNAME_SURNAME()));
            params.add(new BasicNameValuePair("tags", array.toString()));

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

                try {
                    finalResult = new JSONArray(response);
                    obj = finalResult.getJSONObject(0);

                    pictureMsg = obj.getString("msg");
                    param = obj.getString("check");

                    if (obj.getString("check").equals("1")) {
                        Picture picture = new Picture();
                        int setID = db.getLastPictureId() + 1;
                        picture.setID(setID);
                        picture.setDESCRIPTION(description);
                        picture.setWIDTH(Integer.parseInt(width));
                        picture.setHEIGHT(Integer.parseInt(height));
                        picture.setFORMAT(format);
                        picture.setUSER_ID(Integer.parseInt(user.getUSER_ID()));
                        picture.setFLAG(str_Camera_Photo_ImagePath);
                        picture.setDATA(image_str.getBytes());
                        picture.setDATEADDES(img_date);
                        picture.setUSERNAME(user.getUSERNAME());
                        for (Tag tag : tagList) {
                            tag.setPICTURE_ID(setID);
                            db.InsertTag(tag);
                        }
                        db.InsertPicture(picture);
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

    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));
    }
}
