package com.raymind.ratabhost.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.widget.ImageView;

import com.raymind.ratabhost.R;

/**
 * Created by Ashish Das on 27/02/17.
 */
public class RAImageTab extends RATab
{
	public static final int DEFAULT_TAB_IMAGE_COLOR_FILTER = Color.WHITE;

	private ImageView mImageView;

	private ColorStateList mImageColorFilter;

	private Drawable mDrawable;

	private float tabImageViewWidth;

	public RAImageTab(final Context context, final OnTabTouchListener touchListener)
	{
		super(context, touchListener, R.layout.layout_tab_image);

		mImageView = (ImageView) getView().findViewById(R.id.iv_tab_icon);
		setImageView_ColorFilter(getImageColorFilter());

		tabImageViewWidth = (int) context.getResources().getDimension(R.dimen.tab_image_view_width);
	}

	@ColorInt
	public int getImageColorFilter()
	{
		return mImageColorFilter.getDefaultColor();
	}

	public RAImageTab setImageColorFilter(@ColorInt int color)
	{
		return setImageColorFilter(ColorStateList.valueOf(color));
	}

	public RAImageTab setImageColorFilter(ColorStateList color)
	{
		if (mImageColorFilter != null && mImageColorFilter.equals(color))
		{
			return getImageTab();
		}

		mImageColorFilter = (color != null) ? color : ColorStateList.valueOf(getAccentColor());

		setImageView_ColorFilter(getImageColorFilter());

		return getImageTab();
	}


	public RAImageTab setImageDrawable(Drawable drawable)
	{
		mDrawable = drawable;

		mImageView.setImageDrawable(mDrawable);

		setImageView_ColorFilter(getImageColorFilter());

		return getImageTab();
	}

	private RAImageTab getImageTab()
	{
		return this;
	}

	private void setImageView_ColorFilter(@ColorInt int color)
	{
		if (this.mImageView != null)
		{
			this.mImageView.setColorFilter(color);
		}
	}

	@SuppressLint({"NewApi"})
	private void setImageAlpha(int alpha)
	{
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
		{
			mImageView.setImageAlpha(alpha);
			return;
		}

		int color = getImageColorFilter();
		setImageView_ColorFilter(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
	}

	@Override
	protected RATab getTab()
	{
		return getImageTab();
	}

	@Override
	protected void unselectTab()
	{
		setImageAlpha(getDisableStateColorAlpha());
	}

	@Override
	protected void selectTab()
	{
		setImageAlpha(0xFF);
	}

	@Override
	protected float getTabMinWidth()
	{
		return tabImageViewWidth;
	}

	@Override
	protected void setTabAccentColor(final ColorStateList color)
	{
		setImageColorFilter(color);
	}
}
