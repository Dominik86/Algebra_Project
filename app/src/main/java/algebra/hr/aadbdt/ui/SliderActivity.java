package algebra.hr.aadbdt.ui;

import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import algebra.hr.aadbdt.R;
import algebra.hr.aadbdt.ui.adapters.ImageAdapter;

public class SliderActivity extends AppCompatActivity {
    public static ImageView ivBackImage, ivInfo;
    public static TextView tvDate, tvTime;
    public static LinearLayout llUredi, llDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);

        setStatusBar();

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPage);
        ImageAdapter adapterView = new ImageAdapter(this);
        mViewPager.setAdapter(adapterView);

        ivBackImage = (ImageView) findViewById(R.id.ivBackImage);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvTime = (TextView) findViewById(R.id.tvTime);
        ivInfo = (ImageView) findViewById(R.id.ivInfo);
        llUredi = (LinearLayout) findViewById(R.id.llUredi);
        llDelete = (LinearLayout) findViewById(R.id.llDelete);

        ivBackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SliderActivity.super.onBackPressed();
            }
        });
    }

    private void setStatusBar() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.background));
    }
}
