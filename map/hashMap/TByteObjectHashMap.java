package PJC.map.hashMap;

//import paralel.collection.TObjectCollection;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.BiFunction;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.concurrent.atomic.*;

import PJC.collection.TByteCollection;
import PJC.collection.TObjectCollection;
import PJC.hash.TByteObjectHash;
import PJC.iterator.TByteIterator;
import PJC.iterator.TObjectIterator;
import PJC.list.ArraysPar;
import PJC.map.TByteObjectMap;
import PJC.map.hashMap.HashFunctions;
import PJC.map.iterator.TByteObjectIterator;
import PJC.set.TByteSet;
import PJC.spliterator.TByteSpliterator;
import PJC.spliterator.TObjectSpliterator;

public class TByteObjectHashMap extends TByteObjectHash implements TByteObjectMap, Serializable, Cloneable {
    static final long serialVersionUID = 1L;

    /** the values of the map */
    protected transient Object[] _values;
    
    /**boolean used for the threads to share a boolean result*/
    private boolean boolGlobal;
    
    /** int used for the threads to share a boolean result*/
    private int intGlobal;
    
    /**array of ints used for the threads to share a int[] result*/
    private byte[] bytesGlobal;
    private Object[] ObjectsGlobal;
    private int[] intsGlobal2;

    /**array with the threads*/
    private Thread[] threads;
    
    //stringBuilder used for the processors to share a string result
    
    private StringBuilder sbGlobal = new StringBuilder();
    
    private AtomicInteger atIntGlobal = new AtomicInteger();
    
    //private Object objectSync = new Object();
    
    /**
     * 
     * @param i index of the processor
     * @param numElem number of elements to each processor
     * @param offset first position
     * @return
     */
    public int limiteMin (int i, int numElem, int offset)	{
		return i*numElem+offset;
	}

    /**
     * 
     * @param i index of the processor
     * @param numElem number of elements to each processor
     * @param offset first position
     * @param numProcs total number of processors
     * @param numMax length of the data
     * @return
     */
	public int limiteMax (int i, int numElem, int offset, int numProcs, int numMax)	{
		if ((i+1)==numProcs) return numMax;
		return (i+1)*numElem+offset;
	}

    /**
     * Creates a new <code>TByteObjectHashMap</code> instance with the default
     * capacity and load factor.
     */
    public TByteObjectHashMap() {
        super();
    }


    /**
     * Creates a new <code>TByteObjectHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TByteObjectHashMap (int initialCapacity) {
        super(initialCapacity);
    }


    /**
     * Creates a new <code>TByteObjectHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>Object</code> value
     */
    public TByteObjectHashMap (int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }


    /**
     * Creates a new <code>TByteObjectHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>Object</code> value
     * @param noEntryKey a <code>byte</code> value that represents
     *                   <tt>null</tt> for the Key set.
     * @param noEntryValue a <code>Object</code> value that represents
     *                   <tt>null</tt> for the Value set.
     */
    public TByteObjectHashMap (int initialCapacity, float loadFactor,
        byte noEntryKey, Object noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }


    /**
     * Creates a new <code>TByteObjectHashMap</code> instance containing
     * all of the entries in the map passed in.
     *
     * @param keys a <tt>byte</tt> array containing the keys for the matching values.
     * @param values a <tt>Object</tt> array containing the values.
     */
    public TByteObjectHashMap (byte[] keys, Object[] values) {
        super (Math.max(keys.length, values.length));

        int size = Math.min(keys.length, values.length);
        for (int i = 0; i < size; i++) {
            this.put(keys[i], values[i]);
        }
    }
    /**
     * 
     * @param keys keys to be included in the new map
     * @param values values to be included in the new map
     * @param numProcs number of processors that will be used
     */
    public TByteObjectHashMap (byte[] keys, Object[] values, int numProcs) {
        super(Math.max(keys.length, values.length));

        //int size = Math.min( keys.length, values.length );
        /**for ( int i = 0; i < size; i++ ) {
            this.put( keys[i], values[i] );
        }*/
        this.putAllPar(keys, values, numProcs);
    }

    /**
     * Creates a new <code>TByteObjectHashMap</code> instance containing
     * all of the entries in the map passed in.
     *
     * @param map a <tt>TByteObjectMap</tt> that will be duplicated.
     */
    public TByteObjectHashMap (TByteObjectMap map) {
        super(map.size());
        if (map instanceof TByteObjectHashMap) {
            TByteObjectHashMap hashmap = (TByteObjectHashMap) map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != (byte) 0) {
                Arrays.fill(_set, this.no_entry_key);
            }
            if (this.no_entry_value != (Object) 0) {
                Arrays.fill(_values, this.no_entry_value);
            }
            setUp((int)Math.ceil(DEFAULT_CAPACITY/_loadFactor));
        }
        putAll(map);
    }
    /**
     * 
     * @param map the map to copy
     * @param numProcs number of processors that will be used
     */
    public TByteObjectHashMap(TByteObjectMap map, int numProcs) {
        super(map.size());
        if (map instanceof TByteObjectHashMap) {
            TByteObjectHashMap hashmap = (TByteObjectHashMap) map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != (byte)0) {
            	ArraysPar.fillPar(_set, this.no_entry_key, numProcs);
            }
            if (this.no_entry_value != (Object)0) {
                //Arrays.fill( _values, this.no_entry_value );
            	ArraysPar.fillPar(_values, this.no_entry_value, numProcs);
            }
            setUp((int)Math.ceil(DEFAULT_CAPACITY/_loadFactor));
        }
        //putAll( map );
        putAllPar (map, numProcs);
    }
    
    /** {@inheritDoc} */
    public void clear() {
        super.clear();
        Arrays.fill(_set, 0, _set.length, no_entry_key);
        Arrays.fill(_values, 0, _values.length, no_entry_value);
        Arrays.fill(_states, 0, _states.length, FREE);
    }
    
    /**
     * Removes all of the mappings from this map. The map will be empty after this
     * call returns.
     * @param numProcs number of threads that will be used
     */
    public void clearPar (int numProcs)	{
    	super.clear();
    	ArraysPar.fillPar(_set, 0, _set.length, no_entry_key, numProcs);
    	ArraysPar.fillPar(_values, 0, _values.length, no_entry_value, numProcs);
    	ArraysPar.fillPar(_states, 0, _states.length, FREE, numProcs);
    }
    
    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and values
     * themselves are not cloned.
     * @return a shallow copy of this map
     */
    public TByteObjectHashMap clone() {
        TByteObjectHashMap result;
        try {
            result = (TByteObjectHashMap)super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
        result.reinitialize();
        //result.putMapEntries(this, false);
        result.putAll(this);
        return result;
    }
    /**
     * Returns a shallow copy of this <tt>HashMap</tt> instance: the keys and values
     * themselves are not cloned.
     * @param numProcs number of threads that will be used
     * @return a shallow copy of this map
     */
    public TByteObjectHashMap clonePar(int numProcs) {
        TByteObjectHashMap result;
        try {
            result = (TByteObjectHashMap)super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
        //result.reinitialize();
        //result.putMapEntries(this, false);
        result.putAllPar(this, numProcs);
        return result;
    }
    
    /**
     * Attempts to compute a mapping for the specified key and its current mapped
     * value (or null if there is not current mapping).
     * @param key key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     */
    public Object compute (byte key,
    			BiFunction<? super Byte, ? super Object, ? extends Object> remappingFunction)	{
    	int index = index (key);
    	Object value = null;
    	if (index > 0)	value = remappingFunction.apply((Byte)key, (Object)_values[index]);
    	if (value != null)	{
    		put (key, value);
    		return value;
    	}
    	else if (index > 0)	removeAt (index);
        return no_entry_value;
    }
    
    /**
     * If the specified key is not already associated with a value (or is mapped to null),
     * attempts to compute its value using the given mapping function and enters it
     * into this map unless null.
     * If the function returns null no mapping is recorded. If the function itself throws
     * an (unchecked) exception, the exception is rethrown, and no mapping is recorded.
     * The most common usage is to construct a new object serving as an initial mapped
     * value or memorized result.
     * @param key key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with the specified key,
     * or null if the computed value is null
     */
    public Object comptueIfAbsent (byte key, Function<? super Byte,
    		? extends Object> remappingFunction)	{
    	int index = index(key);
    	Object value = remappingFunction.apply(key);
    	if (value == null)	{
    		return no_entry_value;
    	}
        if ((index < 0) || ((index >= 0) && (_values[index] == no_entry_value))) {
           put (key, value);
           return value;
        }
        return no_entry_value;
    }
    
    /**
     * If the value for the specified key is present and no-null, attempts to compute
     * a new mapping given the key and its current mapped value.
     * If the function returns null, the mapping is removed. If the function itself
     * throws an (unchecked) exception, the exception is rethrown, and the current
     * mapping is left unchanged.
     * @param key key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     */
    public Object computeIfPresent (byte key,
    				BiFunction<? super Byte, ? super Object, ? extends Object> remappingFunction)	{
    	if (remappingFunction == null)
    		throw new NullPointerException(); 
    	int index = index (key);
    	
    	if ((index> 0) && (_values[index] != no_entry_value))	{
    		Object value = remappingFunction.apply(key, _values[index]);
    		if (value == no_entry_value)	{
    			removeAt (index);
    		}    			
    		else	{
    			put (key, value);
    			return value;
    		}
    	}
    	return no_entry_value;
    }
    

    /**
     * Returns true if this map contains a mapping for the specified key.
     */
    public boolean containsKey (byte key) {
        return contains (key);
    }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     */
    public boolean containsValue (Object value) {
        byte[] states = _states;
        Object[] vals = _values;

        for (int i=0; i<_values.length; i++) {
            if (states[i] == FULL && value == vals[i]) {
                return true;
            }
        }
        return false;
    }
    
    private class ContainsValue1 implements Runnable	{
    	private int min, max;
    	private Object val;
    	ContainsValue1 (Object value, int a, int b)	{
    		val = value; min = a; max = b;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			if (boolGlobal) return;
    			if (_states[i]==FULL && val==_values[i])	{
    				boolGlobal = true;
    				return;
    			}
    		}
    	}
    }
    /**
     * Returns true if this map maps one or more keys to the specified value.
     * @param value value whose presence in this map is to be tested
     * @param numProcs number of threads that will be used
     * @return true if this map maps one or more keys to the specified value
     */
    public boolean containsValuePar (Object value, int numProcs){
    	int numElemProc = _values.length/numProcs;
    	boolGlobal = false;
    	int numMin, numMax;
        threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
    		threads[i] = new Thread (new ContainsValue1 (value, numMin, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return boolGlobal;
    }
    
    private class Equals1 implements Runnable	{
    	TByteObjectMap that;
    	int min, max;
    	Equals1 (TByteObjectMap obj, int a, int b)	{
    		that=obj; min=a; max=b;
    	}
    	public void run ()	{
            Object this_no_entry_value = getNoEntryValue();
            Object that_no_entry_value = that.getNoEntryValue();
    		for (int i=min; i<max; i++) {
                if (_states[i] == FULL) {
                    byte key = _set[i];
                    Object that_value = that.get(key);
                    Object this_value = _values[i];
                    if ((this_value != that_value) &&
                         (this_value != this_no_entry_value) &&
                         (that_value != that_no_entry_value)) {
                        boolGlobal = false;
                        return;
                    }
                }
            }
    	}
    }
    private class Equals2 implements Runnable	{
    	Map<?,?> that;
    	int min, max;
    	Equals2 (Map<?,?> obj, int a, int b)	{
    		that=obj; min=a; max=b;
    	}
    	public void run ()	{
            Object this_no_entry_value = getNoEntryValue();
            //int that_no_entry_value = that.getNoEntryValue();
    		for (int i=min; i<max; i++) {
                if (_states[i] == FULL) {
                    byte key = _set[i];
                    //int that_value = that.get( key );
                    Object that_value = that.get(key);
                    if (!(that_value instanceof Object))	{
                    	boolGlobal = false;
                    	return;
                    }
                    Object this_value = _values[i];
                    if ((this_value != (Integer)that_value) &&
                        (this_value != this_no_entry_value) &&
                        (that_value != null)) {
                    	boolGlobal = false;
                        return;
                    }
                }
            }
    	}
    }

    /** {@inheritDoc} */
    public boolean equalsPar (Object other, int numProcs)	{
    	if (!((other instanceof TByteObjectMap)||(other instanceof Map<?,?>)))	{
    		return false;
    	}
		boolGlobal = true;
    	if (other instanceof TByteObjectMap)	{
    		TByteObjectMap that = (TByteObjectMap) other;
    		if (that.size () != this.size())	{
    			return false;
    		}	
    		int numElemProc = (_values.length)/numProcs;
    		int numMin, numMax;
            threads = new Thread[numProcs];
    		for (int i=0; i<numProcs; i++)	{
    			numMin = limiteMin (i, numElemProc, 0);
    			numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
    			threads[i] = new Thread (new Equals1 (that, numMin, numMax));
    			threads[i].start();
    		}
    		for (int i=0; i<numProcs; i++)	{
    			try {
    				threads[i].join();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	}
    	if (other instanceof Map<?,?>)	{
    		System.out.println ("Comparando con map");
    		Map<?,?> that = (Map<?,?>) other;
    		if (that.size () != this.size())	{
    			System.out.println ("Tamaños distintos");
    			return false;
    		}	
    		int numElemProc = (_values.length)/numProcs;
    		int numMin, numMax;
            threads = new Thread[numProcs];
    		for (int i=0; i<numProcs; i++)	{
    			numMin = limiteMin (i, numElemProc, 0);
    			numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
    			threads[i] = new Thread (new Equals2 (that, numMin, numMax));
    			threads[i].start();
    		}
    		for (int i=0; i<numProcs; i++)
    			try {
    				threads[i].join();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
    		}
    	return boolGlobal;
    }
    @Override
    public boolean equals (Object other) {
        if (!(other instanceof TByteObjectMap)) {
            return false;
        }
        TByteObjectMap that = (TByteObjectMap) other;
        if (that.size() != this.size()) {
            return false;
        }
        Object[] values = _values;
        byte[] states = _states;
        Object this_no_entry_value = getNoEntryValue();
        Object that_no_entry_value = that.getNoEntryValue();
        for (int i = values.length; i-- > 0;) {
            if (states[i] == FULL) {
                byte key = _set[i];
                Object that_value = that.get(key);
                Object this_value = values[i];
                if ((this_value != that_value) &&
                    (this_value != this_no_entry_value) &&
                    (that_value != that_no_entry_value)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    /**
     * Performs the given action for each entry in this map until all entries 
 	 * been processed or the action throws an exception.
     */
    public void forEach(BiConsumer<? super Byte, ? super Object> action) {
        //Node<K,V>[] tab;
        if (action == null)
            throw new NullPointerException();
        if (_size > 0 && _values != null) {
            //int mc = modCount;
            for (int i = 0; i < _values.length; ++i) {
            	action.accept(_set[i], _values[i]);
                /**for (Node<K,V> e = tab[i]; e != null; e = e.next)
                    action.accept(e.key, e.value);*/
            	
            }
        }
    }
    private class ForEachPar1 implements Runnable	{
    	int min, max;
    	BiConsumer<? super Byte, ? super Object> action;
    	ForEachPar1 (int a, int b, BiConsumer<? super Byte, ? super Object> c)	{
    		min = a; max = b; action = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			if (_states[i] == FULL)
    				action.accept(_set[i], _values[i]);
    		}
    	}
    }
    /**
     * Performs the given action for each entry in this map until all entries have been
     * processed or the action throws an exception. The actions are not performed in order.
     * Exceptions thrown by the action are relayed to the caller.
     * @param action the action to be performed for each entry
     * @param numProcs number of threads that will be used
     */
    public void forEachPar (BiConsumer<? super Byte, ? super Object> action, int numProcs)	{
    	if (action == null)
    		throw new NullPointerException();
    	if (_size > 0 && _values != null) {
    		int numElemProc = _set.length/numProcs;
    		threads = new Thread[numProcs];
    		int numMin, numMax;
    		for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
    			threads[i] = new Thread (new ForEachPar1 (numMin, numMax, action));
    			threads[i].start();
    		}
    		for (int i=0; i<numProcs; i++)	{
    			try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    
    /**
     * Returns the value to which the specified key is mapped, or null if 
     * this map contains no mapping for the key.
     */
    public Object get (byte key) {
        int index = index(key);
        return index < 0 ? no_entry_value : _values[index];
    }
    
    /**
     * Returns the value to which the specified key is mapped, or defaultValue 
     * if this map contains no mapping for the key.
     */
    public Object getOrDefault (byte key, Object defaultValue)	{
    	int index = index(key);
    	return index < 0 ? defaultValue : _values[index];
    }
    
    private class HashCode1 implements Runnable	{
    	int min, max;
    	int hashCode = 0;
    	HashCode1 (int a, int b)	{
    		min=a; max=b;
    	}
    	public void run ()	{
            for (int i=min; i<max; i++) {
                if (_states[i] == FULL) {
                    hashCode += HashFunctions.hash(_set[i]) ^
                                HashFunctions.hash(_values[i]);
                }
            }
            intGlobal += hashCode;
    	}
    }
    /** {@inheritDoc} */
    public int hashCodePar(int numProcs)	{
    	intGlobal=0;
    	int numElemProc = _values.length/numProcs;
    	int numMin, numMax;
        threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
    		threads[i] = new Thread (new HashCode1 (numMin, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return intGlobal;
    }
    @Override
    public int hashCode() {
        int hashcode = 0;
        byte[] states = _states;
        for (int i = _values.length; i-- > 0;) {
            if (states[i] == FULL) {
                hashcode += HashFunctions.hash(_set[i]) ^
                            HashFunctions.hash(_values[i]);
            }
        }
        return hashcode;
    }

    /**
     * Returns true if this map contains no key-value mappings.
     */
    public boolean isEmpty() {
        return 0 == _size;
    }
    
    static class TByteObjectHashMapSpliterator {
        final TByteObjectHashMap map;
        byte currentKey;				// current key
        Object currentValue;			// current value
        int index;                  // current index, modified on advance/split
        int fence;                  // one past last index
        int est;                    // size estimate
        byte _no_entry_key;			// int key that represents null
        Object _no_entry_value;			// int value that represents null

        TByteObjectHashMapSpliterator(TByteObjectHashMap m, int origin,
                           int fence, int est, byte no_entry_key, Object no_entry_value) {
            this.map = m;
            this.index = origin;
            this.fence = fence;
            this.est = est;
            this._no_entry_key = no_entry_key;
            this._no_entry_value = no_entry_value;
        }

        final int getFence() { // initialize fence and size on first use
            int hi;
            if ((hi = fence) < 0) {
                TByteObjectHashMap m = map;
                est = m.size();
                byte[] states = m._states;
                hi = fence = (states == null) ? 0 : states.length;
            }
            return hi;
        }

        public final long estimateSize() {
            getFence(); // force init
            return (long) est;
        }
    }

    static final class TByteObjectKeySpliterator
        extends TByteObjectHashMapSpliterator
        implements TByteSpliterator {
        TByteObjectKeySpliterator(TByteObjectHashMap m, int origin, int fence, int est, byte no_entry, Object no_value) {
            super(m, origin, fence, est, no_entry, no_value);
        }

        public TByteObjectKeySpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || currentKey != _no_entry_key) ? null :
            	new TByteObjectKeySpliterator(map, lo, index = mid, est >>>= 1,
                _no_entry_key, _no_entry_value);
        }


        public void forEachRemaining (Consumer<? super Byte> action)	{
        	int i, hi;
        	TByteObjectHashMap m;
        	byte[] a;
        	if (action == null)
        		throw new NullPointerException();
        	if ((m = map) != null && (a = m._set) != null)	{
        		if ((hi = fence) < 0)
        			hi = m.size();
        		if ((i=index) <= 0 && (index=hi) <= a.length)	{
        			for (; i<hi; ++i)	{
        				byte e = a[i];
        				if (m._states[i] == FULL)	{
        					action.accept(e);
        				}
        			}
        		}
        	}
        	throw new ConcurrentModificationException();
        }

        public boolean tryAdvance(Consumer<? super Byte> action) {
        	if (action == null)
        		throw new NullPointerException();
        	int hi = getFence(), i = index;
        	if (i < hi)	{
        		index = i+1;
        		byte e = map._set[i];
        		action.accept(e);
        		return true;
        	}
        	return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size() ? Spliterator.SIZED : 0) |
                Spliterator.DISTINCT;
        }
    }

    static final class TByteObjectValueSpliterator
        extends TByteObjectHashMapSpliterator
        implements TObjectSpliterator {
        TByteObjectValueSpliterator(TByteObjectHashMap m, int origin, int fence, int est,
                         byte no_entry_key, Object no_entry_value) {
            super(m, origin, fence, est, no_entry_key, no_entry_value);
        }

        public TByteObjectValueSpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || currentValue != _no_entry_value) ? null :
                new TByteObjectValueSpliterator(map, lo, index = mid, est >>>= 1,
                                          _no_entry_key, _no_entry_value);
        }

        public void forEachRemaining (Consumer<? super Object> action)	{
        	int i, hi;
        	TByteObjectHashMap m;
        	Object[] a;
        	if (action == null)
        		throw new NullPointerException();
        	if ((m = map) != null && (a = m._values) != null)	{
        		if ((hi = fence) < 0)
        			hi = m.size();
        		if ((i=index) <= 0 && (index=hi) <= a.length)	{
        			for (; i<hi; ++i)	{
        				Object e = a[i];
        				if (m._states[i] == FULL)	{
        					action.accept(e);
        				}
        			}
        		}
        	}
        	throw new ConcurrentModificationException();
        }

        public boolean tryAdvance(Consumer<? super Object> action) {
        	if (action == null)
        		throw new NullPointerException();
        	int hi = getFence(), i = index;
        	if (i < hi)	{
        		index = i+1;
        		Object e = map._values[i];
        		action.accept(e);
        		return true;
        	}
        	return false;
        }

        public int characteristics() {
            return (fence < 0 || est == map.size() ? Spliterator.SIZED : 0);
        }
    }

    
    /**
     * Returns a Set view of the keys contained in this map.
     */
    public TByteSet keySet() {
        return new TKeyView();
    }
    
    /** a view onto the keys of the map. */
    protected class TKeyView implements TByteSet {

        /** {@inheritDoc} */
        public TByteIterator iterator() {
            return new TByteObjectKeyHashIterator (TByteObjectHashMap.this);
        }
        
        /** {@inheritDoc} */
        public TByteSpliterator spliterator() {
        	return new TByteObjectKeySpliterator(TByteObjectHashMap.this, 0, -1, 0, no_entry_key, no_entry_value);
        }

        /** {@inheritDoc} */
        public byte getNoEntryValue() {
            return no_entry_key;
        }


        /** {@inheritDoc} */
        public int size() {
            return _size;
        }


        /** {@inheritDoc} */
        public boolean isEmpty() {
            return 0 == _size;
        }


        /** {@inheritDoc} */
        public boolean contains (byte entry) {
            return TByteObjectHashMap.this.contains(entry);
        }
        public boolean containsPar (byte entry, int numProcs)	{
        	return TByteObjectHashMap.this.contains(entry);
        }


        /** {@inheritDoc} */
        public byte[] toArray() {
            return TByteObjectHashMap.this.keys();
        }

        /** {@inheritDoc} */
        public byte[] toArrayPar (int numProcs) {
            return TByteObjectHashMap.this.keysPar(numProcs);
        }

        /** {@inheritDoc} */
        public byte[] toArray (byte[] dest) {
            return TByteObjectHashMap.this.keys(dest);
        }
        public byte[] toArrayPar (byte[] dest, int numProcs)	{
        	return TByteObjectHashMap.this.keysPar(dest, numProcs);
        }


        /**
         * Unsupported when operating upon a Key Set view of a TByteObjectMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean add (byte entry) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean remove (byte entry) {
            return no_entry_value != TByteObjectHashMap.this.remove(entry);
        }
        /** {@inheritDoc} */
        public boolean removePar (byte entry, int numProcs)	{
        	return no_entry_value != TByteObjectHashMap.this.remove (entry);
        }

        private class ContainsAll1 implements Runnable	{
        	int min, max;
        	Byte[] array;
        	ContainsAll1 (int a, int b, Byte[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (array[i] instanceof Byte) {
                        byte ele = ((Byte)array[i]).byteValue();
                        if (!TByteObjectHashMap.this.containsKey(ele)) {
                            boolGlobal = false;
                            return;
                        }
                    } 
                    else {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAllPar (Collection<?> collection, int numProcs)	{
        	Byte[] array = collection.toArray(new Byte [collection.size()]);
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, collection.size());
        		threads[i] = new Thread (new ContainsAll1 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)
				try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	return boolGlobal;
        }
        
        /** {@inheritDoc} */
        public boolean containsAll (Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Byte) {
                    byte ele = ((Byte) element).byteValue();
                    if (!TByteObjectHashMap.this.containsKey(ele)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }

        private class ContainsAll2 implements Runnable	{
        	int min, max;
        	byte[] array;
        	ContainsAll2 (int a, int b, byte[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (!TByteObjectHashMap.this.containsKey (array[i])) {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAllPar (TByteCollection collection, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	byte[] array = collection.toArray (new byte[collection.size()]);
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, collection.size());
        		threads[i] = new Thread (new ContainsAll2 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	return boolGlobal;
        }
        /** {@inheritDoc} */
        public boolean containsAll (TByteCollection collection) {
            TByteIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (!TByteObjectHashMap.this.containsKey(iter.next())) {
                    return false;
                }
            }
            return true;
        }

        private class ContainsAll3 implements Runnable	{
        	int min, max;
        	byte[] array;
        	ContainsAll3 (int a, int b, byte[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (!TByteObjectHashMap.this.contains(array[i])) {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAllPar (byte[] array, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = array.length/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, array.length);
        		threads[i] = new Thread (new ContainsAll3 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	return boolGlobal;
        }
        
        /** {@inheritDoc} */
        public boolean containsAll (byte[] array) {
            for (byte element : array) {
                if (!TByteObjectHashMap.this.contains(element)) {
                    return false;
                }
            }
            return true;
        }


        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAll (Collection<? extends Byte> collection) {
            throw new UnsupportedOperationException();
        }
        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAllPar (Collection<? extends Byte> collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAll (TByteCollection collection) {
            throw new UnsupportedOperationException();
        }
        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAllPar (TByteCollection collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAll (byte[] array) {
            throw new UnsupportedOperationException();
        }
        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAllPar (byte[] array, int numProcs) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Removes all of the elements of this map whose key satisfy the given
         * predicate. Errors or runtime exceptions thrown during iteration or by
         * the predicate are relayed to the caller.
         * @param filter a predicate which returns true for elements to be removed
         * @return true if any elements were removed
         */
        public boolean removeIf (Predicate<? super Byte> filter) {
        	boolean modified = false;
            Objects.requireNonNull(filter);
            for (int i=0; i<_set.length; i++)	{
            	if (_states[i] == FULL)	{
            		if (filter.test(_set[i]))	{
            			_states[i] = REMOVED;
            			modified = true;
            		}
            	}
            }
            return modified;
        }
        
        private class RemoveIf1 implements Runnable	{
        	int min, max;
        	Predicate<? super Byte> filter;
        	RemoveIf1 (int a, int b, Predicate<? super Byte> c, int d)	{
        		min = a; max = b; filter = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				if (filter.test(_set[i]))	{
        					_states[i] = REMOVED;
        					boolGlobal = true;
        				}
        			}
        		}
        	}
        }
        /**
         * Removes all of the elements of this map whose key satisfy the given
         * predicate. Errors or runtime exceptions thrown during iteration or by
         * the predicate are relayed to the caller.
         * @param filter a predicate which returns true for elements to be removed
         * @return true if any elements were removed
         */
        public boolean removeIfPar (Predicate<? super Byte> filter, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = _set.length/numProcs;
        	threads = new Thread[numProcs];
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        		threads[i] = new Thread (new RemoveIf1 (numMin, numMax, filter, i));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
        	}
        	return boolGlobal;
        }

        private class RetainAll1 implements Runnable	{
        	int min, max;
        	Object[] array;
        	RetainAll1 (int a, int b, Object[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed=0;
        		for (int i=min; i<max; i++) {
                    if (_states[i] == FULL)	{
        			//if (array[i] instanceof Integer)	{
                    	if (Arrays.binarySearch(array, (Byte)_set[i]) < 0)	{
        				//int pos = TIntIntHashMap.this.index((Integer)array[i]);
        				//if (pos >= 0)	{
                    		_states[i] = REMOVED;
                    		removed++;
                    	}
                    }
                }
        		atIntGlobal.addAndGet(removed);
        	}
        }
        /** {@inheritDoc} */
        public boolean retainAllPar (Collection<?> collection, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	Object[] array = collection.toArray ();
        	Arrays.sort(array);
        	int numElemProc = _set.length/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        		threads[i] = new Thread (new RetainAll1 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }
        /** {@inheritDoc} */
        public boolean retainAll (Collection<?> collection) {
            boolean modified = false;
            TByteIterator iter = iterator();
            while (iter.hasNext()) {
                if (!collection.contains(Byte.valueOf(iter.next()))) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        private class RetainAll2 implements Runnable	{
        	int min, max;
        	byte[] array;
        	RetainAll2 (int a, int b, byte[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed = 0;
        		for (int i=min; i<max; i++) {
        			//if (_states[i] == FULL)	{
        			int pos = TByteObjectHashMap.this.index(array[i]);
        			if (pos < 0)	{
        				//if (Arrays.binarySearch(array, _set[i]) < 0)	{
        				_states[i] = REMOVED;
        				removed++;
        			}
        			//}
                }
				atIntGlobal.addAndGet(removed);
        	}
        }
        /** {@inheritDoc} */
        public boolean retainAllPar (TByteCollection collection, int numProcs)	{
        	if (this == collection)	return false;
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	byte[] array = collection.toArrayPar (numProcs);
        	//Arrays.sort(array);
        	int numElemProc = array.length/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, array.length);
        		threads[i] = new Thread (new RetainAll2 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }
        /** {@inheritDoc} */
        public boolean retainAll (TByteCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TByteIterator iter = iterator();
            while (iter.hasNext()) {
                if (!collection.contains(iter.next())) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }

        /** {@inheritDoc} */
        public boolean retainAllPar (byte[] array, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	int numElemProc = array.length/numProcs;
        	//Arrays.sort(array);
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, array.length);
        		threads[i] = new Thread (new RetainAll2 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }
        
        /** {@inheritDoc} */
        public boolean retainAll (byte[] array) {
            boolean changed = false;
            Arrays.sort(array);
            byte[] set = _set;
            byte[] states = _states;

            for (int i = set.length; i-- > 0;) {
                if (states[i] == FULL && (Arrays.binarySearch(array, set[i]) < 0)) {
                    removeAt(i);
                    changed = true;
                }
            }
            return changed;
        }

        /**private class RemoveAll1 implements Runnable	{
        	int min, max;
        	Object[] array;
        	RemoveAll1 (int a, int b, Object[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed=0;
        		for (int i=min; i<max; i++) {
        			if (_states[i] == FULL)	{
                    	if (Arrays.binarySearch(array, (Byte)_set[i]) >= 0)	{
        			//if (array[i] instanceof Integer)	{
                    	//int pos = TIntIntHashMap.this.index((Integer)array[i]);
                    	//if (pos >= 0)	{
                    		_states[i] = REMOVED;
                    		removed++;
                    	}
                    }
                }
        		atIntGlobal.addAndGet(removed);
        	}
        }*/
        
        private class RemoveAll0 implements Runnable	{
        	int min, max;
        	Object[] array;
        	RemoveAll0 (int a, int b, Object[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run()	{
        		int removed = 0;
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				int pos = TByteObjectHashMap.this.index((Byte)array[i]);
        				if (pos >= 0)	{
        					_states[i] = REMOVED;
        					removed++;
        				}
        			}
        		}
        		atIntGlobal.addAndGet(removed);
        	}
        }
        /** {@inheritDoc} */
        public boolean removeAllPar (Collection<?> collection, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	Object[] array = collection.toArray ();
        	//Arrays.sort(array);
        	int numElemProc = _set.length/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        		threads[i] = new Thread (new RemoveAll0 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }
        
        /** {@inheritDoc} */
        public boolean removeAll (Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                if (element instanceof Byte) {
                    byte c = ((Byte) element).byteValue();
                    if (remove(c)) {
                        changed = true;
                    }
                }
            }
            return changed;
        }

        /** {@inheritDoc} */
        public boolean removeAll (TByteCollection collection) {
            if (this == collection) {
                clear();
                return true;
            }
            boolean changed = false;
            TByteIterator iter = collection.iterator();
            while (iter.hasNext()) {
                byte element = iter.next();
                if (remove(element)) {
                    changed = true;
                }
            }
            return changed;
        }
        private class RemoveAll2 implements Runnable	{
        	int min, max;
        	int[] array;
        	RemoveAll2 (int a, int b, int[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed = 0;
        		for (int i=min; i<max; i++) {
        			if (_states[i] == FULL)	{
        				if (Arrays.binarySearch(array, _set[i]) >= 0)	{
        					_states[i] = REMOVED;
        					removed++;
        				}
        			}
                }
				atIntGlobal.addAndGet(removed);
        	}
        }
        /** {@inheritDoc} */
        public boolean removeAllPar (TByteCollection collection, int numProcs)	{
        	if (this == collection)	{
        		clear();
        		return true;
        	}
        	atIntGlobal = new AtomicInteger(0);
        	int oldSize = _size;
        	byte[] array = collection.toArrayPar (numProcs);
        	//Arrays.sort(array);
        	int numElemProc = array.length/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, array.length);
        		threads[i] = new Thread (new RemoveAll3 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }

        /** {@inheritDoc} */
        public boolean removeAll (byte[] array) {
            boolean changed = false;
            for (int i=array.length; i-->0;) {
                if (remove(array[i])) {
                    changed = true;
                }
            }
            return changed;
        }
        
        private class RemoveAll3 implements Runnable	{
        	int min, max;
        	byte[] array;
        	RemoveAll3 (int a, int b, byte[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed = 0;
        		for (int i=min; i<max; i++) {
        			if (_states[i] == FULL)	{
        				//if (Arrays.binarySearch(array, _set[i]) >= 0)	{
        				int pos = TByteObjectHashMap.this.index(array[i]);
        				if (pos >= 0)	{
        					_states[pos] = REMOVED;
        					removed++;
        				}
        			}
                }
				atIntGlobal.addAndGet(removed);
        	}
        }     
        /** {@inheritDoc} */
        public boolean removeAllPar (byte[] array, int numProcs)	{
        	atIntGlobal = new AtomicInteger(0);
        	int oldSize = _size;
        	int numElemProc = array.length/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, array.length);
        		threads[i] = new Thread (new RemoveAll3 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }
        
        public boolean removeAllPar2 (int[] array, int numProcs)	{
        	atIntGlobal = new AtomicInteger(0);
        	Arrays.sort(array);
        	int oldSize = _size;
        	int numElemProc = _set.length/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        		threads[i] = new Thread (new RemoveAll2 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }


        /** {@inheritDoc} */
        public void clear() {
            TByteObjectHashMap.this.clear();
        }
        /** {@inheritDoc} */
        public void clearPar(int numProcs)	{
        	TByteObjectHashMap.this.clearPar(numProcs);
        }


        /** {@inheritDoc} */
        public void forEach (Consumer<? super Byte> action)	{
        	TByteObjectHashMap.this.forEachKey (action);
        }
        /** {@inheritDoc} */
        public void forEachPar (Consumer<? super Byte> action, int numProcs)	{
        	TByteObjectHashMap.this.forEachKeyPar (action, numProcs);
        }

        /**private class Equals1 implements Runnable	{
        	int min, max;
        	TIntSet that;
        	Equals1 (int a, int b, TIntSet c)	{
        		min = a; max = b; that = c;
        	}
        	public void run ()	{
                for (int i=min; i<max; i++) {
                	if (!boolGlobal)	return;
                    if (_states[i] == FULL) {
                        if (!that.contains(_set[i])) {
                            boolGlobal = false;
                            return;
                        }
                    }
                }
        	}
        }
        @Override
        public boolean equalsPar (Object other, int numProcs)	{
        	if (!(other instanceof TIntSet))	return false;
        	final TIntSet that = (TIntSet) other;
        	if (that.size() != this.size())	return false;
        	boolGlobal = true;
        	int numElemProc = _states.length/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _states.length);
        		threads[i] = new Thread (new Equals1 (numMin, numMax, that));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	return boolGlobal;
        }*/
        private class Equals1 implements Runnable	{
        	int min, max;
        	TByteSet that;
        	Equals1 (int a, int b, TByteSet c)	{
        		min = a; max = b; that = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++)	{
        			if (!that.contains(_set[i]))	{
        				boolGlobal = false;
        				return;
        			}
        		}
        	}
        }
        private class Equals2 implements Runnable	{
        	int min, max;
        	Set<?> that;
        	Equals2 (int a, int b, Set<?> c)	{
        		min = a; max = b; that = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++)	{
        			if (!that.contains(_values[i]))	{
        				boolGlobal = false;
        				return;
        			}
        		}
        	}
        }
        @Override
        /** {@inheritDoc} */
        public boolean equalsPar (Object o, int numProcs)	{
        	boolGlobal = false;
        	if (o instanceof TByteSet)	{
        		TByteSet that = (TByteSet) o;
        		if (this.size() != that.size())	{
        			return false;
        		}
        		threads = new Thread[numProcs];
        		boolGlobal = true;
        		int numElemProc = _values.length/numProcs;
        		int numMin, numMax;
        		for (int i=0; i<numProcs; i++)	{
        			numMin = limiteMin (i, numElemProc, 0);
            		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
            		threads[i] = new Thread (new Equals1 (numMin, numMax, that));
        			threads[i].start();
        		}
        	}
        	if (o instanceof Set<?>)	{
        		Set<?> that = (Set<?>) o;
        		if (this.size() != that.size())
        			return false;
        		threads = new Thread[numProcs];
        		boolGlobal = true;
        		int numElemProc = _values.length/numProcs;
        		int numMin, numMax;
        		for (int i=0; i<numProcs; i++)	{
        			numMin = limiteMin (i, numElemProc, 0);
            		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
            		threads[i] = new Thread (new Equals2 (numMin, numMax, that));
        			threads[i].start();
        		}
        	}
        	return boolGlobal;
        }
        @Override
        /** {@inheritDoc} */
        public boolean equals (Object other) {
            if (!(other instanceof TByteSet)) {
                return false;
            }
            final TByteSet that = (TByteSet) other;
            if (that.size() != this.size()) {
                return false;
            }
            for (int i=_states.length; i-->0;) {
                if (_states[i] == FULL) {
                    if (!that.contains(_set[i])) {
                        return false;
                    }
                }
            }
            return true;
        }

        private class HashCode1 implements Runnable	{
        	int min, max;
        	HashCode1 (int a, int b)	{
        		min = a; max = b;
        	}
        	public void run ()	{
        		int intLocal = 0;
        		for (int i=min; i<max; i++) {
                    if (_states[i] == FULL) {
                        intLocal += HashFunctions.hash(_set[i]);
                    }
                }
        		atIntGlobal.addAndGet(intLocal);
        	}
        }
        
        /** {@inheritDoc} */
        public int hashCodePar (int numProcs)	{
        	atIntGlobal = new AtomicInteger(0);
        	int numElemProc = _states.length/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _states.length);
        		threads[i] = new Thread (new HashCode1 (numMin, numMax));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	return atIntGlobal.get();
        }
        @Override
        /** {@inheritDoc} */
        public int hashCode () {
            int hashcode = 0;
            for (int i=_states.length; i-->0;) {
                if (_states[i] == FULL) {
                    hashcode += HashFunctions.hash(_set[i]);
                }
            }
            return hashcode;
        }


        @Override
        /** {@inheritDoc} */
        /**public String toString() {
            final StringBuilder buf = new StringBuilder( "{" );
            forEachKey( new TIntProcedure() {
                private boolean first = true;


                public boolean execute( int key ) {
                    synchronized (this)	{
                    	if ( first ) {
                    		first = false;
                    	}
                    	else {
                    		buf.append( ", " );
                    	}
                    }
                    synchronized (this)	{
                    	buf.append( key );
                    }
                    return true;
                }
            } );
            synchronized (this)	{
            	buf.append( "}" );
            }
            return buf.toString();
        }*/
        public String toString()	{
        	StringBuilder buf = new StringBuilder("{");
        	int first = 0;
        	if (_size > 0)	{
        		while (_states[first] != FULL)	first++;
        		buf.append(_set[first]);
        	}
        	for (int i=0; i<_set.length; i++)	{
        		if (_states[i] == FULL)	{
        			buf.append(", ");
        			buf.append(_set[i]);
        		}
        	}
        	buf.append(']');
        	return buf.toString();
        }
        private class ToString1 implements Runnable	{
        	int min, max, idProc;
        	ToString1 (int a, int b, int c)	{
        		min = a; max = b; idProc = c;
        	}
        	public void run ()	{
        		//System.out.println("Thread en toString");
        		StringBuilder sbLocal = new StringBuilder ();
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				sbLocal.append(", ");
        				sbLocal.append(_set[i]);
        			}
        		}
        		if (idProc > 0)
        			while (intsGlobal2[idProc-1] == -1)
        				System.out.print ("");
        		sbGlobal.append(sbLocal);
        		intsGlobal2[idProc] = 0;
        	}
        }
        /** {@inheritDoc} */
        public String toStringPar (int numProcs)	{
        	sbGlobal = new StringBuilder ();
        	sbGlobal.append('[');
        	int first = 0;
        	if (_size > 0)	{
        		while (_states[first] != FULL)	first++;
        		sbGlobal.append(_set[first]);
        	}
        	intsGlobal2 = new int[numProcs];
        	int numElemProc = _set.length/numProcs;
        	int numMin, numMax;
        	threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, first+1);
        		numMax = limiteMax (i, numElemProc, first+1, numProcs, _values.length);
        		threads[i] = new Thread (new ToString1 (numMin, numMax, i));
        		intsGlobal2[i] = -1;
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	sbGlobal.append(']');
        	return sbGlobal.toString();
        }
    }

    /** {@inheritDoc} */
    public Object merge (byte key, Object value, BiFunction<? super Byte, ? super Object,
    		? extends Object> remappingFunction)	{
    	if (value == no_entry_value)
    		throw new NullPointerException();
    	if (remappingFunction == null)
    		throw new NullPointerException();
    	int index = index (key);
    	if ((index < 0) || (_values[index] == no_entry_value))	{
    		put (key, value);
    		return value;
    	}
    	Object newValue = remappingFunction.apply (_set[index], value);
    	if (newValue != null)	{
    		put (key, newValue);
    		return newValue;
    	}
    	removeAt (index);
    	return no_entry_value;
    }
    
    /** {@inheritDoc} */
    public Object put (byte key, Object value) {
        int index = insertKey (key);
        return doPut (key, value, index);
    }
    /**
     * Associates the specified value with the specified key in this map. If the map
     * previously contained a mapping for the key, the old value is replaced.
     * @param key key with with the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param numProcs number of threads that will be used
     * @return the previous value associated with key, or null if there was no mapping
     * for key. A null return can also indicate that the map previously associated null
     * with key
     */
     public Object putPar (byte key, Object value) {
     	//System.out.println ("Put: " + key + ", " + value);
         int index = insertKeyPar(key);
         return doPut(key, value, index);
     }

    /**
     * Associates the specified value with the specified key in this map. If the map
     * previously contained a mapping for the key, the old value is replaced.
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key 
     * @param index position to insert the value and the key (if it is not inserted)
     * @return the previous value associated with key, or null if there was no
     * mapping for key. A null return can also indicate that the map previously
     * associated null with key
     */
    private Object doPut (byte key, Object value, int index) {
        Object previous = no_entry_value;
        boolean isNewMapping = true;
        if (index < 0) {
            index = -index -1;
            previous = _values[index];
            isNewMapping = false;
        }
        _values[index] = value;

        if (isNewMapping) {
            postInsertHook(consumeFreeSlot);
        }

        return previous;
    }
    
     /** Associates the specified value with the specified key in this map. If the map
      * previously contained a mapping for the key, the old value is replaced.
      * This is called in putAll only. It just put elements in the hashMap, and the size
      * will be increased when all the calls to this method finalize.
      * @param key key with which the specified value is to be associated
      * @param value value to be associated with the specified key
      * @return the previous value associated with key, or null if there was no mapping
      * for key. A null return can also indicate that the map previously associated null
      * with key
      */
     public Object putParAll (byte key, Object value) {
     	//System.out.println ("Put: " + key + ", " + value);
         int index = insertKeyParAll(key);
         return doPutAll(key, value, index);
     }
    /**
     * Put a element in the map. This method is only called by putAll. It just
     * put elements in the hashMap, and the size will be increased when all
     * the calls to this method finalize.
     * @param key key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @param index position where the element will be inserted.
     * @return the previous value associated with key, or null if there was no
     * mapping for key. A null return can also indicate that the map previously
     * associated null with key
     */
    private Object doPutAll(byte key, Object value, int index) {
        Object previous = no_entry_value;
        //boolean isNewMapping = true;
        if (index < 0) {
        	atIntGlobal.incrementAndGet();
            index = -index -1;
            previous = _values[index];
            //isNewMapping = false;
        }
        _values[index] = value;

        //if (isNewMapping) {
        //    postInsertHook( consumeFreeSlot );
        //}

        return previous;
    }     
    /** {@inheritDoc} */
    public void putAll (Map<? extends Byte, ? extends Object> map) {
        ensureCapacity(map.size());
        // could optimize this for cases when map instanceof THashMap
        for (Map.Entry<? extends Byte, ? extends Object> entry : map.entrySet()) {
            //this.put(entry.getKey().byteValue(), entry.getValue().ObjectValue());
            this.put(entry.getKey().byteValue(), entry.getValue());
        }
    }
     private class PutAll1 implements Runnable	{
     	int min, max;
     	Entry<?,?>[] entradas;
     	PutAll1 (int a, int b, Entry<?, ?>[] e)	{
     		min=a; max=b; entradas = e;
     	}
     	public void run ()	{
     		byte clave;
     		Object valor;
     		for (int i=min; i<max; i++)	{
     			if (entradas[i].getKey() instanceof Integer)	{
     				clave = ((Byte) entradas[i].getKey()).byteValue();
     				valor = ((Object) entradas[i].getValue());
     				putParAll (clave, valor);
     				//put (((Integer)entradas[i].getKey()).intValue(), entradas[i].getValue().intValue());
     			}
     		}
     	}
     }
     /**
      * Copies all of the mappings from the specified map to this map. These mappings
      * will replace any mappings that this map had for any of the keys currently in
      * the specified map.
      * @param map mappings to be stored in this map
      * @param numProcs number of threads that will be used
      */
     public void putAllPar(Map<? extends Byte, ? extends Object> map, int numProcs) {
         ensureCapacity(map.size());
         atIntGlobal = new AtomicInteger(0);
         int numElemProc = map.size()/numProcs;
         Entry<?,?>[] entradas;
         entradas = map.entrySet().toArray (new Entry[map.size()]);
         int numMin, numMax;
         threads = new Thread[numProcs];
         for (int i=0; i<numProcs; i++)	{
     		numMin = limiteMin (i, numElemProc, 0);
     		numMax = limiteMax (i, numElemProc, 0, numProcs, map.size());
         	threads[i] = new Thread (new PutAll1 (numMin, numMax, entradas));
         	threads[i].start();
         }
         for (int i=0; i<numProcs; i++)
 			try {
 				threads[i].join();
 			} catch (InterruptedException e) {
 				e.printStackTrace();
 			}
     }
     
     private class PutAll2 implements Runnable	{
     	int min, max;
     	byte[] keys;
     	Object[] values;
     	PutAll2 (int a, int b, byte[] c, Object[] d)	{
     		min = a; max = b; keys = c; values = d;
     	}
     	public void run ()	{
     		//System.out.println ("Entrando en un thread. Posiciones: desde " + min + " hasta " + max);
     		for (int i=min; i<max; i++)	{
     			putParAll (keys[i], values[i]);
     		}
     	}
     }
     
     /** {@inheritDoc} */
     public void putAll (TByteObjectMap map) {
         ensureCapacity(map.size());
         /**TIntIntIterator iter = map.iterator();
         while (iter.hasNext()) {
             iter.advance();
             this.put(iter.key(), iter.value());
         }*/
         for (int i=0; i<map.size(); i++)	{
        	 if (_states[i] == FULL)
        		 put (_set[i], _values[i]);
         }
     }
     /**
      * Copies all of the mappings from the specified map to this map. These mappings
      * will replace any mappings that this map had for any of the keys currently in
      * the specified map.
      * @param map mappings to be stored in this map
      * @param numProcs number of threads that will be used
      */
     public void putAllPar (TByteObjectMap map, int numProcs) {
         ensureCapacity(map.size());
         int numMin, numMax;
         threads = new Thread[numProcs];
         byte[] set = map.keySet().toArrayPar(numProcs);
         Object[] values = map.values().toArrayPar(numProcs);
         int size = set.length;
         int numElemProc = size/numProcs;
         for (int i=0; i<numProcs; i++)	{
     		numMin = limiteMin (i, numElemProc, 0);
     		numMax = limiteMax (i, numElemProc, 0, numProcs, size);
         	threads[i] = new Thread (new PutAll2 (numMin, numMax, set, values));
         	threads[i].start();
         }
         for (int i=0; i<numProcs; i++)	{
         	try {
 				threads[i].join();
 			} catch (InterruptedException e) {
 				e.printStackTrace();
 			}
         }
     }

     /** {@inheritDoc} */
     public void putAll (byte[] keys, Object[] values)	{
    	 int size = keys.length;
    	 ensureCapacity (size);
    	 for (int i=0; i<size; i++)	{
    		 this.put(keys[i], values[i]);
    	 }
     }
     /**
      * Copies all of the mappings from the specified map to this map. These mappings
      * will replace any mappings that this map had for any of the keys currently in
      * the specified map.
      * @param keys keys to be stored in this map
      * @param values values to be stored in this map
      * @param numProcs number of threads that will be used
      */
     public void putAllPar(byte[] keys, Object[] values, int numProcs) {	
     	int size = keys.length;
         ensureCapacity (size);
         _size+=size;
         _free-=size;
         int numElemProc = size/numProcs;
         int numMin, numMax;
         threads = new Thread[numProcs];
         for (int i=0; i<numProcs; i++)	{
     		numMin = limiteMin (i, numElemProc, 0);
     		numMax = limiteMax (i, numElemProc, 0, numProcs, size);
         	threads[i] = new Thread (new PutAll2 (numMin, numMax, keys, values));
         	threads[i].start();
         }
         for (int i=0; i<numProcs; i++)	{
         	try {
 				threads[i].join();
 			} catch (InterruptedException e) {
 				e.printStackTrace();
 			}
         }
     }

    /** {@inheritDoc} */
    public Object putIfAbsent (byte key, Object value) {
        int index = insertKey(key);
        if (index < 0)
            return _values[-index - 1];
        return doPut(key, value, index);
    }
    
    /** {@inheritDoc} */
    public Object remove(byte key) {
        Object prev = no_entry_value;
        int index = index(key);
        if (index >= 0) {
            prev = _values[index];
            removeAt(index);    // clear key,state; adjust size
        }
        return prev;
    }

    /** {@inheritDoc} */
    public boolean remove (byte key, Object value)	{
    	int index = index (key);
    	if ((index > 0) && (_values[index] == value))	{
    		removeAt (index);
    		return true;
    	}
    	return false;
    }

    /** {@inheritDoc} */
    protected void removeAt (int index) {
        _values[index] = no_entry_value;
        super.removeAt(index);
    }

    /** {@inheritDoc} */
    public Object replace (byte key, Object value)	{
    	int index = index (key);
    	if (index > 0)	{
    		Object oldValue = _values[index];
    		_values[index] = value;
    		return oldValue;
    	}
    	return no_entry_value;
    }

    /** {@inheritDoc} */
    public boolean replace (byte key, Object oldValue, Object newValue)	{
    	int index = index (key);
    	if ((index > 0) && (_values[index] == oldValue))	{
    		_values[index] = newValue;
    		return true;
    	}
    	return false;
    }

    /** {@inheritDoc} */
    public void replaceAll (BiFunction 
    			<? super Byte, ? super Object, ? extends Object> function)	{
    	for (int i=0; i<_values.length; i++)	{
    		if (_states[i] == FULL)	{
    			Object newValue = function.apply(_set[i], _values[i]);
    			_values[i] = newValue;
    		}
    	}
    }
    private class ReplaceAll1 implements Runnable	{
    	int min, max;
    	BiFunction <? super Byte, ? super Object, ? extends Object> function;
    	ReplaceAll1 (int a, int b, BiFunction <? super Byte, ? super Object,
			? extends Object> c)	{
    		min = a; max = b; function = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			if (_states[i] == FULL)	{
    				_values[i] = function.apply(_set[i], _values[i]);
    			}
    		}
    	}
    }
    /**
     * Replaces each entry's value with the result of invoking the given function
     * on that entry until all entries have been processed or the function throws
     * an exception. Exceptions thrown by the function are relayed to the caller.
     * @param function the function to apply to each entry
     * @param numProcs number of threads that will be used
     */
    public void replaceAllPar (BiFunction <? super Byte, ? super Object,
    			? extends Object> function, int numProcs)	{
    	int numElemProc = _values.length/numProcs;
    	int numMin, numMax;
        threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
    		threads[i] = new Thread (new ReplaceAll1 (numMin, numMax, function));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }

    /** {@inheritDoc} */
    public int size()	{
    	return _size;
    }
    
    private class ToString3 implements Runnable	{
    	int min, max, idProc;
    	ToString3 (int a, int b, int c)	{
    		min = a; max = b; idProc = c;
    	}
    	public void run ()	{
    		//String stringLocal = "";
    		StringBuilder sbLocal = new StringBuilder();
    		for (int i=min; i<max; i++)	{
    			if (_states[i] == FULL)	{
    				sbLocal.append(", ");
    				sbLocal.append(_set[i]);
    				sbLocal.append('=');
    				sbLocal.append(_values[i]);
    			}
    		}
    		if (idProc > 0)
    			while (intsGlobal2[idProc-1] == -1)
    				System.out.print ("");
    		sbGlobal.append(sbLocal);
    		intsGlobal2[idProc] = 0;
    	}
    }
    /** {@inheritDoc} */
    public String toStringPar (int numProcs)	{
    	sbGlobal = new StringBuilder();
    	sbGlobal.append('{');
    	int first = 0;
    	if (_size > 0)	{
    		while (_states[first] != FULL)	first++;
    		sbGlobal.append(_set[first]);
    		sbGlobal.append('=');
    		sbGlobal.append(_values[first]);
    	}
    	intsGlobal2 = new int[numProcs];
    	int numElemProc = _values.length/numProcs;
    	int numMin, numMax;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, first+1);
    		numMax = limiteMax (i, numElemProc, first+1, numProcs, _values.length);
    		threads[i] = new Thread (new ToString3 (numMin, numMax, i));
    		intsGlobal2[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	//stringGlobal += "}";
    	sbGlobal.append('}');
    	return sbGlobal.toString();
    }
    /** {@inheritDoc} */
    @Override
    public String toString() {
    	StringBuilder buf = new StringBuilder("{");
    	int first = 0;
    	if (_size > 0)	{
    		while (_states[first] != FULL)	first++;
    		buf.append(_values[first]);
    	}
    	for (int i=first; i<_states.length; i++)	{
    		if (_states[i] == FULL)	{
    			buf.append(", ");
				buf.append(_set[i]);
				buf.append('=');
				buf.append(_values[i]);
    		}
    	}
    	buf.append('}');
    	return buf.toString();
    }
    
    /** {@inheritDoc} */
    public TObjectCollection values ()	{
    	TObjectCollection c = new TValueView ();
    	return c;
    }
    /** a view onto the values of the map. */
    protected class TValueView implements TObjectCollection {

        /** {@inheritDoc} */
        public TObjectIterator iterator() {
            return new TByteObjectValueHashIterator(TByteObjectHashMap.this);
        }

        /** {@inheritDoc} */
        public TObjectSpliterator spliterator() {
        	return new TByteObjectValueSpliterator(TByteObjectHashMap.this, 0, -1, 0, no_entry_key, no_entry_value);
        }
        
        /**private class Equals1 implements Runnable	{
        	int min, max;
        	TIntCollection that;
        	Equals1 (int a, int b, TIntCollection c)	{
        		min = a; max = b; that = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++)	{
        			if (!that.contains(_values[i]))	{
        				boolGlobal = false;
        				return;
        			}
        		}
        	}
        }
        private class Equals2 implements Runnable	{
        	int min, max;
        	Collection<?> that;
        	Equals2 (int a, int b, Collection<?> c)	{
        		min = a; max = b; that = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++)	{
        			if (!that.contains(_values[i]))	{
        				boolGlobal = false;
        				return;
        			}
        		}
        	}
        }*/
        public boolean equalsPar2 (Object o, int numProcs)	{
        	boolGlobal = false;
        	if (o instanceof TObjectCollection)	{
        		TObjectCollection that = (TObjectCollection) o;
        		if (this.size() != that.size())	{
        			return false;
        		}
        		threads = new Thread[numProcs];
        		boolGlobal = true;
        		int numElemProc = _values.length/numProcs;
        		int numMin, numMax;
        		Object[] array = that.toArrayPar(numProcs);
        		for (int i=0; i<numProcs; i++)	{
        			numMin = limiteMin (i, numElemProc, 0);
            		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
            		threads[i] = new Thread (new Equals1 (numMin, numMax, array));
        			threads[i].start();
        		}
        	}
        	if (o instanceof Collection<?>)	{
        		Collection<?> that = (Collection<?>) o;
        		if (this.size() != that.size())
        			return false;
        		threads = new Thread[numProcs];
        		boolGlobal = true;
        		int numElemProc = _values.length/numProcs;
        		int numMin, numMax;
        		Object[] array = that.toArray();
        		for (int i=0; i<numProcs; i++)	{
        			numMin = limiteMin (i, numElemProc, 0);
            		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
            		threads[i] = new Thread (new Equals2 (numMin, numMax, array));
        			threads[i].start();
        		}
        	}
        	return boolGlobal;
        }
        private class Equals3 implements Runnable	{
        	int min, max;
        	TObjectCollection that;
        	Equals3 (int a, int b, TObjectCollection c)	{
        		min = a; max = b; that = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++)	{
        			if (!that.contains(_values[i]))	{
        				boolGlobal = false;
        				return;
        			}
        		}
        	}
        }
        private class Equals4 implements Runnable	{
        	int min, max;
        	Collection<?> that;
        	Equals4 (int a, int b, Collection<?> c)	{
        		min = a; max = b; that = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++)	{
        			if (!that.contains(_values[i]))	{
        				boolGlobal = false;
        				return;
        			}
        		}
        	}
        }
        private class Equals1 implements Runnable	{
        	int min, max;
        	Object[] that;
        	Equals1 (int a, int b, Object[] c)	{
        		min = a; max = b; that = c;
        	}
        	public void run()	{
        		for (int i=min; i<max; i++)	{
        			if (!boolGlobal)
        				return;
        			if (Arrays.binarySearch(that, _values[i]) < 0)	{
        				boolGlobal = false;
        				return;
        			}
        		}
        	}
        }
        private class Equals2 implements Runnable	{
        	int min, max;
        	Object[] that;
        	Equals2 (int a, int b, Object[] c)	{
        		min = a; max = b; that = c;
        	}
        	public void run()	{
        		for (int i=min; i<max; i++)	{
        			if (!boolGlobal)
        				return;
        			if (Arrays.binarySearch(that, (Object)_values[i]) < 0)	{
        				boolGlobal = false;
        				return;
        			}
        		}
        	}
        }
        @Override
        /** {@inheritDoc} */
        public boolean equalsPar (Object o, int numProcs)	{
        	boolGlobal = false;
        	if (o instanceof TObjectCollection)	{
        		TObjectCollection that = (TObjectCollection) o;
        		if (this.size() != that.size())	{
        			return false;
        		}
        		threads = new Thread[numProcs];
        		boolGlobal = true;
        		int numElemProc = _values.length/numProcs;
        		int numMin, numMax;
        		for (int i=0; i<numProcs; i++)	{
        			numMin = limiteMin (i, numElemProc, 0);
            		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
            		threads[i] = new Thread (new Equals3 (numMin, numMax, that));
        			threads[i].start();
        		}
        		for (int i=0; i<numProcs; i++)	{
        			try {
						threads[i].join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
        	}
        	if (o instanceof Collection<?>)	{
        		Collection<?> that = (Collection<?>) o;
        		if (this.size() != that.size())
        			return false;
        		threads = new Thread[numProcs];
        		boolGlobal = true;
        		int numElemProc = _values.length/numProcs;
        		int numMin, numMax;
        		for (int i=0; i<numProcs; i++)	{
        			numMin = limiteMin (i, numElemProc, 0);
            		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
            		threads[i] = new Thread (new Equals4 (numMin, numMax, that));
        			threads[i].start();
        		}
        		for (int i=0; i<numProcs; i++)	{
        			try {
						threads[i].join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
        		}
        	}
        	return boolGlobal;
        }
        /** {@inheritDoc} */
        public boolean equals (Object o)	{
        	if (o instanceof TObjectCollection)	{
        		TObjectCollection that = (TObjectCollection) o;
        		if (this.size() != that.size())	{
        			return false;
        		}
        		for (int i=0; i<_states.length; i++)	{
        			if (_states[i] == FULL)
        				if (!that.contains(_values[i]))
        					return false;
        		}

        	}
        	if (o instanceof Collection<?>)	{
        		Collection<?> that = (Collection<?>) o;
        		if (this.size() != that.size())	{
        			return false;
        		}
        		for (int i=0; i<_states.length; i++)	{
        			if (_states[i] == FULL)
        				if (!that.contains(_values[i]))
        					return false;
        		}
        	}
        	return false;
        }

        /** {@inheritDoc} */
        public Object getNoEntryValue() {
            return no_entry_value;
        }


        /** {@inheritDoc} */
        public int size() {
            return _size;
        }


        /** {@inheritDoc} */
        public boolean isEmpty() {
            return 0 == _size;
        }


        /** {@inheritDoc} */
        public boolean contains (Object entry) {
            return TByteObjectHashMap.this.containsValue(entry);
        }
        @Override
        /** {@inheritDoc} */
        public boolean containsPar(Object entry, int numProcs) {
            return TByteObjectHashMap.this.containsValuePar(entry, numProcs);
        }


        /** {@inheritDoc} */
        public Object[] toArray() {
            return TByteObjectHashMap.this.toArrayValues();
        }

        /** {@inheritDoc} */
        public Object[] toArrayPar(int numProcs) {
            return TByteObjectHashMap.this.toArrayValuesPar(numProcs);
        }

        /** {@inheritDoc} */
        public Object[] toArray(Object[] dest) {
            return TByteObjectHashMap.this.toArrayValues(dest);
        }
        /** {@inheritDoc} */
        public Object[] toArrayPar(Object[] dest, int numProcs) {
            return TByteObjectHashMap.this.toArrayValuesPar(dest, numProcs);
        }

        public boolean add(Object entry) {
            throw new UnsupportedOperationException();
        }
        private class Remove1 implements Runnable	{
        	int min, max;
        	Object entry;
        	Remove1 (int a, int b, Object c)	{
        		min = a; max = b; entry = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (boolGlobal)	return;
                    //if ((_set[i] != FREE && _set[i] != REMOVED) && entry == _values[i]) {
                        //removeAt (i);
        			if (_states[i] == FULL && _values[i] == entry)		{
                        _states[i] = REMOVED;
        				boolGlobal = true;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean removePar (Object entry, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = _values.length/numProcs;
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
        		threads[i] = new Thread (new Remove1 (numMin, numMax, entry));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	return boolGlobal;
        }

        /** {@inheritDoc} */
        public boolean remove(Object entry) {
            Object[] values = _values;
            byte[] states = _states;

            for (int i = values.length; i-- > 0;) {
                if ((states[i] != FREE && states[i] != REMOVED) && entry == values[i]) {
                    removeAt(i);
                    return true;
                }
            }
            return false;
        }

        private class ContainsAll1 implements Runnable	{
        	int min, max;
        	Object[] array;
        	Object[] values;
        	ContainsAll1 (int a, int b, Object[] c, Object[] d)	{
        		min = a; max = b; array = c; values = d;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (array[i] instanceof Object) {
                        Object ele = ((Object) array[i]);
                        //if (!TIntIntHashMap.this.containsValue (ele)) {
                        if (Arrays.binarySearch(values, ele) < 0)	{
                            boolGlobal = false;
                            return;
                        }
                    } else {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAllPar (Collection<?> collection, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	Object[] array = collection.toArray (new Object[collection.size()]);
        	Object[] values = TByteObjectHashMap.this.values().toArrayPar(numProcs);
        	Arrays.sort(values);
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, collection.size());
        		threads[i] = new Thread (new ContainsAll1 (numMin, numMax, array, values));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	return boolGlobal;
        }
        /** {@inheritDoc} */
        public boolean containsAll(Collection<?> collection) {
            for (Object element : collection) {
                if (element instanceof Object) {
                    //Object ele = ((Object) element).ObjectValue();
                    if (!TByteObjectHashMap.this.containsValue(element)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            return true;
        }

        private class ContainsAll2 implements Runnable	{
        	int min, max;
        	Object[] array, values;
        	ContainsAll2 (int a, int b, Object[] c, Object[] d)	{
        		min = a; max = b; array = c; values = d;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    //if (!TIntIntHashMap.this.containsValue(array[i])) {
        			if (Arrays.binarySearch(values, array[i]) < 0)	{
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAllPar (TObjectCollection collection, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	Object[] array = collection.toArray (new Object[collection.size()]);
        	Object[] values = TByteObjectHashMap.this.values().toArrayPar(numProcs);
        	Arrays.sort(values);
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, collection.size());
        		threads[i] = new Thread (new ContainsAll2 (numMin, numMax, array, values));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	return boolGlobal;
        }
        /** {@inheritDoc} */
        public boolean containsAll (TObjectCollection collection) {
            TObjectIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (!TByteObjectHashMap.this.containsValue(iter.next())) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        public boolean containsAllPar (Object[] array, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = array.length/numProcs;
        	Object[] values = TByteObjectHashMap.this.values().toArrayPar(numProcs);
        	Arrays.sort(values);
        	int numMin, numMax;
            threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, array.length);
        		threads[i] = new Thread (new ContainsAll2 (numMin, numMax, array, values));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	return boolGlobal;
        }
        /** {@inheritDoc} */
        public boolean containsAll (Object[] array) {
            for (Object element : array) {
                if (!TByteObjectHashMap.this.containsValue(element)) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        public boolean addAll (Collection<? extends Object> collection) {
            throw new UnsupportedOperationException();
        }
        
        public boolean addAllPar (Collection<? extends Object> collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean addAll (TObjectCollection collection) {
            throw new UnsupportedOperationException();
        }
        
        public boolean addAllPar (TObjectCollection collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean addAll (Object[] array) {
            throw new UnsupportedOperationException();
        }
        
        public boolean addAllPar (Object[] array, int numProcs) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Removes all of the elements of this map whose value satisfy the given
         * predicate. Errors or runtime exceptions thrown during iteration or by
         * the predicate are relayed to the caller.
         * @param filter a predicate which returns true for elements to be removed
         * @return true if any elements were removed
         */
        public boolean removeIf (Predicate<? super Object> filter) {
        	boolean modified = false;
            Objects.requireNonNull(filter);
            for (int i=0; i<_set.length; i++)	{
            	if (_states[i] == FULL)	{
            		if (filter.test(_values[i]))	{
            			_states[i] = REMOVED;
            			modified = true;
            		}
            	}
            }
            return modified;
        }
        
        private class RemoveIf1 implements Runnable	{
        	int min, max;
        	Predicate<? super Object> filter;
        	RemoveIf1 (int a, int b, Predicate<? super Object> c, int d)	{
        		min = a; max = b; filter = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				if (filter.test(_values[i]))	{
        					_states[i] = REMOVED;
        					boolGlobal = true;
        				}
        			}
        		}
        	}
        }
        /**
         * Removes all of the elements of this map whose value satisfy the given
         * predicate. Errors or runtime exceptions thrown during iteration or by
         * the predicate are relayed to the caller.
         * @param filter a predicate which returns true for elements to be removed
         * @return true if any elements were removed
         */
        public boolean removeIfPar (Predicate<? super Object> filter, int numProcs)	{
        	boolGlobal = false;
        	int numElemProc = _set.length/numProcs;
        	threads = new Thread[numProcs];
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        		threads[i] = new Thread (new RemoveIf1 (numMin, numMax, filter, i));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
        	}
        	return boolGlobal;
        }

        private class RetainAll1 implements Runnable	{
        	int min, max;
        	Object[] array;
        	RetainAll1 (int a, int b, Object[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed=0;
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				if (Arrays.binarySearch(array, (Object)_values[i]) < 0)	{
        					_states[i] = REMOVED;
        					removed++;
        				}
        			}
        		}
				atIntGlobal.addAndGet(removed);
        	}
        }
        /** {@inheritDoc} */
        public boolean retainAllPar (Collection<?> collection, int numProcs)	{
        	atIntGlobal = new AtomicInteger(0);
        	int oldSize = _size;
        	int numElemProc = _values.length/numProcs;
        	threads = new Thread[numProcs];
        	Object[] array = collection.toArray();
        	Arrays.sort(array);
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
        		threads[i] = new Thread (new RetainAll1 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }
        /** {@inheritDoc} */
        public boolean retainAll (Collection<?> collection) {
            boolean modified = false;
            TObjectIterator iter = iterator();
            while (iter.hasNext()) {
                //if (!collection.contains(Object.valueOf(iter.next()))) {
            	if (!collection.contains(iter.next()))	{
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }

        private class RetainAll2 implements Runnable	{
        	int min, max;
        	Object[] array;
        	RetainAll2 (int a, int b, Object[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed=0;
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				if (Arrays.binarySearch(array, _values[i]) < 0)	{
        					_states[i] = REMOVED;
        					removed++;
        				}
        			}
        		}
				atIntGlobal.addAndGet(removed);
        	}
        }
        /** {@inheritDoc} */
        public boolean retainAllPar (TObjectCollection collection, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	int numElemProc = _values.length/numProcs;
        	Object[] array = collection.toArrayPar(numProcs);
        	Arrays.sort(array);
        	threads = new Thread[numProcs];
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
        		threads[i] = new Thread (new RetainAll2 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }

        /** {@inheritDoc} */
        public boolean retainAll (TObjectCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TObjectIterator iter = iterator();
            while (iter.hasNext()) {
                if (!collection.contains(iter.next())) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }

        /** {@inheritDoc} */
        public boolean retainAllPar (Object[] array, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	Arrays.sort(array);
        	int numElemProc = _values.length/numProcs;
        	int numMin, numMax;
        	threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
        		threads[i] = new Thread (new RetainAll2 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }
        /** {@inheritDoc} */
        public boolean retainAll (Object[] array) {
            boolean changed = false;
            Arrays.sort (array);
            Object[] values = _values;
            byte[] states = _states;

            for (int i = values.length; i-->0;) {
                if (states[i] == FULL && (Arrays.binarySearch(array, values[i]) < 0)) {
                    removeAt(i);
                    changed = true;
                }
            }
            return changed;
        }

        private class RemoveAll1 implements Runnable	{
        	int min, max;
        	Object[] array;
        	RemoveAll1 (int a, int b, Object[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed=0;
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				if (Arrays.binarySearch(array, (Object)_values[i]) >= 0)	{
        					_states[i] = REMOVED;
        					removed++;
        				}
        			}
        		}
				atIntGlobal.addAndGet(removed);
        	}
        }
        /** {@inheritDoc} */
        public boolean removeAllPar (Collection<?> collection, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	int numElemProc = _values.length/numProcs;
        	threads = new Thread[numProcs];
        	Object[] array = collection.toArray();
        	Arrays.sort(array);
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
        		threads[i] = new Thread (new RemoveAll1 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }
        /** {@inheritDoc} */
        public boolean removeAll (Collection<?> collection) {
            boolean changed = false;
            for (Object element : collection) {
                if (element instanceof Object) {
                    //Object c = ((Object)element).ObjectValue();
                    if (remove(element)) {
                        changed = true;
                    }
                }
            }
            return changed;
        }
        
        private class RemoveAll2 implements Runnable	{
        	int min, max;
        	Object[] array;
        	RemoveAll2 (int a, int b, Object[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed=0;
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				if (Arrays.binarySearch(array, _values[i]) >= 0)	{
        					_states[i] = REMOVED;
        					removed++;
        				}
        			}
        		}
				atIntGlobal.addAndGet(removed);
        	}
        }
        /** {@inheritDoc} */
        public boolean removeAllPar (TObjectCollection collection, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	int numElemProc = _values.length/numProcs;
        	Object[] array = collection.toArrayPar(numProcs);
        	Arrays.sort(array);
        	threads = new Thread[numProcs];
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
        		threads[i] = new Thread (new RemoveAll2 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }
        /** {@inheritDoc} */
        public boolean removeAll (TObjectCollection collection) {
            if (this == collection) {
                clear();
                return true;
            }
            boolean changed = false;
            TObjectIterator iter = collection.iterator();
            while (iter.hasNext()) {
                Object element = iter.next();
                if (remove(element)) {
                    changed = true;
                }
            }
            return changed;
        }

        /** {@inheritDoc} */
        public boolean removeAll (Object[] array) {
            boolean changed = false;
            for (int i = array.length; i-- >0;) {
                if (remove(array[i])) {
                    changed = true;
                }
            }
            return changed;
        }
        /** {@inheritDoc} */
        public boolean removeAllPar (Object[] array, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	Arrays.sort(array);
        	int numElemProc = _values.length/numProcs;
        	int numMin, numMax;
        	threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
        		threads[i] = new Thread (new RemoveAll2 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	_size -= atIntGlobal.get();
        	return (oldSize > _size);
        }


        /** {@inheritDoc} */
        public void clear() {
            TByteObjectHashMap.this.clear();
        }


        /** {@inheritDoc} */
        public void forEach (Consumer<? super Object> action)	{
        	TByteObjectHashMap.this.forEachValue (action);
        }
        /** {@inheritDoc} */
        public void forEachPar (Consumer<? super Object> action, int numProcs)	{
        	TByteObjectHashMap.this.forEachValuePar (action, numProcs);
        }

        /**public String toStringPar (int numProcs) {
            final StringBuilder buf = new StringBuilder( "{" );
            forEachValuePar( new TIntProcedure() {
                private boolean first = true;

                public boolean execute( int value ) {
                    if ( first ) {
                        first = false;
                    } else {
                        buf.append( ", " );
                    }

                    buf.append( value );
                    return true;
                }
            }, numProcs);
            buf.append( "}" );
            return buf.toString();
        }*/
        private class ToString1 implements Runnable	{
        	int min, max, idProc;
        	ToString1 (int a, int b, int c)	{
        		min = a; max = b; idProc = c;
        	}
        	public void run ()	{
        		StringBuilder sbLocal = new StringBuilder();
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				sbLocal.append (", ");
        				sbLocal.append(_values[i]);
        			}
        		}
        		if (idProc > 0)
        			while (intsGlobal2[idProc-1] == -1)
        				System.out.print ("");
        		sbGlobal.append(sbLocal);
        		intsGlobal2[idProc] = 0;
        	}
        }
        /** {@inheritDoc} */
        public String toStringPar (int numProcs)	{
        	sbGlobal = new StringBuilder();
        	sbGlobal.append('[');
        	intsGlobal2 = new int[numProcs];
        	int first = 0;
        	if (_size > 0)	{
        		while (_states[first] != FULL)	first++;
        		sbGlobal.append(_values[first]);
        	}
        	int numElemProc = _values.length/numProcs;
        	int numMin, numMax;
        	threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, first+1);
        		numMax = limiteMax (i, numElemProc, first+1, numProcs, _values.length);
        		threads[i] = new Thread (new ToString1 (numMin, numMax, i));
        		intsGlobal2[i] = -1;
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	sbGlobal.append(']');
        	return sbGlobal.toString();
        }
        /** {@inheritDoc} */
        @Override
        public String toString() {
        	final StringBuilder buf = new StringBuilder ("[");
        	int first = 0;
        	if (_size > 0)	{
        		while (_states[first] != FULL)	first++;
        		sbGlobal.append(_values[first]);
        	}
        	for (int i=first; i<_values.length; i++)	{
        		if (_states[i] == FULL)	{
        			buf.append (", ");
        			buf.append(_values[i]);
        		}
        	}
            buf.append (']');
            return buf.toString();
        }
    }


    class TByteObjectKeyHashIterator extends THashPrimitiveIterator implements TByteIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveHash</tt> we will be iterating over.
         */
        TByteObjectKeyHashIterator (TPrimitiveHash hash) {
            super(hash);
        }

        /** {@inheritDoc} */
        public byte next() {
            moveToNextIndex();
            return _set[_index];
        }

        /** @{inheritDoc} */
        public void remove() {
            if (_expectedSize != _hash.size()) {
                throw new ConcurrentModificationException();
            }

            // Disable auto compaction during the remove. This is a workaround for bug 1642768.
            try {
                _hash.tempDisableAutoCompaction();
                TByteObjectHashMap.this.removeAt(_index);
            }
            finally {
                _hash.reenableAutoCompaction(false);
            }

            _expectedSize--;
        }
    }
    
    
    
    
    
    /**
     * initializes the hashtable to a prime capacity which is at least
     * <tt>initialCapacity + 1</tt>.
     *
     * @param initialCapacity an <code>int</code> value
     * @return the actual capacity chosen
     */
    protected int setUp(int initialCapacity) {
        int capacity;

        capacity = super.setUp(initialCapacity);
        _values = new Object[capacity];
        return capacity;
    }
    
    
    private class Rehash1 implements Runnable	{
    	int min, max;
    	byte[] oldKeys;
    	Object[] oldValues;
    	byte[] oldStates;
    	Rehash1 (int a, int b, byte[] oldK, Object[] oldV, byte[] oldS){
    		min=a; max=b; oldKeys=oldK; oldValues=oldV; oldStates=oldS;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
                if(oldStates[i] == FULL) {
                    byte o = oldKeys[i];
                    int index = insertKeyPar(o);
                    _values[index] = oldValues[i];
                }
            }
    	}
    }
    /**
     * rehashes the map to the new capacity.
     *
     * @param newCapacity number of elements of the new map
     * @numProcs number of processors that will be used
     */
    protected void rehashPar (int newCapacity, int numProcs)	{
    	int oldCapacity = _set.length;
    	byte oldKeys[] = _set;
    	Object oldValues[] = _values;
    	byte oldStates[] = _states;
    	
    	_set = new byte[newCapacity];
    	_values = new Object[newCapacity];
    	_states = new byte[newCapacity];
    	int numElemProc = oldCapacity/numProcs;
    	int numMin, numMax;
        threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, oldCapacity);
    		threads[i] = new Thread (new Rehash1 (numMin, numMax, oldKeys, oldValues, oldStates));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }

    /**
     * rehashes the map to the new capacity.
     *
     * @param newCapacity an <code>int</code> value
     */
    /** {@inheritDoc} */
    protected void rehash(int newCapacity) {
        int oldCapacity = _set.length;
        
        byte oldKeys[] = _set;
        Object oldVals[] = _values;
        byte oldStates[] = _states;

        _set = new byte[newCapacity];
        _values = new Object[newCapacity];
        _states = new byte[newCapacity];

        for (int i = oldCapacity; i-- > 0;) {
            if(oldStates[i] == FULL) {
                byte o = oldKeys[i];
                int index = insertKey(o);
                _values[index] = oldVals[i];
            }
        }
    }    	

    void reinitialize() {
        /**_set = null;
        _values = null;
        _states = null;*/
    	_set = new byte[0];
    	_values = new Object[0];
    	_states = new byte[0];
        //threshold = 0;
        _size = 0;
    }

    private class Keys1 implements Runnable	{
    	int min, max, idProc;
    	Keys1 (int a, int b, int c)	{
    		min=a; max=b; idProc=c;
    	}
    	public void run()	{
        	byte[] bytesLocal = new byte[max-min];
    		int j=0;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL) {
    				bytesLocal[j++] = _set[i];
    			}
    		}
    		if (idProc > 0)	{
        		while (intsGlobal2[idProc-1] == -1)	{
    				System.out.print("");
    			}
    			int size = intGlobal;
    			atIntGlobal.addAndGet(j);
        		intsGlobal2[idProc] = j;
        		if (j>0)
    			System.arraycopy(bytesLocal, 0, bytesGlobal, size, j);
        	}
    		else 	{
    			atIntGlobal.addAndGet(j);
        		intsGlobal2[idProc] = j;
    			System.arraycopy(bytesLocal, 0, bytesGlobal, 0, j);
    		}
    	}
    }
    protected byte[] keysPar(int numProcs)	{
    	int numElemProc = _set.length/numProcs;
    	atIntGlobal = new AtomicInteger(0);
    	bytesGlobal = new byte[size()];
    	intsGlobal2 = new int[numProcs];
    	int numMin, numMax;
        threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
    		threads[i] = new Thread (new Keys1 (numMin, numMax, i));
    		intsGlobal2[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return bytesGlobal;
    }
    /** {@inheritDoc} */
    protected byte[] keys() {
        byte[] keys = new byte[size()];
        byte[] k = _set;
        byte[] states = _states;

        for (int i = k.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            keys[j++] = k[i];
          }
        }
        return keys;
    }
    /** {@inheritDoc} */
    protected byte[] keysPar (byte[] array, int numProcs) {
        int size = size();
        if (array.length < size()) {
            array = new byte[size];
        }
        int numElemProc = _set.length/numProcs;
    	intGlobal = 0;
    	bytesGlobal = new byte[size()];
    	intsGlobal2 = new int[numProcs];
    	int numMin, numMax;
        threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
    		threads[i] = new Thread (new Keys1 (numMin, numMax, i));
    		intsGlobal2[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	//array = intsGlobal;
    	System.arraycopy(bytesGlobal, 0, array, 0, intGlobal);
    	return bytesGlobal;
    }

    /** {@inheritDoc} */
    protected byte[] keys (byte[] array) {
        int size = size();
        if (array.length < size) {
            array = new byte[size];
        }

        byte[] keys = _set;
        byte[] states = _states;

        for (int i = keys.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            array[j++] = keys[i];
          }
        }
        return array;
    }

    /** {@inheritDoc} */
    protected Object[] toArrayValues() {
        Object[] vals = new Object[size()];
        Object[] v = _values;
        byte[] states = _states;

        for (int i = v.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            vals[j++] = v[i];
          }
        }
        return vals;
    }
    private class ToArrayValues1 implements Runnable	{
    	int min, max, idProc;
    	ToArrayValues1 (int a, int b, int c)	{
    		min=a; max=b; idProc = c;
    	}
    	public void run()	{
    		Object[] ObjectsLocal = new Object[max-min];
    		int j=0;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL) {
    				ObjectsLocal[j++] = _values[i];
    			}
    		}
    		atIntGlobal.addAndGet(j);
    		if (idProc > 0)	{
    			//System.out.println ("En el procesador " + idProc);
    			while (intsGlobal2[idProc-1] == -1)	{
    				System.out.print("");
    			}
        		intsGlobal2[idProc] = j;
    			int k = 0;
    			for (int l=0; k<idProc; l++)	k += intsGlobal2[l];
    			System.arraycopy(ObjectsLocal, 0, ObjectsGlobal, k, j);
    		}
    		else	{
    			//System.out.println ("Procesador 0");
        		intsGlobal2[idProc] = j;
    			System.arraycopy(ObjectsLocal, 0, ObjectsGlobal, 0, j);
    			//System.out.println ("Ha acabado el procesador 0");
    		}
    	}
    }
    /** {@inheritDoc} */
    protected Object[] toArrayValuesPar (int numProcs)	{
    	atIntGlobal = new AtomicInteger(0);
    	ObjectsGlobal = new Object[size()];
    	intsGlobal2 = new int[numProcs];
    	//System.out.println ("Tamaño : " + size());
    	int numElemProc = _values.length/numProcs;
    	int numMin, numMax;
        threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
    		//System.out.println ("Mínimo: " + numMin);
    		//System.out.println ("Máximo: " + numMax);
    		threads[i] = new Thread (new ToArrayValues1 (numMin, numMax, i));
    		intsGlobal2[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	return ObjectsGlobal;
    }
    /** {@inheritDoc} */
    protected Object[] toArrayValuesPar (Object[] array, int numProcs)	{
    	if (array.length < size())
    		array = new Object[size()];
    	ObjectsGlobal = new Object[_size];
    	intsGlobal2 = new int[numProcs];
    	intGlobal = 0;
    	int numElemProc = _values.length/numProcs;
    	int numMin, numMax;
        threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
    		threads[i] = new Thread (new ToArrayValues1 (numMin, numMax, i));
    		intsGlobal2[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	//System.out.print ("intsGlobal: ");
		//for (int i=0; i<intsGlobal.length; i++)	{
		//	System.out.print (", " + intsGlobal[i]);
		//}
		//System.out.println();
		//System.out.println ("Tamaño: " + intGlobal);
		//array = intsGlobal;
    	System.arraycopy(ObjectsGlobal, 0, array, 0, intGlobal);
    	//System.out.print ("array dest: ");
		//for (int i=0; i<array.length; i++)	{
		//	System.out.print (", " + array[i]);
		//}
		//System.out.println();
    	return array;
    }
    /** {@inheritDoc} */
    protected Object[] toArrayValues (Object[] array) {
        int size = size();
        if (array.length < size) {
            array = new Object[size];
        }

        Object[] v = _values;
        byte[] states = _states;

        for (int i = v.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            array[j++] = v[i];
          }
        }
        return array;
    }
    /**public int[] values2( int[] array ) {
        int size = size();
        if ( array.length < size ) {
            array = new int[size];
        }
        System.arraycopy(_values, 0, array, 0, _values.length);
        return array;
    }*/
    

    




    public TByteObjectIterator iterator() {
        return new TByteObjectHashIterator(this);
    }


    /** {@inheritDoc} */
    protected void forEachKey (Consumer<? super Byte> action)	{
    	for (int i=0; i<_set.length; i++)	{
    		if (_states[i] == FULL)
    			action.accept(_set[i]);
    	}
    }
    private class ForEachKey1 implements Runnable	{
    	int min, max;
    	Consumer<? super Byte> action;
    	ForEachKey1 (int a, int b, Consumer<? super Byte> c)	{
    		min = a; max = b; action = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)
    			if (_states[i] == FULL)
    				action.accept(_set[i]);
    	}
    }
    /** {@inheritDoc} */
    protected void forEachKeyPar (Consumer<? super Byte> action, int numProcs)	{
    	int numElemProc = _set.length/numProcs;
    	int numMin, numMax;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
    		threads[i] = new Thread (new ForEachKey1 (numMin, numMax, action));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    }

    private class ForEachValue1 implements Runnable	{
    	int min, max;
    	Consumer<? super Object> action;
    	ForEachValue1 (int a, int b, Consumer<? super Object> c)	{
    		min = a; max = b; action = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			if (_states[i] == FULL)
    				action.accept(_values[i]);
    		}
    	}
    }
    /** {@inheritDoc} */
    protected void forEachValuePar (Consumer<? super Object> action, int numProcs)	{
    	if (action == null)
    		throw new NullPointerException();
    	if (_size > 0)	{
    		int numElemProc = _values.length/numProcs;
    		threads = new Thread[numProcs];
    		int numMin, numMax;
    		for (int i=0; i<numProcs; i++)	{
    			numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _values.length);
        		threads[i] = new Thread (new ForEachValue1 (numMin, numMax, action));
        		threads[i].start();
    		}
    		for (int i=0; i<numProcs; i++)	{
    			try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    /** {@inheritDoc} */
    protected void forEachValue (Consumer<? super Object> action)	{
    	if (action == null)
    		throw new NullPointerException();
    	if (_size > 0)	{
    		for (int i=0; i<_values.length; i++)	{
    			action.accept(_values[i]);
    		}
    	}
    }
     
    class TByteObjectValueHashIterator extends THashPrimitiveIterator implements TObjectIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveHash</tt> we will be iterating over.
         */
        TByteObjectValueHashIterator (TPrimitiveHash hash) {
            super(hash);
        }

        /** {@inheritDoc} */
        public Object next() {
            moveToNextIndex();
            return _values[_index];
        }

        /** @{inheritDoc} */
        public void remove() {
            if (_expectedSize != _hash.size()) {
                throw new ConcurrentModificationException();
            }

            // Disable auto compaction during the remove. This is a workaround for bug 1642768.
            try {
                _hash.tempDisableAutoCompaction();
                TByteObjectHashMap.this.removeAt(_index);
            }
            finally {
                _hash.reenableAutoCompaction(false);
            }

            _expectedSize--;
        }
    }


    class TByteObjectHashIterator extends THashPrimitiveIterator implements TByteObjectIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param map the <tt>TByteObjectHashMap</tt> we will be iterating over.
         */
        TByteObjectHashIterator (TByteObjectHashMap map) {
            super(map);
        }

        /** {@inheritDoc} */
        public void advance() {
            moveToNextIndex();
        }

        /** {@inheritDoc} */
        public byte key() {
            return _set[_index];
        }

        /** {@inheritDoc} */
        public Object value() {
            return _values[_index];
        }

        /** {@inheritDoc} */
        public Object setValue(Object val) {
            Object old = value();
            _values[_index] = val;
            return old;
        }

        /** @{inheritDoc} */
        public void remove() {
            if (_expectedSize != _hash.size()) {
                throw new ConcurrentModificationException();
            }
            // Disable auto compaction during the remove. This is a workaround for bug 1642768.
            try {
                _hash.tempDisableAutoCompaction();
                TByteObjectHashMap.this.removeAt(_index);
            }
            finally {
                _hash.reenableAutoCompaction(false);
            }
            _expectedSize--;
        }
    }

    public byte lastKey ()	{
    	for (int i=_set.length-1; i>=0; i--)	{
    		if (_states[i] == FULL)
    			return _set[i];
    	}
    	return no_entry_key;
    }
    
    public Object lastValue ()	{
    	for (int i=_values.length-1; i>=0; i--)	{
    		if (_states[i] == FULL)
    			return _values[i];
    	}
    	return no_entry_value;
    }
} // TIntIntHashMap