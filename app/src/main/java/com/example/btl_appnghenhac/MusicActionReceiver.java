package com.example.btl_appnghenhac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MusicActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getIntExtra("actionMusic", 0);
        Intent serviceIntent = new Intent(context, MusicService.class);
        serviceIntent.putExtra("actionMusicService", action);
        context.startService(serviceIntent);
    }
}
