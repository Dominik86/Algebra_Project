package algebra.hr.database;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

//public class AndroidDatabaseManager {
public class AndroidDatabaseManager extends Activity implements AdapterView.OnItemClickListener {

    static class indexInfo {
        public static int index = 10;
        public static int numberofpages = 0;
        public static int currentpage = 0;
        public static String table_name = "";
        public static Cursor maincursor;
        public static int cursorpostion = 0;
        public static ArrayList<String> value_string;
        public static ArrayList<String> tableheadernames;
        public static ArrayList<String> emptytablecolumnnames;
        public static boolean isEmpty;
        public static boolean isCustomQuery;
    }

    DatabaseOpenHelper dbm;
    TableLayout tableLayout;
    TableRow.LayoutParams tableRowParams;
    HorizontalScrollView hsv;
    ScrollView mainscrollview;
    LinearLayout mainLayout;
    TextView tvmessage;
    Button previous;
    Button next;
    Spinner select_table;
    TextView tv;
    indexInfo info = new indexInfo();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbm = new DatabaseOpenHelper(AndroidDatabaseManager.this);
        mainscrollview = new ScrollView(AndroidDatabaseManager.this);
        mainLayout = new LinearLayout(AndroidDatabaseManager.this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(Color.WHITE);
        mainLayout.setScrollContainer(true);
        mainscrollview.addView(mainLayout);

        setContentView(mainscrollview);

        final LinearLayout firstrow = new LinearLayout(AndroidDatabaseManager.this);
        firstrow.setPadding(0, 10, 0, 20);
        LinearLayout.LayoutParams firstrowlp = new LinearLayout.LayoutParams(0, 150);
        firstrowlp.weight = 1;

        TextView maintext = new TextView(AndroidDatabaseManager.this);
        maintext.setText("Select Table");
        maintext.setTextSize(22);
        maintext.setLayoutParams(firstrowlp);
        select_table = new Spinner(AndroidDatabaseManager.this);
        select_table.setLayoutParams(firstrowlp);

        firstrow.addView(maintext);
        firstrow.addView(select_table);
        mainLayout.addView(firstrow);

        ArrayList<Cursor> alc;

        hsv = new HorizontalScrollView(AndroidDatabaseManager.this);

        tableLayout = new TableLayout(AndroidDatabaseManager.this);
        tableLayout.setHorizontalScrollBarEnabled(true);
        hsv.addView(tableLayout);

        final LinearLayout secondrow = new LinearLayout(AndroidDatabaseManager.this);
        secondrow.setPadding(0, 20, 0, 10);
        LinearLayout.LayoutParams secondrowlp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        secondrowlp.weight = 1;
        TextView secondrowtext = new TextView(AndroidDatabaseManager.this);
        secondrowtext.setText("No. Of Records : ");
        secondrowtext.setTextSize(20);
        secondrowtext.setLayoutParams(secondrowlp);
        tv = new TextView(AndroidDatabaseManager.this);
        tv.setTextSize(20);
        tv.setLayoutParams(secondrowlp);
        secondrow.addView(secondrowtext);
        secondrow.addView(tv);
        mainLayout.addView(secondrow);

        final EditText customquerytext = new EditText(this);
        customquerytext.setVisibility(View.GONE);
        customquerytext.setHint("Enter Your Query here and Click on Submit Query Button .Results will be displayed below");
        mainLayout.addView(customquerytext);

        final Button submitQuery = new Button(AndroidDatabaseManager.this);
        submitQuery.setVisibility(View.GONE);
        submitQuery.setText("Submit Query");

        submitQuery.setBackgroundColor(Color.parseColor("#BAE7F6"));
        mainLayout.addView(submitQuery);

        final TextView help = new TextView(AndroidDatabaseManager.this);
        help.setText("Click on the row below to update values or delete the tuple");
        help.setPadding(0, 5, 0, 5);

        final Spinner spinnertable = new Spinner(AndroidDatabaseManager.this);
        mainLayout.addView(spinnertable);
        mainLayout.addView(help);
        hsv.setPadding(0, 10, 0, 10);
        hsv.setScrollbarFadingEnabled(false);
        hsv.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_INSET);
        mainLayout.addView(hsv);

        final LinearLayout thirdrow = new LinearLayout(AndroidDatabaseManager.this);
        previous = new Button(AndroidDatabaseManager.this);
        previous.setText("Previous");

        previous.setBackgroundColor(Color.parseColor("#BAE7F6"));
        previous.setLayoutParams(secondrowlp);
        next = new Button(AndroidDatabaseManager.this);
        next.setText("Next");
        next.setBackgroundColor(Color.parseColor("#BAE7F6"));
        next.setLayoutParams(secondrowlp);

        TextView tvblank = new TextView(this);
        tvblank.setLayoutParams(secondrowlp);
        thirdrow.setPadding(0, 10, 0, 10);
        thirdrow.addView(previous);
        thirdrow.addView(tvblank);
        thirdrow.addView(next);
        mainLayout.addView(thirdrow);

        tvmessage = new TextView(AndroidDatabaseManager.this);
        tvmessage.setText("Error Messages will be displayed here");
        String Query = "SELECT name _id FROM sqlite_master WHERE type ='table'";
        tvmessage.setTextSize(18);
        mainLayout.addView(tvmessage);

        final Button customQuery = new Button(AndroidDatabaseManager.this);
        customQuery.setText("Custom Query");
        customQuery.setBackgroundColor(Color.parseColor("#BAE7F6"));
        mainLayout.addView(customQuery);

        customQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                indexInfo.isCustomQuery = true;
                secondrow.setVisibility(View.GONE);
                spinnertable.setVisibility(View.GONE);
                help.setVisibility(View.GONE);
                customquerytext.setVisibility(View.VISIBLE);
                submitQuery.setVisibility(View.VISIBLE);
                select_table.setSelection(0);
                customQuery.setVisibility(View.GONE);
            }
        });

        submitQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tableLayout.removeAllViews();
                customQuery.setVisibility(View.GONE);

                ArrayList<Cursor> alc2;
                String Query10 = customquerytext.getText().toString();
                alc2 = dbm.getData(Query10);
                final Cursor c4 = alc2.get(0);
                Cursor Message2 = alc2.get(1);
                Message2.moveToLast();

                if (Message2.getString(0).equalsIgnoreCase("Success")) {
                    tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));

                    if (c4 != null) {
                        tvmessage.setText("Queru Executed successfully.Number of rows returned :" + c4.getCount());

                        if (c4.getCount() > 0) {
                            indexInfo.maincursor = c4;
                            refreshTable(1);
                        }
                    } else {
                        tvmessage.setText("Queru Executed successfully");
                        refreshTable(1);
                    }
                } else {
                    tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                    tvmessage.setText("Error:" + Message2.getString(0));
                }
            }
        });

        tableRowParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        tableRowParams.setMargins(0, 0, 2, 0);

        alc = dbm.getData(Query);
        final Cursor c = alc.get(0);
        Cursor Message = alc.get(1);

        Message.moveToLast();
        String msg = Message.getString(0);

        ArrayList<String> tablenames = new ArrayList<String>();

        if (c != null) {
            c.moveToFirst();
            tablenames.add("click here");

            do {
                tablenames.add(c.getString(0));
            } while (c.moveToNext());
        }

        ArrayAdapter<String> tablenamesadapter = new ArrayAdapter<String>(AndroidDatabaseManager.this, android.R.layout.simple_spinner_item, tablenames) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setBackgroundColor(Color.WHITE);
                TextView adap = (TextView) v;
                adap.setTextSize(20);

                return adap;
            }


            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                v.setBackgroundColor(Color.WHITE);

                return v;
            }
        };

        tablenamesadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (tablenamesadapter != null) {
            select_table.setAdapter(tablenamesadapter);
        }

        select_table.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0 && !indexInfo.isCustomQuery) {
                    secondrow.setVisibility(View.GONE);
                    hsv.setVisibility(View.GONE);
                    thirdrow.setVisibility(View.GONE);
                    spinnertable.setVisibility(View.GONE);
                    help.setVisibility(View.GONE);
                    tvmessage.setVisibility(View.GONE);
                    customquerytext.setVisibility(View.GONE);
                    submitQuery.setVisibility(View.GONE);
                    customQuery.setVisibility(View.GONE);
                }

                if (pos != 0) {
                    secondrow.setVisibility(View.VISIBLE);
                    spinnertable.setVisibility(View.VISIBLE);
                    help.setVisibility(View.VISIBLE);
                    customquerytext.setVisibility(View.GONE);
                    submitQuery.setVisibility(View.GONE);
                    customQuery.setVisibility(View.VISIBLE);
                    hsv.setVisibility(View.VISIBLE);
                    tvmessage.setVisibility(View.VISIBLE);
                    thirdrow.setVisibility(View.VISIBLE);
                    c.moveToPosition(pos - 1);
                    indexInfo.cursorpostion = pos - 1;
                    indexInfo.table_name = c.getString(0);
                    tvmessage.setText("Error Messages will be displayed here");
                    tvmessage.setBackgroundColor(Color.WHITE);

                    tableLayout.removeAllViews();
                    ArrayList<String> spinnertablevalues = new ArrayList<String>();
                    spinnertablevalues.add("Click here to change this table");
                    spinnertablevalues.add("Add row to this table");
                    spinnertablevalues.add("Delete this table");
                    spinnertablevalues.add("Drop this table");
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, spinnertablevalues);
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(AndroidDatabaseManager.this, android.R.layout.simple_spinner_item, spinnertablevalues) {
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View v = super.getView(position, convertView, parent);
                            v.setBackgroundColor(Color.WHITE);
                            TextView adap = (TextView) v;
                            adap.setTextSize(20);

                            return adap;
                        }

                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View v = super.getDropDownView(position, convertView, parent);
                            v.setBackgroundColor(Color.WHITE);

                            return v;
                        }
                    };

                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnertable.setAdapter(adapter);
                    String Query2 = "select * from " + c.getString(0);

                    ArrayList<Cursor> alc2 = dbm.getData(Query2);
                    final Cursor c2 = alc2.get(0);
                    indexInfo.maincursor = c2;

                    if (c2 != null) {
                        int counts = c2.getCount();
                        indexInfo.isEmpty = false;
                        tv.setText("" + counts);

                        spinnertable.setOnItemSelectedListener((new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                                ((TextView) parentView.getChildAt(0)).setTextColor(Color.rgb(0, 0, 0));

                                if (spinnertable.getSelectedItem().toString().equals("Drop this table")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isFinishing()) {
                                                new AlertDialog.Builder(AndroidDatabaseManager.this)
                                                        .setTitle("Are you sure ?")
                                                        .setMessage("Pressing yes will remove " + indexInfo.table_name + " table from database")
                                                        .setPositiveButton("yes",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        String Query6 = "Drop table " + indexInfo.table_name;
                                                                        ArrayList<Cursor> aldropt = dbm.getData(Query6);
                                                                        Cursor tempc = aldropt.get(1);
                                                                        tempc.moveToLast();

                                                                        if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                                            tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                                            tvmessage.setText(indexInfo.table_name + "Dropped successfully");
                                                                            refreshactivity();
                                                                        } else {
                                                                            tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                                            tvmessage.setText("Error:" + tempc.getString(0));
                                                                            spinnertable.setSelection(0);
                                                                        }
                                                                    }
                                                                })
                                                        .setNegativeButton("No",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        spinnertable.setSelection(0);
                                                                    }
                                                                })
                                                        .create().show();
                                            }
                                        }
                                    });
                                }

                                if (spinnertable.getSelectedItem().toString().equals("Delete this table")) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isFinishing()) {
                                                new AlertDialog.Builder(AndroidDatabaseManager.this)
                                                        .setTitle("Are you sure?")
                                                        .setMessage("Clicking on yes will delete all the contents of " + indexInfo.table_name + " table from database")
                                                        .setPositiveButton("yes",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        String Query7 = "Delete  from " + indexInfo.table_name;
                                                                        ArrayList<Cursor> aldeletet = dbm.getData(Query7);
                                                                        Cursor tempc = aldeletet.get(1);
                                                                        tempc.moveToLast();

                                                                        if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                                            tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                                            tvmessage.setText(indexInfo.table_name + " table content deleted successfully");
                                                                            indexInfo.isEmpty = true;
                                                                            refreshTable(0);
                                                                        } else {
                                                                            tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                                            tvmessage.setText("Error:" + tempc.getString(0));
                                                                            spinnertable.setSelection(0);
                                                                        }
                                                                    }
                                                                })
                                                        .setNegativeButton("No",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        spinnertable.setSelection(0);
                                                                    }
                                                                })
                                                        .create().show();
                                            }
                                        }
                                    });
                                }

                                if (spinnertable.getSelectedItem().toString().equals("Add row to this table")) {
                                    final LinkedList<TextView> addnewrownames = new LinkedList<TextView>();
                                    final LinkedList<EditText> addnewrowvalues = new LinkedList<EditText>();
                                    final ScrollView addrowsv = new ScrollView(AndroidDatabaseManager.this);
                                    Cursor c4 = indexInfo.maincursor;

                                    if (indexInfo.isEmpty) {
                                        getcolumnnames();

                                        for (int i = 0; i < indexInfo.emptytablecolumnnames.size(); i++) {
                                            String cname = indexInfo.emptytablecolumnnames.get(i);
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText(cname);
                                            addnewrownames.add(tv);
                                        }

                                        for (int i = 0; i < addnewrownames.size(); i++) {
                                            EditText et = new EditText(getApplicationContext());
                                            addnewrowvalues.add(et);
                                        }
                                    } else {
                                        for (int i = 0; i < c4.getColumnCount(); i++) {
                                            String cname = c4.getColumnName(i);
                                            TextView tv = new TextView(getApplicationContext());
                                            tv.setText(cname);
                                            addnewrownames.add(tv);
                                        }

                                        for (int i = 0; i < addnewrownames.size(); i++) {
                                            EditText et = new EditText(getApplicationContext());
                                            addnewrowvalues.add(et);
                                        }
                                    }

                                    final RelativeLayout addnewlayout = new RelativeLayout(AndroidDatabaseManager.this);
                                    RelativeLayout.LayoutParams addnewparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                    addnewparams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

                                    for (int i = 0; i < addnewrownames.size(); i++) {
                                        TextView tv = addnewrownames.get(i);
                                        EditText et = addnewrowvalues.get(i);

                                        int t = i + 400;
                                        int k = i + 500;
                                        int lid = i + 600;

                                        tv.setId(t);
                                        tv.setTextColor(Color.parseColor("#000000"));
                                        et.setBackgroundColor(Color.parseColor("#F2F2F2"));
                                        et.setTextColor(Color.parseColor("#000000"));
                                        et.setId(k);

                                        final LinearLayout ll = new LinearLayout(AndroidDatabaseManager.this);
                                        LinearLayout.LayoutParams tvl = new LinearLayout.LayoutParams(0, 100);
                                        tvl.weight = 1;
                                        ll.addView(tv, tvl);
                                        ll.addView(et, tvl);
                                        ll.setId(lid);

                                        RelativeLayout.LayoutParams rll = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                        rll.addRule(RelativeLayout.BELOW, ll.getId() - 1);
                                        rll.setMargins(0, 20, 0, 0);
                                        addnewlayout.addView(ll, rll);
                                    }

                                    addnewlayout.setBackgroundColor(Color.WHITE);
                                    addrowsv.addView(addnewlayout);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!isFinishing()) {
                                                new AlertDialog.Builder(AndroidDatabaseManager.this)
                                                        .setTitle("values")
                                                        .setCancelable(false)
                                                        .setView(addrowsv)
                                                        .setPositiveButton("Add",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        indexInfo.index = 10;
                                                                        String Query4 = "Insert into " + indexInfo.table_name + " (";

                                                                        for (int i = 0; i < addnewrownames.size(); i++) {
                                                                            TextView tv = addnewrownames.get(i);
                                                                            tv.getText().toString();

                                                                            if (i == addnewrownames.size() - 1) {
                                                                                Query4 = Query4 + tv.getText().toString();
                                                                            } else {
                                                                                Query4 = Query4 + tv.getText().toString() + ", ";
                                                                            }
                                                                        }

                                                                        Query4 = Query4 + " ) VALUES ( ";

                                                                        for (int i = 0; i < addnewrownames.size(); i++) {
                                                                            EditText et = addnewrowvalues.get(i);
                                                                            et.getText().toString();

                                                                            if (i == addnewrownames.size() - 1) {
                                                                                Query4 = Query4 + "'" + et.getText().toString() + "' ) ";
                                                                            } else {
                                                                                Query4 = Query4 + "'" + et.getText().toString() + "' , ";
                                                                            }
                                                                        }

                                                                        ArrayList<Cursor> altc = dbm.getData(Query4);
                                                                        Cursor tempc = altc.get(1);
                                                                        tempc.moveToLast();

                                                                        if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                                            tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                                            tvmessage.setText("New Row added succesfully to " + indexInfo.table_name);
                                                                            refreshTable(0);
                                                                        } else {
                                                                            tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                                            tvmessage.setText("Error:" + tempc.getString(0));
                                                                            spinnertable.setSelection(0);
                                                                        }
                                                                    }
                                                                })
                                                        .setNegativeButton("close",
                                                                new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        spinnertable.setSelection(0);
                                                                    }
                                                                })
                                                        .create().show();
                                            }
                                        }
                                    });
                                }
                            }

                            public void onNothingSelected(AdapterView<?> arg0) {
                            }
                        }));

                        TableRow tableheader = new TableRow(getApplicationContext());
                        tableheader.setBackgroundColor(Color.BLACK);
                        tableheader.setPadding(0, 2, 0, 2);

                        for (int k = 0; k < c2.getColumnCount(); k++) {
                            LinearLayout cell = new LinearLayout(AndroidDatabaseManager.this);
                            cell.setBackgroundColor(Color.WHITE);
                            cell.setLayoutParams(tableRowParams);

                            final TextView tableheadercolums = new TextView(getApplicationContext());
                            tableheadercolums.setPadding(0, 0, 4, 3);
                            tableheadercolums.setText("" + c2.getColumnName(k));
                            tableheadercolums.setTextColor(Color.parseColor("#000000"));

                            cell.addView(tableheadercolums);
                            tableheader.addView(cell);
                        }

                        tableLayout.addView(tableheader);
                        c2.moveToFirst();
                        paginatetable(c2.getCount());
                    } else {
                        help.setVisibility(View.GONE);
                        tableLayout.removeAllViews();
                        getcolumnnames();

                        TableRow tableheader2 = new TableRow(getApplicationContext());
                        tableheader2.setBackgroundColor(Color.BLACK);
                        tableheader2.setPadding(0, 2, 0, 2);

                        LinearLayout cell = new LinearLayout(AndroidDatabaseManager.this);
                        cell.setBackgroundColor(Color.WHITE);
                        cell.setLayoutParams(tableRowParams);

                        final TextView tableheadercolums = new TextView(getApplicationContext());
                        tableheadercolums.setPadding(0, 0, 4, 3);
                        tableheadercolums.setText("   Table   Is   Empty   ");
                        tableheadercolums.setTextSize(30);
                        tableheadercolums.setTextColor(Color.RED);

                        cell.addView(tableheadercolums);
                        tableheader2.addView(cell);

                        tableLayout.addView(tableheader2);

                        tv.setText("" + 0);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }

    public void getcolumnnames() {
        ArrayList<Cursor> alc3 = dbm.getData("PRAGMA table_info(" + indexInfo.table_name + ")");
        Cursor c5 = alc3.get(0);
        indexInfo.isEmpty = true;

        if (c5 != null) {
            indexInfo.isEmpty = true;
            ArrayList<String> emptytablecolumnnames = new ArrayList<String>();
            c5.moveToFirst();

            do {
                emptytablecolumnnames.add(c5.getString(1));
            } while (c5.moveToNext());

            indexInfo.emptytablecolumnnames = emptytablecolumnnames;
        }
    }

    public void updateDeletePopup(int row) {
        Cursor c2 = indexInfo.maincursor;

        ArrayList<String> spinnerArray = new ArrayList<String>();
        spinnerArray.add("Click Here to Change this row");
        spinnerArray.add("Update this row");
        spinnerArray.add("Delete this row");

        final ArrayList<String> value_string = indexInfo.value_string;
        final LinkedList<TextView> columnames = new LinkedList<TextView>();
        final LinkedList<EditText> columvalues = new LinkedList<EditText>();

        for (int i = 0; i < c2.getColumnCount(); i++) {
            String cname = c2.getColumnName(i);
            TextView tv = new TextView(getApplicationContext());
            tv.setText(cname);
            columnames.add(tv);
        }

        for (int i = 0; i < columnames.size(); i++) {
            String cv = value_string.get(i);
            EditText et = new EditText(getApplicationContext());
            value_string.add(cv);
            et.setText(cv);
            columvalues.add(et);
        }

        int lastrid = 0;
        final RelativeLayout lp = new RelativeLayout(AndroidDatabaseManager.this);
        lp.setBackgroundColor(Color.WHITE);

        RelativeLayout.LayoutParams lay = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lay.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        final ScrollView updaterowsv = new ScrollView(AndroidDatabaseManager.this);
        LinearLayout lcrud = new LinearLayout(AndroidDatabaseManager.this);

        LinearLayout.LayoutParams paramcrudtext = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramcrudtext.setMargins(0, 20, 0, 0);

        final Spinner crud_dropdown = new Spinner(getApplicationContext());

        ArrayAdapter<String> crudadapter = new ArrayAdapter<String>(AndroidDatabaseManager.this, android.R.layout.simple_spinner_item, spinnerArray) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                v.setBackgroundColor(Color.WHITE);
                TextView adap = (TextView) v;
                adap.setTextSize(20);

                return adap;
            }

            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View v = super.getDropDownView(position, convertView, parent);
                v.setBackgroundColor(Color.WHITE);

                return v;
            }
        };

        crudadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        crud_dropdown.setAdapter(crudadapter);
        lcrud.setId(299);
        lcrud.addView(crud_dropdown, paramcrudtext);

        RelativeLayout.LayoutParams rlcrudparam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rlcrudparam.addRule(RelativeLayout.BELOW, lastrid);

        lp.addView(lcrud, rlcrudparam);

        for (int i = 0; i < columnames.size(); i++) {
            TextView tv = columnames.get(i);
            EditText et = columvalues.get(i);

            int t = i + 100;
            int k = i + 200;
            int lid = i + 300;

            tv.setId(t);
            tv.setTextColor(Color.parseColor("#000000"));
            et.setBackgroundColor(Color.parseColor("#F2F2F2"));
            et.setTextColor(Color.parseColor("#000000"));
            et.setId(k);

            final LinearLayout ll = new LinearLayout(AndroidDatabaseManager.this);
            ll.setBackgroundColor(Color.parseColor("#FFFFFF"));
            ll.setId(lid);

            LinearLayout.LayoutParams lpp = new LinearLayout.LayoutParams(0, 100);
            lpp.weight = 1;
            tv.setLayoutParams(lpp);
            et.setLayoutParams(lpp);

            ll.addView(tv);
            ll.addView(et);

            RelativeLayout.LayoutParams rll = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            rll.addRule(RelativeLayout.BELOW, ll.getId() - 1);
            rll.setMargins(0, 20, 0, 0);
            lastrid = ll.getId();
            lp.addView(ll, rll);
        }

        updaterowsv.addView(lp);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isFinishing()) {
                    new AlertDialog.Builder(AndroidDatabaseManager.this)
                            .setTitle("values")
                            .setView(updaterowsv)
                            .setCancelable(false)
                            .setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            String spinner_value = crud_dropdown.getSelectedItem().toString();

                                            if (spinner_value.equalsIgnoreCase("Update this row")) {
                                                indexInfo.index = 10;
                                                String Query3 = "UPDATE " + indexInfo.table_name + " SET ";

                                                for (int i = 0; i < columnames.size(); i++) {
                                                    TextView tvc = columnames.get(i);
                                                    EditText etc = columvalues.get(i);

                                                    if (!etc.getText().toString().equals("null")) {
                                                        Query3 = Query3 + tvc.getText().toString() + " = ";

                                                        if (i == columnames.size() - 1) {
                                                            Query3 = Query3 + "'" + etc.getText().toString() + "'";
                                                        } else {
                                                            Query3 = Query3 + "'" + etc.getText().toString() + "' , ";
                                                        }
                                                    }
                                                }

                                                Query3 = Query3 + " where ";

                                                for (int i = 0; i < columnames.size(); i++) {
                                                    TextView tvc = columnames.get(i);

                                                    if (!value_string.get(i).equals("null")) {
                                                        Query3 = Query3 + tvc.getText().toString() + " = ";

                                                        if (i == columnames.size() - 1) {
                                                            Query3 = Query3 + "'" + value_string.get(i) + "' ";
                                                        } else {
                                                            Query3 = Query3 + "'" + value_string.get(i) + "' and ";
                                                        }
                                                    }
                                                }

                                                ArrayList<Cursor> aluc = dbm.getData(Query3);
                                                Cursor tempc = aluc.get(1);
                                                tempc.moveToLast();

                                                if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                    tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                    tvmessage.setText(indexInfo.table_name + " table Updated Successfully");
                                                    refreshTable(0);
                                                } else {
                                                    tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                    tvmessage.setText("Error:" + tempc.getString(0));
                                                }
                                            }

                                            if (spinner_value.equalsIgnoreCase("Delete this row")) {
                                                indexInfo.index = 10;
                                                String Query5 = "DELETE FROM " + indexInfo.table_name + " WHERE ";

                                                for (int i = 0; i < columnames.size(); i++) {
                                                    TextView tvc = columnames.get(i);

                                                    if (!value_string.get(i).equals("null")) {
                                                        Query5 = Query5 + tvc.getText().toString() + " = ";

                                                        if (i == columnames.size() - 1) {
                                                            Query5 = Query5 + "'" + value_string.get(i) + "' ";
                                                        } else {
                                                            Query5 = Query5 + "'" + value_string.get(i) + "' and ";
                                                        }
                                                    }
                                                }

                                                dbm.getData(Query5);
                                                ArrayList<Cursor> aldc = dbm.getData(Query5);
                                                Cursor tempc = aldc.get(1);
                                                tempc.moveToLast();

                                                if (tempc.getString(0).equalsIgnoreCase("Success")) {
                                                    tvmessage.setBackgroundColor(Color.parseColor("#2ecc71"));
                                                    tvmessage.setText("Row deleted from " + indexInfo.table_name + " table");
                                                    refreshTable(0);
                                                } else {
                                                    tvmessage.setBackgroundColor(Color.parseColor("#e74c3c"));
                                                    tvmessage.setText("Error:" + tempc.getString(0));
                                                }
                                            }
                                        }
                                    })
                            .setNegativeButton("close",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    })
                            .create().show();
                }
            }
        });
    }

    public void refreshactivity() {
        finish();
        startActivity(getIntent());
    }

    public void refreshTable(int d) {
        Cursor c3 = null;
        tableLayout.removeAllViews();

        if (d == 0) {
            String Query8 = "select * from " + indexInfo.table_name;
            ArrayList<Cursor> alc3 = dbm.getData(Query8);
            c3 = alc3.get(0);
            indexInfo.maincursor = c3;
        }

        if (d == 1) {
            c3 = indexInfo.maincursor;
        }

        if (c3 != null) {
            int counts = c3.getCount();
            tv.setText("" + counts);

            TableRow tableheader = new TableRow(getApplicationContext());
            tableheader.setBackgroundColor(Color.BLACK);
            tableheader.setPadding(0, 2, 0, 2);

            for (int k = 0; k < c3.getColumnCount(); k++) {
                LinearLayout cell = new LinearLayout(AndroidDatabaseManager.this);
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(tableRowParams);

                final TextView tableheadercolums = new TextView(getApplicationContext());
                tableheadercolums.setPadding(0, 0, 4, 3);
                tableheadercolums.setText("" + c3.getColumnName(k));
                tableheadercolums.setTextColor(Color.parseColor("#000000"));

                cell.addView(tableheadercolums);
                tableheader.addView(cell);

            }

            tableLayout.addView(tableheader);
            c3.moveToFirst();
            paginatetable(c3.getCount());
        } else {
            TableRow tableheader2 = new TableRow(getApplicationContext());
            tableheader2.setBackgroundColor(Color.BLACK);
            tableheader2.setPadding(0, 2, 0, 2);

            LinearLayout cell = new LinearLayout(AndroidDatabaseManager.this);
            cell.setBackgroundColor(Color.WHITE);
            cell.setLayoutParams(tableRowParams);

            final TextView tableheadercolums = new TextView(getApplicationContext());
            tableheadercolums.setPadding(0, 0, 4, 3);
            tableheadercolums.setText("   Table   Is   Empty   ");
            tableheadercolums.setTextSize(30);
            tableheadercolums.setTextColor(Color.RED);

            cell.addView(tableheadercolums);
            tableheader2.addView(cell);

            tableLayout.addView(tableheader2);

            tv.setText("" + 0);
        }
    }

    public void paginatetable(final int number) {
        final Cursor c3 = indexInfo.maincursor;
        indexInfo.numberofpages = (c3.getCount() / 10) + 1;
        indexInfo.currentpage = 1;
        c3.moveToFirst();
        int currentrow = 0;

        do {
            final TableRow tableRow = new TableRow(getApplicationContext());
            tableRow.setBackgroundColor(Color.BLACK);
            tableRow.setPadding(0, 2, 0, 2);

            for (int j = 0; j < c3.getColumnCount(); j++) {
                LinearLayout cell = new LinearLayout(this);
                cell.setBackgroundColor(Color.WHITE);
                cell.setLayoutParams(tableRowParams);

                final TextView columsView = new TextView(getApplicationContext());
                String column_data = "";

                try {
                    column_data = c3.getString(j);
                } catch (Exception e) {
                }

                columsView.setText(column_data);
                columsView.setTextColor(Color.parseColor("#000000"));
                columsView.setPadding(0, 0, 4, 3);
                cell.addView(columsView);
                tableRow.addView(cell);
            }

            tableRow.setVisibility(View.VISIBLE);
            currentrow = currentrow + 1;
            tableRow.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final ArrayList<String> value_string = new ArrayList<String>();

                    for (int i = 0; i < c3.getColumnCount(); i++) {
                        LinearLayout llcolumn = (LinearLayout) tableRow.getChildAt(i);
                        TextView tc = (TextView) llcolumn.getChildAt(0);

                        String cv = tc.getText().toString();
                        value_string.add(cv);
                    }

                    indexInfo.value_string = value_string;
                    updateDeletePopup(0);
                }
            });

            tableLayout.addView(tableRow);
        } while (c3.moveToNext() && currentrow < 10);

        indexInfo.index = currentrow;

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tobestartindex = (indexInfo.currentpage - 2) * 10;

                if (indexInfo.currentpage == 1) {
                    Toast.makeText(getApplicationContext(), "This is the first page", Toast.LENGTH_LONG).show();
                } else {
                    indexInfo.currentpage = indexInfo.currentpage - 1;
                    c3.moveToPosition(tobestartindex);

                    boolean decider = true;

                    for (int i = 1; i < tableLayout.getChildCount(); i++) {
                        TableRow tableRow = (TableRow) tableLayout.getChildAt(i);

                        if (decider) {
                            tableRow.setVisibility(View.VISIBLE);

                            for (int j = 0; j < tableRow.getChildCount(); j++) {
                                LinearLayout llcolumn = (LinearLayout) tableRow.getChildAt(j);
                                TextView columsView = (TextView) llcolumn.getChildAt(0);
                                columsView.setText("" + c3.getString(j));
                            }

                            decider = !c3.isLast();

                            if (!c3.isLast()) {
                                c3.moveToNext();
                            }
                        } else {
                            tableRow.setVisibility(View.GONE);
                        }
                    }

                    indexInfo.index = tobestartindex;
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (indexInfo.currentpage >= indexInfo.numberofpages) {
                    Toast.makeText(getApplicationContext(), "This is the last page", Toast.LENGTH_LONG).show();
                } else {
                    indexInfo.currentpage = indexInfo.currentpage + 1;
                    boolean decider = true;

                    for (int i = 1; i < tableLayout.getChildCount(); i++) {
                        TableRow tableRow = (TableRow) tableLayout.getChildAt(i);

                        if (decider) {
                            tableRow.setVisibility(View.VISIBLE);

                            for (int j = 0; j < tableRow.getChildCount(); j++) {
                                LinearLayout llcolumn = (LinearLayout) tableRow.getChildAt(j);
                                TextView columsView = (TextView) llcolumn.getChildAt(0);

                                columsView.setText("" + c3.getString(j));
                            }

                            decider = !c3.isLast();

                            if (!c3.isLast()) {
                                c3.moveToNext();
                            }
                        } else {
                            tableRow.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
    }/**/
}