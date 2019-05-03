package tests;

public class AB {
    public static void main(String[] args) {
        Server server = new Server();
        Sender sender = new Sender();

        // listener server, service
        server.start();

        // incoming connection
        sender.start();

        try{
            server.join();
            sender.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }

    }
}
