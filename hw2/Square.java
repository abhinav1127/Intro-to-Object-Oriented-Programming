/**
 * Represents a square on a chessboard.
 *
 * @author atirath6
 * @version 1.0
 */
public class Square {

    private char file;
    private char rank;

    /**
     * Creates a Square given its rank and file
     *
     *@param file the file to which the square belongs
     *@param rank the rank to which the square belongs
     */
    public Square(char file, char rank) {
        this(Character.toString(file) + (Character.toString(rank)));
    }

    /**
     * Creates a Square given its rank and file
     *
     *@param name the file and name combined in the form "a1"
     */
    public Square(String name) {
        this.file = name.charAt(0);
        this.rank = name.charAt(1);
    }

    /**
     *@return the file and rank of the piece in a String
     */
    public String toString() {
        return (Character.toString(file)) + (Character.toString(rank));
    }

    /**
     * Checks whether two Squares have the same file and rank
     *
     *@param obj the Square that we are comparing this Square to
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Square)) {
            return false;
        }
        Square square1 = (Square) obj;
        if (square1.getFile() == this.getFile()) {
            if (square1.getRank() == this.getRank()) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return this Square's file
     */
    public char getFile() {
        return this.file;
    }

    /**
     * @return this Square's rank
     */
    public char getRank() {
        return this.rank;
    }

}
