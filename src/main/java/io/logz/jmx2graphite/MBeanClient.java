package io.logz.jmx2graphite;

import java.util.List;

/**
 * Created by roiravhon on 6/6/16.
 */
public abstract class MBeanClient {

    private Jmx2GraphiteServiceConfiguration service;

    protected MBeanClient(Jmx2GraphiteServiceConfiguration service) {
        this.service = service;
    }

    public abstract List<MetricBean> getBeans();

    public abstract List<MetricValue> getMetrics(List<MetricBean> beans);

    public Jmx2GraphiteServiceConfiguration getService() {
        return service;
    }

    public static class MBeanClientPollingFailure extends RuntimeException {

        public MBeanClientPollingFailure(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
