package algebra.hr.aadbdt.ui.fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import algebra.hr.aadbdt.R;
import algebra.hr.database.DatabaseOpenHelper;
import algebra.hr.database.Packet;
import algebra.hr.database.User;

import static algebra.hr.aadbdt.ui.fragments.FragmentView.ACTIVATION_DATE;
import static algebra.hr.aadbdt.ui.fragments.FragmentView.NEW_ACTIVATION_DATE;

public class FragmentPacket extends Fragment {
    View view;
    DatabaseOpenHelper db;
    User user;
    TextView tvTextPackage;
    LinearLayout llFREEPacket, llPROPacket, llGOLDPacket;
    String validDate = "", newValidDate = "", confirmMsg = "";
    ProgressDialog progressDialog;
    HttpURLConnection conn;
    JSONArray finalResult;
    JSONObject obj;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.packet_fragment, container, false);

        db = new DatabaseOpenHelper(getActivity());
        user = db.getUser();

        validDate = ACTIVATION_DATE;
        newValidDate = NEW_ACTIVATION_DATE;

        tvTextPackage = (TextView) view.findViewById(R.id.tvTextPackage);
        llFREEPacket = (LinearLayout) view.findViewById(R.id.llFREEPacket);
        llPROPacket = (LinearLayout) view.findViewById(R.id.llPROPacket);
        llGOLDPacket = (LinearLayout) view.findViewById(R.id.llGOLDPacket);

        if (user.getPACKET_TYPE().equals("FREE")) {
            tvTextPackage.setText("We offer you 3 packages. Choose one. \nCurrently FREE package");
            llFREEPacket.setBackgroundColor(getResources().getColor(R.color.free));
            llPROPacket.setBackgroundColor(getResources().getColor(R.color.iron));
            llGOLDPacket.setBackgroundColor(getResources().getColor(R.color.iron));
        } else if (user.getPACKET_TYPE().equals("PRO")) {
            tvTextPackage.setText("We offer you 3 packages. Choose one. \nCurrently PRO package");
            llFREEPacket.setBackgroundColor(getResources().getColor(R.color.iron));
            llPROPacket.setBackgroundColor(getResources().getColor(R.color.free));
            llGOLDPacket.setBackgroundColor(getResources().getColor(R.color.iron));
        } else if (user.getPACKET_TYPE().equals("GOLD")) {
            tvTextPackage.setText("We offer you 3 packages. Choose one. \nCurrently GOLD package");
            llFREEPacket.setBackgroundColor(getResources().getColor(R.color.iron));
            llPROPacket.setBackgroundColor(getResources().getColor(R.color.iron));
            llGOLDPacket.setBackgroundColor(getResources().getColor(R.color.free));
        }

        if (!user.getROLE().equals("Anonimn")) {
            llFREEPacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user.getPACKET_TYPE().equals("FREE")) {
                        Toast.makeText(getActivity(), "You already use FREE packet", Toast.LENGTH_LONG).show();
                    } else {
                        confirmChangePacket("FREE", 0.00, 3, 3);
                    }
                }
            });
        }

        if (!user.getROLE().equals("Anonimn")) {
            llPROPacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user.getPACKET_TYPE().equals("PRO")) {
                        Toast.makeText(getActivity(), "You already use PRO packet", Toast.LENGTH_LONG).show();
                    } else {
                        confirmChangePacket("PRO", 15.00, 20, 20);
                    }
                }
            });
        }

        if (!user.getROLE().equals("Anonimn")) {
            llGOLDPacket.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (user.getPACKET_TYPE().equals("GOLD")) {
                        Toast.makeText(getActivity(), "You already use GOLD packet", Toast.LENGTH_LONG).show();
                    } else {
                        confirmChangePacket("GOLD", 25.00, 50, 50);
                    }
                }
            });
        }

        return view;
    }

    private void confirmChangePacket(final String type, final double price, final int daily_limit, final int max_picture_upload) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.confirm_packet_dialog);

        ConstraintLayout cs_dialog = (ConstraintLayout) dialog.findViewById(R.id.cs_dialog);
        if (type.equals("FREE")) {
            cs_dialog.setBackgroundColor(getResources().getColor(R.color.free));
        } else if (type.equals("PRO")) {
            cs_dialog.setBackgroundColor(getResources().getColor(R.color.pro));
        } else if (type.equals("GOLD")) {
            cs_dialog.setBackgroundColor(getResources().getColor(R.color.gold));
        }

        ImageView imageViewExit = (ImageView) dialog.findViewById(R.id.imageViewExit);
        imageViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        TextView tvPacketType = (TextView) dialog.findViewById(R.id.tvPacketType);
        tvPacketType.setText(type);

        TextView tvPacketPrice = (TextView) dialog.findViewById(R.id.tvPacketPrice);
        tvPacketPrice.setText(String.format("%.2f", price) + " $");

        TextView tvPacketDescription = (TextView) dialog.findViewById(R.id.tvPacketDescription);
        tvPacketDescription.setText("Daily limit: " + daily_limit + ", max picture upload: " + max_picture_upload);

        Button btnConfirm = (Button) dialog.findViewById(R.id.btnConfirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmPriceTask(user.getUSER_ID(), type, price, daily_limit, max_picture_upload).execute();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class ConfirmPriceTask extends AsyncTask<Void, Void, Boolean> {
        String user_id, type;
        Double price;
        int daily_limit;
        int max_picture_upload;

        public ConfirmPriceTask(String _user_id, String _type, Double _price, int _daily_limit, int _max_picture_upload) {
            this.user_id = _user_id;
            this.type = _type;
            this.price = _price;
            this.daily_limit = _daily_limit;
            this.max_picture_upload = _max_picture_upload;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Confirming packet...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (confirmPacket(user_id, type, price, daily_limit, max_picture_upload)) {
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

            Toast.makeText(getActivity(), confirmMsg, Toast.LENGTH_LONG).show();
        }
    }

    private boolean confirmPacket(String id, String type, Double price, int daily_limit, int max_picture_upload) {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/confirm_packet.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", id));
            params.add(new BasicNameValuePair("username", user.getUSERNAME()));
            params.add(new BasicNameValuePair("type", type));
            params.add(new BasicNameValuePair("price", "" + price));
            params.add(new BasicNameValuePair("activation_date", getCurrentDateTime()));
            params.add(new BasicNameValuePair("new_activation_date", getCurrentDateTimePlusOne()));
            params.add(new BasicNameValuePair("daily_limit", "" + daily_limit));
            params.add(new BasicNameValuePair("max_pictures_upload", "" + max_picture_upload));

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

                    confirmMsg = obj.getString("msg");

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

    private String getCurrentDateTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c);
    }

    private String getCurrentDateTimePlusOne() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, 1);
        Date c = cal.getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c);
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
}
