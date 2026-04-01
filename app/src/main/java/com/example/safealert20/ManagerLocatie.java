package com.example.safealert20;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ManagerLocatie {

    private Context context;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;

    public ManagerLocatie(Context context) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance();
        this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    public void preiaLocatiaSiTrimiteInCloud(Runnable actiuneUrmatoare) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    String linkGoogleMaps = "Locatie indisponibila";
                    if (location != null) {
                        linkGoogleMaps = "http://maps.google.com/?q=46.534075, 24.540282";
                    }
                    trimiteDate(linkGoogleMaps);


                    if(actiuneUrmatoare != null) actiuneUrmatoare.run();
                }
            });
        } else {
            trimiteDate("Locatie indisponibila");
            if(actiuneUrmatoare != null) actiuneUrmatoare.run();
        }
    }

    private void trimiteDate(String linkLocatie) {
        Map<String, Object> alerta = new HashMap<>();
        alerta.put("status_urgenta", true);
        alerta.put("mesaj_automat", "Sunt in pericol! Locatia mea: " + linkLocatie);

        db.collection("Utilizatori").document("user_test").set(alerta);
        Toast.makeText(context, "Alerta trimisa!", Toast.LENGTH_SHORT).show();
    }
}
