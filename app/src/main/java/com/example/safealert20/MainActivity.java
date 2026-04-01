package com.example.safealert20;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Declarare evenimente
    private Button btnSos, btnCancel;
    private int nrApasariVolum = 0;
    private long timpulUltimeiApasari = 0;
    private Handler timerHandler = new Handler(Looper.getMainLooper());
    private boolean isAlertCancelled = false;


    //Declarare manageri
    private ManagerBaterie managerBaterie;
    private ManagerApeluri managerApeluri;
    private ManagerLocatie managerLocatie;
    private ManagerAudio managerAudio;
    private ManagerMesajeProgramate managerMesajeProgramate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Legarea evenimentelor
        btnSos = findViewById(R.id.btnSos);
        btnCancel = findViewById(R.id.btnCancel);

        //Initializare manageri
        managerApeluri = new ManagerApeluri(this);
        managerLocatie = new ManagerLocatie(this);

        managerBaterie = new ManagerBaterie(this, managerLocatie);
        managerBaterie.pornesteMonitorizarea();

        managerAudio = new ManagerAudio(this);

        managerMesajeProgramate = new ManagerMesajeProgramate(this, new Runnable() {
            @Override
            public void run() {
                managerLocatie.preiaLocatiaSiTrimiteInCloud(null);
            }
        });

        cerePermisiuni();




        btnSos.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                managerMesajeProgramate.pornesteTimerDemo(20);
                return true;
            }
        });



        btnSos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEmergencyCountdown();
            }
        });




        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAlertCancelled = true;
                anuleazaAlertaInCloud();
                managerAudio.opresteInregistrarea();
                managerMesajeProgramate.anuleazaTimer();
            }
        });
    }



    private void startEmergencyCountdown() {
        isAlertCancelled = false;
        managerAudio.incepeInregistrarea();
        Toast.makeText(this, "Alerta se trimite in 10 secunde", Toast.LENGTH_LONG).show();

        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isAlertCancelled) {
                    managerLocatie.preiaLocatiaSiTrimiteInCloud(new Runnable() {
                        @Override
                        public void run() {
                            managerApeluri.efectueazaApelAutomat();
                        }
                    });
                }
            }
        }, 10000);
    }

    private void anuleazaAlertaInCloud() {
        Map<String, Object> alerta = new HashMap<>();
        alerta.put("status_urgenta", false);
        FirebaseFirestore.getInstance().collection("Utilizatori").document("user_test").set(alerta);

        Toast.makeText(this, "Alerta anulata!", Toast.LENGTH_SHORT).show();
        managerAudio.opresteInregistrarea();
    }

    private void cerePermisiuni() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CALL_PHONE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.RECORD_AUDIO
            }, 100);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            long timpulCurent = System.currentTimeMillis();

            if (timpulCurent - timpulUltimeiApasari > 1000) {
                nrApasariVolum = 0;
            }

            nrApasariVolum++;
            timpulUltimeiApasari = timpulCurent;

            if (nrApasariVolum == 3) {
                Toast.makeText(this, "Shortcut fizic", Toast.LENGTH_SHORT).show();
                startEmergencyCountdown();
                nrApasariVolum = 0;
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(managerBaterie != null) {
            managerBaterie.opresteMonitorizarea();
        }
    }
}