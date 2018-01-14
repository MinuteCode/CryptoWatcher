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
    private CMCTicker receivedToken;
    private CMCTicker investedTicker;
    private double receivedAmount;
    private double investedAmount;

    public Investment(CMCTicker receivedToken, double receivedAmount, CMCTicker investedTicker, double investedAmount) {
        this.receivedToken = receivedToken;
        this.investedTicker = investedTicker;
        this.receivedAmount = receivedAmount;
        this.investedAmount = investedAmount;
    }

    public double computeReceivedDollarConversion() {
        return receivedAmount * Double.valueOf(receivedToken.getPrice_usd());
    }

    public CMCTicker getReceivedToken() {
        return receivedToken;
    }

    public void setReceivedToken(CMCTicker receivedToken) {
        this.receivedToken = receivedToken;
    }

    public CMCTicker getInvestedTicker() {
        return investedTicker;
    }

    public void setInvestedTicker(CMCTicker investedTicker) {
        this.investedTicker = investedTicker;
    }

    public double getReceivedAmount() {
        return receivedAmount;
    }

    public void setReceivedAmount(double receivedAmount) {
        this.receivedAmount = receivedAmount;
    }

    public double getInvestedAmount() {
        return investedAmount;
    }

    public void setInvestedAmount(double investedAmount) {
        this.investedAmount = investedAmount;
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
