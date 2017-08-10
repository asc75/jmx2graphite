package io.logz.jmx2graphite;

public class Jmx2GraphiteServiceConfiguration {

    public enum MetricClientType {
        UNKNOWN, JOLOKIA, MBEAN_PLATFORM
    }

    /* Short name of the sampled service, required = false */
    private String serviceName = null;

    /* host of the sampled service */
    private String serviceHost = null;

    private String jolokiaFullUrl = null;

    private MetricClientType metricClientType;

    public Jmx2GraphiteServiceConfiguration(MetricClientType metricClientType, String serviceName, String serviceHost, String jolokiaFullUrl) {
        this.serviceHost = serviceHost;
        this.serviceName = serviceName;
        this.jolokiaFullUrl = jolokiaFullUrl;
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

    public MetricClientType getMetricClientType() {
        return metricClientType;
    }
}
