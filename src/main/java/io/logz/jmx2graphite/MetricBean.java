package io.logz.jmx2graphite;

import java.util.List;

public class MetricBean {

    private final String name;
    private final List<String> attributes;

    public MetricBean(String name, List<String> attributes) {
        super();
        this.name = name;
        this.attributes = attributes;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "MetricBean(name=" + this.name + ", attributes=" + this.attributes + ")";
    }
}
