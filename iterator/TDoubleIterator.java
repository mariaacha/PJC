package PJC.iterator;


public interface TDoubleIterator extends TIterator {
    /**
     * Advances the iterator to the next element in the underlying collection
     * and returns it.
     *
     * @return the next double in the collection
     * @exception NoSuchElementException if the iterator is already exhausted
     */
    public double next();
}