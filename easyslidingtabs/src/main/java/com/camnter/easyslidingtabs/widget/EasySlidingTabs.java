/*
 * Copyright (C) 2015 CaMnter yuanyu.camnter@gmail.com
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

package com.camnter.easyslidingtabs.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.camnter.easyslidingtabs.R;
import java.util.Locale;

public class EasySlidingTabs extends HorizontalScrollView {

    public interface IconTabProvider {
        int getPageIconResId(int position);
    }

    private static final int[] ATTRS = new int[] { android.R.attr.textSize,
            android.R.attr.textColor };

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public OnPageChangeListener delegatePageListener;

    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;
    private int width;
    private boolean isTabsAdded = false;

    private int currentPosition = 0;
    private int selectedPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    // Default Setting
    private int indicatorColor = 0xffFF4081;
    private int underlineColor = 0xffdddddd;
    private int dividerColor = 0x1A000000;
    private boolean shouldExpand = false;
    private boolean textAllCaps = true;
    private int scrollOffset = 52;
    private int indicatorHeight = 1;
    private int underlineHeight = 1;
    private int dividerPadding = 12;
    // tab as TextView 时的padding
    private int tabPadding = 2;
    private int dividerWidth = 1;
    private int mMyUnderlinePadding = 12;
    private int tabTextSize = 12;
    private int tabTextColor = 0xff303F9F;
    private int selectedTabTextColor = 0xffFF4081;

    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;

    private int lastScrollX = 0;
    private int tabWidth;

    private int tabBackgroundResId = R.drawable.bg_easy_sliding_tabs;

    private Locale locale;
    private Drawable indicatorDrawable;

    private OnGlobalLayoutListener onGlobalLayoutListener = new OnGlobalLayoutListener() {
        @Override public void onGlobalLayout() {
            EasySlidingTabs.this.currentPosition = EasySlidingTabs.this.pager.getCurrentItem();
            scrollToChild(EasySlidingTabs.this.currentPosition, 0);
        }
    };


    public EasySlidingTabs(Context context) {
        this(context, null);
    }


    public EasySlidingTabs(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }


    public EasySlidingTabs(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {

        this.setHorizontalScrollBarEnabled(false);
        this.setFillViewport(true);
        this.setWillNotDraw(false);

        this.tabsContainer = new LinearLayout(context);
        this.tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        this.tabsContainer.setLayoutParams(
                new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        this.addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        this.scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                scrollOffset, dm);
        this.indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                indicatorHeight, dm);
        this.underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                underlineHeight, dm);
        this.dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dividerPadding, dm);
        this.tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding,
                dm);
        this.dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dividerWidth, dm);
        this.tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize,
                dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
        this.tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        this.tabTextColor = a.getColor(1, tabTextColor);
        a.recycle();

        // get custom attrs
        a = context.obtainStyledAttributes(attrs, R.styleable.EasySlidingTabs);
        this.indicatorColor = a.getColor(R.styleable.EasySlidingTabs_easyIndicatorColor,
                indicatorColor);
        this.underlineColor = a.getColor(R.styleable.EasySlidingTabs_easyUnderlineColor,
                underlineColor);
        this.tabTextColor = a.getColor(R.styleable.EasySlidingTabs_easyTabTextColor,
                this.tabTextColor);
        this.selectedTabTextColor = a.getColor(R.styleable.EasySlidingTabs_easySelectedTagTextColor,
                this.selectedTabTextColor);
        this.dividerColor = a.getColor(R.styleable.EasySlidingTabs_easyDividerColor, dividerColor);
        this.indicatorHeight = a.getDimensionPixelSize(
                R.styleable.EasySlidingTabs_easyIndicatorHeight, indicatorHeight);
        this.underlineHeight = a.getDimensionPixelSize(
                R.styleable.EasySlidingTabs_easyUnderlineHeight, underlineHeight);
        this.dividerPadding = a.getDimensionPixelSize(
                R.styleable.EasySlidingTabs_easyDividerPadding, dividerPadding);
        this.tabPadding = a.getDimensionPixelSize(
                R.styleable.EasySlidingTabs_easyTabPaddingLeftRight, tabPadding);
        this.tabBackgroundResId = a.getResourceId(R.styleable.EasySlidingTabs_easyTabBackground,
                tabBackgroundResId);
        this.scrollOffset = a.getDimensionPixelSize(R.styleable.EasySlidingTabs_easyScrollOffset,
                scrollOffset);
        this.shouldExpand = a.getBoolean(R.styleable.EasySlidingTabs_easyShouldExpand,
                shouldExpand);
        this.textAllCaps = a.getBoolean(R.styleable.EasySlidingTabs_easyTextAllCaps, textAllCaps);
        this.indicatorDrawable = a.getDrawable(R.styleable.EasySlidingTabs_easyIndicatorDrawable);
        this.mMyUnderlinePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                mMyUnderlinePadding, dm);
        a.recycle();

        this.rectPaint = new Paint();
        this.rectPaint.setAntiAlias(true);
        this.rectPaint.setStyle(Style.FILL);

        this.dividerPaint = new Paint();
        this.dividerPaint.setAntiAlias(true);
        this.dividerPaint.setStrokeWidth(dividerWidth);

        //defaultTabLayoutParams = new LinearLayout.LayoutParams(200, LayoutParams.MATCH_PARENT);
        this.expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT,
                1.0f);

        if (this.locale == null) {
            this.locale = getResources().getConfiguration().locale;
        }

        this.getViewTreeObserver().addOnGlobalLayoutListener(this.onGlobalLayoutListener);
    }


    /**
     * set some value when set view pager
     */
    private void setTabsValue() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        // set tab fill the screen
        this.setShouldExpand(true);
        // set tab divider color
        this.setDividerColor(Color.TRANSPARENT);
        // set tab under line height
        this.setUnderlineHeight(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.underlineHeight,
                        dm));
        // set tab indicator height
        this.setIndicatorHeight(
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.indicatorHeight,
                        dm));
        // set tab text size
        this.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, dm));
        // set tab indicator color
        this.setIndicatorColor(this.indicatorColor);
        // set tab text selected color
        this.setSelectedTextColor(this.selectedTabTextColor);
        // set tab under line color
        this.setUnderlineColor(this.underlineColor);
        // remove the background color when the tab on clicked
        this.setTabBackground(0);
    }


    /**
     * Set the view pager
     *
     * @param pager pager
     */
    public void setViewPager(ViewPager pager) {
        // TODO tmp strategy
        if (pager == null || pager.getAdapter() == null) {
            return;
        }
        this.pager = pager;
        this.selectedPosition = pager.getCurrentItem();
        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }
        pager.addOnPageChangeListener(pageListener);
        this.notifyDataSetChanged();
        this.setTabsValue();
    }


    /**
     * set the view pager change listener
     *
     * @param listener listener
     */
    public void setOnPageChangeListener(OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }


    /**
     * reset tabs
     */
    public void notifyDataSetChanged() {
        if (this.width != 0) this.addTabs();
    }


    /**
     * add tabs
     */
    private void addTabs() {
        this.isTabsAdded = true;
        this.tabsContainer.removeAllViews();
        this.tabCount = pager.getAdapter().getCount();
        for (int i = 0; i < this.tabCount; i++) {

            // If the adapter is IconTabProvider type
            if (this.pager.getAdapter() instanceof IconTabProvider) {
                this.addIconTab(i, ((IconTabProvider) this.pager.getAdapter()).getPageIconResId(i));
            } else {
                PagerAdapter adapter = this.pager.getAdapter();
                if (adapter instanceof TabsTitleInterface) {
                    TabsTitleInterface weChatTabTitleInterface = (TabsTitleInterface) adapter;
                    addTextTab(i, weChatTabTitleInterface.getTabTitle(i),
                            weChatTabTitleInterface.getTabDrawableLeft(i),
                            weChatTabTitleInterface.getTabDrawableTop(i),
                            weChatTabTitleInterface.getTabDrawableRight(i),
                            weChatTabTitleInterface.getTabDrawableBottom(i));
                } else {
                    addTextTab(i, adapter.getPageTitle(i).toString());
                }
            }
        }

        this.updateTabStyles();
    }


    /**
     * add text view type of tab
     * <p/>
     * you can set the text view attribute in here
     *
     * @param position position
     * @param title title
     * @param start start
     * @param top top
     * @param end end
     * @param bottom bottom
     */
    private void addTextTab(final int position, SpannableString title, int start, int top, int end, int bottom) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        //        You can set the text view single line
        //        tab.setSingleLine();
        tab.setCompoundDrawablesWithIntrinsicBounds(start, top, end, bottom);
        tab.setLineSpacing(8, 1.0f);
        addTab(position, tab);
    }


    /**
     * add text view type of tab
     * <p/>
     * you can set the text view attribute in here
     *
     * @param position position
     * @param title title
     */
    private void addTextTab(final int position, String title) {
        TextView tab = new TextView(getContext());
        tab.setText(title);
        tab.setGravity(Gravity.CENTER);
        //        You can set the text view single line
        //        tab.setSingleLine();
        tab.setLineSpacing(5, 1.0f);
        addTab(position, tab);
    }


    /**
     * add icon type of tab
     * <p/>
     * you can set the image button attribute in here
     *
     * @param position position
     * @param resId resId
     */
    private void addIconTab(final int position, int resId) {
        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);
        addTab(position, tab);
    }


    /**
     * add tab
     *
     * @param position position
     * @param tab tab
     */
    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override public void onClick(View v) {
                EasySlidingTabs.this.pager.setCurrentItem(position);
            }
        });

        //        You can set padding
        //        tab.setPadding(2, 0, 2, 0);

        if (this.defaultTabLayoutParams == null) {
            if (this.width == 0) this.width = getWidth();

            this.tabWidth = this.width / (this.tabCount > 5 ? 5 : this.tabCount);
            this.defaultTabLayoutParams = new LinearLayout.LayoutParams(this.tabWidth,
                    ViewGroup.LayoutParams.MATCH_PARENT);
        }
        tab.setLayoutParams(this.defaultTabLayoutParams);
        //tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
        this.tabsContainer.addView(tab, position);
    }


    /**
     * call this function if you call some setXXX() function
     */
    private void updateTabStyles() {
        PagerAdapter adapter = this.pager.getAdapter();
        if (adapter instanceof TabsTitleInterface) {
            for (int i = 0; i < this.tabCount; i++) {
                View v = this.tabsContainer.getChildAt(i);
                v.setBackgroundResource(this.tabBackgroundResId);
                if (v instanceof TextView) {
                    TextView tab = (TextView) v;
                    SpannableString spannableString = ((TabsTitleInterface) adapter).getTabTitle(i);
                    if (i == this.selectedPosition) {
                        spannableString.setSpan(new ForegroundColorSpan(this.selectedTabTextColor),
                                0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        tab.setText(spannableString);
                    } else {
                        spannableString.setSpan(new ForegroundColorSpan(this.tabTextColor), 0,
                                spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                        tab.setText(spannableString);
                    }
                }
            }
        } else {
            for (int i = 0; i < this.tabCount; i++) {
                View v = this.tabsContainer.getChildAt(i);
                v.setBackgroundResource(this.tabBackgroundResId);
                if (v instanceof TextView) {
                    TextView tab = (TextView) v;
                    tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
                    tab.setTypeface(this.tabTypeface, this.tabTypefaceStyle);
                    tab.setTextColor(this.tabTextColor);
                    // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                    // pre-ICS-build
                    if (textAllCaps) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                            tab.setAllCaps(true);
                        } else {
                            tab.setText(tab.getText().toString().toUpperCase(locale));
                        }
                    }
                    if (i == this.selectedPosition) {
                        tab.setTextColor(this.selectedTabTextColor);
                    } else {
                        tab.setTextColor(this.tabTextColor);
                    }
                }
            }
        }
    }


    @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int heght = getMeasuredHeight();
        if (width > 0) {
            this.width = width;
            if (!this.isTabsAdded && this.pager != null) {
                this.addTabs();
            }
        }
    }


    /**
     * indicator scroll
     *
     * @param position position
     * @param offset offset
     */
    private void scrollToChild(int position, int offset) {
        if (this.tabCount == 0) {
            return;
        }
        this.scrollOffset = getWidth() / 2 - this.tabWidth / 2;
        int newScrollX = this.tabsContainer.getChildAt(position).getLeft() + offset;
        if (position > 0 || offset > 0) {
            newScrollX -= this.scrollOffset;
        }
        if (newScrollX != this.lastScrollX) {
            this.lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }
    }


    /**
     * draw the sliding tabs
     *
     * @param canvas canvas
     */
    @Override protected void onDraw(Canvas canvas) {
        // OPPO device may throw nullPointerException here!!!
        try {
            super.onDraw(canvas);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (isInEditMode() || this.tabCount == 0) {
            return;
        }
        final int height = getHeight();

        // draw underline
        this.rectPaint.setColor(this.underlineColor);
        canvas.drawRect(0, height - this.underlineHeight, tabsContainer.getWidth(), height,
                rectPaint);

        // draw indicator line
        this.rectPaint.setColor(this.indicatorColor);

        // default: line below current tab
        View currentTab = this.tabsContainer.getChildAt(this.currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (this.currentPositionOffset > 0f && this.currentPosition < this.tabCount - 1) {
            View nextTab = this.tabsContainer.getChildAt(this.currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();
            lineLeft = (this.currentPositionOffset * nextTabLeft +
                    (1f - this.currentPositionOffset) * lineLeft);
            lineRight = (this.currentPositionOffset * nextTabRight +
                    (1f - this.currentPositionOffset) * lineRight);
        }
        if (this.indicatorDrawable != null) {
            int indicatorDrawableWidth = this.indicatorDrawable.getIntrinsicWidth();
            this.indicatorDrawable.setBounds(
                    (int) (lineLeft + this.tabWidth / 2 - indicatorDrawableWidth / 2),
                    height - this.indicatorDrawable.getIntrinsicHeight(),
                    (int) (lineRight - tabWidth / 2 + indicatorDrawableWidth / 2), height);
            this.indicatorDrawable.draw(canvas);
        } else {
            canvas.drawRect(lineLeft + this.mMyUnderlinePadding, height - this.indicatorHeight,
                    lineRight - this.mMyUnderlinePadding, height, this.rectPaint);
        }

        // draw divider
        this.dividerPaint.setColor(this.dividerColor);
        for (int i = 0; i < this.tabCount - 1; i++) {
            View tab = this.tabsContainer.getChildAt(i);
            canvas.drawLine(tab.getRight(), this.dividerPadding, tab.getRight(),
                    height - this.dividerPadding, this.dividerPaint);
        }
    }


    /**
     * inner page change listener
     * if you set delegatePageListener and it will call delegatePageListener
     */
    private class PageListener implements OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            EasySlidingTabs.this.currentPosition = position;
            EasySlidingTabs.this.currentPositionOffset = positionOffset;

            View view = EasySlidingTabs.this.tabsContainer.getChildAt(position);
            if (view == null) {
                return;
            }
            //scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));
            scrollToChild(position, (int) (positionOffset * view.getWidth()));

            invalidate();

            if (EasySlidingTabs.this.delegatePageListener != null) {
                EasySlidingTabs.this.delegatePageListener.onPageScrolled(position, positionOffset,
                        positionOffsetPixels);
            }
        }


        @Override public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(EasySlidingTabs.this.pager.getCurrentItem(), 0);
            }

            if (EasySlidingTabs.this.delegatePageListener != null) {
                EasySlidingTabs.this.delegatePageListener.onPageScrollStateChanged(state);
            }
        }


        @Override public void onPageSelected(int position) {
            EasySlidingTabs.this.selectedPosition = position;
            updateTabStyles();
            if (EasySlidingTabs.this.delegatePageListener != null) {
                EasySlidingTabs.this.delegatePageListener.onPageSelected(position);
            }
        }
    }


    /**
     * set indicator color
     *
     * @param indicatorColor indicatorColor
     */
    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }


    /**
     * set indicator color resource
     *
     * @param resId resId
     */
    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }


    /**
     * get indicator color
     *
     * @return color
     */
    public int getIndicatorColor() {
        return this.indicatorColor;
    }


    /**
     * set indicator height
     *
     * @param indicatorLineHeightPx indicatorLineHeightPx
     */
    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }


    /**
     * get indicator height
     *
     * @return indicatorHeight
     */
    public int getIndicatorHeight() {
        return this.indicatorHeight;
    }


    /**
     * set under line height
     *
     * @param underlineColor underlineColor
     */
    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }


    /**
     * set under line color resource
     *
     * @param resId resId
     */
    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }


    /**
     * get under line color
     *
     * @return color
     */
    public int getUnderlineColor() {
        return this.underlineColor;
    }


    /**
     * set divider color
     *
     * @param dividerColor dividerColor
     */
    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }


    /**
     * set divider color resource
     *
     * @param resId resId
     */
    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }


    /**
     * get divider color
     *
     * @return dividerColor
     */
    public int getDividerColor() {
        return this.dividerColor;
    }


    /**
     * set under line height
     *
     * @param underlineHeightPx underlineHeightPx
     */
    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }


    /**
     * get under line height
     *
     * @return underlineHeight
     */
    public int getUnderlineHeight() {
        return this.underlineHeight;
    }


    /**
     * set divider padding
     *
     * @param dividerPaddingPx dividerPaddingPx
     */
    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }


    /**
     * get divider padding
     *
     * @return dividerPadding
     */
    public int getDividerPadding() {
        return this.dividerPadding;
    }


    /**
     * set scroll offset
     *
     * @param scrollOffsetPx scrollOffsetPx
     */
    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }


    /**
     * get scroll offset
     *
     * @return scrollOffset
     */
    public int getScrollOffset() {
        return this.scrollOffset;
    }


    /**
     * set expand status
     */
    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        this.notifyDataSetChanged();
    }


    /**
     * get espand status
     *
     * @return shouldExpand
     */
    public boolean getShouldExpand() {
        return shouldExpand;
    }


    /**
     * get text all caps status
     *
     * @return isTextAllCaps
     */
    public boolean isTextAllCaps() {
        return textAllCaps;
    }


    /**
     * set text all caps status
     *
     * @param textAllCaps textAllCaps
     */
    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }


    /**
     * set text size
     *
     * @param textSizePx textSizePx
     */
    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        this.updateTabStyles();
    }


    /**
     * get under line padding
     */
    public void setUnderlinePadding0() {
        mMyUnderlinePadding = 0;
    }


    /**
     * get text size
     *
     * @return tabTextSize
     */
    public int getTextSize() {
        return tabTextSize;
    }


    /**
     * set text color
     *
     * @param textColor textColor
     */
    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        this.updateTabStyles();
    }


    /**
     * set text color resource
     *
     * @param resId resId
     */
    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        this.updateTabStyles();
    }


    /**
     * get text color
     *
     * @return tabTextColor
     */
    public int getTextColor() {
        return this.tabTextColor;
    }


    /**
     * set text selected color
     *
     * @param textColor textColor
     */
    public void setSelectedTextColor(int textColor) {
        this.selectedTabTextColor = textColor;
        this.updateTabStyles();
    }


    /**
     * set text selected color resource
     *
     * @param resId resId
     */
    public void setSelectedTextColorResource(int resId) {
        this.selectedTabTextColor = getResources().getColor(resId);
        this.updateTabStyles();
    }


    /**
     * get text selected color
     *
     * @return selectedTabTextColor
     */
    public int getSelectedTextColor() {
        return this.selectedTabTextColor;
    }


    /**
     * set typeface
     *
     * @param typeface typeface
     * @param style style
     */
    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        this.updateTabStyles();
    }


    /**
     * set tab background
     *
     * @param resId resId
     */
    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
        this.updateTabStyles();
    }


    /**
     * get tab background
     *
     * @return tabBackgroundResId
     */
    public int getTabBackground() {
        return this.tabBackgroundResId;
    }


    /**
     * set tab padding left and padding right
     *
     * @param paddingPx paddingPx
     */
    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        this.updateTabStyles();
    }


    /**
     * get tab padding left and padding right
     *
     * @return tabPadding
     */
    public int getTabPaddingLeftRight() {
        return this.tabPadding;
    }


    @Override public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        this.currentPosition = savedState.currentPosition;
        requestLayout();
    }


    @Override public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = this.currentPosition;
        return savedState;
    }


    /**
     * in order to save position status
     */
    static class SavedState extends BaseSavedState {
        int currentPosition;


        public SavedState(Parcelable superState) {
            super(superState);
        }


        private SavedState(Parcel in) {
            super(in);
            this.currentPosition = in.readInt();
        }


        @Override public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(this.currentPosition);
        }


        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }


            @Override public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }

    /**
     * expand interface
     */
    public interface TabsTitleInterface {
        SpannableString getTabTitle(int position);

        int getTabDrawableBottom(int position);

        int getTabDrawableLeft(int position);

        int getTabDrawableRight(int position);

        int getTabDrawableTop(int position);
    }
}
