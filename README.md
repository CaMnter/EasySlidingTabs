EasySlidingTabs
==
[toc]

---

## EasySlidingTabs Attribute



```XML
    <declare-styleable name="EasySlidingTabs">
        <attr name="easyIndicatorColor" format="color" />
        <attr name="easyUnderlineColor" format="color" />
        <attr name="easyTabTextColor" format="color" />
        <attr name="easySelectedTagTextColor" format="color" />
        <attr name="easyDividerColor" format="color" />
        <attr name="easyIndicatorHeight" format="dimension" />
        <attr name="easyUnderlineHeight" format="dimension" />
        <attr name="easyDividerPadding" format="dimension" />
        <attr name="easyTabPaddingLeftRight" format="dimension" />
        <attr name="easyTabBackground" format="reference" />
        <attr name="easyScrollOffset" format="dimension" />
        <attr name="easyShouldExpand" format="boolean" />
        <attr name="easyTextAllCaps" format="boolean" />
        <attr name="easyIndicatorDrawable" format="reference" />
    </declare-styleable>
```

---

## EasySlidingTabs Layout


**activity_main.xml**

```XML
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <com.camnter.easyslidingtabs.widget.EasySlidingTabs
        android:id="@+id/easy_sliding_tabs"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:easyIndicatorColor="#ff57C1CC"
        app:easyUnderlineColor="#ffdddddd"
        app:easyTabTextColor="#ffFF4081"
        app:easySelectedTagTextColor="#ff57C1CC"
        android:paddingBottom="16dp"
        android:paddingTop="16dp" />

    <android.support.v4.view.ViewPager
        android:id="@+id/easy_vp"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1" />

</LinearLayout>

```

---

## FragmentPagerAdapter

```Java
public class TabsFragmentAdapter extends FragmentPagerAdapter implements EasySlidingTabs.TabsTitleInterface {

    private String[] titles;
    private List<Fragment> fragments;
    public TabsFragmentAdapter(FragmentManager fm, String[] titles, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
        this.titles = titles;
    }

    @Override
    public SpannableString getTabTitle(int position) {
        CharSequence title = this.getPageTitle(position);
        if (TextUtils.isEmpty(title)) return new SpannableString("");
        SpannableString spannableString = new SpannableString(title);
        return spannableString;
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
    @Override
    public CharSequence getPageTitle(int position) {
        if (position < titles.length) {
            return titles[position];
        } else {
            return "";
        }
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = this.fragments.get(position);
        if (fragment != null) {
            return this.fragments.get(position);
        } else {
            return null;
        }
    }

    @Override
    public int getTabDrawableBottom(int position) {
        return 0;
    }

    @Override
    public int getTabDrawableLeft(int position) {
        return 0;
    }

    @Override
    public int getTabDrawableRight(int position) {
        return 0;
    }

    @Override
    public int getTabDrawableTop(int position) {
        return 0;
    }


    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return this.fragments.size();
    }
    
}
```

---


## EasySlidingTabs set tab background

**easyslidingtabs**Module：**bg_easy_sliding_tabs.xml：**

```XML
<selector xmlns:android="http://schemas.android.com/apk/res/android" android:exitFadeDuration="@android:integer/config_shortAnimTime">
    <item android:state_pressed="true" android:drawable="@color/bg_easy_sliding_tabs_pressed" />
    <item android:state_focused="true" android:drawable="@color/bg_easy_sliding_tabs_pressed" />
    <item android:drawable="@android:color/transparent" />
</selector>
```

---

## Consequence

![readme_1](https://github.com/CaMnter/AIEditText/raw/master/readme/readme_1.png)






