package minutecode.cryptowatcher.model;

import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;

import java.util.List;

/**
 * Created by bameur on 19/01/2018.
 */

public class InvestmentDiffCallback extends DiffUtil.Callback {

    private final List<Investment> oldInvestmentList;
    private final List<Investment> newInvestmentList;

    public InvestmentDiffCallback(List<Investment> old, List<Investment> newList) {
        this.oldInvestmentList = old;
        this.newInvestmentList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldInvestmentList.size();
    }

    @Override
    public int getNewListSize() {
        return newInvestmentList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldInvestmentList.get(oldItemPosition).getReceivedToken().getSymbol().equals(newInvestmentList.get(newItemPosition).getReceivedToken().getSymbol());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        final Investment oldInvestment = oldInvestmentList.get(oldItemPosition);
        final Investment newInvestment = newInvestmentList.get(newItemPosition);

        return oldInvestment.equals(newInvestment);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
