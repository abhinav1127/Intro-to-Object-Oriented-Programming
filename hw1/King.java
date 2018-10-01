/**
 * Represents a piece of type King.
 *
 * @author atirath6
 * @version 1.0
 */
public class King extends Piece {

    /**
     * Creates a King given the color of the piece
     *
     *@param color the color of the Piece (WHITE or BLACK)
     */
    public King(Color color) {
        super(color);
    }

    @Override
    public String algebraicName() {
        return "K";
    }

    @Override
    public String fenName() {

        String fenName;
        if (this.getColor() == Color.WHITE) {
            fenName = "K";
        } else {
            fenName = "k";
        }

        return fenName;
    }

    @Override
    public Square[] movesFrom(Square square) {
        int initialFile = super.fileToNum(square.getFile());
        int initialRank = Character.getNumericValue(square.getRank());
        int movesPossible = 0;
        int[][] positionsPossible = new int[8][2];
        if (super.isLegalMove((initialFile + 1), (initialRank + 0))) {
            positionsPossible[movesPossible][0] = initialFile + 1;
            positionsPossible[movesPossible][1] = initialRank + 0;
            movesPossible++;
        }
        if (super.isLegalMove((initialFile + 1), (initialRank + 1))) {
            positionsPossible[movesPossible][0] = initialFile + 1;
            positionsPossible[movesPossible][1] = initialRank + 1;
            movesPossible++;
        }
        if (super.isLegalMove((initialFile + 1), (initialRank - 1))) {
            positionsPossible[movesPossible][0] = initialFile + 1;
            positionsPossible[movesPossible][1] = initialRank - 1;
            movesPossible++;
        }
        if (super.isLegalMove((initialFile + 0), (initialRank + 1))) {
            positionsPossible[movesPossible][0] = initialFile;
            positionsPossible[movesPossible][1] = initialRank + 1;
            movesPossible++;
        }
        if (super.isLegalMove((initialFile + 0), (initialRank - 1))) {
            positionsPossible[movesPossible][0] = initialFile;
            positionsPossible[movesPossible][1] = initialRank - 1;
            movesPossible++;
        }
        if (super.isLegalMove((initialFile - 1), (initialRank + 1))) {
            positionsPossible[movesPossible][0] = initialFile - 1;
            positionsPossible[movesPossible][1] = initialRank + 1;
            movesPossible++;
        }
        if (super.isLegalMove((initialFile - 1), (initialRank))) {
            positionsPossible[movesPossible][0] = initialFile - 1;
            positionsPossible[movesPossible][1] = initialRank;
            movesPossible++;
        }
        if (super.isLegalMove((initialFile - 1), (initialRank - 1))) {
            positionsPossible[movesPossible][0] = initialFile - 1;
            positionsPossible[movesPossible][1] = initialRank - 1;
            movesPossible++;
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
