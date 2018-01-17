package minutecode.cryptowatcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import minutecode.cryptowatcher.model.Config;
import minutecode.cryptowatcher.model.CryptoCompareTicker;
import minutecode.cryptowatcher.model.Investment;
import minutecode.cryptowatcher.view.TokenRecapRecyclerAdapter;

public class MainHome extends AppCompatActivity {

    final static int ADD_TOKEN_REQUEST = 1;

    private RecyclerView addedTokensRecyclerView;
    private TokenRecapRecyclerAdapter tokenRecapAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noIcoText;

    private List<Investment> investmentList = new ArrayList<>();
    private int updatedInvestments = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FileInputStream fis = null;
        try {
            fis = openFileInput("investments.bak");
            ObjectInputStream ois = new ObjectInputStream(fis);
            investmentList = (ArrayList<Investment>) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTokenAddition();
            }
        });

        addedTokensRecyclerView = findViewById(R.id.added_token_recycler_view);
        addedTokensRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tokenRecapAdapter = new TokenRecapRecyclerAdapter(investmentList);
        addedTokensRecyclerView.setAdapter(tokenRecapAdapter);

        swipeRefreshLayout = findViewById(R.id.refresh_tokens_price_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                for (int i = 0; i < investmentList.size(); i++) {
                    final Investment investment = investmentList.get(i);
                    final int finalI = i;
                    Ion.with(MainHome.this)
                            .load(Config.baseAPIUrl + Config.conversionUrlStart + investment.getReceivedToken().getSymbol() + Config.conversionUrlMiddle + "USD")
                            .asJsonObject()
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    double conversionDollar = result.get("USD").getAsDouble();
                                    conversionDollar *= investment.getReceivedAmount();

                                    investment.setTotalFiatAmount(conversionDollar);

                                    tokenRecapAdapter.notifyItemChanged(finalI);
                                    updatedInvestments++;
                                    if (updatedInvestments == investmentList.size()) {
                                        updatedInvestments = 0;
                                        saveInvestmentListToFile();
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                swipeRefreshLayout.setRefreshing(false);
                                            }
                                        });
                                    }
                                }
                            });
                }
            }
        });

        noIcoText = findViewById(R.id.no_ico_text);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (investmentList.size() > 0) {
            noIcoText.setVisibility(View.INVISIBLE);
        } else {
            noIcoText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void goToTokenAddition() {
        Intent goToTokenAddition = new Intent(this, AddTokenActivity.class);
        startActivityForResult(goToTokenAddition, ADD_TOKEN_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ADD_TOKEN_REQUEST:
                if (resultCode == RESULT_OK) {
                    final CryptoCompareTicker addedTicker = data.getParcelableExtra("receivedTicker");
                    final CryptoCompareTicker investedTicker = data.getParcelableExtra("investedTicker");
                    final double investedAmount = data.getDoubleExtra("investedAmount", 0.0);
                    final double receivedAmount = data.getDoubleExtra("receivedAmount", 0.0);

                    Ion.with(this)
                            .load(Config.baseAPIUrl + Config.conversionUrlStart + addedTicker.getSymbol() + Config.conversionUrlMiddle + "USD")
                            .as(new TypeToken<JsonObject>() {
                            })
                            .setCallback(new FutureCallback<JsonObject>() {
                                @Override
                                public void onCompleted(Exception e, JsonObject result) {
                                    Investment investment = new Investment(addedTicker, receivedAmount, investedTicker, investedAmount);
                                    investment.computeReceivedDollarConversion(result.get("USD").getAsDouble());
                                    investment.computeTokenOutput();
                                    investmentList.add(investment);
                                    tokenRecapAdapter.notifyItemInserted(investmentList.size() - 1);
                                    saveInvestmentListToFile();
                                }
                            });
                }
        }
    }

    private void saveInvestmentListToFile() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                ObjectOutputStream oos = null;
                try {
                    fos = openFileOutput("investments.bak", MODE_PRIVATE);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(investmentList);
                    oos.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        fos.close();
                        if (oos != null) {
                            oos.flush();
                            oos.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

}
