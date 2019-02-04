package com.example.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivitiesUtils {
	private static final LocalTime startTime = LocalTime.parse("09:00");
	private static final LocalTime lunchTime = LocalTime.parse("12:00");
	private static final LocalTime lunchFinish = LocalTime.parse("13:00");
	private static final LocalTime presMinStart = LocalTime.parse("16:00");
	private static final LocalTime presMaxStart = LocalTime.parse("17:00");
	private static final String delimiter = " ";
	private static BufferedReader br = null;
	private static Map<String, LocalTime> activities = new HashMap<String, LocalTime>();
	
	private ActivitiesUtils(){}
	
	public static void loadActivitiesFromFile(File file){
		br = FileUtils.getBufferedReader(file);
		
		try {
			while(br.ready()) {
				String lineRead = br.readLine();
				lineRead = lineRead.replaceAll("sprint", "15min");
				String[] lineReadAsArray = lineRead.split(delimiter);
				StringBuilder activitiesName = new StringBuilder();
				StringBuilder minsAsString = new StringBuilder();
				for (String word : lineReadAsArray) {
					if (word.endsWith("min")) {
						String[] wordAsArray = word.split("");
						String numberPattern = "[0-9]";
						for (String character : wordAsArray) {
							if (character.matches(numberPattern)) {
								minsAsString.append(character);
							}
						}
					} else {
						if (activitiesName.length() == 0) {
							activitiesName.append(word);
						} else {
							activitiesName.append(" " + word);
						}
					}
				}
				LocalTime minsOfActivity;
				int minsAsInt = Integer.parseInt(minsAsString.toString());
				if (minsAsInt == 60) {
					minsOfActivity = LocalTime.parse("01:00:00");
				} else {
					minsOfActivity = LocalTime.parse("00:" + minsAsString.toString() + ":00");
				}
				activities.put(activitiesName.toString() + " " + lineReadAsArray[lineReadAsArray.length - 1], minsOfActivity);
			}
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
	
	public static Map<Integer, List<String>> getTeamsWithActivities(int noOfTeams){
		Map<Integer, List<String>> teamsWithActivitiesWithTimes = new HashMap<Integer, List<String>>();
		
		for (int teamId = 1; teamId <= noOfTeams; teamId++) {
			List<String> activitiesForTeam = createListOfActivitiesForTeamId(teamId, teamsWithActivitiesWithTimes);
			teamsWithActivitiesWithTimes.put(teamId, activitiesForTeam);
		}
		return teamsWithActivitiesWithTimes;
	}
	
	private static List<String> createListOfActivitiesForTeamId(int tId, Map<Integer, List<String>> teamsWithActivities) {
		String[] activitiesAsArray = activities.keySet().toArray(new String[activities.keySet().size()]);
		int repeatCounter = 0;
		List<String> teamActivitiesWithTimes = new ArrayList<String>();
		LocalTime currentTime = startTime;
		
		while (repeatCounter < tId && (repeatCounter == 0 || repeatCounter == 1)) {
			int activityCounter = 0;
			for (String activity : activitiesAsArray) {
				boolean isActivityInOtherTeam = false;
				boolean isActivityAlreadyInList = false;
				if (!teamsWithActivities.isEmpty()) {
					for (int otherTeamId = 1; otherTeamId < tId; otherTeamId++) {
						List<String> activitiesInOtherTeam = teamsWithActivities.get(otherTeamId);
						for (String activityInOtherTeam : activitiesInOtherTeam) {
							if (activityInOtherTeam.contains(activity)) {
								isActivityInOtherTeam = true;
								break;
							}
						}
					}
				}
				if (!teamActivitiesWithTimes.isEmpty()) {
					for (String activityInListAlready : teamActivitiesWithTimes) {
						if (activityInListAlready.contains(activity)){
							isActivityAlreadyInList = true;
						}
					}
				}
				if (!isActivityInOtherTeam && !isActivityAlreadyInList) {
					LocalTime timeAdded;
					if (activities.get(activity).getHour() == 0) {
						timeAdded = currentTime.plusMinutes(activities.get(activity).getMinute());
					} else {
						timeAdded = currentTime.plusHours(1);
					}
					if (currentTime.plusHours(1).isAfter(lunchTime) &&
							currentTime.plusHours(1).isBefore(lunchFinish)) {
						teamActivitiesWithTimes.add(currentTime + " Lunch Break");
						currentTime = currentTime.plusHours(1);
					} else if (timeAdded.isBefore(lunchTime)) {
						teamActivitiesWithTimes.add(currentTime + " " + activitiesAsArray[activityCounter]);
						currentTime = timeAdded;
					} else if (timeAdded.isAfter(lunchFinish) &&
							timeAdded.isBefore(presMaxStart)) {
						teamActivitiesWithTimes.add(currentTime + " " + activitiesAsArray[activityCounter]);
						currentTime = timeAdded;
					} else if (currentTime.isAfter(presMinStart) &&
							currentTime.isBefore(presMaxStart)) {
						teamActivitiesWithTimes.add("17:00 " + "Staff Motivation Presentation");
						break;
					}
				}
				activityCounter++;
			}
			repeatCounter++;
		}
		return teamActivitiesWithTimes;
	}

}
