package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.GluteBridgeRepository;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class GluteBridgeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GluteBridgeRepository gluteBridgeRepository;

    @Autowired
    private VideoRepository videoRepository;

    /**
     * Analyzes glute bridge exercises from a video and saves the analysis to the associated user.
     *
     * @param videoId The ID of the video containing glute bridge exercises.
     * @param userId The ID of the user performing the glute bridge exercises.
     */
    public void analyzeGluteBridges(int videoId, int userId) {
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

        // Analyze glute bridge exercises
        int repetitions = calculateGluteBridgeRepetitions(video); // Calculate the number of glute bridge repetitions
        double score = evaluateGluteBridge(video);                // Evaluate the form of the glute bridge

        // Initialize and fill up the GluteBridge object
        GluteBridge gluteBridge = new GluteBridge(user, repetitions, score, new Date(), video.getLabel(), "User's glute bridge performance analysis");

        // Save the GluteBridge object
        gluteBridgeRepository.save(gluteBridge);
    }

    private int calculateGluteBridgeRepetitions(Video video) {
        // Implement the logic for calculating the number of glute bridge repetitions
        // This is a placeholder implementation
        return 15; // Temporary number of repetitions
    }

    private double evaluateGluteBridge(Video video) {
        // Implement the logic for evaluating the glute bridge based on form and repetitions
        // This is a placeholder implementation
        return 10.0; // Temporary score for the glute bridge
    }
}
