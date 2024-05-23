package com.anggastudio.sample.WebApiSVEN.Models;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiSpeedChecker {

    public static int getWifiSpeed(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return wifiInfo.getLinkSpeed(); // En Mbps (megabits por segundo)
    }

    public static boolean isWifiFast(Context context, int thresholdSpeed) {
        int currentSpeed = getWifiSpeed(context);
        return currentSpeed >= thresholdSpeed;
    }

}
