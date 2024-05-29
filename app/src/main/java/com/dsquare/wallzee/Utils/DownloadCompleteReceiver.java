package com.dsquare.wallzee.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, VideoLiveWallpaperService.class);
        context.startActivity(service);
    }
}
