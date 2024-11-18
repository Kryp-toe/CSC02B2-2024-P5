package acsse.csc2b.send;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class Leecher extends BorderPane
{
	private DatagramSocket socket = null;
	private DatagramPacket sendPacket = null;
	private DatagramPacket recievePacket = null;
	private InetAddress IPAddress;
	private int portNumber;
	
	private ArrayList<String> fileList = null;
	
	private GridPane root;
	private Button btnConnect;
	private Button btnGetFileList;
	private Button btnGetFile;
	private TextArea txtFileList;
	private TextField txtFileChoice;
	
	/**
	  *Constructor
	  */
	public Leecher(int portnumber)
	{
		try
		{
			//initialize variables
			this.portNumber = portnumber;
			IPAddress = InetAddress.getByName("localhost");
			this.socket = new DatagramSocket(portNumber);
			this.fileList = new ArrayList<String>();
			this.root = new GridPane();
			
			//set up root
			root.setPadding(new Insets(5, 5, 5, 5));
			root.setVgap(5);
			root.setHgap(5);
			
			//connect to SEEDER
			btnConnect = new Button("Connect");
			btnConnect.setOnAction(e-> connect());
			root.add(btnConnect, 0, 0, 13, 1);
			
			//add connect btn to pane
			this.getChildren().add(root);
			
		} catch (SocketException | UnknownHostException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	  *Method for setting up GUI
	  *
	  */
	public void createUI()
	{
		//remove connect btn from pane and root
		root.getChildren().clear();
		this.getChildren().remove(root);
		
		//set up root
		root.setPadding(new Insets(5, 5, 5, 5));
		root.setVgap(5);
		root.setHgap(5);
		
		//btn to get List of Files
		btnGetFileList = new Button("Get List");
		btnGetFileList.setOnAction(e-> getList());
		
		//btn to get a specific file
		btnGetFile = new Button("Get File");
		btnGetFile.setOnAction(e-> getFile());
		
		txtFileList = new TextArea();
		
		txtFileChoice = new TextField("Enter File Number: ");
		
		root.add(btnGetFileList, 0, 0, 52, 1);  // Button at the top (spanning two columns)
		root.add(txtFileList, 0, 1, 52, 1);     // Text area for the file list below the button
		root.add(txtFileChoice, 0, 2, 51, 1);   // Text field for file choice below the list
		root.add(btnGetFile, 51, 2, 1, 1);      // Button next to the txtFileChoice
		
		this.getChildren().add(root);
	}
	
	/**
	  *Method for sending message
	  *@param message
	  */
	public void sendMsg(String message)
	{
		message = message.toUpperCase();
		try
		{
			byte[] buffer = message.getBytes(); 
			sendPacket = new DatagramPacket(buffer, buffer.length, IPAddress, portNumber);
			socket.send(sendPacket);
			
			System.out.println(message + " request sent to SEEDER");
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void connect()
	{
		try
		{
			//initializing socket
			String messageString = "CONNECT";
			
			//recieve response from SEEDER
			byte[] buffer = new byte[2048];
			recievePacket =	new DatagramPacket(buffer, buffer.length);
			socket.receive(recievePacket);
			
			String response = new String(recievePacket.getData());
			System.out.println(response);
			
			createUI();
			
		} catch (UnknownHostException e)
		{
			e.printStackTrace();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	  *Method for getting list
	  *
	  */
	public void getList()
	{
		try
		{
			String message = "GETLIST";
			sendMsg(message);
			
			byte[] buffer = new byte[2048]; 
			DatagramPacket recievePacket = new DatagramPacket(buffer, buffer.length);
			socket.receive(recievePacket);
			
			String list = new String(buffer);
			
			for(String fileString : list.split("#"))
			{
				fileList.add(fileString);
				txtFileList.appendText(fileString + "\n");
			}
			
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public void getFile()
	{
		int fileNumb = Integer.parseInt(txtFileChoice.getText());
		String message = "GETFILE " + fileNumb;
		
		sendMsg(message);
		
		try
		{			
			//receive the file
			byte[] buffer = new byte[2048];
			recievePacket = new DatagramPacket(buffer, buffer.length);
			socket.receive(recievePacket);
			
			File file = new File("./data/Leecher/" + fileList.get(fileNumb - 1));
			DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file));
			
			dataOutputStream.write(buffer);
			dataOutputStream.close();
			
			System.out.println("File " + file.getName() + " was saved in " + file.getAbsolutePath());
			
		} catch (IOException e) 
		{	
			e.printStackTrace();
		}
	}
}
