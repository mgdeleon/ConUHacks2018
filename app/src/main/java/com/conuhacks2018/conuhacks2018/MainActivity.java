package com.conuhacks2018.conuhacks2018;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.CandleStickChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ICandleDataSet;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import clarifai2.api.ClarifaiBuilder;
import clarifai2.api.ClarifaiClient;
import clarifai2.api.request.model.PredictRequest;
import clarifai2.dto.input.ClarifaiInput;
import clarifai2.dto.model.output.ClarifaiOutput;
import clarifai2.dto.prediction.Logo;

public class MainActivity extends AppCompatActivity {

    /* API KEY */
    final ClarifaiClient client = new ClarifaiBuilder("b274ccb0426b4b3da3b313e1f118141a").buildSync();

    /* UI ELEMENTS */
    ImageView searchImage;
    ImageView cameraImage;
    ImageView brandImage;
    ListView stockDetailsListView;
    private CandleStickChart stockChart;
    TextView noStocksTextView ;

    String brand;
    String imagePath;
    File imageFile;

    Stock stock;
    ArrayList<StockData> stockDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //DELETE THIS PART
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        // Get reference to UI elements
        searchImage = findViewById(R.id.searchImage);
        searchImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                stockDetails = new ArrayList<>();
                if(getBrand()) {
                    stock = new Stock();

                    try {
                        stockDetails = stock.getStockInfo("Apple");

                    } catch (IOException e) {
                        Log.d("TAG", "IOException");
                        e.printStackTrace();
                    } catch (JSONException e) {
                        Log.d("TAG", "JSONException");
                        e.printStackTrace();
                    }


                    createChart();
                }
                else{
                    noStocksTextView.setText("Please Try Again.");
                }
                // Create custom adapter to access courses' data
                StockAdapter adapter = new StockAdapter(getApplicationContext(), stockDetails);

                // Get reference to listview
                ListView listView = (ListView) findViewById(R.id.stockDetails);
                LinearLayout noStocksView =  findViewById(R.id.noStocksView);


                Log.d("TAG", Integer.toString(stockDetails.size()));

                //Set ListView layout
                listView.setEmptyView(noStocksView); // Set default layout when list is empty
                listView.setAdapter(adapter);
            }}
        );
        cameraImage = findViewById(R.id.cameraImage);
        cameraImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                dispatchTakePictureIntent();
            }}
        );
        brandImage = findViewById(R.id.brandImage);
        stockDetailsListView = findViewById(R.id.stockDetails);
        noStocksTextView = findViewById(R.id.noStocksTextView);
        stockChart = findViewById(R.id.stockChart);

    }


    /* TO TAKE A PICTURE */
    static final int REQUEST_IMAGE_CAPTURE = 1;

    // Create file location to store image at
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        imagePath = imageFile.getAbsolutePath();
        return imageFile;
    }

    // Start intent to take picture
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            //...
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.conuhacks2018.conuhacks2018.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    // After the image is received
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new  File(imagePath);
            if(imgFile.exists()) {
                setPic();
            }
        }
    }

    /* END OF TAKING PICTURE */

    /* DISPLAY PICTURE*/

    // Process image to scale it down
    private void setPic() {
        // Get the dimensions of the View
        int targetW = brandImage.getWidth();
        int targetH = brandImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        brandImage.setImageBitmap(bitmap);
    }

    /* END OF DISPLAYING PICTURE*/

    /* TO PROCESS A PICTURE THROUGH CLARIFAI */
    Boolean getBrand(){
        try {
            Log.d("TAG", "Started getBrand()");

            final ClarifaiClient client = new ClarifaiBuilder("b274ccb0426b4b3da3b313e1f118141a").buildSync();

            Log.d("TAG", "Valid Key");

//        PredictRequest<Logo> request = client.getDefaultModels().logoModel().predict()
//                .withInputs(ClarifaiInput.forImage(imageFile));

            PredictRequest<Logo> request = client.getDefaultModels().logoModel().predict()
                    .withInputs(ClarifaiInput.forImage("http://krishibazareradda.com/wp-content/uploads/2016/12/Pepsi-Can-01.jpg"));

            Log.d("TAG", "Valid Request");

            List<ClarifaiOutput<Logo>> result = request.executeSync().get();

            Log.d("TAG", "Valid Result");

            // In one line
//        result = client.getDefaultModels().logoModel().predict()
//                .withInputs(ClarifaiInput.forImage(imageFile)).executeSync().get();

            brand = "Invalid Company";
            try {
                brand = result.get(0).data().get(0).concepts().get(0).name();
            } catch (Exception e) {
                Log.d("TAG", "ERROR");
                return false;
            }
            Log.d("TAG", brand);

        } catch (Exception e) {
            Log.d("TAG", "false");
            return false;
        }
        return true;
    }

    /* END OF PROCESSING PICTURE */


    /* TO CREATE A CHART */
    private Thread thread;
    void createChart(){
        final int nbrEntries = 15;
        // Set layout
        stockChart.getDescription().setEnabled(false);
        stockChart.setMaxVisibleValueCount(5);
        stockChart.setDrawGridBackground(false);
        stockChart.getLegend().setEnabled(false);

        // Set Axis
        XAxis xAxis = stockChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setCenterAxisLabels(true);
        xAxis.setGranularity(1f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("mm:ss");
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(new Date(Calendar.getInstance().getTimeInMillis()));
            }
        });

        YAxis rightAxis = stockChart.getAxisRight();
        rightAxis.setLabelCount(7, false);
        rightAxis.setDrawGridLines(true);
        rightAxis.setDrawAxisLine(true);
        rightAxis.setGridColor(Color.WHITE);
        rightAxis.setTextColor(Color.WHITE);

        stockChart.getAxisLeft().setEnabled(false);

        // Create Data Points
        final ArrayList<CandleEntry> yVals1 = new ArrayList<CandleEntry>();

        for (int i = 0; i < nbrEntries; i++) {

            try {
                yVals1.add(new CandleEntry(
                        i,
                        Float.parseFloat(stock.getGraphInfo().get(0)), //HIGH
                        Float.parseFloat(stock.getGraphInfo().get(1)), //LOW
                        Float.parseFloat(stock.getGraphInfo().get(2)), //OPEN
                        Float.parseFloat(stock.getGraphInfo().get(3))  //CLOSE
                ));



            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            try {
                Log.d("Stock", Float.toString(Float.parseFloat(stock.getGraphInfo().get(0))));
                Log.d("Stock", Float.toString(Float.parseFloat(stock.getGraphInfo().get(1))));
                Log.d("Stock", Float.toString(Float.parseFloat(stock.getGraphInfo().get(2))));
                Log.d("Stock", Float.toString(Float.parseFloat(stock.getGraphInfo().get(3))));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


//            Log.d("TAG", "Added Entry");
//            float val = (float) (Math.random() * 40) + 50;
//            float high = (float) (Math.random() * 9) + 8f;
//            float low = (float) (Math.random() * 9) + 8f;
//            float open = (float) (Math.random() * 6) + 1f;
//            float close = (float) (Math.random() * 6) + 1f;
//
//            boolean even = i % 2 == 0;
//
//            yVals1.add(new CandleEntry(
//                    i, val + high,
//                    val - low,
//                    even ? val + open : val - open,
//                    even ? val - close : val + close
//            ));
//
//            Log.d("Stock", Float.toString(val + high));
//            Log.d("Stock", Float.toString(val - low));
//            Log.d("Stock", Float.toString(even ? val + open : val - open));
//            Log.d("Stock", Float.toString(even ? val - close : val + close));
        }

        final CandleDataSet set1 = new CandleDataSet(yVals1, "Data Set");
        set1.setDrawHighlightIndicators(false);
        set1.setDrawIcons(false);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set1.setDrawValues(false);
        set1.setShadowColor(Color.WHITE);
        set1.setShadowWidth(0.7f);
        set1.setDecreasingColor(Color.RED);
        set1.setDecreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingPaintStyle(Paint.Style.FILL);
        set1.setIncreasingColor(Color.rgb(122, 242, 84));
        set1.setIncreasingPaintStyle(Paint.Style.STROKE);
        set1.setNeutralColor(Color.BLUE);
        //set1.setHighlightLineWidth(1f);

        Log.d("TAG", "Formated data set");
        final CandleData data = new CandleData(set1);

        Log.d("TAG", "Gave dataset to data");
        stockChart.setData(data);

        Log.d("TAG", "Set data on chart");
        stockChart.setVisibleXRangeMaximum(10); // allow 20 values to be displayed at once on the x-axis, not more
        stockChart.moveViewToX(nbrEntries-1);

        Log.d("TAG", "formatted chart");

        stockChart.invalidate();
        Log.d("TAG", "ADDED FIRST SET OF POINTS");


        if (thread != null)
            thread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {


                try {
                    set1.addEntry(new CandleEntry(set1.getEntryCount(),
                            Float.parseFloat(stock.getGraphInfo().get(0)),
                            Float.parseFloat(stock.getGraphInfo().get(1)),
                            Float.parseFloat(stock.getGraphInfo().get(2)),
                            Float.parseFloat(stock.getGraphInfo().get(3))));
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                data.notifyDataChanged();

                // let the chart know it's data has changed
                stockChart.notifyDataSetChanged();

                // limit the number of visible entries
                stockChart.setVisibleXRangeMaximum(10);
                // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                // move to the latest entry
                stockChart.moveViewToX(data.getEntryCount());
            }
        };

        thread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();

    }
}
