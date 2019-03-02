import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Set;

// Referenced from Gilderman's Channel and implemented something similar to that 

public class ClientConnection extends Thread {

	
	DatagramSocket socket;
	DatagramPacket recievePacket;
	DatagramPacket sendPacket;
	byte [] sendbuffer = new byte[1024];
	byte [] recievebuffer = new byte [1024];
	public HashMap<Integer, String> records;
	public HashMap<String, Integer> names;
	
	public ClientConnection(DatagramSocket udp, YChatterCLI client) {
		socket=udp;
		records=client.records;
		names=YChatterCLI.names;
		
	}
	public void run() {
		try {
			recievePacket= new DatagramPacket(recievebuffer,recievebuffer.length);
			while(true) {
			
				
				socket.receive(recievePacket);
				if(recievePacket!=null) {
					String udp_in_message = new String(recievePacket.getData(),0,recievePacket.getLength());
					if(udp_in_message.startsWith("JOIN ")) {
						udp_in_message=udp_in_message.replaceFirst("JOIN ", "");
						udp_in_message=udp_in_message.replaceAll("\n", "");
						String [] udp_join_break=udp_in_message.split(" ");
						String name=udp_join_break[0];
						String ip=udp_join_break[1];
						int port=Integer.parseInt(udp_join_break[2]);
						if(name.equals(Client.ScreenName)) {
							//do nothing
						}else {
							names.put(name, port);
							records.put(port,ip);
							System.out.println(name +" has joined the chatroom");
						}
							
						
						

					}
					
					if(udp_in_message.startsWith("EXIT")) {
						
						udp_in_message=udp_in_message.replaceFirst("EXIT ", "");
						udp_in_message=udp_in_message.replaceAll("\n", "");
						if(udp_in_message.equals(Client.ScreenName)) {
							System.out.println("Good Bye");
							socket.close();
							System.exit(0);
							
						}else {
						int port= names.get(udp_in_message);
						records.remove(port);
						names.remove(udp_in_message);
						System.out.println(udp_in_message + " has left the chatroom");
						}
					}
					if(udp_in_message.startsWith("MESG")) {
						udp_in_message=udp_in_message.replaceFirst("MESG", "");
						System.out.println(udp_in_message);
					}
			}
			
		}
		
	}catch(Exception e) {
		e.printStackTrace();
	}
		
}
//Referenced from Gilderman's youtube channel and implemented something similar
	public void sendAll(String text) {
		sendbuffer=text.getBytes();
		Set<Integer> keys =records.keySet();
		for(Integer i:keys) {
			InetAddress IP;
			try {
				IP = InetAddress.getByName(records.get(i));
				if(i==socket.getLocalPort()) {
					//do nothing
				}else {
					DatagramPacket sendPacket= new DatagramPacket(sendbuffer, sendbuffer.length, IP,i);
					try {
						socket.send(sendPacket);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
		
	}
}

