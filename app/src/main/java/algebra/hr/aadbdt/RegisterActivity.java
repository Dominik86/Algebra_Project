package algebra.hr.aadbdt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

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

import algebra.hr.aadbdt.ui.adapters.CustomAdapter;
import algebra.hr.database.Packet;

public class RegisterActivity extends Activity {
    EditText _nameText, _emailText, _usernameText, _passwordText;
    Button _signupButton;
    TextView _loginLink;
    String name_surname, email, username, password, registerMsg;
    HttpURLConnection conn;
    JSONArray finalResult;
    JSONObject obj;
    ProgressDialog progressDialog;
    ArrayList<Packet> dataModels;
    private static CustomAdapter adapter;
    private GoogleSignInClient mGoogleSignInClient;
    ImageView sign_out_button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _nameText = (EditText) findViewById(R.id.input_name);
        _emailText = (EditText) findViewById(R.id.input_email);
        _usernameText = (EditText) findViewById(R.id.input_username);
        _passwordText = (EditText) findViewById(R.id.input_password);
        _signupButton = (Button) findViewById(R.id.btn_signup);
        _loginLink = (TextView) findViewById(R.id.link_login);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 111);
            }
        });

        sign_out_button = (ImageView) findViewById(R.id.sign_out_button);
        sign_out_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut();
                updateUI(null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            updateUI(account);
        } catch (ApiException e) {
            updateUI(null);
        }
    }

    private void updateUI(Object o) {
        if (o != null) {
            GoogleSignInAccount account = (GoogleSignInAccount)o;
            _nameText.setText(account.getDisplayName());
            _emailText.setText(account.getEmail());
            _usernameText.setText(account.getGivenName());
        } else {
            _nameText.setText("");
            _emailText.setText("");
            _usernameText.setText("");
        }
    }

    public void signup() {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        progressDialog = new ProgressDialog(RegisterActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        name_surname = _nameText.getText().toString();
        email = _emailText.getText().toString();
        username = _usernameText.getText().toString();
        password = _passwordText.getText().toString();

        onSignupSuccess();
    }

    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        showPacketDialog();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !email.contains("@") || email.length() < 4) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (username.isEmpty() || username.length() < 3) {
            _usernameText.setError("at least 3 characters");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }

    class RegisterTask extends AsyncTask<Void, Void, Boolean> {
        int id;

        public RegisterTask (int packet_id) {
            this.id = packet_id;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (sendRegister(id)) {
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
                setResult(RESULT_OK, null);
                finish();
            } else {
                Toast.makeText(getBaseContext(), registerMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showPacketDialog() {
        dataModels = new ArrayList<>();
        dataModels.add(new Packet(1, "FREE", 0.00, "", "", 3, 3, true));
        dataModels.add(new Packet(2, "PRO", 15.00, "", "", 20, 20, true));
        dataModels.add(new Packet(3, "GOLD", 25.00, "", "", 50, 50, true));

        adapter = new CustomAdapter(dataModels, getApplicationContext());

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this);
        alertDialog.setTitle("Choose one packet");

        View dialogLayout = getLayoutInflater().inflate(R.layout.list_packets, null);
        final ListView alternativeDeviceCodesLw = (ListView) dialogLayout.findViewById(R.id.lvPackets);
        alternativeDeviceCodesLw.setAdapter(adapter);
        alternativeDeviceCodesLw.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        alertDialog.setView(dialogLayout);
        final AlertDialog d = alertDialog.show();

        alternativeDeviceCodesLw.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                int packet_id = dataModels.get(position).getID();
                d.dismiss();
                new RegisterTask(packet_id).execute();
            }
        });
    }

    private boolean sendRegister(int id) {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/register.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            Packet packet = dataModels.get(id - 1);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("username", username));
            params.add(new BasicNameValuePair("password", password));
            params.add(new BasicNameValuePair("name_surname", name_surname));
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("type", packet.getTYPE()));
            params.add(new BasicNameValuePair("price", "" + packet.getPRICE()));
            params.add(new BasicNameValuePair("activation_date", getCurrentDateTime()));
            params.add(new BasicNameValuePair("new_activation_date", getCurrentDateTimePlusOne()));
            params.add(new BasicNameValuePair("daily_limit", "" + packet.getDAILY_LIMIT()));
            params.add(new BasicNameValuePair("max_pictures_upload", "" + packet.getMAX_PICTURE_UPLOAD()));

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

                    registerMsg = obj.getString("msg");

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
        cal.add(Calendar.MONTH, 1);
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