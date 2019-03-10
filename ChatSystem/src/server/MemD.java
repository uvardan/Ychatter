package server;

import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class MemD {
	ServerConnect sc;
	ServerSocket serverSocket;
	Socket socket;
	public DatagramSocket udp;
	private static int port;
	public static Members member;
	static List<Members> memberList;
	public static String ScreenName = null;
	OutputStream os = null;
	String tcp_in_message = null;
	String tcp_out_message;
	String udp_out_message = null;
	byte[] sendbuffer = new byte[1024];

	public static void main(String[] args) {
		port = Integer.parseInt(args[0]);
		System.out.println("Connecting to port: " + port);
		new MemD();

	}

	public MemD() {
		member = new Members();
		memberList = new ArrayList<Members>();

		try {
			serverSocket = new ServerSocket(port);
			System.out.println("Connected!");
			while (true) {

				socket = serverSocket.accept();
				udp = new DatagramSocket();
				// Read message from client

				sc = new ServerConnect(socket, udp, this);
				sc.start();

			}

			// tcpListener(server_out_message);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}