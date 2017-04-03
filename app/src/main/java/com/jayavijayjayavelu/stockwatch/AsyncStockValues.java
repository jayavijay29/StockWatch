package com.jayavijayjayavelu.stockwatch;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by jayavijayjayavelu on 3/18/17.
 */

public class AsyncStockValues extends AsyncTask<String, Integer, String> {
    private MainActivity mainActivity;
    private int count;
    private RadioButton rb;
    private RadioGroup rg;
    private TextView tv;
    private ArrayList<Stock> stockList;
    private RecyclerView recyclerView;
    private StockAdapter sAdapter;

    public String dataURL =
            "http://finance.google.com/finance/info?client=ig&q=";
    private static final String TAG = "AsyncStockValueLoader";
    private Context context;

    public AsyncStockValues(MainActivity ma) {
        mainActivity = ma;
        this.context = ma;
    }

    @Override
    protected void onPreExecute() {
        //Toast.makeText(mainActivity, "Loading Stock Value Data...", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPostExecute(String s) {
        s=s.replace("//","");
        stockList = parseJSON(s);
        AsyncStockName asyncStockName = new AsyncStockName(mainActivity);
        for(int i=0;i<MainActivity.stockList.size();i++){
            if(MainActivity.stockList.get(i).getSymbol().equals(stockList.get(0).getSymbol())){
                MainActivity.stockList.get(i).setChange(stockList.get(0).getChange());
                MainActivity.stockList.get(i).setChangePercentage(stockList.get(0).getChangePercentage());
                MainActivity.stockList.get(i).setPrice(stockList.get(0).getPrice());
            }
        }
        MainActivity main = new MainActivity();
        recyclerView = (RecyclerView) mainActivity.findViewById(R.id.recycler);
        sAdapter = new StockAdapter(main.stockList, mainActivity);
        recyclerView.setAdapter(sAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(mainActivity));
    }


    @Override
    protected String doInBackground(String... params) {
        Uri dataUri = Uri.parse(dataURL);

        String urlToUse = dataUri.toString();
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            Log.e(TAG, "doInBackground: ", e);
            return null;
        }
        return sb.toString();
    }


    private ArrayList<Stock> parseJSON(String s) {

        ArrayList<Stock> stockList = new ArrayList<>();
        try {
            JSONArray jObjMain = new JSONArray(s);
            count = jObjMain.length();

            for (int i = 0; i < jObjMain.length(); i++) {
                JSONObject jStock = (JSONObject) jObjMain.get(i);
                String symbol = jStock.getString("t");
                String price = jStock.getString("l");
                String change = jStock.getString("c");
                String changePercentage = jStock.getString("cp");
                changePercentage = "(" + changePercentage + "%)";
                stockList.add(new Stock(symbol, Double.parseDouble(price), Double.parseDouble(change),changePercentage));
            }
            return stockList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
