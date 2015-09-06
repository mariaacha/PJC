package PJC.map.hashMap;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import PJC.iterator.TPrimitiveIterator;

public abstract class THashPrimitiveIterator implements TPrimitiveIterator {

    /** the data structure this iterator traverses */
    protected final TPrimitiveHash _hash;
    /**
     * the number of elements this iterator believes are in the
     * data structure it accesses.
     */
    protected int _expectedSize;
    /** the index used for iteration. */
    protected int _index;


    /**
     * Creates a <tt>TPrimitiveIterator</tt> for the specified collection.
     *
     * @param hash the <tt>TPrimitiveHash</tt> we want to iterate over.
     */
    public THashPrimitiveIterator( TPrimitiveHash hash ) {
        _hash = hash;
        _expectedSize = _hash.size();
        _index = _hash.capacity();
    }


    /**
     * Returns the index of the next value in the data structure
     * or a negative value if the iterator is exhausted.
     *
     * @return an <code>int</code> value
     * @throws java.util.ConcurrentModificationException
     *          if the underlying collection's
     *          size has been modified since the iterator was created.
     */
    protected final int nextIndex() {
        if ( _expectedSize != _hash.size() ) {
            throw new ConcurrentModificationException();
        }

        byte[] states = _hash._states;
        int i = _index;
        while ( i-- > 0 && ( states[i] != TPrimitiveHash.FULL ) ) {
            ;
        }
        return i;
    }


    /**
     * Returns true if the iterator can be advanced past its current
     * location.
     *
     * @return a <code>boolean</code> value
     */
    public boolean hasNext() {
        return nextIndex() >= 0;
    }


    /**
     * Removes the last entry returned by the iterator.
     * Invoking this method more than once for a single entry
     * will leave the underlying data structure in a confused
     * state.
     */
    public void remove() {
        if (_expectedSize != _hash.size()) {
            throw new ConcurrentModificationException();
        }

        // Disable auto compaction during the remove. This is a workaround for bug 1642768.
        try {
            _hash.tempDisableAutoCompaction();
            _hash.removeAt(_index);
        }
        finally {
            _hash.reenableAutoCompaction( false );
        }

        _expectedSize--;
    }


    /**
     * Sets the internal <tt>index</tt> so that the `next' object
     * can be returned.
     */
    protected final void moveToNextIndex() {
        // doing the assignment && < 0 in one line shaves
        // 3 opcodes...
        if ( ( _index = nextIndex() ) < 0 ) {
            throw new NoSuchElementException();
        }
    }


} // TPrimitiveIterator