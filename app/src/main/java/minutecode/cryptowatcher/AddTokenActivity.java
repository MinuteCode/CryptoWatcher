package minutecode.cryptowatcher;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import minutecode.cryptowatcher.model.Config;
import minutecode.cryptowatcher.model.CryptoCompareCoinListResponse;
import minutecode.cryptowatcher.model.CryptoCompareTicker;
import minutecode.cryptowatcher.view.CryptoCompareTickerAdapter;

public class AddTokenActivity extends AppCompatActivity {

    private AutoCompleteTextView tickerNames;
    private AutoCompleteTextView investmentTickerSymbol;
    private EditText investedAmount;
    private EditText receivedAmount;
    private EditText dateInvested;
    private TextView valueAtInvestmentDate;
    private TextView dateRecapTextView;
    private Button addTokenButton;

    private List<CryptoCompareTicker> tickerList;
    private CryptoCompareTickerAdapter cmcAdapterInvestment;
    private CryptoCompareTickerAdapter cmcAdapterInvestmentSymbol;

    private CryptoCompareTicker selectedTicker;
    private CryptoCompareTicker investedTicker;

    private Date investmentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_token);

        tickerNames = findViewById(R.id.token_name_autocomplete_textview);
        investmentTickerSymbol = findViewById(R.id.investment_currency_name_autocomplete_textview);
        investedAmount = findViewById(R.id.invested_amount_edit_text);
        receivedAmount = findViewById(R.id.received_amount_edit_text);
        addTokenButton = findViewById(R.id.add_token);
        dateInvested = findViewById(R.id.date_edit_text);
        valueAtInvestmentDate = findViewById(R.id.value_at_investment_date);
        dateRecapTextView = findViewById(R.id.date_recap_textview);

        dateInvested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddTokenActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.YEAR, year);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        dateInvested.setText(sdf.format(calendar.getTime()));
                        dateRecapTextView.setText(sdf.format(calendar.getTime()));


                        investmentDate = calendar.getTime();

                        String getHistoricPriceUrl = Config.baseAPIUrl
                                + Config.conversionHistoricalUrlStart
                                + investedTicker.getSymbol()
                                + Config.conversionUrlMiddle
                                + "USD"
                                + Config.timeStampParameter
                                + investmentDate.getTime() / 1000;

                        Ion.with(AddTokenActivity.this)
                                .load(getHistoricPriceUrl)
                                .asJsonObject()
                                .setCallback(new FutureCallback<JsonObject>() {
                                    @Override
                                    public void onCompleted(Exception e, JsonObject result) {
                                        if (e == null) {
                                            double investedTokenConversionRate = result.get(investedTicker.getSymbol())
                                                    .getAsJsonObject()
                                                    .get("USD")
                                                    .getAsDouble();
                                            investedTicker.setOriginalConversionRateFiat(investedTokenConversionRate);
                                            valueAtInvestmentDate.setText(Double.toString(investedTokenConversionRate * Double.valueOf(investedAmount.getText().toString())) + "$");
                                        } else {
                                            investedTicker.setOriginalConversionRateFiat(0);
                                            valueAtInvestmentDate.setText(getString(R.string.not_a_number));
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }
                },
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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
                                AddTokenActivity.this,
                                R.layout.crypto_compare_ticker_adapter_layout,
                                tickerList,
                                CryptoCompareTickerAdapter.TickerDescription.NAME
                        );
                        cmcAdapterInvestmentSymbol = new CryptoCompareTickerAdapter(
                                AddTokenActivity.this,
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
                String getPriceUrl = Config.baseAPIUrl + Config.conversionUrlStart + selectedTicker.getSymbol() + Config.conversionUrlMiddle + "USD";
                Ion.with(AddTokenActivity.this)
                        .load(getPriceUrl)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (e == null) {
                                    selectedTicker.setNowConversionRateFiat(
                                            result.get("USD").getAsDouble()
                                    );
                                } else {
                                    selectedTicker.setNowConversionRateFiat(0);
                                }
                            }
                        });
            }
        });

        investmentTickerSymbol.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                investedTicker = cmcAdapterInvestmentSymbol.getItem(position);
                String getPriceUrl = Config.baseAPIUrl + Config.conversionUrlStart + investedTicker.getSymbol() + Config.conversionUrlMiddle + "USD";
                Ion.with(AddTokenActivity.this)
                        .load(getPriceUrl)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject>() {
                            @Override
                            public void onCompleted(Exception e, JsonObject result) {
                                if (e == null) {
                                    investedTicker.setNowConversionRateFiat(
                                            result.get("USD").getAsDouble()
                                    );
                                } else {
                                    investedTicker.setNowConversionRateFiat(0);
                                }
                            }
                        });
            }
        });

        addTokenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedTicker.setAmount(Double.valueOf(receivedAmount.getText().toString()));
                investedTicker.setAmount(Double.valueOf(investedAmount.getText().toString()));
                Intent resultIntent = new Intent();
                resultIntent.putExtra("receivedTicker", (Parcelable) selectedTicker);
                resultIntent.putExtra("investedTicker", (Parcelable) investedTicker);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
