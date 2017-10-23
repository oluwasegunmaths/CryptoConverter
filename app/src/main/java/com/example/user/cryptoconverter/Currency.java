package com.example.user.cryptoconverter;

/**
 * Created by USER on 10/10/2017.
 */

public class Currency {

    // to hold conversion value of current currency to BTC
    private double btcValue;

    //to hold conversion value of current currency to ETH
    private double ethValue;

    //to hold full currency string including country name and currency code
    private String currency;

    public Currency(String currentCurrency, double btc, double eth) {
        currency = currentCurrency;
        btcValue = btc;
        ethValue = eth;

    }

    //returns the full currency string for the current currency
    public String getCurrency() {
        return currency;
    }

    //returns the BTC conversion value for the current currency
    public double getBtcValue() {
        return btcValue;
    }

    //returns the ETH conversion value  for the current currency
    public double getEthValue() {
        return ethValue;
    }

}


