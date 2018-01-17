package minutecode.cryptowatcher.model;

/**
 * Created by Benjamin on 1/14/2018.
 */

public class Config {
    public static final String coinListUrl = "http://vps.antr.fr:8000";
    public static final String baseAPIUrl = "https://min-api.cryptocompare.com/data/";
    public static String baseImageUrl = "https://www.cryptocompare.com";

    public static final String conversionUrlStart = "price?fsym=";
    public static final String conversionHistoricalUrlStart = "pricehistorical?fsym=";
    public static final String conversionUrlMiddle = "&tsyms=";
    public static final String timeStampParameter = "&ts=";
}
