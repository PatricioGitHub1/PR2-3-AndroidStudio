package com.example.cameraximgcapture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private static final String[] CAMERA_PERMISSION = new String[]{android.Manifest.permission.CAMERA};
    private static final int CAMERA_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView imgView = findViewById(R.id.imageView2);

        Button enableCamera = findViewById(R.id.enableCamera);
        enableCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasCameraPermission()) {
                    enableCamera();
                } else {
                    requestPermission();
                }
            }
        });

        if (getIntent().hasExtra("image_location")) {
            String imagePath = getIntent().getStringExtra("image_location");
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imgView.setImageBitmap(bitmap);
            Log.i("INFO", "image placed on imageView");
        }
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                this,
                CAMERA_PERMISSION,
                CAMERA_REQUEST_CODE
        );
    }

    private void enableCamera() {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
    }
}