public class Main {

    public static void main(String[] args) {
        Piece king = new Pawn(Color.BLACK);
        System.out.println(king.algebraicName().equals("p"));
        System.out.println(king.fenName().equals("p"));
        Square[] attackedSquares = king.movesFrom(new Square('a', '2'));
        printArray(attackedSquares);
        System.out.println("/n/n/n/n/n");
        Square square1 =  new Square('a', '1');
        Square square2 =  new Square('a', '1');
        Square square3 =  new Square('a', '2');
        Square square4 =  new Square('b', '1');
        Square square5 =  new Square('c', '7');

        System.out.println(square1.equals(square2));
        System.out.println(square1.equals(square3));
        System.out.println(square1.equals(square4));
        System.out.println(square1.equals(square5));
        System.out.println(square1.equals(square1));
        System.out.println(square1.toString());

    }

    public static void printArray(Square[] squares) {
        for (int i = 0; i < squares.length; i++) {
            System.out.print(squares[i] + "  ");
        }
    }
}
