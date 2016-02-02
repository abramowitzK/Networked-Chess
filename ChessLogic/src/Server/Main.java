package Server;

import java.io.IOException;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Main {
    /**
     * Server.Main entry point
     * @param args arguments to main program
     */
    public static void main(String [] args){
        Server mainServer;
        try {
            //Create a Server.Server
            mainServer = new Server();
            //Start the server
            mainServer.Start();
        }
        catch (IOException ex){
        }
    }



}
