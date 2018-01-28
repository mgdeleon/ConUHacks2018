package com.conuhacks2018.conuhacks2018;

/**
 * Created by MG on 2018-01-28.
 */

public class StockData {
    String type;
    String value;



    StockData(){}

    StockData(String type, String value){
        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

