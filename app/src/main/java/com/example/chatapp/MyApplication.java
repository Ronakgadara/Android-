package com.example.chatapp;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.onesignal.OneSignal;

public class MyApplication extends Application {

    private static final String ONESIGNAL_APP_ID = "YOUR_KEY";

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize OneSignal
        OneSignal.initWithContext(this, ONESIGNAL_APP_ID);

        // Login with external user ID (current Firebase UID)
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (uid != null) {
            OneSignal.login(uid);
        }
    }
}