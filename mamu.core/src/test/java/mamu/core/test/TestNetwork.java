/**
 * 
 */
package mamu.core.test;

import static org.junit.Assert.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.hive2hive.core.api.H2HNode;
import org.hive2hive.core.api.configs.FileConfiguration;
import org.hive2hive.core.api.configs.NetworkConfiguration;
import org.hive2hive.core.api.interfaces.IFileConfiguration;
import org.hive2hive.core.api.interfaces.IH2HNode;
import org.hive2hive.core.api.interfaces.INetworkConfiguration;
import org.junit.Before;
import org.junit.Test;

/**
 * @author johnny
 *
 */
public class TestNetwork {

	IH2HNode myrepostoryNode = null;
	IFileConfiguration fileConfig = FileConfiguration.createDefault();
	INetworkConfiguration netConfig = NetworkConfiguration.createInitial();
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// Create a new P2P network at the first (initial) peer
		System.out.println("#net config infomation:");
		System.out.println("BootstrapPort:"+netConfig.getBootstrapPort());
		System.out.println("nodeId:"+netConfig.getNodeID());
		System.out.println("port:"+netConfig.getPort());
		System.out.println("Peer:"+netConfig.getBootstrapPort());
		System.out.println("address:"+netConfig.getBootstrapAddress());
		System.out.println("#file config infomation:");
		System.out.println("maxFileSize:"+fileConfig.getMaxFileSize());
		System.out.println("chunkSize:"+fileConfig.getChunkSize());
		System.out.println("maxSizeAllVersions:"+fileConfig.getMaxSizeAllVersions());
		
		myrepostoryNode = H2HNode.createNode(fileConfig);
		myrepostoryNode.connect(netConfig);
	}

	@Test
	public void test() {
		NetworkConfiguration node2Conf = null;
		try {
			node2Conf = NetworkConfiguration.create(InetAddress.getLocalHost());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		IH2HNode node2 = H2HNode.createNode(fileConfig);
		node2.connect(node2Conf);
		
		System.out.println(node2.isConnected());
	}

}
