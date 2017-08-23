package io.logz.jmx2graphite;

import java.util.List;

public class Jmx2GraphiteServiceConfiguration {

    public enum MetricClientType {
        UNKNOWN, JOLOKIA, MBEAN_PLATFORM
    }

    /* Short name of the sampled service, required = false */
    private String serviceName = null;

    /* host of the sampled service */
    private String serviceHost = null;

    private String jolokiaFullUrl = null;
    private List<String> domains = null;

    private MetricClientType metricClientType;

    public Jmx2GraphiteServiceConfiguration(MetricClientType metricClientType, String serviceName, String serviceHost, String jolokiaFullUrl, List<String> domains) {
        this.serviceHost = serviceHost;
        this.serviceName = serviceName;
        this.jolokiaFullUrl = jolokiaFullUrl;
        this.domains = domains;
        this.metricClientType = metricClientType;
    }

    public String getServiceHost() {
        return serviceHost;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getJolokiaFullUrl() {
        return jolokiaFullUrl;
    }

    public List<String> getDomains() {
        return domains;
    }

    public MetricClientType getMetricClientType() {
        return metricClientType;
    }
}
