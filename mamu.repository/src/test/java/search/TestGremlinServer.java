package search;


import java.net.URL;

import org.apache.tinkerpop.gremlin.driver.Client;
import org.apache.tinkerpop.gremlin.driver.Cluster;

public class TestGremlinServer {

	public static void main(String[] args) {
		try {
            URL file = TestGremlinServer.class.getResource("remote.yaml") ;
			Cluster cl = Cluster.open(file.getPath());
			Client client = cl.connect();
		    
			System.out.println(client.submit("1+1").all().join().get(0).getInt());
			client.close();
			cl.close();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
