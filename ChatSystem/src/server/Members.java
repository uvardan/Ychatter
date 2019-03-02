package server;

public class Members {
	
	String name;
	String IP;
	int port;
	
	public Members(String name, String IP,int port ) {
		this.name=name;
		this.IP=IP;
		this.port=port;
	}

	public Members() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIP() {
		return IP;
	}

	public void setIP(String iP) {
		IP = iP;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

}
