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
 * Description：ThirdFragment
 * Created by：CaMnter
 * Time：2015-10-17 12:15
 */
public class ThirdFragment extends Fragment {

    private volatile View self;

    private static ThirdFragment instance;


    @SuppressLint("ValidFragment") private ThirdFragment() {
    }


    public static ThirdFragment getInstance() {
        if (instance == null) {
            synchronized (ThirdFragment.class) {
                if (instance == null) instance = new ThirdFragment();
            }
        }
        return instance;
    }


    @SuppressLint("InflateParams") @Nullable @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.self == null) {
            this.self = inflater.inflate(R.layout.third_fragment, null);
        }
        if (this.self.getParent() != null) {
            ViewGroup parent = (ViewGroup) this.self.getParent();
            parent.removeView(this.self);
        }
        return this.self;
    }
}
