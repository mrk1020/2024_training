package com.hw.server.controller;


import com.hw.server.domain.Metrics;
import com.hw.server.domain.dto.Result;
import com.hw.server.service.IMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author mrk
 * @since 2024-05-22
 */
@RestController
@RequestMapping("/api/metric")
@RequiredArgsConstructor
public class MetricsController {

    private final IMetricsService metricsService;

    @PostMapping("/upload")
    public Result uploadMetrics(@RequestBody List<Metrics> metrics) {
        return metricsService.uploadMetrics(metrics);
    }

    @GetMapping("/query")
    public Result queryMetrics(@RequestParam String endpoint,
                               @RequestParam String metric,
                               @RequestParam Long startTs,
                               @RequestParam Long endTs) {
        return metricsService.queryMetrics(endpoint, metric, startTs, endTs);
    }

}
