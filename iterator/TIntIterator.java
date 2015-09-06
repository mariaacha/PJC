package PJC.iterator;

public interface TIntIterator extends TIterator {
    /**
     * Advances the iterator to the next element in the underlying collection
     * and returns it.
     *
     * @return the next int in the collection
     * @exception NoSuchElementException if the iterator is already exhausted
     */
    public int next();
}
