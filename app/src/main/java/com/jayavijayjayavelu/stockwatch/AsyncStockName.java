package com.jayavijayjayavelu.stockwatch;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
import android.content.Intent;
import java.util.List;

/**
 * Created by jayavijayjayavelu on 3/8/17.
 */

public class AsyncStockName extends AsyncTask<String, Integer, String> {
    private MainActivity mainActivity;
    private int count;
    private RadioButton rb;
    private RadioGroup rg;
    private TextView tv;
    public ArrayList<Stock> stockList;

    public String dataURL =
            "http://stocksearchapi.com/api/?api_key=b18326c13d4970ce1199f31a10b28d6bd3efda0a&search_text=";
    private static final String TAG = "AsyncStockLoader";
    private Context context;

    public AsyncStockName(MainActivity ma) {
        mainActivity = ma;
        this.context = ma;
    }

    @Override
    protected void onPreExecute() {
        //Toast.makeText(mainActivity, "Loading Stock Data...", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onPostExecute(String s) {
        if(!s.equals("")) {
            stockList = parseJSON(s);
            if (!stockList.isEmpty()) {
                if (stockList.size() > 1) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
                    alertDialog.setTitle("MAKE A SELECTION");
                    final RadioGroup rgp = new RadioGroup(mainActivity);
                    rgp.setOrientation(LinearLayout.VERTICAL);
                    for (int i = 0; i < stockList.size(); i++) {
                        final RadioButton rb = new RadioButton(mainActivity);
                        rb.setId(i);
                        rb.setText(stockList.get(i).getSymbol() + "-" + stockList.get(i).getCompany());
                        rb.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<Stock> stockListTemp = new ArrayList<Stock>();
                                int id = rgp.getCheckedRadioButtonId();
                                String symbol = stockList.get(id).getSymbol();
                                String company = stockList.get(id).getCompany();
                                int count=0;
                                for(int i=0;i<MainActivity.stockList.size();i++){
                                    if(MainActivity.stockList.get(i).getSymbol().toString().equals(symbol))
                                        count++;
                                }
                                if(count==0){
                                    stockListTemp.add(new Stock(symbol, company));
                                    mainActivity.updateData(stockListTemp);
                                    AsyncStockValues asyncStockValues = new AsyncStockValues(mainActivity);
                                    asyncStockValues.dataURL = asyncStockValues.dataURL + stockList.get(id).getSymbol().toString();
                                    DatabaseHandler.getInstance(mainActivity).addStock(new Stock(symbol, company));
                                    asyncStockValues.execute();
                                    Intent intent = new Intent(context,MainActivity.class);
                                    context.startActivity(intent);
                                }else{
                                    final AlertDialog.Builder alertDialog1 = new AlertDialog.Builder(mainActivity);
                                    final TextView tv = new TextView(mainActivity);
                                    tv.setText("Stock Symbol "+symbol+" is already displayed.");
                                    tv.setTextColor(Color.BLACK);
                                    tv.setTextSize(15);
                                    tv.setPadding(50,20,0,30);
                                    alertDialog1.setView(tv);
                                    alertDialog1.setTitle("Duplicate Stock");
                                    alertDialog1.setIcon(android.R.drawable.ic_dialog_alert);
                                    alertDialog1.show();
                                }
                            }
                        });
                        rb.setButtonDrawable(android.R.color.transparent);
                        rb.setTextSize(15);
                        rb.setPadding(50,10,10,10);
                        rb.setClickable(true);
                        rgp.addView(rb);
                    }
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    rgp.setLayoutParams(lp);
                    alertDialog.setView(rgp);
                    alertDialog.setPositiveButton("NEVER MIND", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            alertDialog.setCancelable(true);
                        }
                    }).show();

                }else{
                    int count =0;
                    for(int i=0;i<MainActivity.stockList.size();i++){
                        if(MainActivity.stockList.get(i).getSymbol().equals(MainActivity.sym))
                            count++;
                    }if(count>0){
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(mainActivity);
                        final TextView tv = new TextView(mainActivity);
                        tv.setText("Stock Symbol "+MainActivity.sym+" is already displayed.");
                        tv.setTextColor(Color.BLACK);
                        tv.setTextSize(15);
                        tv.setPadding(50,20,0,30);
                        alertDialog.setView(tv);
                        alertDialog.setTitle("Duplicate Stock");
                        alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
                        alertDialog.show();
                    }else{
                        String symbol = stockList.get(0).getSymbol();
                        String company = stockList.get(0).getCompany();
                        ArrayList<Stock> stockListTemp = new ArrayList<Stock>();
                        stockListTemp.add(new Stock(symbol, company));
                        mainActivity.updateData(stockListTemp);
                        AsyncStockValues asyncStockValues = new AsyncStockValues(mainActivity);
                        asyncStockValues.dataURL = asyncStockValues.dataURL + stockList.get(0).getSymbol().toString();
                        DatabaseHandler.getInstance(mainActivity).addStock(new Stock(symbol, company));
                        asyncStockValues.execute();
                    }
                }
            }
        }
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

            Intent intent = new Intent(context,DialogActivity.class);
            String header = "Symbol Not Found : "+MainActivity.sym;
            intent.putExtra("header",header);
            String body = "Data for Stock Symbol";
            intent.putExtra("body",body);
            context.startActivity(intent);
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
                String symbol = jStock.getString("company_symbol");
                String company = jStock.getString("company_name");
                stockList.add(new Stock(symbol, company));

            }
            return stockList;
        } catch (Exception e) {
            Log.d(TAG, "parseJSON: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
