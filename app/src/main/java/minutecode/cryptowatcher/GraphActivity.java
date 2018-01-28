package minutecode.cryptowatcher;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import minutecode.cryptowatcher.model.CandleData;
import minutecode.cryptowatcher.model.CandleDataResponse;
import minutecode.cryptowatcher.model.Config;
import minutecode.cryptowatcher.model.Investment;

public class GraphActivity extends AppCompatActivity {

    private Investment investment;
    private CandleDataResponse response;
    private List<Entry> candleEntries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        investment = getIntent().getParcelableExtra("ticker");

        final LineChart chart = findViewById(R.id.candlestick_chart);
        Description desc = new Description();
        desc.setText("Daily price data USD");
        chart.setDescription(desc);

        Ion.with(this)
                .load(Config.baseAPIUrl + Config.histoDay + investment.getReceivedToken().getSymbol() + Config.singleToSym + "USD" + Config.limit + "190")
                .as(new TypeToken<CandleDataResponse>() {
                })
                .setCallback(new FutureCallback<CandleDataResponse>() {
                    @Override
                    public void onCompleted(Exception e, CandleDataResponse result) {
                        response = result;

                        for (CandleData data : response.getData()) {
                            Entry entry = new Entry(data.getTime(), (float) data.getClose());
                            candleEntries.add(entry);
                        }

                        LineDataSet set = new LineDataSet(candleEntries, "price");
                        set.setValueTextColor(Color.DKGRAY);
                        set.setCircleColor(ContextCompat.getColor(GraphActivity.this, R.color.colorAccent));
                        set.setColor(ContextCompat.getColor(GraphActivity.this, R.color.colorAccent));
                        set.setCircleRadius(1.75f);
                        set.setCircleColorHole(ContextCompat.getColor(GraphActivity.this, R.color.colorAccent));
                        set.setDrawFilled(true);

                        set.setHighlightLineWidth(1.5f);

                        LineData lineData = new LineData(set);
                        chart.setData(lineData);
                        chart.invalidate();
                    }
                });


    }
}
