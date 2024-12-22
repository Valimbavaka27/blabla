package client;

import javax.sound.midi.Soundbank;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    String pt;

    Socket socket;
    DataOutputStream outputStream;
    DataInputStream inputStream;

//seters and getters
    public String getPt() {return pt;}
    public void setPt(String pt) {this.pt = pt;}
    public void setInputStream(DataInputStream inputStream) {this.inputStream = inputStream;}
    public void setOutputStream(DataOutputStream outputStream) {this.outputStream = outputStream;}
    public void setSocket(Socket socket) {this.socket = socket;}
    public DataInputStream getInputStream() {return inputStream;}
    public DataOutputStream getOutputStream() {return outputStream;}
    public Socket getSocket() {return socket;}
//

//Constructor of Class
    public Client(String host, int port) throws Exception {
        setSocket(new Socket(host, port));
        setInputStream(new DataInputStream(getSocket().getInputStream()));
        setOutputStream(new DataOutputStream(getSocket().getOutputStream()));
    }
    public Client(Socket socket) throws Exception {
        setSocket(socket);
        setInputStream(new DataInputStream(getSocket().getInputStream()));
        setOutputStream(new DataOutputStream(getSocket().getOutputStream()));
    }
//

    //functions

//Send file' name from client to more servers to have the same name in final 
    public void sendFileName(File file) throws Exception {
        int length = file.getName().getBytes().length;
        getOutputStream().writeInt(length);
        getOutputStream().write(file.getName().getBytes());
    }
//
//send a file's Content  from client to more servers
    public void sendContentFile(File file) throws Exception {
        int lenght = 0;
        int offset = 0;
        FileInputStream fileInputStream = new FileInputStream(file);
        byte[] bytes = new byte[4 * 1024];
        while ((lenght = fileInputStream.read(bytes)) != -1) {
            outputStream.write(bytes, offset, lenght);
            outputStream.flush();
        }
        fileInputStream.close();
    }
    public void sendFile(File file) throws Exception {
        sendFileName(file);
        sendContentFile(file);
    }
//
//Menu' Content
    public static void choiceBare() {
        System.out.println("Menu :");
        System.out.println("1. Insert file to Servers");
        System.out.println("2. Download a new File to servers");
        System.out.println("3. Exit the program ");
        System.out.print("Enter your option : ");
    }
//
public static void finalExecut(String path) throws Exception {
    Client.choiceBare();
    Scanner scanner = new Scanner(System.in);
    int choice = Integer.parseInt(scanner.nextLine());
    switch (choice) {
        case 1: //insert a new file
            System.out.println("Enter the file name : (Your file must be located in this path "+path+")");
            String fileName = scanner.nextLine();
            File file = new File(path + fileName);
            if(file.exists()) {
                Client client = new Client("localhost", 4000);
                client.outputStream.writeInt(choice);
                client.sendFile(new File(path+fileName));
                client.getSocket().close();
                System.out.println("File sent Successfully");
            } else {
                System.out.println("File does not exist");
            }
            break;
        case 2: // Download file in a server
            System.out.println("Enter the file name : (Your file will be located in this path client/downloads/)");
            String fileName2 = scanner.nextLine();
            Client client = new Client("localhost", 4000);
            client.outputStream.writeInt(choice);
            client.sendFileName(new File(fileName2));
            break;
        case 3 :
            System.out.println("Program exited");
            return;
    }
}
    public static void main(String[] args) {
        try{
            while (true) {
                String path = "client/sources/";
                Client.finalExecut(path);
            }
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
