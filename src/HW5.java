import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class HW5 {
    public static void main(String[] args) throws FileNotFoundException {
        if (args.length > 0) parseAndHandleCommands(args[0]);
    }

    public static void parseAndHandleCommands(String fileName) throws FileNotFoundException {
        File inputFile = new File(fileName);
        SkipListMap skipList = new SkipListMap();
        FakeRandHeight randHeight = new FakeRandHeight(); // One generator to rule them all

        try (Scanner fileScanner = new Scanner(inputFile)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] commandAndArgs = line.split(" ");
                if (commandAndArgs.length == 0) continue;

                switch (commandAndArgs[0]) {
                    case "AddActivity":
                        // Get the height here to keep the sequence in sync
                        int h = randHeight.get();
                        addActivity(commandAndArgs[1], commandAndArgs[2], h, skipList);
                        break;
                    case "RemoveActivity":
                        removeActivity(commandAndArgs[1], skipList);
                        break;
                    case "GetActivity":
                        getActivity(commandAndArgs[1], skipList);
                        break;
                    case "GetActivitiesBetweenTimes":
                        getActivitiesBetweenTimes(commandAndArgs[1], commandAndArgs[2], skipList);
                        break;
                    case "GetActivitiesFromEarlierInTheDay":
                        getActivitiesFromEarlierInTheDay(commandAndArgs[1], skipList);
                        break;
                    case "GetActivitiesForOneDay":
                        getActivitiesForOneDay(commandAndArgs[1], skipList);
                        break;
                    case "PrintSkipList":
                        printSkipList(skipList);
                        break;
                }
            }
        }
    }

    public static void addActivity(String time, String activity, int height, SkipListMap skipListMap) {
        if (!skipListMap.put(time, activity, height)) {
            System.out.println("AddActivity " + time + " " + activity + " ExistingActivityError:" + skipListMap.get(time));
        } else {
            System.out.println("AddActivity " + time + " " + activity);
        }
    }

    /*
     * Method that removes an activity by time
     */
    public static void removeActivity(String time, SkipListMap skipListMap) {
        String removedActivity = skipListMap.remove(time);
        if (removedActivity == null) System.out.println("RemoveActivity " + time + " NoActivityError");
        else System.out.println("RemoveActivity " + time + " " + removedActivity);
    }

    /*
     * Method that gets an activity by time
     */
    public static void getActivity(String time, SkipListMap skipListMap) {
        String gottenActivity = skipListMap.get(time);
        if (gottenActivity == null) System.out.println("GetActivity " + time + " none");
        else System.out.println("GetActivity " + time + " " + gottenActivity);
    }

    /*
     * Method that gets the activities between the two given times (inclusive)
     */
    public static void getActivitiesBetweenTimes(String startTime, String endTime, SkipListMap skipListMap) {
        String subMapReturn = skipListMap.subMap(startTime, endTime).trim();
        if (subMapReturn.isEmpty()) {
            System.out.println("GetActivitiesBetweenTimes " + startTime + " " + endTime + " none");
        } else {
            System.out.println("GetActivitiesBetweenTimes " + startTime + " " + endTime + " " + subMapReturn);
        }
    }

    /*
     * Method that gets the activities for a specific day
     */
    public static void getActivitiesForOneDay(String date, SkipListMap skipListMap) {
        String subMapReturn = skipListMap.subMap(date + "0000", date + "2359").trim();
        if (subMapReturn.isEmpty()) {
            System.out.println("GetActivitiesForOneDay " + date + " none");
        } else {
            System.out.println("GetActivitiesForOneDay " + date + " " + subMapReturn);
        }
    }

    /*
     * Method that gets the activities from the beginning of a day to the given time
     */
    public static void getActivitiesFromEarlierInTheDay(String currentTime, SkipListMap skipListMap) {
        String date = currentTime.substring(0, 4);
        String subMapReturn = skipListMap.subMap(date + "0000", currentTime).trim();
        if (subMapReturn.isEmpty()) {
            System.out.println("GetActivitiesFromEarlierInTheDay " + currentTime + " none");
        } else {
            System.out.println("GetActivitiesFromEarlierInTheDay " + currentTime + " " + subMapReturn);
        }
    }

    /*
     * Method that prints the skip list
     */
    public static void printSkipList(SkipListMap skipListMap) {
        System.out.println("PrintSkipList");
        skipListMap.print();
    }
}
