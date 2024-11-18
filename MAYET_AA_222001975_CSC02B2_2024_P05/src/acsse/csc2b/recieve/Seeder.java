package acsse.csc2b.recieve;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class Seeder extends BorderPane
{
	private DatagramSocket seederSocket;
	
	private DatagramPacket recievePacket;
	private DatagramPacket sendPacket;
	
	InetAddress IPAddress;
	int portNumber;
	
	private ArrayList<String> fileList = new ArrayList<>();
	private boolean isRunning = false;
	
	private Button btnAddFile;
	private TextArea txtFileList;
	private Button btnHost;
	
	/**
	 *Constructor
	 *@param portNumber to connect on when sending data
	 */
	public Seeder(int portNumber)
	{
		createUI();
		
		try
		{
			IPAddress = InetAddress.getByName("localhost");
			this.portNumber = portNumber;
			seederSocket = new DatagramSocket();
			
		}catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}


	public void runSeeder()
	{
		isRunning = true;
		while(isRunning)
		{
			try
			{
				//receive packet data
				byte[] buffer = new byte[2048];
				recievePacket = new DatagramPacket(buffer, buffer.length);
				seederSocket.receive(recievePacket);
				
				String message = new String(buffer).trim();
				StringTokenizer tokens = new StringTokenizer(message);
				String comand = tokens.nextToken();
				
				try
				{
					if(comand.equals("CONNECT"))
					{
						String response = "Connected on port: " + seederSocket.getPort();
						sendPacket = new DatagramPacket(response.getBytes(), response.getBytes().length, IPAddress, portNumber);
						
					}else if(comand.equals("GETLIST"))
					{
						String response = "";
						
						//sending the list
						for(String str: fileList)
						{
							response += str + "#";
						}
						
						sendPacket = new DatagramPacket(response.getBytes(), response.getBytes().length, IPAddress, portNumber);
						
					}else if(comand.startsWith("GETFILE"))
					{
						int fileIndex = Integer.parseInt(tokens.nextToken()) - 1;
						
						File file = new File("./data/Seeder/" + fileList.get(fileIndex));
						
						buffer = new byte [(int) file.length()];
						DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
						
						dataInputStream.readFully(buffer);
						dataInputStream.close();
						
						sendPacket = new DatagramPacket(buffer, buffer.length , IPAddress, portNumber);
					}
					
					seederSocket.send(sendPacket);
					
				} catch (IOException e)
				{
					isRunning = false;
					e.printStackTrace();
				}
			} catch (IOException e)
			{
				isRunning = false;
				e.printStackTrace();
			}
		}
	}
	
	/**
	 *Method for setting up the GUI
	 *
	 */
	public void createUI()
	{
		GridPane root = new GridPane();
		root.setPadding(new Insets(5, 5, 5, 5));
		root.setVgap(5);
		root.setHgap(5);
		
		btnAddFile = new Button("Add File");
		btnAddFile.setOnAction((event)-> addFile());
		
		txtFileList = new TextArea();
		
		btnHost = new Button("Host Files");
		btnHost.setOnAction(e -> runSeeder());
		
		root.add(btnAddFile, 0, 0, 51, 5);    // Button at the top
		root.add(txtFileList, 0, 5, 51, 10);   // Text area below the button
		root.add(btnHost, 0, 15, 51, 5);       // Button below the text area
		
		this.getChildren().add(root);
	}
	
	public void addFile()
	{
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File("./data/Seeder"));
		
		//Openning window for file chooser
		File handle = fileChooser.showOpenDialog(null);
		
		if(handle != null)
		{
			String fileName = handle.getName();
			fileList.add(fileName);
			txtFileList.appendText(fileList.size() + " " + fileName + "\n");
		}
	}
}
