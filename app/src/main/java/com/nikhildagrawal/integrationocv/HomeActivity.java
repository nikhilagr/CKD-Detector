package com.nikhildagrawal.integrationocv;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

public class HomeActivity extends AppCompatActivity {

    Button mBtnBlur,mBtnLoad;
    private ImageView ivImage, ivImageProcessed;
    Mat src;

    public static final int SELECT_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mBtnBlur = findViewById(R.id.btnMeanBlur);
        mBtnLoad = findViewById(R.id.btnLoad);
        ivImage = findViewById(R.id.ivImage);
        ivImageProcessed = findViewById(R.id.ivImageProcessed);


        mBtnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);

            }
        });



        mBtnBlur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            try {
                //Code to load image into a Bitmap and convert it to a Mat for processing.

                final Uri imageUri = data.getData();


                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                src = new Mat(selectedImage.getHeight(), selectedImage.getWidth(), CvType.CV_8UC4);

                Utils.bitmapToMat(selectedImage, src);


                //Imgproc.blur(src, src, new Size(3,3));
                //Imgproc.GaussianBlur(src, src, new Size(3,3), 0);
                //Imgproc.medianBlur(src, src, 3);

//                Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
//                Imgproc.adaptiveThreshold(src, src, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 3, 0);



                Mat kernelDilate = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
                Imgproc.dilate(src, src, kernelDilate);

//                Mat kernelErode = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(5, 5));
//                Imgproc.erode(src, src, kernelErode);



                //Code to convert Mat to Bitmap to load in an ImageView. Also load original image in imageView

                Bitmap processedImage = Bitmap.createBitmap(src.cols(), src.rows(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(src, processedImage);
                ivImage.setImageBitmap(selectedImage);
                ivImageProcessed.setImageBitmap(processedImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    //DO YOUR WORK/STUFF HERE
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };
    @Override
    protected void onResume() {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, this,
                mOpenCVCallBack);
    }



}
