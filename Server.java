package server;

import client.Client;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    Socket[] servers;
    DataInputStream dataInputStream;
    DataOutputStream dataOutputStream;
    Client client;
    ServerSocket serverSocket;

//Setters and Getters
    public void setDataInputStream(DataInputStream dataInputStream) {this.dataInputStream = dataInputStream;}
    public void setDataOutputStream(DataOutputStream dataOutputStream) {this.dataOutputStream = dataOutputStream;}
    public void setServerSocket(ServerSocket serverSocket) {this.serverSocket = serverSocket;}
    public void setServers(Socket[] servers) {this.servers = servers;}
    public void setClient(Client client) {this.client = client;}
    public DataInputStream getDataInputStream() {return dataInputStream;}
    public DataOutputStream getDataOutputStream() {return dataOutputStream;}
    public ServerSocket getServerSocket() {return serverSocket;}
    public Socket[] getServers() {return servers;}
    public Client getClient() {return client;}
//

//Constructor of Class
    public Server(int port) throws Exception {
        System.out.println("Waiting a client ...");
        setServerSocket(new ServerSocket(port));
        setClient(new Client(getServerSocket().accept()));
        System.out.println("Client connected");
    }
//
//receive file' name from client to more servers to have the same name in final
    public String receiveFileNameString() throws Exception {
        int offset = 0;
        int lenght = getClient().getInputStream().readInt();
        byte[] bytes = new byte[lenght];
        getClient().getInputStream().readFully(bytes, offset, lenght);
        return new String(bytes);
    }
//
//receive file' name from  more servers to client to have the same name in final
    public static String receiveFileNameString(InputStream inputStream) throws Exception {
        int offset = 0;
        int lenght = ((DataInputStream) inputStream).readInt();
        byte[] bytes = new byte[lenght];
        ((DataInputStream) inputStream).readFully(bytes, offset, lenght);
        return new String(bytes);
    }
//
//send part of file To AnotherServer
    public void sendToAnotherServer() throws Exception {
        int part = 2;
        int offset = 0;
        int choice = getClient().getInputStream().readInt();
        switch (choice) {
            case 1:
                String file = receiveFileNameString();
                byte[] bytes = new byte[4 * 1024];
                int lenght = getClient().getInputStream().read(bytes);
                int portion = lenght / part;
                DataOutputStream out = null;
                Socket socket = null;
                for (int i = 1; i <= part; i++) {
                    socket = new Socket("localhost", 5000 + i);
                    out = new DataOutputStream(socket.getOutputStream());
                    out.writeInt(choice);
                    out.writeInt(file.getBytes().length);
                    out.write(file.getBytes());
                    if (i == part) {
                        portion += lenght - offset - portion;
                    }
                    out.write(bytes, offset, portion);
                    out.close();
                    socket.close();
                    offset += portion;
                }
                break;
            case 2:
            System.out.println("Waiting to execute choice 2");
            DataOutputStream out2 = null;
            String file2 = receiveFileNameString();
            for (int i = 1; i <= part; i++) {
                Socket socket2 = new Socket("localhost", 5000 + i);
                out2 = new DataOutputStream(socket2.getOutputStream());
                out2.writeInt(choice);
                out2.writeInt(file2.getBytes().length);
                out2.write(file2.getBytes());
                out2.close();
                socket2.close();
            }
            break;
        }
            System.out.println("choice 2 executed");
}

//Send to Principal
public static void sendToprincipalServer(ServerSocket serverSocket, String folder) throws Exception {
    Socket client = serverSocket.accept();
    DataInputStream dataInputStream = new DataInputStream(client.getInputStream());
    int choice = dataInputStream.readInt();
    System.out.println(choice + " choice");
    if (choice == 1) {
        comitSending(dataInputStream, folder);
    } else if (choice == 2) {
        comitGeting(dataInputStream, folder);
        System.out.println(choice + " choice");
        client.close();
    } else if (choice == 3) {
        System.out.println("Program exited");
    }
}
//

//send to the content file to the mini server
    public static void comitSending(InputStream inputStream, String folder) throws Exception {
        try {
            int offset = 0;
            String fileName = receiveFileNameString(inputStream);
            FileOutputStream fileOutputStream = new FileOutputStream(new File(folder + fileName));
            byte[] bytes = new byte[4 * 1024];
            int lenght;
            while ((lenght = inputStream.read(bytes)) > -1) {
                fileOutputStream.write(bytes, offset, lenght);
            }
        }catch (Exception error) {
            error.printStackTrace();
        }
    }
// get to the content file to the mini server
    public static void comitGeting(InputStream inputStream, String folder) throws Exception {
        try {
            String pathDownload = "client/downloads/";
            int offset = 0;
            int lenght = 0;
            String fileName = receiveFileNameString(inputStream);
        System.out.println(fileName + " fileName");
            File file = new File(folder + fileName);
            if(file.exists()) {
                FileInputStream fileInputStream = new FileInputStream(file);
                FileOutputStream fileOutputStream = new FileOutputStream(new File(pathDownload + fileName), true);
                byte[] bytes = new byte[4 * 1024];
                while ((lenght = fileInputStream.read(bytes)) != -1) {
                    fileOutputStream.write(bytes, offset, lenght);
                    fileOutputStream.flush();
                }
                fileInputStream.close();
            } else {
                System.out.println("File does not exist");
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
//
    public static void main(String[] args) {
        try {
            while (true) {
                Server server = new Server(4000);
                server.sendToAnotherServer();
                server.getServerSocket().close();
            }
        } catch (Exception error) {
            System.out.println(error.getMessage());
            error.printStackTrace();
        }
    }
}
