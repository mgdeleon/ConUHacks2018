package com.conuhacks2018.conuhacks2018;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.Model;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Concept;
import clarifai2.dto.prediction.Logo;
import clarifai2.exception.ClarifaiException;
import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity {

    /* API KEY */

    /* UI ELEMENTS */
    ImageView brandImage;

    String imagePath;
    File imageFile;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DELETE THIS PART
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dispatchTakePictureIntent();

        brandImage = findViewById(R.id.brandImage);
        brandImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                dispatchTakePictureIntent();
            }}
        );

    }


    /* TO TAKE A PICTURE */
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap thumbnail = (Bitmap) extras.get("data");
            brandImage.setImageBitmap(thumbnail);

            String timeStamp = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            String imageFileName = "IMG_" + timeStamp;
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            try {
                imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
                imagePath = imageFile.getAbsolutePath();
                Log.i("TAG", imagePath);
                //getBrand(imageFile);
            } catch (Exception e) {}

        }
    }

    /* END OF TAKING PICTURE */

    /* TO PROCESS A PICTURE */
    String getBrand(File imageFile){
        Log.d("TAG", "Started getBrand()");

        final ClarifaiClient client = new ClarifaiBuilder("b274ccb0426b4b3da3b313e1f118141a").buildSync();

        Log.d("TAG", "Valid Key");

        PredictRequest<Logo> request = client.getDefaultModels().logoModel().predict()
                .withInputs(ClarifaiInput.forImage(imageFile));

        Log.d("TAG", "Valid Request");

        List<ClarifaiOutput<Logo>> result = request.executeSync().get();

        Log.d("TAG", "Valid Result");

        String brand = result.get(0).data().get(0).concepts().get(0).name();
        Log.d("TAG", brand);

        return brand;
    }
    /* END OF PROCESSING PICTURE */
}
