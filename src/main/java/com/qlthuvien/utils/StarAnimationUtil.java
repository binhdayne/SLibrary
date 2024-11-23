package com.qlthuvien.utils;

import java.util.Random;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class StarAnimationUtil {
    private static final Random random = new Random(); 
    private static final int MAX_STARS = 20;
    private static final String[] STAR_SHAPES = {"★", "✦", "✧", "⋆", "✫"};

    public static void createStarAnimation(VBox starContainer) {
        
        // Check if starContainer is null
        if (starContainer == null) return;
        
        // Create star animation
        Timeline spawnTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> spawnStar(starContainer)),
            new KeyFrame(Duration.millis(30))
        );
        spawnTimeline.setCycleCount(Timeline.INDEFINITE);
        spawnTimeline.play();
    }

    private static void spawnStar(VBox container) {
        // If the number of stars exceeds the maximum, return nothing
        if (container.getChildren().size() >= MAX_STARS) return;
        
        // Create a star label
        Label star = new Label(STAR_SHAPES[random.nextInt(STAR_SHAPES.length)]);
        star.getStyleClass().add("star");

        double startX;
        double width = container.getWidth();
        double edgeZone = width * 0.1; // 10% của chiều rộng cho mỗi bên
        double middleZoneStart = edgeZone;
        double middleZoneEnd = width - edgeZone;
        
        // Print the container's width
        System.out.println("Container width: " + width);

        // Generate a random value to determine the position
        double randomValue = random.nextDouble();

        if (randomValue < 0.333) {
            // 33.3% chance to appear on the left
            startX = random.nextDouble() * edgeZone;
        } else if (randomValue < 0.666) {
            // 33.3% chance to appear in the middle
            startX = middleZoneStart + random.nextDouble() * (middleZoneEnd - middleZoneStart);
        } else {
            // 33.3% chance to appear on the right
            startX = middleZoneEnd + random.nextDouble() * edgeZone;
        }
        
        // Print the startX value
        System.out.println("StartX: " + startX);

        // Set the star's position
        star.setTranslateX(startX);
        star.setTranslateY(-20);

        container.getChildren().add(star);

        // Add animation for star
        ParallelTransition parallelTransition = new ParallelTransition();

        // Falling animation
        TranslateTransition fall = new TranslateTransition(Duration.seconds(3 + random.nextDouble() * 2), star);
        fall.setToY(container.getHeight() + 20);
        fall.setInterpolator(Interpolator.EASE_IN);

        // Rotate animation
        RotateTransition rotate = new RotateTransition(Duration.seconds(3), star);
        rotate.setByAngle(360 * (random.nextInt(3) + 1));
        rotate.setInterpolator(Interpolator.LINEAR);

        // Fade animation
        FadeTransition fade = new FadeTransition(Duration.seconds(3), star);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setDelay(Duration.seconds(2));

        // Scale animation
        ScaleTransition scale = new ScaleTransition(Duration.seconds(3), star);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.5);
        scale.setToY(0.5);
        
        // Add all animations to parallel transition
        parallelTransition.getChildren().addAll(fall, rotate, fade, scale);
        parallelTransition.setOnFinished(event -> container.getChildren().remove(star));
        parallelTransition.play();
    }
} 
