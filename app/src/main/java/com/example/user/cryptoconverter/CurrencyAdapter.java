package com.example.user.cryptoconverter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by USER on 10/10/2017.
 */

public class CurrencyAdapter extends RecyclerView.Adapter<CurrencyAdapter.CurrencyAdapterViewHolder> {
    //to hold the currency list as a global variable
    private List<Currency> mCurrencyList;
    //would hold context as a global variable
    private Context mainActivityContext;
    private CurrencyAdapterOnClickHandler currencyAdapterOnClickHandler;

    public CurrencyAdapter(Context context, CurrencyAdapterOnClickHandler clickHandler) {
        mainActivityContext = context;
        currencyAdapterOnClickHandler = clickHandler;
    }

    @Override
    public CurrencyAdapter.CurrencyAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        boolean shouldAttachImmediatelyToParent = false;
        View view = inflater.inflate(R.layout.list_item, parent, shouldAttachImmediatelyToParent);
        return new CurrencyAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CurrencyAdapter.CurrencyAdapterViewHolder holder, int position) {
        holder.bind(position);

    }


    @Override
    public int getItemCount() {
        if (null == mCurrencyList) return 0;
        return mCurrencyList.size();
    }

    public void setCurrencyList(List<Currency> currencies) {
        mCurrencyList = currencies;
        notifyDataSetChanged();
    }

    //interface implemented in the main activity to handle click events
    public interface CurrencyAdapterOnClickHandler {
        void onClick(int position);
    }

    public class CurrencyAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //would hold the text view which will display the selected currency as a global variable
        TextView currencyTextView;
        //would hold the text view which will display the btc conversion as a global variable
        TextView btcValueTextView;
        //would hold the text view which will display the eth conversion as a global variable
        TextView ethValueTextView;
        //holds number format instance as a global variable
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        //would hold the list item card view as a global variable
        CardView mCardView;

        public CurrencyAdapterViewHolder(View itemView) {
            super(itemView);
            currencyTextView = (TextView) itemView.findViewById(R.id.list_view_currency_text);
            btcValueTextView = (TextView) itemView.findViewById(R.id.list_view_btc_conversion_text);
            ethValueTextView = (TextView) itemView.findViewById(R.id.list_view_eth_conversion_text);
            itemView.setOnClickListener(this);
            mCardView = (CardView) itemView.findViewById(R.id.card_view);
        }

        //method called to attach data to each of the views in the viewholder
        void bind(int listIndex) {
            //gets the current currency object
            Currency currentCurrency = mCurrencyList.get(listIndex);
            //gets the full currency string from the currency object
            String currentCurrencyString = currentCurrency.getCurrency();
            //extracts the 3 letter java recognized abbreviated currency from the full currency string using the currencyutils class method
            String abbreviatedCurrency = CurrencyUtils.extractAbbreviatedCurrencyFromString(currentCurrencyString);
            //sets the full currency string unto the currency text view
            currencyTextView.setText(currentCurrency.getCurrency());
            //sets the currency in the number format class as the 3 letter abbreviated currency
            //this is to enable adding the currency symbol automatically and commas and points to the currency double according to convention in the user locale
            numberFormat.setCurrency(java.util.Currency.getInstance(abbreviatedCurrency));
            //these two lines adds the currency symbol automatically and adds commas and points to the currency double according to convention in the user locale
            String formattedBtcValue = numberFormat.format(currentCurrency.getBtcValue());
            String formattedEthValue = numberFormat.format(currentCurrency.getEthValue());
            //these lines of code set the formatted currencies unto their respective text views
            String btcTextViewString = mainActivityContext.getResources().getString(R.string.btc_equals) + formattedBtcValue;
            String ethTextViewString = mainActivityContext.getResources().getString(R.string.eth_equals) + formattedEthValue;
            btcValueTextView.setText(btcTextViewString);
            ethValueTextView.setText(ethTextViewString);

        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            currencyAdapterOnClickHandler.onClick(clickedPosition);

        }
    }
}
