package com.example.user.cryptoconverter;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

/**
 * Created by USER on 10/10/2017.
 */

public class CurrencyLoader extends AsyncTaskLoader<List<Currency>> {
    String cryptoApiQueryString;

    //initializes the crypto api url string
    public CurrencyLoader(Context context, String urlString) {
        super(context);
        cryptoApiQueryString = urlString;
    }

    //method called to call forceLoad method which in turn calls loadInBackground
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    //calls on the CurrencyUtils.getCurrenciesAndConversionValues method to get a list of Currency objects and eventually returns this list to the Currency activity loader
    @Override
    public List<Currency> loadInBackground() {
        if (cryptoApiQueryString == null) {
            return null;
        }
        List<Currency> currencies = CurrencyUtils.getCurrenciesAndConversionValues(cryptoApiQueryString);
        return currencies;

    }

}

