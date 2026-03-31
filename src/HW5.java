/*

  Author: Alexander Prescott Doyle
  Email: adoyle2025@my.fit.edu
  Course: Data Structures and Algorithms
  Section: 2
  Description of this file: A fitness watch program that tracks activities over the course of time
                            efficiently using skip lists.

 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Objects;
import java.util.Scanner;

public class HW5
{

    /*
      Description of each method, including parameters 
    */
    public static void main(String[] args) throws FileNotFoundException {
        parseAndHandleCommands(args[0]);
    }

    public static void parseAndHandleCommands(String fileName) throws FileNotFoundException {
        // The input file object that allows for access of the input file
        File inputFile = new File(fileName);

        // Skip List to store all the items in
        SkipListMap skipList = new SkipListMap();

        // Try for safety
        try (Scanner fileScanner = new Scanner(inputFile)) {
            // Loop through each line of the file after the initial line
            while (fileScanner.hasNextLine()) {
                // Get the next line in the file
                String line = fileScanner.nextLine();

                // Split the string into its command and args
                String[] commandAndArgs = line.split(" ");

                // Determine which method to run and run it
                switch (commandAndArgs[0]) {
                    case "AddActivity":
                        addActivity(commandAndArgs[1], commandAndArgs[2], skipList);
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
                    default:
                        throw new UnsupportedOperationException("The data is formatted wrong and the command " + commandAndArgs[0] + " does not exist!");
                }
            }
        }
    }


    /*
     * Method that adds an activity by time and activity
     */
    public static void addActivity(String time, String activity, SkipListMap skipListMap) {
        String output = skipListMap.put(time, activity);
        if (output != null) {
            System.out.println("AddActivity " + time + " " + activity + " ExistingActivityError:" + output);
        } else {
            System.out.println("AddActivity " + time + " " + activity);
        }
    }

    /*
     * Method that removes an activity by time
     */
    public static void removeActivity(String time, SkipListMap skipListMap) {
        String removedActivity = skipListMap.remove(time);
        if (removedActivity == null) {
            System.out.println("RemoveActivity " + time + " NoActivityError");
        } else {
            System.out.println("RemoveActivity " + time + " " + removedActivity);
        }
    }

    /*
     * Method that gets an activity by time
     */
    public static void getActivity(String time, SkipListMap skipListMap) {
        String gottenActivity = skipListMap.get(time);
        if (gottenActivity == null) {
            System.out.println("GetActivity " + time + " none");
        } else {
            System.out.println("GetActivity " + time + " " + gottenActivity);
        }
    }

    /*
     * Method that gets the activities between the two given times (inclusive)
     */
    public static void getActivitiesBetweenTimes(String startTime, String endTime, SkipListMap skipListMap) {
        String subMapReturn = skipListMap.subMap(startTime, endTime);
        if (Objects.equals(subMapReturn, "")) {
            System.out.println("GetActivitiesBetweenTimes " + startTime + " " + endTime + " none");
        } else {
            System.out.println("GetActivitiesBetweenTimes " + startTime + " " + endTime + subMapReturn);
        }
    }

    /*
     * Method that gets the activities for a specific day
     */
    public static void getActivitiesForOneDay(String date, SkipListMap skipListMap) {
        String startTime = date + "0000";
        String endTime = date + "2359";
        String subMapReturn = skipListMap.subMap(startTime, endTime);
        if (Objects.equals(subMapReturn, "")) {
            System.out.println("GetActivitiesForOneDay " + date + " none");
        } else {
            System.out.println("GetActivitiesForOneDay " + date + subMapReturn);
        }
    }

    /*
     * Method that gets the activities from the beginning of a day to the given time
     */
    public static void getActivitiesFromEarlierInTheDay(String currentTime, SkipListMap skipListMap) {
        String date = currentTime.substring(0, 4);
        String startTime = date + "0000";
        String subMapReturn = skipListMap.subMap(startTime, currentTime);
        if (subMapReturn.isEmpty()) {
            System.out.println("GetActivitiesFromEarlierInTheDay " + currentTime + " none");
        } else {
            System.out.println("GetActivitiesFromEarlierInTheDay " + currentTime + " " + subMapReturn.trim());
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
