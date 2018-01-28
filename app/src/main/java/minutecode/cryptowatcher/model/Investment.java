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

/**
 * Created by Benjamin on 1/13/2018.
 */

public class Investment implements Serializable {
    private CryptoCompareTicker receivedToken;
    private CryptoCompareTicker investedTicker;

    private double tokenOutput;
    private double totalFiatAmount;
    private double cryptoROI;
    private double fiatROI;

    private RefreshCallback refreshListener;

    public enum CounterpartType {
        FIAT,
        CRYPTO
    }

    public Investment(CryptoCompareTicker receivedToken, CryptoCompareTicker investedTicker) {
        this.receivedToken = receivedToken;
        this.investedTicker = investedTicker;
        this.tokenOutput = 0;
    }

    public double getCryptoROI() {
        return cryptoROI;
    }

    public void setCryptoROI(double cryptoROI) {
        this.cryptoROI = cryptoROI;
    }

    public double getFiatROI() {
        return fiatROI;
    }

    public void setFiatROI(double fiatROI) {
        this.fiatROI = fiatROI;
    }

    public void computeReceivedDollarConversion() {
        totalFiatAmount = receivedToken.getAmount() * receivedToken.getNowConversionRateFiat();
    }

    public void computeTokenOutput() {
        tokenOutput = receivedToken.getNowConversionRateCrypto() * receivedToken.getAmount();
    }

    public RefreshCallback getRefreshListener() {
        return refreshListener;
    }

    public void setRefreshListener(RefreshCallback refreshListener) {
        this.refreshListener = refreshListener;
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

    public void refreshValue(Context ctx, final CryptoCompareTicker ticker, final String against, final CounterpartType type) {
        Ion.with(ctx)
                .load(Config.baseAPIUrl + Config.conversionUrlStart + ticker.getSymbol() + Config.conversionUrlMiddle + against)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        if (e == null) {
                            double conversion = result.get(against).getAsDouble();

                            switch (type) {
                                case FIAT:
                                    if (ticker == receivedToken) {
                                        fiatROI = (conversion / ticker.getOriginalConversionRateFiat()) * 100;
                                        ticker.setNowConversionRateFiat(conversion);
                                        computeReceivedDollarConversion();
                                    }
                                    break;

                                case CRYPTO:
                                    if (ticker == receivedToken) {
                                        cryptoROI = (conversion / ticker.getOriginalConversionRateCrypto()) * 100;
                                        ticker.setNowConversionRateCrypto(conversion);
                                        computeTokenOutput();
                                    }
                                    break;
                            }

                            refreshListener.refreshDone(Investment.this);
                        } else {
                            e.printStackTrace();
                        }
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
                && tokenOutput == comparison.getTokenOutput()
                && totalFiatAmount == comparison.getTotalFiatAmount();
    }

    public interface RefreshCallback {
        void refreshDone(Investment investment);
    }
}
