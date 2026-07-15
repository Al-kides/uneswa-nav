package com.uneswa.nav.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.net.wifi.WifiManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
// Copied and adapted from AutoCOnnect. See ICT Society UNESWA github
class WifiHelper(private val context: Context) {
    private val wifiMgr = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    fun isWifiOn(): Boolean = wifiMgr.isWifiEnabled

    fun getCurrentSsid(): String? {
        val info = wifiMgr.connectionInfo
        return info.ssid?.removeSurrounding("\"")
    }

    fun isStudentsWifi(): Boolean {
        val ssid = getCurrentSsid() ?: return false
        return ssid.equals("uniswawifi-students", ignoreCase = true)//netreg doesn't work wwithout wifi. and ictc.
        // will add later
    }

    suspend fun registerDevice(studentId: String, birthday: String): Pair<Boolean, String> = withContext(Dispatchers.IO) {
        val password = if (birthday.startsWith("Uneswa")) birthday else "Uneswa$birthday"
        val urls = listOf(
            "http://kwnetreg.uniswa.sz/cgi-bin/register.cgi",
            "http://netreg.uniswa.sz/cgi-bin/register.cgi" //compatibility in case I extend to Luyengo and Mbaps.
        )

        for (urlString in urls) {
            try {
                val url = URL(urlString)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.connectTimeout = 10000
                conn.readTimeout = 10000
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")

                val postData = "user=${URLEncoder.encode(studentId, "UTF-8")}" +
                        "&pass=${URLEncoder.encode(password, "UTF-8")}" +
                        "&submit=ACCEPT"

                conn.outputStream.use { it.write(postData.toByteArray()) }

                val responseCode = conn.responseCode
                if (responseCode in 200..302) {
                    val body = conn.inputStream.bufferedReader().use { it.readText() }.lowercase()
                    
                    if (body.contains("not required") || body.contains("not on a network that requires registration")) {
                        return@withContext true to "Already registered or not required."
                    }
                    if (body.contains("already registered") || body.contains("hardware already registered")) {
                        return@withContext true to "Device is already registered."
                    }
                    if (body.contains("success") || body.contains("registered")) {
                        return@withContext true to "Success! Please restart your device."
                    }
                    return@withContext true to "Registration submitted. Restart if no connection."
                }
            } catch (e: Exception) {
                // Try next URL
                // todo(!)
            }
        }
        return@withContext false to "Failed to reach registration portal. Check your WiFi."
    }

    fun openPsiphonStore() {
        val pkg = "com.psiphon3.subscription"
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$pkg"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$pkg"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }
}
