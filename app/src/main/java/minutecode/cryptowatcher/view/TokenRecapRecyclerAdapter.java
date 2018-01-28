package minutecode.cryptowatcher.view;

import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import minutecode.cryptowatcher.R;
import minutecode.cryptowatcher.model.Config;
import minutecode.cryptowatcher.model.Investment;
import minutecode.cryptowatcher.model.InvestmentDiffCallback;

/**
 * Created by Benjamin on 1/14/2018.
 */

public class TokenRecapRecyclerAdapter extends RecyclerView.Adapter<TokenRecapRecyclerAdapter.ViewHolder> {

    private ArrayList<Investment> tokenList;
    private OnTokenListAction tokenListListner;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tokenName, receivedAmount, dollarConversion, investedTokenOutput, roiFiat, roiCrypto, updateTime;
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
            roiFiat = v.findViewById(R.id.roi_fiat);
            roiCrypto = v.findViewById(R.id.roi_crypto);
            updateTime = v.findViewById(R.id.update_time);
        }

        private void setCryptoROIDrawable(double roi) {
            if (roi > 100) {
                VectorDrawable drawable = (VectorDrawable) itemView.getContext().getDrawable(R.drawable.ic_trending_up_black_24dp);
                roiCrypto.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
                int colorId = ContextCompat.getColor(itemView.getContext(), R.color.colorPositive);
                drawable.setTint(colorId);
                roiCrypto.setBackgroundColor(colorId);
                roiCrypto.getBackground().setAlpha(50);
            } else if (roi == 100) {
                VectorDrawable drawable = (VectorDrawable) itemView.getContext().getDrawable(R.drawable.ic_trending_flat_black_24dp);
                roiCrypto.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
                int colorId = ContextCompat.getColor(itemView.getContext(), R.color.colorNeutral);
                drawable.setTint(colorId);
                roiCrypto.setBackgroundColor(colorId);
                roiCrypto.getBackground().setAlpha(50);
            } else if (roi < 100) {
                VectorDrawable drawable = (VectorDrawable) itemView.getContext().getDrawable(R.drawable.ic_trending_down_black_24dp);
                roiCrypto.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
                int colorId = ContextCompat.getColor(itemView.getContext(), R.color.colorNegative);
                drawable.setTint(colorId);
                roiCrypto.setBackgroundColor(colorId);
                roiCrypto.getBackground().setAlpha(50);
            }
        }

        private void setFiatROIDrawable(double roi) {
            if (roi > 100) {
                VectorDrawable drawable = (VectorDrawable) itemView.getContext().getDrawable(R.drawable.ic_trending_up_black_24dp);
                roiFiat.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
                int colorId = ContextCompat.getColor(itemView.getContext(), R.color.colorPositive);
                drawable.setTint(colorId);
                roiFiat.setBackgroundColor(colorId);
                roiFiat.getBackground().setAlpha(50);
            } else if (roi == 100) {
                VectorDrawable drawable = (VectorDrawable) itemView.getContext().getDrawable(R.drawable.ic_trending_flat_black_24dp);
                roiFiat.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
                int colorId = ContextCompat.getColor(itemView.getContext(), R.color.colorNeutral);
                drawable.setTint(colorId);
                roiFiat.setBackgroundColor(colorId);
                roiFiat.getBackground().setAlpha(50);
            } else if (roi < 100) {
                VectorDrawable drawable = (VectorDrawable) itemView.getContext().getDrawable(R.drawable.ic_trending_down_black_24dp);
                roiFiat.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
                int colorId = ContextCompat.getColor(itemView.getContext(), R.color.colorNegative);
                drawable.setTint(colorId);
                roiFiat.setBackgroundColor(colorId);
                roiFiat.getBackground().setAlpha(50);
            }
        }
    }

    public TokenRecapRecyclerAdapter(List<Investment> lst) {
        tokenList = new ArrayList<>(lst.size());
        tokenList.addAll(lst);
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

        holder.tokenCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                holder.tokenCard.setCardElevation(100);
                tokenListListner.onLongClick(position);
                return true;
            }
        });

        holder.tokenName.setText(token.getReceivedToken().getFullName());
        holder.receivedAmount.setText(Double.toString(token.getReceivedToken().getAmount()) + " " + token.getReceivedToken().getSymbol());
        holder.dollarConversion.setText(String.format(Locale.getDefault(), "%1$.2f", token.getTotalFiatAmount()) + " " + holder.itemView.getContext().getString(R.string.dollar_symbol));
        String tokenOutput = String.format(Locale.getDefault(), "%1$.5f", token.getTokenOutput()) + " " + token.getInvestedTicker().getSymbol();
        holder.investedTokenOutput.setText(tokenOutput);

        SimpleDateFormat sdf = new SimpleDateFormat("'Last updated : ' dd/MM/yyyy H':'mm");
        String dateFormatted = sdf.format(Calendar.getInstance(TimeZone.getDefault()).getTime());
        holder.updateTime.setText(dateFormatted);

        String cryptoRoi = String.format(Locale.getDefault(), "%1$.2f", token.getCryptoROI());
        holder.roiCrypto.setText("Crypto change : \n" + cryptoRoi + " %");

        String fiatRoi = String.format(Locale.getDefault(), "%1$.2f", token.getFiatROI());
        holder.roiFiat.setText("Fiat change : \n" + fiatRoi + " %");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            holder.setCryptoROIDrawable(token.getCryptoROI());
            holder.setFiatROIDrawable(token.getFiatROI());
        }

    }

    @Override
    public int getItemCount() {
        return tokenList.size();
    }

    public ArrayList<Investment> getTokenList() {
        return tokenList;
    }

    public void setTokenList(ArrayList<Investment> tokenList) {
        this.tokenList = tokenList;
    }

    public OnTokenListAction getTokenListListner() {
        return tokenListListner;
    }

    public void setTokenListListner(OnTokenListAction tokenListListner) {
        this.tokenListListner = tokenListListner;
    }

    public void updateTokenList(List<Investment> newList) {
        final InvestmentDiffCallback diffCallback = new InvestmentDiffCallback(tokenList, newList);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        tokenList.clear();
        tokenList.addAll(newList);
        diffResult.dispatchUpdatesTo(this);
    }

    public interface OnTokenListAction {
        void onLongClick(int position);
    }
}
