package minutecode.cryptowatcher.model;

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
}
