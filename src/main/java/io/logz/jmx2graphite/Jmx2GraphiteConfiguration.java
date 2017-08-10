package io.logz.jmx2graphite;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.typesafe.config.Config;

import io.logz.jmx2graphite.Jmx2GraphiteServiceConfiguration.MetricClientType;

/**
 * @author amesika
 */
public class Jmx2GraphiteConfiguration {

    private Graphite graphite;

    private List<Jmx2GraphiteServiceConfiguration> services;

    /* Metrics polling interval in seconds */
    private int metricsPollingIntervalInSeconds;
    private int graphiteConnectTimeout;
    private int graphiteSocketTimeout;
    private int graphiteWriteTimeoutMs;

    // Which client should we use
    private GraphiteProtocol graphiteProtocol;

    public GraphiteProtocol getGraphiteProtocol() {
        return graphiteProtocol;
    }

    private class Graphite {
        public String hostname;
        public int port;
    }

    public Jmx2GraphiteConfiguration(Config config) throws IllegalConfiguration {

        services = new ArrayList<>();
        List<? extends Config> serviceConfigs = config.getConfigList("services");

        for (Config serviceConfig : serviceConfigs) {

            String serviceName = serviceConfig.getString("name");
            String serviceHost = null;
            String jolokiaFullUrl = null;
            MetricClientType metricClientType = MetricClientType.UNKNOWN;

            if (serviceConfig.hasPath("host")) {
                serviceHost = serviceConfig.getString("host");
            }

            if (serviceConfig.hasPath("poller.jolokia")) {
                metricClientType = MetricClientType.JOLOKIA;
            } else if (serviceConfig.hasPath("poller.mbean-direct")) {
                metricClientType = MetricClientType.MBEAN_PLATFORM;
            }

            if (metricClientType == MetricClientType.JOLOKIA) {
                jolokiaFullUrl = serviceConfig.getString("poller.jolokia.jolokiaFullUrl");
                String jolokiaHost;
                try {
                    URL jolokia = new URL(jolokiaFullUrl);
                    jolokiaHost = jolokia.getHost();
                } catch (MalformedURLException e) {
                    throw new IllegalConfiguration("service.jolokiaFullUrl must be a valid URL. Error = " + e.getMessage());
                }

                // Setting jolokia url as default
                if (serviceHost == null) {
                    serviceHost = jolokiaHost;
                }

            } else if (metricClientType == MetricClientType.MBEAN_PLATFORM) {

                // Try to find hostname as default to serviceHost in case it was not provided
                if (serviceHost == null) {
                    try {
                        serviceHost = InetAddress.getLocalHost().getHostName();
                    } catch (UnknownHostException e) {
                        throw new IllegalConfiguration("service.host was not defined, and could not determine it from the servers hostname");
                    }
                }
            }

            services.add(new Jmx2GraphiteServiceConfiguration(metricClientType, serviceHost, serviceName, jolokiaFullUrl));
        }

        graphite = new Graphite();
        graphite.hostname = config.getString("graphite.hostname");
        graphite.port = config.getInt("graphite.port");
        metricsPollingIntervalInSeconds = config.getInt("metricsPollingIntervalInSeconds");

        graphiteConnectTimeout = config.getInt("graphite.connectTimeout");
        graphiteSocketTimeout = config.getInt("graphite.socketTimeout");
        graphiteProtocol = getGraphiteProtocol(config);
        if (config.hasPath("graphite.writeTimeout")) {
            graphiteWriteTimeoutMs = config.getInt("graphite.writeTimeout");
        } else {
            graphiteWriteTimeoutMs = Math.round(0.7f * TimeUnit.SECONDS.toMillis(metricsPollingIntervalInSeconds));
        }
    }

    private GraphiteProtocol getGraphiteProtocol(Config config) {
        String protocol = config.hasPath("graphite.protocol") ? config.getString("graphite.protocol") : null;
        if (protocol != null) {
            return GraphiteProtocol.valueOf(protocol.toUpperCase());
        }
        return null;
    }

    public String getGraphiteHostname() {
        return graphite.hostname;
    }

    public void setGraphiteHostname(String graphiteHostname) {
        this.graphite.hostname = graphiteHostname;
    }

    public int getGraphitePort() {
        return graphite.port;
    }

    public void setGraphitePort(int graphitePort) {
        this.graphite.port = graphitePort;
    }

    public List<Jmx2GraphiteServiceConfiguration> getServices() {
        return services;
    }

    public int getMetricsPollingIntervalInSeconds() {
        return metricsPollingIntervalInSeconds;
    }

    public void setMetricsPollingIntervalInSeconds(int metricsPollingIntervalInSeconds) {
        this.metricsPollingIntervalInSeconds = metricsPollingIntervalInSeconds;
    }

    public void setGraphiteConnectTimeout(int graphiteConnectTimeout) {
        this.graphiteConnectTimeout = graphiteConnectTimeout;
    }

    public void setGraphiteSocketTimeout(int graphiteSocketTimeout) {
        this.graphiteSocketTimeout = graphiteSocketTimeout;
    }

    public void setGraphiteWriteTimeoutMs(int graphiteWriteTimeoutMs) {
        this.graphiteWriteTimeoutMs = graphiteWriteTimeoutMs;
    }

    public int getGraphiteConnectTimeout() {
        return graphiteConnectTimeout;
    }

    public int getGraphiteSocketTimeout() {
        return graphiteSocketTimeout;
    }

    public int getGraphiteWriteTimeoutMs() {
        return graphiteWriteTimeoutMs;
    }
}
