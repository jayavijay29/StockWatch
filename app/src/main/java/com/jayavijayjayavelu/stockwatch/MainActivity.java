package com.jayavijayjayavelu.stockwatch;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    public static ArrayList<Stock> stockList = new ArrayList<>();
    private RecyclerView recyclerView;
    private StockAdapter sAdapter;
    private SwipeRefreshLayout swiper;
    public static String sym;
    public Stock s;
    public int pos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            ArrayList<Stock> list = DatabaseHandler.getInstance(this).loadStocks();
            Collections.sort(list, new Comparator<Stock>() {
                @Override
                public int compare(Stock o1, Stock o2) {
                    int one = (int)o1.symbol.charAt(0);
                    int two = (int)o2.symbol.charAt(0);
                    return one- two;
                }
            });
            if(!list.isEmpty()) {
                stockList = list;
                for (int i =0;i<list.size();i++) {
                    AsyncStockValues asyncStockValues = new AsyncStockValues(MainActivity.this);
                    asyncStockValues.dataURL=asyncStockValues.dataURL+list.get(i).getSymbol();
                    asyncStockValues.execute();
                }
            }
            swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
            swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    doRefresh();
                }
            });
        } else {
            Toast.makeText(this, "You are NOT Connected to the Internet!", Toast.LENGTH_LONG).show();
        }
        swiper = (SwipeRefreshLayout) findViewById(R.id.swiper);
        swiper.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doRefresh();
            }
        });
    }
    private void doRefresh() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            ArrayList<Stock> list = DatabaseHandler.getInstance(this).loadStocks();
            Collections.sort(list, new Comparator<Stock>() {
                @Override
                public int compare(Stock o1, Stock o2) {
                    int one = (int)o1.symbol.charAt(0);
                    int two = (int)o2.symbol.charAt(0);
                    return one- two;
                }
            });
            if (!list.isEmpty()) {
                stockList = list;
                for (int i = 0; i < list.size(); i++) {
                    AsyncStockValues asyncStockValues = new AsyncStockValues(MainActivity.this);
                    asyncStockValues.dataURL = asyncStockValues.dataURL + list.get(i).getSymbol();
                    asyncStockValues.execute();
                }
            }
            swiper.setRefreshing(false);
            Toast.makeText(this, "List Updated.", Toast.LENGTH_SHORT).show();
        }else{
            swiper.setRefreshing(false);
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
            final TextView tv = new TextView(MainActivity.this);
            tv.setText("Stocks cannot be updated without Network Connection.");
            tv.setTextColor(Color.BLACK);
            tv.setTextSize(15);
            tv.setPadding(50,20,0,30);
            alertDialog.setView(tv);
            alertDialog.setTitle("No Network Connection");
            alertDialog.setIcon(android.R.drawable.ic_menu_search);
            alertDialog.show();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return true;
    }
    public void updateData(ArrayList<Stock> sList) {
        stockList.addAll(sList);
        sAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        pos = recyclerView.getChildLayoutPosition(v);
        s = stockList.get(pos);
        String url = "http://www.marketwatch.com/investing/stock/"+s.getSymbol();
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }


    @Override
    public boolean onLongClick(View v) {
        recyclerView=(RecyclerView)findViewById(R.id.recycler);
        pos = recyclerView.getChildLayoutPosition(v);
        s = stockList.get(pos);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_menu_delete);
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DatabaseHandler.getInstance(MainActivity.this).deleteStock(s.getSymbol());
                stockList.remove(pos);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        builder.setMessage("Delete Stock Symbol: " + stockList.get(pos).getSymbol() + " ?");
        builder.setTitle("Delete Stock");

        AlertDialog dialog = builder.create();
        dialog.show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addStock:
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null && netInfo.isConnectedOrConnecting()) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    alertDialog.setTitle("STOCK SELECTION");
                    final EditText input = new EditText(MainActivity.this);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    input.setLayoutParams(lp);
                    alertDialog.setView(input);
                    alertDialog.setMessage("Please enter a Stock Symbol:");
                    alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            AsyncStockName asyncStockName = new AsyncStockName(MainActivity.this);
                            asyncStockName.dataURL = asyncStockName.dataURL + input.getText().toString();
                            sym = input.getText().toString();
                            asyncStockName.execute();
                            sAdapter = new StockAdapter(stockList, MainActivity.this);
                            DatabaseHandler.getInstance(MainActivity.this);
                        }
                    }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    }).show();
                    return true;
                }else{
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                    final TextView tv = new TextView(MainActivity.this);
                    tv.setText("Stocks cannot be added without Network Connection.");
                    tv.setTextColor(Color.BLACK);
                    tv.setTextSize(15);
                    tv.setPadding(50,20,0,30);
                    alertDialog.setView(tv);
                    alertDialog.setTitle("No Network Connection");
                    alertDialog.setIcon(android.R.drawable.ic_menu_search);
                    alertDialog.show();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
