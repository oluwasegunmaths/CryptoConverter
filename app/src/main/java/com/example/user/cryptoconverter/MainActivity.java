package com.example.user.cryptoconverter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Currency>>, CurrencyAdapter.CurrencyAdapterOnClickHandler {
    private static final String LOG_TAG = MainActivity.class.getName();
    //base url string which currency values from the preference class are added to
    private static final String BASE_CRYPTO_API = "https://min-api.cryptocompare.com/data/pricemulti?fsyms=BTC,ETH&tsyms=";
    public String cryptoUrlString;
    //text view to hold empty return state or no network status
    TextView mTextView;
    //list of currencies
    List<Currency> currencyList;
    //adapter which helps in populating the views
    private CurrencyAdapter currencyAdapter;

    @Override
    protected void onPause() {
        super.onPause();
        //this destroys the loader and prevents automatic refreshing of the main screen based on url from a previous instance
        getSupportLoaderManager().destroyLoader(1);
        //this prevents the text view from displaying a previously set text
        mTextView.setText("");
    }

    //on resume is overriden and network calls are made here
    //this makes the home screen be refreshed immediately based on the user currency preferences in the preference screen even if the user presses back button
    //this is necessary to query the api again whenever user navigates back to home screen because the conversion values change very frequently
    @Override
    protected void onResume() {
        super.onResume();
        View barView = findViewById(R.id.main_progress_bar);
        //get an instance of the connectivity manager class to determine whether there's internet connection or not
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            cryptoUrlString = BASE_CRYPTO_API;
            for (int i = 0; i < 20; i++) {
                //if statements checks if the current currency is selected in the shared preference class and it is added to the query url in the body of the if statement
                //this prevents requesting for data that wont be used to populate the views
                if (sharedPreferences.getBoolean(String.valueOf(i), false)) {
                    cryptoUrlString = cryptoUrlString + CurrencyUtils.extractAbbreviatedCurrencyFromString(CurrencyUtils.getCurrency(i)) + ",";
                }
            }
            //this checks whether user has selected any currency in the preference class
            if (cryptoUrlString.equals(BASE_CRYPTO_API)) {
                //if user hasnt selected, it displays a message prompting the user to click the fab button
                barView.setVisibility(View.GONE);
                mTextView.setText(R.string.no_currency_selected);
            } else {
                //makes progress bar visible if activity is returned to from a child activity and network requests are made
                barView.setVisibility(View.VISIBLE);
                //if user has selected a currency, it initializes the loader to make network requests
                getSupportLoaderManager().initLoader(1, null, this);
            }
        } else {
            barView.setVisibility(View.GONE);
            //set the text as no internet connection when user is not connected to the internet
            mTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CurrencySelectionPreferenceActivity.class);
                startActivity(intent);
            }
        });


        // Find a reference to the recycler view in the layout
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        currencyAdapter = new CurrencyAdapter(this, this);
        // Set the adapter on the recycler view
        // so the list can be populated in the user interface
        mRecyclerView.setAdapter(currencyAdapter);
        mTextView = (TextView) findViewById(R.id.empty_text);


    }

    // sets up the Uri based on the parameters in the sharedPreferences and uses this Uri to instantiate a new DeveloperLoader instance
    @Override
    public Loader<List<Currency>> onCreateLoader(int id, Bundle args) {
        //this simple string algorithm removes final 'comma' sign at the end of the url
        cryptoUrlString = cryptoUrlString.substring(0, cryptoUrlString.length() - 1);
        return new CurrencyLoader(this, cryptoUrlString);
    }

    //sets the content of the adapter based on the data fetched from the internet
    @Override
    public void onLoadFinished(Loader<List<Currency>> loader, List<Currency> data) {
        currencyList = data;
        currencyAdapter.setCurrencyList(null);
        View barView = findViewById(R.id.main_progress_bar);
        barView.setVisibility(View.GONE);


        if (data != null && !data.isEmpty()) {
            currencyAdapter.setCurrencyList(currencyList);
        } else {
            //sets to a default string if no data retrieved from the net
            mTextView.setText(R.string.no_currencies);
        }

    }

    //resets the loader when a new activity is being called
    @Override
    public void onLoaderReset(Loader<List<Currency>> loader) {
        currencyAdapter.setCurrencyList(null);

    }

    @Override
    public void onClick(int position) {
        Currency currentCurrency = currencyList.get(position);
        Intent intent = new Intent(MainActivity.this, CurrencyConversionActivity.class);
        intent.putExtra("currency", currentCurrency.getCurrency());
        intent.putExtra("btcValue", currentCurrency.getBtcValue());
        intent.putExtra("ethValue", currentCurrency.getEthValue());
        startActivity(intent);
    }
}
