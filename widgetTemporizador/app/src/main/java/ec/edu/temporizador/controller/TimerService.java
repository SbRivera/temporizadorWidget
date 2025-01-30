package ec.edu.temporizador.controller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import ec.edu.temporizador.R;
import ec.edu.temporizador.model.Timer;
import ec.edu.temporizador.ui.TimerWidgetProvider;

public class TimerService extends Service {

    private static final String CHANNEL_ID = "TIMER_SERVICE_CHANNEL";
    private static final int NOTIFICATION_ID = 1001;

    private Timer timer;
    private Handler handler;
    private Runnable runnable;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startInForeground();

        long totalTime = intent.getLongExtra("total_time", 0);
        if (totalTime > 0) {
            timer = new Timer(totalTime);
        }

        startTimerLoop();

        return START_NOT_STICKY;
    }

    private void startInForeground() {
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Temporizador en curso")
                .setContentText("El widget se actualizará en tiempo real")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
        startForeground(NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Canal del Temporizador",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Muestra el tiempo restante mientras el temporizador corre");
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private void startTimerLoop() {
        runnable = new Runnable() {
            @Override
            public void run() {
                if (timer.isFinished()) {
                    updateWidget(0);
                    stopSelf();
                } else {
                    updateWidget(timer.getTimeRemaining());
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.post(runnable);
    }

    private void updateWidget(long millisRemaining) {
        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName widgetComponent = new ComponentName(this, TimerWidgetProvider.class);
        int[] widgetIds = manager.getAppWidgetIds(widgetComponent);

        RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget_layout);
        views.setTextViewText(R.id.text_time, timer.formatTime());

        for (int widgetId : widgetIds) {
            manager.updateAppWidget(widgetId, views);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null && runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
