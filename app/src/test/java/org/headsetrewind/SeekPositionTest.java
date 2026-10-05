package org.headsetrewind;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public final class SeekPositionTest {
    @Test
    public void thirtySecondRewindHasExactDestination() {
        assertEquals(30000, SeekPosition.backward(60000, 1000, 9000, 1f, false, -1, 30));
        assertEquals(32000, SeekPosition.backward(60000, 1000, 3000, 1f, true, -1, 30));
    }

    @Test
    public void playingPositionExtrapolatedAtPlaybackSpeed() {
        assertEquals(23000, SeekPosition.backward(30000, 1000, 3000, 1.5f, true, -1, 10));
    }

    @Test
    public void pausedPositionDoesNotAdvance() {
        assertEquals(20000, SeekPosition.backward(30000, 1000, 9000, 1f, false, -1, 10));
    }

    @Test
    public void clampsAtStartIncludingExtrapolatedReversePlayback() {
        assertEquals(0, SeekPosition.backward(5000, 1000, 2000, 1f, true, -1, 10));
        assertEquals(0, SeekPosition.backward(500, 1000, 2000, -1f, true, -1, 1));
    }

    @Test
    public void respectsKnownDurationBeforeSubtracting() {
        assertEquals(50000, SeekPosition.backward(59000, 1000, 9000, 1f, true, 60000, 10));
    }

    @Test
    public void missingOrFutureTimestampDoesNotAdvance() {
        assertEquals(20000, SeekPosition.backward(30000, 0, 9000, 1f, true, 0, 10));
        assertEquals(20000, SeekPosition.backward(30000, 10000, 9000, 1f, true, 0, 10));
    }

    @Test
    public void zeroSpeedDoesNotAdvance() {
        assertEquals(20000, SeekPosition.backward(30000, 1000, 9000, 0f, true, -1, 10));
    }

    @Test
    public void largeValuesDoNotOverflow() {
        assertEquals(Long.MAX_VALUE, SeekPosition.backward(Long.MAX_VALUE, 1, Long.MAX_VALUE,
                Float.MAX_VALUE, true, -1, 120));
    }

    @Test
    public void invalidPositionAndSpeedAreRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> SeekPosition.backward(-1, 0, 0, 1f, true, -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> SeekPosition.backward(10000, 0, 0, Float.NaN, true, -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> SeekPosition.backward(10000, 0, 0, Float.POSITIVE_INFINITY, true, -1, 10));
        assertThrows(IllegalArgumentException.class,
                () -> SeekPosition.backward(10000, 0, 0, 1f, true, -1, 0));
    }
}
