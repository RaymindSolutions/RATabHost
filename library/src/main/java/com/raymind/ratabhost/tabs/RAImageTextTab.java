package com.raymind.ratabhost.tabs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
	private Drawable mDrawable;
	private ColorStateList mImageColorFilter;
	private ImageView mImageView;
	private ColorStateList mTextColor;
	private float mTextSize;
	private int mTextSizeUnit;
	private TextView mTextView;
	private float tabImageViewWidth;

	public RAImageTextTab(Context context)
	{
		this(context, null);
	}

	public RAImageTextTab(Context context, OnTabTouchListener touchListener)
	{
		super(context, touchListener, R.layout.layout_tab_image_text);

		mImageView = (ImageView) getView().findViewById(R.id.iv_tab_icon);
		setImageColorFilter(getAccentColor());

		mTextView = (TextView) getView().findViewById(R.id.tv_tab_text);
		setTextColor(getAccentColor());

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
		if (color == null)
		{
			color = ColorStateList.valueOf(getAccentColor());
		}
		mImageColorFilter = color;
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
		if (mImageView != null)
		{
			mImageView.setColorFilter(color);
		}
	}

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
		if (color == null)
		{
			color = ColorStateList.valueOf(getAccentColor());
		}
		mTextColor = color;
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
		mTextView.getPaint().getTextBounds(textString, 0, textString.length(), bounds);
		return bounds.width();
	}

	private RAImageTextTab getImageTextTab()
	{
		return this;
	}

	protected RATab getTab()
	{
		return getImageTextTab();
	}

	protected void unselectTab()
	{
		setImageAlpha(getDisableStateColorAlpha());
		int color = getTextColor();
		setTextView_TextColor(Color.argb(getDisableStateColorAlpha(), Color.red(color), Color.green(color), Color.blue(color)));
	}

	protected void selectTab()
	{
		setImageAlpha(0xFF);
		setTextView_TextColor(getTextColor());
	}

	protected float getTabMinWidth()
	{
		float minImageWidth = tabImageViewWidth;
		float minTextWidth = (float) getTextLenght();
		return minImageWidth > minTextWidth ? minImageWidth : minTextWidth;
	}
}