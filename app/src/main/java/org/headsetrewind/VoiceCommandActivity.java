package org.headsetrewind;

import android.app.Activity;
import android.content.Intent;
import android.media.session.MediaController;
import android.os.Bundle;

public final class VoiceCommandActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        if (!Intent.ACTION_VOICE_COMMAND.equals(getIntent().getAction())) {
            Status.report(this, getString(R.string.voice_command_unavailable), true);
            finish();
            return;
        }
        MediaControl media = new MediaControl(this);
        MediaController target = media.findTarget();
        if (target != null) {
            media.seek(target, Preferences.get(this)
                    .getInt(Preferences.SECONDS, Preferences.DEFAULT_SECONDS));
        }
        // Theme.NoDisplay requires finishing before onResume; no microphone or assistant UI is opened.
        finish();
    }
}
