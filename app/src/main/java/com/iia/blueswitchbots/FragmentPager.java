package com.iia.blueswitchbots;

import java.util.ArrayList;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentPager extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<>();

    FragmentPager(FragmentManager manager) {
        super(manager);

        fragments.add(Constants.FRAGMENTS_PAGER_INDEX_BOTS, new BotsFragment());
        fragments.add(Constants.FRAGMENTS_PAGER_INDEX_SCAN, new ScanFragment());
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
        switch (position) {
            case Constants.FRAGMENTS_PAGER_INDEX_BOTS:
                return "Bots";

            case Constants.FRAGMENTS_PAGER_INDEX_SCAN:
                return "Scan";

            default:
                return super.getPageTitle(position);
        }
    }
}
