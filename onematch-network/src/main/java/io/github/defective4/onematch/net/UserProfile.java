package io.github.defective4.onematch.net;

public class UserProfile {
    public int bestStreak, currentStreak, dailyPlace, allTimePlace, solvedChallenges;
    public String bestTime = "Unknown";
    public long joinedDate;
    public String name = "Username";

    public UserProfile(int bestStreak, int currentStreak, int dailyPlace, int allTimePlace, int solvedChallenges,
            String bestTime, long joinedDate, String name) {
        this.bestStreak = bestStreak;
        this.currentStreak = currentStreak;
        this.dailyPlace = dailyPlace;
        this.allTimePlace = allTimePlace;
        this.solvedChallenges = solvedChallenges;
        this.bestTime = bestTime;
        this.joinedDate = joinedDate;
        this.name = name;
    }

    public UserProfile() {}
}
