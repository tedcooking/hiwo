package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Connection {
	/***************************************************************************
	 * Cette classe gere la connexion entre le client et le serveur via sockets
	 * Elle utilise le port 9001 pour communiquer sur le reseau 
	 ***************************************************************************/
	BufferedReader in;
    PrintWriter out;
    Socket socket;
    String serverAddress;
    int port;
    static Connection currentConnection;
    
	private Connection(String serverAddress)throws IOException {
        socket = new Socket(serverAddress, 9001);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        currentConnection = this;
	}
	
	private Connection(String serverAddress,int port)throws IOException {
        socket = new Socket(serverAddress, port);
        in = new BufferedReader(new InputStreamReader(
            socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        currentConnection = this;
	}
	
	static Connection doConnection(String serverAddress) throws IOException {
		if (currentConnection!=null){
			return currentConnection;
		}else {
			return new Connection(serverAddress);
		}
	}
	
	static Connection doConnection(String serverAddress,int port) throws IOException {
		if (currentConnection!=null){
			return currentConnection;
		}else {
			return new Connection(serverAddress,port);
		}
	}
	
	void closeConnection() throws IOException {
		in.close();
		out.close();
		socket.close();
		currentConnection = null;
	}
}
