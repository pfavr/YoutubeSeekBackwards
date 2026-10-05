package org.headsetrewind;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public final class MainActivity extends Activity {
    private SharedPreferences preferences;
    private MediaControl media;
    private EditText seconds;
    private TextView accessState;
    private TextView lastResult;
    private Button accessButton;
    private final SharedPreferences.OnSharedPreferenceChangeListener changes =
            (prefs, key) -> updateStatus();

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        preferences = Preferences.get(this);
        media = new MediaControl(this);
        restoreHandler();
        setContentView(R.layout.activity_main);
        seconds = findViewById(R.id.seconds);
        accessState = findViewById(R.id.access_state);
        lastResult = findViewById(R.id.last_result);
        accessButton = findViewById(R.id.notification_settings);
        seconds.setText(getString(R.string.duration_value,
                preferences.getInt(Preferences.SECONDS, Preferences.DEFAULT_SECONDS)));
        findViewById(R.id.save).setOnClickListener(view -> save());
        seconds.setOnEditorActionListener((view, action, event) -> {
            if (action == EditorInfo.IME_ACTION_DONE) {
                save();
                return true;
            }
            return false;
        });
        accessButton.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            } catch (ActivityNotFoundException error) {
                Status.report(this, getString(R.string.settings_unavailable), true);
            }
        });
    }

    private void restoreHandler() {
        ComponentName handler = new ComponentName(this, VoiceCommandActivity.class);
        PackageManager manager = getPackageManager();
        if (manager.getComponentEnabledSetting(handler) != PackageManager.COMPONENT_ENABLED_STATE_DEFAULT) {
            try {
                // Older versions had an opt-in switch; the manifest now always enables the handler.
                manager.setComponentEnabledSetting(handler, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                        PackageManager.DONT_KILL_APP);
            } catch (SecurityException | IllegalArgumentException error) {
                android.util.Log.w("HeadsetRewind", "Voice-command setup failed", error);
                Status.report(this, getString(R.string.voice_command_setup_error), true);
            }
        }
    }

    private void save() {
        try {
            int amount = Integer.parseInt(seconds.getText().toString().trim());
            if (amount < 1 || amount > 120) {
                seconds.setError(getString(R.string.invalid_settings));
                return;
            }
            preferences.edit().putInt(Preferences.SECONDS, amount).apply();
            seconds.setError(null);
            getSystemService(InputMethodManager.class)
                    .hideSoftInputFromWindow(seconds.getWindowToken(), 0);
            seconds.clearFocus();
            Toast.makeText(this, R.string.settings_saved, Toast.LENGTH_SHORT).show();
        } catch (NumberFormatException error) {
            seconds.setError(getString(R.string.invalid_settings));
        }
    }

    private void updateStatus() {
        boolean granted = media.hasAccess();
        accessState.setText(granted ? R.string.access_ready : R.string.access_needed);
        accessState.setTextColor(getColor(granted ? R.color.accent : R.color.primary_text));
        accessButton.setText(granted ? R.string.manage_access : R.string.grant_access);
        String result = preferences.getString(Preferences.STATUS, "");
        lastResult.setText(result);
        lastResult.setVisibility(result.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        preferences.registerOnSharedPreferenceChangeListener(changes);
        updateStatus();
    }

    @Override
    protected void onPause() {
        preferences.unregisterOnSharedPreferenceChangeListener(changes);
        super.onPause();
    }
}
