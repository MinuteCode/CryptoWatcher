package minutecode.cryptowatcher;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import minutecode.cryptowatcher.model.CMCTicker;
import minutecode.cryptowatcher.model.Investment;

public class MainHome extends AppCompatActivity {

    final static int ADD_TOKEN_REQUEST = 1;

    private List<CMCTicker> tokenList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToTokenAddition();
            }
        });
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
                }
        }
    }
}
