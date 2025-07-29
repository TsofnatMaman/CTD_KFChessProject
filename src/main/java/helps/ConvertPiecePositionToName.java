package helps;

import pieces.Position;

public class ConvertPiecePositionToName {

    public static String getName(Position pos){
        return (char)(pos.getRow()+'A')+""+(pos.getCol()+1);
    }
}
