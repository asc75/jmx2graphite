package io.logz.jmx2graphite;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.logz.jmx2graphite.Jmx2GraphiteServiceConfiguration.MetricClientType;

/**
 * @author amesika
 */
public class Jmx2Graphite {

    private static final Logger logger = LoggerFactory.getLogger(Jmx2Graphite.class);

    private final Jmx2GraphiteConfiguration conf;
    private final ScheduledThreadPoolExecutor taskScheduler;
    private final List<MBeanClient> clients;

    public Jmx2Graphite(Jmx2GraphiteConfiguration conf) {
        this.conf = conf;

        this.clients = new ArrayList<>();
        for (Jmx2GraphiteServiceConfiguration service : conf.getServices()) {
            if (service.getMetricClientType() == MetricClientType.JOLOKIA) {
                this.clients.add(new JolokiaClient(service));
                logger.info("Running with Jolokia URL: {}", service.getJolokiaFullUrl());
            } else if (service.getMetricClientType() == MetricClientType.MBEAN_PLATFORM) {
                this.clients.add(new JavaAgentClient(conf.getServices().get(0)));
                logger.info("Running with Mbean client");
            } else {
                throw new IllegalConfiguration("Unsupported client type: " + service.getMetricClientType());
            }
        }

        this.taskScheduler = new ScheduledThreadPoolExecutor(clients.size());
    }

    public void run() {
        logger.info("Graphite: host = {}, port = {}", conf.getGraphiteHostname(), conf.getGraphitePort());
        enableHangupSupport();
        for (MBeanClient client : clients) {
            MetricsPipeline pipeline = new MetricsPipeline(conf, client);
            taskScheduler.scheduleWithFixedDelay(pipeline::pollAndSend, 0, conf.getMetricsPollingIntervalInSeconds(), TimeUnit.SECONDS);
        }
    }

    private void shutdown() {
        logger.info("Shutting down...");
        try {
            taskScheduler.shutdown();
            taskScheduler.awaitTermination(20, TimeUnit.SECONDS);
            taskScheduler.shutdownNow();
        } catch (InterruptedException e) {

            Thread.interrupted();
            taskScheduler.shutdownNow();
        }
    }

    /**
     * Enables the hangup support. Gracefully stops by calling shutdown() on a
     * Hangup signal.
     */
    private void enableHangupSupport() {
        HangupInterceptor interceptor = new HangupInterceptor(this);
        Runtime.getRuntime().addShutdownHook(interceptor);
    }

    /**
     * A class for intercepting the hang up signal and do a graceful shutdown of
     * the Camel.
     */
    private static final class HangupInterceptor extends Thread {
        private Logger logger = LoggerFactory.getLogger(HangupInterceptor.class);
        private Jmx2Graphite main;

        public HangupInterceptor(Jmx2Graphite main) {
            this.main = main;
        }

        @Override
        public void run() {
            logger.info("Received hang up - stopping...");
            try {
                main.shutdown();
            } catch (Exception ex) {
                logger.warn("Error during stopping main", ex);
            }
        }
    }
}
