package org.headsetrewind;

import android.content.Context;
import android.content.SharedPreferences;

final class Preferences {
    static final int DEFAULT_SECONDS = 30;
    static final String SECONDS = "seconds";
    static final String STATUS = "status";

    static SharedPreferences get(Context context) {
        SharedPreferences preferences =
                context.getSharedPreferences("headset_rewind", Context.MODE_PRIVATE);
        if (!preferences.getBoolean("long_press_only", false)) {
            int seconds = preferences.getInt(SECONDS, DEFAULT_SECONDS);
            // Replace the former 10-second baseline once; retain other saved durations.
            preferences.edit()
                    .putInt(SECONDS, seconds == 10 ? DEFAULT_SECONDS : seconds)
                    .putBoolean("long_press_only", true)
                    .remove("enabled").remove("clicks").remove("gap")
                    .remove("media_keys").remove(STATUS).apply();
        }
        return preferences;
    }

    private Preferences() {}
}
