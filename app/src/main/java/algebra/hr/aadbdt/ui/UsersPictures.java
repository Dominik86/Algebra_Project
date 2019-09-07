package algebra.hr.aadbdt.ui;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import algebra.hr.aadbdt.MainActivity;
import algebra.hr.aadbdt.R;
import algebra.hr.aadbdt.ui.adapters.PicturesAdapter;
import algebra.hr.database.DatabaseOpenHelper;

public class UsersPictures extends AppCompatActivity {
    DatabaseOpenHelper db;
    PicturesAdapter adapter;
    RecyclerView recyclerView;
    String user = "", user_id = "";
    TextView tvUserText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_pictures);

        tvUserText = (TextView) findViewById(R.id.tvUserText);

        setStatusBar();

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            user = extras.getString("user");
            user_id = extras.getString("user_id");
        }

        db = new DatabaseOpenHelper(this);

        tvUserText.setText("Pictures of user " + user);

        showPictureListDetails();
    }

    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));
    }

    private void showPictureListDetails() {
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_pictures);
        recyclerView.setLayoutManager(new LinearLayoutManager(UsersPictures.this));

        adapter = new PicturesAdapter(db.getAllPictures(" WHERE " + DatabaseOpenHelper.USER_ID + " = " + user_id), UsersPictures.this);
        recyclerView.setAdapter(adapter);
    }
}
