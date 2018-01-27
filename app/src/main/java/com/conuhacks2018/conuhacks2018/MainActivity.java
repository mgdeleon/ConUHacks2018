package com.conuhacks2018.conuhacks2018;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        final ClarifaiClient client = new ClarifaiBuilder("b274ccb0426b4b3da3b313e1f118141a").buildSync();

//        Model<Concept> generalModel = client.getDefaultModels().generalModel();
//
//        PredictRequest<Concept> request = generalModel.predict().withInputs(
//                ClarifaiInput.forImage("https://samples.clarifai.com/metro-north.jpg")
//        );
//        List<ClarifaiOutput<Concept>> result = request.executeSync().get();
//
//        Log.d("TAG", result.toString());


        List<ClarifaiOutput<Logo>> result = client.getDefaultModels().logoModel().predict()
                .withInputs(ClarifaiInput.forImage("https://samples.clarifai.com/logo.jpg"))
                .executeSync().get();

        String type = result.getClass().toString();
        Log.d("TAG", type);
        Log.d("TAG", result.toString());

    }
}
