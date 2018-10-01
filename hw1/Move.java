/**
 * Represents a move of both a white and black Piece in a ChessGame
 *
 * @author atirath6
 * @version 1.0
 */
public class Move {

    private Ply whitePly;
    private Ply blackPly;

    /**
     * Creates a Move with the given parameters
     * @param  whitePly the Ply of the color White
     * @param  blackPly the Ply of the color Black
     */
    public Move(Ply whitePly, Ply blackPly) {
        this.whitePly = whitePly;
        this.blackPly = blackPly;
    }

    /**
     * @return the Ply of the color White
     */
    public Ply getWhitePly() {
        return this.whitePly;
    }

    /**
     * @return the Ply of the color Black
     */
    public Ply getBlackPly() {
        return this.blackPly;
    }
}
