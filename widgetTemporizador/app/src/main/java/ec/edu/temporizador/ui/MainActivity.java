package ec.edu.temporizador.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ec.edu.temporizador.R;
import ec.edu.temporizador.controller.TimerService;

public class MainActivity extends AppCompatActivity {

    private NumberPicker pickerHours, pickerMinutes, pickerSeconds;
    private Button btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        pickerHours = findViewById(R.id.pickerHours);
        pickerMinutes = findViewById(R.id.pickerMinutes);
        pickerSeconds = findViewById(R.id.pickerSeconds);
        btnStart = findViewById(R.id.btnStart);

        pickerHours.setMinValue(0);
        pickerHours.setMaxValue(23);
        pickerHours.setWrapSelectorWheel(true);
        pickerHours.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);

        pickerMinutes.setMinValue(0);
        pickerMinutes.setMaxValue(59);
        pickerMinutes.setWrapSelectorWheel(true);
        pickerMinutes.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);

        pickerSeconds.setMinValue(0);
        pickerSeconds.setMaxValue(59);
        pickerSeconds.setWrapSelectorWheel(true);
        pickerSeconds.setDescendantFocusability(NumberPicker.FOCUS_AFTER_DESCENDANTS);

        btnStart.setOnClickListener(v -> {
            int hours = pickerHours.getValue();
            int minutes = pickerMinutes.getValue();
            int seconds = pickerSeconds.getValue();

            long totalMillis = (hours * 3600L + minutes * 60L + seconds) * 1000L;
            if (totalMillis == 0) {
                Toast.makeText(this, "Selecciona un tiempo mayor a 0", Toast.LENGTH_SHORT).show();
                return;
            }

            // 1) Iniciar el servicio con el tiempo total
            Intent serviceIntent = new Intent(this, TimerService.class);
            serviceIntent.putExtra("total_time", totalMillis);
            startService(serviceIntent);

            // 2) Forzar la actualización inmediata del widget
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            int[] widgetIds = manager.getAppWidgetIds(
                    new ComponentName(this, TimerWidgetProvider.class));

            Intent updateIntent = new Intent(this, TimerWidgetProvider.class);
            updateIntent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
            updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, widgetIds);
            sendBroadcast(updateIntent);

            Toast.makeText(this, "Temporizador iniciado", Toast.LENGTH_SHORT).show();
            finish();
        });

    }

}
