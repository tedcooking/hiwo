package client;

import java.io.IOException;
import java.util.Optional;
import java.util.Vector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import utility.*;


public class Client extends Application{
	/******************************************************************************
	 * Application qui se connecte à un serveur via la classe connection
	 * elle gere l'interface graphique, la gestion de l'envoi et de la reception
	 * des messages sur le reseau est confiée a la clase MessageHandler
	 ******************************************************************************/
    TextArea textPanel;
    Vector<User> usersVector = new Vector<User>();
    VBox userVBoxList;
	BorderPane  border;
	TextField  userMessage;
	Stage primaryStage;
	Connection connection;
	MessageHandler messageHandler;
	
   	public void start(Stage p) throws Exception {  		
   		primaryStage = p;
		primaryStage.setTitle("HiWO");
		primaryStage.setResizable(false);
		
   	    border = new BorderPane();
   	    Scene scene = new Scene(border, 400, 400, Color.LIGHTBLUE);
   	    
   	    //Panneau d'affichage des messages
   	    textPanel = new TextArea();
   	    textPanel.setEditable(false);
   	    textPanel.setPrefSize(300,280);
   	    textPanel.setWrapText(true);
   	    textPanel.setStyle("-fx-focus-color: transparent;");
   	    border.setLeft(textPanel);
   	    
   	    // Panneau comprenant la liste des utilisateurs
	   	userVBoxList = new VBox();
	   	userVBoxList.setPadding(new Insets(5, 5, 5, 5));
	   	userVBoxList.setSpacing(3);
	   	userVBoxList.setPrefSize(100,1343);
	   	userVBoxList.setStyle("-fx-background-color: #e9edf1;-fx-focus-color: transparent;");   	    
   	    ScrollPane scroll = new ScrollPane();	       
   	    scroll.setContent(userVBoxList);
   	    scroll.setStyle("-fx-focus-color: transparent;");
   	    scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
   		border.setRight(scroll);  	
   		 
   		// panneau de saisie et envoi des messages
   	    HBox bottom = bottomBox();
   	    border.setBottom(bottom);

   	    primaryStage.setScene(scene);
   	    
   	    //creation de la connexion
    	connection = Connection.doConnection(getServerAddress());
    	
    	//lancement du thread de recuperation & traitement des messages serveur
        messageHandler = new MessageHandler(this, connection.socket);
        messageHandler.run();
        
        primaryStage.show();
    }
  	private HBox bottomBox() {
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 10, 15, 10));
        hbox.setSpacing(20);
        hbox.setStyle("-fx-background-color: #50708f;");
        
        userMessage = new TextField ();
        userMessage.setPrefSize(280, 20);
        userMessage.setOnKeyPressed(new EventHandler<KeyEvent>(){
        	public void handle(KeyEvent keyEvent){
        		 if (keyEvent.getCode() == KeyCode.ENTER)  {
        			 try {
						messageHandler.sendObject(new Packet(null, Command.MESSAGE,userMessage.getText()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
        			 userMessage.clear();
                }
        	}
        });

        Button buttonSend = new Button("SEND");
        buttonSend.setPrefSize(80, 20);
        buttonSend.setOnAction(new EventHandler<ActionEvent>() {
        	 public void handle(ActionEvent event) {
        		 try {
					messageHandler.sendObject(new Packet(null, Command.MESSAGE,userMessage.getText()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
        		 userMessage.clear();
        	 }
        });        
        hbox.getChildren().addAll(userMessage, buttonSend);
        return hbox;  
    }
  	
    private String getServerAddress() {
    	TextInputDialog dialog = new TextInputDialog();
    	dialog.setTitle("Server");
    	dialog.setHeaderText("Enter IP Address of the Server:");
    	Optional<String> result = dialog.showAndWait();
    	if(result.isPresent()){
    		return result.get();
    	}
        return null;
    }
   	void submitname() {		
   		Platform.runLater(new Runnable() {
    	    public void run() {
    	    	try {
					messageHandler.sendObject(new Packet(null, Command.SUBMITNAME,getString("Name",true)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
    	}); 
   	}
   	
   	void submitpassword() {		
   		Platform.runLater(new Runnable() {
    	    public void run() {
    	    	try {
					messageHandler.sendObject(new Packet(null, Command.PASSWORD,getString("Password",true)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    	    }
    	}); 
   	}
   	
    /**
     * A function to get a String according to the <code>string</code> Q asked. <code>Boolean</code> B sets the behavior if dialog is cancelled, true would shut down the application
     * @param q the <code>String</code> value to be asked
     * @param b the <code>Boolean</code> that allow dialog to shut down application if cancelled     * 
     * 
     */

    String getString(String q, Boolean b) {    	
    	TextInputDialog dialog = new TextInputDialog();
    	dialog.setTitle(q);
    	dialog.setHeaderText("Information required : "+q);
    	
    	Optional<String> result = dialog.showAndWait();
    	if(result.isPresent()){
    		return result.get();
    	}
    	if(b)Platform.exit();
    	return null;
    }    
   
	public void addMessage(Packet content) {
		textPanel.appendText(content.toString() +"\n");
	}
	public void updateUserList(Vector<User> v) {
		usersVector = v;
		refreshUsers();
	}
	
	public void refreshUsers() {
		 Platform.runLater(new Runnable() {
	  	    public void run() {	
	  	    	userVBoxList.getChildren().clear();
		    	    for(User user:usersVector){	    	    	
		    	    	Button buttonUser = new Button(user.getName());
		    	        buttonUser.setPrefSize(80, 20);
		    	        buttonUser.setStyle("-fx-background-color: #d3dbe3");
		    	        buttonUser.setOnAction(new EventHandler<ActionEvent>() {
		    	        	 public void handle(ActionEvent event) {
		    	        		 Alert alert = new Alert(AlertType.INFORMATION);
		    	        		 alert.setTitle(user.getName());
		    	        		 alert.setHeaderText(null);
		    	        		 alert.setContentText(user.getInfo());
		    	        		 alert.showAndWait();
		    	        	 }
		    	        });
		    	        userVBoxList.getChildren().add(buttonUser);
		    	    }   
	  	    }
	  	});	
	} 
  
}