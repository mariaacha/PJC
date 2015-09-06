package PJC.iterator;


public interface TCharIterator extends TIterator {
    /**
     * Advances the iterator to the next element in the underlying collection
     * and returns it.
     *
     * @return the next char in the collection
     * @exception NoSuchElementException if the iterator is already exhausted
     */
    public char next();
}