package minutecode.cryptowatcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import minutecode.cryptowatcher.model.CryptoCompareTicker;
import minutecode.cryptowatcher.model.Investment;
import minutecode.cryptowatcher.view.TokenRecapRecyclerAdapter;

public class MainHome extends AppCompatActivity implements Investment.RefreshCallback, TokenRecapRecyclerAdapter.OnTokenListAction {

    final static int ADD_TOKEN_REQUEST = 1;

    private RecyclerView addedTokensRecyclerView;
    private TokenRecapRecyclerAdapter tokenRecapAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView noIcoText;
    private Menu optionsMenu;

    private List<Investment> investmentList = new ArrayList<>();
    private int selectedPosition;
    private CardView selectedCard;

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
            ArrayList<Map.Entry<CryptoCompareTicker, CryptoCompareTicker>> tickerList = (ArrayList<Map.Entry<CryptoCompareTicker, CryptoCompareTicker>>) ois.readObject();
            for (Map.Entry<CryptoCompareTicker, CryptoCompareTicker> tokenPair : tickerList) {
                Investment inv = new Investment(tokenPair.getKey(), tokenPair.getValue());
                inv.setRefreshListener(MainHome.this);
                investmentList.add(inv);
            }
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
        tokenRecapAdapter.setTokenListListner(this);
        addedTokensRecyclerView.setAdapter(tokenRecapAdapter);
        updateList();

        swipeRefreshLayout = findViewById(R.id.refresh_tokens_price_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateList();
                swipeRefreshLayout.setRefreshing(false);
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
        optionsMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete) {
            investmentList.remove(selectedPosition);
            tokenRecapAdapter.updateTokenList(investmentList);
            tokenRecapAdapter.notifyItemRemoved(selectedPosition);

            saveInvestmentListToFile();
            selectedPosition = 9999;
            item.setVisible(false);
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
                    addedTicker.setOriginalConversionRateFiat(investedTicker.getOriginalConversionRateFiat() * investedTicker.getAmount() / addedTicker.getAmount());

                    final Investment investment = new Investment(addedTicker, investedTicker);
                    investment.refreshValue(MainHome.this, investedTicker, "USD", Investment.CounterpartType.FIAT);
                    investment.refreshValue(MainHome.this, addedTicker, "USD", Investment.CounterpartType.FIAT);
                    investment.refreshValue(MainHome.this, addedTicker, investedTicker.getSymbol(), Investment.CounterpartType.CRYPTO);
                    investment.setRefreshListener(this);
                    investmentList.add(investment);
                    tokenRecapAdapter.updateTokenList(investmentList);
                }
        }
    }

    private void updateList() {
        for (int i = 0; i < investmentList.size(); i++) {
            final Investment investment = investmentList.get(i);

            investment.refreshValue(MainHome.this, investment.getReceivedToken(), "USD", Investment.CounterpartType.FIAT);
            investment.refreshValue(MainHome.this, investment.getInvestedTicker(), "USD", Investment.CounterpartType.FIAT);
            investment.refreshValue(MainHome.this, investment.getReceivedToken(), investment.getInvestedTicker().getSymbol(), Investment.CounterpartType.CRYPTO);
        }
    }

    private void saveInvestmentListToFile() {
        final ArrayList<Map.Entry<CryptoCompareTicker, CryptoCompareTicker>> investments = new ArrayList<>();
        for (Investment investment : investmentList) {
            investments.add(new AbstractMap.SimpleEntry<>(investment.getReceivedToken(), investment.getInvestedTicker()));
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                ObjectOutputStream oos = null;
                try {
                    fos = openFileOutput("investments.bak", MODE_PRIVATE);
                    oos = new ObjectOutputStream(fos);
                    oos.writeObject(investments);
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

    @Override
    public void refreshDone(Investment investment) {
        tokenRecapAdapter.updateTokenList(investmentList);
        tokenRecapAdapter.notifyItemChanged(investmentList.indexOf(investment));
        saveInvestmentListToFile();
    }


    @Override
    public void onLongClick(int position) {
        optionsMenu.getItem(0).setVisible(true);
        selectedPosition = position;
    }
}
