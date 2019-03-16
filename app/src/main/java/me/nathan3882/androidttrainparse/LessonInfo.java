package me.nathan3882.androidttrainparse;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.*;

/**
 * @author Nathan Allanson
 * @purpose Used to fetch information about a specific day of the week, called from
 * @instantiatedBy CoreForm.java
 */

public class LessonInfo {

    private final DayOfWeek dayOfWeek;
    private final ArrayList<String> lessonNames;
    private int lessonCount;

    private LinkedList<String> orderedLessons = new LinkedList<>();
    private Map<String, LinkedList<LocalTime>> orderedSubjectStartTimes = new LinkedHashMap<>();
    private Map<String, LinkedList<LocalTime>> orderedSubjectFinishTimes = new LinkedHashMap<>();
    private String lastLesson;
    private String firstLesson;
    private boolean parsedSuccessfully = true;

    public LessonInfo(ArrayList<String> lessonNames, List<String> words, DayOfWeek dayOfWeek) {
        this.lessonNames = lessonNames;
        this.dayOfWeek = dayOfWeek;
        Map<String, List<String>> subjectAndBounds = getBoundsForSubjects(words);

        for (String subject : subjectAndBounds.keySet()) {
            try {
                storeStartEndTimes(subject, words, subjectAndBounds,
                        orderedSubjectStartTimes, orderedSubjectFinishTimes);
            } catch (Exception exception) {
                this.setParsedSuccessfully(false);
                exception.printStackTrace();
                return;
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

    public void setParsedSuccessfully(boolean parsedSuccessfully) {
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
            int lowerOrDifferenceBound = prs(valueSplit[0]);
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
            for (int i = lowerBoundForJustTimes; i <= upperBoundForJustTimes; i++)
                timeString += words.get(i);

            String[] startFinish = timeString.split("-"); //"10:05-11:10" left is start, right is finish
            LocalTime startTime = LocalTime.parse(startFinish[0]);
            LocalTime finishTime = LocalTime.parse(startFinish[1]);

            addToList(subject, subjectAndBounds, orderedSubjectStartTimes, startTime);
            addToList(subject, subjectAndBounds, orderedSubjectFinishTimes, finishTime);
        }
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
            for (String aLesson : lessonNames) {
                if (aLesson.contains(" ")) {
                    String firstWord = aLesson.split(" ")[0];
                    if (firstWord.equals(currentWord)) {
                        List<String> split = Arrays.asList(aLesson.split(" ")); //started = true because it contains " ", list = "Business", "studies", "two"

                        int lowerBound = i; //If first iteration, endBoundIndex is 0 anyways so its i - 0 which is just i

                        endBoundIndex = lowerBound + (split.size() - 1);

                        String boundString = lowerBound + ", " + endBoundIndex;

                        List<String> bounds = subjectAndBounds.containsKey(aLesson) ? subjectAndBounds.get(aLesson) : new ArrayList<>(Arrays.asList(boundString));
                        if (subjectAndBounds.containsKey(aLesson)) {
                            bounds.add(boundString);
                        }
                        subjectAndBounds.put(aLesson, bounds);
                    }
                } else if (aLesson.equals(currentWord)) {
                    String boundString = i + ", " + i;
                    List<String> bounds = subjectAndBounds.containsKey(aLesson) ? subjectAndBounds.get(aLesson) : new ArrayList<>(Arrays.asList(boundString));
                    if (subjectAndBounds.containsKey(aLesson)) {
                        bounds.add(boundString);
                    }
                    subjectAndBounds.put(aLesson, bounds);
                }
            }
        }

        return subjectAndBounds;
    }

    private int prs(String s) {
        return Integer.parseInt(s);
    }
}
