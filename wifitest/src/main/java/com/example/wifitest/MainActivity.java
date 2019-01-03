package com.example.wifitest;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	
	private Button mBtWifiScan,mBtWifiAp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mBtWifiScan = (Button) findViewById(R.id.bt_scan_wifi);
		mBtWifiAp = (Button) findViewById(R.id.bt_start_ap);
		mBtWifiScan.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View arg0) {
				Intent in = new Intent(MainActivity.this, WifiListActivity.class);
				startActivity(in);				
			}
		});

		mBtWifiAp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent in = new Intent(MainActivity.this, WifiApActivity.class);
				startActivity(in);				
			}
		});
	}
}
