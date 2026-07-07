package com.uneswa.nav.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.net.wifi.WifiManager

class WifiHelper(private val context: Context) {
    private val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun isWifiConnected(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }

    fun getCurrentSsid(): String? {
        val info = wifiManager.connectionInfo
        return info.ssid?.removeSurrounding("\"")
    }

    fun isCampusWifi(): Boolean {
        val ssid = getCurrentSsid() ?: return false
        return ssid.contains("UNESWA", ignoreCase = true) || ssid.contains("eduroam", ignoreCase = true)
    }

    fun openProxifierOnPlayStore() {
        val packageName = "com.proxifier"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
