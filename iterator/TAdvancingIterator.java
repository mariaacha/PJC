package PJC.iterator;


/**
 * Common interface for iterators that operate via the "advance" method for moving the
 * cursor to the next element.
 */
public interface TAdvancingIterator extends TIterator {
    /**
     * Moves the iterator forward to the next entry.
     *
     * @throws java.util.NoSuchElementException if the iterator is already exhausted
     */
    public void advance();
}