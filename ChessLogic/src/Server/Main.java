package Server;

import java.util.logging.*;

class Main {
    private static final Logger log = Logger.getLogger(Main.class.getName());
    /**
     * Server.Main entry point
     * @param args arguments to main program
     */
    public static void main(String [] args){
        Server mainServer;
        //Create a Server.Server
        mainServer = new Server(Integer.parseInt(args[0]), args[1]);
        //Start the Server
        log.log(Level.FINE, "Starting Server");
        mainServer.Start();
    }



}
