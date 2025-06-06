import javax.swing.*;     
import java.awt.*;        
import java.awt.event.*;  
import java.io.*;         
import java.net.*;        

//Main client class that connects to server and sends/receives drawing data
public class Client {
    private Socket socket;                  
    //Stream to send serialized DrawingData
    private ObjectOutputStream out;        
    //Stream to receive serialized DrawingData
    private ObjectInputStream in;          
    private DrawingPanel panel;          

    // Constructor that connects to server and launches the drawing window
    public Client(String address, int port) {
        try {
            //Socket used for client-server communication
            socket = new Socket(address, port); 
            //Stream to send serialized DrawingData
            out = new ObjectOutputStream(socket.getOutputStream()); 
            //Stream to receive serialized DrawingData
            in = new ObjectInputStream(socket.getInputStream());     

            // Create the Window for the client
            JFrame frame = new JFrame("Pictochat Client");
            //Reference DrawingPanel script to build panel
            panel = new DrawingPanel(out);
            frame.add(panel);
            frame.setSize(900, 700);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            //Listen for data from server
            new Thread(() -> listen()).start();
        } catch (IOException e) {
            e.printStackTrace(); 
        }
    }

    //Updates canvas from the listened data in the broadcast() method (from ClientHandler in Server)
    private void listen() {
        try {
            Object input;
            //Always receive drawing data from server
            while ((input = in.readObject()) != null) {
                if (input instanceof DrawingData) {
                    panel.addRemoteStroke((DrawingData) input);
                }
            }
        } catch (Exception e) {
            System.out.println("Disconnected from server.");
        }
    }

    //Runs the server on port 12345 or any custom port
    public static void main(String[] args) {
        new Client("localhost", 12345);
    }
}