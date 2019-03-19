package me.nathan3882.androidttrainparse.responding;

import android.support.annotation.Nullable;
import me.nathan3882.androidttrainparse.fragments.DayLessonsFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class GetBestTrainsRequestResponseData implements RequestResponseData {


    private final String homeCrs;
    private JSONObject jsonObject;
    private String response;
    private LinkedList<LinkedList<JSONObject>> got = new LinkedList<>();
    private LinkedList<Service> mostCommonServices = new LinkedList<>();

    public GetBestTrainsRequestResponseData(String homeCrs, String response) {
        this.response = response;
        this.homeCrs = homeCrs;
        try {
            setJsonObject(new JSONObject(response));
            updateSubclassValues();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        extractSortedServices();

    }

    public String getHomeCrs() {
        return homeCrs;
    }

    public void extractSortedServices() {
        Map<String, Integer> frequencies = new HashMap<>(); //String is "{departure time}, {arrival time}", Integer is amount
        for (LinkedList<JSONObject> oneSqlEntrysTwoBestTrains : getGottenTrains()) {
            System.out.println("gotten train one size = " + oneSqlEntrysTwoBestTrains.size());
            //Iterating through the entries in the database which each contain 2 optimal trains for each lesson start time
            for (JSONObject aBestTrain : oneSqlEntrysTwoBestTrains) {
                String freqString = null;
                try {
                    freqString = aBestTrain.get("departure") +
                            ", " + aBestTrain.get("arrival") +
                            ", " + aBestTrain.get("walk");
                } catch (JSONException e) {
                    e.printStackTrace();
                    continue;
                }
                int currentFreq = frequencies.getOrDefault(freqString, 0);
                frequencies.put(freqString, currentFreq + 1);
                System.out.println("new freq for " + freqString + " = " + (currentFreq + 1));
            }
        }
        LinkedList<Map.Entry<String, Integer>> sortedFrequencyEntries = new LinkedList<>(frequencies.entrySet());
        if (!sortedFrequencyEntries.isEmpty()) {

            sortedFrequencyEntries.sort(Comparator.comparing(Map.Entry::getValue));

            for (int m = 0; m < sortedFrequencyEntries.size(); m++) {
                Map.Entry<String, Integer> entry = sortedFrequencyEntries.get(m);
                String[] keySplit = entry.getKey().split(", "); //"{departure time}, {arrival time}, {walk}"

                mostCommonServices.add(new Service(keySplit[1], keySplit[0], Long.parseLong(keySplit[2])));
                if (m + 1 == DayLessonsFragment.MAX_TRAINS_PER_LESSON) break;
            }
        }
    }

    public LinkedList<Service> getMostCommonServices() {
        return mostCommonServices;
    }

    @Override
    public void updateSubclassValues() {
        try {
            if (getJsonObject() != null) {
                JSONArray trainsArray = getJsonObject().getJSONArray("trains");
                for (int i = 0; i < trainsArray.length(); i++) {
                    LinkedList<JSONObject> temp = new LinkedList<>();
                    String theString = trainsArray.getString(i);
                    String[] listOfTrains = theString.split(" sep ");


                    for (String aTrainJson : listOfTrains) {
                        if (aTrainJson.equals("null")) continue;
                        System.out.println("While... has " + aTrainJson);
                        String insertInto = aTrainJson.replace("\\\"", "\"");
                        System.out.println("insert -> " + insertInto);
                        temp.add(new JSONObject(insertInto)); //web service returns \" for quote marks
                    }
                    got.add(temp);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getResponseString() {
        return this.response;
    }


    @Nullable
    @Override
    public JSONObject getJsonObject() {
        return this.jsonObject;
    }

    @Override
    public void setJsonObject(JSONObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    private LinkedList<LinkedList<JSONObject>> getGottenTrains() {
        return this.got;
    }
}
