package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.PushUpRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PushUpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PushUpRepository pushUpRepository;

    @Autowired
    private VideoRepository videoRepository;

    /**
     * Analyzes pushups from a video and saves the analysis to the associated user.
     *
     * @param videoId The ID of the video containing pushups.
     * @param userId The ID of the user performing the pushups.
     */
    public void analyzePushups(int videoId, int userId){
        Video video = videoRepository.findById(videoId).orElse(null);
        if (video == null) {
            // Handle video not found
            return;
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            // Handle user not found
            return;
        }

        // Count pushups and calculate average score
        int pushupCount = countPushups(video);
        double averageScore = calculateAverageScore(video);

        // Initialize and fill up the PushUp object
        PushUp pushup = new PushUp(user, pushupCount, averageScore, new Date(), video.getLabel(), "User's pushup performance");

        // Save the PushUp object
        pushUpRepository.save(pushup);
    }

    private int countPushups(Video video) {
        int pushupCount = 0;
        boolean isDown = false;

        for (Image image : video.getImages()) {
            if (isPushupDown(image)) {
                if (!isDown) {
                    isDown = true;
                }
            } else if (isDown) {
                pushupCount++;
                isDown = false;
            }
        }

        return pushupCount;
    }

    private boolean isPushupDown(Image image) {
        // Assuming the first coordinate is the right elbow and the second is the right shoulder
        Coordinate rightElbow = image.getCoordinates().get(13);
        Coordinate rightShoulder = image.getCoordinates().get(12);

        // Check if the elbow is below the shoulder
        return rightElbow.getY() > rightShoulder.getY();
    }

    private double calculateAverageScore(Video video) {
        // Implement the logic for calculating the average score based on pushup form
        // This is a placeholder implementation
        return 10.0; // Temporary average score for each pushup
    }
}
