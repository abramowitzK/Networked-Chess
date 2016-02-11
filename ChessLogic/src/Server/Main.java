package server;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Kyle on 2/1/2016.
 */
public class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());
    /**
     * server.Main entry point
     * @param args arguments to main program
     */
    public static void main(String [] args){
        Server mainServer;
        try {
            //Create a server.server
            mainServer = new Server();
            //Start the server
            log.log(Level.FINE, "Starting server");
            mainServer.Start();
        }
        catch (IOException ex){
            log.log(Level.FINE, ex.toString(), ex);

        }
    }



}
