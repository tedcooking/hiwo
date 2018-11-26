package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import javafx.concurrent.Task;
import utility.Command;
import utility.Packet;
import utility.User;

public class MessageHandler {
	/*********************************************************************************
	 * Cette classe gere la reception de message par le client via un thread
	 * qui lis un integer sur le flux du socket, en recupere la commande 
	 * correspondante et ensuite execute la routine associée.
	 * Elle possede des fonctions pour envoyer des objets serialisés sur le reseau
	 ********************************************************************************/
	private Socket socket;
	private Client client;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	public MessageHandler(Client cl, Socket co){
		socket = co;
		client = cl;
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			ois = new ObjectInputStream(socket.getInputStream()); 			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	void sendObject(Object s) throws IOException {
			oos.writeObject(s);
			oos.flush();						
	}
	
	Object readObject() throws ClassNotFoundException, IOException {
			Object to = ois.readObject();
			return to;			
	}

	private Thread grabMessages = new Thread(new Task<Void>() {
        protected Void call() {       	
        	while (true) {        		
        		try {
        			int i = ois.readInt();        			
        			Command cmd = Command.getCommand(i);
        			Packet pckt=null;
        			
            	    switch(cmd) {
            	    	case PASSWORD:
            	    		client.submitpassword();
                    	break;
	                    case SUBMITNAME:
	                    	client.submitname();
	                    	break;
	                    case UPDATEUSERS:
	                    	Vector<User> userList = (Vector<User>)readObject();             	                    	
	                    	client.updateUserList(userList);
	                    	break;
	                    case MESSAGE:    	
	                    	pckt = (Packet)readObject();
	                    	client.addMessage(pckt);
	                    	break;
	                    case UPDATEUSERNAME:
	                    	pckt = (Packet)readObject();
	                    	pckt.getUser().setName(pckt.getContent());
	                    	client.refreshUsers();
	                    	break;
	                    case UPDATEUSERINFO:
	                    	pckt = (Packet)readObject();
	                    	pckt.getUser().setInfo(pckt.getContent());
	                    	client.refreshUsers();
	                    	break;
	    				default:
	    					break;
            	    }
        		} catch (IOException e) {
        			System.exit(0);
        		} catch (ClassNotFoundException e) {
        			e.printStackTrace();
				}		
            }
        }
   		});
	
	public void run (){
		grabMessages.setDaemon(true);
		grabMessages.start();
	}
}
