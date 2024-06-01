package com.aqsoft.bookingappnoran.Partner;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Size;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.aqsoft.bookingappnoran.AI.OnDataReceivedCallback;
import com.aqsoft.bookingappnoran.AI.SimilarityClassifier;
import com.aqsoft.bookingappnoran.AI.TestActivity;
import com.aqsoft.bookingappnoran.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class PartnerVerifyAIActivity extends AppCompatActivity {
    FaceDetector detector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    ImageView face_preview;
    Interpreter tfLite;
    TextView reco_name, preview_info, textAbove_preview;
    Button recognize, camera_switch, actions;
    ImageButton add_face;
    CameraSelector cameraSelector;
    boolean developerMode = false;
    float distance = 1.0f;
    boolean start = true, flipX = false;
    Context context = PartnerVerifyAIActivity.this;
    int cam_face = CameraSelector.LENS_FACING_FRONT; //Default Back Camera

    int[] intValues;
    int inputSize = 112;  //Input size for model
    boolean isModelQuantized = false;
    float[][] embeedings;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    int OUTPUT_SIZE = 192; //Output size of model
    private static int SELECT_PICTURE = 1;
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    String modelFile = "mobile_face_net.tflite"; //model name
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>();


    // Thêm các biến mới cho việc đếm ngược 10 giây
    private Handler handler = new Handler();
    private long lastUpdateTime = 0;
    private final long UPDATE_INTERVAL = 5000; // 10 seconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_partner_verify_aiactivity);
    }

//        readFromFirebase(data -> {
//            registered = data; // Lưu dữ liệu vào biến registered
//        });
//        try {
//            tfLite=new Interpreter(loadModelFile(PartnerVerifyAIActivity.this,modelFile));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        FaceDetectorOptions highAccuracyOpts =
//                new FaceDetectorOptions.Builder()
//                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
//                        .build();
//        detector = FaceDetection.getClient(highAccuracyOpts);
//        cameraBind();
    }
//
//    private void cameraBind()
//    {
//        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//
//        previewView=findViewById(R.id.previewView);
//        cameraProviderFuture.addListener(() -> {
//            try {
//                cameraProvider = cameraProviderFuture.get();
//
//                bindPreview(cameraProvider);
//            } catch (ExecutionException | InterruptedException e) {
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }
//
//    @OptIn(markerClass = ExperimentalGetImage.class)
//    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
//        Preview preview = new Preview.Builder().build();
//
//        cameraSelector = new CameraSelector.Builder()
//                .requireLensFacing(cam_face)
//                .build();
//
//        preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
//                .setTargetResolution(new Size(640, 480))
//                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                .build();
//
//        Executor executor = Executors.newSingleThreadExecutor();
//        imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
//            @Override
//            public void analyze(@NonNull ImageProxy imageProxy) {
//                @SuppressLint("UnsafeExperimentalUsageError")
//                Image mediaImage = imageProxy.getImage();
//                if (mediaImage != null) {
//                    long currentTimeMillis = System.currentTimeMillis();
//                    if (lastUpdateTime == 0 || (currentTimeMillis - lastUpdateTime) > UPDATE_INTERVAL) {
//                        lastUpdateTime = currentTimeMillis;
//                        // Convert the Image to an InputImage
//                        InputImage inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
//                        Task<List<Face>> result =
//                                detector.process(inputImage)
//                                        .addOnSuccessListener(
//                                                new OnSuccessListener<List<Face>>() {
//                                                    @Override
//                                                    public void onSuccess(List<Face> faces) {
//
//                                                        if(faces.size()!=0) {
//
//                                                            Face face = faces.get(0); //Get first face from detected faces
////                                                    System.out.println(face);
//
//                                                            //mediaImage to Bitmap
//                                                            Bitmap frame_bmp = toBitmap(mediaImage);
//
//                                                            int rot = imageProxy.getImageInfo().getRotationDegrees();
//
//                                                            //Adjust orientation of Face
//                                                            Bitmap frame_bmp1 = rotateBitmap(frame_bmp, rot, false, false);
//
//
//
//                                                            //Get bounding box of face
//                                                            RectF boundingBox = new RectF(face.getBoundingBox());
//
//                                                            //Crop out bounding box from whole Bitmap(image)
//                                                            Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);
//
//                                                            if(flipX)
//                                                                cropped_face = rotateBitmap(cropped_face, 0, flipX, false);
//                                                            //Scale the acquired Face to 112*112 which is required input for model
//                                                            Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);
//
//                                                            if(start)
//                                                                recognizeImage(scaled); //Send scaled bitmap to create face embeddings.
////
//
//                                                        }
//                                                        else
//                                                        {
//                                                            if(registered.isEmpty())
//                                                                reco_name.setText("Add Face");
//                                                            else
//                                                                reco_name.setText("No Face Detected!");
//                                                        }
//
//                                                    }
//                                                })
//                                        .addOnFailureListener(
//                                                new OnFailureListener() {
//                                                    @Override
//                                                    public void onFailure(@NonNull Exception e) {
//
//                                                    }
//                                                })
//                                        .addOnCompleteListener(new OnCompleteListener<List<Face>>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<List<Face>> task) {
//
//                                                imageProxy.close(); //v.important to acquire next frame for analysis
//                                            }
//                                        });
//                    } else {
//                        imageProxy.close(); // Always close the imageProxy if not processing
//                    }
//                }
//            }
//        });
//
//        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
//    }
//
//
//
//    private File loadModelFile(TestActivity testActivity, String modelFile) {
//    }
//
//    private void readFromFirebase(OnDataReceivedCallback callback) {
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference ref = database.getReference("Thongtin").child(uid);
//
//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    String jsonString = dataSnapshot.getValue(String.class);
//                    Type type = new TypeToken<HashMap<String, SimilarityClassifier.Recognition>>(){}.getType();
//                    HashMap<String, SimilarityClassifier.Recognition> retrievedMap = new Gson().fromJson(jsonString, type);
//
//                    // Xử lý dữ liệu giống như trong hàm readFromSP()
//                    for (Map.Entry<String, SimilarityClassifier.Recognition> entry : retrievedMap.entrySet()) {
//                        float[][] output = new float[1][OUTPUT_SIZE];
//                        ArrayList arrayList = (ArrayList) entry.getValue().getExtra();
//                        arrayList = (ArrayList) arrayList.get(0);
//                        for (int counter = 0; counter < arrayList.size(); counter++) {
//                            output[0][counter] = ((Double) arrayList.get(counter)).floatValue();
//                        }
//                        entry.getValue().setExtra(output);
//                    }
//
//                    callback.onDataReceived(retrievedMap);
//                } else {
//                    Log.d("FirebaseData", "No data found.");
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                Log.d("FirebaseData", "Error loading data: " + databaseError.getMessage());
//            }
//        });
//    }
//}