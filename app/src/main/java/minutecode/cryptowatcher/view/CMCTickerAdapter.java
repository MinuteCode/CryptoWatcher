package minutecode.cryptowatcher.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import minutecode.cryptowatcher.R;
import minutecode.cryptowatcher.model.CMCTicker;

/**
 * Created by Benjamin on 1/13/2018.
 */

public class CMCTickerAdapter extends ArrayAdapter<CMCTicker> {

    public enum TickerDescription {
        NAME,
        SYMBOL
    }

    private List<CMCTicker> tickerList;
    private Context context;
    private int itemLayout;
    private List<CMCTicker> tickerListAllItems;
    private TickerDescription tickerDescription;

    public CMCTickerAdapter(@NonNull Context context, int resource, List<CMCTicker> list, TickerDescription description) {
        super(context, resource, list);
        tickerList = new ArrayList<>(list);
        tickerListAllItems = new ArrayList<>(list);
        this.context = context;
        itemLayout = resource;
        tickerDescription = description;
    }

    @Override
    public int getCount() {
        return tickerList.size();
    }

    @Nullable
    @Override
    public CMCTicker getItem(int position) {
        return tickerList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.cmc_ticker_adapter_layout, parent, false);
        }

        CMCTicker ticker = getItem(position);

        TextView name = convertView.findViewById(R.id.ticker_name);
        TextView price = convertView.findViewById(R.id.ticker_price);

        if (tickerDescription == TickerDescription.NAME) {
            name.setText(ticker.getName());
            String cryptoPrice = ticker.getPrice_usd() + " " + context.getString(R.string.dollar_symbol);
            price.setText(cryptoPrice);
        } else {
            name.setText(ticker.getSymbol());
            price.setText("");
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                return ((CMCTicker) resultValue).getName();
            }

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                List<CMCTicker> tickerSuggestions = new ArrayList<>();

                if (charSequence != null) {
                    for (CMCTicker ticker : tickerListAllItems) {
                        if (ticker.getName().toLowerCase().startsWith(charSequence.toString().toLowerCase()) && tickerDescription == TickerDescription.NAME) {
                            tickerSuggestions.add(ticker);
                        } else if (ticker.getSymbol().toLowerCase().startsWith(charSequence.toString().toLowerCase()) && tickerDescription == TickerDescription.SYMBOL) {
                            tickerSuggestions.add(ticker);
                        }
                    }
                    filterResults.values = tickerSuggestions;
                    filterResults.count = tickerSuggestions.size();
                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                tickerList.clear();

                if (filterResults != null && filterResults.count > 0) {
                    for (Object object : (List<?>) filterResults.values) {
                        if (object instanceof CMCTicker) {
                            tickerList.add((CMCTicker) object);
                        }
                    }
                    notifyDataSetChanged();
                } else if (charSequence == null) {
                    tickerList.addAll(tickerListAllItems);
                    notifyDataSetInvalidated();
                }
            }
        };
    }
}
