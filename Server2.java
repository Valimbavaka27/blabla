package server;

import java.net.ServerSocket;

public class Server2 {
    public void runServer2() throws Exception {
        ServerSocket server = new ServerSocket(5002);
        Server.sendToprincipalServer(server, "server/Beta/");
    }
    public static void main(String[] args) throws Exception {
        try {
            Server2 s2 = new Server2();
            s2.runServer2();
        } catch (Exception error) {
            error.printStackTrace();
        }
    }
}
