package com.jayavijayjayavelu.stockwatch;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jayavijayjayavelu on 3/8/17.
 */

public class MyViewHolder extends RecyclerView.ViewHolder{

    public TextView symbol;
    public TextView company;
    public TextView price;
    public TextView change;
    public TextView changePercentage;
    public ImageView arrow;

    public MyViewHolder(View view) {
        super(view);
        symbol = (TextView) view.findViewById(R.id.symbol);
        company = (TextView) view.findViewById(R.id.company);
        price = (TextView) view.findViewById(R.id.price);
        change = (TextView) view.findViewById(R.id.change);
        changePercentage = (TextView) view.findViewById(R.id.changePercentage);
        arrow =(ImageView) view.findViewById(R.id.imageView);
    }
}
