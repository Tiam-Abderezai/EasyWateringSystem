package com.example.easywateringsystem.adapters;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import com.example.easywateringsystem.R;
import com.example.easywateringsystem.fragments.DayFragment;
import com.example.easywateringsystem.fragments.ZoneFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ItemPagerAdapter extends FragmentStatePagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.tab_zones, R.string.tab_schedules};
    private final Context mContext;
    String mSystemKey;
    String mSystemAddress;
    List<String> mZoneKey;


    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    public ItemPagerAdapter(Context context, FragmentManager fm, String systemKey, String systemAddress, List<String> zoneKey) {
        super(fm);
        mContext = context;
        mSystemKey = systemKey;
        mSystemAddress = systemAddress;
        mZoneKey = new ArrayList<String>();
        mZoneKey = zoneKey;
    }

    @Override
    public Fragment getItem(int position) {
        // Bundle below used to pass String from ItemPagerAdapter to ZoneFragment
        // See https://stackoverflow.com/questions/33290309/pass-string-to-a-fragment-from-an-activity
        Fragment fragment = null;
        Bundle args = new Bundle();
        args.putString("KEY_SYSTEM_KEY", mSystemKey);
        args.putString("KEY_SYSTEM_NAME", mSystemAddress);
        args.putSerializable("KEY_ZONE_KEY", (Serializable) mZoneKey);
        switch (position) {
            case 0:
                fragment = new ZoneFragment();
                break;
            case 1:
                fragment = new DayFragment();
                break;
        }
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}