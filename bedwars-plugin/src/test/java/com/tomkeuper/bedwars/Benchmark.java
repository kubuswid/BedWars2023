package com.tomkeuper.bedwars;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Benchmark {
    public static void main(String[] args) {
        int onlinePlayersCount = 1000;
        int viewersCount = 5;

        List<String> onlinePlayers = new ArrayList<>();
        for (int i = 0; i < onlinePlayersCount; i++) {
            onlinePlayers.add("Player" + i);
        }

        Set<String> viewers = new HashSet<>();
        for (int i = 0; i < viewersCount; i++) {
            viewers.add("Player" + i);
        }

        // Warmup
        for (int i = 0; i < 10000; i++) {
            testAll(onlinePlayers);
            testViewers(viewers);
        }

        long start1 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            testAll(onlinePlayers);
        }
        long end1 = System.nanoTime();

        long start2 = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            testViewers(viewers);
        }
        long end2 = System.nanoTime();

        System.out.println("Iterating all online players: " + (end1 - start1) / 1000000.0 + " ms");
        System.out.println("Iterating viewers only: " + (end2 - start2) / 1000000.0 + " ms");
    }

    private static void testAll(List<String> players) {
        int count = 0;
        for (String p : players) {
            if (p.startsWith("Player1")) {
                count++;
            }
        }
    }

    private static void testViewers(Set<String> players) {
        int count = 0;
        for (String p : players) {
            if (p.startsWith("Player1")) {
                count++;
            }
        }
    }
}
