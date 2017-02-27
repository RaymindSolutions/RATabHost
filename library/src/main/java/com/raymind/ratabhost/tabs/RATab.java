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

	// 60% alpha
	public static final int DEFAULT_DISABLE_TAB_ALPHA = 0x99;
	public static final int DEFAULT_TAB_ACCENT_COLOR = Color.WHITE;
	public static final int DEFAULT_TAB_PRIMARY_COLOR = Color.parseColor("#3F51B5");

	private View mRootView;
	private View mSelectorBottomLineView;

	private int mPosition;
	private boolean mSelected;

	private int mDisableStateColorAlpha;

	private ColorStateList mAccentColor;
	private ColorStateList mPrimaryColor;

	private ArrayList<WeakReference<OnTabTouchListener>> mTouchListeners;

	public RATab(Context context, OnTabTouchListener touchListener, @LayoutRes int tabLayoutResource)
	{
		mTouchListeners = new ArrayList<WeakReference<OnTabTouchListener>>();

		mRootView = LayoutInflater.from(context).inflate(tabLayoutResource, null);
		mSelectorBottomLineView = mRootView.findViewById(R.id.v_selector);

		mRootView.setOnTouchListener(mOnTouchListener);

		setDisableStateColorAlpha(RATab.DEFAULT_DISABLE_TAB_ALPHA);
		setAccentColor(RATab.DEFAULT_TAB_ACCENT_COLOR);
		setPrimaryColor(RATab.DEFAULT_TAB_PRIMARY_COLOR);

		addTabTouchListener(touchListener);

		mSelected = false;
	}

	public void addTabTouchListener(final OnTabTouchListener tabTouchListener)
	{
		if (tabTouchListener != null)
		{
			mTouchListeners.add(new WeakReference<OnTabTouchListener>(tabTouchListener));
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
		if (mAccentColor != null && mAccentColor.equals(color))
		{
			return;
		}

		mAccentColor = (color != null) ? color : ColorStateList.valueOf(RATab.DEFAULT_TAB_ACCENT_COLOR);

		setTabAccentColor(mAccentColor);
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
		if (mPrimaryColor != null && mPrimaryColor.equals(color))
		{
			return;
		}

		mPrimaryColor = (color != null) ? color : ColorStateList.valueOf(RATab.DEFAULT_TAB_PRIMARY_COLOR);

		if (mRootView != null)
		{
			mRootView.setBackgroundColor(getPrimaryColor());
		}
	}

	public void unselect()
	{
		unselectTab();
		setSelectorColor(Color.TRANSPARENT);
		mSelected = false;
	}

	public void select()
	{
		selectTab();
		setSelectorColor(getAccentColor());
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
		this.mPosition = position;
	}

	private void setSelectorColor(@ColorInt int color)
	{
		if (mSelectorBottomLineView != null)
		{
			mSelectorBottomLineView.setBackgroundColor(color);
		}
	}

	private View.OnTouchListener mOnTouchListener = new View.OnTouchListener()
	{
		@Override
		public boolean onTouch(View v, MotionEvent event)
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

	protected abstract void unselectTab();

	protected abstract void selectTab();

	protected abstract float getTabMinWidth();

	protected abstract void setTabAccentColor(ColorStateList color);

}
