package com.example.herokupipeexample;

import com.codahale.metrics.MetricAttribute;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Configuration
public class GraphiteMetricsConfig {

    @Bean
    public MetricRegistry getRegistry() {
        return new MetricRegistry();
    }

    @Bean
    public GraphiteReporter getReporter(MetricRegistry registry) {
        final Set<MetricAttribute> disabledAttributes = new HashSet<>();
        disabledAttributes.addAll(Arrays.asList(
                MetricAttribute.M1_RATE,
                MetricAttribute.M15_RATE,
                MetricAttribute.MEAN_RATE,
                MetricAttribute.MIN,
                MetricAttribute.MEAN,
                MetricAttribute.STDDEV,
                MetricAttribute.P98,
                MetricAttribute.P95,
                MetricAttribute.P75,
                MetricAttribute.P99));

        Graphite graphite = new Graphite(new InetSocketAddress("carbon.hostedgraphite.com", 2003));
        GraphiteReporter reporter = GraphiteReporter.forRegistry(registry)
                .prefixedWith(System.getenv("HOSTEDGRAPHITE_APIKEY"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .filter(MetricFilter.ALL)
                .disabledMetricAttributes(disabledAttributes)
                .build(graphite);
        reporter.start(1, TimeUnit.SECONDS);
        return reporter;
    }
}
