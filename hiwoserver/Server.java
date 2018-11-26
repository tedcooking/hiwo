package hiwoserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.URL;
import java.util.Vector;

import utility.Command;
import utility.Packet;
import utility.User;

public class Server extends Thread {
	/*************************************************************************
	 * Cette classe ouvre un socket pour chaque utilisateur qui se connecte. 
	 * Chaque utilisateur est geré par sa classe UserHandler, le serveur en
	 * possede la liste et intervient pour gerer la diffusion de messages
	 * et les connexions ou déconnexions
	 **************************************************************************/
	private ServerIHM serverIHM;
    Vector<UserHandler> UserHandlerList;
    private static final int PORT = 9001;
    final private static User me = new User("SERVER","I am Server"); 
    private ServerSocket listener = null;
    private String password="";

	public void setPassword(String password) {
		this.password = password;
	}
	// parametrer l'interface grahique du serveur
    void setIHM(ServerIHM s) {
    	serverIHM =s;
    }    
    //Verifie si un nom est conforme
	boolean checkName(String name) {
		if (name != null
				&&!(name.equalsIgnoreCase(""))
				&&!(name.equalsIgnoreCase(me.getName()))) {
			return true;
		}
		return false;
	}
	//Verifie le password fourni
	boolean checkPassword(String psswd) {
		if(psswd.equals(password))return true;
		return false;
	}
    // Enregistre un utilisateur sur le serveur
    void registerUserHandler(UserHandler usr) {
    	UserHandlerList.add(usr);
    	updateUserLists();
    	broadcoast(new Packet(me, Command.BROADCOAST, usr.getUser().getName()+ " has entered the chat."));
    }
    
    //Supprime un utilisateur du serveur
    void removeUserHandler(UserHandler usr) {
    	usr.disconnect();
		UserHandlerList.remove(usr);
    	cout(usr.getUser().getName() + " has disconnected.");
    	updateUserLists();
    }
    // Retourne un Vector<User> serializable
    Vector<User> getUserList(){
    	Vector<User> result = new Vector<User>();
    	for (UserHandler UserHandler : UserHandlerList) {
    		 if(UserHandler.isReady())result.add(UserHandler.getUser());
    	} 
    	return result;
    }
    
    // Envoi à l'ensemble des utilisateurs connectés et loggés correctements la liste des utilisateurs.
    void updateUserLists() {	
        for (UserHandler usr : UserHandlerList) {
        	 if(usr.isReady())
				try {
					usr.updateUserList(getUserList());					
				} catch (IOException e) {
					e.printStackTrace();
					cout("An error has occured while broadcoasting user list");
				}
   		 	}
        serverIHM.refreshUsers();
        }
    
    // Envoi à l'ensemble des utilisateurs connectés et loggés correctements l'information qu'un utilisateur a MAJ ses informations (nom ou info)
    void updateUserData(Packet m) {
    	serverIHM.refreshUsers();
    	for (UserHandler usr : UserHandlerList) {
   		 if (usr.isReady())
				try {
					usr.updateUserData(m);
					usr.sendPacket(new Packet(me, Command.BROADCOAST, m.getUser().getName() +" has updated his info."));
				} catch (IOException e) {					
					cout("An error has occured while sending updated user data");
				}
   		 } 
    }      
    // Envoi à l'ensemble des utilisateurs connectés et loggés correctements un message.
    void broadcoast(Packet pckt) {
    	 for (UserHandler usr : UserHandlerList) {
    		 if (usr.isReady())
				try {
					usr.sendPacket(pckt);
				} catch (IOException e) {					
					cout("An error has occured while broadcoasting a packet");
				}
    		 } 
    }
    //Affiche dans l'IHM les messages console
    void cout(String s) {
    	serverIHM.consolePrint(s);
    }
    
    // merci stackoverflow :)
    // https://stackoverflow.com/questions/2939218/getting-the-external-ip-address-in-java
    // Renvois un string avec l'adresse ip public de la machine sur laquelle s'execute le serveur.
    String getPublicIp() {
	    URL whatismyip;
	    BufferedReader in;
		try {
			whatismyip = new URL("http://checkip.amazonaws.com");
			in = new BufferedReader(new InputStreamReader(
			                whatismyip.openStream()));
			String ip = in.readLine();
			return ip;
		} catch (IOException e) {
			cout("Unable to get Public IP");
		}
		return null;
    }
    
    public void shutdown() {   	
    	for(UserHandler usr : UserHandlerList) {
    		usr.disconnect();
    	}    	
    	UserHandlerList.clear();
    	serverIHM.refreshUsers();
    	try {
    		listener.close();
    		this.join();
    	} catch (IOException e) {			
    		e.printStackTrace();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
    	}	
    }
	public void run() {    		
		try {
			UserHandlerList = new Vector<UserHandler>();
			listener = new ServerSocket(PORT);
			listener.setReuseAddress(true);
			cout("The chat server is running"); 
			while (!isInterrupted()) {		
				cout("Waiting for a connection");
			 	UserHandler newUser = new UserHandler(this, listener.accept());
			    cout("A connection has been made");	
			}
		} catch (SocketException e) {			
			cout("Socket closed");	
		} catch (IOException e) {			
			cout("Network error");	
		} finally {
			cout("Server closed");
		}
	}
}
