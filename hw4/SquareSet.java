import java.util.Set;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Represents a Set of Squares
 *
 * @author atirath6
 * @version 1.0
 */
public class SquareSet implements Set<Square> {

    private Square[] squareArray;

    /**
     * This is an empty constructor, initializes squareArray with 0 elements
     */
    public SquareSet() {
        squareArray = new Square[0];
    }

    /**
     * This is constructs a SquareSet that has the elements of the given
     * collection
     * @param collection We want to include elements from this collection
     */
    public SquareSet(Collection<? extends Square> collection) {
        squareArray = new Square[collection.size()];
        for (Square square: collection) {
            this.add(square);
        }
    }

    private class SquareSetIterator implements Iterator<Square> {
        private int cursor = 0;

        public boolean hasNext() {
            return cursor < squareArray.length;
        }

        public Square next() throws NoSuchElementException {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            return squareArray[cursor++]; //need to check why ++ here
            //really have no idea what's going on here
        }

        public void remove() {
            SquareSet.this.remove(cursor - 1); //also confused about this
        }
    }

    /**
     * attempts to add the given square to the set
     * @param square we are trying to add this Square to the Set
     * @return returns true if it Square is successfully added and
     * false if it already exists in the set or is not a Square
     * @throws InvalidSquareException thrown if the given Square is invalid
     * @throws NullPointerException thrown if the parameter is null
     */
    public boolean add(Square square)
                throws InvalidSquareException, NullPointerException {

        if (square == null) {
            throw new NullPointerException();
        }
        char fileTemp = square.getFile();
        char rankTemp = square.getRank();
        if (fileTemp < 97 || fileTemp > 104 || rankTemp < 49
                    || rankTemp > 56) {
            String name = ("" + square.getFile() + square.getRank());
            throw new InvalidSquareException(name);
        }
        if (this.contains(square)) {
            return false;
        } else {
            Square[] tempArray = makeArrayBigger();
            for (int i = 0; i < tempArray.length; i++) {
                if (tempArray[i] == null) {
                    tempArray[i] = square;
                    adjustSquareArray(tempArray);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * attempts to add all Squares in the given Collection to the set
     * @param c we are trying to add the Squares in the c to the Set
     * @return returns true if the set has been modified and false otherwise
     * @throws InvalidSquareException thrown if the a given Square is invalid
     * @throws NullPointerException thrown if a space in c is null
     */
    public boolean addAll(Collection<? extends Square> c)
                throws InvalidSquareException, NullPointerException {
        for (Square square: c) {
            if (square == null) {
                throw new NullPointerException();
            }
            char fileTemp = square.getFile();
            char rankTemp = square.getRank();
            if (fileTemp < 97 || fileTemp > 104 || rankTemp < 49
                        || rankTemp > 56) {
                String name = ("" + square.getFile() + square.getRank());
                throw new InvalidSquareException(name);
            }
        }
        Square[] sameChecker = new Square[squareArray.length];
        for (int i = 0; i < this.squareArray.length; i++) {
            sameChecker[i] = this.squareArray[i];
        }
        boolean uselessBoolean = false;
        boolean usefulBoolean = false;

        for (Square square: c) {
            if (!usefulBoolean) {
                usefulBoolean = this.add(square);
            } else {
                uselessBoolean = this.add(square);
            }
        }

        return usefulBoolean;
    }

    /**
     * Checks to see if a certain object is contained in this set
     * @param o we are checking this object
     * @return returns true if the set has this element and false otherwise
     */
    public boolean contains(Object o) {
        if (!(o instanceof Square)) {
            return false;
        }
        int counter = 0;
        for (Square square: squareArray) {
            if (square == null) {
                counter++;
            }
        }
        counter = squareArray.length - counter;
        Square that = (Square) o;
        for (int i = 0; i < counter; i++) {
            if (squareArray[i].equals(that)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if all objects in a Collcetion are contained in this set
     * @param c we are checking this Collection
     * @return returns true if the set has all the element and false otherwise
     */
    @Override
    public boolean containsAll(Collection<?> c) {

        for (Object object: c) {
            if (!(this.contains(object))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether two Sets have all the same elements
     * @param  o the Set we are comparing this Set to
     * @return returns true if the Sets are equal and false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Set)) {
            return false;
        }
        Set that = (Set) o;
        if (this.squareArray.length != that.size()) {
            return false;
        }
        if (!this.containsAll(that)) {
            return false;
        }
        return true;
    }

    /**
     * Checks if there are any Squares in squareArray
     * @return true if there is at least one Square and false otherwise
     */
    public boolean isEmpty() {
        if (squareArray.length != 0) {
            return false;
        }
        return true;
    }

    /**
     * Removes a Square from a Collection
     * @param o the object we are removing
     * @return returns false if the object was not in the set and true otherwise
     */
    public boolean remove(Object o) {
        if (!(o instanceof Square)) {
            return false;
        }
        Square removing = (Square) o;
        if (this.contains(removing)) {
            Square[] shorterArray = new Square[squareArray.length - 1];
            int counter = 0;
            for (int i = 0; i < this.squareArray.length; i++) {
                if (!this.squareArray[i].equals(removing)) {
                    shorterArray[counter] = this.squareArray[i];
                    counter++;
                }
            }
            adjustSquareArray(shorterArray);
            return true;
        }
        return false;
    }

    /**
     * inapplicable
     * @param c inapplicable
     * @return no return
     * @throws UnsupportedOperationException This operation is not supported
     */
    public boolean removeAll(Collection<?> c)
                throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * inapplicable
     * @param c inapplicable
     * @return no return
     * @throws UnsupportedOperationException This operation is not supported
     */
    public boolean retainAll(Collection<?> c)
                throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * inapplicable
     * @throws UnsupportedOperationException This operation is not supported
     */
    public void clear() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * returns the hashCode for the set
     * @return the actual integer value of the hashCode
     */
    public int hashCode() {
        if (this.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (Square square: squareArray) {
            result += square.hashCode();
        }
        return result;
    }

    /**
     * returns an Iterator of the Square Set
     * @return the iterator itself
     */
    public Iterator<Square> iterator() {
        Iterator<Square> setIterator = new SquareSetIterator();
        return setIterator;
    }

    /**
     * Returns the amount of elements in the Set
     * @return the size itself
     */
    public int size() {
        return this.squareArray.length;
    }

    /**
     * returns an array of all the elements in the Set
     * @return the array itself
     */
    public Object[] toArray() {
        return this.squareArray;
    }

    /**
     * inserts all the elements of the Set into an array of type T
     * @param a the array that that we must modify
     * @param <T> specifies the type of the array
     * @return the array of type T that has been modified
     */

    public <T> T[] toArray(T[] a) {

        if (a == null) {
            throw new NullPointerException();
        }
        if (a.getClass().isAssignableFrom(squareArray.getClass())) {
            if (a.length <= this.squareArray.length) {
                return (T[]) toArray();
            } else {
                for (int i = 0; i < squareArray.length; i++) {
                    a[i] = (T) squareArray[i];
                }
                a[squareArray.length] = null;
                return a;
            }
        } else {
            throw new ArrayStoreException();
        }
    }

    private Square[] makeArrayBigger() {
        Square[] temp = new Square[squareArray.length + 1];
        for (int i = 0; i < this.squareArray.length; i++) {
            temp[i] = this.squareArray[i];
        }
        return temp;
    }

    private void adjustSquareArray(Square[] temp) {
        int counter = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != null) {
                counter++;
            }
        }
        squareArray = new Square[counter];
        int counter1 = 0;
        for (int i = 0; i < temp.length; i++) {
            if (temp[i] != null) {
                squareArray[counter1] = temp[i];
                counter1++;
            }
        }
    }
}
