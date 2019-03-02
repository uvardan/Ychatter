import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

public class YChatterCLI {

	ClientConnection cc;
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

	public static void main(String args[]) {
		ScreenName = args[0];
		address = args[1];
		port = Integer.parseInt(args[2]);
		if (args.length != 3 || args == null) {
			System.out.println("Please enter screenname ip and port as an argument");
		} else {
			new YChatterCLI();
		}

	}

	public YChatterCLI() {

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
			tcpListener(tcp_out_message);

			is = tcp_socket.getInputStream();
			InputStreamReader input_stream_reader = new InputStreamReader(is);
			BufferedReader buffer_reader = new BufferedReader(input_stream_reader);
			tcp_in_message = buffer_reader.readLine();
			if (tcp_in_message.startsWith("RJCT")) {
				tcp_in_message = tcp_in_message.replaceFirst("RJCT", "");
				tcp_in_message = tcp_in_message.replaceAll("\n", "");
				System.out.println(tcp_in_message + " is already in use.");
				System.exit(0);
			} else {

				records = new HashMap<Integer, String>();
				records = Records(tcp_in_message, records);
				Set<String> keys = names.keySet();
				for (String i : keys) {
					System.out.println(i + " is in the chatroom");
				}
				System.out.println(ScreenName + ":" + ScreenName + " accepted to the chatroom");
				cc = new ClientConnection(udp_socket, this);
				cc.start();

				udpListener();

			}
			exit();
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
		Scanner sc = new Scanner(System.in);
		while (true) {
			while (!sc.hasNextLine()) {

				String exit = "Exit \n";
				tcpListener(exit);
				Thread.sleep(1);
				sc.close();

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
