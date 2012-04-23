package org.springframework.config;

import java.io.IOException;

import com.netflix.curator.framework.CuratorFramework;
import com.netflix.curator.framework.CuratorFrameworkFactory;
import com.netflix.curator.retry.RetryOneTime;
import org.springframework.core.env.PropertySource;

/**
 * @author Stig Kleppe-Jorgensen, 2012.04.23
 * @todo fill in
 */
public class ZookeeperPropertySource extends PropertySource<CuratorFramework> {
     public ZookeeperPropertySource(String name, String connectionString) throws IOException {
         super(name, CuratorFrameworkFactory.newClient(connectionString, new RetryOneTime(1)));
         // FIXME how to close it? Can we register for a Spring context shutdown event?
         source.start();
     }

     @Override
     public Object getProperty(String name) {
         try {
             return new String(source.getData().forPath(name));
         } catch (Exception e) {
             throw new IllegalStateException("Could not lookup property " + name, e);
         }
     }
 }
