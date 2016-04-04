/*
 * Copyright (C) 2015 CaMnter 421482590@qq.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.camnter.easytest.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.SpannableString;
import android.text.TextUtils;
import com.camnter.easyslidingtabs.widget.EasySlidingTabs;
import java.util.List;

/**
 * Description：TabsFragmentAdapter
 * Created by：CaMnter
 * Time：2015-10-15 14:58
 */
public class TabsFragmentAdapter extends FragmentPagerAdapter
        implements EasySlidingTabs.TabsTitleInterface {

    private String[] titles;
    private List<Fragment> fragments;


    public TabsFragmentAdapter(FragmentManager fm, String[] titles, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }


    @Override public SpannableString getTabTitle(int position) {
        CharSequence title = this.getPageTitle(position);
        if (TextUtils.isEmpty(title)) return new SpannableString("");
        return new SpannableString(title);
    }


    /**
     * This method may be called by the ViewPager to obtain a title string
     * to describe the specified page. This method may return null
     * indicating no title for this page. The default implementation returns
     * null.
     *
     * @param position The position of the title requested
     * @return A title for the requested page
     */
    @Override public CharSequence getPageTitle(int position) {
        if (position < titles.length) {
            return titles[position];
        } else {
            return "";
        }
    }


    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position position
     */
    @Override public Fragment getItem(int position) {
        Fragment fragment = this.fragments.get(position);
        if (fragment != null) {
            return this.fragments.get(position);
        } else {
            return null;
        }
    }


    @Override public int getTabDrawableBottom(int position) {
        return 0;
    }


    @Override public int getTabDrawableLeft(int position) {
        return 0;
    }


    @Override public int getTabDrawableRight(int position) {
        return 0;
    }


    @Override public int getTabDrawableTop(int position) {
        return 0;
    }


    /**
     * Return the number of views available.
     */
    @Override public int getCount() {
        return this.fragments.size();
    }
}