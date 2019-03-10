package client;
import java.net.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;
import java.io.*;

public class Chatter {
	ChatterConnection cc;
	// initialize socket and input output streams
	public static  Socket tcp_socket = null;
	private DatagramSocket udp_socket = null;
	private static InputStream is = null;
	private static OutputStream os = null;
	private String tcp_out_message = null;
	private String tcp_in_message = null;
	private String udp_in_message = null;
	public HashMap<Integer, String> records = null;
	public static HashMap<String, Integer> names = null;
	public static String ScreenName = null;
	public static String address = null;
	public static int port;
	private Scanner sc;

	public static void main(String args[]) {
		ScreenName = args[0];
		address = args[1];
		port = Integer.parseInt(args[2]);
		if (args.length != 3 || args == null) {
			System.out.println("Please enter screenname ip and port as an argument");
		} else {
			new Chatter();
		}

	}

	public Chatter() {

		try {
			names = new HashMap<String, Integer>();

			// Using InetAddress class provided by java represents Internet protocol convert
			// user input string IP address
			InetAddress IP_Address = InetAddress.getByName(address);

			// Creating a Scanner object to use scanner in future

			// TCP Connection Getting Established to the
			tcp_socket = new Socket(address, port);

			System.out.println("My port is " + tcp_socket.getLocalPort());
			// UDP connection getting Established.
			udp_socket = new DatagramSocket();
			os = tcp_socket.getOutputStream();

			tcp_out_message = "HELO " + ScreenName + " " + IP_Address.getHostAddress() + " " + udp_socket.getLocalPort()
					+ "\n";
			// Sending the HELO message to get the connection established with the server 
			tcpListener(tcp_out_message);
			// Creating an input stream for reading the message from TCP
			is = tcp_socket.getInputStream();
			InputStreamReader input_stream_reader = new InputStreamReader(is);
			BufferedReader buffer_reader = new BufferedReader(input_stream_reader);
			tcp_in_message = buffer_reader.readLine();
			
			// Validating unique screen name from server response
			if (tcp_in_message.startsWith("RJCT")) {
				tcp_in_message = tcp_in_message.replaceFirst("RJCT", "");
				tcp_in_message = tcp_in_message.replaceAll("\n", "");
				System.out.println(tcp_in_message + " is already in use.");
				System.exit(0);
			} else {
				// Creating a list of clients in the hash map
				records = new HashMap<Integer, String>();
				records = Records(tcp_in_message, records);
				Set<String> keys = names.keySet();
				for (String i : keys) {
					System.out.println(i + " is in the chatroom");
				}
				System.out.println(ScreenName + ":" + ScreenName + " accepted to the chatroom");
				cc = new ChatterConnection(udp_socket, this);
				cc.start();
				// 
				udpListener();

			}
		}

		catch (UnknownHostException u) {
			System.out.println(u);
		} catch (IOException i) {
			System.out.println(i);
		}

	}

	public static void exit() {
		try {

			is.close();
			os.close();
			tcp_socket.close();
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

	public void udpListener() {
		sc = new Scanner(System.in);
		while (true) {
			while (!sc.hasNextLine()) {

				String exit = "EXIT \n";
				tcpListener(exit);
				

			}
			String input = sc.nextLine();
			if (input != null) {

				udp_in_message = "MESG " + ScreenName + ": " + input + "\n";
				cc.sendAll(udp_in_message);
			}

		}

	}

	public static HashMap<Integer, String> Records(String ServerResponse, HashMap<Integer, String> Addressbook)
			throws UnknownHostException {

		String[] response_spilt = null;
		HashMap<Integer, String> Local_AddresBook = new HashMap<Integer, String>();
		Local_AddresBook = Addressbook;

		if (ServerResponse.startsWith("JOIN")) {
			ServerResponse = ServerResponse.replaceFirst("JOIN ", "");

		}
		if (ServerResponse.startsWith("ACPT")) {
			ServerResponse = ServerResponse.replaceFirst("ACPT ", "");

		}
		if (ServerResponse.contains("\n")) {
			ServerResponse = ServerResponse.replaceAll("\n", "");
		}
		if (ServerResponse.contains(":")) {
			ServerResponse = ServerResponse.replaceAll(":", " ");
		}

		response_spilt = ServerResponse.split(" ");

		for (int i = 0; i < response_spilt.length; i++) {

			String name = response_spilt[i];
			String IP_Address = response_spilt[i + 1];
			int port = Integer.parseInt(response_spilt[i + 2]);
			if (name.equals(ScreenName)) {
				// do nothing
			} else {
				names.put(name, port);
				Local_AddresBook.put(port, IP_Address);
			}
			i = i + 2;
		}

		return Local_AddresBook;
	}
}