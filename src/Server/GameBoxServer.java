package Server;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class GameBoxServer{
    static int ins1 = 1;
    static int ins2 = 1;
    public static void main(String[] args) {
            try (ServerSocket serverSocket = new ServerSocket(6606)) {
                while (true){
                    Socket sc = serverSocket.accept();
                    ServerMain serverMain = new ServerMain(sc);
                    Thread t = new Thread(serverMain);
                    t.start();
                }
            
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}
class ServerMain implements Runnable{
    BufferedReader reader;
    BufferedWriter writer;

    final static ArrayList<ServerMain> clients = new ArrayList<>(2);
    final static ArrayList<Users> user = new ArrayList<>(2);



    ServerMain(Socket sc){
        try {
            OutputStreamWriter o = new OutputStreamWriter(sc.getOutputStream());
            writer = new BufferedWriter(o);
            InputStreamReader isr = new InputStreamReader(sc.getInputStream());
            reader = new BufferedReader(isr);
            clients.add(this);
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void run() {
        while (true) {
            try {
                String str = reader.readLine();
                if(str.equals("Login Button")) {
                    user.add(new Users(reader.readLine(), reader.readLine()));
                }
                if(str.equals("Connection Button")) {
                    boolean isFound = false;
                    String checkKey = reader.readLine();
                    for (Users u:user){
                        if(u.secret_key.equals(checkKey)){
                            isFound = true;
                        }
                    }
                    for (ServerMain s: clients){
                        s.writer.write(isFound+"\n");
                        s.writer.flush();
                    }

                }
                if(str.equals("ChatBox Before Game")){
                    String msg = reader.readLine();
                    synchronized (clients) {
                        for (ServerMain s : clients) {
                            s.writer.write(str + "\n");
                            s.writer.write(msg + "\n");
                            s.writer.flush();
                        }
                    }
                }
                if(str.equals("Tic Tac Toe")){
                    if(GameBoxServer.ins1==1){
                        GameBoxServer.ins1++;
                        new Tic_Tac_Toe_Server();
                    }
                }
               

            } catch (IOException e) {
                System.out.println(e.getMessage());
                break;
            }
        }
    }
    public static ArrayList<Users> getList(){
        return user;
    }
}

class Users{
    String username;
    String secret_key;

    public Users(String username, String secret_key) {
        this.username = username;
        this.secret_key = secret_key;
    }
}
