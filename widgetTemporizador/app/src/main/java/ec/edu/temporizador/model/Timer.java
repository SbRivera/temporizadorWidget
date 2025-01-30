package ec.edu.temporizador.model;

public class Timer {
    private long durationMillis;
    private long endTimeMillis;

    public Timer(long durationMillis) {
        this.durationMillis = durationMillis;
        this.endTimeMillis = System.currentTimeMillis() + durationMillis;
    }

    public long getTimeRemaining() {
        long currentTime = System.currentTimeMillis();
        return Math.max(0, endTimeMillis - currentTime);
    }

    public String formatTime() {
        long millis = getTimeRemaining();
        long totalSeconds = millis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;
        return String.format("%02dh %02dm %02ds", hours, minutes, seconds);
    }

    public boolean isFinished() {
        return getTimeRemaining() <= 0;
    }
}
