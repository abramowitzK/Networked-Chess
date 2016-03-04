package Networking;

import java.io.Serializable;
public enum OpCode implements Serializable{
    UpdateBoard,
    UpdatedBoard,
    JoinQueue,
    JoinedQueue,
    JoinGame,
    JoinedGame,
    QuitGame,
    Promotion,
    Castle,
}
