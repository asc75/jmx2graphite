package io.logz.jmx2graphite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Created by roiravhon on 6/6/16.
 */
public class Jmx2GraphiteJolokia {

    private static final Logger logger = LoggerFactory.getLogger(Jmx2GraphiteJolokia.class);

    public static void main(String[] args) {

        if (System.getenv("J2G_CFG") != null) {
            System.setProperty("config.file", System.getenv("J2G_CFG"));
        }

        Config config = ConfigFactory.load();
        Jmx2GraphiteConfiguration jmx2GraphiteConfiguration = new Jmx2GraphiteConfiguration(config);
        Jmx2Graphite main = new Jmx2Graphite(jmx2GraphiteConfiguration);

        logger.info("Starting jmx2graphite using Jolokia-based poller");
        main.run();
    }
}
