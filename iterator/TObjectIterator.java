package PJC.iterator;


public interface TObjectIterator extends TIterator {
    /**
     * Advances the iterator to the next element in the underlying collection
     * and returns it.
     *
     * @return the next Object in the collection
     * @exception NoSuchElementException if the iterator is already exhausted
     */
    public Object next();
}