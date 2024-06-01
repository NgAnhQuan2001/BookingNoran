package com.aqsoft.bookingappnoran.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.text.InputType;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.aqsoft.bookingappnoran.AI.SimilarityClassifier;
import com.aqsoft.bookingappnoran.AI.TestActivity;
import com.aqsoft.bookingappnoran.Verification.LoginActivity;
import com.aqsoft.bookingappnoran.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private LinearLayout lin2, lin3, lin4, lin5, dangxuat;
    private FirebaseAuth mAuth;
    private boolean trangthaixacminh ;

    FaceDetector detector;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;

    Interpreter tfLite;
    float distance= 1.0f;
    boolean start=true,flipX=false;
    Context context= SettingActivity.this;
    int cam_face=CameraSelector.LENS_FACING_FRONT;

    int[] intValues;
    int inputSize=112;  //Input size for model
    boolean isModelQuantized=false;
    float[][] embeedings;
    float IMAGE_MEAN = 128.0f;
    float IMAGE_STD = 128.0f;
    int OUTPUT_SIZE=192; //Output size of model
    private static int SELECT_PICTURE = 1;

    String modelFile="mobile_face_net.tflite";
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    private HashMap<String, SimilarityClassifier.Recognition> registered = new HashMap<>();

    private TextView trangthaixacminh_tv;
    
    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        gettrangthaixacminh();
        initListeners();


        try {
            tfLite=new Interpreter(loadModelFile(SettingActivity.this,modelFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        FaceDetectorOptions highAccuracyOpts =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                        .build();
        detector = FaceDetection.getClient(highAccuracyOpts);
    }

    private void gettrangthaixacminh() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Thongtin").child(uid);
        reference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    trangthaixacminh = true;
                    trangthaixacminh_tv.setText("Đã xác minh");
                } else {
                    trangthaixacminh = false;
                    trangthaixacminh_tv.setText("Chưa xác minh");
                    trangthaixacminh_tv.setTextColor(Color.RED);
                }
            }
        });
    }

    private MappedByteBuffer loadModelFile(Activity activity, String MODEL_FILE) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(MODEL_FILE);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        lin2 = findViewById(R.id.lin2);
        trangthaixacminh_tv = findViewById(R.id.trangthaixacminh_tv);
        lin3 = findViewById(R.id.lin3);
        lin4 = findViewById(R.id.lin4);
        lin5 = findViewById(R.id.lin5);
        dangxuat = findViewById(R.id.dangxuat);
    }

    private void initListeners() {
        toolbar.setOnClickListener(v -> navigateTo(MainActivity.class));
        lin3.setOnClickListener(v -> navigateTo(PrivacyPolicyActivity.class));
        lin2.setOnClickListener(v -> navigateTo(AppInformationActivity.class));
        lin4.setOnClickListener(v -> showFeedbackDialog());
        lin5.setOnClickListener(v -> verifyAccount());
        dangxuat.setOnClickListener(v -> signOut());
    }

    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(SettingActivity.this, destination);
        startActivity(intent);
        finish();
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.signOut();
                    navigateTo(LoginActivity.class);
                    finishAffinity();
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showFeedbackDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle("Choose Message");

        final EditText input = new EditText(SettingActivity.this);
        input.setHint("Góp ý của khách hàng");
        builder.setView(input);

        final boolean[] usePredefinedMessage = {false};
        builder.setMultiChoiceItems(new String[]{"Thay đổi"}, null, (dialog, which, isChecked) -> {
            usePredefinedMessage[0] = isChecked;
            input.setEnabled(!isChecked);
            if (isChecked) {
                input.setText("");
            }
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            String messageText = usePredefinedMessage[0] ? "Nâng cấp tài khoản" : input.getText().toString();
            sendMessage(messageText);
        }).setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void verifyAccount() {
        if (!trangthaixacminh) {
            Dialog dialog = new Dialog(SettingActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog);
            MaterialButton next_btn = dialog.findViewById(R.id.next_btn);
            next_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    loadphoto();
                }
            });
            dialog.show();
        }
    }

    private void loadphoto()
    {
        start=false;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                try {
                    InputImage impphoto=InputImage.fromBitmap(getBitmapFromUri(selectedImageUri),0);
                    detector.process(impphoto).addOnSuccessListener(new OnSuccessListener<List<Face>>() {
                        @Override
                        public void onSuccess(List<Face> faces) {
                            if(faces.size()!=0) {
                                Face face = faces.get(0);
                                Bitmap frame_bmp= null;
                                try {
                                    frame_bmp = getBitmapFromUri(selectedImageUri);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Bitmap frame_bmp1 = rotateBitmap(frame_bmp, 0, flipX, false);
                                RectF boundingBox = new RectF(face.getBoundingBox());
                                Bitmap cropped_face = getCropBitmapByCPU(frame_bmp1, boundingBox);
                                Bitmap scaled = getResizedBitmap(cropped_face, 112, 112);
                                recognizeImage(scaled);
                                addFace();
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                Toast.makeText(context, "Không xác minh được khuôn mặt, xin vui lòng thử lại chân dung khác!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            start=true;
                            Toast.makeText(context, "Face detection failed!", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void addFace()
    {
        SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
                "0", "", -1f);
        result.setExtra(embeedings);

        registered.put(uid,result);
        start=true;
        insertToSP(registered);
    }

    private void insertToSP(HashMap<String, SimilarityClassifier.Recognition> jsonMap) {
        String jsonString = new Gson().toJson(jsonMap);
        SharedPreferences sharedPreferences = getSharedPreferences("HashMap", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("map", jsonString);
        editor.apply();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Thongtin").child(uid);
        ref.setValue(jsonString);
        Toast.makeText(context, "Lưu thông tin khuôn mặt thành công", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void recognizeImage(final Bitmap bitmap) {
        ByteBuffer imgData = ByteBuffer.allocateDirect(1 * inputSize * inputSize * 3 * 4);

        imgData.order(ByteOrder.nativeOrder());

        intValues = new int[inputSize * inputSize];

        //get pixel values from Bitmap to normalize
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
        //imgData is input to our model
        Object[] inputArray = {imgData};

        Map<Integer, Object> outputMap = new HashMap<>();


        embeedings = new float[1][OUTPUT_SIZE]; //output of model will be stored in this variable

        outputMap.put(0, embeedings);

        tfLite.runForMultipleInputsOutputs(inputArray, outputMap); //Run model



        float distance_local = Float.MAX_VALUE;
        String id = "0";
        String label = "?";

        //Compare new face with saved Faces.
        if (registered.size() > 0) {

            final List<Pair<String, Float>> nearest = findNearest(embeedings[0]);//Find 2 closest matching face

            if (nearest.get(0) != null) {

                final String name = nearest.get(0).first; //get name and distance of closest matching face
                // label = name;
                distance_local = nearest.get(0).second;



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

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }

    private void sendMessage(String messageText) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Gopy");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("Email", email);
        hashMap.put("UserId", userId);
        hashMap.put("Message", messageText);
        reference.push().setValue(hashMap);
    }
}
