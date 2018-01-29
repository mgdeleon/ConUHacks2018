package com.conuhacks2018.conuhacks2018;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by MG on 2018-01-27.
 */

public class StockAdapter extends ArrayAdapter {

    TextView typeTextView;
    TextView valueTextView;

    Context context;
    StockAdapter(Context context, ArrayList<StockData> stockDetails){
        super(context, R.layout.detail_row, stockDetails);
        this.context = context;
    }

    // Formats each listview item to display stock details
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("StockAdapter", "Called StockAdapter getView");
        LayoutInflater inflater = LayoutInflater.from(getContext());


        View detailRow = inflater.inflate(R.layout.detail_row, parent, false);

        typeTextView = detailRow.findViewById(R.id.typeTextView);
        valueTextView = detailRow.findViewById(R.id.valueTextView);


        typeTextView.setText((String)((StockData)getItem(position)).getType());
        valueTextView.setText((String)((StockData)getItem(position)).getValue());

        return detailRow;

    }

}
