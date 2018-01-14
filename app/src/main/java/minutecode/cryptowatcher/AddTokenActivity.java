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

import minutecode.cryptowatcher.model.CMCTicker;
import minutecode.cryptowatcher.view.CMCTickerAdapter;

public class AddTokenActivity extends AppCompatActivity {

    private AutoCompleteTextView tickerNames;
    private AutoCompleteTextView investmentTickerSymbol;
    private EditText investedAmount;
    private EditText receivedAmount;
    private Button addTokenButton;

    private List<CMCTicker> tickerList;
    private CMCTickerAdapter cmcAdapterInvestment;
    private CMCTickerAdapter cmcAdapterInvestmentSymbol;

    private CMCTicker selectedTicker;
    private CMCTicker investedTicker;

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
        tickerNames.setAdapter(cmcAdapterInvestment);

        Ion.with(this)
                .load("https://api.coinmarketcap.com/v1/ticker/?limit=0")
                .as(new TypeToken<List<CMCTicker>>(){})
                .setCallback(new FutureCallback<List<CMCTicker>>() {
                    @Override
                    public void onCompleted(Exception e, List<CMCTicker> result) {
                        tickerList = result;
                        cmcAdapterInvestment = new CMCTickerAdapter(getApplicationContext(), R.layout.cmc_ticker_adapter_layout, tickerList, CMCTickerAdapter.TickerDescription.NAME);
                        cmcAdapterInvestmentSymbol = new CMCTickerAdapter(getApplicationContext(), R.layout.cmc_ticker_adapter_layout, tickerList, CMCTickerAdapter.TickerDescription.SYMBOL);
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
                resultIntent.putExtra("investedAmount", Double.valueOf(investedAmount.getText().toString()));
                resultIntent.putExtra("receivedAmount", Double.valueOf(receivedAmount.getText().toString()));
                setResult(RESULT_OK,resultIntent);
                finish();
            }
        });
    }
}
