package com.jayavijayjayavelu.stockwatch;

import java.io.Serializable;

/**
 * Created by jayavijayjayavelu on 3/8/17.
 */

public class Stock implements Serializable {

    public String symbol;
    public String company;
    public double price;
    public double change;
    public String changePercentage;


    public Stock(String symbol, String company) {
        this.symbol = symbol;
        this.company = company;
    }
    public Stock(String symbol, double price, double change, String changePercentage) {
        this.symbol = symbol;
        this.price = price;
        this.change = change;
        this.changePercentage=changePercentage;
    }


    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getChange() {
        return change;
    }

    public void setChange(double change) {
        this.change = change;
    }

    public String getChangePercentage() {
        return changePercentage;
    }

    public void setChangePercentage(String changePercentage) {
        this.changePercentage = changePercentage;
    }
    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

}
