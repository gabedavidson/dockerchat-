import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * ClientHandler.java
 *
 * <p>This class handles communication between the client
 * and the server.  It runs in a separate thread but has a
 * link to a common list of sockets to handle broadcast.
 *
 */
public class ClientHandler implements Runnable {
  private Client client = null;
  private ArrayList<Client> clientList;

  ClientHandler(Client client, ArrayList<Client> clientList) {
    this.client = client;
    this.clientList = clientList;  // Keep reference to master list

  }

  /**
   * received input from a client.
   * sends it to other clients.
   */
  public void run() {
    try {
      Socket connectionSock = client.connectionSock;
      System.out.println("Connection made with socket " + connectionSock);
      BufferedReader clientInput = new BufferedReader(
          new InputStreamReader(connectionSock.getInputStream()));
          this.client.username = clientInput.readLine();
      while (true) {
        // Get data sent from a client
        String clientText = clientInput.readLine();
        if (clientText != null) {
          System.out.println("Input received from: " + this.client.username + " -> " + clientText);
          // Turn around and output this data
          // to all other clients except the one
          // that sent us this information
          for (Client client : clientList) {
            Socket sock = client.connectionSock;
            if (sock != connectionSock) {
              DataOutputStream clientOutput = new DataOutputStream(sock.getOutputStream());
              clientOutput.writeBytes(this.client.username + ": " + clientText + "\n");
            }
          }
        } else {
          // Connection was lost
          System.out.println("Closing connection for socket " + connectionSock);
          // Remove from arraylist
          clientList.remove(client);
          connectionSock.close();
          break;
        }
      }
    } catch (Exception e) {
      System.out.println("Error: " + e.toString());
      // Remove from arraylist
      clientList.remove(client);
    }
  }
} // ClientHandler for MtServer.java
