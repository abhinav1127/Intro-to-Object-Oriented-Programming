/**
 * Represents a piece of type Rook.
 *
 * @author atirath6
 * @version 1.0
 */
public class Rook extends Piece {

    /**
     * Creates a Rook given the color of the piece
     *
     *@param color the color of the Piece (WHITE or BLACK)
     */
    public Rook(Color color) {
        super(color);
    }

    @Override
    public String algebraicName() {
        return "R";
    }

    @Override
    public String fenName() {

        String fenName;
        if (this.getColor() == Color.WHITE) {
            fenName = "R";
        } else {
            fenName = "r";
        }

        return fenName;
    }

    @Override
    public Square[] movesFrom(Square square) {

        Square[] squareArray = new Square[14];
        int counter = 0;
        int initialRank = Character.getNumericValue(square.getRank());
        for (int i = 1; i < 9; i++) {
            if (i != initialRank) {
                squareArray[counter] = new Square(square.getFile(),
                            Integer.toString(i).charAt(0));
                counter++;
            }
        }

        for (int i = 1; i < 9; i++) {
            if (i != super.fileToNum(square.getFile())) {
                char correctFile = super.numToFile(i);
                squareArray[counter] = new Square(correctFile,
                            Integer.toString(initialRank).charAt(0));
                counter++;
            }
        }
        return squareArray;
    }
}
