import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PgnReader {

    /**
     * Find the tagName tag pair in a PGN game and return its value.
     *
     * @see http://www.saremba.de/chessgml/standards/pgn/pgn-complete.htm
     *
     * @param tagName the name of the tag whose value you want
     * @param game a `String` containing the PGN text of a chess game
     * @return the value in the named tag pair
     */
    public static String tagValue(String tagName, String game) {
        int tagNameIndex = game.indexOf(tagName);
        if (tagNameIndex == -1) {
            return "NOT GIVEN";
        }
        int startingPosition = game.indexOf("\"", tagNameIndex + 1);
        int endingPosition = game.indexOf("\"", startingPosition + 1);
        String nameOfTag = game.substring(startingPosition + 1, endingPosition);
        return nameOfTag;
    }

    /**
     * Play out the moves in game and return a String with the game's
     * final position in Forsyth-Edwards Notation (FEN).
     *
     * @see http://www.saremba.de/chessgml/standards/pgn/pgn-complete.htm#c16.1
     *
     * @param game a `Strring` containing a PGN-formatted chess game or opening
     * @return the game's final position in FEN.
     */
    public static String finalPosition(String game) {

        game = removeNewLines(game);
        game = game.trim();
        //Gets the final game board from handlesEachMove
        char[][] chessBoard = handlesEachMove(game);

        //The rest of this method finds the String that denotes the postiion
        int counter = 0;
        String chessLayout = "";
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {

                if (!(chessBoard[row][col] == '-')) {

                    if (counter != 0) {
                        chessLayout += counter;
                        counter = 0;
                    }
                    chessLayout += chessBoard[row][col];

                } else {
                    counter++;
                }
                if (col == 0) {
                    if (counter != 0) {
                        chessLayout += counter;
                    }
                    if (row != 0) {
                        chessLayout += "/";
                    }
                    counter = 0;
                }
            }
        }
        return chessLayout;
    }

    public static char[][] handlesEachMove(String game) {

        char[][] chessBoard = {{'R', 'N', 'B', 'K', 'Q', 'B', 'N', 'R' },
                               {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' },
                               {'-', '-', '-', '-', '-', '-', '-', '-' },
                               {'-', '-', '-', '-', '-', '-', '-', '-' },
                               {'-', '-', '-', '-', '-', '-', '-', '-' },
                               {'-', '-', '-', '-', '-', '-', '-', '-' },
                               {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p' },
                               {'r', 'n', 'b', 'k', 'q', 'b', 'n', 'r' } };

        //Checks if game is over
        boolean gameOver = false;
        //Tellus which move we are working with
        int moveCount = 1;
        //Finds the index of of the move and calls moveWhite and moveBlack
        while (!gameOver) {
            int moveIndex = game.indexOf((Integer.toString(moveCount)) + ". ");
            if (!(moveIndex == -1)) {

                chessBoard = moveWhite(game, moveIndex, chessBoard);
                chessBoard = moveBlack(game, moveIndex, chessBoard);
                moveCount++;
            } else {
                gameOver = true;
            }
        }

        return chessBoard;
    }

    public static char[][] moveWhite(String game, int moveIndex,
                char[][] chessBoard) {

        //These next four lines find just the text that describe white's move
        int spaceBeforeMove = game.indexOf(" ", moveIndex + 1);
        int spaceAfterMove =  game.indexOf(" ", spaceBeforeMove + 2);
        //This String holds the exact description of the move we have to do
        String abbrevAlgNotation;
        if (spaceAfterMove == -1) {
            spaceAfterMove = game.length() - 1;
            abbrevAlgNotation = game.substring(spaceBeforeMove + 1,
            spaceAfterMove + 1);
        } else {
            abbrevAlgNotation = game.substring(spaceBeforeMove + 1,
            spaceAfterMove);
        }
        if (abbrevAlgNotation.indexOf("O") != -1) {
            if (abbrevAlgNotation.indexOf("O-O-O") != -1) {
                chessBoard = performCastle(true, false, chessBoard);
            } else {
                chessBoard = performCastle(true, true, chessBoard);
            }
            return chessBoard;
        }
        int digitFinder = 0;
        char firstChar;
        char secondChar;
        String positionToMoveTo = "";
        for (int i = abbrevAlgNotation.length() - 2; i >= 0; i--) {
            firstChar = abbrevAlgNotation.charAt(i);
            secondChar = abbrevAlgNotation.charAt(i + 1);
            if ((Character.isLowerCase(firstChar))
                && (Character.isDigit(secondChar))) {
                positionToMoveTo = new
                StringBuilder().append(firstChar).
                append(secondChar).toString();
            }
        }
        // The next 5 lines will determine the position that is being moved to
        char columnNumber = positionToMoveTo.charAt(1);
        int rowOfPositionToMoveTo = Character.getNumericValue(columnNumber) - 1;
        int colOfPositionToMoveTo = colDeterminer(positionToMoveTo.charAt(0));

        //Determines what type of piece is moving i.e. Knight, Bishop
        char whichTypeMoves = typeToMoveDeterminer(
            abbrevAlgNotation.charAt(0));

        //This is the case in which the type of piece is specified
        if (whichTypeMoves != 'z') {

            int counter = 0;
            //These are only important if there is only one type of the piece
            int rowOfMatch = -1;
            int colOfMatch = -1;
            /*Parsing through the chessBoard to check how many of the specified
            piece of the correct color exist.*/
            for (int row = 7; row > -1; row--) {
                for (int col = 7; col > -1; col--) {
                    if (chessBoard[row][col] == whichTypeMoves) {
                        counter++;
                        rowOfMatch = row;
                        colOfMatch = col;
                    }
                }
            }
            if (counter == 1) {
                chessBoard = executeMove(chessBoard, rowOfMatch, colOfMatch,
                rowOfPositionToMoveTo, colOfPositionToMoveTo);
            } else {

                if (whichTypeMoves == 'N') {
                    chessBoard = moveWhiteKnight(chessBoard,
                    rowOfPositionToMoveTo, colOfPositionToMoveTo, counter,
                    abbrevAlgNotation);

                } else if (whichTypeMoves == 'B') {

                    chessBoard = moveWhiteBishop(chessBoard,
                    rowOfPositionToMoveTo, colOfPositionToMoveTo, counter,
                    abbrevAlgNotation);

                } else if (whichTypeMoves == 'R') {

                    chessBoard = moveWhiteRook(chessBoard,
                    rowOfPositionToMoveTo, colOfPositionToMoveTo, counter,
                    abbrevAlgNotation);

                } else if (whichTypeMoves == 'Q') {

                    chessBoard = moveWhiteQueen(chessBoard,
                    rowOfPositionToMoveTo, colOfPositionToMoveTo, counter,
                    abbrevAlgNotation);

                } else if (whichTypeMoves == 'K') {
                    chessBoard = moveWhiteKing(chessBoard,
                    rowOfPositionToMoveTo, colOfPositionToMoveTo, counter,
                    abbrevAlgNotation);
                }
            }
            //aka a pawn moves
        } else {
            int counter = 0;
            /*Parsing through the chessBoard to check how many of the specified
            piece of the correct color exist.*/
            for (int row = 7; row > -1; row--) {
                for (int col = 7; col > -1; col--) {
                    if (chessBoard[row][col] == 'P') {
                        counter++;
                    }
                }
            }

            chessBoard = moveWhitePawn(chessBoard, rowOfPositionToMoveTo,
            colOfPositionToMoveTo, counter,
            abbrevAlgNotation);

            if (abbrevAlgNotation.indexOf("=") != -1) {
                chessBoard = promotePawn(abbrevAlgNotation,
                rowOfPositionToMoveTo, colOfPositionToMoveTo, chessBoard);
            }
        }

        return chessBoard;
    }

    public static char[][] moveBlack(String game, int moveIndex,
              char[][] chessBoard) {


        int firstSpace = game.indexOf(" ", moveIndex + 1);
        int spaceBeforeMove =  game.indexOf(" ", firstSpace + 2);
        int spaceAfterMove =  game.indexOf(" ", spaceBeforeMove + 2);
        String abbrevAlgNotation;
        if (spaceBeforeMove == -1) {
            return chessBoard;
        }
        if (spaceAfterMove == -1) {
            spaceAfterMove = game.length() - 1;
            abbrevAlgNotation = game.substring(spaceBeforeMove + 1,
                          spaceAfterMove + 1);
        } else {
            abbrevAlgNotation = game.substring(spaceBeforeMove + 1,
                      spaceAfterMove);
        }
        if (abbrevAlgNotation.indexOf("O") != -1) {
            if (abbrevAlgNotation.indexOf("O-O-O") != -1) {
                chessBoard = performCastle(false, false, chessBoard);
            } else {
                chessBoard = performCastle(false, true, chessBoard);
            }
            return chessBoard;
        }
        /*This ensures that the board that was passed to blackMove does
        not change if the game ends after white's move */
        char checkForOctothorpe = abbrevAlgNotation.charAt(
                    abbrevAlgNotation.length() - 1);
        if (Character.isDigit(abbrevAlgNotation.charAt(0))) {
            return chessBoard;
        }

        int digitFinder = 0;
        char firstChar;
        char secondChar;
        String positionToMoveTo = "";
        for (int i = abbrevAlgNotation.length() - 2; i >= 0; i--) {
            firstChar = abbrevAlgNotation.charAt(i);
            secondChar = abbrevAlgNotation.charAt(i + 1);
            if ((Character.isLowerCase(firstChar))
                    && (Character.isDigit(secondChar))) {
                positionToMoveTo = new
                        StringBuilder().append(firstChar).
                        append(secondChar).toString();
            }
        }
        // The next 5 lines will determine the position that is being moved to
        char columnNumber = positionToMoveTo.charAt(1);
        int rowOfPositionToMoveTo = Character.getNumericValue(columnNumber) - 1;
        int colOfPositionToMoveTo = colDeterminer(positionToMoveTo.charAt(0));
        //Determines what type of piece is moving i.e. Knight, Bishop
        char whichTypeMoves = typeToMoveDeterminer(
                        abbrevAlgNotation.charAt(0));
        //This is the case in which the type of piece is specified
        if (whichTypeMoves != 'z') {

            int counter = 0;
            //These are only important if there is only one type of the piece
            int rowOfMatch = -1;
            int colOfMatch = -1;
            /*Parsing through the chessBoard to check how many of the specified
            piece of the correct color exist.*/
            for (int row = 7; row > -1; row--) {
                for (int col = 7; col > -1; col--) {
                    if (chessBoard[row][col]
                                == Character.toLowerCase(whichTypeMoves)) {
                        counter++;
                        rowOfMatch = row;
                        colOfMatch = col;
                    }
                }
            }

            if (counter == 1) {

                chessBoard = executeMove(chessBoard, rowOfMatch, colOfMatch,
                            rowOfPositionToMoveTo, colOfPositionToMoveTo);
            } else {

                if (whichTypeMoves == 'N') {
                    chessBoard = moveBlackKnight(chessBoard,
                    rowOfPositionToMoveTo, colOfPositionToMoveTo, counter,
                    abbrevAlgNotation);

                } else if (whichTypeMoves == 'B') {

                    chessBoard = moveBlackBishop(chessBoard,
                    rowOfPositionToMoveTo, colOfPositionToMoveTo, counter,
                    abbrevAlgNotation);

                } else if (whichTypeMoves == 'R') {

                    chessBoard = moveBlackRook(chessBoard,
                    rowOfPositionToMoveTo, colOfPositionToMoveTo, counter,
                    abbrevAlgNotation);

                } else if (whichTypeMoves == 'Q') {

                    chessBoard = moveBlackQueen(chessBoard,
                    rowOfPositionToMoveTo, colOfPositionToMoveTo, counter,
                    abbrevAlgNotation);

                } else if (whichTypeMoves == 'K') {
                    chessBoard = moveBlackKing(chessBoard,
                    rowOfPositionToMoveTo, colOfPositionToMoveTo, counter,
                    abbrevAlgNotation);
                }
            }
            //a pawn moves
        } else {
            int counter = 0;
            /*Parsing through the chessBoard to check how many of the specified
            piece of the correct color exist.*/
            for (int row = 7; row > -1; row--) {
                for (int col = 7; col > -1; col--) {
                    if (chessBoard[row][col] == 'p') {
                        counter++;
                    }
                }
            }

            chessBoard = moveBlackPawn(chessBoard, rowOfPositionToMoveTo,
            colOfPositionToMoveTo, counter,
            abbrevAlgNotation);

            if (abbrevAlgNotation.indexOf("=") != -1) {
                chessBoard = promotePawn(abbrevAlgNotation,
                rowOfPositionToMoveTo, colOfPositionToMoveTo, chessBoard);
            }
        }

        return chessBoard;
    }

    public static char typeToMoveDeterminer(char type) {

        if (type == 'Q') {
            return type;
        } else if (type == 'Q') {
            return type;
        } else if (type == 'B') {
            return type;
        } else if (type == 'R') {
            return type;
        } else if (type == 'N') {
            return type;
        } else if (type == 'K') {
            return type;
        } else {
            return 'z';
        }
    }

    public static int colDeterminer(char letter) {

        if (letter == 'h') {
            return 0;
        }
        if (letter == 'g') {
            return 1;
        }
        if (letter == 'f') {
            return 2;
        }
        if (letter == 'e') {
            return 3;
        }
        if (letter == 'd') {
            return 4;
        }
        if (letter == 'c') {
            return 5;
        }
        if (letter == 'b') {
            return 6;
        }
        if (letter == 'a') {
            return 7;
        } else {
            return -1;
        }
    }

    public static char[][] executeMove(char[][] chessBoard, int rowOfOriginal,
                    int colOfOriginal,  int rowOfFinal, int colOfFinal) {

        //accounts for en passants
        if (chessBoard[rowOfOriginal][colOfOriginal] == 'P') {
            if (rowOfOriginal == 4) {
                if ((colOfFinal == colOfOriginal - 1)
                            || (colOfFinal == colOfOriginal + 1)) {
                    if (chessBoard[rowOfFinal][colOfFinal] == '-') {
                        if (chessBoard[rowOfOriginal][colOfFinal] == 'p') {
                            chessBoard[rowOfOriginal][colOfFinal] = '-';
                        }
                    }
                }
            }
        }

        //accounts for en passants
        if (chessBoard[rowOfOriginal][colOfOriginal] == 'p') {
            if (rowOfOriginal == 3) {
                if ((colOfFinal == colOfOriginal - 1)
                            || (colOfFinal == colOfOriginal + 1)) {
                    if (chessBoard[rowOfFinal][colOfFinal] == '-') {
                        if (chessBoard[rowOfOriginal][colOfFinal] == 'P') {
                            chessBoard[rowOfOriginal][colOfFinal] = '-';
                        }
                    }
                }
            }
        }

        char movingPiece = chessBoard[rowOfOriginal][colOfOriginal];
        chessBoard[rowOfFinal][colOfFinal] = movingPiece;
        chessBoard[rowOfOriginal][colOfOriginal] = '-';

        return chessBoard;
    }

    public static boolean canTheRookMoveHere(char[][] chessBoard,
                int rowOfOriginal, int colOfOriginal,
                int rowOfFinal, int colOfFinal) {

        /*if the original row and column are different than those of the final
        the rook will not be able to move there*/

        if (!((rowOfOriginal == rowOfFinal) || (colOfOriginal == colOfFinal))) {
            return false;
        } else if (rowOfOriginal == rowOfFinal) {
            if (colOfFinal > colOfOriginal) {
                for (int i = colOfOriginal + 1; i < colOfFinal; i++) {
                    if (chessBoard[rowOfFinal][i] != '-') {
                        return false;
                    }
                }

            } else {
                for (int i = colOfOriginal - 1; i > colOfFinal; i--) {
                    if (chessBoard[rowOfFinal][i] != '-') {
                        return false;
                    }
                }
            }

        //implies colOfOriginal == colOfFinal
        } else {
            if (rowOfFinal > rowOfOriginal) {
                for (int i = rowOfOriginal + 1; i < rowOfFinal; i++) {
                    if (chessBoard[i][colOfFinal] != '-') {
                        return false;
                    }
                }
            // implies rowOfFinal < rowOfOriginal
            } else {
                for (int i = rowOfOriginal - 1; i > rowOfFinal; i--) {
                    if (chessBoard[i][colOfFinal] != '-') {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    public static boolean canTheBishopMoveHere(char[][] chessBoard,
                int rowOfOriginal, int colOfOriginal,
                int rowOfFinal, int colOfFinal) {

        int differenceInRow = (rowOfFinal - rowOfOriginal);
        int differenceInCol = (colOfFinal - colOfOriginal);
        if (!(Math.abs(differenceInCol) == Math.abs(differenceInRow))) {
            return false;
        }

        if ((differenceInRow > 0) && (differenceInCol > 0)) {
            for (int i = 1; i < differenceInRow; i++) {
                if (chessBoard[rowOfOriginal + i][colOfOriginal + i] != '-') {
                    return false;
                }
            }
        } else if ((differenceInRow < 0) && (differenceInCol < 0)) {
            for (int i = 1; i < Math.abs(differenceInRow); i++) {
                if (chessBoard[rowOfOriginal - i][colOfOriginal - i] != '-') {
                    return false;
                }
            }
        } else if ((differenceInRow > 0) && (differenceInCol < 0)) {
            for (int i = 1; i < Math.abs(differenceInRow); i++) {
                if (chessBoard[rowOfOriginal + i][colOfOriginal - i] != '-') {
                    return false;
                }
            }
        } else if ((differenceInRow < 0) && (differenceInCol > 0)) {
            for (int i = 1; i < Math.abs(differenceInRow); i++) {
                if (chessBoard[rowOfOriginal - i][colOfOriginal + i] != '-') {
                    return false;
                }
            }

        }

        return true;

    }

    public static boolean canTheKnightMoveHere(char[][] chessBoard,
                int rowOfOriginal, int colOfOriginal,
                int rowOfFinal, int colOfFinal) {

        int differenceInRow = (rowOfFinal - rowOfOriginal);
        int differenceInCol = (colOfFinal - colOfOriginal);

        if ((differenceInRow == 2) && (differenceInCol == 1)) {
            return true;
        }
        if ((differenceInRow == 2) && (differenceInCol == -1)) {
            return true;
        }
        if ((differenceInRow == 1) && (differenceInCol == 2)) {
            return true;
        }
        if ((differenceInRow == 1) && (differenceInCol == -2)) {
            return true;
        }
        if ((differenceInRow == -1) && (differenceInCol == 2)) {
            return true;
        }
        if ((differenceInRow == -1) && (differenceInCol == -2)) {
            return true;
        }
        if ((differenceInRow == -2) && (differenceInCol == -1)) {
            return true;
        }
        if ((differenceInRow == -2) && (differenceInCol == 1)) {
            return true;
        }

        return false;
    }

    //COMBINE ROOK AND  BISHOP
    public static boolean canTheQueenMoveHere(char[][] chessBoard,
                int rowOfOriginal, int colOfOriginal,
                int rowOfFinal, int colOfFinal) {

        if (canTheBishopMoveHere(chessBoard, rowOfOriginal, colOfOriginal,
                rowOfFinal, colOfFinal)) {
            return true;
        } else if (canTheRookMoveHere(chessBoard, rowOfOriginal, colOfOriginal,
                rowOfFinal, colOfFinal)) {
            return true;
        }

        return false;

    }

    //NEED TO FIX, DOESN'T CHECK KING MOVING INTO CHECK
    public static boolean canTheKingMoveHere(char[][] chessBoard,
                int rowOfOriginal, int colOfOriginal,
                int rowOfFinal, int colOfFinal) {

        int differenceInRow = (rowOfFinal - rowOfOriginal);
        int differenceInCol = (colOfFinal - colOfOriginal);

        if ((differenceInRow <= 1) && (differenceInCol <= 1)) {
            return true;
        }
        return false;

    }

    public static boolean canTheWhitePawnMoveHere(char[][] chessBoard,
                int rowOfOriginal, int colOfOriginal,
                int rowOfFinal, int colOfFinal) {

        int differenceInRow = (rowOfFinal - rowOfOriginal);

        if (rowOfOriginal == 1) {
            if ((rowOfFinal == 3) && (colOfOriginal == colOfFinal)) {
                if ((chessBoard[2][colOfOriginal] == '-')
                            && (chessBoard[3][colOfOriginal] == '-')) {
                    return true;
                }
            }
        }

        if (differenceInRow != 1) {
            return false;
        }

        if ((colOfOriginal == colOfFinal)
                && (chessBoard[rowOfFinal][colOfFinal] == '-')) {
            return true;
        }

        char finalPosition = chessBoard[rowOfFinal][colOfFinal];
        if (colOfFinal == colOfOriginal + 1) {
            if (Character.isLowerCase(finalPosition)) {
                return true;
            }
        }

        if (colOfFinal == colOfOriginal - 1) {
            if (Character.isLowerCase(finalPosition)) {
                return true;
            }
        }

        if (rowOfOriginal == 4) {
            if ((colOfFinal == colOfOriginal - 1)
                        || (colOfFinal == colOfOriginal + 1)) {
                if (chessBoard[rowOfFinal][colOfFinal] == '-') {
                    if (chessBoard[rowOfOriginal][colOfFinal] == 'p') {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static boolean canTheBlackPawnMoveHere(char[][] chessBoard,
                int rowOfOriginal, int colOfOriginal,
                int rowOfFinal, int colOfFinal) {

        int differenceInRow = (rowOfFinal - rowOfOriginal);

        if (rowOfOriginal == 6) {
            if ((rowOfFinal == 4) && (colOfOriginal == colOfFinal)) {
                if ((chessBoard[5][colOfOriginal] == '-')
                            && (chessBoard[4][colOfOriginal] == '-')) {
                    return true;
                }
            }
        }

        if (differenceInRow != -1) {
            return false;
        }

        if ((colOfOriginal == colOfFinal)
                    && (chessBoard[rowOfFinal][colOfFinal] == '-')) {
            return true;
        }

        char finalPosition = chessBoard[rowOfFinal][colOfFinal];
        if (colOfFinal == colOfOriginal + 1) {
            if (Character.isUpperCase(finalPosition)) {
                return true;
            }
        }

        if (colOfFinal == colOfOriginal - 1) {
            if (Character.isUpperCase(finalPosition)) {
                return true;
            }
        }

        if (rowOfOriginal == 3) {
            if ((colOfFinal == colOfOriginal - 1)
                        || (colOfFinal == colOfOriginal + 1)) {
                if (chessBoard[rowOfFinal][colOfFinal] == '-') {
                    if (chessBoard[rowOfOriginal][colOfFinal] == 'P') {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static void printArray(char[][] chessBoard) {

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                System.out.print("[" + chessBoard[row][col] + "]");
            }

            System.out.println();
        }
        System.out.println();
    }

    public static char[][] performCastle(boolean white, boolean kingSide,
                char[][] chessBoard) {

        if (white && kingSide) {
            chessBoard = executeMove(chessBoard, 0, 3, 0, 1);
            chessBoard = executeMove(chessBoard, 0, 0, 0, 2);
        }

        if ((white) && (!kingSide)) {
            chessBoard = executeMove(chessBoard, 0, 3, 0, 5);
            chessBoard = executeMove(chessBoard, 0, 7, 0, 4);
        }

        if ((!white) && (kingSide)) {
            chessBoard = executeMove(chessBoard, 7, 3, 7, 1);
            chessBoard = executeMove(chessBoard, 7, 0, 7, 2);
        }

        if ((!white) && (!kingSide)) {
            chessBoard = executeMove(chessBoard, 7, 3, 7, 5);
            chessBoard = executeMove(chessBoard, 7, 7, 7, 4);
        }

        return chessBoard;
    }

    public static char[][] promotePawn(String abbrevAlgNotation,
                int row, int col,
                char[][] chessBoard) {

        int equalsIndex = abbrevAlgNotation.lastIndexOf("=");
        char typeToConvertTo = abbrevAlgNotation.charAt(equalsIndex + 1);
        if (Character.isUpperCase(chessBoard[row][col])) {
            chessBoard[row][col] = typeToConvertTo;
        } else {
            chessBoard[row][col] = Character.toLowerCase(typeToConvertTo);
        }
        return chessBoard;
    }

    public static char[][] moveWhiteKnight(char[][] chessBoard,
                    int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                    int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'N') {
                    if (canTheKnightMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo,
                        colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0;
                i < typeOfCorrectPieceBool.length;
                i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            if (colOfCorrectPiece[0] != colOfCorrectPiece[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowOfCorrectPiece[0]
                != rowOfCorrectPiece[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveWhiteBishop(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'B') {
                    if (canTheBishopMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo,
                        colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0;
                i < typeOfCorrectPieceBool.length;
                i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            if (colOfCorrectPiece[0] != colOfCorrectPiece[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowOfCorrectPiece[0]
                != rowOfCorrectPiece[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveWhiteRook(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'R') {
                    if (canTheRookMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo,
                        colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0;
                i < typeOfCorrectPieceBool.length;
                i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            if (colOfCorrectPiece[0] != colOfCorrectPiece[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowOfCorrectPiece[0] != rowOfCorrectPiece[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveWhiteQueen(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'Q') {
                    if (canTheQueenMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo, colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0;
                    i < typeOfCorrectPieceBool.length;
                    i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            if (colOfCorrectPiece[0] != colOfCorrectPiece[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowOfCorrectPiece[0]
                != rowOfCorrectPiece[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveWhiteKing(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'K') {
                    if (canTheKingMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo,
                        colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0; i < typeOfCorrectPieceBool.length; i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            if (colOfCorrectPiece[0] != colOfCorrectPiece[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowOfCorrectPiece[0] != rowOfCorrectPiece[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveWhitePawn(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'P') {
                    if (canTheWhitePawnMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo, colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0; i < typeOfCorrectPieceBool.length; i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            int[] rowChosen = new int[2];
            int[] colChosen = new int[2];
            int counter17 = 0;
            for (int x = 0; x < colOfCorrectPiece.length; x++) {
                if (typeOfCorrectPieceBool[x]) {
                    rowChosen[counter17] = rowOfCorrectPiece[x];
                    colChosen[counter17] = colOfCorrectPiece[x];
                    counter17++;
                }
            }
            if (colChosen[0] != colChosen[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colChosen[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowChosen[0], colChosen[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowChosen[1], colChosen[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowChosen[0] != rowChosen[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowChosen[0], colChosen[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowChosen[1], colChosen[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveBlackPawn(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'p') {
                    if (canTheBlackPawnMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo, colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0; i < typeOfCorrectPieceBool.length; i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {

            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            int[] rowChosen = new int[2];
            int[] colChosen = new int[2];
            int counter17 = 0;
            for (int x = 0; x < colOfCorrectPiece.length; x++) {
                if (typeOfCorrectPieceBool[x]) {
                    rowChosen[counter17] = rowOfCorrectPiece[x];
                    colChosen[counter17] = colOfCorrectPiece[x];
                    counter17++;
                }
            }
            if (colChosen[0] != colChosen[1]) {

                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colChosen[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowChosen[0], colChosen[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowChosen[1], colChosen[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowChosen[0] != rowChosen[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowChosen[0], colChosen[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowChosen[1], colChosen[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveBlackKnight(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'n') {
                    if (canTheKnightMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo, colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0; i < typeOfCorrectPieceBool.length; i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            if (colOfCorrectPiece[0] != colOfCorrectPiece[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowOfCorrectPiece[0] != rowOfCorrectPiece[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                        rowOfCorrectPiece[0], colOfCorrectPiece[0],
                        rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                        rowOfCorrectPiece[1], colOfCorrectPiece[1],
                        rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveBlackBishop(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'b') {
                    if (canTheBishopMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo, colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0; i < typeOfCorrectPieceBool.length; i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            if (colOfCorrectPiece[0] != colOfCorrectPiece[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowOfCorrectPiece[0] != rowOfCorrectPiece[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveBlackRook(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'r') {
                    if (canTheRookMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo, colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0; i < typeOfCorrectPieceBool.length; i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            if (colOfCorrectPiece[0] != colOfCorrectPiece[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowOfCorrectPiece[0] != rowOfCorrectPiece[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveBlackQueen(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == '1') {
                    if (canTheQueenMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo, colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0; i < typeOfCorrectPieceBool.length; i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            if (colOfCorrectPiece[0] != colOfCorrectPiece[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowOfCorrectPiece[0] != rowOfCorrectPiece[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static char[][] moveBlackKing(char[][] chessBoard,
                int rowOfPositionToMoveTo, int colOfPositionToMoveTo,
                int counter, String abbrevAlgNotation) {
        boolean[] typeOfCorrectPieceBool = new boolean[counter];
        int[] rowOfCorrectPiece = new int[counter];
        int[] colOfCorrectPiece = new int[counter];
        int counter1 = 0;
        //checks how many knights can move to the final position
        int counter2 = 0;
        for (int row = 7; row > -1; row--) {
            for (int col = 7; col > -1; col--) {
                if (chessBoard[row][col] == 'k') {
                    if (canTheKingMoveHere(chessBoard, row, col,
                        rowOfPositionToMoveTo, colOfPositionToMoveTo)) {
                        typeOfCorrectPieceBool[counter1] = true;
                        rowOfCorrectPiece[counter1] = row;
                        colOfCorrectPiece[counter1] = col;
                        counter1++;
                        counter2++;
                    } else {
                        typeOfCorrectPieceBool[counter1] = false;
                        rowOfCorrectPiece[counter1] = -1;
                        colOfCorrectPiece[counter1] = -1;
                        counter1++;
                    }
                }
            }
        }

        if (counter2 == 1) {
            for (int i = 0; i < typeOfCorrectPieceBool.length; i++) {

                if (typeOfCorrectPieceBool[i]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[i],
                    colOfCorrectPiece[i],
                    rowOfPositionToMoveTo,
                    colOfPositionToMoveTo);
                }
            }
            /*THIS DEALS WITH THE CASE IN WHICH TWO OF THE
            CORRECT TYPE OF PIECE CAN MOVE TO THE CORRECT
            POSITION. DEAL WITH LATER*/
        } else {
            char colFinder = '-';
            char rowFinder = '-';
            int i = 0;
            boolean found = false;
            if (colOfCorrectPiece[0] != colOfCorrectPiece[1]) {
                while (!found) {
                    colFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isLowerCase(colFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueCol = colDeterminer(colFinder);
                if (uniqueCol == colOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }

            } else if (rowOfCorrectPiece[0] != rowOfCorrectPiece[1]) {
                while (!found) {
                    rowFinder = abbrevAlgNotation.charAt(i);
                    if (Character.isDigit(rowFinder)) {
                        found = true;
                    }
                    i++;
                }
                int uniqueRow = Character.getNumericValue(rowFinder);
                if (uniqueRow == rowOfCorrectPiece[0]) {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[0], colOfCorrectPiece[0],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                } else {
                    chessBoard = executeMove(chessBoard,
                    rowOfCorrectPiece[1], colOfCorrectPiece[1],
                    rowOfPositionToMoveTo, colOfPositionToMoveTo);
                }
            }
        }
        return chessBoard;
    }

    public static String removeNewLines(String game) {
        game = game.replace("\n", " ");
        return game;
    }

    /**
     * Reads the file named by path and returns its content as a String.
     *
     * @param path the relative or abolute path of the file to read
     * @return a String containing the content of the file
     */
    public static String fileContent(String path) {
        Path file = Paths.get(path);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                // Add the \n that's removed by readline()
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            System.err.format("IOException: %s%n", e);
            System.exit(1);
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        String game = fileContent(args[0]);
        System.out.format("Event: %s%n", tagValue("Event", game));
        System.out.format("Site: %s%n", tagValue("Site", game));
        System.out.format("Date: %s%n", tagValue("Date", game));
        System.out.format("Round: %s%n", tagValue("Round", game));
        System.out.format("White: %s%n", tagValue("White", game));
        System.out.format("Black: %s%n", tagValue("Black", game));
        System.out.format("Result: %s%n", tagValue("Result", game));
        System.out.println("Final Position:");
        System.out.println(finalPosition(game));

    }


}
