package minutecode.cryptowatcher.model;

import java.util.ArrayList;

/**
 * Created by Benjamin on 1/14/2018.
 */

public class CryptoCompareCoinListResponse {
    private String BaseImageUrl;
    private ArrayList<CryptoCompareTicker> coin;

    public String getBaseImageUrl() {
        return BaseImageUrl;
    }

    public void setBaseImageUrl(String baseImageUrl) {
        BaseImageUrl = baseImageUrl;
    }

    public ArrayList<CryptoCompareTicker> getCoin() {
        return coin;
    }

    public void setCoin(ArrayList<CryptoCompareTicker> coin) {
        this.coin = coin;
    }
}
