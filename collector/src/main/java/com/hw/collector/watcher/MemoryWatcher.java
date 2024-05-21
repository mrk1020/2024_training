package com.hw.collector.watcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author mrk
 * @create 2024-05-21-17:39
 */
public class MemoryWatcher {

    public double getMemoryUsage() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/meminfo"));
        long memTotal = 0;
        long memAvailable = 0;
        String line = bufferedReader.readLine();
        while (line != null) {
            if (line.startsWith("MemTotal:")) {
                memTotal = parseMemValue(line);
            } else if (line.startsWith("MemAvailable:")) {
                memAvailable = parseMemValue(line);
            }
            if (memTotal > 0 && memAvailable > 0) {
                break;
            }
        }
        return 1.0 - (memAvailable / (double) memTotal);
    }

    private long parseMemValue(String line) {
        String[] tokens = line.split(" ");
        return Long.parseLong(tokens[1]) * 1024;
    }
}