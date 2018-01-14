package minutecode.cryptowatcher;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import minutecode.cryptowatcher.model.Config;
import minutecode.cryptowatcher.model.CryptoCompareCoinListResponse;
import minutecode.cryptowatcher.model.CryptoCompareTicker;
import minutecode.cryptowatcher.view.CryptoCompareTickerAdapter;

public class AddTokenActivity extends AppCompatActivity {

    private AutoCompleteTextView tickerNames;
    private AutoCompleteTextView investmentTickerSymbol;
    private EditText investedAmount;
    private EditText receivedAmount;
    private Button addTokenButton;

    private List<CryptoCompareTicker> tickerList;
    private CryptoCompareTickerAdapter cmcAdapterInvestment;
    private CryptoCompareTickerAdapter cmcAdapterInvestmentSymbol;

    private CryptoCompareTicker selectedTicker;
    private CryptoCompareTicker investedTicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_token);

        tickerNames = findViewById(R.id.token_name_autocomplete_textview);
        investmentTickerSymbol = findViewById(R.id.investment_currency_name_autocomplete_textview);
        investedAmount = findViewById(R.id.invested_amount_edit_text);
        receivedAmount = findViewById(R.id.received_amount_edit_text);
        addTokenButton = findViewById(R.id.add_token);

        tickerList = new ArrayList<>();

        Ion.with(this)
                .load(Config.coinListUrl)
                .as(new TypeToken<CryptoCompareCoinListResponse>() {
                })
                .setCallback(new FutureCallback<CryptoCompareCoinListResponse>() {
                    @Override
                    public void onCompleted(Exception e, CryptoCompareCoinListResponse result) {
                        tickerList = result.getCoin();
                        cmcAdapterInvestment = new CryptoCompareTickerAdapter(
                                getApplicationContext(),
                                R.layout.crypto_compare_ticker_adapter_layout,
                                tickerList,
                                CryptoCompareTickerAdapter.TickerDescription.NAME
                        );
                        cmcAdapterInvestmentSymbol = new CryptoCompareTickerAdapter(
                                getApplicationContext(),
                                R.layout.crypto_compare_ticker_adapter_layout,
                                tickerList,
                                CryptoCompareTickerAdapter.TickerDescription.SYMBOL
                        );
                        tickerNames.setAdapter(cmcAdapterInvestment);
                        investmentTickerSymbol.setAdapter(cmcAdapterInvestmentSymbol);
                    }
                });

        tickerNames.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectedTicker = cmcAdapterInvestment.getItem(position);
            }
        });

        investmentTickerSymbol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                investedTicker = cmcAdapterInvestmentSymbol.getItem(position);
            }
        });

        addTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("receivedTicker", (Parcelable) selectedTicker);
                resultIntent.putExtra("investedTicker", (Parcelable) investedTicker);
                resultIntent.putExtra("receivedAmount", Double.valueOf(receivedAmount.getText().toString()));
                resultIntent.putExtra("investedAmount", Double.valueOf(investedAmount.getText().toString()));
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
