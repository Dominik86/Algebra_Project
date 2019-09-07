package algebra.hr.aadbdt;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreenActivity extends Activity {

    private ImageView container;
    private AnimationDrawable animationDrawable;
    TextView _versionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        _versionText = (TextView) findViewById(R.id.tv_version);
        _versionText.setText(Html.fromHtml("<h5><font color=\"#000000\">v: </font><font color=\"#EB5A2D\">" + getVersion() + "</font></h5>"));
        container = findViewById(R.id.iv_icons);
        container.setBackgroundResource(R.drawable.splash_animation);

        animationDrawable = (AnimationDrawable) container.getBackground();
    }

    private String getVersion() {
        try {
            PackageInfo pInfo = SplashScreenActivity.this.getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        animationDrawable.start();

        checkAnimationStatus(50, animationDrawable);
    }

    private void checkAnimationStatus(final int time, final AnimationDrawable animationDrawable) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (animationDrawable.getCurrent() != animationDrawable.getFrame(animationDrawable.getNumberOfFrames() - 1)) {
                    checkAnimationStatus(time, animationDrawable);
                } else {
                    try {
                        Thread.sleep(2000);
                    } catch (Exception e) {
                    }
                    finish();
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }
            }
        }, time);
    }
}
