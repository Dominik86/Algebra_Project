package algebra.hr.aadbdt.ui.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import algebra.hr.database.User;

public class FragmentView extends Fragment {
    View view;
    User user;
    DatabaseOpenHelper db;
    String checkMsg, DAILY_UPLOADS = "0", DATE_EXPIRE = "01.01.2019.", TOTAL_DEBTS = "0";
    public static String ACTIVATION_DATE = "", NEW_ACTIVATION_DATE = "";
    HttpURLConnection conn;
    JSONArray finalResult;
    JSONObject obj;
    ProgressDialog progressDialog;
    TextView tv_status, tv_status2, tv_status3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.view_fragment, container, false);

        db = new DatabaseOpenHelper(getActivity());
        user = db.getUser();
        setControls();
        updateUI();

        new ConsumptionReviewTask().execute();

        return view;
    }

    private void setControls() {
        tv_status = (TextView) view.findViewById(R.id.tv_status);
        tv_status2 = (TextView) view.findViewById(R.id.tv_status2);
        tv_status3 = (TextView) view.findViewById(R.id.tv_status3);
    }

    class ConsumptionReviewTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Geting data...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (getData()) {
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
                updateUI();
            } else {
                Toast.makeText(getActivity(), checkMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void updateUI() {
        tv_status.setText(DAILY_UPLOADS + " credits");
        tv_status2.setText(formatDate(DATE_EXPIRE));
        tv_status3.setText(String.format("%.2f", Double.parseDouble(TOTAL_DEBTS)) + " $");
    }

    private boolean getData() {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/consumption_review.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", "" + user.getUSER_ID()));
            params.add(new BasicNameValuePair("date", "" + getCurrentDateTime()));

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

                    checkMsg = obj.getString("msg");

                    if (obj.getString("check").equals("1")) {
                        DAILY_UPLOADS = obj.getString("daily_uploads");
                        DATE_EXPIRE = obj.getString("date_expire");
                        TOTAL_DEBTS = obj.getString("total_debts");
                        ACTIVATION_DATE = obj.getString("activation_date");
                        NEW_ACTIVATION_DATE = obj.getString("new_activation_date");
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

    private String formatDate(String old_date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(old_date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy.");
            return sdf2.format(date);
        } catch (Exception e) {
            return "";
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
}
