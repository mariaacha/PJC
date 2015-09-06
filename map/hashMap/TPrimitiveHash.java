package PJC.map.hashMap;

import PJC.map.hashMap.HashFunctions;
abstract public class TPrimitiveHash extends THash {
	static final long serialVersionUID = 1L;

    /**
     * flags indicating whether each position in the hash is
     * FREE, FULL, or REMOVED
     */
    public transient byte[] _states;

    /* constants used for state flags */

    /** flag indicating that a slot in the hashtable is available */
    public static final byte FREE = 0;

    /** flag indicating that a slot in the hashtable is occupied */
    public static final byte FULL = 1;

    /**
     * flag indicating that the value of a slot in the hashtable
     * was deleted
     */
    public static final byte REMOVED = 2;
    
    /** flag indicating that the slot is reserved to make a put there */
    public static final byte RESERVED = 3;
    


    /**
     * Creates a new <code>THash</code> instance with the default
     * capacity and load factor.
     */
    public TPrimitiveHash() {
        super();
    }


    /**
     * Creates a new <code>TPrimitiveHash</code> instance with a prime
     * capacity at or near the specified capacity and with the default
     * load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TPrimitiveHash( int initialCapacity ) {
        this( initialCapacity, DEFAULT_LOAD_FACTOR );
    }


    /**
     * Creates a new <code>TPrimitiveHash</code> instance with a prime
     * capacity at or near the minimum needed to hold
     * <tt>initialCapacity<tt> elements with load factor
     * <tt>loadFactor</tt> without triggering a rehash.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor      a <code>float</code> value
     */
    public TPrimitiveHash( int initialCapacity, float loadFactor ) {
        super();
		initialCapacity = Math.max( 1, initialCapacity );
        _loadFactor = loadFactor;
        setUp( HashFunctions.fastCeil( initialCapacity / loadFactor ) );
    }


    /**
     * Returns the capacity of the hash table.  This is the true
     * physical capacity, without adjusting for the load factor.
     *
     * @return the physical capacity of the hash table.
     */
    public int capacity() {
        return _states.length;
    }


    /**
     * Delete the record at <tt>index</tt>.
     *
     * @param index an <code>int</code> value
     */
    protected void removeAt( int index ) {
        _states[index] = REMOVED;
        super.removeAt( index );
    }


    /**
     * initializes the hashtable to a prime capacity which is at least
     * <tt>initialCapacity + 1</tt>.
     *
     * @param initialCapacity an <code>int</code> value
     * @return the actual capacity chosen
     */
    protected int setUp( int initialCapacity ) {
        int capacity;

        capacity = super.setUp( initialCapacity );
        _states = new byte[capacity];
        return capacity;
    }
} // TPrimitiveHash