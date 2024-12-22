package server;

import java.net.ServerSocket;

public class Server1 {
    public void runServer1() throws Exception{
        ServerSocket server = new ServerSocket(5001);
        Server.sendToprincipalServer(server, "server/Alpha/");
    }
    public static void main(String[] args) throws Exception {
        try {
                Server1 s1 = new Server1();
                s1.runServer1();
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
}
