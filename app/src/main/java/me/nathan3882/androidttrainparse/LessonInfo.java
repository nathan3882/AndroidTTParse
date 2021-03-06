package me.nathan3882.androidttrainparse;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

/**
 * @author Nathan Allanson
 * @purpose Used to fetch information about a specific day of the week, called from
 * @instantiatedBy CoreForm.java
 */

public class LessonInfo {

    public static boolean tried = false;
    private int lessonCount;
    private LinkedList<String> orderedLessons = new LinkedList<>();
    private Map<String, LinkedList<LocalTime>> orderedSubjectStartTimes = new LinkedHashMap<>();
    private Map<String, LinkedList<LocalTime>> orderedSubjectFinishTimes = new LinkedHashMap<>();
    private String lastLesson;
    private String firstLesson;
    private boolean parsedSuccessfully = true;
    private DayOfWeek dayOfWeek;
    private ArrayList<String> lessonNames;

    public LessonInfo(ArrayList<String> lessonNames, List<String> words, DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
        this.lessonNames = lessonNames;

        Map<String, List<String>> subjectAndBounds = getBoundsForSubjects(words);

        for (String subject : subjectAndBounds.keySet()) {
            try {
                storeStartEndTimes(subject, words, subjectAndBounds,
                        orderedSubjectStartTimes, orderedSubjectFinishTimes);
            } catch (Exception exception) {
                this.setParsedSuccessfully(false);
                exception.printStackTrace();
                break;
            }
        }

        try {
            lastLesson = orderedLessons.get(orderedLessons.size() - 1);
            firstLesson = orderedLessons.get(0);
            lessonCount = orderedLessons.size();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public LinkedList<String> getOrderedLessons() {
        return orderedLessons;
    }

    public String getLastLesson() {
        return lastLesson;
    }

    public String getFirstLesson() {
        return firstLesson;
    }

    public LinkedList<LocalTime> getStartTimes(String lesson) {
        return this.orderedSubjectStartTimes.get(lesson);
    }

    public LinkedList<LocalTime> getFinishTimes(String lesson) {
        return this.orderedSubjectFinishTimes.get(lesson);
    }

    public LinkedList<String> getLessons() {
        return this.orderedLessons;
    }

    public DayOfWeek getDayOfWeek() {
        return this.dayOfWeek;
    }

    public boolean isParsedSuccessfully() {
        return parsedSuccessfully;
    }

    private void setParsedSuccessfully(boolean parsedSuccessfully) {
        this.parsedSuccessfully = parsedSuccessfully;
    }

    private void storeStartEndTimes(String subject, List<String> words, Map<String, List<String>> subjectAndBounds, Map<String, LinkedList<LocalTime>> orderedSubjectStartTimes, Map<String, LinkedList<LocalTime>> orderedSubjectFinishTimes) {
        int previousLowerBound = -1;
        String[] split = subject.split(" ");
        List<String> allBounds = subjectAndBounds.get(subject);
        for (String oneBound : allBounds) {
            if (!orderedLessons.contains(subject)) {
                orderedLessons.add(subject);
            }
            String[] valueSplit = oneBound.split(", ");
            int lowerOrDifferenceBound = parseInt(valueSplit[0]);
            if (previousLowerBound != -1) {
                lowerOrDifferenceBound = previousLowerBound + lowerOrDifferenceBound;
            } else {
                previousLowerBound = lowerOrDifferenceBound - 1;
            }

            int subjectNameLowerBound = lowerOrDifferenceBound;
            int subjectNameUpperBound = subjectNameLowerBound + (split.length - 1);

            int lowerBoundForJustTimes = subjectNameUpperBound + 1;
            int upperBoundForJustTimes = subjectNameUpperBound + 3;

            String timeString = "";
            try {
                words.get(upperBoundForJustTimes);
            } catch (IndexOutOfBoundsException e) {
                upperBoundForJustTimes = words.size() - 1; //-2 because iteration below is < and = aswell.
                lowerBoundForJustTimes = words.size() - 3; //This part of code will rely on the "parse" function to do its job as this situation likely arose from text being merged together by ocr
            }
            for (int i = lowerBoundForJustTimes; i <= upperBoundForJustTimes; i++) {
                timeString += words.get(i);
            }

            String[] startFinish = timeStringSplit(timeString); //"10:05-11:10" left is start, right is finish
            if (getColonCount(timeString) == 1) { //Sometimes has just been one, down to dodgy ocr
                timeString += words.get(upperBoundForJustTimes + 1); //Add next few words
                String addingBefore = words.get(lowerBoundForJustTimes - 1);
                timeString = addingBefore + timeString;
                startFinish = timeStringSplit(timeString);
            }
            LocalTime startTime = parse(startFinish[0], false);
            LocalTime finishTime = parse(startFinish[1], false);

            addToList(subject, subjectAndBounds, orderedSubjectStartTimes, startTime);
            addToList(subject, subjectAndBounds, orderedSubjectFinishTimes, finishTime);
        }
    }

    private int getColonCount(String timeString) {
        int colonCount = 0;
        for (char c : timeString.toCharArray()) {
            if (c == ':') colonCount++;
        }
        return colonCount;
    }

    private String[] timeStringSplit(String toSplit) {
        return toSplit.split("-");
    }

    //Allows "LRCed11:30-12:35ed" to be parsed into start time 11:30  and end time  12:35 easily
    private LocalTime parse(String timeString, boolean recursivelyCalled) {
        LocalTime parsed = null;
        try {
            parsed = LocalTime.parse(timeString);
        } catch (DateTimeParseException | IndexOutOfBoundsException exception) {
            int indexOfColon = timeString.indexOf(":");
            String newString = null;
            if (recursivelyCalled) {
                //Has excepted on the supposedly "fixed" & parsable string, now try removing the end of the string
                newString = timeString.substring(indexOfColon - 2, indexOfColon + 3); //ie "12:30blabla" would return "12:30"
            } else {
                newString = timeString.substring(indexOfColon - 2);
            }
            return parse(newString, true);
        }
        return parsed;
    }

    private void addToList(String subject, Map<String, List<String>> subjectAndBounds, Map<String, LinkedList<LocalTime>> orderedSubjectTimes, LocalTime finishTime) {
        LinkedList<LocalTime> finishTimes = orderedSubjectTimes.containsKey(subject) ? new LinkedList<>(orderedSubjectTimes.get(subject)) : new LinkedList<>();
        if (subjectAndBounds.containsKey(subject)) {
            finishTimes.add(finishTime);
        }

        orderedSubjectTimes.put(subject, finishTimes);
    }

    private Map<String, List<String>> getBoundsForSubjects(List<String> words) {
        Map<String, List<String>> subjectAndBounds = new LinkedHashMap<String, List<String>>();
        int endBoundIndex = 0;
        for (int i = 0; i < words.size(); i++) {
            String currentWord = words.get(i);
            for (String aSubject : lessonNames) {
                if (aSubject.contains(" ")) {
                    String firstWord = aSubject.split(" ")[0];
                    if (firstWord.equals(currentWord)) {
                        List<String> split = Arrays.asList(aSubject.split(" ")); //started = true because it contains " ", list = "Business", "studies", "two"

                        int lowerBound = i; //If first iteration, endBoundIndex is 0 anyways so its i - 0 which is just i

                        endBoundIndex = lowerBound + (split.size() - 1);

                        String boundString = lowerBound + ", " + endBoundIndex;

                        List<String> bounds = subjectAndBounds.containsKey(aSubject) ? subjectAndBounds.get(aSubject) : new ArrayList<>(Arrays.asList(boundString));
                        if (subjectAndBounds.containsKey(aSubject)) {
                            bounds.add(boundString);
                        }
                        subjectAndBounds.put(aSubject, bounds);

                    }
                } else if (aSubject.equals(currentWord)) {
                    String boundString = i + ", " + i;
                    List<String> bounds = subjectAndBounds.containsKey(aSubject) ? subjectAndBounds.get(aSubject) : new ArrayList<>(Arrays.asList(boundString));
                    if (subjectAndBounds.containsKey(aSubject)) {
                        bounds.add(boundString);
                    }
                    subjectAndBounds.put(aSubject, bounds);
                }
            }
        }

        return subjectAndBounds;
    }

    private int parseInt(String s) {
        return Integer.parseInt(s);
    }
}