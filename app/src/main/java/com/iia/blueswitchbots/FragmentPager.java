/**
 * Blue Switch Bots
 * Copyright (C) 2019 Ishraq Ibne Ashraf <ishraq.i.ashraf@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */

package com.iia.blueswitchbots;

import java.util.ArrayList;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class FragmentPager extends FragmentPagerAdapter {
    private ArrayList<Fragment> fragments = new ArrayList<>();

    FragmentPager(FragmentManager manager) {
        super(manager);

        fragments.add(Constants.FRAGMENT_PAGER_INDEX_BOTS, new BotsFragment());
        fragments.add(Constants.FRAGMENT_PAGER_INDEX_SCAN, new ScanFragment());
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
            case 0:
                return Constants.FRAGMENT_PAGER_TITLE_BOTS;

            case 1:
                return Constants.FRAGMENT_PAGER_TITLE_SCAN;

            default:
                return super.getPageTitle(position);
        }
    }
}
