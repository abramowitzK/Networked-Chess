package testClient;
import game.Move;
import game.Position;
import networking.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Kyle_Windows10 on 2/2/2016.
 */
public class TestClient {
    public static void main(String[] args){
        try {
            Packet packet = new Packet(OpCode.JoinQueue, -1, null);
            Socket socket = new Socket("127.0.0.1", 4444);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(packet);
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Packet inObj = (Packet)in.readObject();
            System.out.println(inObj);
            out.writeObject(new Packet(OpCode.UpdateBoard, inObj.GetID(), new Move(new Position(0,0), new Position(0,0))));
            Packet up  = (Packet)in.readObject();
            out.writeObject(new Packet(OpCode.QuitGame, inObj.GetID(), null));
            socket.close();
        }
        catch (UnknownHostException ex){
            ex.printStackTrace();
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
    }
}
