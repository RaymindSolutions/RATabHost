package com.raymind.ratabhost.tabs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.ColorInt;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.raymind.ratabhost.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Ashish Das on 27/02/17.
 */
public abstract class RATab
{
	public interface OnTabTouchListener
	{
		void onTouch(RATab tab, Point touchedPoint);
	}

	public static final int DEFAULT_DISABLE_TAB_ALPHA = 0x99;
	public static final int DEFAULT_TAB_ACCENT_COLOR = Color.WHITE;
	public static final int DEFAULT_TAB_PRIMARY_COLOR = Color.parseColor("#3F51B5");

	private int mPosition;
	private boolean mSelected;
	private boolean mEnableSelector;

	private ColorStateList mAccentColor;
	private ColorStateList mPrimaryColor;
	private ColorStateList mSelectorColor;

	private int mDisableStateColorAlpha;

	private View mRootView;
	private View mSelectorBottomLineView;
	private ArrayList<WeakReference<OnTabTouchListener>> mTouchListeners;

	public RATab(Context context, OnTabTouchListener touchListener, @LayoutRes int tabLayoutResource)
	{
		mTouchListeners = new ArrayList();
		mRootView = LayoutInflater.from(context).inflate(tabLayoutResource, null);
		mSelectorBottomLineView = mRootView.findViewById(R.id.v_selector);
		mRootView.setOnTouchListener(mOnTouchListener);
		setEnableSelector(true);

		setDisableStateColorAlpha(DEFAULT_DISABLE_TAB_ALPHA);

		setAccentColor(DEFAULT_TAB_ACCENT_COLOR);
		setPrimaryColor(DEFAULT_TAB_PRIMARY_COLOR);
		setSelectorColor(DEFAULT_TAB_ACCENT_COLOR);

		addTabTouchListener(touchListener);

		mSelected = false;
	}

	public void addTabTouchListener(OnTabTouchListener tabTouchListener)
	{
		if (tabTouchListener != null)
		{
			mTouchListeners.add(new WeakReference(tabTouchListener));
		}
	}

	private boolean isEnableSelector()
	{
		return mEnableSelector;
	}

	public void setEnableSelector(boolean enableSelector)
	{
		mEnableSelector = enableSelector;
		if (mSelectorBottomLineView != null)
		{
			mSelectorBottomLineView.setVisibility(isEnableSelector() ? View.VISIBLE : View.GONE);
		}
	}

	public int getDisableStateColorAlpha()
	{
		return mDisableStateColorAlpha;
	}

	public void setDisableStateColorAlpha(int alpha)
	{
		mDisableStateColorAlpha = alpha;
	}

	@ColorInt
	public int getAccentColor()
	{
		return mAccentColor.getDefaultColor();
	}

	public void setAccentColor(@ColorInt int color)
	{
		setAccentColor(ColorStateList.valueOf(color));
	}

	public void setAccentColor(ColorStateList color)
	{
		if (mAccentColor == null || !mAccentColor.equals(color))
		{
			if (color == null)
			{
				color = ColorStateList.valueOf(DEFAULT_TAB_ACCENT_COLOR);
			}
			mAccentColor = color;
		}
	}

	@ColorInt
	public int getPrimaryColor()
	{
		return mPrimaryColor.getDefaultColor();
	}

	public void setPrimaryColor(@ColorInt int color)
	{
		setPrimaryColor(ColorStateList.valueOf(color));
	}

	public void setPrimaryColor(ColorStateList color)
	{
		if (mPrimaryColor == null || !mPrimaryColor.equals(color))
		{
			if (color == null)
			{
				color = ColorStateList.valueOf(DEFAULT_TAB_PRIMARY_COLOR);
			}
			mPrimaryColor = color;
			if (mRootView != null)
			{
				mRootView.setBackgroundColor(getPrimaryColor());
			}
		}
	}

	@ColorInt
	public int getSelectorColor()
	{
		return mSelectorColor.getDefaultColor();
	}

	public void setSelectorColor(@ColorInt int color)
	{
		setSelectorColor(ColorStateList.valueOf(color));
	}

	public void setSelectorColor(ColorStateList color)
	{
		if (mSelectorColor == null || !mSelectorColor.equals(color))
		{
			if (color == null)
			{
				color = ColorStateList.valueOf(getAccentColor());
			}
			mSelectorColor = color;
			if (isSelected())
			{
				setSelectorBottomLineColor(getSelectorColor());
			}
		}
	}

	public void unselect()
	{
		unselectTab();
		setSelectorBottomLineColor(Color.TRANSPARENT);
		mSelected = false;
	}

	public void select()
	{
		selectTab();
		setSelectorBottomLineColor(getSelectorColor());
		mSelected = true;
	}

	public boolean isSelected()
	{
		return mSelected;
	}

	public View getView()
	{
		return mRootView;
	}

	public float getMinWidth()
	{
		return getTabMinWidth();
	}

	public int getPosition()
	{
		return mPosition;
	}

	public void setPosition(int position)
	{
		mPosition = position;
	}

	private void setSelectorBottomLineColor(@ColorInt int color)
	{
		if (mSelectorBottomLineView != null && mSelectorBottomLineView.getVisibility() == View.VISIBLE)
		{
			mSelectorBottomLineView.setBackgroundColor(color);
		}
	}

	private View.OnTouchListener mOnTouchListener = new View.OnTouchListener()
	{
		@Override
		public boolean onTouch(final View v, final MotionEvent event)
		{
			final Point lastTouchedPoint = new Point();
			lastTouchedPoint.x = (int) event.getX();
			lastTouchedPoint.y = (int) event.getY();

			if (event.getAction() == MotionEvent.ACTION_DOWN)
			{
				// do nothing
				return true;
			}

			if (event.getAction() == MotionEvent.ACTION_CANCEL)
			{
				return true;
			}
			// new effects
			if (event.getAction() == MotionEvent.ACTION_UP)
			{
				for (WeakReference<OnTabTouchListener> tabTouchListenerWeakReference : mTouchListeners)
				{
					OnTabTouchListener tabTouchListener = tabTouchListenerWeakReference.get();
					if (tabTouchListener != null)
					{
						tabTouchListener.onTouch(getTab(), lastTouchedPoint);
					}
				}

				return true;
			}

			return false;
		}
	};


	protected abstract RATab getTab();

	protected abstract float getTabMinWidth();

	protected abstract void selectTab();

	protected abstract void unselectTab();
}
