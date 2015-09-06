package PJC.hash;

import PJC.map.hashMap.HashFunctions;
import PJC.map.hashMap.TPrimitiveHash;

abstract public class TLongFloatHash extends TPrimitiveHash {
	static final long serialVersionUID = 1L;

    /** the set of #k#s */
    public transient long[] _set;


    /**
     * key that represents null
     *
     * NOTE: should not be modified after the Hash is created, but is
     *       not final because of Externalization
     *
     */
    protected long no_entry_key;


    /**
     * value that represents null
     *
     * NOTE: should not be modified after the Hash is created, but is
     *       not final because of Externalization
     *
     */
    protected float no_entry_value;

    protected boolean consumeFreeSlot;
   
    
    
    /**object used to synchronize the methods used in put*/
    //private Object objectPut = new Object();

    /**
     * Creates a new <code>TIntHash</code> instance with the default
     * capacity and load factor.
     */
    public TLongFloatHash() {
        super();
        no_entry_key = (long) 0;
        no_entry_value = (float) 0;
    }


    /**
     * Creates a new <code>TIntHash</code> instance whose capacity
     * is the next highest prime above <tt>initialCapacity + 1</tt>
     * unless that value is already prime.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TLongFloatHash( int initialCapacity ) {
        super( initialCapacity );
        no_entry_key = (long) 0;
        no_entry_value = (float) 0;
    }


    /**
     * Creates a new <code>TIntIntHash</code> instance with a prime
     * value at or near the specified capacity and load factor.
     *
     * @param initialCapacity used to find a prime capacity for the table.
     * @param loadFactor used to calculate the threshold over which
     * rehashing takes place.
     */
    public TLongFloatHash (int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
        no_entry_key = (long) 0;
        no_entry_value = (float) 0;
    }


    /**
     * Creates a new <code>TIntIntHash</code> instance with a prime
     * value at or near the specified capacity and load factor.
     *
     * @param initialCapacity used to find a prime capacity for the table.
     * @param loadFactor used to calculate the threshold over which
     * rehashing takes place.
     * @param no_entry_value value that represents null
     */
    public TLongFloatHash( int initialCapacity, float loadFactor,
        long no_entry_key, float no_entry_value ) {
        super(initialCapacity, loadFactor);
        this.no_entry_key = no_entry_key;
        this.no_entry_value = no_entry_value;
    }
    

    /**
     * Returns true if the map contains a mapping for the specified key
     * @param key the key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    public boolean contains (long key) {
        return index(key) >= 0;
    }

    /**
     * Returns the value that is used to represent null as a key. The default
     * value is generally zero, but can be changed during construction
     * of the collection.
     *
     * @return the key that represents null
     */
    public long getNoEntryKey() {
        return no_entry_key;
    }


    /**
     * Returns the value that is used to represent null. The default
     * value is generally zero, but can be changed during construction
     * of the collection.
     *
     * @return the value that represents null
     */
    public float getNoEntryValue() {
        return no_entry_value;
    }

    /**
     * Releases the element currently stored at <tt>index</tt>.
     *
     * @param index an <code>int</code> value
     */
    protected void removeAt (int index) {
        _set[index] = no_entry_key;
        super.removeAt (index);
    }
    
    /**
     * initializes the hashtable to a prime capacity which is at least
     * <tt>initialCapacity + 1</tt>.
     *
     * @param initialCapacity an <code>int</code> value
     * @return the actual capacity chosen
     */
    protected int setUp (int initialCapacity) {
        int capacity;

        capacity = super.setUp(initialCapacity);
        _set = new long[capacity];
        return capacity;
    }

    
    /**
     * Locates the index of <tt>value</tt>.
     *
     * @param key an <code>byte</code> value
     * @return the index of <tt>value</tt> or -1 if it isn't in the set.
     */
    protected int index (long key) {
        int hash, index, length;

        final byte[] states = _states;
        final long[] set = _set;
        length = states.length;
        hash = HashFunctions.hash(key) & 0x7fffffff;
        index = hash % length;
        byte state = states[index];

        if (state == FREE)
            return -1;

        if (state == FULL && set[index] == key)
            return index;

        return indexRehashed (key, index, hash, state);
    }

    int indexRehashed (long key, int index, int hash, byte state) {
        int length = _set.length;
        int probe = 1 + (hash % (length - 2));
        final int loopIndex = index;

        do {
            index -= probe;
            if (index < 0) {
                index += length;
            }
            state = _states[index];
            if (state == FREE)
                return -1;

            if (key == _set[index] && state != REMOVED)
                return index;
        } while (index != loopIndex);

        return -1;
    }


    /**
     * Locates the index at which <tt>value</tt> can be inserted.  if
     * there is already a value equal()ing <tt>value</tt> in the set,
     * returns that value as a negative integer.
     *
     * @param key an <code>byte</code> value
     * @return an <code>float</code> value
     */
         protected int insertKeyPar (long key) {
             int hash, index = 0;

             hash = HashFunctions.hash(key) & 0x7fffffff;
             index = hash % _states.length;
             byte state = _states[index];

             consumeFreeSlot = false;

             synchronized ((Object)this._states[index])	{
             //synchronized ((Object) (_set[index]))	{
            	 if (state == FREE) {
            		 consumeFreeSlot = true;
            		 insertKeyAt(index, key);

            		 return index;
            	 }
             }

             if (state == FULL && _set[index] == key) {
                 return -index - 1;
             }

             return insertKeyRehash(key, index, hash, state);
         }
         
         protected int insertKeyParAll (long key) {
             int hash, index = 0;

             hash = HashFunctions.hash(key) & 0x7fffffff;
             index = hash % _states.length;
             byte state = _states[index];

             //consumeFreeSlot = false;

             /**synchronized ((Object)this._states[index])	{
             //synchronized ((Object) (_set[index]))	{
            	 if (state == FREE) {
            		 //consumeFreeSlot = true;
            		 insertKeyAt(index, val);
            		 //System.out.println ("Posición " + index + " : " + val + " " + _set[index]);

            		 return index;       // empty, all done
            	 }
             }*/
             boolean empty = false;
             //synchronized ((Object)this._states[index])	{
            	 if (state == FREE)	{
            		 empty = true;
            		 _states[index] = FULL;
            	 }
             //}
             if (empty)	{
            	 _set[index] = key;
            	 return index;
             }
             
             if (state == FULL && _set[index] == key) {
                 return -index - 1;
             }

             return insertKeyRehash(key, index, hash, state);
         }
         
         protected int insertKey (long key) {
             int hash, index;

             hash = HashFunctions.hash(key) & 0x7fffffff;
             index = hash % _states.length;
             byte state = _states[index];

             consumeFreeSlot = false;

             if (state == FREE) {
                 consumeFreeSlot = true;
                 insertKeyAt(index, key);

                 return index;
             }

             if (state == FULL && _set[index] == key) {
                 return -index - 1;
             }

             return insertKeyRehash2(key, index, hash, state);
         }

         int insertKeyRehash(long key, int index, int hash, byte state) {
             final int length = _set.length;
             int probe = 1 + (hash % (length - 2));
             final int loopIndex = index;
             int firstRemoved = -1;

             /**
              * Look until FREE slot or we start to loop
              */
             do {
                 // Identify first removed slot
                 synchronized ((Object)this._states[index])	{
                	 if (state == REMOVED && (!this.contains(key)))	{
                		 insertKeyAt (index, key);
                		 return index;
                	 }
                 }
                 index -= probe;
                 if (index < 0) {
                     index += length;
                 }
                 state = _states[index];

                 // A FREE slot stops the search
                 synchronized ((Object)this._states[index])	{
                	 if (state == FREE)	{
                		 consumeFreeSlot = true;
                		 insertKeyAt (index,key);
                		 return index;
                	 }
                 }
                 /**if ((state == FREE) && (firstRemoved != -1))	{
                	 insertKeyAt (firstRemoved, val);
                	 return firstRemoved;
                 }*/
/**                	 if (state == FREE) {
                		 if (firstRemoved != -1) {
                			 insertKeyAt(firstRemoved, val);
                			 return firstRemoved;
                		 } else {
                			 consumeFreeSlot = true;
                			 insertKeyAt(index, val);
                			 return index;
                		 }
                     }*/

                 if (state == FULL && _set[index] == key) {
                	 //unreserves the position of the first removed
                	 _states[firstRemoved] = REMOVED;
                     return -index - 1;
                 }

                 // Detect loop
             } while (index != loopIndex);

             // We inspected all reachable slots and did not find a FREE one
             // If we found a REMOVED slot we return the first one found
             if (firstRemoved != -1) {
                 insertKeyAt(firstRemoved, key);
                 return firstRemoved;
             }

             // Can a resizing strategy be found that resizes the set?
             throw new IllegalStateException("No free or removed slots available. Key set full?!!");
         }
         int insertKeyRehash2(long key, int index, int hash, byte state) {
             // compute the long hash
             final int length = _set.length;
             int probe = 1 + (hash % (length - 2));
             final int loopIndex = index;
             int firstRemoved = -1;

             /**
              * Look until FREE slot or we start to loop
              */
             do {
                 // Identify first removed slot
                 if (state == REMOVED && firstRemoved == -1)
                     firstRemoved = index;

                 index -= probe;
                 if (index < 0) {
                     index += length;
                 }
                 state = _states[index];

                 // A FREE slot stops the search
                 if (state == FREE) {
                     if (firstRemoved != -1) {
                         insertKeyAt(firstRemoved, key);
                         return firstRemoved;
                     } else {
                         consumeFreeSlot = true;
                         insertKeyAt(index, key);
                         return index;
                     }
                 }

                 if (state == FULL && _set[index] == key) {
                     return -index - 1;
                 }

                 // Detect loop
             } while (index != loopIndex);

             // We inspected all reachable slots and did not find a FREE one
             // If we found a REMOVED slot we return the first one found
             if (firstRemoved != -1) {
                 insertKeyAt(firstRemoved, key);
                 return firstRemoved;
             }

             // Can a resizing strategy be found that resizes the set?
             throw new IllegalStateException("No free or removed slots available. Key set full?!!");
         }

         void insertKeyAt(int index, long key) {
             _set[index] = key;  // insert value
             _states[index] = FULL;
         }

    protected int XinsertKey (long key ) {
        int hash, probe, index, length;

        final byte[] states = _states;
        final long[] set = _set;
        length = states.length;
        hash = HashFunctions.hash( key ) & 0x7fffffff;
        index = hash % length;
        byte state = states[index];

        consumeFreeSlot = false;

        if ( state == FREE ) {
            consumeFreeSlot = true;
            set[index] = key;
            states[index] = FULL;

            return index;
        } 
        else if ( state == FULL && set[index] == key ) {
            return -index -1;
        } 
        else {
            probe = 1 + ( hash % ( length - 2 ) );


            if ( state != REMOVED ) {
				do {
					index -= probe;
					if (index < 0) {
						index += length;
					}
					state = states[index];
				} while ( state == FULL && set[index] != key );
            }

            if ( state == REMOVED) {
                int firstRemoved = index;
                while ( state != FREE && ( state == REMOVED || set[index] != key ) ) {
                    index -= probe;
                    if (index < 0) {
                        index += length;
                    }
                    state = states[index];
                }

                if (state == FULL) {
                    return -index -1;
                } else {
                    set[index] = key;
                    states[index] = FULL;

                    return firstRemoved;
                }
            }
            if (state == FULL) {
                return -index -1;
            } else {
                consumeFreeSlot = true;
                set[index] = key;
                states[index] = FULL;

                return index;
            }
        }
    }


    /** {@inheritDoc} */
    /**public void writeExternal( ObjectOutput out ) throws IOException {
        // VERSION
    	out.writeByte( 0 );

        // SUPER
    	super.writeExternal( out );

    	// NO_ENTRY_KEY
    	out.writeInt( no_entry_key );

    	// NO_ENTRY_VALUE
    	out.writeInt( no_entry_value );
    }*/


    /** {@inheritDoc} */
    /**public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        // VERSION
    	in.readByte();

        // SUPER
    	super.readExternal( in );

    	// NO_ENTRY_KEY
    	no_entry_key = in.readInt();

    	// NO_ENTRY_VALUE
    	no_entry_value = in.readInt();
    }*/
} // TByteFloatHash
