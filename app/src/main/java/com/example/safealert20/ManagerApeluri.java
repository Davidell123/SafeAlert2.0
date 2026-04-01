package com.example.safealert20;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;

public class ManagerApeluri {

    private Context context;
    private final String NUMAR_URGENTA = "0729143337";

    public ManagerApeluri(Context context) {
        this.context = context;
    }

    public void efectueazaApelAutomat() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + NUMAR_URGENTA));
            context.startActivity(callIntent);
        } else {
            Toast.makeText(context, "Nu exista permisiune pentru apel!", Toast.LENGTH_SHORT).show();
        }
    }
}