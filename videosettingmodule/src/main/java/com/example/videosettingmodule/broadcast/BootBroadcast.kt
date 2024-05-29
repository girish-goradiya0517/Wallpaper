package moe.cyunrei.videolivewallpaper.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.example.videosettingmodule.service.VideoLiveWallpaperService

internal class BootBroadCast : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(context, "this is BroadCast", Toast.LENGTH_SHORT).show()
        val service = Intent(context, VideoLiveWallpaperService::class.java)
        context.startService(service)
    }
}