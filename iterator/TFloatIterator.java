package PJC.iterator;


public interface TFloatIterator extends TIterator {
    /**
     * Advances the iterator to the next element in the underlying collection
     * and returns it.
     *
     * @return the next float in the collection
     * @exception NoSuchElementException if the iterator is already exhausted
     */
    public float next();
}