import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * Represents a game of chess with metadata
 *
 * @author atirath6
 * @version 1.0
 */
public class ChessGame {

    private StringProperty event = new SimpleStringProperty(this, "NA");
    private StringProperty site = new SimpleStringProperty(this, "NA");
    private StringProperty date = new SimpleStringProperty(this, "NA");
    private StringProperty white = new SimpleStringProperty(this, "NA");
    private StringProperty black = new SimpleStringProperty(this, "NA");
    private StringProperty result = new SimpleStringProperty(this, "NA");
    private List<String> moves;
    private String opening;

    /**
     * Constructor for ChessGame
     * @param event  this signifies the event of the game
     * @param site   this signifies the site of the game
     * @param date   this signifies the date of the game
     * @param white  this signifies the player who played white
     * @param black  this signifies the player who played black
     * @param result this signifies the result of the game
     */
    public ChessGame(String event, String site, String date,
                     String white, String black, String result) {
        this.event.set(event);
        this.site.set(site);
        this.date.set(date);
        this.white.set(white);
        this.black.set(black);
        this.result.set(result);
        moves = new ArrayList<>();
        opening = "Not recognized";
    }

    /**
     * adds a move to the game
     * @param move describes the move to be added
     */
    public void addMove(String move) {
        moves.add(move);
        if (moves.size() >= 3) {
            opening = openingDeterminer();
        }
    }

    /**
     * returns a specific move
     * @param n number of the move to be returned
     * @return the selected move
     */
    public String getMove(int n) {
        return moves.get(n - 1);
    }

    /**
     * returns the opening
     * @return the opening
     */
    public String getOpening() {
        return opening;
    }

    /**
     * returns the event
     * @return the event
     */
    public String getEvent() {
        return event.get();
    }

    /**
     * returns the site
     * @return the site
     */
    public String getSite() {
        return site.get();
    }

    /**
     * returns the date
     * @return the date
     */
    public String getDate() {
        return date.get();
    }

    /**
     * returns the white player's name
     * @return the white player's name
     */
    public String getWhite() {
        return white.get();
    }

    /**
     * returns the black player's name
     * @return the black player's name
     */
    public String getBlack() {
        return black.get();
    }

    /**
     * returns the result
     * @return the result
     */
    public String getResult() {
        return result.get();
    }

    /**
     * returns the list of moves
     * @return the list of moves
     */
    public List<String> getMoves() {
        return this.moves;
    }

    private String openingDeterminer() {

        if (this.getMove(1).equals("e4 c5")) {
            return "Sicilian Defence";
        } else if (this.getMove(1).equals("d4 Nf6")) {
            return "Indian Defence";
        } else if (this.getMove(1).equals("d4 d5")
                    && this.getMove(2).contains("c4")) {
            return "Queen's Gambit";
        } else if (this.getMove(1).equals("e4 e5")) {
            if (this.getMove(2).equals("Nf3 d6")) {
                return "Philidor Defence";
            } else if (this.getMove(2).equals("Nf3 Nc6")) {
                if (this.getMove(2).equals("Bc4 Bc5")) {
                    return "Giuoco Piano";
                }
                String bb5 = this.getMove(3).substring(0, 3);
                if (bb5.equals("Bb5")) {
                    return "Ruy Lopez";
                }
            }
        }

        return "Not recognized";
    }
}
