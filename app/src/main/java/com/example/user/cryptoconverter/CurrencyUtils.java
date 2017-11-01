package com.example.user.cryptoconverter;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by USER on 10/10/2017.
 */

public final class CurrencyUtils {
    // Tag for the log messages
    private static final String LOG_TAG = CurrencyUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link CurrencyUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name CurrencyUtils (and an object instance of CurrencyUtils is not needed).
     */
    private CurrencyUtils() {

    }

    /**
     * Query the crypto api and return a list of {@link Currency} objects that has been built up from
     * parsing a JSON response.
     */
    //calls on a private helper method (getJsonString) and finally returns a list of currency objects from the  JSON object gotten from the internet
    protected static List<Currency> getCurrenciesAndConversionValues(String urlString) {
        // query the crypto api and return a raw json string
        String jsonString = getJsonString(urlString);
        //return early if json string is empty_text or null
        if (TextUtils.isEmpty(jsonString)) {
            return null;
        }
        // Create an empty_text ArrayList that we can start adding currencies to
        List<Currency> currencies = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonString);
            JSONObject btcObject = baseJsonResponse.optJSONObject("BTC");
            JSONObject ethObject = baseJsonResponse.optJSONObject("ETH");

            for (int i = 0; i < 20; i++) {
                String abbreviatedCurrency = extractAbbreviatedCurrencyFromString(getCurrency(i));
                if (btcObject.has(abbreviatedCurrency)) {
                    String currencyString = getCurrency(i);
                    double btcValue = btcObject.getDouble(abbreviatedCurrency);
                    double ethValue = ethObject.getDouble(abbreviatedCurrency);
                    currencies.add(new Currency(currencyString, btcValue, ethValue));
                }
            }


        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the currency JSON results", e);
        }

        // Return the list of currencies and conversion values
        return currencies;
    }

    //takes in as input a string which points to the Url and returns the processed JSON result
    private static String getJsonString(String urlString) {
        //create URL object
        URL url = makeUrl(urlString);
        //perform HTTP request to the URL and receive a JSON response back
        String jsonOutputString = null;
        try {
            jsonOutputString = getResponseFromHttpsUrl(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "error closing input stream", e);
        }
        // return the json response
        return jsonOutputString;
    }

    //converts the String to a Url object
    private static URL makeUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException ex) {
            Log.e(LOG_TAG, "ERROR CREATING URL: ", ex);
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTPS response.
     *
     * @param url The URL to fetch the HTTPS response from.
     * @return The contents of the HTTPS response.
     * @throws IOException Related to network and stream reading
     */
    private static String getResponseFromHttpsUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    //called to return the appropriate full currency string according to the way they are arranged in the url
    protected static String getCurrency(int position) {
        switch (position) {
            case 0:
                return "Nigerian naira-NGN";
            case 1:
                return "US dollars-USD";
            case 2:
                return "European union-EUR";
            case 3:
                return "British pounds-GBP";
            case 4:
                return "Japanese yen-JPY";
            case 5:
                return "Russian rouble-RUB";
            case 6:
                return "Singapore dollar-SGD";
            case 7:
                return "Poland zloty-PLN";
            case 8:
                return "Australian dollar-AUD";
            case 9:
                return "Canadian dollar-CAD";
            case 10:
                return "Swedish krona-SEK";
            case 11:
                return "UAE dirham-AED";
            case 12:
                return "Indian rupee-INR";
            case 13:
                return "Mexican peso-MXN";
            case 14:
                return "Romanian new lei-RON";
            case 15:
                return "Switzerland franc-CHF";
            case 16:
                return "Philippines peso-PHP";
            case 17:
                return "Hong kong dollar-HKD";
            case 18:
                return "Czech koruna-CZK";
            case 19:
                return "Venezuelan bolivar-VEF";
            default:
                throw new IllegalArgumentException("Integer does not match any currency");
        }
    }

    //parses the full currency string to extract the internationally and java recognized 3 letter abbreviated currency string
    protected static String extractAbbreviatedCurrencyFromString(String currencyString) {
        int index = currencyString.indexOf("-");
        index += 1;
        return currencyString.substring(index);
    }
}




