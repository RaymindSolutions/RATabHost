package com.raymind.ratabhost.tabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.widget.ImageView;
import android.widget.TextView;

import com.raymind.ratabhost.R;

/**
 * Created by Ashish Das on 27/02/17.
 */
public class RAImageTextTab extends RATab
{
	private ImageView mImageView;

	private TextView mTextView;

	private ColorStateList mTextColor;

	private ColorStateList mImageColorFilter;

	private float mTextSize;

	private int mTextSizeUnit;

	private Drawable mDrawable;

	private float tabImageViewWidth;

	public RAImageTextTab(final Context context, final OnTabTouchListener touchListener)
	{
		super(context, touchListener, R.layout.layout_tab_image);

		mImageView = (ImageView) getView().findViewById(R.id.iv_tab_icon);
		setImageView_ColorFilter(getImageColorFilter());

		mTextView = (TextView) getView().findViewById(R.id.tv_tab_text);
		setTextView_TextColor(getTextColor());

		tabImageViewWidth = context.getResources().getDimension(R.dimen.tab_image_view_width);
	}

	@ColorInt
	public int getImageColorFilter()
	{
		return mImageColorFilter.getDefaultColor();
	}

	public RAImageTextTab setImageColorFilter(@ColorInt int color)
	{
		return setImageColorFilter(ColorStateList.valueOf(color));
	}

	public RAImageTextTab setImageColorFilter(ColorStateList color)
	{
		if (mImageColorFilter != null && mImageColorFilter.equals(color))
		{
			return getImageTextTab();
		}

		mImageColorFilter = (color != null) ? color : ColorStateList.valueOf(getAccentColor());

		setImageView_ColorFilter(getImageColorFilter());

		return getImageTextTab();
	}


	public RAImageTextTab setImageDrawable(Drawable drawable)
	{
		mDrawable = drawable;

		mImageView.setImageDrawable(mDrawable);

		setImageView_ColorFilter(getImageColorFilter());

		return getImageTextTab();
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

	@ColorInt
	public int getTextColor()
	{
		return mTextColor.getDefaultColor();
	}

	public RAImageTextTab setTextColor(@ColorInt int color)
	{
		return setTextColor(ColorStateList.valueOf(color));
	}

	public RAImageTextTab setTextColor(ColorStateList color)
	{
		if (mTextColor != null && mTextColor.equals(color))
		{
			return getImageTextTab();
		}

		mTextColor = (color != null) ? color : ColorStateList.valueOf(getAccentColor());

		setTextView_TextColor(getTextColor());

		return getImageTextTab();
	}

	public RAImageTextTab setTextSize(int unit, float size)
	{
		if (mTextSizeUnit == unit && mTextSize == size)
		{
			return getImageTextTab();
		}

		mTextSizeUnit = unit;
		mTextSize = size;

		setTextView_TextSize(mTextSizeUnit, mTextSize);

		return getImageTextTab();
	}

	public RAImageTextTab setText(CharSequence text)
	{
		if (mTextView != null)
		{
			mTextView.setText(text.toString());
		}
		return getImageTextTab();
	}

	public CharSequence getText()
	{
		if (mTextView != null)
		{
			return mTextView.getText();
		}
		return "";
	}

	private void setTextView_TextColor(@ColorInt int color)
	{
		if (mTextView != null)
		{
			mTextView.setTextColor(color);
		}
	}

	private void setTextView_TextSize(int unit, float size)
	{
		if (mTextView != null)
		{
			mTextView.setTextSize(unit, size);
		}
	}

	private int getTextLenght()
	{
		String textString = mTextView.getText().toString();
		Rect bounds = new Rect();
		Paint textPaint = mTextView.getPaint();
		textPaint.getTextBounds(textString, 0, textString.length(), bounds);
		return bounds.width();
	}

	private RAImageTextTab getImageTextTab()
	{
		return this;
	}

	@Override
	protected RATab getTab()
	{
		return getImageTextTab();
	}

	@Override
	protected void unselectTab()
	{
		setImageAlpha(getDisableStateColorAlpha());
		int color = getTextColor();
		setTextView_TextColor(Color.argb(getDisableStateColorAlpha(), Color.red(color), Color.green(color), Color.blue(color)));
	}

	@Override
	protected void selectTab()
	{
		setImageAlpha(0xFF);
		int color = getTextColor();
		setTextView_TextColor(color);
	}

	@Override
	protected float getTabMinWidth()
	{
		final float minImageWidth = tabImageViewWidth;
		final float minTextWidth = getTextLenght();
		return minImageWidth > minTextWidth ? minImageWidth : minTextWidth;
	}

	@Override
	protected void setTabAccentColor(final ColorStateList color)
	{
		setImageColorFilter(color);
		setTextColor(color);
	}
}
