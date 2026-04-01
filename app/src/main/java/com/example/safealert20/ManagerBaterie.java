package com.example.safealert20;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.provider.Settings;
import android.widget.Toast;

public class ManagerBaterie {

    private Context context;
    private ManagerLocatie managerLocatie;
    private BroadcastReceiver batteryReceiver;
    private boolean alertaTrimisa = false;

    public ManagerBaterie(Context context, ManagerLocatie managerLocatie) {
        this.context = context;
        this.managerLocatie = managerLocatie;
    }

    public void pornesteMonitorizarea() {
        batteryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int nivel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scara = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float procentBaterie = nivel * 100 / (float) scara;

                // Verificam daca bateria a scazut la 10%
                if (procentBaterie <= 10.0f && !alertaTrimisa) {
                    alertaTrimisa = true;

                    Toast.makeText(context, "Baterie scazuta 10%! Se trimite locatia...", Toast.LENGTH_LONG).show();


                    managerLocatie.preiaLocatiaSiTrimiteInCloud(null);

                    // Activam modul de economisire
                    try {
                        Intent intentBaterie = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
                        intentBaterie.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intentBaterie);
                    } catch (Exception e) {
                        Toast.makeText(context, "Activati modul economisire a bateriei!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        };


        IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        context.registerReceiver(batteryReceiver, filter);
    }

    public void opresteMonitorizarea() {
        if (batteryReceiver != null) {
            context.unregisterReceiver(batteryReceiver);
        }
    }
}