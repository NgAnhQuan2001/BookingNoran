package com.aqsoft.bookingappnoran.AI;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.YuvImage;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.InputType;
import android.util.Log;
import android.util.Pair;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AlertDialog;
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

import com.aqsoft.bookingappnoran.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
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
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.Face;

import org.tensorflow.lite.Interpreter;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.ReadOnlyBufferException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class TestActivity extends AppCompatActivity {
    FaceDetector detector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;

    Interpreter tfLite;

    private String ID;
    TextView reco_name;

    CameraSelector cameraSelector;
    boolean developerMode=false;
    float distance= 1.0f;
    boolean start=true,flipX=false;
    Context context=TestActivity.this;
    private boolean dialogShown = false;
    int cam_face=CameraSelector.LENS_FACING_FRONT; //Default Back Camera

    int[] intValues;
    int inputSize=112;  //Input size for model
    boolean isModelQuantized=false;
    float[][] embeedings;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    int OUTPUT_SIZE=192; //Output size of model
    private static int SELECT_PICTURE = 1;
    ProcessCameraProvider cameraProvider;
    private static final int MY_CAMERA_REQUEST_CODE = 100;

    String modelFile="mobile_face_net.tflite";
    Intent intent = getIntent();
    private boolean isVerificationDone = false;
    private String uid ;

    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>();
    private Handler handler = new Handler();
    private long lastUpdateTime = 0;
    private final long UPDATE_INTERVAL = 5000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        uid = intent.getStringExtra("useruid");
        ID = intent.getStringExtra("ID");

        readFromFirebase(data -> {
            registered = data;
        });
        setContentView(R.layout.activity_test);
        SharedPreferences sharedPref = getSharedPreferences("Distance",Context.MODE_PRIVATE);
        distance = sharedPref.getFloat("distance",1.00f);
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        try {
            tfLite=new Interpreter(loadModelFile(TestActivity.this,modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);
        cameraBind();
    }
    private void readFromFirebase(OnDataReceivedCallback callback) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Thongtin").child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String jsonString = dataSnapshot.getValue(String.class);
                    Type type = new TypeToken<HashMap<String, SimilarityClassifier.Recognition>>(){}.getType();
                    HashMap<String, SimilarityClassifier.Recognition> retrievedMap = new Gson().fromJson(jsonString, type);

                    // Xử lý dữ liệu giống như trong hàm readFromSP()
                    for (Map.Entry<String, SimilarityClassifier.Recognition> entry : retrievedMap.entrySet()) {
                        float[][] output = new float[1][OUTPUT_SIZE];
                        ArrayList arrayList = (ArrayList) entry.getValue().getExtra();
                        arrayList = (ArrayList) arrayList.get(0);
                        for (int counter = 0; counter < arrayList.size(); counter++) {
                            output[0][counter] = ((Double) arrayList.get(counter)).floatValue();
                        }
                        entry.getValue().setExtra(output);
                    }

                    callback.onDataReceived(retrievedMap);
                } else {
                    Log.d("FirebaseData", "No data found.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("FirebaseData", "Error loading data: " + databaseError.getMessage());
            }
        });
    }
    private HashMap<String, SimilarityClassifier.Recognition> readFromSP(){
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        String defValue = new Gson().toJson(new HashMap<String, SimilarityClassifier.Recognition>());
        String json=sharedPreferences.getString("map",defValue);
        TypeToken<HashMap<String,SimilarityClassifier.Recognition>> token = new TypeToken<HashMap<String,SimilarityClassifier.Recognition>>() {};
        HashMap<String,SimilarityClassifier.Recognition> retrievedMap=new Gson().fromJson(json,token.getType());
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : retrievedMap.entrySet())
        {
            float[][] output=new float[1][OUTPUT_SIZE];
            ArrayList arrayList= (ArrayList) entry.getValue().getExtra();
            arrayList = (ArrayList) arrayList.get(0);
            for (int counter = 0; counter < arrayList.size(); counter++) {
                output[0][counter]= ((Double) arrayList.get(counter)).floatValue();
            }
            entry.getValue().setExtra(output);
        }
        Toast.makeText(context, "Recognitions Loaded", Toast.LENGTH_SHORT).show();
        return retrievedMap;
    }

    private void cameraBind()
    {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        previewView=findViewById(R.id.previewView);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();

                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(cam_face)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(640, 480))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        Executor executor = Executors.newSingleThreadExecutor();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
                    @Override
                    public void analyze(@NonNull ImageProxy imageProxy) {
                        @SuppressLint("UnsafeExperimentalUsageError")
                        Image mediaImage = imageProxy.getImage();
                        if (mediaImage != null) {
                            InputImage inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
                            Task<List<Face>> result =
                                    detector.process(inputImage)
                                            .addOnSuccessListener(
                                                    new OnSuccessListener<List<Face>>() {
                                                        @Override
                                                        public void onSuccess(List<Face> faces) {
                                                            if(faces.size()!=0) {
                                                                Face face = faces.get(0); //Get first face from detected faces
//
                                                                Bitmap frame_bmp = toBitmap(mediaImage);
                                                                int rot = imageProxy.getImageInfo().getRotationDegrees();
                                                                //Adjust orientation of Face
                                                                Bitmap frame_bmp1 = rotateBitmap(frame_bmp, rot, false, false);
                                                                //Get bounding box of face
                                                                RectF boundingBox = new RectF(face.getBoundingBox());

                                                                //Crop out bounding box from whole Bitmap(image)
                                                                Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);

                                                                if(flipX)
                                                                    cropped_face = rotateBitmap(cropped_face, 0, flipX, false);
                                                                //Scale the acquired Face to 112*112 which is required input for model
                                                                Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);

                                                                if(start)
                                                                    recognizeImage(scaled);
                                                            }
                                                            else
                                                            {
                                                                if(registered.isEmpty())
                                                                    Toast.makeText(context, "Add faces", Toast.LENGTH_SHORT).show();
                                                                else
                                                                    Toast.makeText(context, "No Face Detected!", Toast.LENGTH_SHORT).show();
                                                            }

                                                        }
                                                    })
                                            .addOnFailureListener(
                                                    new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    })
                                            .addOnCompleteListener(new OnCompleteListener<List<Face>>() {
                                                @Override
                                                public void onComplete(@NonNull Task<List<Face>> task) {

                                                    imageProxy.close(); //v.important to acquire next frame for analysis
                                                }
                                            });
                        } else {
                            imageProxy.close();
                        }

                    }
                });
            }} , 4000);


        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
    }


    private Bitmap toBitmap(Image image) {

        byte[] nv21=YUV_420_888toNV21(image);
        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 75, out);
        byte[] imageBytes = out.toByteArray();
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }
    private static byte[] YUV_420_888toNV21(Image image) {

        int width = image.getWidth();
        int height = image.getHeight();
        int ySize = width*height;
        int uvSize = width*height/4;

        byte[] nv21 = new byte[ySize + uvSize*2];

        ByteBuffer yBuffer = image.getPlanes()[0].getBuffer(); // Y
        ByteBuffer uBuffer = image.getPlanes()[1].getBuffer(); // U
        ByteBuffer vBuffer = image.getPlanes()[2].getBuffer(); // V

        int rowStride = image.getPlanes()[0].getRowStride();
        assert(image.getPlanes()[0].getPixelStride() == 1);

        int pos = 0;

        if (rowStride == width) { // likely
            yBuffer.get(nv21, 0, ySize);
            pos += ySize;
        }
        else {
            long yBufferPos = -rowStride; // not an actual position
            for (; pos<ySize; pos+=width) {
                yBufferPos += rowStride;
                yBuffer.position((int) yBufferPos);
                yBuffer.get(nv21, pos, width);
            }
        }
        rowStride = image.getPlanes()[2].getRowStride();
        int pixelStride = image.getPlanes()[2].getPixelStride();

        assert(rowStride == image.getPlanes()[1].getRowStride());
        assert(pixelStride == image.getPlanes()[1].getPixelStride());

        if (pixelStride == 2 && rowStride == width && uBuffer.get(0) == vBuffer.get(1)) {
            // maybe V an U planes overlap as per NV21, which means vBuffer[1] is alias of uBuffer[0]
            byte savePixel = vBuffer.get(1);
            try {
                vBuffer.put(1, (byte)~savePixel);
                if (uBuffer.get(0) == (byte)~savePixel) {
                    vBuffer.put(1, savePixel);
                    vBuffer.position(0);
                    uBuffer.position(0);
                    vBuffer.get(nv21, ySize, 1);
                    uBuffer.get(nv21, ySize + 1, uBuffer.remaining());

                    return nv21; // shortcut
                }
            }
            catch (ReadOnlyBufferException ex) {
            }
            vBuffer.put(1, savePixel);
        }
        for (int row=0; row<height/2; row++) {
            for (int col=0; col<width/2; col++) {
                int vuPos = col*pixelStride + row*rowStride;
                nv21[pos++] = vBuffer.get(vuPos);
                nv21[pos++] = uBuffer.get(vuPos);
            }
        }
        return nv21;
    }
    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    private static Bitmap rotateBitmap(
            Bitmap bitmap, int rotationDegrees, boolean flipX, boolean flipY) {
        Matrix matrix = new Matrix();

        // Rotate the image back to straight.
        matrix.postRotate(rotationDegrees);

        // Mirror the image along the X or Y axis.
        matrix.postScale(flipX ? -1.0f : 1.0f, flipY ? -1.0f : 1.0f);
        Bitmap rotatedBitmap =
                Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        if (rotatedBitmap != bitmap) {
            bitmap.recycle();
        }
        return rotatedBitmap;
    }
    private static Bitmap getCropBitmapByCPU(Bitmap source, RectF cropRectF) {
        Bitmap resultBitmap = Bitmap.createBitmap((int) cropRectF.width(),
                (int) cropRectF.height(), Bitmap.Config.ARGB_8888);
        Canvas cavas = new Canvas(resultBitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        paint.setColor(Color.WHITE);
        cavas.drawRect(
                new RectF(0, 0, cropRectF.width(), cropRectF.height()),
                paint);
        Matrix matrix = new Matrix();
        matrix.postTranslate(-cropRectF.left, -cropRectF.top);
        cavas.drawBitmap(source, matrix, paint);
        if (source != null && !source.isRecycled()) {
            source.recycle();
        }
        return resultBitmap;
    }
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    public void recognizeImage(final Bitmap bitmap) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);
        imgData.order(ByteOrder.nativeOrder());
        intValues = new int[inputSize * inputSize];
        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        imgData.rewind();
        for (int i = 0; i < inputSize; ++i) {
            for (int j = 0; j < inputSize; ++j) {
                int pixelValue = intValues[i * inputSize + j];
                if (isModelQuantized) {
                    // Quantized model
                    imgData.put((byte) ((pixelValue >> 16) & 0xFF));
                    imgData.put((byte) ((pixelValue >> 8) & 0xFF));
                    imgData.put((byte) (pixelValue & 0xFF));
                } else { // Float model
                    imgData.putFloat((((pixelValue >> 16) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat((((pixelValue >> 8) & 0xFF) - IMAGE_MEAN) / IMAGE_STD);
                    imgData.putFloat(((pixelValue & 0xFF) - IMAGE_MEAN) / IMAGE_STD);

                }
            }
        }
        Object[] inputArray = {imgData};
        Map<Integer, Object> outputMap = new HashMap<>();
        embeedings = new float[1][OUTPUT_SIZE]; //output of model will be stored in this variable
        outputMap.put(0, embeedings);
        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model
        float distance_local = Float.MAX_VALUE;
        String id = "0";
        String label = "?";
        if (registered.size() > 0) {
            final List<Pair<String, Float>> nearest = findNearest(embeedings[0]);//Find 2 closest matching face
            if (nearest.get(0) != null) {
                final String name = nearest.get(0).first; //get name and distance of closest matching face
                // label = name;
                distance_local = nearest.get(0).second;
                if (developerMode)
                {
                    if(distance_local<distance) //If distance between Closest found face is more than 1.000 ,then output UNKNOWN face.
                        reco_name.setText("Nearest: "+name +"\nDist: "+ String.format("%.3f",distance_local)+"\n2nd Nearest: "+nearest.get(1).first +"\nDist: "+ String.format("%.3f",nearest.get(1).second));
                    else
                        reco_name.setText("Unknown "+"\nDist: "+String.format("%.3f",distance_local)+"\nNearest: "+name +"\nDist: "+ String.format("%.3f",distance_local)+"\n2nd Nearest: "+nearest.get(1).first +"\nDist: "+ String.format("%.3f",nearest.get(1).second));
                }
                else
                {
                    if(distance_local < distance && !dialogShown) {



                        Dialog dialog = new Dialog(context);
                        dialog.setContentView(R.layout.dialog2);
                        dialog.setTitle("Xác minh");
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("TaiKhoan").child(uid);
                        DatabaseReference TrangThaiDanhGia = FirebaseDatabase.getInstance().getReference("Lichsu").child(uid).child(ID);
                        TrangThaiDanhGia.child("TrangThai").setValue("Đã xác minh");
                        ref.addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.child("name").exists()) {
                                    String namee = snapshot.child("name").getValue(String.class);
                                    TextView name = dialog.findViewById(R.id.name);
                                    name.setText("Họ và tên: " + namee);
                                }
                                if (snapshot.child("email").exists()) {
                                    String email = snapshot.child("email").getValue(String.class);
                                    TextView email_tv = dialog.findViewById(R.id.email);
                                    email_tv.setText("Email: " + email);
                                }
                                if (snapshot.child("phone").exists()) {
                                    String phone = snapshot.child("phone").getValue(String.class);
                                    TextView phone_tv = dialog.findViewById(R.id.phone);
                                    phone_tv.setText("Số điện thoại: " + phone);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        MaterialButton next_btn = dialog.findViewById(R.id.next_btn);
                        next_btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                finish();
                                dialog.dismiss();
                            }
                        });
                        dialogShown = true; // Đánh dấu rằng dialog đã được hiển thị
                        dialog.show();


                    }
                    else if(distance_local>distance) {
                        if (!dialogShown) {
                            Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.dialog3);
                            dialog.setTitle("Xác minh");
                            MaterialButton next_btn = dialog.findViewById(R.id.next_btn);
                            next_btn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                    handler.postDelayed(() -> {
                                        dialogShown = false; // Cho phép hiển thị dialog xác minh lại sau này
                                    }, 4000);

                                }
                            });
                            dialog.show();
                            dialogShown = true; // Đánh dấu rằng dialog đã được hiển thị
                        }
                    }


                }
            }
        }
    }
    private List<Pair<String, Float>> findNearest(float[] emb) {
        List<Pair<String, Float>> neighbour_list = new ArrayList<Pair<String, Float>>();
        Pair<String, Float> ret = null; //to get closest match
        Pair<String, Float> prev_ret = null; //to get second closest match
        for (Map.Entry<String, SimilarityClassifier.Recognition> entry : registered.entrySet())
        {
            final String name = entry.getKey();
            final float[] knownEmb = ((float[][]) entry.getValue().getExtra())[0];
            float distance = 0;
            for (int i = 0; i < emb.length; i++) {
                float diff = emb[i] - knownEmb[i];
                distance += diff*diff;
            }
            distance = (float) Math.sqrt(distance);
            if (ret == null || distance < ret.second) {
                prev_ret=ret;
                ret = new Pair<>(name, distance);
            }
        }
        if(prev_ret==null) prev_ret=ret;
        neighbour_list.add(ret);
        neighbour_list.add(prev_ret);
        return neighbour_list;
    }
}
