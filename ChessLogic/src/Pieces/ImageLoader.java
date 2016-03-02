package Pieces;

import javafx.scene.image.Image;
public class ImageLoader
{
    private String colorString;
    public ImageLoader(Color color){
        if(color == Color.Black)
            colorString = "black";
        else
            colorString = "white";
    }
    private Image LoadKing(){
        return new Image("Images/" + colorString + "_king.png");
    }
    private Image LoadQueen(){
        return new Image("Images/" + colorString + "_queen.png");
    }
    private Image LoadRook(){
        return new Image("Images/" + colorString + "_rook.png");
    }
    private Image LoadBishop(){
        return new Image("Images/" + colorString + "_bishop.png");
    }
    private Image LoadKnight(){
        return new Image("Images/" + colorString + "_knight.png");
    }
    private Image LoadPawn(){
        return new Image("Images/" + colorString + "_pawn.png");
    }
    public Image LoadPiece(PieceType type){
        switch (type){
            case King:
                return LoadKing();
            case Queen:
                return LoadQueen();
            case Rook:
                return LoadRook();
            case Bishop:
                return LoadBishop();
            case Knight:
                return LoadKnight();
            case Pawn:
                return LoadPawn();
        }
        return null;
    }
}
