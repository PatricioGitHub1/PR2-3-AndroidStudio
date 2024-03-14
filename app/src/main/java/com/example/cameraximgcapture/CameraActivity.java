package com.example.cameraximgcapture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.core.resolutionselector.ResolutionStrategy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {
    private PreviewView previewView;
    private Preview preview;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private TextView textView;
    private Button buttonCaptureImg;
    private ImageCapture imageCapture;
    private ImageCapture.OutputFileOptions outputFileOptions;
    private String fullPath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        textView = findViewById(R.id.orientation);

        // img capture
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        fullPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/img_".concat(timeStamp).concat(".jpg");
        Log.i("INFO", "Imagen en = "+fullPath);
        outputFileOptions = new ImageCapture.OutputFileOptions.Builder(new File(fullPath)).build();

        ImageCapture.Builder builder = new ImageCapture.Builder();
        imageCapture = builder
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .build();

        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));

        buttonCaptureImg = findViewById(R.id.buttonPhoto);
        buttonCaptureImg.setOnClickListener(v -> {
            captureImage();
        });
    }

    private void captureImage() {


        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                intent.putExtra("image_location", fullPath);
                startActivity(intent);
                Log.i("info", "going back");
                finish();
            }


            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Toast.makeText(CameraActivity.this, "Error en capturar la imagen", Toast.LENGTH_SHORT).show();
                Log.e("error", exception.getMessage());
            }
        });
    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(1280, 720))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(@NonNull ImageProxy image) {

            }

        });
        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
                textView.setText(Integer.toString(orientation));
            }
        };
        orientationEventListener.enable();
        preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector,
                imageAnalysis, preview, imageCapture);
    }
}
