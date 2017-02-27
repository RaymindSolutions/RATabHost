package com.raymind.ratabhost;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.raymind.ratabhost.tabs.RAImageTab;
import com.raymind.ratabhost.tabs.RAImageTextTab;
import com.raymind.ratabhost.tabs.RATab;
import com.raymind.ratabhost.tabs.RATextTab;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ashish Das on 27/02/17.
 */
public class RATabHost extends FrameLayout
{
	public interface OnTabSelectedListener
	{
		void onTabReselected(RATab tab);

		void onTabSelected(RATab tab);

		void onTabUnselected(RATab tab);
	}

	private static boolean sIsTabletLayouts;
	private static int sNavigationViewWidth;
	private static int sScrollviewLeftRightPadding;
	private static int sSelectedTabPosition;
	private static int sSpaceBetweenTabs;

	private Context mContext;
	private List<RATab> mTabList;

	private boolean mScrollable;
	private boolean mAutoEnableScrolling;
	private boolean mEnableScrolling;
	private boolean mEnableTabSelector;
	private boolean mEnabledTabletTabNavigation;

	private float mTabTextSize;
	private int mTabTextSizeUnit;
	private int mTabDisableStateColorAlpha;

	private LinearLayout mTabHolder;
	private HorizontalScrollView mScrollTabView;
	private ImageButton mNavigationNextBtn;
	private ImageButton mNavigationPreviusBtn;

	private ColorStateList mTabSelectorColor;
	private ColorStateList mTabPrimaryColor;
	private ColorStateList mTabAccentColor;
	private ColorStateList mTabTextColor;
	private ColorStateList mTabImageColorFilter;

	private OnTabSelectedListener mTabSelectedListener;

	public RATabHost(final Context context)
	{
		this(context, null);
	}

	public RATabHost(final Context context, final AttributeSet attrs)
	{
		this(context, attrs, 0);
	}

	public RATabHost(final Context context, final AttributeSet attrs, final int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		init(context, attrs, defStyleAttr);
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	public RATabHost(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
		init(context, attrs, defStyleAttr);
	}

	private void init(Context context, AttributeSet attrs, int defStyleAttr)
	{
		mContext = context;

		initView(mContext);

		mNavigationNextBtn.setOnClickListener(mOnNavigationButtonClickListener);
		mNavigationPreviusBtn.setOnClickListener(mOnNavigationButtonClickListener);

		mTabList = new LinkedList();

		Resources resources = getResources();

		sIsTabletLayouts = true;
		if (resources.getBoolean(R.bool.isPhoneLayout))
		{
			sIsTabletLayouts = false;
		}

		if (sIsTabletLayouts)
		{
			sNavigationViewWidth = round(resources.getDimension(R.dimen.tabhost_navigation_previous_next_view_width));
		}

		sSpaceBetweenTabs = round(resources.getDimension(R.dimen.tabhost_space_between_tabs)) * 2;
		sScrollviewLeftRightPadding = round(resources.getDimension(R.dimen.tabhost_scrollview_left_right_padding));
		sSelectedTabPosition = -1;

		setStyledAttributes(mContext, attrs);

		setScrollable(isEnableScrolling());
	}

	public RAImageTab newTab(Drawable drawable)
	{
		RAImageTab tab = new RAImageTab(mContext);
		tab.setAccentColor(getTabAccentColor());
		tab.setPrimaryColor(getTabPrimaryColor());
		tab.setDisableStateColorAlpha(getTabDisableStateColorAlpha());
		tab.setImageDrawable(drawable);
		tab.setImageColorFilter(getTabImageColorFilter());
		return tab;
	}

	public RATextTab newTab(String text)
	{
		RATextTab tab = new RATextTab(mContext);
		tab.setAccentColor(getTabAccentColor());
		tab.setPrimaryColor(getTabPrimaryColor());
		tab.setDisableStateColorAlpha(getTabDisableStateColorAlpha());
		tab.setText(text);
		tab.setTextColor(getTabTextColor());
		if (mTabTextSize > 0.0f)
		{
			tab.setTextSize(mTabTextSizeUnit, mTabTextSize);
		}
		return tab;
	}

	public RAImageTextTab newTab(String text, Drawable drawable)
	{
		RAImageTextTab tab = new RAImageTextTab(mContext);
		tab.setAccentColor(getTabAccentColor());
		tab.setPrimaryColor(getTabPrimaryColor());
		tab.setDisableStateColorAlpha(getTabDisableStateColorAlpha());
		tab.setImageDrawable(drawable);
		tab.setImageColorFilter(getTabImageColorFilter());
		tab.setText(text);
		tab.setTextColor(getTabTextColor());
		if (mTabTextSize > 0.0f)
		{
			tab.setTextSize(mTabTextSizeUnit, mTabTextSize);
		}
		return tab;
	}

	public boolean isAutoEnableScrolling()
	{
		return mAutoEnableScrolling;
	}

	public void setAutoEnableScrolling(boolean autoEnableScrolling)
	{
		mAutoEnableScrolling = autoEnableScrolling;
		if (mTabList.size() > 0)
		{
			refreshView();
		}
	}

	public boolean isEnableScrolling()
	{
		return mEnableScrolling;
	}

	public void setEnableScrolling(boolean enableScrolling)
	{
		mEnableScrolling = enableScrolling;
		if (mTabList.size() > 0)
		{
			refreshView();
		}
	}

	private boolean isEnabledTabletTabNavigation()
	{
		return mEnabledTabletTabNavigation;
	}

	public void setEnabledTabletTabNavigation(boolean enabledTabletTabNavigation)
	{
		mEnabledTabletTabNavigation = enabledTabletTabNavigation;
	}

	private boolean isEnableTabSelector()
	{
		return mEnableTabSelector;
	}

	public RATabHost setEnableTabSelector(boolean disableTabSelector)
	{
		mEnableTabSelector = disableTabSelector;
		for (RATab tab : mTabList)
		{
			tab.setEnableSelector(isEnableTabSelector());
		}
		return this;
	}

	private int getTabDisableStateColorAlpha()
	{
		return mTabDisableStateColorAlpha;
	}

	public RATabHost setTabDisableStateColorAlpha(int alpha)
	{
		mTabDisableStateColorAlpha = alpha;
		for (RATab tab : mTabList)
		{
			tab.setDisableStateColorAlpha(getTabDisableStateColorAlpha());
		}
		return this;
	}

	@ColorInt
	private int getTabAccentColor()
	{
		return mTabAccentColor.getDefaultColor();
	}

	public RATabHost setTabAccentColor(@ColorInt int color)
	{
		return setTabAccentColor(ColorStateList.valueOf(color));
	}

	public RATabHost setTabAccentColor(ColorStateList color)
	{
		if (mTabAccentColor == null || !mTabAccentColor.equals(color))
		{
			mTabAccentColor = (color != null) ? color : ColorStateList.valueOf(RATab.DEFAULT_TAB_ACCENT_COLOR);
			for (RATab tab : mTabList)
			{
				tab.setAccentColor(getTabAccentColor());
			}
		}
		return this;
	}

	@ColorInt
	private int getTabPrimaryColor()
	{
		return mTabPrimaryColor.getDefaultColor();
	}

	public RATabHost setTabPrimaryColor(@ColorInt int color)
	{
		return setTabPrimaryColor(ColorStateList.valueOf(color));
	}

	public RATabHost setTabPrimaryColor(ColorStateList color)
	{
		if (mTabPrimaryColor == null || !mTabPrimaryColor.equals(color))
		{
			mTabPrimaryColor = (color != null) ? color : ColorStateList.valueOf(RATab.DEFAULT_TAB_PRIMARY_COLOR);
			for (RATab tab : mTabList)
			{
				tab.setPrimaryColor(getTabPrimaryColor());
			}
		}
		return this;
	}

	@ColorInt
	private int getTabSelectorColor()
	{
		return mTabSelectorColor.getDefaultColor();
	}

	public RATabHost setTabSelectorColor(@ColorInt int color)
	{
		return setTabSelectorColor(ColorStateList.valueOf(color));
	}

	public RATabHost setTabSelectorColor(ColorStateList color)
	{
		if (mTabSelectorColor == null || !mTabSelectorColor.equals(color))
		{
			mTabSelectorColor = (color != null) ? color : ColorStateList.valueOf(getTabAccentColor());
			for (RATab tab : mTabList)
			{
				tab.setSelectorColor(getTabSelectorColor());
			}
		}
		return this;
	}

	@ColorInt
	private int getTabTextColor()
	{
		return mTabTextColor.getDefaultColor();
	}

	public RATabHost setTabTextColor(@ColorInt int color)
	{
		return setTabTextColor(ColorStateList.valueOf(color));
	}

	public RATabHost setTabTextColor(ColorStateList color)
	{
		if (mTabTextColor == null || !mTabTextColor.equals(color))
		{
			mTabTextColor = (color != null) ? color : ColorStateList.valueOf(getTabAccentColor());
			for (RATab tab : mTabList)
			{
				if (tab instanceof RATextTab)
				{
					((RATextTab) tab).setTextColor(getTabTextColor());
				}
				else if (tab instanceof RAImageTextTab)
				{
					((RAImageTextTab) tab).setTextColor(getTabTextColor());
				}
			}
		}
		return this;
	}

	@ColorInt
	public int getTabImageColorFilter()
	{
		return mTabImageColorFilter.getDefaultColor();
	}

	public RATabHost setTabImageColorFilter(@ColorInt int color)
	{
		return setImageColorFilter(ColorStateList.valueOf(color));
	}

	public RATabHost setImageColorFilter(ColorStateList color)
	{
		if (mTabImageColorFilter == null || !mTabImageColorFilter.equals(color))
		{
			mTabImageColorFilter = (color != null) ? color : ColorStateList.valueOf(getTabAccentColor());;
			for (RATab tab : mTabList)
			{
				if (tab instanceof RAImageTab)
				{
					((RAImageTab) tab).setImageColorFilter(getTabImageColorFilter());
				}
				else if (tab instanceof RAImageTextTab)
				{
					((RAImageTextTab) tab).setImageColorFilter(getTabImageColorFilter());
				}
			}
		}
		return this;
	}

	public RATabHost setTabTextSize(int unit, float size)
	{
		if (!(mTabTextSizeUnit == unit && mTabTextSize == size))
		{
			mTabTextSizeUnit = unit;
			mTabTextSize = size;
			for (RATab tab : mTabList)
			{
				if (tab instanceof RATextTab)
				{
					((RATextTab) tab).setTextSize(mTabTextSizeUnit, mTabTextSize);
				}
				else if (tab instanceof RAImageTextTab)
				{
					((RAImageTextTab) tab).setTextSize(mTabTextSizeUnit, mTabTextSize);
				}
			}
		}
		return this;
	}

	public void removeAllTabs()
	{
		mTabList.clear();
		mTabHolder.removeAllViews();
	}

	public void setOnTabSelectedListener(OnTabSelectedListener tabSelectedListener)
	{
		mTabSelectedListener = tabSelectedListener;
	}

	public RATabHost addTab(RATab tab)
	{
		tab.setPosition(mTabList.size());
		if (sSelectedTabPosition == tab.getPosition())
		{
			tab.select();
		}
		tab.addTabTouchListener(mOnTabTouchListener);
		mTabList.add(tab);
		return this;
	}

	public void setSelectTab(int tabPosition)
	{
		setSelectTab(tabPosition, true);
	}

	public RATab getCurrentSelectedTab()
	{
		for (RATab tab : mTabList)
		{
			if (tab.isSelected())
			{
				return tab;
			}
		}
		return null;
	}

	public void notifyDataSetChanged()
	{
		if (mTabHolder != null)
		{
			mTabHolder.removeAllViews();
			if (isAutoEnableScrolling())
			{
				int width = getViewWidth();
				setScrollable(canScroll(width));
				if (isScrollable() || width < 0)
				{
					addTabs();
				}
				else
				{
					addTabs(width);
				}
			}
			else
			{
				addTabs();
				mScrollTabView.setSmoothScrollingEnabled(isEnableScrolling());
			}
		}
		setSelectTab(sSelectedTabPosition, false);
	}

	private void initView(Context context)
	{
		LayoutInflater.from(context).inflate(R.layout.layout_tab_host, this);

		mScrollTabView = (HorizontalScrollView) findViewById(R.id.hsv_tabhost_scrollview);
		mTabHolder = (LinearLayout) findViewById(R.id.ll_tabhost_tabholder);
		mNavigationNextBtn = (ImageButton) findViewById(R.id.ib_tabhost_navigation_next);
		mNavigationPreviusBtn = (ImageButton) findViewById(R.id.ib_tabhost_navigation_previous);
	}

	private void setStyledAttributes(Context context, AttributeSet attrs)
	{
		boolean isEnabledScrollBar = false;
		int tabTextSizeInPixel = 0;

		if (attrs != null)
		{
			TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RATabHost, 0, 0);
			try
			{
				setAutoEnableScrolling(a.getBoolean(R.styleable.RATabHost_raAutoEnableScrolling, true));
				setEnableScrolling(a.getBoolean(R.styleable.RATabHost_raEnableScrolling, false));
				setEnabledTabletTabNavigation(a.getBoolean(R.styleable.RATabHost_raEnableTabletTabNavigation, true));
				setEnableTabSelector(a.getBoolean(R.styleable.RATabHost_raEnableTabSelector, true));
				isEnabledScrollBar = a.getBoolean(R.styleable.RATabHost_raEnableScrollBar, false);
				setTabDisableStateColorAlpha(a.getInteger(R.styleable.RATabHost_raTabDisableStateColorAlpha, RATab.DEFAULT_DISABLE_TAB_ALPHA));
				setTabAccentColor(a.getColor(R.styleable.RATabHost_raTabAccentColor, -1));
				setTabPrimaryColor(a.getColor(R.styleable.RATabHost_raTabPrimaryColor, RATab.DEFAULT_TAB_PRIMARY_COLOR));
				setTabSelectorColor(a.getColor(R.styleable.RATabHost_raTabSelectorColor, -1));
				setTabTextColor(a.getColor(R.styleable.RATabHost_raTabTextColor, -1));
				setTabImageColorFilter(a.getColor(R.styleable.RATabHost_raTabImageColorFilter, -1));
				tabTextSizeInPixel = a.getDimensionPixelSize(R.styleable.RATabHost_raTabTextSize, 0);
			}
			finally
			{
				a.recycle();
			}
		}
		if (tabTextSizeInPixel > 0)
		{
			setTabTextSize(0, (float) tabTextSizeInPixel);
		}

		mScrollTabView.setHorizontalScrollBarEnabled(isEnabledScrollBar);
		mScrollable = !isEnableScrolling();
	}

	private void addTabs()
	{
		for (RATab tab : mTabList)
		{
			mTabHolder.addView(tab.getView(), new LayoutParams(getTabMinWidth(tab), -1));
		}
	}

	private void addTabs(int width)
	{
		LayoutParams params = new LayoutParams(width / mTabList.size(), -1);
		for (RATab tab : mTabList)
		{
			mTabHolder.addView(tab.getView(), params);
		}
	}

	public void removeAllViews()
	{
		if (mTabHolder != null)
		{
			mTabHolder.removeAllViews();
		}
	}

	private void refreshView()
	{
		new Handler().post(new Runnable()
		{
			@Override
			public void run()
			{
				notifyDataSetChanged();
			}
		});
	}

	private int getViewWidth()
	{
		int originalWidth = MeasureSpec.getSize(getMeasuredWidth());
		if (originalWidth > 0)
		{
			return originalWidth - (getPaddingLeftRight() * 2);
		}
		return -1;
	}

	private boolean isScrollable()
	{
		return mScrollable;
	}

	private void setScrollable(boolean scrollable)
	{
		if (mScrollable != scrollable)
		{
			mScrollable = scrollable;
			if (isScrollable() && sIsTabletLayouts && isEnabledTabletTabNavigation())
			{
				mNavigationNextBtn.setVisibility(VISIBLE);
				mNavigationPreviusBtn.setVisibility(VISIBLE);
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				{
					mNavigationNextBtn.setBackground(getBackground());
					mNavigationPreviusBtn.setBackground(getBackground());
				}
				else
				{
					mNavigationNextBtn.setBackgroundDrawable(getBackground());
					mNavigationPreviusBtn.setBackgroundDrawable(getBackground());
				}
			}
			else
			{
				mNavigationNextBtn.setVisibility(GONE);
				mNavigationPreviusBtn.setVisibility(GONE);
			}
			setScrollViewPadding();
		}
	}

	private boolean canScroll(int viewWidth)
	{
		if (viewWidth <= 0)
		{
			return false;
		}
		int totalWidth = 0;
		for (RATab tab : mTabList)
		{
			int width = tab.getView().getWidth();
			if (width == 0)
			{
				width = getTabMinWidth(tab);
			}
			totalWidth += width;
		}
		if (totalWidth <= 0 || viewWidth >= totalWidth)
		{
			return false;
		}
		return true;
	}

	private void setSelectTab(int tabPosition, boolean enableNotify)
	{
		int lastSelectedTabPosition = sSelectedTabPosition;
		sSelectedTabPosition = tabPosition;
		if (enableNotify)
		{
			RATab currentSelectedTab = getCurrentSelectedTab();
			if (!(currentSelectedTab == null || currentSelectedTab.getPosition() == tabPosition))
			{
				currentSelectedTab.unselect();
				notifyTabUnselection(currentSelectedTab);
			}
		}
		boolean isValidTabIndex = false;
		if (tabPosition >= 0 && tabPosition < mTabList.size())
		{
			isValidTabIndex = true;
		}
		for (RATab tab : mTabList)
		{
			if (isValidTabIndex && tab.getPosition() == tabPosition)
			{
				boolean isReselectTab = tab.isSelected();
				if (!isReselectTab)
				{
					tab.select();
				}
				if (enableNotify)
				{
					if (isReselectTab)
					{
						notifyTabReselection(tab);
					}
					else if (lastSelectedTabPosition != tabPosition)
					{
						notifyTabSelection(tab);
					}
				}
			}
			else
			{
				tab.unselect();
			}
		}
		if (isValidTabIndex && isScrollable())
		{
			scrollTo(tabPosition);
		}
	}

	private int getTabMinWidth(RATab tab)
	{
		return round(tab.getMinWidth() + ((float) sSpaceBetweenTabs));
	}

	private static int round(float value)
	{
		return Math.round(value);
	}

	private void scrollTo(int position)
	{
		int totalWidth = 0;
		for (RATab tab : mTabList)
		{
			if (tab.getPosition() >= position)
			{
				break;
			}
			int width = tab.getView().getWidth();
			if (width == 0)
			{
				width = getTabMinWidth(tab);
			}
			totalWidth += width;
		}
		mScrollTabView.smoothScrollTo(totalWidth, 0);
	}

	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		if (getWidth() != 0 && mTabList.size() != 0)
		{
			refreshView();
		}
	}

	private void setScrollViewPadding()
	{
		if (mScrollTabView != null)
		{
			int leftRightPadding = getPaddingLeftRight();
			mScrollTabView.setPadding(leftRightPadding, 0, leftRightPadding, 0);
			mScrollTabView.setClipToPadding(false);
		}
	}

	private int getPaddingLeftRight()
	{
		int leftRightPadding = sScrollviewLeftRightPadding;
		if (isScrollable() && sIsTabletLayouts)
		{
			return leftRightPadding + sNavigationViewWidth;
		}
		return leftRightPadding;
	}

	private void notifyTabSelection(RATab tab)
	{
		if (mTabSelectedListener != null)
		{
			mTabSelectedListener.onTabSelected(tab);
		}
	}

	private void notifyTabUnselection(RATab tab)
	{
		if (mTabSelectedListener != null)
		{
			mTabSelectedListener.onTabUnselected(tab);
		}
	}

	private void notifyTabReselection(RATab tab)
	{
		if (mTabSelectedListener != null)
		{
			mTabSelectedListener.onTabReselected(tab);
		}
	}

	private RATab.OnTabTouchListener mOnTabTouchListener = new RATab.OnTabTouchListener()
	{
		@Override
		public void onTouch(final RATab tab, final Point touchedPoint)
		{
			if (tab.isSelected())
			{
				RATabHost.this.notifyTabReselection(tab);
			}
			else
			{
				RATabHost.this.setSelectTab(tab.getPosition());
			}
		}
	};

	private OnClickListener mOnNavigationButtonClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick(final View view)
		{
			RATab tab = getCurrentSelectedTab();
			if (tab != null)
			{
				int position = tab.getPosition();
				if (view.getId() == R.id.ib_tabhost_navigation_next)
				{
					if (position < mTabList.size() - 1)
					{
						setSelectTab(position + 1);
					}
				}
				else if (view.getId() == R.id.ib_tabhost_navigation_previous && position > 0)
				{
					setSelectTab(position - 1);
				}
			}
		}
	};
}
