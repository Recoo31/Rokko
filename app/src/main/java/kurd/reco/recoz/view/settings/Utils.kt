package kurd.reco.recoz.view.settings

import android.content.Context
import android.content.Intent
import android.content.pm.ResolveInfo
import android.net.Uri

fun getVideoPlayers(context: Context): List<ResolveInfo> {
    val intent = Intent(Intent.ACTION_VIEW).apply {
        setDataAndType(Uri.parse("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"), "video/*")
    }
    return context.packageManager.queryIntentActivities(intent, 0)
}

