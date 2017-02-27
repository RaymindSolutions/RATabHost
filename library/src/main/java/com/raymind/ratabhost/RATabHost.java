package com.raymind.ratabhost;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.TypedValue;
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
		void onTabSelected(RATab tab);
	}

	private static int sSelectedTab;
	private static int sSpaceBetweenTabs;
	private static int sNavigationViewWidth;
	private static int sScrollviewLeftRightPadding;
	private static boolean sIsTabletLayouts;

	private Context mContext;
	private HorizontalScrollView mScrollTabView;
	private LinearLayout mTabHolder;
	private ImageButton mNavigationNextBtn;
	private ImageButton mNavigationPreviusBtn;

	private boolean mScrollable;
	private List<RATab> mTabList;
	private OnTabSelectedListener mTabSelectedListener;

	private boolean isTab = true;

	private int mTabDisableStateColorAlpha;
	private ColorStateList mTabAccentColor;
	private ColorStateList mTabPrimaryColor;
	private ColorStateList mTabTextColor;
	private ColorStateList mTabImageColorFilter;
	private int mTabTextSizeUnit;
	private float mTabTextSize;

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

	private void init(final Context context, final AttributeSet attrs, final int defStyleAttr)
	{
		mContext = context;
		LayoutInflater.from(mContext).inflate(R.layout.layout_tab_host, this);

		mScrollTabView = (HorizontalScrollView) findViewById(R.id.hsv_tabhost_scrollview);
		mTabHolder = (LinearLayout) findViewById(R.id.ll_tabhost_tabholder);

		mNavigationNextBtn = (ImageButton) findViewById(R.id.ib_tabhost_navigation_next);
		mNavigationPreviusBtn = (ImageButton) findViewById(R.id.ib_tabhost_navigation_previous);

		mNavigationNextBtn.setOnClickListener(mOnNavigationButtonClickListener);
		mNavigationPreviusBtn.setOnClickListener(mOnNavigationButtonClickListener);

		mTabList = new LinkedList<>();

		Resources resources = getResources();

		sIsTabletLayouts = !(resources.getBoolean(R.bool.isPhoneLayout));
		if (sIsTabletLayouts)
		{
			sNavigationViewWidth = round(resources.getDimension(R.dimen.tabhost_navigation_previous_next_view_width));
		}
		sSpaceBetweenTabs = round(resources.getDimension(R.dimen.tabhost_space_between_tabs)) * 2;
		sScrollviewLeftRightPadding = round(resources.getDimension(R.dimen.tabhost_scrollview_left_right_padding));
		sSelectedTab = -1;

		int tabTextSizeInPixel = 0;
		if (attrs != null)
		{
			TypedArray a = mContext.getTheme().obtainStyledAttributes(attrs, R.styleable.RATabHost, 0, 0);

			try
			{
				setTabDisableStateColorAlpha(a.getInteger(R.styleable.RATabHost_raTabDisableStateColorAlpha, RATab.DEFAULT_DISABLE_TAB_ALPHA));
				setTabPrimaryColor(a.getColor(R.styleable.RATabHost_raTabPrimaryColor, RATab.DEFAULT_TAB_PRIMARY_COLOR));
				setTabAccentColor(a.getColor(R.styleable.RATabHost_raTabAccentColor, RATab.DEFAULT_TAB_ACCENT_COLOR));
				setTabTextColor(a.getColor(R.styleable.RATabHost_raTabTextColor, RATextTab.DEFAULT_TAB_TEXT_COLOR));
				setTabImageColorFilter(a.getColor(R.styleable.RATabHost_raTabImageColorFilter, RAImageTab.DEFAULT_TAB_IMAGE_COLOR_FILTER));

				tabTextSizeInPixel = a.getDimensionPixelSize(R.styleable.RATabHost_raTabTextSize, 0);
			}
			finally
			{
				a.recycle();
			}
		}

		if (tabTextSizeInPixel > 0)
		{
			setTabTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSizeInPixel);
		}
		setScrollable(false);
	}

	public RATextTab newTab(String text)
	{
		RATextTab tab = new RATextTab(mContext);
		tab.setAccentColor(getTabAccentColor());
		tab.setPrimaryColor(getTabPrimaryColor());
		tab.setDisableStateColorAlpha(getTabDisableStateColorAlpha());
		tab.setText(text);
		tab.setTextColor(getTabTextColor());
		if (mTabTextSize > 0)
		{
			tab.setTextSize(mTabTextSizeUnit, mTabTextSize);
		}
		return tab;
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
		if (mTabAccentColor != null && mTabAccentColor.equals(color))
		{
			return this;
		}

		mTabAccentColor = (color != null) ? color : ColorStateList.valueOf(RATab.DEFAULT_TAB_ACCENT_COLOR);

		for (RATab tab : mTabList)
		{
			tab.setAccentColor(getTabAccentColor());
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
		if (mTabPrimaryColor != null && mTabPrimaryColor.equals(color))
		{
			return this;
		}

		mTabPrimaryColor = (color != null) ? color : ColorStateList.valueOf(RATab.DEFAULT_TAB_PRIMARY_COLOR);

		for (RATab tab : mTabList)
		{
			tab.setPrimaryColor(getTabPrimaryColor());
		}

		return this;
	}

	@ColorInt
	public int getTabTextColor()
	{
		return mTabTextColor.getDefaultColor();
	}

	public RATabHost setTabTextColor(@ColorInt int color)
	{
		return setTabTextColor(ColorStateList.valueOf(color));
	}

	public RATabHost setTabTextColor(ColorStateList color)
	{
		if (mTabTextColor != null && mTabTextColor.equals(color))
		{
			return this;
		}

		mTabTextColor = (color != null) ? color : ColorStateList.valueOf(getTabAccentColor());

		for (RATab tab : mTabList)
		{
			if (tab instanceof RATextTab)
			{
				RATextTab textTab = (RATextTab) tab;
				textTab.setTextColor(getTabTextColor());
			}
			else if (tab instanceof RAImageTextTab)
			{
				RAImageTextTab imageTextTab = (RAImageTextTab) tab;
				imageTextTab.setTextColor(getTabTextColor());
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
		if (mTabImageColorFilter != null && mTabImageColorFilter.equals(color))
		{
			return this;
		}

		mTabImageColorFilter = (color != null) ? color : ColorStateList.valueOf(getTabAccentColor());

		for (RATab tab : mTabList)
		{
			if (tab instanceof RAImageTab)
			{
				RAImageTab imageTab = (RAImageTab) tab;
				imageTab.setImageColorFilter(getTabImageColorFilter());
			}
			else if (tab instanceof RAImageTextTab)
			{
				RAImageTextTab imageTextTab = (RAImageTextTab) tab;
				imageTextTab.setImageColorFilter(getTabImageColorFilter());
			}
		}

		return this;
	}

	public RATabHost setTabTextSize(int unit, float size)
	{
		if (mTabTextSizeUnit == unit && mTabTextSize == size)
		{
			return this;
		}

		mTabTextSizeUnit = unit;
		mTabTextSize = size;

		for (RATab tab : mTabList)
		{
			if (tab instanceof RATextTab)
			{
				RATextTab textTab = (RATextTab) tab;
				textTab.setTextSize(mTabTextSizeUnit, mTabTextSize);
			}
			else if (tab instanceof RAImageTextTab)
			{
				RAImageTextTab imageTextTab = (RAImageTextTab) tab;
				imageTextTab.setTextSize(mTabTextSizeUnit, mTabTextSize);
			}
		}

		return this;
	}

	public void removeAllTabs()
	{
		mTabList.clear();
		notifyDataSetChanged();
	}

	public void setOnTabSelectedListener(final OnTabSelectedListener tabSelectedListener)
	{
		mTabSelectedListener = tabSelectedListener;
	}

	public boolean isScrollable()
	{
		return mScrollable;
	}

	public RATabHost setScrollable(final boolean scrollable)
	{
		if (this.mScrollable != scrollable)
		{
			this.mScrollable = scrollable;

			if (isScrollable() && sIsTabletLayouts && !isTab)
			{
				mNavigationPreviusBtn.setVisibility(VISIBLE);
				mNavigationNextBtn.setVisibility(VISIBLE);

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
				{
					mNavigationPreviusBtn.setBackground(this.getBackground());
					mNavigationNextBtn.setBackground(this.getBackground());
				}
				else
				{
					mNavigationPreviusBtn.setBackgroundColor(this.getDrawingCacheBackgroundColor());
					mNavigationNextBtn.setBackgroundColor(this.getDrawingCacheBackgroundColor());
				}
			}
			else
			{
				mNavigationPreviusBtn.setVisibility(GONE);
				mNavigationNextBtn.setVisibility(GONE);
			}

			setScrollViewPadding();
		}
		return this;
	}

	public RATabHost addTab(RATab tab)
	{
		tab.setPosition(mTabList.size());
		tab.addTabTouchListener(mOnTabTouchListener);
		mTabList.add(tab);

		if (mTabList.size() == 4)
		{
			setScrollable(true);
		}

		notifyDataSetChanged();
		return this;
	}

	public void setSelectTab(int tabIndex)
	{
		if (tabIndex >= 0 && tabIndex < mTabList.size())
		{
			for (RATab tab : mTabList)
			{
				if (tab.getPosition() == tabIndex)
				{
					if (isTab)
					{
						tab.select();
					}
					else
					{
						tab.unselect();
					}
					if (sSelectedTab != tabIndex)
					{
						notifyTabSelection(tab);

					}
				}
				else
				{
					tab.unselect();
				}
			}

			if (isScrollable())
			{
				scrollTo(tabIndex);
			}
		}

		sSelectedTab = tabIndex;
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

			if (isScrollable())
			{
				for (RATab tab : mTabList)
				{
					int tabWidth = getTabMinWidth(tab);
					mTabHolder.addView(tab.getView(), new LinearLayout.LayoutParams(tabWidth, LayoutParams.MATCH_PARENT));
				}
			}
			else
			{
				LinearLayout.LayoutParams params;
				int tabWidth = this.getWidth() / mTabList.size();
				params = new LinearLayout.LayoutParams(tabWidth, LayoutParams.MATCH_PARENT);
				for (RATab tab : mTabList)
				{
					mTabHolder.addView(tab.getView(), params);
				}
			}
		}

		setSelectTab(sSelectedTab);
	}

	@Override
	public void removeAllViews()
	{
		if (mTabHolder != null)
		{
			mTabHolder.removeAllViews();
		}
	}

	private int getTabMinWidth(RATab tab)
	{
		return round(tab.getMinWidth() + sSpaceBetweenTabs);
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
			else
			{
				int width = tab.getView().getWidth();

				if (width == 0)
				{
					width = getTabMinWidth(tab);
				}

				totalWidth += width;
			}
		}

		mScrollTabView.smoothScrollTo(totalWidth, 0);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		if (this.getWidth() != 0 && mTabList.size() != 0)
		{
			notifyDataSetChanged();
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		super.onLayout(changed, l, t, r, b);
		// Ensure first scroll
		if (changed)
		{
			scrollTo(sSelectedTab);
		}
	}

	private void setScrollViewPadding()
	{
		if (mScrollTabView != null)
		{
			int leftRightPadding = sScrollviewLeftRightPadding;

			if (isScrollable() && sIsTabletLayouts)
			{
				leftRightPadding = leftRightPadding + sNavigationViewWidth;
			}

			mScrollTabView.setPadding(leftRightPadding, 0, leftRightPadding, 0);
			mScrollTabView.setClipToPadding(false);
		}
	}

	private void notifyTabSelection(RATab tab)
	{
		if (mTabSelectedListener != null)
		{
			mTabSelectedListener.onTabSelected(tab);
		}
	}

	private OnClickListener mOnNavigationButtonClickListener = new OnClickListener()
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
						position++;
						setSelectTab(position);
					}
				}
				else if (view.getId() == R.id.ib_tabhost_navigation_previous)
				{
					if (position > 0)
					{
						position--;
						setSelectTab(position);
					}
				}
			}
		}
	};

	private RATab.OnTabTouchListener mOnTabTouchListener = new RATab.OnTabTouchListener()
	{
		@Override
		public void onTouch(final RATab tab, final Point touchedPoint)
		{
			if (tab.isSelected())
			{
				notifyTabSelection(tab);
			}
			else
			{
				setSelectTab(tab.getPosition());
			}
		}
	};
}
