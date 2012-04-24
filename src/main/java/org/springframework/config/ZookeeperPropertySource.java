package org.springframework.config;

import java.io.IOException;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryOneTime;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

/**
 * @author Stig Kleppe-Jorgensen, 2012.04.23
 * @todo fill in
 */
public class ZookeeperPropertySource extends PropertySource<CuratorFramework> {
    public static final String ZOOKEEPER_URL_ENV_KEY = "spring.propertysource.zookeeper.url";
    private static final String DEFAULT_NAME = "zookeeper";

    public ZookeeperPropertySource() throws IOException {
        this(DEFAULT_NAME, lookupConnectionString());
    }

    public ZookeeperPropertySource(String name, String connectionString) throws IOException {
        super(name, CuratorFrameworkFactory.newClient(connectionString, new RetryOneTime(1)));
        // FIXME how to close it? Can we register for a Spring context shutdown event?
        source.start();
    }

    @Override
    public Object getProperty(String name) {
        try {
            return new String(source.getData().forPath("/" + name));
        } catch (Exception e) {
            return null;
        }
    }

    private static String lookupConnectionString() {
        final String property = System.getProperty(ZOOKEEPER_URL_ENV_KEY);

        if (StringUtils.hasText(property)) {
            return property;
        } else {
            throw new IllegalStateException(ZOOKEEPER_URL_ENV_KEY + " must be set");
        }
    }
}
