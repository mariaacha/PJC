package PJC.iterator;


public interface TByteIterator extends TIterator {
    /**
     * Advances the iterator to the next element in the underlying collection
     * and returns it.
     *
     * @return the next byte in the collection
     * @exception NoSuchElementException if the iterator is already exhausted
     */
    public byte next();
}