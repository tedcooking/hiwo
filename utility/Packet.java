package utility;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Packet implements Serializable {
	/*************************************************************************
	 * Cette classe du package utility sert à uniformiser la gestion 
	 * des paquets echangés sur le reseau par le serveur et ses clients
	 * Un paquet comporte une date l'utilisateur proprietaire, la commande 
	 * à executer et une chaine de caractere contextuelle. 
	 *************************************************************************/
	
	private static final long serialVersionUID = 1L;	
	private String date;
	private User user;
	private Command command;
	private String content;	
	
	public Packet(User f, Command c, String co ){
		date = date();
		user = f;
		command = c;
		content = co;
	}
	
	public Packet(User f, Command c){
		date = date();
		user = f;
		command = c;
		content = null;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User from) {
		this.user = from;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
    private static String date() {
	    String pattern ="HH:mm";
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
		String dateString = dateFormat.format(new Date());
		return dateString;
	}
	public String toString() {
		String result = date + "-"+user.getName()+" : "+content;
		return result;
	}
}
