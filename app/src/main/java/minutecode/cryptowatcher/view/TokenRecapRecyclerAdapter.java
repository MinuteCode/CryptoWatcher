package minutecode.cryptowatcher.view;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import minutecode.cryptowatcher.R;
import minutecode.cryptowatcher.model.Config;
import minutecode.cryptowatcher.model.Investment;
import minutecode.cryptowatcher.model.InvestmentDiffCallback;

/**
 * Created by Benjamin on 1/14/2018.
 */

public class TokenRecapRecyclerAdapter extends RecyclerView.Adapter<TokenRecapRecyclerAdapter.ViewHolder> {

    private ArrayList<Investment> tokenList;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tokenName, receivedAmount, dollarConversion, investedTokenOutput;
        CardView tokenCard;
        ImageView tokenImage;

        ViewHolder(View v) {
            super(v);
            tokenCard = v.findViewById(R.id.token_card);
            tokenName = v.findViewById(R.id.received_token_name);
            tokenImage = v.findViewById(R.id.token_image);
            receivedAmount = v.findViewById(R.id.received_amount);
            dollarConversion = v.findViewById(R.id.dollar_result);
            investedTokenOutput = v.findViewById(R.id.invested_token_output);
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
    public void onBindViewHolder(final TokenRecapRecyclerAdapter.ViewHolder holder, final int position) {
        final Investment token = tokenList.get(position);

        Ion.with(holder.tokenImage)
                .load(Config.baseImageUrl + token.getReceivedToken().getImageUrl());

        holder.tokenCard.setCardElevation(16);
        holder.tokenName.setText(token.getReceivedToken().getFullName());
        holder.receivedAmount.setText(Double.toString(token.getReceivedAmount()) + " " + token.getReceivedToken().getSymbol());
        holder.dollarConversion.setText(String.format(Locale.getDefault(), "%1$.2f", token.getTotalFiatAmount()) + holder.itemView.getContext().getString(R.string.dollar_symbol));
        String tokenOutput = String.format(Locale.getDefault(), "%1$.5f", token.getTokenOutput()) + " " + token.getInvestedTicker().getSymbol();
        holder.investedTokenOutput.setText(tokenOutput);
    }

    @Override
    public int getItemCount() {
        return tokenList.size();
    }

    public void updateTokenList(List<Investment> newList) {
        final InvestmentDiffCallback diffCallback = new InvestmentDiffCallback(tokenList, newList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        tokenList.clear();
        tokenList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }
}
