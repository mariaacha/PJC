package PJC.iterator;

public interface TIterator {
    /**
     * Returns true if the iterator can be advanced past its current location.
     *
     * @return a <code>boolean</code> value
     */
    public boolean hasNext();

    /**
     * Removes the last entry returned by the iterator. The result of invoking this method
     * more than once for a single entry is undefined and can leave the underlying data
     * structure in a confused state.
     */
    public void remove();
}