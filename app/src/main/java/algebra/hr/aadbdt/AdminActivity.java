package algebra.hr.aadbdt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
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
import java.util.Date;
import java.util.List;

import algebra.hr.aadbdt.ui.adapters.ListViewLogsAdapter;
import algebra.hr.aadbdt.ui.adapters.UsersAdapter;
import algebra.hr.database.DatabaseOpenHelper;
import algebra.hr.database.Log;
import algebra.hr.database.User;

public class AdminActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    DatabaseOpenHelper db;
    User user;
    private TextView mTextMessage;
    private LinearLayout llStatistics, llUsers, llLogs, llFilterDate, llFilterUsers, llFilterType;
    private TextView tvActiveUsers, tvActivePictures, tvActivePicturesToday, tvActivePackets, tvActiveMoney;
    private ImageView ivClearFilter;
    HttpURLConnection conn;
    JSONArray finalResult;
    JSONObject obj;
    ProgressDialog progressDialog;
    String errorMsg = "", logMsg = "", usersMsg = "", filter_text = "", filter_type = "",
            ACTIVE_USERS = "0", ACTIVE_PICTURES = "0", ACTIVE_PICTURES_TODAY = "0", ACTIVE_PACKETS = "0", ACTIVE_MONEY = "0.00";
    SearchView logSearchView;
    RecyclerView recyclerView, vRecyclerView;
    private UsersAdapter mAdapter;
    private int numColumns = 3;
    ListViewLogsAdapter adapter;
    List<Log> logList = new ArrayList<Log>();
    List<User> listUsers = new ArrayList<User>();
    List<String> listFilterDates = new ArrayList<>();
    List<String> listFilterUsers = new ArrayList<>();
    List<String> listFilterType = new ArrayList<>();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    setStatisticsLayout();
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    setPicturesLayout();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    setLogsLayout();
                    return true;
            }
            return false;
        }
    };

    private void setStatisticsLayout() {
        llStatistics.setVisibility(View.VISIBLE);
        llUsers.setVisibility(View.GONE);
        llLogs.setVisibility(View.GONE);

        new StatisticsTask().execute();
        setStatisticsValues();
    }

    private void setPicturesLayout() {
        llStatistics.setVisibility(View.GONE);
        llUsers.setVisibility(View.VISIBLE);
        llLogs.setVisibility(View.GONE);

        new GetUsersTask().execute();
        setUsersLayout();
    }

    private void setLogsLayout() {
        llStatistics.setVisibility(View.GONE);
        llUsers.setVisibility(View.GONE);
        llLogs.setVisibility(View.VISIBLE);

        new GetLogTask().execute();
        updateLogScreen();
    }

    private void updateLogScreen() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_logs);
        recyclerView.setLayoutManager(new LinearLayoutManager(AdminActivity.this));

        adapter = new ListViewLogsAdapter(logList, AdminActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setStatusBar();

        db = new DatabaseOpenHelper(this);
        user = db.getUser();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        mTextMessage = findViewById(R.id.message);

        llStatistics = findViewById(R.id.llStatistics);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        tvActivePictures = findViewById(R.id.tvActivePictures);
        tvActivePicturesToday = findViewById(R.id.tvActivePicturesToday);
        tvActivePackets = findViewById(R.id.tvActivePackets);
        tvActiveMoney = findViewById(R.id.tvActiveMoney);

        llUsers = findViewById(R.id.llUsers);

        llLogs = findViewById(R.id.llLogs);
        llFilterDate = findViewById(R.id.llFilterDate);
        llFilterUsers = findViewById(R.id.llFilterUsers);
        llFilterType = findViewById(R.id.llFilterType);
        ivClearFilter = findViewById(R.id.ivClearFilters);

        llFilterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog(listFilterDates, "dates");
            }
        });

        llFilterUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog(listFilterUsers, "users");
            }
        });

        llFilterType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterDialog(listFilterType, "types");
            }
        });

        ivClearFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter_text = "";
                filter_type = "";
                adapter.filter(logSearchView.getQuery().toString(), filter_type, filter_text);
            }
        });

        logSearchView = (SearchView) findViewById(R.id.logSearchView);
        int id = logSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) logSearchView.findViewById(id);
        textView.setTextColor(Color.BLACK);

        logSearchView.setOnQueryTextListener(this);

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        setStatisticsLayout();
    }

    private void showFilterDialog(List<String> list, final String type) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AdminActivity.this);
        builderSingle.setIcon(R.drawable.filter);
        builderSingle.setTitle("Select One filter");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AdminActivity.this, android.R.layout.select_dialog_singlechoice);
        for (String s : list) {
            arrayAdapter.add(s);
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filter_text = arrayAdapter.getItem(which);
                filter_type = type;
                dialog.dismiss();
                adapter.filter(logSearchView.getQuery().toString(), filter_type, filter_text);

                /*String strName = arrayAdapter.getItem(which);
                AlertDialog.Builder builderInner = new AlertDialog.Builder(AdminActivity.this);
                builderInner.setMessage(strName);
                builderInner.setTitle("Your Selected Item is");
                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,int which) {
                        dialog.dismiss();
                    }
                });
                builderInner.show();*/
            }
        });

        builderSingle.show();
    }

    private void setUsersLayout() {
        GridLayoutManager lLayout = new GridLayoutManager(AdminActivity.this, numColumns);
        mAdapter = new UsersAdapter(listUsers, AdminActivity.this);

        vRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        vRecyclerView.setHasFixedSize(true);
        vRecyclerView.setLayoutManager(lLayout);
        vRecyclerView.setItemAnimator(new DefaultItemAnimator());
        vRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        try {
            String text = s;
            adapter.filter(text, filter_type, filter_text);
        } catch (Exception e) {
        }

        return false;
    }

    class StatisticsTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AdminActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Get data...");
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
                setStatisticsValues();
            } else {
                Toast.makeText(AdminActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setStatisticsValues() {
        tvActiveUsers.setText(ACTIVE_USERS);
        tvActivePictures.setText(ACTIVE_PICTURES);
        if (ACTIVE_PICTURES_TODAY.toString().equals("0")) {
            tvActivePicturesToday.setText("There is no uploaded pictures today");
        } else {
            tvActivePicturesToday.setText("Today uploaded " + ACTIVE_PICTURES_TODAY + " pictures");
        }
        tvActivePackets.setText(ACTIVE_PACKETS);
        tvActiveMoney.setText(String.format("%.2f", Double.parseDouble(ACTIVE_MONEY)) + " $");
    }

    private boolean getData() {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/statistics.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", "" + user.getUSER_ID()));

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

                    errorMsg = obj.getString("msg");

                    if (obj.getString("check").equals("1")) {
                        ACTIVE_USERS = obj.getString("active_users");
                        ACTIVE_PICTURES = obj.getString("active_pictures");
                        ACTIVE_PICTURES_TODAY = obj.getString("active_pictures_today");
                        ACTIVE_PACKETS = obj.getString("active_packets");
                        ACTIVE_MONEY = obj.getString("active_money");
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

    class GetLogTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AdminActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Get data...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (getLogs()) {
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
                updateLogScreen();
            } else {
                Toast.makeText(AdminActivity.this, logMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean getLogs() {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/get_logs.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("admin", "admin"));

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

                    logMsg = obj.getString("msg");

                    if (obj.getString("check").equals("1")) {
                        if (obj.getJSONArray("records") != null && obj.getJSONArray("records").length() > 0) {
                            logList.clear();
                            listFilterDates.clear();
                            listFilterUsers.clear();
                            listFilterType.clear();
                            for (int i = 0; i < obj.getJSONArray("records").length(); i++) {
                                Log log = new Log();
                                log.setMSG(obj.getJSONArray("records").getJSONObject(i).getString("msg"));
                                log.setDATE_MSG(obj.getJSONArray("records").getJSONObject(i).getString("date"));

                                String date = formatDate(obj.getJSONArray("records").getJSONObject(i).getString("date").substring(0, 10));
                                if (!listFilterDates.contains(date)) {
                                    listFilterDates.add(date);
                                }

                                log.setUSER_ID(Integer.parseInt(obj.getJSONArray("records").getJSONObject(i).getString("user_id")));
                                log.setUSERNAME(obj.getJSONArray("records").getJSONObject(i).getString("username"));

                                String username = obj.getJSONArray("records").getJSONObject(i).getString("username");
                                if (!listFilterUsers.contains(username)) {
                                    listFilterUsers.add(username);
                                }

                                log.setTYPE(obj.getJSONArray("records").getJSONObject(i).getString("type"));

                                String type = obj.getJSONArray("records").getJSONObject(i).getString("type");
                                if (!listFilterType.contains(type)) {
                                    listFilterType.add(type);
                                }
                                logList.add(log);
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

    private String formatDate(String old_date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(old_date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("dd.MM.yyyy.");
            return sdf2.format(date);
        } catch (Exception e) {
            return "";
        }
    }

    class GetUsersTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(AdminActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Get data...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (getUsersList()) {
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
                setUsersLayout();
            } else {
                Toast.makeText(AdminActivity.this, usersMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean getUsersList() {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/get_users.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("admin", "admin"));

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

                    usersMsg = obj.getString("msg");

                    if (obj.getString("check").equals("1")) {
                        if (obj.getJSONArray("records") != null && obj.getJSONArray("records").length() > 0) {
                            listUsers.clear();
                            for (int i = 0; i < obj.getJSONArray("records").length(); i++) {
                                User user = new User();
                                user.setID(Integer.parseInt(obj.getJSONArray("records").getJSONObject(i).getString("id")));
                                user.setUSERNAME(obj.getJSONArray("records").getJSONObject(i).getString("username"));
                                user.setPASSWORD(obj.getJSONArray("records").getJSONObject(i).getString("password"));
                                user.setNAME_SURNAME(obj.getJSONArray("records").getJSONObject(i).getString("name_surname"));
                                user.setEMAIL(obj.getJSONArray("records").getJSONObject(i).getString("email"));
                                user.setROLE(obj.getJSONArray("records").getJSONObject(i).getString("role"));
                                user.setPACKET_ID(Integer.parseInt(obj.getJSONArray("records").getJSONObject(i).getString("packet_id")));
                                user.setPACKET_DATE_START(obj.getJSONArray("records").getJSONObject(i).getString("packet_date_start"));
                                user.setPACKET_DATE_END(obj.getJSONArray("records").getJSONObject(i).getString("packet_date_end"));
                                user.setUSER_ID(obj.getJSONArray("records").getJSONObject(i).getString("id"));
                                user.setPACKET_TYPE(obj.getJSONArray("records").getJSONObject(i).getString("packet_type"));
                                user.setPICTURE_NUMBER(obj.getJSONArray("records").getJSONObject(i).getString("number"));
                                listUsers.add(user);
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

    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));
    }
}
