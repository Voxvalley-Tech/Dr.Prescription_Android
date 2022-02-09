package com.rx.text;

import android.app.IntentService;
import android.content.Intent;

public class WakefulService extends IntentService {
    public WakefulService() {
        super("WakefulService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        //TODO:Service code
        AlarmBroadCastReceiver.completeWakefulIntent(intent);
    }
}
