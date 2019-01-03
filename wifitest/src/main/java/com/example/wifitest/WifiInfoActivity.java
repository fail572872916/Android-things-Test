package com.example.wifitest;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class WifiInfoActivity extends Activity {
	private String TAG = "WifiInfoActivity";
	private TextView mTVWifiInfo;
	private WifiManager mWifiManager;
	private WifiInfo mWifiInfo;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wifi_connected_info);
		mTVWifiInfo =(TextView) findViewById(R.id.wifi_info);
		mWifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        if(null != mWifiInfo && null != mWifiInfo.getSSID()){
        	String info = "getSSID()="+mWifiInfo.getSSID()+"\n"
        				+"getBSSID()="+mWifiInfo.getBSSID()+"\n"
        				+"getHiddenSSID()="+mWifiInfo.getHiddenSSID()+"\n"
        				+"getLinkSpeed()="+mWifiInfo.getLinkSpeed()+"\n"
        				+"getMacAddress()="+mWifiInfo.getMacAddress()+"\n"
        				+"getNetworkId()="+mWifiInfo.getNetworkId()+"\n"
        				+"getRssi()="+mWifiInfo.getRssi()+"\n"
        				+"getSupplicantState()="+mWifiInfo.getSupplicantState()+"\n"
        				+"getDetailedStateOf()="+mWifiInfo.getDetailedStateOf(mWifiInfo.getSupplicantState());
        	mTVWifiInfo.setText(info);
        }else {
        	mTVWifiInfo.setText("没有连接到wifi");
        }
        Log.i(TAG,"iswifiEnable = "+mWifiManager.isWifiEnabled());
	}
}
