package com.dsquare.wallzee.Utils;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

public class DarkModeUtils {

    // Check if the device is in night mode (dark mode)
    public static boolean isDarkModeEnabled(Context context) {
        int currentNightMode = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;

        return currentNightMode == Configuration.UI_MODE_NIGHT_YES;
    }

    // For devices using Android 10 (API level 29) and above, you can use UiModeManager
    public static boolean isDarkModeEnabledWithUiModeManager(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            UiModeManager uiModeManager = (UiModeManager) context.getSystemService(Context.UI_MODE_SERVICE);
            return uiModeManager != null && uiModeManager.getNightMode() == UiModeManager.MODE_NIGHT_YES;
        } else {
            // Handle devices running versions earlier than Android 10
            return isDarkModeEnabled(context);
        }
    }
}

