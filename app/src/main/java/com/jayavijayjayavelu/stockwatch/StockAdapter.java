package com.jayavijayjayavelu.stockwatch;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by jayavijayjayavelu on 3/8/17.
 */

public class StockAdapter extends RecyclerView.Adapter<MyViewHolder> {

    private static final String TAG = "StockAdapter";
    private List<Stock> stockList;
    private MainActivity mainAct;

    public StockAdapter(List<Stock> empList, MainActivity ma) {
        this.stockList = empList;
        mainAct = ma;
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stock_entry, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Stock stock = stockList.get(position);
        holder.symbol.setText(stock.getSymbol());
        holder.company.setText(stock.getCompany());
        holder.price.setText(String.valueOf(stock.getPrice()));
        holder.change.setText(String.valueOf(stock.getChange()));
        holder.changePercentage.setText(String.valueOf(stock.getChangePercentage()));
        String temp =String.valueOf(stock.getChangePercentage());
        temp = temp.replace("(","");
        temp = temp.replace(")","");
        temp = temp.replace("%","");
        if(!temp.equals("null")) {
            if (Double.parseDouble(temp) < 0) {
                holder.symbol.setTextColor(Color.RED);
                holder.company.setTextColor(Color.RED);
                holder.price.setTextColor(Color.RED);
                holder.change.setTextColor(Color.RED);
                holder.changePercentage.setTextColor(Color.RED);
                holder.arrow.setImageResource(R.drawable.down);
            } else {
                holder.symbol.setTextColor(Color.GREEN);
                holder.company.setTextColor(Color.GREEN);
                holder.price.setTextColor(Color.GREEN);
                holder.change.setTextColor(Color.GREEN);
                holder.changePercentage.setTextColor(Color.GREEN);
                holder.arrow.setImageResource(R.drawable.up);
            }
        }
    }

    @Override
    public int getItemCount() {
        return stockList.size();
    }

}
