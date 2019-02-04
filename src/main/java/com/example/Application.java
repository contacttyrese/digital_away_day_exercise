package com.example;

import java.io.File;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import com.example.utils.ActivitiesUtils;

public class Application {
	
	public static void main(String... args) {
		if (args.length == 2) {
			String filename = args[0];
			String numberOfTeams = args[1];
			if (filename.endsWith(".txt") && numberOfTeams.matches("[\\d]")) {
				File file = new File(filename);
				int noOfTeams = Integer.parseInt(numberOfTeams);
				System.out.println("Retrieving activities from file..");
				ActivitiesUtils.loadActivitiesFromFile(file);
				Map<Integer, List<String>> teamsWithActivities = ActivitiesUtils.getTeamsWithActivities(noOfTeams);
				teamsWithActivities.keySet().forEach(teamId -> {
					System.out.println("Activities for team: " + teamId);
					teamsWithActivities.get(teamId).forEach(activity -> {
						System.out.println("Activity: " + activity);
					});
				});
				
				System.out.println("Complete");
				
			} else {
				System.out.println("file format was not recognised or number of teams was NaN");
			}
		} else {
			System.out.println("Incorrect arguments received or unrecognisable arguments");
		}
	}

}
