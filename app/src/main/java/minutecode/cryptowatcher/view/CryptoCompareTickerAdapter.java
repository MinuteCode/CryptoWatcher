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
import minutecode.cryptowatcher.model.CryptoCompareTicker;

/**
 * Created by Benjamin on 1/13/2018.
 */

public class CryptoCompareTickerAdapter extends ArrayAdapter<CryptoCompareTicker> {

    public enum TickerDescription {
        NAME,
        SYMBOL
    }

    private List<CryptoCompareTicker> tickerList;
    private Context context;
    private List<CryptoCompareTicker> tickerListAllItems;
    private TickerDescription tickerDescription;

    public CryptoCompareTickerAdapter(@NonNull Context context, int resource, List<CryptoCompareTicker> list, TickerDescription description) {
        super(context, resource, list);
        tickerList = new ArrayList<>(list);
        tickerListAllItems = new ArrayList<>(list);
        this.context = context;
        tickerDescription = description;
    }

    @Override
    public int getCount() {
        return tickerList.size();
    }

    @Nullable
    @Override
    public CryptoCompareTicker getItem(int position) {
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
            convertView = LayoutInflater.from(context).inflate(R.layout.crypto_compare_ticker_adapter_layout, parent, false);
        }

        CryptoCompareTicker ticker = getItem(position);

        TextView name = convertView.findViewById(R.id.ticker_name);

        if (tickerDescription == TickerDescription.NAME) {
            name.setText(ticker.getFullName());
        } else {
            name.setText(ticker.getSymbol());
        }

        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            public CharSequence convertResultToString(Object resultValue) {
                if (tickerDescription == TickerDescription.NAME)
                    return ((CryptoCompareTicker) resultValue).getFullName();
                else if (tickerDescription == TickerDescription.SYMBOL)
                    return ((CryptoCompareTicker) resultValue).getSymbol();

                return "";
            }

            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                FilterResults filterResults = new FilterResults();
                List<CryptoCompareTicker> tickerSuggestions = new ArrayList<>();

                if (charSequence != null) {
                    for (CryptoCompareTicker ticker : tickerListAllItems) {
                        if (ticker.getFullName().toLowerCase().startsWith(charSequence.toString().toLowerCase()) && tickerDescription == TickerDescription.NAME) {
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
                        if (object instanceof CryptoCompareTicker) {
                            tickerList.add((CryptoCompareTicker) object);
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
