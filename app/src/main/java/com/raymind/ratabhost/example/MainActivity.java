package com.raymind.ratabhost.example;

import android.animation.Animator;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.raymind.ratabhost.RATabHost;
import com.raymind.ratabhost.tabs.RATab;

import at.markushi.ui.RevealColorView;

public class MainActivity extends AppCompatActivity
{
	private static final String TAG = "MainActivity";
	private RATabHost mTabHost;
	private RevealColorView mRevealColorView;

	@Override
	protected void onCreate(@Nullable final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		mRevealColorView = (RevealColorView) findViewById(R.id.reveal);

		int[] colors = getResources().getIntArray(R.array.colors);

		mTabHost = (RATabHost) findViewById(R.id.ra_tabhost);
		mTabHost.setOnTabSelectedListener(mTabSelectedListener);
		mTabHost.setSelectTab(0);
		int length = colors.length;
		for (int i = 0; i < length; i++)
		{
			RATab tab = mTabHost.newTab("Tab - " + i);
			tab.setAccentColor(colors[i]);
			mTabHost.addTab(tab);
		}

		RATab selectedTab = mTabHost.getCurrentSelectedTab();
		if (selectedTab != null)
		{
			mRevealColorView.setBackgroundColor(selectedTab.getAccentColor());
		}
	}

	private RATabHost.OnTabSelectedListener mTabSelectedListener = new RATabHost.OnTabSelectedListener()
	{
		@Override
		public void onTabReselected(final RATab tab)
		{
			Log.d(TAG, "onTabReselected : " + tab.getPosition());
			Point touchedPoint = getLocationInView(mRevealColorView, tab.getView());
			mRevealColorView
					.hide(touchedPoint.x, touchedPoint.y, Color.argb(99, Color.red(tab.getAccentColor()), Color.green(tab.getAccentColor()), Color.blue(tab.getAccentColor())), 0,
					      500, new MyAnimatorListener(tab, touchedPoint));
		}

		@Override
		public void onTabSelected(final RATab tab)
		{
			Log.d(TAG, "onTabSelected : " + tab.getPosition());
			Point touchedPoint = getLocationInView(mRevealColorView, tab.getView());
			mRevealColorView.reveal(touchedPoint.x, touchedPoint.y, tab.getAccentColor(), tab.getView().getHeight() / 2, 1000, null);
		}

		@Override
		public void onTabUnselected(final RATab tab)
		{
			Log.d(TAG, "onTabUnselected : " + tab.getPosition());
		}
	};


	private class MyAnimatorListener implements Animator.AnimatorListener
	{
		private RATab mTab;
		private Point mTouchedPoint;

		public MyAnimatorListener(final RATab tab, final Point touchedPoint)
		{
			mTab = tab;
			mTouchedPoint = touchedPoint;
		}

		public void onAnimationStart(Animator animator)
		{
		}

		public void onAnimationEnd(Animator animator)
		{
			mRevealColorView.reveal(mTouchedPoint.x, mTouchedPoint.y, mTab.getAccentColor(), mTab.getView().getHeight() / 2, 500, null);
		}

		public void onAnimationCancel(Animator animator)
		{
		}

		public void onAnimationRepeat(Animator animator)
		{
		}
	}

	private Point getLocationInView(View src, View target)
	{
		Point point = new Point();

		int[] srcLocations = new int[2];
		int[] targetLocations = new int[2];

		src.getLocationOnScreen(srcLocations);
		target.getLocationOnScreen(targetLocations);

		point.x = targetLocations[0] - srcLocations[0] + target.getWidth() / 2;
		point.y = targetLocations[1] - srcLocations[1] + target.getHeight() / 2;

		return point;
	}
}
