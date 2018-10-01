/**
 * Represents an Piece in a game of chess; cannot be instantiated
 *
 * @author atirath6
 * @version 1.0
 */
public abstract class Piece {

    private Color color;

    /**
     * Creates a Piece given the color of the piece
     *
     *@param color the color of the Piece (WHITE or BLACK)
     */
    public Piece(Color color) {
        this.color = color;
    }

    /**
     * @return this Piece's color
     */
    public Color getColor() {
        return this.color;
    }

    /**
     * @return the algebraic name of a certain type of Piece.
     */
    public abstract String algebraicName();

    /**
     * @return the name of a piece as it would appear in FEN notation.
     */
    public abstract String fenName();

    /**
     * Given a specific Square, finds each position that the Piece could have
     *                  moved from
     *
     *@param square the position on the board that the Piece is being moved to
     *
     *@return The array of positions that the Piece could have moved from
     */
    public abstract Square[] movesFrom(Square square);


    protected int fileToNum(char file) {

        int toNum;
        if (file == 'a') {
            toNum = 1;
        } else if (file == 'b') {
            toNum = 2;
        } else if (file == 'c') {
            toNum = 3;
        } else if (file == 'd') {
            toNum = 4;
        } else if (file == 'e') {
            toNum = 5;
        } else if (file == 'f') {
            toNum = 6;
        } else if (file == 'g') {
            toNum = 7;
        } else if (file == 'h') {
            toNum = 8;
        } else {
            toNum = -1;
        }
        return toNum;
    }

    protected char numToFile(int num) {

        char tochar;
        if (num == 1) {
            tochar = 'a';
        } else if (num == 2) {
            tochar = 'b';
        } else if (num == 3) {
            tochar = 'c';
        } else if (num == 4) {
            tochar = 'd';
        } else if (num == 5) {
            tochar = 'e';
        } else if (num == 6) {
            tochar = 'f';
        } else if (num == 7) {
            tochar = 'g';
        } else if (num == 8) {
            tochar = 'h';
        } else {
            tochar = 'z';
        }
        return tochar;
    }

    protected boolean isLegalMove(int file, int rank) {

        boolean checker = false;
        if (file > 0 && file < 9) {
            if (rank > 0 && rank < 9) {
                checker = true;
            }
        }
        return checker;
    }
}
