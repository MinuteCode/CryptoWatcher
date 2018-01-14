package minutecode.cryptowatcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
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
import java.util.ArrayList;
import java.util.List;

import minutecode.cryptowatcher.model.CMCTicker;
import minutecode.cryptowatcher.model.Investment;
import minutecode.cryptowatcher.view.TokenRecapRecyclerAdapter;

public class MainHome extends AppCompatActivity {

    final static int ADD_TOKEN_REQUEST = 1;

    private RecyclerView addedTokensRecyclerView;
    private TokenRecapRecyclerAdapter tokenRecapAdapter;
    private TextView noIcoText;

    private List<Investment> investmentList = new ArrayList<>();


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
                    CMCTicker addedTicker = data.getParcelableExtra("receivedTicker");
                    CMCTicker investedTicker = data.getParcelableExtra("investedTicker");
                    double investedAmount = data.getDoubleExtra("investedAmount",0.0);
                    double receivedAmount = data.getDoubleExtra("receivedAmount", 0.0);

                    Investment investment = new Investment(addedTicker, receivedAmount, investedTicker, investedAmount);
                    investmentList.add(investment);
                    tokenRecapAdapter = new TokenRecapRecyclerAdapter(investmentList);
                    addedTokensRecyclerView.setAdapter(tokenRecapAdapter);
                }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            FileOutputStream fos = openFileOutput("investments.bak", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(investmentList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
