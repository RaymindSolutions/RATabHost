package com.raymind.ratabhost.tabs;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.widget.TextView;

import com.raymind.ratabhost.R;

/**
 * Created by Ashish Das on 27/02/17.
 */
public class RATextTab extends RATab
{
	public static final int DEFAULT_TAB_TEXT_COLOR = Color.WHITE;

	private TextView mTextView;

	private ColorStateList mTextColor;

	private float mTextSize;

	private int mTextSizeUnit;

	public RATextTab(final Context context)
	{
		this(context, null);
	}

	public RATextTab(final Context context, final OnTabTouchListener touchListener)
	{
		super(context, touchListener, R.layout.layout_tab_text);

		mTextView = (TextView) getView().findViewById(R.id.tv_tab_text);
		setTextView_TextColor(getTextColor());
	}

	@ColorInt
	public int getTextColor()
	{
		return mTextColor.getDefaultColor();
	}

	public RATextTab setTextColor(@ColorInt int color)
	{
		return setTextColor(ColorStateList.valueOf(color));
	}

	public RATextTab setTextColor(ColorStateList color)
	{
		if (mTextColor != null && mTextColor.equals(color))
		{
			return getTextTab();
		}

		mTextColor = (color != null) ? color : ColorStateList.valueOf(getAccentColor());

		setTextView_TextColor(getTextColor());

		return getTextTab();
	}

	public RATextTab setTextSize(int unit, float size)
	{
		if (mTextSizeUnit == unit && mTextSize == size)
		{
			return getTextTab();
		}

		mTextSizeUnit = unit;
		mTextSize = size;

		setTextView_TextSize(mTextSizeUnit, mTextSize);

		return getTextTab();
	}

	public RATextTab setText(CharSequence text)
	{
		if (mTextView != null)
		{
			mTextView.setText(text.toString());
		}
		return getTextTab();
	}

	public CharSequence getText()
	{
		if (mTextView != null)
		{
			return mTextView.getText();
		}
		return "";
	}

	private RATextTab getTextTab()
	{
		return this;
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

	@Override
	protected RATab getTab()
	{
		return getTextTab();
	}

	@Override
	protected void unselectTab()
	{
		int color = getTextColor();
		setTextView_TextColor(Color.argb(getDisableStateColorAlpha(), Color.red(color), Color.green(color), Color.blue(color)));
	}

	@Override
	protected void selectTab()
	{
		int color = getTextColor();
		setTextView_TextColor(color);
	}

	@Override
	protected float getTabMinWidth()
	{
		return getTextLenght();
	}

	@Override
	protected void setTabAccentColor(final ColorStateList color)
	{
		setTextColor(color);
	}
}
