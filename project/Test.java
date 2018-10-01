public class Test {


    public static void main(String[] args) {

      char[][] chessBoard = {{'R', 'N', 'B', 'K', 'Q', 'B', 'N', 'R' },
                                   {'P', 'P', 'P', 'P', 'P', 'P', 'P', 'P' },
                                   {'▢', '▢', '▢', '▢', '▢', '▢', '▢', '▢' },
                                   {'▢', '▢', '▢', '▢', '▢', '▢', '▢', '▢' },
                                   {'▢', '▢', '▢', '▢', '▢', '▢', '▢', '▢' },
                                   {'▢', '▢', '▢', '▢', '▢', '▢', '▢', '▢' },
                                   {'p', 'p', 'p', 'p', 'p', 'p', 'p', 'p' },
                                   {'r', 'n', 'b', 'k', 'q', 'b', 'n', 'r' } };


                                   int counter = 0;
                                   String chessLayout = "";
                                   for (int row = 7; row > -1; row--) {
                                       for (int col = 7; col > -1; col--) {

                                           if (!(chessBoard[row][col] == '▢')) {

                                               chessLayout += chessBoard[row][col];
                                               if (counter != 0) {
                                                   chessLayout += counter;
                                                   counter = 0;
                                               }
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
                                   System.out.println(chessLayout);
    }
}
