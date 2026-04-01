package com.example.safealert20;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class ManagerMesajeProgramate {
    private Context context;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private Runnable actiuneTrimitere;
    private boolean isTimerActive = false;

    public ManagerMesajeProgramate(Context context, Runnable actiuneDeTrimis) {
        this.context = context;


        this.actiuneTrimitere = new Runnable() {
            @Override
            public void run() {
                isTimerActive = false;
                Toast.makeText(context, "Timpul a expirat! Mesajul a fost trimis.", Toast.LENGTH_LONG).show();
                if (actiuneDeTrimis != null) actiuneDeTrimis.run();
            }
        };
    }

    public void pornesteTimerDemo(int secunde) {
        if (isTimerActive) return;
        isTimerActive = true;
        Toast.makeText(context, "Timer setat (" + secunde + "s). Daca nu ajung la destinatie si nu anulez, se trimite alerta!", Toast.LENGTH_LONG).show();


        timerHandler.postDelayed(actiuneTrimitere, secunde * 1000L);
    }

    public void anuleazaTimer() {
        if (isTimerActive) {
            timerHandler.removeCallbacks(actiuneTrimitere);
            isTimerActive = false;
            Toast.makeText(context, "Ai ajuns cu bine!", Toast.LENGTH_SHORT).show();
        }
    }
}