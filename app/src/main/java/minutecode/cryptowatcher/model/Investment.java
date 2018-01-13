package minutecode.cryptowatcher.model;

/**
 * Created by Benjamin on 1/13/2018.
 */

public class Investment {
    private CMCTicker recievedToken;
    private CMCTicker investedTicker;
    private double recievedAmount;
    private double investedAmount;

    public Investment(CMCTicker recievedToken, double recievedAmount, CMCTicker investedTicker, double investedAmount) {
        this.recievedToken = recievedToken;
        this.investedTicker = investedTicker;
        this.recievedAmount = recievedAmount;
        this.investedAmount = investedAmount;
    }
}
