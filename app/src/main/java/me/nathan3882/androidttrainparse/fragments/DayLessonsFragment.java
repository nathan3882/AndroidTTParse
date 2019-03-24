package me.nathan3882.androidttrainparse.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.SpannableStringBuilder;
import android.widget.Toast;
import me.nathan3882.androidttrainparse.*;
import me.nathan3882.androidttrainparse.requesting.Action;
import me.nathan3882.androidttrainparse.requesting.GetRequest;
import me.nathan3882.androidttrainparse.requesting.Pair;
import me.nathan3882.androidttrainparse.responding.*;
import org.json.JSONObject;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @instantiated by DayFragmentFactory#createHasLessonsFragment
 */
public class DayLessonsFragment extends DayFragment {

    public static final int MAX_TRAINS_PER_LESSON = 2;
    private static final long DAY_TRAIN_RELEARN_COUNT = 28; //28 days until must relearn the trains
    private List<LessonInfo> lessonInfo;
    private ArrayList<String> lessons;
    private boolean showTrainsForEveryLesson = true;


    public DayLessonsFragment() {
    }

    public void synchroniseLessonInfo(List<LessonInfo> lessonInfo) {
        this.lessonInfo = lessonInfo;
    }

    public List<LessonInfo> getLessonInfo() {
        return lessonInfo;
    }

    public ArrayList<String> getLessons() {
        return lessons;
    }

    @Override
    public void makeStringToDisplay(SpannableStringBuilder mainString, ResponseEvent event) {


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        Calendar lessonTimeCal = Calendar.getInstance();
        lessonTimeCal.setTime(new Date());
        List<LessonInfo> infoForOneDay = getLessonInfo();
        Pair pair = new Pair();
        for (int i = 0; i < infoForOneDay.size(); i++) {
            LessonInfo collegeDay = infoForOneDay.get(i);
            if (i != 0) mainString.append(Util.SPANNABLE_BREAK);
            LinkedList<String> les = collegeDay.getLessons();
            for (String lessonName : les) {
                List<LocalTime> startTimes = collegeDay.getStartTimes(lessonName);
                List<LocalTime> finishTimes = collegeDay.getFinishTimes(lessonName);
                if (startTimes == null) continue;
                for (int k = 0; k < startTimes.size(); k++) {

                    LocalTime aLessonsStartTime = startTimes.get(k);

                    DayOfWeek today = DayOfWeek.of(calendar.get(Calendar.DAY_OF_WEEK));
                    DayOfWeek dayOfLesson = collegeDay.getDayOfWeek();
                    int dif = dayOfLesson.getValue() - today.getValue();

                    updateLessonCalendar(calendar, lessonTimeCal, aLessonsStartTime, dif);

                    Date aLessonsStartDate = lessonTimeCal.getTime();

                    LocalTime finishTime = finishTimes.get(k);

                    String prettyStartMinutesString = Util.getPrettyMinute(aLessonsStartTime.getMinute());
                    String prettyEndMinutesString = Util.getPrettyMinute(finishTime.getMinute());

                    String startsAtPrettyString = aLessonsStartTime.getHour() + ":" + prettyStartMinutesString;
                    String finishesAtPrettyString = finishTime.getHour() + ":" + prettyEndMinutesString;


                    int lastLesson = k - 1;
                    if (showTrainsForEveryLesson || k == 0 || k == lastLesson) {
                        int finalK = k;
                        getPotentialTwoBestLearnedTrains(aLessonsStartDate, new ResponseEvent() {

                            @Override
                            public void onCompletion(@NonNull RequestResponse requestResponse) {
                                mainString.append(Util.SPANNABLE_BREAK);

                                List<Pair.IntegerIntegerPair> lowerUpperForBigFont = new ArrayList<>();
                                List<Pair.IntegerIntegerPair> lowerUpperForSmallFont = new ArrayList<>();

                                int lowerBig = mainString.length();

                                String lessonStringToAppend = "You have " + lessonName + " #" + (finalK + 1) +
                                        " from " + startsAtPrettyString + " until " + finishesAtPrettyString
                                        + Util.SPANNABLE_BREAK;

                                int upperBig = lowerBig + lessonStringToAppend.length();

                                lowerUpperForBigFont.add(pair.new IntegerIntegerPair(lowerBig, upperBig));
                                mainString.append(lessonStringToAppend);



                                SpannableStringBuilder trainString = new SpannableStringBuilder();


                                RequestResponseData data = requestResponse.getData();
                                if (data instanceof GetBestTrainsRequestResponseData) {
                                    GetBestTrainsRequestResponseData bestTrainsData =
                                            (GetBestTrainsRequestResponseData) data;

                                    LinkedList<Service> mostCommonServices = bestTrainsData.getMostCommonServices();

                                    if (!mostCommonServices.isEmpty()) {

                                        trainString.append("Catch the...");
                                        for (Service service : mostCommonServices) {
                                            int lower = trainString.length();
                                            String toAppend = service.getDeparture()
                                                    + " from " + getUser().getHomeCrs() +
                                                    " arriving @ " + service.getArrival() +
                                                    " - " + service.getWalk() + "m walk)" + Util.SPANNABLE_BREAK;
                                            int upper = lower + toAppend.length();
                                            trainString.append(toAppend);
                                            lowerUpperForSmallFont.add(pair.new IntegerIntegerPair(lower, upper));
                                        }
                                        mainString.append(trainString + Util.SPANNABLE_BREAK);

                                    }
                                }
                                /*lowerUpperForBigFont.forEach(pair -> {
                                    mainString.setSpan(new AbsoluteSizeSpan(75),
                                            pair.getKey(), pair.getValue(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                });
                                lowerUpperForSmallFont.forEach(pair -> {
                                    mainString.setSpan(new AbsoluteSizeSpan(50),
                                            pair.getKey(), pair.getValue(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                                });*/
                                event.onCompletion(requestResponse);
                            }

                            @Override
                            public void onFailure() {
                                Toast.makeText(getContainer().getContext(), "An error occurred whilst inserting train data to learn for the future!", Toast.LENGTH_LONG)
                                        .show();
                                mainString.append("No trains found for this lesson...");
                                event.onFailure();
                            }

                            @Override
                            public void doFinally() {
                                if (finalK == lastLesson) {
                                    mainString.append(Util.SPANNABLE_BREAK);
                                }
                                event.doFinally();
                            }
                        });
                        //Here is where the end of a lesson's train times iteration breaks,
                        //checks whether it's the last lesson for that day, if so TTrainParser.BREAK
                    }
                }
            }
        }
    }

    @Override
    public String getHeader() {
        return this.header;
    }

    @Override
    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            this.dayOfWeek = DayOfWeek.of(args.getInt(BundleName.DAY_OF_WEEK_TO_SHOW.asString()));
            this.header = args.getString(BundleName.HEADER_NO_HTML.asString());
            this.lessons = args.getStringArrayList(BundleName.LESSONS.asString());
        }
    }

    /**
     * @returns a list of (lists that contain 2 best trains)
     */
    private LinkedList<LinkedList<JSONObject>> getPotentialTwoBestLearnedTrains(Date aLessonsStartDate, ResponseEvent event) {
        LinkedList<LinkedList<JSONObject>> learned = new LinkedList<>();
        TrainDate trainDate = new TrainDate(aLessonsStartDate);

        String columnName = trainDate.withoutColon();

        long currentMillis = System.currentTimeMillis();

        long aMonthInMillis = TimeUnit.DAYS.toMillis(DAY_TRAIN_RELEARN_COUNT);

        /**
         * Selects the time column for example 900 for 9am lesson which contains json object like this:
         "{ crs: "BMH" departure: "10:05", arrival: "11:05", walk: "8"};" //crs = homeCrs, d = departure time, a = arrival to brock time, walk: different in mins between arrival and lesson start time
         */
        Client client = new Client(Util.DEFAULT_TTRAINPARSE, Action.GET_BEST_TRAINS);

        System.out.println("train date without colon = " + trainDate.withoutColon());
        new GetRequest(client,
                "/" + trainDate.withoutColon() + "/" + getUser().getHomeCrs() + Util.PARAMS, event)
                .execute();


        return learned;
    }

    private void updateLessonCalendar(Calendar calendar, Calendar lessonTimeCal, LocalTime aLessonsStartTime, int dif) {
        lessonTimeCal.set(Calendar.HOUR_OF_DAY, aLessonsStartTime.getHour());
        lessonTimeCal.set(Calendar.MINUTE, aLessonsStartTime.getMinute());
        lessonTimeCal.set(Calendar.SECOND, aLessonsStartTime.getSecond());
        lessonTimeCal.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + dif);
    }
}