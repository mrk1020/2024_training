package com.hw.collector.watcher;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author mrk
 * @create 2024-05-21-15:30
 */
public class MetricCollector {

    private static final String SERVER_URL = "";

    /**
     * 指标的采集周期
     */
    private static final int INTERVAL = 60000;

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    collectAndSendMetrics();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }, 0, INTERVAL);
    }

    private static void collectAndSendMetrics() throws IOException {
        CPUWatcher cpuWatcher = new CPUWatcher();
        MemoryWatcher memoryWatcher = new MemoryWatcher();
        double cpuUsage = cpuWatcher.getCpuUsage();
        double memoryUsage = memoryWatcher.getMemoryUsage();

        InetAddress inetAddress = InetAddress.getLocalHost();
        String ip = inetAddress.getHostAddress();

        JSONArray metrics = new JSONArray();

        JSONObject cpuMetric = new JSONObject();
        cpuMetric.put("metric", "cpu.used.percent");
        cpuMetric.put("endpoint", ip);
        cpuMetric.put("timestamp", System.currentTimeMillis() / 1000);
        cpuMetric.put("step", 60);
        cpuMetric.put("value", cpuUsage);

        JSONObject memMetric = new JSONObject();
        memMetric.put("metric", "mem.used.percent");
        memMetric.put("endpoint", ip);
        memMetric.put("timestamp", System.currentTimeMillis() / 1000);
        memMetric.put("step", 60);
        memMetric.put("value", memoryUsage);

        metrics.add(cpuMetric);
        metrics.add(memMetric);

        sendMetrics(metrics);
    }

    private static void sendMetrics(JSONArray metrics) throws IOException {
        URL url = new URL(SERVER_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        OutputStream outputStream = connection.getOutputStream();
        outputStream.write(metrics.toString().getBytes());
        outputStream.flush();

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new RuntimeException("Failed! HTTP error code : " + connection.getResponseCode());
        }

        connection.disconnect();
    }
}
