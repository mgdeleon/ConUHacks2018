package com.conuhacks2018.conuhacks2018;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import clarifai2.internal.JSONObjectBuilder;


/**
 * Created by Mohammad on 2018-01-27.
 */

public class Stock {

    public String internal_symbol = "";

    public Stock(){

    }

    public ArrayList<StockData> getStockInfo(String name) throws IOException, JSONException {
        ArrayList<StockData> result = new ArrayList<>();
        name = "*"+name+"*";
        JSONObject json = readJsonFromUrl("http://conu.astuce.media:9993/api/finance/security/search?MarketCode=&Keyword="+name+"&Take=20&Skip=0&DataProvider=&SecurityTypes=&IncludeVirtualMarkets=false&IsInactive=false&FromInactiveMarkets=false&IsFavourite=false&IsOrderByDescending=false&OrderBy=&format=json",false);
        //System.out.println(json.toString());
        result.add(new StockData("ticker_code",json.get("ticker_code").toString()));
        result.add(new StockData("display_name",json.get("display_name").toString()));
        result.add(new StockData("trade_price",json.get("trade_price").toString()));
        result.add(new StockData("yield",json.get("yield").toString()));
        result.add(new StockData("net_change",json.get("net_change").toString()));
        result.add(new StockData("percent_change",json.get("percent_change").toString()));
        internal_symbol = json.get("internal_symbol").toString();

        return result;
    }

    public ArrayList<String> getGraphInfo() throws IOException, JSONException {
        ArrayList<String> result = new ArrayList<>();
        JSONObject json = readJsonFromUrl("http://conu.astuce.media/api/finance/historical/vizchart.json?symbol="+internal_symbol,true);
        JSONArray securities = json.getJSONArray("securities");
        JSONArray keys = json.names();
        //System.out.println(keys.get(1));
        JSONObject history = json.getJSONObject(keys.getString(1));
        //System.out.println(history.get("CLOSE_VALUES"));


        //System.out.println("before grapp ---------->");
        //System.out.println(securities.getJSONObject(0).get("OPEN_VALUE").toString());
        result.add(securities.getJSONObject(0).get("HIGH_VALUE").toString());
        result.add(securities.getJSONObject(0).get("LOW_VALUE").toString());
        result.add(securities.getJSONObject(0).get("OPEN_VALUE").toString());
        result.add(securities.getJSONObject(0).get("CLOSE_VALUE").toString());


        result.add(history.get("CLOSE_VALUES").toString());

        return result;
    }

    //System.out.println(" ticker_code "+ticker_code +" display_name "+display_name+" trade_price "+trade_price+" yield "+yield+" net_change "+net_change+" percentage_change "+percent_change );


    private static String readAll(Reader rd,boolean graph) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        int count = 0;
        while ((cp = rd.read()) != -1) {
            count++;
            sb.append((char) cp);
        }
        //System.out.println(sb.toString());
        if(graph == false) {
            System.out.println(sb.deleteCharAt(0));
            System.out.println(sb.deleteCharAt(count - 2));
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url,boolean graph) throws IOException,
            JSONException {
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is,
                    Charset.forName("UTF-8")));
            String jsonText = readAll(rd,graph);
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }
}