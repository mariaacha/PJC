package PJC.iterator;


public interface TShortIterator extends TIterator {
    /**
     * Advances the iterator to the next element in the underlying collection
     * and returns it.
     *
     * @return the next short in the collection
     * @exception NoSuchElementException if the iterator is already exhausted
     */
    public short next();
}