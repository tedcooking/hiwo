package hiwoserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Vector;

import utility.Command;
import utility.Packet;
import utility.User;

public class UserHandler {
	/**************************************************************************************************************
	 * Cette classe creer un utilisateur et en gere la connexion et le transfere de paquets sur le reseau
	 * Pour l'instancier, il faut lui fournir un serveur qui gerera la diffusion des messages aux
	 * autres utilisateurs, ainsi que le socket sur lequel elle communiquera sur le reseau.
	 * Elle possede une classe privée Handler, qui est un thread effectuant la recuperation des messages reseau
	 ***************************************************************************************************************/
	
	private Server server;
	private User user;
	private Socket socket;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;    
    private boolean ready=false;   
	
	UserHandler(Server serv, Socket sock) throws IOException{
    	server = serv;
		socket = sock;
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
		user = new User();
        new Handler(this).start();
	}
	private void setReady(){
		ready = true;
		server.updateUserLists();
	}
	private Packet readPacket() throws ClassNotFoundException, IOException {
		Packet to = (Packet)ois.readObject();
		return to;
	}
	private void sendSerialise(Object s) throws IOException {
			oos.writeObject(s);
			oos.flush();
	}

	User getUser() {
		return user;
	}
	
	void sendPacket(Packet msg) throws IOException {
			oos.writeInt(Command.MESSAGE.getcode());
			oos.flush();
			sendSerialise(msg);
	}


	void updateUserList(Vector<User> userList) throws IOException {
		oos.writeInt(Command.UPDATEUSERS.getcode());
		oos.flush();
		sendSerialise(userList);
	}
	void updateUserData(Packet m) throws IOException {
		oos.writeInt(m.getCommand().getcode());
		oos.flush();
		sendSerialise(m);
	}	
	boolean isReady(){
		return ready;
	}

	void disconnect() {
		try{
			ois.close();
			oos.close();
			socket.close();			
		}catch(IOException e){
			server.cout("Couldn't disconnect user properly");
		}	
	}   

	
	private class Handler extends Thread{	
        private UserHandler userHandler;
        private User user;
        
        Handler(UserHandler usr) {
        	userHandler = usr;
        	user = userHandler.getUser();
        }        
        public void run() {
            try {
            	// Obtention du mot de passe
            	while (true) {           		
            		oos.writeInt(Command.PASSWORD.getcode());
                	oos.flush();               	
                	Packet pckt = readPacket();
                	String psswd = pckt.getContent();
                	if(server.checkPassword(psswd)) {
                		break;
                	}      	     
                }
            	// Obtention du nom
            	while (true) {           		
            		oos.writeInt(Command.SUBMITNAME.getcode());
                	oos.flush();               	
                	Packet pckt = readPacket();
                	String name = pckt.getContent();
                	if(server.checkName(name)) {
                		user.setName(name);
                		setReady();
                		server.registerUserHandler(userHandler);
                		break;
                	}
                }
                
            	// Bien loggé; receptionne et traite les messages du client
                while (true) {                	
                	Packet pckt = readPacket();                   
                	if (pckt != null) {
                		// Si l'utilisateur envoi une commande specifique au serveur
                    	if(pckt.getContent().startsWith("/")) {
                    		if(pckt.getContent().startsWith("/info ")) {
                    			user.setInfo(pckt.getContent().substring(6));
                    			server.updateUserData(new Packet(user, Command.UPDATEUSERINFO, user.getInfo()));
                    			}
                    		if(pckt.getContent().startsWith("/name ")) {
                    			user.setName(pckt.getContent().substring(6));
                    			server.updateUserData(new Packet(user, Command.UPDATEUSERNAME, user.getName()));
                    			}
                    	}else {
                    		// Sinon diffusion de ses messages
                    		server.broadcoast(new Packet(user, Command.BROADCOAST, pckt.getContent()));
                    	}
                    }                	
                }  
                
            } catch (IOException e ) {
            	server.cout("Error sending or receiving a packet throught the network");
            } catch (ClassNotFoundException e) {
            	server.cout("Couldn't convert the ObjectInputStream : Class not found");
			} finally {
                // Fermeture du thread, on retire le thread utilisateur du serveur
            	server.removeUserHandler(userHandler);
           }
        }
}
}
