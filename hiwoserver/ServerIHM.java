package hiwoserver;

import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ServerIHM extends Application{	
	/***************************************************************************
	 * Cette classe est l'interface graphique qui gere la classe serveur,
	 * elle instancie cette derniere et en recupere la liste des utilisateurs
	 * Elle affiche les messages console du serveur.
	 ***************************************************************************/
	
	Server server = null;
	Stage primaryStage;
	TextArea serverInfo= new TextArea();
	TextArea serverConsole = new TextArea();
	VBox userBList;
	
	public void start(Stage p) throws Exception { 		 		
		primaryStage = p;
		primaryStage.setTitle("HiWO");
		primaryStage.setResizable(false);		
   	    BorderPane border = new BorderPane();
   	    Scene scene = new Scene(border, 400, 400); 	    
   	    
	    HBox topMenu = topMenu();
	    border.setTop(topMenu);
	    
	    VBox leftPanel = serverData();
	    border.setCenter(leftPanel);

   	    ScrollPane rightPanel = userList();	
   		border.setRight(rightPanel);  	
   		    	  
   	    primaryStage.setScene(scene);
   	    primaryStage.show();
	}	
	/*
	 * (non-Javadoc)
	 * @see javafx.application.Application#stop()
	 * Overrides la methode stop de l'application
	 * ici le but est de s'assurer que le serveur est arreté lors de la fermeture de la fenetre
	 */
	public void stop() {
		if(server!=null) {
	    	server.shutdown();
	    	server=null;
		}
	}
	
	/*
	 * Sur appel du serveur, lors de connexions ou deconnexion d'utilisateurs, cette fonction
	 * va chercher la liste des utilisateurs connectés et se mettre à jour, affichage d'un bouton
	 * par utilisateur, avec la possibilité de consulter ses informations et de le kicker
	 */
	public void refreshUsers() {
		 Platform.runLater(new Runnable() {
	  	    public void run() {	  	    	
	  	    	userBList.getChildren().clear();
	  	    	if(server!=null&&!server.UserHandlerList.isEmpty()) {
		    	    for(UserHandler user:server.UserHandlerList){	    	    	
		    	    	Button buttonUser = new Button(user.getUser().getName());
		    	        buttonUser.setPrefSize(120, 20);
		    	        buttonUser.setOnAction(new EventHandler<ActionEvent>() {
		    	        	 public void handle(ActionEvent event) {
		    	        		 Alert alert = new Alert(AlertType.CONFIRMATION);
		    	        		 alert.setTitle(user.getUser().getName());
		    	        		 alert.setHeaderText(user.getUser().getInfo());
		    	        		 alert.setContentText("Kick user ?");
		    	        		 Optional<ButtonType> result = alert.showAndWait();
		    	        		 if (result.get() == ButtonType.OK)user.disconnect();		    	        		 	 
		    	        	 }
		    	        });
		    	        userBList.getChildren().add(buttonUser);
		    	    }
	  	    	}
	  	    	}
	  	});
	}
	
	/*
	 * Permets l'affichage des messages console dans la fenetre au lieu de la console
	 */
	void consolePrint(String s) {
		serverConsole.appendText(">"+s+"\n");
	}

	/*
	 * Creer et renvoi les differents composants de la fenetre de l'application
	 */	
	private HBox topMenu() {
		ServerIHM me = this;
	   	Button startButton = new Button("Start Server");
	   	startButton.setOnAction(new EventHandler<ActionEvent>() {
	    	public void handle(ActionEvent event) {	
	    		if(server==null) {
		    		server = new Server();
		    		server.setIHM(me);
		    		server.start();
		    		serverInfo.clear();
		    		serverInfo.appendText("Adresse IP du serveur :\n");
		    		serverInfo.appendText(server.getPublicIp());
	    		}
	    	}
	    });
	    Button stopButton = new Button("Stop Server");
	    stopButton.setOnAction(new EventHandler<ActionEvent>() {
	    	public void handle(ActionEvent event) {
	    		if(server!=null) {
			    	server.shutdown();
			    	server=null;
	    		}
	     }
	    });	    
	    TextField passwordField = new TextField ();
	    passwordField.setPrefSize(135, 20);

        Button buttonSend = new Button("Set Password");
        buttonSend.setOnAction(new EventHandler<ActionEvent>() {
        	 public void handle(ActionEvent event) {
        		 if(server!=null) {
        			 server.setPassword(passwordField.getText());
        			 passwordField.clear();        			 
        		 }
        	 }
        });
	    
	    HBox topMenu = new HBox();
	    topMenu.getChildren().addAll(startButton,stopButton,passwordField,buttonSend);
	    topMenu.setPadding(new Insets(5, 5, 5, 5));
	    topMenu.setSpacing(5);
	    return topMenu;	  
	}
	private ScrollPane userList() {
   	    userBList = new VBox();  	    
   	    userBList.setPadding(new Insets(5, 5, 5, 5));
   	    userBList.setSpacing(3);
   	    userBList.setPrefSize(170,400);
   	    userBList.setAlignment(Pos.TOP_CENTER);
   	    
   	    ScrollPane scroll = new ScrollPane();	       
   	    scroll.setContent(userBList);
   	    scroll.setStyle("-fx-focus-color: transparent;");
   	    scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
   	    return scroll;
	}
	private VBox serverData() {
		 serverInfo.setEditable(false);
		    serverInfo.setWrapText(true);
		    serverInfo.setStyle("-fx-focus-color: transparent;");
		    serverInfo.setPrefHeight(50);
		    
		    serverConsole.setEditable(false);
		    serverConsole.setWrapText(true);
		    serverConsole.setPrefHeight(310);
		    serverConsole.setPrefWidth(300);
		    serverConsole.setStyle("-fx-focus-color: transparent;");
		    
		    VBox serverData = new VBox();	    
		    serverData.getChildren().addAll(serverInfo,serverConsole);
		    return serverData;
	}
}
