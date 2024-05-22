package com.hw.server.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hw.server.domain.Metrics;
import com.hw.server.domain.dto.Result;
import com.hw.server.mapper.MetricsMapper;
import com.hw.server.service.IMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.hw.server.constants.RedisConstants.CACHE_METRIC_KEY;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author mrk
 * @since 2024-05-22
 */
@Service
@RequiredArgsConstructor
public class MetricsServiceImpl extends ServiceImpl<MetricsMapper, Metrics> implements IMetricsService {

    private final StringRedisTemplate stringRedisTemplate;

    private static final int THRESHOLD = 10;

    @Override
    public Result<?> uploadMetrics(List<Metrics> metrics) {
        for (Metrics metric : metrics) {
            // 1. 将数据存入 mysql 数据库
            this.save(metric);
            // 2. 将数据存入 redis 缓存，缓存最近的十条数据
            // 使用当前时间戳为 score
            // TODO: zset默认按照 score 从小到大排序，应优化成从大到小排序
            stringRedisTemplate.opsForZSet().add(CACHE_METRIC_KEY, JSONUtil.toJsonStr(metric), System.currentTimeMillis());
            int nums = stringRedisTemplate.opsForZSet().zCard(CACHE_METRIC_KEY).intValue();
            if (nums > THRESHOLD) {
                stringRedisTemplate.opsForZSet().removeRange(CACHE_METRIC_KEY, 0, nums - THRESHOLD - 1);
            }
        }
        return Result.ok();
    }

    @Override
    public Result<?> queryMetrics(String endpoint, String metric, Long startTs, Long endTs) {
        List<Metrics> metricsList = this.list(new LambdaQueryWrapper<Metrics>()
                .eq(Metrics::getEndpoint, endpoint)
                .eq(Metrics::getMetric, metric)
                .between(Metrics::getTimestamp, startTs, endTs));
        return Result.ok(metricsList);
    }
}
