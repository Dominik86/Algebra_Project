package algebra.hr.aadbdt.ui.classes;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.List;

import algebra.hr.aadbdt.R;
import algebra.hr.database.DatabaseOpenHelper;
import algebra.hr.database.User;

public class ViewDialog {
    DatabaseOpenHelper db;
    Context mContext;
    String updateMsg;
    HttpURLConnection conn;
    JSONArray finalResult;
    JSONObject obj;
    ProgressDialog progressDialog;

    public void showDialog(final Activity activity, String msg, final User user){
        db = new DatabaseOpenHelper(activity);
        mContext = activity;

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.edit_dialog);

        final EditText input_username = (EditText) dialog.findViewById(R.id.input_username);
        input_username.setText(user.getUSERNAME());

        final EditText input_name_surname = (EditText) dialog.findViewById(R.id.input_name_surname);
        input_name_surname.setText(user.getNAME_SURNAME());

        final EditText input_password = (EditText) dialog.findViewById(R.id.input_password);
        input_password.setText(user.getPASSWORD());

        final EditText input_email = (EditText) dialog.findViewById(R.id.input_email);
        input_email.setText(user.getEMAIL());

        Button dialogButtonCancel = (Button) dialog.findViewById(R.id.btn_dialogCancel);
        dialogButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        Button dialogButtonUpdate = (Button) dialog.findViewById(R.id.btn_dialogUpdate);
        dialogButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input_username.getText().toString().equals("")) {
                    Toast.makeText(activity, "Username is required!", Toast.LENGTH_LONG).show();
                } else if (input_name_surname.getText().toString().equals("")) {
                    Toast.makeText(activity, "Name and surname is required!", Toast.LENGTH_LONG).show();
                } else if (input_password.getText().toString().length() < 4) {
                    Toast.makeText(activity, "Password required and minimum 4 characters!", Toast.LENGTH_LONG).show();
                } else if (!input_email.getText().toString().contains("@") || input_email.getText().toString().length() < 4) {
                    Toast.makeText(activity, "Email is not valid!", Toast.LENGTH_LONG).show();
                } else {
                    User u = db.getUser();
                    new UpdateUserTask(
                            Integer.parseInt(u.getUSER_ID()),
                            input_username.getText().toString(),
                            input_name_surname.getText().toString(),
                            input_password.getText().toString(),
                            input_email.getText().toString()).execute();
                    dialog.dismiss();
                }
            }
        });

        dialog.show();
    }

    class UpdateUserTask extends AsyncTask<Void, Void, Boolean> {
        int id;
        String username, name_surname, password, email;
        public UpdateUserTask (int _id, String _username, String _name_surname, String _password, String _email) {
            this.id = _id;
            this.username = _username;
            this.name_surname = _name_surname;
            this.password = _password;
            this.email = _email;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(mContext, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Updating Account...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (updateUser(id, username, name_surname, password, email)) {
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

            Toast.makeText(mContext, updateMsg, Toast.LENGTH_LONG).show();

            if (result) {
                User user = db.getUser();
                user.setUSERNAME(username);
                user.setNAME_SURNAME(name_surname);
                user.setPASSWORD(password);
                user.setEMAIL(email);
                db.UpdateUser(user);
            }
        }
    }

    private boolean updateUser(int id, String username, String name_surname, String password, String email) {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/update_user.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", "" + id));
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("name_surname", name_surname));
            params.add(new BasicNameValuePair("email", email));

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
}