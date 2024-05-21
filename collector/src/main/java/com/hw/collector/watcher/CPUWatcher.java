package com.hw.collector.watcher;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author mrk
 * @create 2024-05-21-16:31
 */
public class CPUWatcher {

    private long[] lastCpuTimes;

    public double getCpuUsage() throws IOException {
        long[] cpuTimes = getCpuTimes();

        if (lastCpuTimes == null) {
            lastCpuTimes = cpuTimes;
            return 0.;
        }

        long idleTime = cpuTimes[3] - lastCpuTimes[3];
        long totalTime = getTotalTime(cpuTimes) - getTotalTime(lastCpuTimes);

        lastCpuTimes = cpuTimes;

        return 1.0 - (idleTime / (double) totalTime);
    }

    /**
     * 读取 "/proc/stat"文件 获取 "cpu " 开头行的 cpu 各类型时间
     * @return
     * @throws IOException
     */
    private long[] getCpuTimes() throws IOException {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("/proc/stat"));
            String line = bufferedReader.readLine();
            String[] tokens = line.split(" ");
            if (line != null && line.startsWith("cpu ")) {

                long[] cpuTimes = new long[tokens.length - 1];
                for (int i = 1; i < tokens.length; i++) {
                    cpuTimes[i - 1] = Long.parseLong(tokens[i]);
                }
                return cpuTimes;
            }
            return new long[tokens.length - 1];
        } catch (IOException e) {
            throw new IOException("Unable to read /proc/stat!!!");
        }
    }

    private long getTotalTime(long[] cpuTimes) {
        long totalTime = 0;
        for (long cpuTime : cpuTimes) {
            totalTime += cpuTime;
        }
        return totalTime;
    }
}
