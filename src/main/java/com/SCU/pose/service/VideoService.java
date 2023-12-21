package com.SCU.pose.service;

import com.SCU.pose.model.*;
import com.SCU.pose.repository.UserRepository;
import com.SCU.pose.repository.VideoRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class VideoService {

    @Autowired
    private PushUpService pushUpService;

    @Autowired
    private PlankService plankService;

    @Autowired
    private SitUpService sitUpService;

    @Autowired
    private GluteBridgeService gluteBridgeService;

    @Autowired
    private FlutterKicksService flutterKicksService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VideoRepository videoRepository;

    // Method to process video
    public String processVideo(byte[] videoBytes, int userId, String label) {
        // Find user by userId
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            // Handle user not found
            return "Invalid user";
        }

        // Create and save the video object
        Video video = new Video();
        video.setUser(user);
        video.setLabel(label); // Set the label for the video
        videoRepository.save(video);

        // Split the video into key frames (every 3 frames)
        List<byte[]> keyFrames = extractKeyFrames(videoBytes);

        // Process each frame
        for (byte[] frame : keyFrames) {
            Image image = new Image();
            List<Coordinate> coordinates = getCoordinatesFromFrame(frame);
            image.setCoordinates(coordinates);
            video.getImages().add(image); // Add image to video
        }

        // Analyze the video based on the label
        switch (label) {
            case "push_up":
                pushUpService.analyzePushups(video.getId(), userId);
                break;
            case "plank":
                plankService.analyzePlank(video.getId(), userId);
                break;
            case "sit_up":
                sitUpService.analyzeSitUps(video.getId(), userId);
                break;
            case "glute_bridge":
                gluteBridgeService.analyzeGluteBridges(video.getId(), userId);
                break;
            case "flutter_kick":
                flutterKicksService.analyzeFlutterKicks(video.getId(), userId);
                break;
            // ... 可以根据需要添加其他案例
        }

        // Save the updated video object to the database
        videoRepository.save(video);

        return "Video processed successfully";
    }

    private List<byte[]> extractKeyFrames(byte[] videoBytes) {
        List<byte[]> keyFrames = new ArrayList<>();
        Java2DFrameConverter converter = new Java2DFrameConverter();

        try (FFmpegFrameGrabber frameGrabber = new FFmpegFrameGrabber(new ByteArrayInputStream(videoBytes))) {
            frameGrabber.start();

            int frameCount = 0;
            while (frameGrabber.getFrameNumber() < frameGrabber.getLengthInFrames()) {
                org.bytedeco.javacv.Frame frame = frameGrabber.grabImage();
                if (frame == null) {
                    continue;
                }

                // Extract a key frame every 3 frames
                if (frameCount % 3 == 0) {
                    BufferedImage bufferedImage = converter.convert(frame);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpg", baos);
                    keyFrames.add(baos.toByteArray());
                }

                frameCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return keyFrames;
    }

    private List<Coordinate> getCoordinatesFromFrame(byte[] frame) {
        RestTemplate restTemplate = new RestTemplate();
        String endpoint = "http://127.0.0.1:5000/upload";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(frame, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(endpoint, requestEntity, String.class);

        // Check if response is OK
        if (response.getStatusCode() != HttpStatus.OK) {
            // Handle error response
            return new ArrayList<>();
        }

        // Parse the response to create Coordinate objects
        return parseCoordinates(response.getBody());
    }

    private List<Coordinate> parseCoordinates(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            List<Map<String, Double>> coordinatesList = mapper.readValue(json, new TypeReference<List<Map<String, Double>>>() {});

            List<Coordinate> coordinates = new ArrayList<>();
            String[] labels = {
                    "Nose", "Left Eye Inner", "Left Eye", "Left Eye Outer",
                    "Right Eye Inner", "Right Eye", "Right Eye Outer",
                    "Left Ear", "Right Ear", "Mouth Left", "Mouth Right",
                    "Left Shoulder", "Right Shoulder", "Left Elbow", "Right Elbow",
                    "Left Wrist", "Right Wrist", "Left Pinky", "Right Pinky",
                    "Left Index", "Right Index", "Left Thumb", "Right Thumb",
                    "Left Hip", "Right Hip", "Left Knee", "Right Knee",
                    "Left Ankle", "Right Ankle", "Left Heel", "Right Heel",
                    "Left Foot Index", "Right Foot Index"
            };
            for (int i = 0; i < coordinatesList.size(); i++) {
                Map<String, Double> coordinateMap = coordinatesList.get(i);
                Coordinate coordinate = new Coordinate(
                        labels[i],
                        coordinateMap.get("x"),
                        coordinateMap.get("y"),
                        coordinateMap.get("z"),
                        coordinateMap.get("visibility")
                );
                coordinates.add(coordinate);
            }
            return coordinates;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
