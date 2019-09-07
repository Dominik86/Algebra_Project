package algebra.hr.aadbdt.ui.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import algebra.hr.aadbdt.ui.fragments.FragmentPacket;
import algebra.hr.aadbdt.ui.fragments.FragmentProfil;
import algebra.hr.aadbdt.ui.fragments.FragmentView;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FragmentProfil tab1 = new FragmentProfil();
                return tab1;
            case 1:
                FragmentView tab2 = new FragmentView();
                return tab2;
            case 2:
                FragmentPacket tab3 = new FragmentPacket();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}