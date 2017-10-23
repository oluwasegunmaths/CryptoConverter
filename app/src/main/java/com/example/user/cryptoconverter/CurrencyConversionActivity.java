package com.example.user.cryptoconverter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;

/**
 * Created by USER on 10/10/2017.
 */

public class CurrencyConversionActivity extends AppCompatActivity {
    //a copy of decimal format object is set as a global variable
    private DecimalFormat decimalFormat;
    //this will hold the btc conversion value
    private double btcValue;
    //this will hold the eth conversion value
    private double ethValue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_conversion);
        //ensures the result is formatted to ten decimal places
        decimalFormat = new DecimalFormat("0.000000000000");
        btcValue = getIntent().getDoubleExtra("btcValue", 0);
        ethValue = getIntent().getDoubleExtra("ethValue", 0);
        String currentCurrency = getIntent().getStringExtra("currency");
        String abbreviatedCurrency = CurrencyUtils.extractAbbreviatedCurrencyFromString(currentCurrency);
        //sets the title of the conversion page dynamically depending on the currency being converted
        setTitle(getResources().getString(R.string.convert) + " " + abbreviatedCurrency);
        //private helper method
        initializeViewsAndDoTheConversion();

    }

    private void initializeViewsAndDoTheConversion() {
        final EditText currencyEditText = (EditText) findViewById(R.id.convert_currency_edit_text);
        final EditText btcValueEditText = (EditText) findViewById(R.id.btc_conversion_result_edit_text);
        final EditText ethValueEditText = (EditText) findViewById(R.id.eth_conversion_result_edit_text);
        final TextView btcText = (TextView) findViewById(R.id.btc_tv);
        final TextView ethText = (TextView) findViewById(R.id.eth_tv);
        currencyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //this automatically sets the texts in the result views anytime user inputs any value in the input edit text
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!currencyEditText.getText().toString().matches("")) {
                    //checks if visibility of the views has already been set to visible..
                    //this avoids multiple repetitive calls on views already visible
                    if (btcText.getVisibility() != View.VISIBLE) {
                        btcText.setVisibility(View.VISIBLE);
                        ethText.setVisibility(View.VISIBLE);
                        btcValueEditText.setVisibility(View.VISIBLE);
                        ethValueEditText.setVisibility(View.VISIBLE);
                    }

                    Double baseCurrency = Double.valueOf(currencyEditText.getText().toString().trim());
                    btcValueEditText.setText(String.valueOf(decimalFormat.format(baseCurrency / btcValue)));
                    ethValueEditText.setText(String.valueOf(decimalFormat.format(baseCurrency / ethValue)));
                } else {
                    btcText.setVisibility(View.INVISIBLE);
                    ethText.setVisibility(View.INVISIBLE);
                    btcValueEditText.setVisibility(View.INVISIBLE);
                    ethValueEditText.setVisibility(View.INVISIBLE);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
}
