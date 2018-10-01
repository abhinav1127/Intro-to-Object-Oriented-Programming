import java.util.Optional;

/**
 * Represents half of a move
 *
 * @author atirath6
 * @version 1.0
 */
public class Ply {

    private Piece piece;
    private Square from;
    private Square to;
    private Optional<String> comment;

    /**
     * Creates a Ply with the given parameters
     * @param  piece the Piece that is being moved
     * @param  from where the Piece is moving from
     * @param  to where the Piece is moving to
     * @param  comment1 a comment about the move, may be null
     */
    public Ply(Piece piece, Square from, Square to, Optional<String> comment1) {
        this.piece = piece;
        this.from = from;
        this.to = to;
        this.comment = comment1;
    }

    /**
     * @return the Piece that is moving
     */
    public Piece getPiece() {
        return this.piece;
    }

    /**
     * @return the From Square
     */
    public Square getFrom() {
        return this.from;
    }

    /**
     * @return the To Square
     */
    public Square getTo() {
        return this.to;
    }

    /**
     * @return the comment, if it exists
     */
    public Optional<String> getComment() {
        return comment;
    }

}
