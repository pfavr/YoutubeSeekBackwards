package org.headsetrewind;

final class SeekPosition {
    static long backward(long position, long updatedAt, long now, float speed,
                         boolean playing, long duration, int seconds) {
        if (position < 0 || seconds < 1 || !Float.isFinite(speed)) {
            throw new IllegalArgumentException("Unknown position or invalid seek parameters");
        }
        double estimated = position;
        if (playing && updatedAt > 0) {
            estimated += Math.max(0.0, (double) now - updatedAt) * speed;
        }
        if (duration > 0) {
            estimated = Math.min(estimated, duration);
        }
        return (long) Math.max(0.0, Math.min(Long.MAX_VALUE, estimated - seconds * 1000.0));
    }

    private SeekPosition() {}
}
