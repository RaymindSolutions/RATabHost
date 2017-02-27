package com.raymind.ratabhost.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.raymind.ratabhost.RATabHost;
import com.raymind.ratabhost.tabs.RATab;
import com.raymind.ratabhost.tabs.RATextTab;

public class MainActivity extends AppCompatActivity
{
	private RATabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mTabHost = (RATabHost) findViewById(R.id.ra_tabhost);
		mTabHost.setOnTabSelectedListener(mTabSelectedListener);
		mTabHost.setSelectTab(4);
		for (int i = 0; i < 10; i++)
		{
			RATab tab = mTabHost.newTab("Tab - " + i);
			mTabHost.addTab(tab);
		}
	}

	private RATabHost.OnTabSelectedListener mTabSelectedListener = new RATabHost.OnTabSelectedListener()
	{
		@Override
		public void onTabSelected(final RATab tab)
		{
			Toast toast = Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT);
			if (tab instanceof RATextTab)
			{
				RATextTab textTab = (RATextTab) tab;
				toast.setText(textTab.getText());
			}
			else
			{
				toast.setText("onTabSelected - " + tab.getPosition());
			}
			toast.show();
		}
	};
}
