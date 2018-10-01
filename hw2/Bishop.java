/**
 * Represents a piece of type Bishop.
 *
 * @author atirath6
 * @version 1.0
 */
public class Bishop extends Piece {

    /**
     * Creates a Bishop given the color of the piece
     *
     *@param color the color of the Piece (WHITE or BLACK)
     */
    public Bishop(Color color) {
        super(color);
    }

    @Override
    public String algebraicName() {
        return "B";
    }

    @Override
    public String fenName() {

        String fenName;
        if (this.getColor() == Color.WHITE) {
            fenName = "B";
        } else {
            fenName = "b";
        }

        return fenName;
    }

    @Override
    public Square[] movesFrom(Square square) {

        int initialFile = super.fileToNum(square.getFile());
        int initialRank = Character.getNumericValue(square.getRank());
        int movesPossible = 0;
        int[][] positionsPossible = new int[13][2];
        for (int i = 1; i < 8; i++) {
            if (isLegalMove((initialFile + i), (initialRank + i))) {
                positionsPossible[movesPossible][0] = initialFile + i;
                positionsPossible[movesPossible][1] = initialRank + i;
                movesPossible++;
            }
            if (isLegalMove((initialFile + i), (initialRank - i))) {
                positionsPossible[movesPossible][0] = initialFile + i;
                positionsPossible[movesPossible][1] = initialRank - i;
                movesPossible++;
            }
            if (isLegalMove((initialFile - i), (initialRank + i))) {
                positionsPossible[movesPossible][0] = initialFile - i;
                positionsPossible[movesPossible][1] = initialRank + i;
                movesPossible++;
            }
            if (isLegalMove((initialFile - i), (initialRank - i))) {
                positionsPossible[movesPossible][0] = initialFile - i;
                positionsPossible[movesPossible][1] = initialRank - i;
                movesPossible++;
            }
        }

        Square[] squareArray = new Square[movesPossible];
        int counter = 0;
        for (int i = 0; i < positionsPossible.length; i++) {
            if (positionsPossible[i][0] > 0) {
                char correctFile = super.numToFile(positionsPossible[i][0]);
                squareArray[counter] = new Square(
                            correctFile,
                            Integer.toString(positionsPossible[i][1]).
                            charAt(0));
                counter++;
            }
        }

        return squareArray;

    }
}
