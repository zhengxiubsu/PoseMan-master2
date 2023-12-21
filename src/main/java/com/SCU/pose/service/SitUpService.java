package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.SitUpRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class SitUpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SitUpRepository sitUpRepository;

    @Autowired
    private VideoRepository videoRepository;

    /**
     * Analyzes sit-up exercises from a video and saves the analysis to the associated user.
     *
     * @param videoId The ID of the video containing sit-up exercises.
     * @param userId The ID of the user performing the sit-up exercises.
     */
    public void analyzeSitUps(int videoId, int userId) {
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

        // Analyze sit-up exercises
        int repetitions = countSitUps(video); // This method counts the number of sit-ups
        double score = evaluateSitUp(video);  // This method evaluates the form and gives a score

        // Initialize and fill up the SitUp object
        SitUp sitUp = new SitUp(user, repetitions, score, new Date(), video.getLabel(), "User's sit-up performance analysis");

        // Save the SitUp object
        sitUpRepository.save(sitUp);
    }

    private int countSitUps(Video video) {
        // Implement the logic for counting sit-ups
        // This is a placeholder implementation
        return 10; // Temporarily returning a fixed number of sit-ups
    }

    private double evaluateSitUp(Video video) {
        // Implement the logic for evaluating the sit-up based on form and count
        // This is a placeholder implementation
        return 10.0; // Temporary score for the sit-up
    }
}
