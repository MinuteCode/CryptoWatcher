package minutecode.cryptowatcher.model;

import java.util.List;

/**
 * Created by Benjamin on 1/28/2018.
 */

public class CandleDataResponse {
    private String Response;
    private int Type;
    private boolean Aggregated;
    private List<CandleData> Data;
    private int TimeTo;
    private int TimeFrom;
    private boolean FirstValueInArray;

    public String getResponse() {
        return Response;
    }

    public void setResponse(String response) {
        Response = response;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public boolean isAggregated() {
        return Aggregated;
    }

    public void setAggregated(boolean aggregated) {
        Aggregated = aggregated;
    }

    public List<CandleData> getData() {
        return Data;
    }

    public void setData(List<CandleData> data) {
        Data = data;
    }

    public int getTimeTo() {
        return TimeTo;
    }

    public void setTimeTo(int timeTo) {
        TimeTo = timeTo;
    }

    public int getTimeFrom() {
        return TimeFrom;
    }

    public void setTimeFrom(int timeFrom) {
        TimeFrom = timeFrom;
    }

    public boolean isFirstValueInArray() {
        return FirstValueInArray;
    }

    public void setFirstValueInArray(boolean firstValueInArray) {
        FirstValueInArray = firstValueInArray;
    }
}
