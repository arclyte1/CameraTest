package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity
{
    private static final String NOTIFICATION_CHANNEL_ID = "Main Notification";
    private SurfaceView surfaceView;
    private Camera camera;
    private static final int REQUEST_CODE_PERMISSION_CAMERA = 0;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        surfaceView = findViewById(R.id.surfaceView);
        int permissionStatus = requestCameraPermissions();
        setupSurfaceView(permissionStatus);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            createNotificationChannel();
        }
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                showNotification();
            }
        });
    }

    private int requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
        }
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
    }

    private void setupSurfaceView(int permissionStatus) {
        if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
            camera = Camera.open();
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            try {
                camera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
            camera.startPreview();
        } else {
            surfaceView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    requestCameraPermissions();
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CAMERA: // это тот	код, который объявили в классе и использовали при запросе разрешения.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    setupSurfaceView(PackageManager.PERMISSION_GRANTED);
                } else {
                    Toast.makeText(this, "Sorry, I cant work without camera permissions, please click on black view to request permissions", Toast.LENGTH_LONG).show();
                    setupSurfaceView(PackageManager.PERMISSION_DENIED);
                }
                return;
        }
    }

    private void showNotification() {
        NotificationCompat.Builder builder = new
                NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("Заголовок")
                .setContentText("Текст")
                .setStyle(new NotificationCompat.BigTextStyle())
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager =	NotificationManagerCompat.from(this);
        Random random = new Random();
        notificationManager.notify(random.nextInt(), builder.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Main", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Main notification channel");
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

}
