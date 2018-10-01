/**
 * Represents a piece of type Queen.
 *
 * @author atirath6
 * @version 1.0
 */
public class Queen extends Piece {

    /**
     * Creates a Queen given the color of the piece
     *
     *@param color the color of the Piece (WHITE or BLACK)
     */
    public Queen(Color color) {
        super(color);
    }

    @Override
    public String algebraicName() {
        return "Q";
    }

    @Override
    public String fenName() {

        String fenName;
        if (this.getColor() == Color.WHITE) {
            fenName = "Q";
        } else {
            fenName = "q";
        }

        return fenName;
    }

    @Override
    public Square[] movesFrom(Square square) {

        Bishop bishop = new Bishop(Color.WHITE);
        Rook rook = new Rook(Color.WHITE);
        Square[] bishopMoves = bishop.movesFrom(square);
        Square[] rookMoves = rook.movesFrom(square);
        Square[] squareArray = new Square[bishopMoves.length
                    + rookMoves.length];
        int counter = 0;
        for (int i = 0; i < bishopMoves.length; i++) {
            squareArray[counter] = bishopMoves[i];
            counter++;
        }
        for (int i = 0; i < rookMoves.length; i++) {
            squareArray[counter] = rookMoves[i];
            counter++;
        }
        return squareArray;
    }
}
