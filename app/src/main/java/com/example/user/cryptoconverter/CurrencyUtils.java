package com.example.user.cryptoconverter;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
            jsonOutputString = getResponse(url);
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

    // opens up an internet connection
    //use helper 'readStream' method to decode output into a String containing the raw json
    //return the json as a string
    private static String getResponse(URL url) throws IOException {

        String jsonResponse = "";
        // return early if url is null
        if (url == null) {
            return jsonResponse;
        }
        InputStream inputStream = null;
        HttpsURLConnection httpsURLConnection = null;

        try {
            httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setReadTimeout(10000);
            httpsURLConnection.setConnectTimeout(15000);
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.connect();
            //read input stream and parse response if request was successful(response code 200)
            if (httpsURLConnection.getResponseCode() == 200) {
                inputStream = httpsURLConnection.getInputStream();
                jsonResponse = readStream(inputStream);
            } else {
                Log.e(LOG_TAG, "ERROR RESPONSE CODE :" + httpsURLConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "problem retrieving currency json results", e);
        } finally {
            if (httpsURLConnection != null) {
                httpsURLConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    //converts the input stream from the internet connection to a string containing the whole unmodified json response
    private static String readStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = bufferedReader.readLine();
            }
        }
        return stringBuilder.toString();
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
    public static String extractAbbreviatedCurrencyFromString(String currencyString) {
        int index = currencyString.indexOf("-");
        index += 1;
        return currencyString.substring(index);
    }
}




