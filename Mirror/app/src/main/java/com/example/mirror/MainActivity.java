package com.example.mirror;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private AudioPlayer audioPlayer;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO = 1;
    private Boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        audioPlayer = new AudioPlayer();
        checkAndRequestPermissions();
    }

    private void checkAndRequestPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_AUDIO)) {
                new AlertDialog.Builder(this)
                        .setTitle("Permission needed")
                        .setMessage("This permission is needed to access the music files on your device.")
                        .setPositiveButton("OK", (dialog, which) -> ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                                MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO))
                        .create()
                        .show();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_AUDIO},
                        MY_PERMISSIONS_REQUEST_READ_MEDIA_AUDIO);
            }
        } else {
            // Permission already granted, load the audiobook
            Log.d("MainActivity", "Before loadAudio");
            loadAudio();
            Log.d("MainActivity", "After loadAudio");
        }
    }

    private void loadAudio() {
        audioPlayer.load("/storage/self/primary/Music/The Beat/I Just Can't Stop It/01 Mirror In The Bathroom.mp3", 1);
        isPlaying = true;
        new Thread(() -> {
            int count = 0;
            try{
                while(isPlaying) {
                    Thread.sleep(1000);
                    count += 1;
                    if (count >= 190) {
                        audioPlayer.skipTo(10);
                        Log.d("TAG", "SONGS restart");
                        count = 0;
                    }
                    Log.d("TAG", String.valueOf(count));
                }
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            audioPlayer.stop();
        }).start();
    }

    @Override
    protected void onPause(){
        isPlaying = false;
        this.onStop();
        super.onPause();
    }

    @Override
    protected void onStop() {
        isPlaying = false;
        super.onStop();
        finish();
    }
}