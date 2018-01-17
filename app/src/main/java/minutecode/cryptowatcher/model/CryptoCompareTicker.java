package minutecode.cryptowatcher.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by Benjamin on 1/13/2018.
 */

public class CryptoCompareTicker implements Parcelable, Serializable {

    private String Name;
    private String CoinName;
    private String Symbol;
    private String FullName;
    private String TotalCoinSupply;
    private String ImageUrl;
    private double originalConversionRateFiat;
    private double nowConversionRateFiat;

    public CryptoCompareTicker(String name, String coinName, String symbol, String fullName, String totalCoinSupply, String imageUrl) {
        Name = name;
        CoinName = coinName;
        Symbol = symbol;
        FullName = fullName;
        TotalCoinSupply = totalCoinSupply;
        ImageUrl = imageUrl;
        originalConversionRateFiat = 0;
        nowConversionRateFiat = 0;
    }

    public CryptoCompareTicker(Parcel in) {
        Name = in.readString();
        CoinName = in.readString();
        Symbol = in.readString();
        FullName = in.readString();
        TotalCoinSupply = in.readString();
        ImageUrl = in.readString();
        originalConversionRateFiat = in.readDouble();
        nowConversionRateFiat = in.readDouble();
    }

    public double getNowConversionRateFiat() {
        return nowConversionRateFiat;
    }

    public void setNowConversionRateFiat(double nowConversionRateFiat) {
        this.nowConversionRateFiat = nowConversionRateFiat;
    }

    public double getOriginalConversionRateFiat() {
        return originalConversionRateFiat;
    }

    public void setOriginalConversionRateFiat(double originalConversionRateFiat) {
        this.originalConversionRateFiat = originalConversionRateFiat;
    }

    public String getCoinName() {
        return CoinName;
    }

    public void setCoinName(String coinName) {
        CoinName = coinName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String symbol) {
        Symbol = symbol;
    }

    public String getFullName() {
        return FullName;
    }

    public void setFullName(String fullName) {
        FullName = fullName;
    }

    public String getTotalCoinSupply() {
        return TotalCoinSupply;
    }

    public void setTotalCoinSupply(String totalCoinSupply) {
        TotalCoinSupply = totalCoinSupply;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(Name);
        parcel.writeString(CoinName);
        parcel.writeString(Symbol);
        parcel.writeString(FullName);
        parcel.writeString(TotalCoinSupply);
        parcel.writeString(ImageUrl);
        parcel.writeDouble(originalConversionRateFiat);
        parcel.writeDouble(nowConversionRateFiat);
    }

    public static final Parcelable.Creator<CryptoCompareTicker> CREATOR = new Parcelable.Creator<CryptoCompareTicker>() {

        @Override
        public CryptoCompareTicker createFromParcel(Parcel parcel) {
            return new CryptoCompareTicker(parcel);
        }

        @Override
        public CryptoCompareTicker[] newArray(int i) {
            return new CryptoCompareTicker[i];
        }
    };
}
