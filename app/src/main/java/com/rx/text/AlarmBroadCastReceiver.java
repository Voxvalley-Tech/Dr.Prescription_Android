package com.rx.text;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

import androidx.legacy.content.WakefulBroadcastReceiver;

public class AlarmBroadCastReceiver extends WakefulBroadcastReceiver
{
    Vibrator v;
    private PowerManager.WakeLock screenWakeLock;
    @SuppressLint("InvalidWakeLockTag")
    @Override
    public void onReceive(Context context, Intent intent)
    {

        Log.e("onReceive", "ladskjflsakjdflskjdflskjdfslkjdflasdf");

        if (screenWakeLock == null)
        {
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            screenWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "ScreenLock tag from AlarmListener");
            screenWakeLock.acquire();
        }
//TODO:Do your code here related to alarm receiver.
        if (screenWakeLock != null) {
            screenWakeLock.release();
            v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(5000);
        }
    }
}