package com.example.androidproject;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class Fragment_MainGroup_TabAdapter extends FragmentPagerAdapter {
    Bundle bundle;
    public Fragment_MainGroup_TabAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    public Fragment_MainGroup_TabAdapter(@NonNull FragmentManager fm, Bundle bundle) {
        super(fm);
        this.bundle=bundle;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return Fragment_MainGroup_Assignment.newInstance(bundle);
            case 1:
                return Fragment_MainGroup_Transcript.newInstance(bundle);
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Bài làm";
            case 1:
                return "Bảng điểm";
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
