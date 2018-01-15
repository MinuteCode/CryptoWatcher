package minutecode.cryptowatcher.view;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import minutecode.cryptowatcher.R;
import minutecode.cryptowatcher.model.Config;
import minutecode.cryptowatcher.model.Investment;

/**
 * Created by Benjamin on 1/14/2018.
 */

public class TokenRecapRecyclerAdapter extends RecyclerView.Adapter<TokenRecapRecyclerAdapter.ViewHolder> {

    private ArrayList<Investment> tokenList;
    private RefreshCallback refreshCallback;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tokenName, receivedAmount, dollarConversion;
        CardView tokenCard;
        ImageView tokenImage;

        ViewHolder(View v) {
            super(v);
            tokenCard = v.findViewById(R.id.token_card);
            tokenName = v.findViewById(R.id.received_token_name);
            tokenImage = v.findViewById(R.id.token_image);
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
    public void onBindViewHolder(final TokenRecapRecyclerAdapter.ViewHolder holder, final int position) {
        final Investment token = tokenList.get(position);

        Ion.with(holder.itemView.getContext())
                .load(Config.baseImageUrl + token.getReceivedToken().getImageUrl())
                .as(new TypeToken<Bitmap>() {
                })
                .setCallback(new FutureCallback<Bitmap>() {
                    @Override
                    public void onCompleted(Exception e, Bitmap result) {
                        final RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(holder.itemView.getResources(), result);
                        roundedBitmapDrawable.setCircular(true);
                        new Handler(Looper.getMainLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                holder.tokenImage.setImageDrawable(roundedBitmapDrawable);
                            }
                        };
                    }
                });

        Ion.with(holder.tokenImage)
                .load(Config.baseImageUrl + token.getReceivedToken().getImageUrl());

        holder.tokenCard.setCardElevation(16);
        holder.tokenName.setText(token.getReceivedToken().getFullName());
        holder.receivedAmount.setText(Double.toString(token.getReceivedAmount()) + " " + token.getReceivedToken().getSymbol());
        holder.dollarConversion.setText(String.format(Locale.getDefault(), "%1$.2f", token.getTotalFiatAmount()) + holder.itemView.getContext().getString(R.string.dollar_symbol));
    }

    @Override
    public int getItemCount() {
        return tokenList.size();
    }

    public void hideTotalFiatAmount() {
        for (Investment investment :
                tokenList) {
            investment.setTotalFiatAmount(0);
        }
    }

    public RefreshCallback getRefreshCallback() {
        return refreshCallback;
    }

    public void setRefreshCallback(RefreshCallback refreshCallback) {
        this.refreshCallback = refreshCallback;
    }

    public interface RefreshCallback {
        void onRefreshStarted();

        void onRefreshComplete();
    }
}
