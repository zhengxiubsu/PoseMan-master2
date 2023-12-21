package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.PlankRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PlankService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlankRepository plankRepository;

    @Autowired
    private VideoRepository videoRepository;

    /**
     * Analyzes plank exercises from a video and saves the analysis to the associated user.
     *
     * @param videoId The ID of the video containing plank exercises.
     * @param userId The ID of the user performing the plank exercises.
     */
    public void analyzePlank(int videoId, int userId) {
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

        // Analyze plank exercises
        int duration = calculatePlankDuration(video); // Calculate the duration of the plank
        double score = evaluatePlank(video);          // Evaluate the form of the plank

        // Initialize and fill up the Plank object
        Plank plank = new Plank(user, duration, score, new Date(), video.getLabel(), "User's plank performance analysis");

        // Save the Plank object
        plankRepository.save(plank);
    }

    private int calculatePlankDuration(Video video) {
        // Implement the logic for calculating the duration of the plank
        // This is a placeholder implementation
        return 60; // Temporary duration in seconds
    }

    private double evaluatePlank(Video video) {
        // Implement the logic for evaluating the plank based on form and duration
        // This is a placeholder implementation
        return 10.0; // Temporary score for the plank
    }
}
