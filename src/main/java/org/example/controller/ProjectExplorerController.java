package org.example.controller;

import org.example.SuperMoneyAssignment.dto.Song;
import org.example.SuperMoneyAssignment.dto.User;
import org.example.SuperMoneyAssignment.dto.service.RecommendationService;
import org.example.SuperMoneyAssignment.dto.service.SongsUniverseService;
import org.example.SuperMoneyAssignment.dto.service.UserDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1")
public class ProjectExplorerController {

    private final RecommendationService recommendationService = new RecommendationService();
    private final SongsUniverseService songsUniverseService = SongsUniverseService.getInstance();
    private final UserDataService userDataService = UserDataService.getInstance();

    @GetMapping
    public ResponseEntity<Map<String, Object>> getApiOverview() {
        Map<String, Object> info = new LinkedHashMap<>();
        info.put("service", "Java Mini Projects Microservice");
        info.put("status", "UP");
        info.put("framework", "Spring Boot 3.2");
        info.put("endpoints", List.of(
                Map.of("method", "POST", "path", "/api/v1/webhooks/leads", "description", "Ingest lead webhook with HMAC-SHA256 signature verification and idempotency"),
                Map.of("method", "GET", "path", "/api/v1/modules", "description", "Overview of all contained Java modules and patterns"),
                Map.of("method", "GET", "path", "/api/v1/recommendations/{userName}", "description", "Song recommendations for a user"),
                Map.of("method", "GET", "path", "/actuator/health", "description", "Spring Boot Actuator health endpoint")
        ));
        return ResponseEntity.ok(info);
    }

    @GetMapping("/modules")
    public ResponseEntity<Map<String, Object>> getModules() {
        Map<String, Object> modules = new LinkedHashMap<>();
        modules.put("webhookDesign", List.of("HmacSecurityValidator", "IdempotencyRepository", "RequestDto", "WebhookController"));
        modules.put("lowLevelDesign", List.of("ParkingLot", "ParkingBuilding", "ParkingLevel", "EntranceGate", "ExitGate", "Payment", "PricingStrategy"));
        modules.put("superMoneyAssignment", List.of("RecommendationService", "SongsUniverseService", "UserDataService", "Song", "User"));
        modules.put("designPatterns", Map.of(
                "behavioural", List.of("State Pattern (VendingMachine)", "Strategy Pattern (Course Tax)"),
                "creational", List.of("Builder", "Factory / Abstract Factory", "Singleton (Eager, Lazy, Thread-Safe, Double-Checked)"),
                "structural", List.of("Adapter", "Bridge", "Decorator", "Proxy")
        ));
        modules.put("dataStructuresAndAlgorithms", List.of(
                "Arrays (MaxSubarraySum, Binary Search, Prefix Sum, Sliding Window, Two Pointer)",
                "LinkedList (DLL, DetectLoop, ReverseNodesInKGroup, RotateByK, Intersection)",
                "Recursion (Fibonacci, Palindrome, SumOfFirstXNumbers)",
                "Strings (LongestCommonPrefix)"
        ));
        return ResponseEntity.ok(modules);
    }

    @GetMapping("/recommendations/{userName}")
    public ResponseEntity<?> getSongRecommendations(@PathVariable String userName) {
        // Initialize default sample data if not already populated
        ensureSampleDataPopulated();

        User user = userDataService.getUserByName(userName);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "User not found",
                    "availableUsers", List.of("A", "B", "C", "D")
            ));
        }

        TreeMap<String, Float> recommendations = recommendationService.recommendedSongs(user);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("user", userName);
        List<String> friendNames = user.getFriendList() != null
                ? user.getFriendList().stream().map(User::getName).toList()
                : Collections.emptyList();
        response.put("friends", friendNames);
        response.put("recommendedSongsWithScore", recommendations);
        return ResponseEntity.ok(response);
    }

    private synchronized void ensureSampleDataPopulated() {
        if (userDataService.getUserByName("A") == null) {
            songsUniverseService.addSong("song1", "AB", "Folk", 60);
            songsUniverseService.addSong("song2", "DEF", "Rock", 70);
            songsUniverseService.addSong("song3", "AB", "Country", 55);
            songsUniverseService.addSong("song4", "XYZ", "Rock", 60);
            songsUniverseService.addSong("song5", "XYZ", "Rock", 75);
            songsUniverseService.addSong("song6", "AB", "Country", 60);
            songsUniverseService.addSong("song7", "DEF", "Indie", 55);

            userDataService.addUser("A", new String[]{"song1", "song2", "song3"}, new String[]{"B", "C"});
            userDataService.addUser("B", new String[]{"song6", "song7", "song3"}, new String[]{"A", "D"});
            userDataService.addUser("C", new String[]{"song4", "song3", "song6"}, new String[]{"A", "D"});
            userDataService.addUser("D", new String[]{"song7", "song3", "song1", "song2"}, new String[]{"B", "C"});
        }
    }
}
