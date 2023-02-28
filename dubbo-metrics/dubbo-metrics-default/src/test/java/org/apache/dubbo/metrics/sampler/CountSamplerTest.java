/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.dubbo.metrics.sampler;

import org.apache.dubbo.metrics.collector.sample.MetricsCountSampleConfigurer;
import org.apache.dubbo.metrics.collector.sample.SimpleMetricsCountSampler;
import org.apache.dubbo.metrics.model.MethodMetric;
import org.apache.dubbo.metrics.model.Metric;
import org.apache.dubbo.metrics.model.MetricsCategory;
import org.apache.dubbo.metrics.model.MetricsKey;
import org.apache.dubbo.metrics.model.sample.GaugeMetricSample;
import org.apache.dubbo.metrics.model.sample.MetricSample;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.apache.dubbo.metrics.model.MetricsCategory.RT;

public class CountSamplerTest {

    public RequestMetricsCountSampler sampler = new RequestMetricsCountSampler();

    @BeforeEach
    public void before() {
        sampler = new RequestMetricsCountSampler();
    }

    @Test
    public void rtTest() {
        String applicationName = "test";

        sampler.addRT(applicationName, RTType.METHOD_REQUEST, 2L);
        Map<String, GaugeMetricSample> collect = getCollect(RTType.METHOD_REQUEST);

        Assertions.assertNotNull(collect);

        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_LAST.getName()) && collect.get(
                MetricsKey.METRIC_RT_LAST.getName()).getSupplier().get().longValue() == 2);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_MIN.getName()) && collect.get(
                MetricsKey.METRIC_RT_MIN.getName()).getSupplier().get().longValue() == 2);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_MAX.getName()) && collect.get(
                MetricsKey.METRIC_RT_MAX.getName()).getSupplier().get().longValue() == 2);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_AVG.getName()) && collect.get(
                MetricsKey.METRIC_RT_AVG.getName()).getSupplier().get().longValue() == 2);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_SUM.getName()) && collect.get(
                MetricsKey.METRIC_RT_SUM.getName()).getSupplier().get().longValue() == 2);

        sampler.addRT(applicationName, RTType.METHOD_REQUEST, 1L);
        collect = getCollect(RTType.METHOD_REQUEST);

        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_LAST.getName()) && collect.get(
                MetricsKey.METRIC_RT_LAST.getName()).getSupplier().get().longValue() == 1);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_MIN.getName()) && collect.get(
                MetricsKey.METRIC_RT_MIN.getName()).getSupplier().get().longValue() == 1);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_MAX.getName()) && collect.get(
                MetricsKey.METRIC_RT_MAX.getName()).getSupplier().get().longValue() == 2);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_AVG.getName()) && collect.get(
                MetricsKey.METRIC_RT_AVG.getName()).getSupplier().get().longValue() == 1);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_SUM.getName()) && collect.get(
                MetricsKey.METRIC_RT_SUM.getName()).getSupplier().get().longValue() == 3);

        sampler.addRT(applicationName, RTType.APPLICATION, 4L);
        collect = getCollect(RTType.APPLICATION);

        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_LAST.getName()) && collect.get(
                MetricsKey.METRIC_RT_LAST.getName()).getSupplier().get().longValue() == 4);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_MIN.getName()) && collect.get(
                MetricsKey.METRIC_RT_MIN.getName()).getSupplier().get().longValue() == 4);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_MAX.getName()) && collect.get(
                MetricsKey.METRIC_RT_MAX.getName()).getSupplier().get().longValue() == 4);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_AVG.getName()) && collect.get(
                MetricsKey.METRIC_RT_AVG.getName()).getSupplier().get().longValue() == 4);
        Assertions.assertTrue(
            null != collect.get(MetricsKey.METRIC_RT_SUM.getName()) && collect.get(
                MetricsKey.METRIC_RT_SUM.getName()).getSupplier().get().longValue() == 4);
    }

    @NotNull
    private Map<String, GaugeMetricSample> getCollect(RTType rtType) {
        List<GaugeMetricSample> metricSamples = sampler.collectRT((key, metric, count) -> new GaugeMetricSample(key.formatName("consumer"), metric.getTags(), RT, () -> count),rtType);

        Map<String, GaugeMetricSample> collect = metricSamples.stream()
            .collect(Collectors.toMap(MetricSample::getName, v -> v));
        return collect;
    }

    private GaugeMetricSample getGaugeMetricSample(MetricsKey metricsKey, MethodMetric methodMetric,
                                                   MetricsCategory metricsCategory, Supplier<Number> get) {
        return new GaugeMetricSample(metricsKey.getNameByType(methodMetric.getSide()), metricsKey.getDescription(),
            methodMetric.getTags(), metricsCategory, get);
    }


    public class RequestMetricsCountSampler
        extends SimpleMetricsCountSampler<String, RTType, RequestMethodMetrics> {

        @Override
        public List<MetricSample> sample() {
            return null;
        }

        @Override
        protected void countConfigure(
            MetricsCountSampleConfigurer<String, RTType, RequestMethodMetrics> sampleConfigure) {
            sampleConfigure.configureMetrics(
                configure -> new RequestMethodMetrics(configure.getSource()));
            sampleConfigure.configureEventHandler(configure -> {
                System.out.println("generic event");
            });
        }

        @Override
        public void rtConfigure(
            MetricsCountSampleConfigurer<String, RTType, RequestMethodMetrics> sampleConfigure) {
            sampleConfigure.configureMetrics(configure -> new RequestMethodMetrics(configure.getSource()));
            sampleConfigure.configureEventHandler(configure -> {
                System.out.println("rt event");
            });
        }
    }

    static enum RTType{
        METHOD_REQUEST,
        APPLICATION
    }

    static class RequestMethodMetrics implements Metric {

        private String applicationName;

        public RequestMethodMetrics(String applicationName) {
            this.applicationName=applicationName;
        }
        @Override
        public Map<String, String> getTags() {
            Map<String,String> tags = new HashMap<>();
            tags.put("serviceName", "test");
            tags.put("version", "1.0.0");
            tags.put("uptime", "20220202");
            return tags;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (!(o instanceof RequestMethodMetrics))
                return false;
            RequestMethodMetrics that = (RequestMethodMetrics) o;
            return Objects.equals(applicationName, that.applicationName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(applicationName);
        }
    }

}
