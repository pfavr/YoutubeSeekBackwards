package org.headsetrewind;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

final class Status {
    static void report(Context context, String message, boolean error) {
        Preferences.get(context).edit().putString(Preferences.STATUS, message).apply();
        if (error) {
            Log.w("HeadsetRewind", message);
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }

    private Status() {}
}
