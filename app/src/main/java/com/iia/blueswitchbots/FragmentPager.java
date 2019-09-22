package com.iia.blueswitchbots;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class FragmentPager extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<>();

    FragmentPager(FragmentManager manager) {
        super(manager);

        fragments.add(new BotsFragment());
        fragments.add(new ScanFragment());
        //fragments.add(new LogsFragment());
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0 :
                return "Bots";

            case 1 :
                return "Scan";

            /*
            case 2 :
                return "Logs";
            */

            default:
                return null;
        }
    }
}
