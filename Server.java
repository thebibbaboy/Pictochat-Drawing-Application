import java.net.*;     
import java.io.*;      
import java.util.*;     

//Main server class that handles client connections and drawing broadcast
public class Server {
    private ServerSocket serverSocket;  
    private final List<DrawingData> drawingHistory = Collections.synchronizedList(new ArrayList<>());
    private final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    //Launch server WINDOW through run(); and message for user 
    public Server(int port) throws IOException {
        //Socket used for client-server communication
        serverSocket = new ServerSocket(port);
        System.out.println("Server started on port " + port);
        run(); 
    }

    //The server beings to accept new clients.
    // Responsible for threading new clients.
    public void run() {
        while (true) {
            try {
                //Get the socket that a client uses to join the server
                Socket clientSocket = serverSocket.accept(); 

                synchronized (clients) {
                    if (clients.size() >= 2) {
                        System.out.println("Connection refused: Max 2 clients allowed.");
                        clientSocket.close();
                        continue;
                    }
                }

                //Socket used for client-server communication
                ClientHandler handler = new ClientHandler(clientSocket);
                clients.add(handler);
                //Creates a client thread run(); in ClientHandler
                new Thread(handler).start();
            } catch (IOException e) {
                e.printStackTrace(); 
            }
        }
    }

    //Send to the listen() method in Client script. This calls send() below.  
    private void broadcast(Object obj, ClientHandler exclude) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client != exclude) {
                    client.send(obj);
                }
            }
        }
    }

    //The class that starts listening loop
    //Always listens for DrawingData and broadcasts it to all connected clients for them to listen to
    private class ClientHandler implements Runnable {
        private Socket socket;
        //Stream to send serialized DrawingData
        private ObjectOutputStream out;
        //Stream to receive serialized DrawingData
        private ObjectInputStream in;   

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void send(Object obj) {
            try {
                out.writeObject(obj);
                out.flush(); 
            } catch (IOException e) {
                System.out.println("Send failed: " + e.getMessage());
            }
        }

        //Runs automatically from new Thread(handler).start(); in the run() method above
        //Always listens for DrawingData and broadcasts it to all connected clients for them to listen to
        public void run() {
            try {
                //Stream to send serialized DrawingData
                out = new ObjectOutputStream(socket.getOutputStream());
                //Stream to receive serialized DrawingData
                in = new ObjectInputStream(socket.getInputStream());
                
                //Updates arrays

                synchronized (drawingHistory) {
                    for (DrawingData stroke : drawingHistory) {
                        out.writeObject(stroke);
                        out.flush();
                    }
                }

                Object input;
                while ((input = in.readObject()) != null) {
                    if (input instanceof DrawingData) {
                        drawingHistory.add((DrawingData) input);
                        broadcast(input, this);
                    }
                }
            } catch (Exception e) {
                System.out.println("Client disconnected.");
            } finally {
                synchronized (clients) {
                    clients.remove(this);
                }
            }
        }
    }

    //Launches server
    //Main method, entry point of the program
    public static void main(String[] args) throws IOException {
        new Server(12345);
    }
}