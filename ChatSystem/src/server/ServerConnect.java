package server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

public class ServerConnect extends Thread {
	Socket socket;
	DatagramSocket udp;
	MemD server;
	OutputStream os = null;
	Members member;
	boolean isrunning = true;
	List<Members> memberList;
	public static String ScreenName = null;
	byte[] sendbuffer = new byte[1024];
	String tcp_in_message = null;
	String tcp_out_message;
	String udp_out_message = null;
	public static HashMap<Integer, String> names;
	public HashMap<Integer, String> records;

	public ServerConnect(Socket socket, DatagramSocket udp, MemD server) {
		super("Super");
		this.socket = socket;
		this.udp = udp;
		this.server = server;
		member = MemD.member;
		memberList = MemD.memberList;
	}

	public void run() {

		try {

			while (isrunning) {
				
				InputStream is = socket.getInputStream();
				InputStreamReader input_stream_reader = new InputStreamReader(is);
				BufferedReader buffer_reader = new BufferedReader(input_stream_reader);
				tcp_in_message = buffer_reader.readLine();
				//	tcp_in_message = din.readLine();					
					if (tcp_in_message != null) {
						if (tcp_in_message.startsWith("EXIT")) {
							tcp_out_message = "EXIT " + ScreenName;
							sendAllMembers(tcp_out_message);
							memberList.remove(member);
							isrunning=false;
						}

						if (tcp_in_message.startsWith("HELO ")) {
							String temp = tcp_in_message.replaceFirst("HELO ", "");
							temp = temp.replaceFirst("\n", "");
							String[] server_in = temp.split(" ");
							ScreenName = server_in[0];
							String IP = server_in[1];
							int port = Integer.parseInt(server_in[2]);
							member = new Members(ScreenName, IP, port, socket.getPort());
							boolean rep = false;
							for (Members m : memberList) {
								if (m.name.equals(member.name)) {
									tcp_out_message = "RJCT " + ScreenName + "\n";
									tcpWriter(tcp_out_message);
									rep = true;
									isrunning = false;

									// check if member exist
								}
							}
							if (rep == false) {
								memberList.add(member);
								tcp_out_message = TcpMessage(memberList);
								// tcp_out_message=TCPMessage(records);
								tcpWriter(tcp_out_message);
								tcp_in_message = tcp_in_message.replaceAll("HELO ", "JOIN ");
								sendAllMembers(tcp_in_message);

							}
						}
					}
				}
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void tcpWriter(String message) {

		try {
			os = socket.getOutputStream();
			OutputStreamWriter output_stream_writer = new OutputStreamWriter(os);
			BufferedWriter buffer_writer = new BufferedWriter(output_stream_writer);
			buffer_writer.write(message);
			buffer_writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public String TcpMessage(List<Members> MemberList) {

		String message = "ACPT ";
		int number = MemberList.size() - 1;
		for (int i = number; i >= 0; i--) {
			String Name = MemberList.get(i).name + " ";
			String IP = MemberList.get(i).IP + " ";
			String Port = Integer.toString(MemberList.get(i).port);
			message = message + Name + IP + Port;

			if (i != 0) {
				message = message + ":";
			}
		}
		message = message + "\n";

		return message;

	}

	public void sendAllMembers(String message) {

		sendbuffer = message.getBytes();
		for (Members i : memberList) {
			InetAddress IP;

			try {
				IP = InetAddress.getByName(i.getIP());
				int port = i.getPort();
				DatagramPacket send = new DatagramPacket(sendbuffer, sendbuffer.length, IP, port);
				try {
					udp.send(send);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
