package com.example.safealert20;

import android.content.Context;
import android.media.MediaRecorder;
import android.widget.Toast;
import java.io.IOException;

public class ManagerAudio {
    private MediaRecorder recorder;
    private String fisierSalvare;
    private Context context;
    private boolean isRecording = false;

    public ManagerAudio(Context context) {
        this.context = context;

        fisierSalvare = context.getExternalCacheDir().getAbsolutePath() + "/inregistrare_urgenta.3gp";
    }

    public void incepeInregistrarea() {
        if (isRecording) return;

        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setOutputFile(fisierSalvare);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
            recorder.start();
            isRecording = true;
            Toast.makeText(context, "Inregistrare audio pornita!", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(context, "Eroare la pornirea microfonului", Toast.LENGTH_SHORT).show();
        }
    }

    public void opresteInregistrarea() {
        if (recorder != null && isRecording) {
            try {
                recorder.stop();
                recorder.release();
                recorder = null;
                isRecording = false;
                Toast.makeText(context, "Inregistrare salvata cu succes!", Toast.LENGTH_SHORT).show();
            } catch (RuntimeException e) {
                recorder.release();
                recorder = null;
                isRecording = false;
            }
        }
    }
}