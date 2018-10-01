/**
 * Represents a piece of type Knight.
 *
 * @author atirath6
 * @version 1.0
 */
public class Knight extends Piece {

    /**
     * Creates a Knight given the color of the piece
     *
     *@param color the color of the Piece (WHITE or BLACK)
     */
    public Knight(Color color) {
        super(color);
    }

    @Override
    public String algebraicName() {
        return "N";
    }

    @Override
    public String fenName() {

        String fenName;
        if (this.getColor() == Color.WHITE) {
            fenName = "N";
        } else {
            fenName = "n";
        }

        return fenName;
    }

    @Override
    public Square[] movesFrom(Square square) {

        int initialFile = super.fileToNum(square.getFile());
        int initialRank = Character.getNumericValue(square.getRank());
        int movesPossible = 0;
        int[][] positionsPossible = new int[8][2];
        if (super.isLegalMove((initialFile + 2), (initialRank + 1))) {
            movesPossible++;
            positionsPossible[0][0] = initialFile + 2;
            positionsPossible[0][1] = initialRank + 1;
        }
        if (super.isLegalMove((initialFile + 1), (initialRank + 2))) {
            movesPossible++;
            positionsPossible[1][0] = initialFile + 1;
            positionsPossible[1][1] = initialRank + 2;
        }
        if (super.isLegalMove((initialFile - 1), (initialRank + 2))) {
            movesPossible++;
            positionsPossible[2][0] = initialFile - 1;
            positionsPossible[2][1] = initialRank + 2;
        }
        if (super.isLegalMove((initialFile - 2), (initialRank + 1))) {
            movesPossible++;
            positionsPossible[3][0] = initialFile - 2;
            positionsPossible[3][1] = initialRank + 1;
        }
        if (super.isLegalMove((initialFile + 2), (initialRank - 1))) {
            movesPossible++;
            positionsPossible[4][0] = initialFile + 2;
            positionsPossible[4][1] = initialRank - 1;
        }
        if (super.isLegalMove((initialFile + 1), (initialRank - 2))) {
            movesPossible++;
            positionsPossible[5][0] = initialFile + 1;
            positionsPossible[5][1] = initialRank - 2;
        }
        if (super.isLegalMove((initialFile - 1), (initialRank - 2))) {
            movesPossible++;
            positionsPossible[6][0] = initialFile - 1;
            positionsPossible[6][1] = initialRank - 2;
        }
        if (super.isLegalMove((initialFile - 2), (initialRank - 1))) {
            movesPossible++;
            positionsPossible[7][0] = initialFile - 2;
            positionsPossible[7][1] = initialRank - 1;
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
