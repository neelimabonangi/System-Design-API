package com.example.pickspot.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/pickSpot")
public class PickSpotController {

    private AtomicReference<String> yardMap = new AtomicReference<>("Initial Yard Map");

    @GetMapping
    public String pickSpot() {
        return "Best spot selected based on " + yardMap.get();
    }

    @GetMapping("/refresh")
    public String refreshYardMap() {
        yardMap.set("Refreshed Yard Map at " + System.currentTimeMillis());
        return "Yard map refreshed.";
    }
}
