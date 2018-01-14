package minutecode.cryptowatcher.view;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import minutecode.cryptowatcher.R;
import minutecode.cryptowatcher.model.Investment;

/**
 * Created by Benjamin on 1/14/2018.
 */

public class TokenRecapRecyclerAdapter extends RecyclerView.Adapter<TokenRecapRecyclerAdapter.ViewHolder> {

    private ArrayList<Investment> tokenList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tokenName, receivedAmount, dollarConversion;
        public CardView tokenCard;

        public ViewHolder(View v) {
            super(v);
            tokenCard = v.findViewById(R.id.token_card);
            tokenName = v.findViewById(R.id.received_token_name);
            receivedAmount = v.findViewById(R.id.received_amount);
            dollarConversion = v.findViewById(R.id.dollar_result);
        }
    }

    public TokenRecapRecyclerAdapter(List<Investment> lst) {
        tokenList = (ArrayList<Investment>) lst;
    }

    @Override
    public TokenRecapRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.token_recap_adapter_layout, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(TokenRecapRecyclerAdapter.ViewHolder holder, int position) {
        Investment token = tokenList.get(position);
        holder.tokenCard.setCardElevation(24);
        holder.tokenName.setText(token.getReceivedToken().getFullName());
        holder.receivedAmount.setText(Double.toString(token.getReceivedAmount()) + " " + token.getReceivedToken().getSymbol());
        holder.dollarConversion.setText(Double.toString(token.getTotalFiatAmount()) + " $");
    }

    @Override
    public int getItemCount() {
        return tokenList.size();
    }
}
