package com.camnter.easytest.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.camnter.easytest.R;


/**
 * Description：SecondFragment
 * Created by：CaMnter
 * Time：2015-10-17 12:15
 */
public class SecondFragment extends Fragment {

    private volatile View self;

    private static SecondFragment instance;

    @SuppressLint("ValidFragment")
    private SecondFragment() {
    }

    public static SecondFragment getInstance() {
        if (instance == null) {
            synchronized (SecondFragment.class) {
                if (instance == null)
                    instance = new SecondFragment();
            }
        }
        return instance;
    }

    @SuppressLint("InflateParams")
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.self == null) {
            this.self = inflater.inflate(R.layout.second_fragment, null);
        }
        if (this.self.getParent() != null) {
            ViewGroup parent = (ViewGroup) this.self.getParent();
            parent.removeView(this.self);
        }
        return this.self;
    }
}
