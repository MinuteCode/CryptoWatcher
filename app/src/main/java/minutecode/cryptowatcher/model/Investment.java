package minutecode.cryptowatcher.model;

import android.content.Context;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import minutecode.cryptowatcher.MainHome;

/**
 * Created by Benjamin on 1/13/2018.
 */

public class Investment implements Serializable {
    private CryptoCompareTicker receivedToken;
    private CryptoCompareTicker investedTicker;
    private double receivedAmount;
    private double investedAmountCrypto;

    private double tokenOutput;
    private double totalFiatAmount;

    public Investment(CryptoCompareTicker receivedToken, double receivedAmount, CryptoCompareTicker investedTicker, double investedAmountCrypto) {
        this.receivedToken = receivedToken;
        this.investedTicker = investedTicker;
        this.receivedAmount = receivedAmount;
        this.investedAmountCrypto = investedAmountCrypto;
        this.tokenOutput = 0;
    }

    public void computeReceivedDollarConversion(Double price) {
        totalFiatAmount = receivedAmount * price;
    }

    public void computeTokenOutput() {
        tokenOutput = totalFiatAmount / investedTicker.getNowConversionRateFiat();
    }

    public double getTokenOutput() {
        return tokenOutput;
    }

    public void setTokenOutput(double tokenOutput) {
        this.tokenOutput = tokenOutput;
    }

    public CryptoCompareTicker getReceivedToken() {
        return receivedToken;
    }

    public void setReceivedToken(CryptoCompareTicker receivedToken) {
        this.receivedToken = receivedToken;
    }

    public CryptoCompareTicker getInvestedTicker() {
        return investedTicker;
    }

    public void setInvestedTicker(CryptoCompareTicker investedTicker) {
        this.investedTicker = investedTicker;
    }

    public double getTotalFiatAmount() {
        return totalFiatAmount;
    }

    public void setTotalFiatAmount(double value) {
        totalFiatAmount = value;
    }

    public double getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(double receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public double getInvestedAmountCrypto() {
        return investedAmountCrypto;
    }

    public void setInvestedAmountCrypto(double investedAmountCrypto) {
        this.investedAmountCrypto = investedAmountCrypto;
    }

    public void refreshValues(Context ctx, final CryptoCompareTicker ticker, final String against, final int position) {
        Ion.with(ctx)
                .load(Config.baseAPIUrl + Config.conversionUrlStart + ticker.getSymbol() + Config.conversionUrlMiddle + against)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        double conversionDollar = result.get(against).getAsDouble();

                        ticker.setNowConversionRateFiat(conversionDollar);
                        computeTokenOutput();
                    }
                });
    }

    public void writeToFile(FileOutputStream fos) {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(fos);
            oos.writeObject(this);
            oos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
                if (oos != null) {
                    oos.flush();
                    oos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Investment readFromFile(FileInputStream fis) {
        ObjectInputStream ois = null;
        Investment investment = null;
        try {
            ois = new ObjectInputStream(fis);
            investment = (Investment) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return investment;
    }

    @Override
    public boolean equals(Object obj) {
        Investment comparison = (Investment) obj;

        return receivedToken.equals(comparison.getReceivedToken())
                && investedTicker.equals(comparison.getInvestedTicker())
                && receivedAmount == comparison.getReceivedAmount()
                && investedAmountCrypto == comparison.getInvestedAmountCrypto()
                && tokenOutput == comparison.getTokenOutput()
                && totalFiatAmount == comparison.getTotalFiatAmount();
    }
}
