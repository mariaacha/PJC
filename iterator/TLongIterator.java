package PJC.iterator;


public interface TLongIterator extends TIterator {
    /**
     * Advances the iterator to the next element in the underlying collection
     * and returns it.
     *
     * @return the next long in the collection
     * @exception NoSuchElementException if the iterator is already exhausted
     */
    public long next();
}