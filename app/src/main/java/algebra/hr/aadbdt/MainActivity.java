package algebra.hr.aadbdt;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.SearchView;
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

import algebra.hr.aadbdt.ui.SliderActivity;
import algebra.hr.aadbdt.ui.adapters.PicturesAdapter;
import algebra.hr.aadbdt.ui.classes.MyReceiver;
import algebra.hr.database.AndroidDatabaseManager;
import algebra.hr.database.DatabaseOpenHelper;
import algebra.hr.database.Picture;
import algebra.hr.database.Tag;
import algebra.hr.database.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    DatabaseOpenHelper db;
    User user;
    LinearLayout llMain;
    List<Picture> allPictures = new ArrayList<>();
    String picturesMsg = "";
    HttpURLConnection conn;
    JSONArray finalResult;
    JSONObject obj;
    ProgressDialog progressDialog;
    PicturesAdapter adapter;
    RecyclerView recyclerView;
    SearchView searchViewPictures;
    ImageView ivHashTagFilter, ivSizeFilter, ivUserFilter, ivCalendarFilter, ivFilterClear;
    boolean hashTagFilterCheck = false, sizeFilterCheck = false, userFilterCheck = false, calendarFilterCheck;
    String hashTagFilterText = "", sizeFilterText = "", userFilterText = "", calendarFilterText = "",
            whereClause = "", sizeWhereClause = "", usersWhereClause = "", calendarWhereClause = "";
    List<String> sizeListFilter = new ArrayList<>();
    List<String> userListFilter = new ArrayList<>();
    List<String> calendarListFilter = new ArrayList<>();
    MyReceiver receiver;
    IntentFilter intentFilter;
    boolean isReceiverStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        registerBroadcastReceiver();

        db = new DatabaseOpenHelper(this);
        user = db.getUser();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();
        if (user.getROLE().equals("Anonimn")) {
            nav_Menu.findItem(R.id.nav_slideshow).setVisible(false);
            nav_Menu.findItem(R.id.nav_close).setVisible(false);
            nav_Menu.findItem(R.id.nav_administration).setVisible(false);
        }

        if (user.getROLE().equals("Admin")) {
            nav_Menu.findItem(R.id.nav_administration).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.nav_administration).setVisible(false);
        }

        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.textViewNameSurname);
        nav_user.setText(user.getUSERNAME());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (!user.getROLE().equals("Anonimn")) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(MainActivity.this, TakePictureActivity.class));
                }
            });
        } else {
            CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            p.setAnchorId(View.NO_ID);
            fab.setLayoutParams(p);
            fab.setVisibility(View.GONE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        ivHashTagFilter = (ImageView) findViewById(R.id.ivHashTagFilter);
        ivSizeFilter = (ImageView) findViewById(R.id.ivSizeFilter);
        ivUserFilter = (ImageView) findViewById(R.id.ivUserFilter);
        ivCalendarFilter = (ImageView) findViewById(R.id.ivCalendarFilter);
        ivFilterClear = (ImageView) findViewById(R.id.ivFilterClear);

        ivHashTagFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hashTagFilterCheck = !hashTagFilterCheck;
                if (hashTagFilterCheck) {
                    ivHashTagFilter.setImageDrawable(getDrawable(R.drawable.hashtag));
                } else {
                    ivHashTagFilter.setImageDrawable(getDrawable(R.drawable.hashtag_white));
                }
            }
        });

        ivSizeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sizeFilterCheck = !sizeFilterCheck;
                if (sizeFilterCheck) {
                    showSizeFilterDialog(sizeFilterCheck);
                } else {
                    ivSizeFilter.setImageDrawable(getDrawable(R.drawable.size_white));
                    whereClause = whereClause.replace(sizeWhereClause, "");
                }
            }
        });

        ivUserFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userFilterCheck = !userFilterCheck;
                if (userFilterCheck) {
                    showUserFilterDialog(userFilterCheck);
                } else {
                    ivUserFilter.setImageDrawable(getDrawable(R.drawable.ic_active_users));
                    whereClause = whereClause.replace(usersWhereClause, "");
                }
            }
        });

        ivCalendarFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarFilterCheck = !calendarFilterCheck;
                if (calendarFilterCheck) {
                    showCalendarFilterDialog(calendarFilterCheck);
                } else {
                    ivCalendarFilter.setImageDrawable(getDrawable(R.drawable.calendar_white));
                    whereClause = whereClause.replace(calendarWhereClause, "");
                }
            }
        });

        ivFilterClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilters();
            }
        });

        checkForPictures();

        new GetPicturesTask().execute();
    }

    private void registerBroadcastReceiver() {
        receiver = new MyReceiver();
        intentFilter = new IntentFilter("android.net.conn.CONNECTIIVTY_CHANGED");
    }

    private void showSizeFilterDialog(boolean sizeFilterCheck) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.filter);
        builderSingle.setTitle("Select One filter");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        for (String s : sizeListFilter) {
            arrayAdapter.add(s);
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ivSizeFilter.setImageDrawable(getDrawable(R.drawable.size_white));
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ivSizeFilter.setImageDrawable(getDrawable(R.drawable.size));
                sizeFilterText = arrayAdapter.getItem(which);
                String[] separated = sizeFilterText.split(" x ");
                String width = separated[0];
                String height = separated[1];
                if (whereClause.equals("")) {
                    sizeWhereClause = " WHERE (" + DatabaseOpenHelper.WIDTH + " = " + width + " AND " + DatabaseOpenHelper.HEIGHT + " = " + height + ")";
                } else {
                    sizeWhereClause = " AND (" + DatabaseOpenHelper.WIDTH + " = " + width + " AND " + DatabaseOpenHelper.HEIGHT + " = " + height + ")";
                }

                whereClause += sizeWhereClause;
                adapter = new PicturesAdapter(db.getAllPictures(whereClause), MainActivity.this);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }

    private void showUserFilterDialog(boolean userFilterCheck) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.filter);
        builderSingle.setTitle("Select One filter");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        for (String s : userListFilter) {
            arrayAdapter.add(s);
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ivUserFilter.setImageDrawable(getDrawable(R.drawable.ic_active_users));
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ivUserFilter.setImageDrawable(getDrawable(R.drawable.ic_active_users_blue));
                userFilterText = arrayAdapter.getItem(which);

                if (whereClause.equals("")) {
                    usersWhereClause = " WHERE " + DatabaseOpenHelper.PICTURE_USERNAME + " = '" + userFilterText + "'";
                } else {
                    usersWhereClause = " AND " + DatabaseOpenHelper.PICTURE_USERNAME + " = '" + userFilterText + "'";
                }

                whereClause += usersWhereClause;
                adapter = new PicturesAdapter(db.getAllPictures(whereClause), MainActivity.this);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }

    private void showCalendarFilterDialog(boolean calendarFilterCheck) {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
        builderSingle.setIcon(R.drawable.filter);
        builderSingle.setTitle("Select One filter");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
        for (String s : calendarListFilter) {
            arrayAdapter.add(s);
        }

        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ivCalendarFilter.setImageDrawable(getDrawable(R.drawable.calendar_white));
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ivCalendarFilter.setImageDrawable(getDrawable(R.drawable.calendar));
                calendarFilterText = arrayAdapter.getItem(which);

                if (whereClause.equals("")) {
                    calendarWhereClause = " WHERE " + DatabaseOpenHelper.PICTURE_MSG + " LIKE '" + reformatDate(calendarFilterText) + "%'";
                } else {
                    calendarWhereClause = " AND " + DatabaseOpenHelper.PICTURE_MSG + " LIKE '" + reformatDate(calendarFilterText) + "%'";
                }

                whereClause += calendarWhereClause;
                adapter = new PicturesAdapter(db.getAllPictures(whereClause), MainActivity.this);
                recyclerView.setAdapter(adapter);
                dialog.dismiss();
            }
        });

        builderSingle.show();
    }

    private void resetFilters() {
        hashTagFilterCheck = false;
        sizeFilterCheck = false;
        userFilterCheck = false;
        calendarFilterCheck = false;
        ivHashTagFilter.setImageDrawable(getDrawable(R.drawable.hashtag_white));
        ivSizeFilter.setImageDrawable(getDrawable(R.drawable.size_white));
        ivUserFilter.setImageDrawable(getDrawable(R.drawable.ic_active_users));
        ivCalendarFilter.setImageDrawable(getDrawable(R.drawable.calendar_white));
        hashTagFilterText = "";
        sizeFilterText = "";
        userFilterText = "";
        calendarFilterText = "";
        whereClause = "";
        adapter = new PicturesAdapter(db.getAllPictures(""), MainActivity.this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, intentFilter);

        new CheckAvailableUpdatePacket().execute();

        checkForPictures();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(receiver);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        try {
            String text = s;
            if (hashTagFilterCheck) {
                adapter.filter(text);
            }
        } catch (Exception e) {
        }

        return false;
    }

    class CheckAvailableUpdatePacket extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (checkPacket()) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
    }

    private boolean checkPacket() {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/update_packet.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("id", user.getUSER_ID()));
            params.add(new BasicNameValuePair("date", getCurrentDate() + " 23:59:59"));

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

                    if (obj.getString("check").equals("1")) {
                        User user = db.getUser();
                        user.setPACKET_TYPE(obj.getString("packet_type"));
                        db.UpdateUser(user);
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

    private void checkForPictures() {
        llMain = (LinearLayout) findViewById(R.id.llMain);
        if (allPictures.size() == 0) {
            findViewById(R.id.ivNoImage).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.ivNoImage).setVisibility(View.GONE);
            showPictureListDetails();
        }
    }

    private void showPictureListDetails() {
        for (int i = 0; i < llMain.getChildCount(); i++) {
            View v =  (View)llMain.getChildAt(i);
            if (v instanceof ImageView) {
                ImageView iv = (ImageView) v;
                iv.setVisibility(View.GONE);
            }
        }

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_pictures);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

        try {
            adapter = new PicturesAdapter(db.getAllPictures(""), MainActivity.this);
        } catch (Exception e) {
            db = new DatabaseOpenHelper(this);
            adapter = new PicturesAdapter(db.getAllPictures(""), MainActivity.this);
        }
        recyclerView.setAdapter(adapter);

        searchViewPictures = (SearchView) findViewById(R.id.searchViewPictures);
        int id = searchViewPictures.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
        TextView textView = (TextView) searchViewPictures.findViewById(id);
        textView.setTextColor(Color.BLACK);

        searchViewPictures.setOnQueryTextListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem login = menu.findItem(R.id.action_login);
        if (user.getROLE().equals("Anonimn")) {
            login.setVisible(true);
        } else {
            login.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_login) {
            finish();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_profil) {
            startActivity(new Intent(MainActivity.this, ProfilActivity.class));
        } else if (id == R.id.nav_administration) {
            startActivity(new Intent(MainActivity.this, AdminActivity.class));
        } else if (id == R.id.nav_camera) {
            startActivity(new Intent(MainActivity.this, TakePictureActivity.class));
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(MainActivity.this, SliderActivity.class));
        } else if (id == R.id.nav_manage) {
            Intent dbmanager = new Intent(MainActivity.this, AndroidDatabaseManager.class);
            startActivity(dbmanager);
        } else if (id == R.id.nav_close) {
            logoutUser();
        } else if (id == R.id.nav_exit) {
            exitApp();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logoutUser() {
        showLogoutDialog();
    }

    private void showLogoutDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setTitle("LOGOUT");
        alertDialogBuilder.setIcon(R.drawable.ic_exit_app);
        alertDialogBuilder
                .setMessage("Log out from app?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        user.setUSERNAME("");
                        user.setPASSWORD("");
                        user.setNAME_SURNAME("Anonymus");
                        user.setEMAIL("");
                        user.setROLE("Anonimn");
                        user.setPACKET_ID(1);
                        user.setPACKET_DATE_START(getCurrentDateTime());
                        user.setPACKET_DATE_END("");
                        user.setUSER_ID("");
                        db.UpdateUser(user);
                        exitApp();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private String getCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(c);
    }

    private String getCurrentDateTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(c);
    }

    private void exitApp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.finishAndRemoveTask();
        } else {
            super.finish();
        }
    }

    class GetPicturesTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MainActivity.this, R.style.AppTheme_Dark_Dialog);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Getting pictures...");
            progressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (getPictures()) {
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

            if (!isReceiverStarted) {
                isReceiverStarted = true;
                Intent intent = new Intent("android.net.conn.CONNECTIIVTY_CHANGED");
                sendBroadcast(intent);
            }

            if (result) {
                showPictureListDetails();
            } else {
                Toast.makeText(MainActivity.this, picturesMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean getPictures() {
        URL url;
        String response = "";

        try {
            url = new URL("http://185.62.75.218/algebra/get_pictures.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(4000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("admin", "all"));

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

                    picturesMsg = obj.getString("msg");

                    if (obj.getString("check").equals("1")) {
                        if (obj.getJSONArray("records") != null && obj.getJSONArray("records").length() > 0) {
                            allPictures.clear();
                            sizeListFilter.clear();
                            userListFilter.clear();
                            calendarListFilter.clear();

                            for (int i = 0; i < obj.getJSONArray("records").length(); i++) {
                                Picture picture = new Picture();
                                picture.setID(Integer.parseInt(obj.getJSONArray("records").getJSONObject(i).getString("id")));
                                picture.setDESCRIPTION(obj.getJSONArray("records").getJSONObject(i).getString("description"));
                                picture.setWIDTH(Integer.parseInt(obj.getJSONArray("records").getJSONObject(i).getString("width")));
                                picture.setHEIGHT(Integer.parseInt(obj.getJSONArray("records").getJSONObject(i).getString("height")));
                                picture.setFORMAT(obj.getJSONArray("records").getJSONObject(i).getString("format"));
                                picture.setUSER_ID(Integer.parseInt(obj.getJSONArray("records").getJSONObject(i).getString("user_id")));
                                picture.setFLAG("1");
                                picture.setDATA(obj.getJSONArray("records").getJSONObject(i).getString("data").getBytes());
                                picture.setDATEADDES(obj.getJSONArray("records").getJSONObject(i).getString("dateaddes"));
                                List<Tag> tagListToAdd = new ArrayList<>();
                                for (int j = 0; j < obj.getJSONArray("records").getJSONObject(i).getJSONArray("tags").length(); j++) {
                                    Tag tag = new Tag();
                                    tag.setID(Integer.parseInt(obj.getJSONArray("records").getJSONObject(i).getJSONArray("tags").getJSONObject(j).getString("id")));
                                    tag.setPICTURE_ID(Integer.parseInt(obj.getJSONArray("records").getJSONObject(i).getJSONArray("tags").getJSONObject(j).getString("picture_id")));
                                    tag.setTAG_DATE(obj.getJSONArray("records").getJSONObject(i).getJSONArray("tags").getJSONObject(j).getString("create_time"));
                                    tag.setTAG_TEXT(obj.getJSONArray("records").getJSONObject(i).getJSONArray("tags").getJSONObject(j).getString("text"));
                                    tagListToAdd.add(tag);
                                    picture.setLISTA_TAGOVA(tagListToAdd);
                                }
                                allPictures.add(picture);

                                String size = obj.getJSONArray("records").getJSONObject(i).getString("width")
                                        + " x " + obj.getJSONArray("records").getJSONObject(i).getString("height");
                                if (!sizeListFilter.contains(size)) {
                                    sizeListFilter.add(size);
                                }

                                String username = obj.getJSONArray("records").getJSONObject(i).getString("username");
                                if (!userListFilter.contains(username)) {
                                    userListFilter.add(username);
                                }

                                String date = formatDate(obj.getJSONArray("records").getJSONObject(i).getString("dateaddes").substring(0, 10));
                                if (!calendarListFilter.contains(date)) {
                                    calendarListFilter.add(date);
                                }
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

    private String reformatDate(String old_date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
            Date date = sdf.parse(old_date);
            SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
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
