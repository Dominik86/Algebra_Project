package algebra.hr.aadbdt;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import java.util.ArrayList;
import java.util.List;

import algebra.hr.database.DatabaseOpenHelper;
import algebra.hr.database.User;

public class LoginActivity extends AppCompatActivity {
    DatabaseOpenHelper db;
    User user;
    private static final int REQUEST_SIGNUP = 0;
    EditText _emailText, _passwordText;
    Button _loginButton;
    TextView _signupLink;
    String email, password, loginMsg;
    HttpURLConnection conn;
    JSONArray finalResult;
    JSONObject obj;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseOpenHelper(this);
        user = db.getUser();

        _emailText = (EditText) findViewById(R.id.input_email);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _loginButton = (Button) findViewById(R.id.btn_login);
        _signupLink = (TextView) findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });
    }

    public void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        email = _emailText.getText().toString();
        password = _passwordText.getText().toString();

        onLoginSuccess();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode != RESULT_OK) {

            }
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        new LoginTask().execute();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Wong data! Try again", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !email.contains("@") || email.length() < 4) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    class LoginTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (sendLogin()) {
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
                finish();
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            } else {
                Toast.makeText(getBaseContext(), loginMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean sendLogin() {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/login.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", email));
            params.add(new BasicNameValuePair("password", password));

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

                    loginMsg = obj.getString("msg");

                    if (obj.getString("check").equals("1")) {
                        if (obj.getJSONArray("records") != null && obj.getJSONArray("records").length() > 0) {
                            for (int i = 0; i < obj.getJSONArray("records").length(); i++) {
                                User user = db.getUser();
                                user.setUSER_ID(obj.getJSONArray("records").getJSONObject(i).getString("id"));
                                user.setUSERNAME(obj.getJSONArray("records").getJSONObject(i).getString("username"));
                                user.setPASSWORD(obj.getJSONArray("records").getJSONObject(i).getString("password"));
                                user.setNAME_SURNAME(obj.getJSONArray("records").getJSONObject(i).getString("name_surname"));
                                user.setEMAIL(obj.getJSONArray("records").getJSONObject(i).getString("email"));
                                user.setROLE(obj.getJSONArray("records").getJSONObject(i).getString("role"));
                                user.setPACKET_ID(Integer.parseInt(obj.getJSONArray("records").getJSONObject(i).getString("packet_id")));
                                user.setPACKET_DATE_START(obj.getJSONArray("records").getJSONObject(i).getString("packet_date_start"));
                                user.setPACKET_DATE_END(obj.getJSONArray("records").getJSONObject(i).getString("packet_date_end"));
                                user.setPACKET_TYPE(obj.getJSONArray("records").getJSONObject(i).getString("packet_type"));
                                db.UpdateUser(user);
                            }
                        }
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