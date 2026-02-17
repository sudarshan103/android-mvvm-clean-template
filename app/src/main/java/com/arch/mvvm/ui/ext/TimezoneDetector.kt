package com.arch.mvvm.ui.ext

import android.content.Context
import android.telephony.TelephonyManager
import java.util.Locale
import java.util.TimeZone

/**
 * Utility class for detecting device timezone automatically
 * Tries multiple methods to get accurate timezone information
 */
object TimezoneDetector {
    /**
     * Detect device timezone using multiple fallback methods
     * Priority:
     * 1. TelephonyManager (network-based timezone)
     * 2. System default timezone
     * 3. Fallback to UTC if detection fails
     *
     * @param context Android application context
     * @return Timezone ID string (e.g., "Asia/Kolkata", "UTC")
     */
    fun detectDeviceTimezone(context: Context): String {
        return try {
            // Try to get timezone from TelephonyManager (network operator timezone)
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
            if (telephonyManager != null) {
                val networkCountryIso = telephonyManager.networkCountryIso
                if (networkCountryIso.isNotEmpty()) {
                    val timezoneFromNetwork = getTimezoneFromCountryCode(networkCountryIso)
                    if (timezoneFromNetwork != null) {
                        return timezoneFromNetwork
                    }
                }
            }

            // Fallback to system default timezone
            TimeZone.getDefault().id
        } catch (_: Exception) {
            // Ultimate fallback
            TimeZone.getDefault().id
        }
    }

    /**
     * Map ISO country code to timezone ID
     * Note: This is a simplified mapping. For production, consider using a more comprehensive library
     *
     * @param countryCode ISO country code (e.g., "IN", "US")
     * @return Timezone ID or null if not found
     */
    private fun getTimezoneFromCountryCode(countryCode: String): String? =
        when (countryCode.uppercase(Locale.getDefault())) {
            "IN" -> "Asia/Kolkata"
            "US" -> "America/New_York" // Default US timezone
            "GB" -> "Europe/London"
            "AU" -> "Australia/Sydney"
            "JP" -> "Asia/Tokyo"
            "DE" -> "Europe/Berlin"
            "FR" -> "Europe/Paris"
            "CA" -> "America/Toronto"
            "BR" -> "America/Sao_Paulo"
            "SG" -> "Asia/Singapore"
            "HK" -> "Asia/Hong_Kong"
            "CN" -> "Asia/Shanghai"
            "RU" -> "Europe/Moscow"
            "ZA" -> "Africa/Johannesburg"
            "MX" -> "America/Mexico_City"
            "NZ" -> "Pacific/Auckland"
            else -> null
        }

    /**
     * Get the offset of given timezone from UTC in a human-readable format
     * @param timezoneId Timezone ID (e.g., "Asia/Kolkata")
     * @return Offset string (e.g., "+05:30", "-08:00")
     */
    fun getTimezoneOffset(timezoneId: String): String =
        try {
            val timezone = TimeZone.getTimeZone(timezoneId)
            val offsetMs = timezone.getOffset(System.currentTimeMillis())
            val offsetHours = offsetMs / (1000 * 60 * 60)
            val offsetMinutes = (kotlin.math.abs(offsetMs) / (1000 * 60)) % 60

            val sign = if (offsetMs >= 0) "+" else "-"
            String.format(Locale.getDefault(), "%s%02d:%02d", sign, kotlin.math.abs(offsetHours), offsetMinutes)
        } catch (_: Exception) {
            "N/A"
        }
}
