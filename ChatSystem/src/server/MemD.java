package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class MemD {
	ServerSocket serverSocket;
	Socket socket;
	DataInputStream din;
	DataOutputStream dout;
	private static int port;
	private static InputStream is = null;
	private static Members member;
	public static HashMap<Integer, String> names = null;
	public static String ScreenName = null;
	public HashMap<Integer, String> records = null;
	private static OutputStream os = null;
	private String tcp_out_message = null;
	private String server_out_message=null;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 port = Integer.parseInt(args[0]);
		 System.out.println("Attempting to connect to port: "+port);
		 new MemD();
		

	}
	
	public MemD() {
		
		try {
			serverSocket = new ServerSocket(port);
			socket=serverSocket.accept();
			// Read message from client
			is = socket.getInputStream();
			InputStreamReader input_stream_reader = new InputStreamReader(is);
			BufferedReader buffer_reader = new BufferedReader(input_stream_reader);
			String server_in_message = buffer_reader.readLine();
			if(!(server_in_message.startsWith("HELO "))) {
			System.out.println(server_in_message);
			records=Records(server_in_message, records);
			 server_out_message=TCPMessage(records);
			 tcpListener(server_out_message);
			}else {
				// Get the screen name from the server in message
				// Send it to the client 
				 server_out_message="Reject "+server_in_message;
				
			}
			//Respond to Client
			os=socket.getOutputStream();
			tcpListener(server_out_message);
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void tcpListener(String message) {
		OutputStreamWriter output_stream_writer = new OutputStreamWriter(os);
		BufferedWriter buffer_writer = new BufferedWriter(output_stream_writer);
		try {
			buffer_writer.write(message);
			buffer_writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public String TCPMessage(HashMap<Integer, String> MemberList) {
		String message="ACPT ";
		//String temp="";
		Set<Integer> keys =MemberList.keySet();
		for(Integer i:keys) {
			String Name=names.get(i)+" ";
			String IP=MemberList.get(i)+" ";
			String Port=Integer.toString(i)+":";
			message=message+Name+IP+Port;
		}
		message=message+"\n";
		return message;
		
	}
	public List<Members> MemberList(){
		List<Members> m = new ArrayList<Members>();
		
		return m;
		
	}
	public static HashMap<Integer, String> Records(String ClientInput, HashMap<Integer, String> AddressBook)
			throws UnknownHostException {
	//	String response="HELO Anne 127.0.0.1 63912";
		member=new Members();
		String[] response_spilt = null;
		HashMap<Integer, String> Local_AddresBook = new HashMap<Integer, String>();
		Local_AddresBook = AddressBook;

		if (ClientInput.startsWith("HELO")) {
			ClientInput = ClientInput.replaceFirst("HELO ", "");

		}
		
		if (ClientInput.contains("\n")) {
			ClientInput = ClientInput.replaceAll("\n", "");
		}
		

		response_spilt = ClientInput.split(" ");

		for (int i = 0; i < response_spilt.length; i++) {
			
			String name = response_spilt[i];
			member.setName(name);
			String IP_Address = response_spilt[i + 1];
			member.setIP(IP_Address);
			int port = Integer.parseInt(response_spilt[i + 2]);
			member.setPort(port);
			if (name.equals(ScreenName)) {
				// do nothing
			} else {
				names.put(port,name);
				Local_AddresBook.put(port, IP_Address);
			}
			i = i + 2;
		}

		return Local_AddresBook;
	}
	public void readClient() {
		while(true) {
			try {
				while(din.available()==0) {
					try {
						Thread.sleep(1);
					}catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				String input =din.readUTF();
				System.out.println(input);
			}catch(IOException i) {
				i.printStackTrace();
				break;
			}
		}
	}

}
