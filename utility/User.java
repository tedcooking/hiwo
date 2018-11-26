package utility;

import java.io.Serializable;

public class User implements Serializable{
	/*************************************************************************
	 * Cette classe du package utility sert à uniformiser la gestion 
	 * des users sur le reseau par le serveur et ses clients
	 *************************************************************************/
	private static final long serialVersionUID = 1L;
	private String name;
	private String info;
	
	public User(){		
	}

	public User(String n, String inf) {
		this.name=n;
		this.info=inf;
	}

	public void setName(String n) {
		this.name=n;
	}
	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getName() {
		return name;
	}
}
