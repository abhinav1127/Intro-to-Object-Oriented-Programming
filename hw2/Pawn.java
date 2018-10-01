/**
 * Represents a piece of type Pawn.
 *
 * @author atirath6
 * @version 1.0
 */
public class Pawn extends Piece {

    /**
     * Creates a Pawn given the color of the piece
     *
     *@param color the color of the Piece (WHITE or BLACK)
     */
    public Pawn(Color color) {
        super(color);
    }

    @Override
    public String algebraicName() {
        return "";
    }

    @Override
    public String fenName() {

        String fenName;
        if (this.getColor() == Color.WHITE) {
            fenName = "P";
        } else {
            fenName = "p";
        }

        return fenName;
    }

    @Override
    public Square[] movesFrom(Square square) {

        int initialRank = Character.getNumericValue(square.getRank());
        Square[] squareArray;
        if (this.getColor() == Color.WHITE) {
            if (initialRank == 2) {

                squareArray = new Square[2];
                squareArray[0] = new Square(square.getFile(), '3');
                squareArray[1] = new Square(square.getFile(), '4');
            } else if (initialRank == 8) {
                squareArray = new Square[0];
            } else {
                squareArray = new Square[1];
                squareArray[0] = new Square(square.getFile(),
                            Integer.toString(initialRank + 1).charAt(0));
            }

        } else {

            if (initialRank == 7) {

                squareArray = new Square[2];
                squareArray[0] = new Square(square.getFile(), '6');
                squareArray[1] = new Square(square.getFile(), '5');
            } else if (initialRank == 1) {
                squareArray = new Square[0];
            } else {
                squareArray = new Square[1];
                squareArray[0] = new Square(square.getFile(),
                            Integer.toString(initialRank - 1).charAt(0));
            }
        }

        return squareArray;
    }
}
