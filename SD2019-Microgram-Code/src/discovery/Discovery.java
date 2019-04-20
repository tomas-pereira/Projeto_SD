package discovery;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class Discovery {

	private static Logger Log = Logger.getLogger(Discovery.class.getName());

	static {
		System.setProperty("java.net.preferIPv4Stack", "true");
		System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s\n");
	}
	
	
	static final InetSocketAddress DISCOVERY_ADDR = new InetSocketAddress("226.226.226.226", 2266);
	static final int DISCOVERY_PERIOD = 1000;
	static final int DISCOVERY_TIMEOUT = 30000;

	private static final String DELIMITER = "\t";
	private static final int MAX_DATAGRAM_SIZE = 65536;

	/**
	 * 
	 * Announces periodically a service in a separate thread .
	 * 
	 * @param serviceName the name of the service being announced.
	 * @param serviceURI the location of the service
	 */
	public static void announce(String serviceName, String serviceURI) {
		Log.info(String.format("Starting Discovery announcements on: %s for: %s -> %s", DISCOVERY_ADDR, serviceName, serviceURI));
		
		byte[] pktBytes = String.format("%s%s%s", serviceName, DELIMITER, serviceURI).getBytes();

		DatagramPacket pkt = new DatagramPacket(pktBytes, pktBytes.length, DISCOVERY_ADDR);
		new Thread(() -> {
			try (DatagramSocket ms = new DatagramSocket()) {
				for (;;) {
					ms.send(pkt);
					Thread.sleep(DISCOVERY_PERIOD);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
	}


	/**
	 * Performs discovery of instances of the service with the given name.
	 * 
	 * @param  serviceName the name of the service being discovered
	 * @param  minRepliesNeeded the required number of service replicas to find. 
	 * @return an array of URI with the service instances discovered. Returns an empty, 0-length, array if the service is not found within the alloted time.
	 * 
	 */
	public static URI[] findUrisOf(String serviceName, int minRepliesNeeded) {
		InetAddress group = DISCOVERY_ADDR.getAddress();
		URI[] uris = new URI[minRepliesNeeded];
		int counter = 0;
		List<String> list = new LinkedList<String>();
		long timeout = DISCOVERY_TIMEOUT;
		
		if( ! group.isMulticastAddress()) {
		    System.out.println( "Not a multicast address (use 226.226.226.226)");
		    System.exit( 1);
		}

		try( MulticastSocket socket = new MulticastSocket( DISCOVERY_ADDR.getPort() )) {
		    socket.joinGroup( group);
		    long starttime = System.currentTimeMillis();
		    while( true ) {
		        byte[] buffer = new byte[MAX_DATAGRAM_SIZE] ;
		        DatagramPacket request = new DatagramPacket( buffer, buffer.length ) ;
		        long elapsed = System.currentTimeMillis() - starttime;
		        socket.setSoTimeout((int) timeout);
		        socket.receive( request );
		        timeout = DISCOVERY_TIMEOUT - elapsed;
		        String req = new String( request.getData(), 0, request.getLength() ) ;
		        System.out.println(req);
		        String[] reqData = new String[2];
		        
		        reqData = req.split(DELIMITER);
		        
		        if(reqData[0].equalsIgnoreCase(serviceName)) {
		        	
		        	if (!list.contains(reqData[1])){
		        		
		        		uris[counter] = new URI(reqData[1]);
			        	counter ++;
			        	list.add(reqData[1]);
			        	
			        	if(counter == minRepliesNeeded) {
			        		break;
			        	}
			        	
		        	} 
		        	
		        }      
		        
		    }   
		    
		} catch (SocketTimeoutException e) {
			System.out.println("Timeout exceeded");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			System.out.println("Not valid URI");
		}
		
		return uris;
		
	}	
}
