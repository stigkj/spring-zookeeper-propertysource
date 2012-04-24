package org.springframework.config

import com.netflix.curator.framework.CuratorFramework
import com.netflix.curator.framework.CuratorFrameworkFactory
import com.netflix.curator.retry.RetryOneTime
import com.netflix.curator.test.TestingServer
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import spock.lang.Specification

/**
 * Unit test of {@link ZookeeperPropertySource}
 *
 * @author Stig Kleppe-Jorgensen, 2012.04.23
 */
class ZookeeperPropertySourceTest extends Specification {
    TestingServer server
    CuratorFramework client
    AnnotationConfigApplicationContext context

    def setup() {
        server = new TestingServer()

        client = CuratorFrameworkFactory.newClient(server.connectString, new RetryOneTime(1))
        client.start()
        client.create().forPath('/test', 'value'.bytes)

        System.setProperty(ZookeeperPropertySource.ZOOKEEPER_URL_ENV_KEY, server.connectString)

        context = new AnnotationConfigApplicationContext()
        context.environment.propertySources.addFirst(new ZookeeperPropertySource())
        context.register(PropertySourcesPlaceholderConfigurer.class)
        context.register(TestComponent.class)
        context.refresh()
    }

    def "should fail if Zookeeper URL is not set in the environment"() {
        when: "creating the property source without having the environment setup correctly"
        System.setProperty(ZookeeperPropertySource.ZOOKEEPER_URL_ENV_KEY, '')
        new ZookeeperPropertySource()

        then: "it should throw an exception"
        def e = thrown(IllegalStateException)
        e.message == 'spring.propertysource.zookeeper.url must be set'
    }

    def "should load properties from the given Zookeeper URL"() {
        when: "looking up a bean from an application context including the Zookeeper property source"
        def bean = context.getBean(TestComponent.class)

        then: "it should return the correct value from the Zookeeper instance"
        bean.getValueUsingValueAnnotationOnVariable == 'value'
        bean.getValueFromEnvironment() == 'value'
    }

    def cleanup() {
        client?.close()
        server?.stop()
    }
}
