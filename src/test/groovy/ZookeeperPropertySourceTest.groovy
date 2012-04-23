import com.netflix.curator.framework.CuratorFramework
import com.netflix.curator.framework.CuratorFrameworkFactory
import com.netflix.curator.retry.RetryOneTime
import com.netflix.curator.test.TestingServer
import org.springframework.config.ZookeeperPropertySource
import spock.lang.Specification

/**
 * @author Stig Kleppe-Jorgensen, 2012.04.23
 * @todo fill in
 */
class ZookeeperPropertySourceTest extends Specification {
    TestingServer server
    CuratorFramework client

    def setup() {
        server = new TestingServer()

        client = CuratorFrameworkFactory.newClient(server.connectString, new RetryOneTime(1))
        client.start()
        client.create().forPath('/test', 'data'.bytes)
    }

    def "should load properties from zookeeper"() {
        expect:
        def source = new ZookeeperPropertySource("test", server.connectString)

        source.getProperty('/test') == 'data'
    }

    def cleanup() {
        client.close()
        server?.stop()
    }
}
