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

import PJC.collection.TFloatCollection;
import PJC.collection.TShortCollection;
import PJC.hash.TShortFloatHash;
import PJC.iterator.TFloatIterator;
import PJC.iterator.TShortIterator;
import PJC.list.ArraysPar;
import PJC.map.TShortFloatMap;
import PJC.map.hashMap.HashFunctions;
import PJC.map.iterator.TShortFloatIterator;
import PJC.set.TShortSet;
import PJC.spliterator.TFloatSpliterator;
import PJC.spliterator.TShortSpliterator;

public class TShortFloatHashMap extends TShortFloatHash implements TShortFloatMap, Serializable, Cloneable {
    static final long serialVersionUID = 1L;

    /** the values of the map */
    protected transient float[] _values;
    
    /**boolean used for the threads to share a boolean result*/
    private boolean boolGlobal;
    
    /** int used for the threads to share a boolean result*/
    private int intGlobal;
    
    /**array of ints used for the threads to share a int[] result*/
    private short[] shortsGlobal;
    private float[] floatsGlobal;
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
     * Creates a new <code>TShortFloatHashMap</code> instance with the default
     * capacity and load factor.
     */
    public TShortFloatHashMap() {
        super();
    }


    /**
     * Creates a new <code>TShortFloatHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TShortFloatHashMap (int initialCapacity) {
        super(initialCapacity);
    }


    /**
     * Creates a new <code>TShortFloatHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     */
    public TShortFloatHashMap (int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }


    /**
     * Creates a new <code>TShortFloatHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     * @param noEntryKey a <code>short</code> value that represents
     *                   <tt>null</tt> for the Key set.
     * @param noEntryValue a <code>float</code> value that represents
     *                   <tt>null</tt> for the Value set.
     */
    public TShortFloatHashMap (int initialCapacity, float loadFactor,
        short noEntryKey, float noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }


    /**
     * Creates a new <code>TShortFloatHashMap</code> instance containing
     * all of the entries in the map passed in.
     *
     * @param keys a <tt>short</tt> array containing the keys for the matching values.
     * @param values a <tt>float</tt> array containing the values.
     */
    public TShortFloatHashMap (short[] keys, float[] values) {
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
    public TShortFloatHashMap (short[] keys, float[] values, int numProcs) {
        super(Math.max(keys.length, values.length));

        //int size = Math.min( keys.length, values.length );
        /**for ( int i = 0; i < size; i++ ) {
            this.put( keys[i], values[i] );
        }*/
        this.putAllPar(keys, values, numProcs);
    }

    /**
     * Creates a new <code>TShortFloatHashMap</code> instance containing
     * all of the entries in the map passed in.
     *
     * @param map a <tt>TShortFloatMap</tt> that will be duplicated.
     */
    public TShortFloatHashMap (TShortFloatMap map) {
        super(map.size());
        if (map instanceof TShortFloatHashMap) {
            TShortFloatHashMap hashmap = (TShortFloatHashMap) map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != (int) 0) {
                Arrays.fill(_set, this.no_entry_key);
            }
            if (this.no_entry_value != (int) 0) {
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
    public TShortFloatHashMap(TShortFloatMap map, int numProcs) {
        super(map.size());
        if (map instanceof TShortFloatHashMap) {
            TShortFloatHashMap hashmap = (TShortFloatHashMap) map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != (short)0) {
            	ArraysPar.fillPar(_set, this.no_entry_key, numProcs);
            }
            if (this.no_entry_value != (float)0) {
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
    public TShortFloatHashMap clone() {
        TShortFloatHashMap result;
        try {
            result = (TShortFloatHashMap)super.clone();
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
    public TShortFloatHashMap clonePar(int numProcs) {
        TShortFloatHashMap result;
        try {
            result = (TShortFloatHashMap)super.clone();
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
    public float compute (short key,
    			BiFunction<? super Short, ? super Float, ? extends Float> remappingFunction)	{
    	int index = index (key);
    	Float value = null;
    	if (index > 0)	value = remappingFunction.apply((Short)key, (Float)_values[index]);
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
    public float comptueIfAbsent (short key, Function<? super Short,
    		? extends Float> remappingFunction)	{
    	int index = index(key);
    	Float value = remappingFunction.apply(key);
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
    public float computeIfPresent (short key,
    				BiFunction<? super Short, ? super Float, ? extends Float> remappingFunction)	{
    	if (remappingFunction == null)
    		throw new NullPointerException(); 
    	int index = index (key);
    	
    	if ((index> 0) && (_values[index] != no_entry_value))	{
    		float value = remappingFunction.apply(key, _values[index]);
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
    public boolean containsKey (short key) {
        return contains (key);
    }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     */
    public boolean containsValue (float value) {
        byte[] states = _states;
        float[] vals = _values;

        for (int i=0; i<_values.length; i++) {
            if (states[i] == FULL && value == vals[i]) {
                return true;
            }
        }
        return false;
    }
    
    private class ContainsValue1 implements Runnable	{
    	private int min, max;
    	private float val;
    	ContainsValue1 (float value, int a, int b)	{
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
    public boolean containsValuePar (float value, int numProcs){
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
    	TShortFloatMap that;
    	int min, max;
    	Equals1 (TShortFloatMap obj, int a, int b)	{
    		that=obj; min=a; max=b;
    	}
    	public void run ()	{
            float this_no_entry_value = getNoEntryValue();
            float that_no_entry_value = that.getNoEntryValue();
    		for (int i=min; i<max; i++) {
                if (_states[i] == FULL) {
                    short key = _set[i];
                    float that_value = that.get(key);
                    float this_value = _values[i];
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
            float this_no_entry_value = getNoEntryValue();
            //int that_no_entry_value = that.getNoEntryValue();
    		for (int i=min; i<max; i++) {
                if (_states[i] == FULL) {
                    short key = _set[i];
                    //int that_value = that.get( key );
                    Object that_value = that.get(key);
                    if (!(that_value instanceof Float))	{
                    	boolGlobal = false;
                    	return;
                    }
                    float this_value = _values[i];
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
    	if (!((other instanceof TShortFloatMap)||(other instanceof Map<?,?>)))	{
    		return false;
    	}
		boolGlobal = true;
    	if (other instanceof TShortFloatMap)	{
    		TShortFloatMap that = (TShortFloatMap) other;
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
        if (!(other instanceof TShortFloatMap)) {
            return false;
        }
        TShortFloatMap that = (TShortFloatMap) other;
        if (that.size() != this.size()) {
            return false;
        }
        float[] values = _values;
        byte[] states = _states;
        float this_no_entry_value = getNoEntryValue();
        float that_no_entry_value = that.getNoEntryValue();
        for (int i = values.length; i-- > 0;) {
            if (states[i] == FULL) {
                short key = _set[i];
                float that_value = that.get(key);
                float this_value = values[i];
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
    public void forEach(BiConsumer<? super Short, ? super Float> action) {
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
    	BiConsumer<? super Short, ? super Float> action;
    	ForEachPar1 (int a, int b, BiConsumer<? super Short, ? super Float> c)	{
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
    public void forEachPar (BiConsumer<? super Short, ? super Float> action, int numProcs)	{
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
    public float get (short key) {
        int index = index(key);
        return index < 0 ? no_entry_value : _values[index];
    }
    
    /**
     * Returns the value to which the specified key is mapped, or defaultValue 
     * if this map contains no mapping for the key.
     */
    public float getOrDefault (short key, float defaultValue)	{
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
    
    static class TShortFloatHashMapSpliterator {
        final TShortFloatHashMap map;
        short currentKey;				// current key
        float currentValue;			// current value
        int index;                  // current index, modified on advance/split
        int fence;                  // one past last index
        int est;                    // size estimate
        short _no_entry_key;			// int key that represents null
        float _no_entry_value;			// int value that represents null

        TShortFloatHashMapSpliterator(TShortFloatHashMap m, int origin,
                           int fence, int est, short no_entry_key, float no_entry_value) {
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
                TShortFloatHashMap m = map;
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

    static final class TShortFloatKeySpliterator
        extends TShortFloatHashMapSpliterator
        implements TShortSpliterator {
        TShortFloatKeySpliterator(TShortFloatHashMap m, int origin, int fence, int est, short no_entry, float no_value) {
            super(m, origin, fence, est, no_entry, no_value);
        }

        public TShortFloatKeySpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || currentKey != _no_entry_key) ? null :
            	new TShortFloatKeySpliterator(map, lo, index = mid, est >>>= 1,
                _no_entry_key, _no_entry_value);
        }


        public void forEachRemaining (Consumer<? super Short> action)	{
        	int i, hi;
        	TShortFloatHashMap m;
        	short[] a;
        	if (action == null)
        		throw new NullPointerException();
        	if ((m = map) != null && (a = m._set) != null)	{
        		if ((hi = fence) < 0)
        			hi = m.size();
        		if ((i=index) <= 0 && (index=hi) <= a.length)	{
        			for (; i<hi; ++i)	{
        				short e = a[i];
        				if (m._states[i] == FULL)	{
        					action.accept(e);
        				}
        			}
        		}
        	}
        	throw new ConcurrentModificationException();
        }

        public boolean tryAdvance(Consumer<? super Short> action) {
        	if (action == null)
        		throw new NullPointerException();
        	int hi = getFence(), i = index;
        	if (i < hi)	{
        		index = i+1;
        		short e = map._set[i];
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

    static final class TShortFloatValueSpliterator
        extends TShortFloatHashMapSpliterator
        implements TFloatSpliterator {
        TShortFloatValueSpliterator(TShortFloatHashMap m, int origin, int fence, int est,
                         short no_entry_key, float no_entry_value) {
            super(m, origin, fence, est, no_entry_key, no_entry_value);
        }

        public TShortFloatValueSpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || currentValue != _no_entry_value) ? null :
                new TShortFloatValueSpliterator(map, lo, index = mid, est >>>= 1,
                                          _no_entry_key, _no_entry_value);
        }

        public void forEachRemaining (Consumer<? super Float> action)	{
        	int i, hi;
        	TShortFloatHashMap m;
        	float[] a;
        	if (action == null)
        		throw new NullPointerException();
        	if ((m = map) != null && (a = m._values) != null)	{
        		if ((hi = fence) < 0)
        			hi = m.size();
        		if ((i=index) <= 0 && (index=hi) <= a.length)	{
        			for (; i<hi; ++i)	{
        				float e = a[i];
        				if (m._values[i] == FULL)	{
        					action.accept(e);
        				}
        			}
        		}
        	}
        	throw new ConcurrentModificationException();
        }

        public boolean tryAdvance(Consumer<? super Float> action) {
        	if (action == null)
        		throw new NullPointerException();
        	int hi = getFence(), i = index;
        	if (i < hi)	{
        		index = i+1;
        		float e = map._values[i];
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
    public TShortSet keySet() {
        return new TKeyView();
    }
    
    /** a view onto the keys of the map. */
    protected class TKeyView implements TShortSet {

        /** {@inheritDoc} */
        public TShortIterator iterator() {
            return new TShortFloatKeyHashIterator (TShortFloatHashMap.this);
        }
        
        /** {@inheritDoc} */
        public TShortSpliterator spliterator() {
        	return new TShortFloatKeySpliterator(TShortFloatHashMap.this, 0, -1, 0, no_entry_key, no_entry_value);
        }

        /** {@inheritDoc} */
        public short getNoEntryValue() {
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
        public boolean contains (short entry) {
            return TShortFloatHashMap.this.contains(entry);
        }
        public boolean containsPar (short entry, int numProcs)	{
        	return TShortFloatHashMap.this.contains(entry);
        }


        /** {@inheritDoc} */
        public short[] toArray() {
            return TShortFloatHashMap.this.keys();
        }

        /** {@inheritDoc} */
        public short[] toArrayPar (int numProcs) {
            return TShortFloatHashMap.this.keysPar(numProcs);
        }

        /** {@inheritDoc} */
        public short[] toArray (short[] dest) {
            return TShortFloatHashMap.this.keys(dest);
        }
        public short[] toArrayPar (short[] dest, int numProcs)	{
        	return TShortFloatHashMap.this.keysPar(dest, numProcs);
        }


        /**
         * Unsupported when operating upon a Key Set view of a TShortFloatMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean add (short entry) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean remove (short entry) {
            return no_entry_value != TShortFloatHashMap.this.remove(entry);
        }
        /** {@inheritDoc} */
        public boolean removePar (short entry, int numProcs)	{
        	return no_entry_value != TShortFloatHashMap.this.remove (entry);
        }

        private class ContainsAll1 implements Runnable	{
        	int min, max;
        	Short[] array;
        	ContainsAll1 (int a, int b, Short[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (array[i] instanceof Short) {
                        short ele = ((Short)array[i]).shortValue();
                        if (!TShortFloatHashMap.this.containsKey(ele)) {
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
        	Short[] array = collection.toArray(new Short [collection.size()]);
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
                if (element instanceof Short) {
                    short ele = ((Short) element).shortValue();
                    if (!TShortFloatHashMap.this.containsKey(ele)) {
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
        	short[] array;
        	ContainsAll2 (int a, int b, short[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (!TShortFloatHashMap.this.containsKey (array[i])) {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAllPar (TShortCollection collection, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	short[] array = collection.toArray (new short[collection.size()]);
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
        public boolean containsAll (TShortCollection collection) {
            TShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (!TShortFloatHashMap.this.containsKey(iter.next())) {
                    return false;
                }
            }
            return true;
        }

        private class ContainsAll3 implements Runnable	{
        	int min, max;
        	short[] array;
        	ContainsAll3 (int a, int b, short[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (!TShortFloatHashMap.this.contains(array[i])) {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAllPar (short[] array, int numProcs)	{
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
        public boolean containsAll (short[] array) {
            for (short element : array) {
                if (!TShortFloatHashMap.this.contains(element)) {
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
        public boolean addAll (Collection<? extends Short> collection) {
            throw new UnsupportedOperationException();
        }
        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAllPar (Collection<? extends Short> collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAll (TShortCollection collection) {
            throw new UnsupportedOperationException();
        }
        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAllPar (TShortCollection collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAll (short[] array) {
            throw new UnsupportedOperationException();
        }
        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAllPar (short[] array, int numProcs) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Removes all of the elements of this map whose key satisfy the given
         * predicate. Errors or runtime exceptions thrown during iteration or by
         * the predicate are relayed to the caller.
         * @param filter a predicate which returns true for elements to be removed
         * @return true if any elements were removed
         */
        public boolean removeIf (Predicate<? super Short> filter) {
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
        	Predicate<? super Short> filter;
        	RemoveIf1 (int a, int b, Predicate<? super Short> c, int d)	{
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
        public boolean removeIfPar (Predicate<? super Short> filter, int numProcs)	{
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
                    	if (Arrays.binarySearch(array, (Short)_set[i]) < 0)	{
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
            TShortIterator iter = iterator();
            while (iter.hasNext()) {
                if (!collection.contains(Short.valueOf(iter.next()))) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        private class RetainAll2 implements Runnable	{
        	int min, max;
        	short[] array;
        	RetainAll2 (int a, int b, short[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed = 0;
        		for (int i=min; i<max; i++) {
        			//if (_states[i] == FULL)	{
        			int pos = TShortFloatHashMap.this.index(array[i]);
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
        public boolean retainAllPar (TShortCollection collection, int numProcs)	{
        	if (this == collection)	return false;
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	short[] array = collection.toArrayPar (numProcs);
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
        public boolean retainAll (TShortCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TShortIterator iter = iterator();
            while (iter.hasNext()) {
                if (!collection.contains(iter.next())) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }

        /** {@inheritDoc} */
        public boolean retainAllPar (short[] array, int numProcs)	{
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
        public boolean retainAll (short[] array) {
            boolean changed = false;
            Arrays.sort(array);
            short[] set = _set;
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
                    	if (Arrays.binarySearch(array, (Short)_set[i]) >= 0)	{
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
        				int pos = TShortFloatHashMap.this.index((Short)array[i]);
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
                if (element instanceof Short) {
                    short c = ((Short) element).shortValue();
                    if (remove(c)) {
                        changed = true;
                    }
                }
            }
            return changed;
        }

        /** {@inheritDoc} */
        public boolean removeAll (TShortCollection collection) {
            if (this == collection) {
                clear();
                return true;
            }
            boolean changed = false;
            TShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                short element = iter.next();
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
        public boolean removeAllPar (TShortCollection collection, int numProcs)	{
        	if (this == collection)	{
        		clear();
        		return true;
        	}
        	atIntGlobal = new AtomicInteger(0);
        	int oldSize = _size;
        	short[] array = collection.toArrayPar (numProcs);
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
        public boolean removeAll (short[] array) {
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
        	short[] array;
        	RemoveAll3 (int a, int b, short[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed = 0;
        		for (int i=min; i<max; i++) {
        			if (_states[i] == FULL)	{
        				//if (Arrays.binarySearch(array, _set[i]) >= 0)	{
        				int pos = TShortFloatHashMap.this.index(array[i]);
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
        public boolean removeAllPar (short[] array, int numProcs)	{
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
            TShortFloatHashMap.this.clear();
        }
        /** {@inheritDoc} */
        public void clearPar(int numProcs)	{
        	TShortFloatHashMap.this.clearPar(numProcs);
        }


        /** {@inheritDoc} */
        public void forEach (Consumer<? super Short> action)	{
        	TShortFloatHashMap.this.forEachKey (action);
        }
        /** {@inheritDoc} */
        public void forEachPar (Consumer<? super Short> action, int numProcs)	{
        	TShortFloatHashMap.this.forEachKeyPar (action, numProcs);
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
        	TShortSet that;
        	Equals1 (int a, int b, TShortSet c)	{
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
        	if (o instanceof TShortSet)	{
        		TShortSet that = (TShortSet) o;
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
            if (!(other instanceof TShortSet)) {
                return false;
            }
            final TShortSet that = (TShortSet) other;
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
    public float merge (short key, float value, BiFunction<? super Short, ? super Float,
    		? extends Float> remappingFunction)	{
    	if (value == no_entry_value)
    		throw new NullPointerException();
    	if (remappingFunction == null)
    		throw new NullPointerException();
    	int index = index (key);
    	if ((index < 0) || (_values[index] == no_entry_value))	{
    		put (key, value);
    		return value;
    	}
    	Float newValue = remappingFunction.apply (_set[index], value);
    	if (newValue != null)	{
    		put (key, newValue);
    		return newValue;
    	}
    	removeAt (index);
    	return no_entry_value;
    }
    
    /** {@inheritDoc} */
    public float put (short key, float value) {
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
     public float putPar (short key, float value) {
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
    private float doPut (short key, float value, int index) {
        float previous = no_entry_value;
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
     public float putParAll (short key, float value) {
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
    private float doPutAll(short key, float value, int index) {
        float previous = no_entry_value;
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
    public void putAll (Map<? extends Short, ? extends Float> map) {
        ensureCapacity(map.size());
        // could optimize this for cases when map instanceof THashMap
        for (Map.Entry<? extends Short, ? extends Float> entry : map.entrySet()) {
            this.put(entry.getKey().shortValue(), entry.getValue().floatValue());
        }
    }
     private class PutAll1 implements Runnable	{
     	int min, max;
     	Entry<?,?>[] entradas;
     	PutAll1 (int a, int b, Entry<?, ?>[] e)	{
     		min=a; max=b; entradas = e;
     	}
     	public void run ()	{
     		short clave;
     		float valor;
     		for (int i=min; i<max; i++)	{
     			if (entradas[i].getKey() instanceof Integer)	{
     				clave = ((Short) entradas[i].getKey()).shortValue();
     				valor = ((Float) entradas[i].getValue()).floatValue();
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
     public void putAllPar(Map<? extends Short, ? extends Float> map, int numProcs) {
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
     	short[] keys;
     	float[] values;
     	PutAll2 (int a, int b, short[] c, float[] d)	{
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
     public void putAll (TShortFloatMap map) {
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
     public void putAllPar (TShortFloatMap map, int numProcs) {
         ensureCapacity(map.size());
         int numMin, numMax;
         threads = new Thread[numProcs];
         short[] set = map.keySet().toArrayPar(numProcs);
         float[] values = map.values().toArrayPar(numProcs);
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
     public void putAll (short[] keys, float[] values)	{
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
     public void putAllPar(short[] keys, float[] values, int numProcs) {	
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
    public float putIfAbsent (short key, float value) {
        int index = insertKey(key);
        if (index < 0)
            return _values[-index - 1];
        return doPut(key, value, index);
    }
    
    /** {@inheritDoc} */
    public float remove(short key) {
        float prev = no_entry_value;
        int index = index(key);
        if (index >= 0) {
            prev = _values[index];
            removeAt(index);    // clear key,state; adjust size
        }
        return prev;
    }

    /** {@inheritDoc} */
    public boolean remove (short key, float value)	{
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
    public float replace (short key, float value)	{
    	int index = index (key);
    	if (index > 0)	{
    		float oldValue = _values[index];
    		_values[index] = value;
    		return oldValue;
    	}
    	return no_entry_value;
    }

    /** {@inheritDoc} */
    public boolean replace (short key, float oldValue, float newValue)	{
    	int index = index (key);
    	if ((index > 0) && (_values[index] == oldValue))	{
    		_values[index] = newValue;
    		return true;
    	}
    	return false;
    }

    /** {@inheritDoc} */
    public void replaceAll (BiFunction 
    			<? super Short, ? super Float, ? extends Float> function)	{
    	for (int i=0; i<_values.length; i++)	{
    		if (_states[i] == FULL)	{
    			float newValue = function.apply(_set[i], _values[i]);
    			_values[i] = newValue;
    		}
    	}
    }
    private class ReplaceAll1 implements Runnable	{
    	int min, max;
    	BiFunction <? super Short, ? super Float, ? extends Float> function;
    	ReplaceAll1 (int a, int b, BiFunction <? super Short, ? super Float,
			? extends Float> c)	{
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
    public void replaceAllPar (BiFunction <? super Short, ? super Float,
    			? extends Float> function, int numProcs)	{
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
    public TFloatCollection values ()	{
    	TFloatCollection c = new TValueView ();
    	return c;
    }
    /** a view onto the values of the map. */
    protected class TValueView implements TFloatCollection {

        /** {@inheritDoc} */
        public TFloatIterator iterator() {
            return new TShortFloatValueHashIterator(TShortFloatHashMap.this);
        }

        /** {@inheritDoc} */
        public TFloatSpliterator spliterator() {
        	return new TShortFloatValueSpliterator(TShortFloatHashMap.this, 0, -1, 0, no_entry_key, no_entry_value);
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
        	if (o instanceof TFloatCollection)	{
        		TFloatCollection that = (TFloatCollection) o;
        		if (this.size() != that.size())	{
        			return false;
        		}
        		threads = new Thread[numProcs];
        		boolGlobal = true;
        		int numElemProc = _values.length/numProcs;
        		int numMin, numMax;
        		float[] array = that.toArrayPar(numProcs);
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
        	TFloatCollection that;
        	Equals3 (int a, int b, TFloatCollection c)	{
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
        	float[] that;
        	Equals1 (int a, int b, float[] c)	{
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
        	if (o instanceof TFloatCollection)	{
        		TFloatCollection that = (TFloatCollection) o;
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
        	if (o instanceof TFloatCollection)	{
        		TFloatCollection that = (TFloatCollection) o;
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
        public float getNoEntryValue() {
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
        public boolean contains (float entry) {
            return TShortFloatHashMap.this.containsValue(entry);
        }
        @Override
        /** {@inheritDoc} */
        public boolean containsPar(float entry, int numProcs) {
            return TShortFloatHashMap.this.containsValuePar(entry, numProcs);
        }


        /** {@inheritDoc} */
        public float[] toArray() {
            return TShortFloatHashMap.this.toArrayValues();
        }

        /** {@inheritDoc} */
        public float[] toArrayPar(int numProcs) {
            return TShortFloatHashMap.this.toArrayValuesPar(numProcs);
        }

        /** {@inheritDoc} */
        public float[] toArray(float[] dest) {
            return TShortFloatHashMap.this.toArrayValues(dest);
        }
        /** {@inheritDoc} */
        public float[] toArrayPar(float[] dest, int numProcs) {
            return TShortFloatHashMap.this.toArrayValuesPar(dest, numProcs);
        }

        public boolean add(float entry) {
            throw new UnsupportedOperationException();
        }
        private class Remove1 implements Runnable	{
        	int min, max;
        	float entry;
        	Remove1 (int a, int b, float c)	{
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
        public boolean removePar (float entry, int numProcs)	{
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
        public boolean remove(float entry) {
            float[] values = _values;
            short[] set = _set;

            for (int i = values.length; i-- > 0;) {
                if ((set[i] != FREE && set[i] != REMOVED) && entry == values[i]) {
                    removeAt(i);
                    return true;
                }
            }
            return false;
        }

        private class ContainsAll1 implements Runnable	{
        	int min, max;
        	Object[] array;
        	float[] values;
        	ContainsAll1 (int a, int b, Object[] c, float[] d)	{
        		min = a; max = b; array = c; values = d;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (array[i] instanceof Float) {
                        float ele = ((Float) array[i]).floatValue();
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
        	float[] values = TShortFloatHashMap.this.values().toArrayPar(numProcs);
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
                if (element instanceof Float) {
                    float ele = ((Float) element).floatValue();
                    if (!TShortFloatHashMap.this.containsValue(ele)) {
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
        	float[] array, values;
        	ContainsAll2 (int a, int b, float[] c, float[] d)	{
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
        public boolean containsAllPar (TFloatCollection collection, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	float[] array = collection.toArray (new float[collection.size()]);
        	float[] values = TShortFloatHashMap.this.values().toArrayPar(numProcs);
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
        public boolean containsAll (TFloatCollection collection) {
            TFloatIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (!TShortFloatHashMap.this.containsValue(iter.next())) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        public boolean containsAllPar (float[] array, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = array.length/numProcs;
        	float[] values = TShortFloatHashMap.this.values().toArrayPar(numProcs);
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
        public boolean containsAll (float[] array) {
            for (float element : array) {
                if (!TShortFloatHashMap.this.containsValue(element)) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        public boolean addAll (Collection<? extends Float> collection) {
            throw new UnsupportedOperationException();
        }
        
        public boolean addAllPar (Collection<? extends Float> collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean addAll (TFloatCollection collection) {
            throw new UnsupportedOperationException();
        }
        
        public boolean addAllPar (TFloatCollection collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean addAll (float[] array) {
            throw new UnsupportedOperationException();
        }
        
        public boolean addAllPar (float[] array, int numProcs) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Removes all of the elements of this map whose value satisfy the given
         * predicate. Errors or runtime exceptions thrown during iteration or by
         * the predicate are relayed to the caller.
         * @param filter a predicate which returns true for elements to be removed
         * @return true if any elements were removed
         */
        public boolean removeIf (Predicate<? super Float> filter) {
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
        	Predicate<? super Float> filter;
        	RemoveIf1 (int a, int b, Predicate<? super Float> c, int d)	{
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
        public boolean removeIfPar (Predicate<? super Float> filter, int numProcs)	{
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
        				if (Arrays.binarySearch(array, (Float)_values[i]) < 0)	{
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
            TFloatIterator iter = iterator();
            while (iter.hasNext()) {
                if (!collection.contains(Float.valueOf(iter.next()))) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }

        private class RetainAll2 implements Runnable	{
        	int min, max;
        	float[] array;
        	RetainAll2 (int a, int b, float[] c)	{
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
        public boolean retainAllPar (TFloatCollection collection, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	int numElemProc = _values.length/numProcs;
        	float[] array = collection.toArrayPar(numProcs);
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
        public boolean retainAll (TFloatCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TFloatIterator iter = iterator();
            while (iter.hasNext()) {
                if (!collection.contains(iter.next())) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }

        /** {@inheritDoc} */
        public boolean retainAllPar (float[] array, int numProcs)	{
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
        public boolean retainAll (float[] array) {
            boolean changed = false;
            Arrays.sort (array);
            float[] values = _values;
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
        				if (Arrays.binarySearch(array, (Float)_values[i]) >= 0)	{
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
                if (element instanceof Float) {
                    float c = ((Float)element).floatValue();
                    if (remove(c)) {
                        changed = true;
                    }
                }
            }
            return changed;
        }
        
        private class RemoveAll2 implements Runnable	{
        	int min, max;
        	float[] array;
        	RemoveAll2 (int a, int b, float[] c)	{
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
        public boolean removeAllPar (TFloatCollection collection, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	int numElemProc = _values.length/numProcs;
        	float[] array = collection.toArrayPar(numProcs);
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
        public boolean removeAll (TFloatCollection collection) {
            if (this == collection) {
                clear();
                return true;
            }
            boolean changed = false;
            TFloatIterator iter = collection.iterator();
            while (iter.hasNext()) {
                float element = iter.next();
                if (remove(element)) {
                    changed = true;
                }
            }
            return changed;
        }

        /** {@inheritDoc} */
        public boolean removeAll (float[] array) {
            boolean changed = false;
            for (int i = array.length; i-- >0;) {
                if (remove(array[i])) {
                    changed = true;
                }
            }
            return changed;
        }
        /** {@inheritDoc} */
        public boolean removeAllPar (float[] array, int numProcs)	{
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
            TShortFloatHashMap.this.clear();
        }


        /** {@inheritDoc} */
        public void forEach (Consumer<? super Float> action)	{
        	TShortFloatHashMap.this.forEachValue (action);
        }
        /** {@inheritDoc} */
        public void forEachPar (Consumer<? super Float> action, int numProcs)	{
        	TShortFloatHashMap.this.forEachValuePar (action, numProcs);
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


    class TShortFloatKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveHash</tt> we will be iterating over.
         */
        TShortFloatKeyHashIterator (TPrimitiveHash hash) {
            super(hash);
        }

        /** {@inheritDoc} */
        public short next() {
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
                TShortFloatHashMap.this.removeAt(_index);
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
        _values = new float[capacity];
        return capacity;
    }
    
    
    private class Rehash1 implements Runnable	{
    	int min, max;
    	short[] oldKeys;
    	float[] oldValues;
    	byte[] oldStates;
    	Rehash1 (int a, int b, short[] oldK, float[] oldV, byte[] oldS){
    		min=a; max=b; oldKeys=oldK; oldValues=oldV; oldStates=oldS;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
                if(oldStates[i] == FULL) {
                    short o = oldKeys[i];
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
    	short oldKeys[] = _set;
    	float oldValues[] = _values;
    	byte oldStates[] = _states;
    	
    	_set = new short[newCapacity];
    	_values = new float[newCapacity];
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
        
        short oldKeys[] = _set;
        float oldVals[] = _values;
        byte oldStates[] = _states;

        _set = new short[newCapacity];
        _values = new float[newCapacity];
        _states = new byte[newCapacity];

        for (int i = oldCapacity; i-- > 0;) {
            if(oldStates[i] == FULL) {
                short o = oldKeys[i];
                int index = insertKey(o);
                _values[index] = oldVals[i];
            }
        }
    }    	

    void reinitialize() {
        /**_set = null;
        _values = null;
        _states = null;*/
    	_set = new short[0];
    	_values = new float[0];
    	_states = new byte[0];
        //threshold = 0;
        _size = 0;
    }

    /**public TObjectSet entrySet()	{
    	return new IIEntrySet();
    }*/
    /**static class TIntIntEntry	{
    	private int key;
    	private int value;
    	TIntIntEntry (int k, int v)	{
    		key = k;
    		value = v;
    	}
    	public final int getKey()	{
    		return key;
    	}
    	public final int getValue()	{
    		return value;
    	}
    	public final String toString()	{
    		return (key + "=" + value);
    	}
    	public final int setValue (int newValue)	{
    		int oldValue = value;
    		value = newValue;
    		return oldValue;
    	}
    	public final boolean equals (Object o)	{
    		if (o==this)
    			return true;
    		if (o instanceof TIntIntEntry)	{
    			TIntIntEntry entry = (TIntIntEntry) o;
    			if ((entry.getKey() == key) && (entry.getValue() == value))
    				return true;
    		}
    		if (o instanceof Entry<?,?>)	{
    			Entry<?,?> entry = (Entry<?,?>) o;
    			if ((entry.getKey() instanceof Integer) && (entry.getValue() instanceof Integer))	{
    				Integer _key = (Integer)entry.getKey();
    				Integer _value = (Integer)entry.getValue();
    				if ((_key.intValue() == key) && (_value.intValue() == value))	{
    					return true;
    				}
    			}
    		}
    		return false;
    	}
    }
    final class IIEntrySet	{
    	public final int size()	{
    		return _size;
    	}
    	public final void clear()	{
    		TIntIntHashMap.this.clear();
    	}
    	public final void clearPar (int numProcs)	{
    		TIntIntHashMap.this.clearPar(numProcs);
    	}
    	
    	public boolean add (Object o)	{
    		if (o instanceof Entry<?,?>)	{
    			Entry<?,?> entry = (Entry<?,?>) o;
    			return (TIntIntHashMap.this.put((Integer)entry.getKey(), (Integer)entry.getValue()) != no_entry_value);
    		}
    		if (o instanceof TIntIntEntry)	{
    			TIntIntEntry entry = (TIntIntEntry) o;
    			return (TIntIntHashMap.this.put(entry.getKey(), entry.getValue()) != no_entry_value);
    		}
    		return false;
    	}
    	
    	public boolean addAll( Collection<?> collection )	{
    		int oldSize = _size;
    		Object[] array = collection.toArray();
    		for (int i=0; i<array.length; i++)	{
    			if (array[i] instanceof Entry<?,?>)	{
    				Entry<?,?> entry = (Entry<?,?>) array[i];
    				if (entry.getKey() instanceof Integer)	{
    					Integer key = (Integer)entry.getKey();
        				if (entry.getValue() instanceof Integer)	{
        					Integer value = (Integer)entry.getValue();
        					TIntIntHashMap.this.put(key.intValue(), value.intValue());
        				}
    				}
    			}
    		}
    		return (oldSize != _size);
    	}
    	private class AddAll1 implements Runnable	{
    		int min, max;
    		Entry<?,?>[] array;
    		AddAll1 (int a, int b, Entry<?,?>[] c)	{
    			min = a; max = b; array = c;
    		}
    		public void run ()	{
    			for (int i=min; i<max; i++)	{
    				TIntIntHashMap.this.putPar ((Integer)array[i].getKey(), (Integer)array[i].getValue());
    			}
    		}
    	}
        public boolean addAllPar (Collection<?> collection, int numProcs)	{
        	int oldSize = _size;
        	Entry<?,?>[] array = collection.toArray(new Entry<?,?>[collection.size()]);
        	int numElemProc = array.length/numProcs;
        	threads = new Thread[numProcs];
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        		threads[i] = new Thread (new AddAll1 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	return (oldSize != _size);
        }
        public boolean addAll (TObjectCollection collection )	{
    		int oldSize = collection.size();
    		TIntIntEntry[] entries = (TIntIntEntry[])collection.toArray();
    		for (int i=0; i<entries.length; i++)	{
    			TIntIntHashMap.this.put(entries[i].getKey(), entries[i].getValue());
    		}
    		return (oldSize != _size);
    	}
        private class AddAll2 implements Runnable	{
        	int min, max;
        	TIntIntEntry[] array;
        	AddAll2 (int a, int b, TIntIntEntry[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run()	{
        		for (int i=min; i<max; i++)	{
        			TIntIntHashMap.this.putPar (array[i].getKey(), array[i].getValue());
        		}
        	}
        }
        public boolean addAllPar (TObjectCollection collection, int numProcs)	{
        	int oldSize = _size;
        	TIntIntEntry[] array = (TIntIntEntry[])collection.toArray();
        	int numElemProc = array.length/numProcs;
        	threads = new Thread[numProcs];
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        		threads[i] = new Thread (new AddAll2 (numMin, numMax, array));
        		threads[i].start();
        	}
        	for (int i=0; i<numProcs; i++)	{
        		try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
        	}
        	return (oldSize != _size);
        }
        public boolean addAll(Object[] array)	{
    		int oldSize = _size;
    		if (array[0] instanceof Entry<?,?>)	{
    			Entry<?,?>[] entries = (Entry<?,?>[]) array;
    			for (int i=0; i<entries.length; i++)	{
    				TIntIntHashMap.this.put ((Integer)entries[i].getKey(), (Integer)entries[i].getValue());
    			}
    		}
    		if (array[0] instanceof TIntIntEntry)	{
    			TIntIntEntry[] entries = (TIntIntEntry[]) array;
    			for (int i=0; i<entries.length; i++)	{
    				TIntIntHashMap.this.put (entries[i].getKey(), entries[i].getValue());
    			}
    		}
    		return oldSize != _size;
    	}
        public boolean addAllPar (Object[] array, int numProcs)	{
        	int oldSize = _size;
        	if (array[0] instanceof Entry<?,?>)	{
        		Entry<?,?>[] entries = (Entry<?,?>[]) array;
        		int numElemProc = array.length/numProcs;
            	threads = new Thread[numProcs];
            	int numMin, numMax;
            	for (int i=0; i<numProcs; i++)	{
            		numMin = limiteMin (i, numElemProc, 0);
            		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
            		threads[i] = new Thread (new AddAll1 (numMin, numMax, entries));
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
        	if (array[0] instanceof TIntIntEntry)	{
        		TIntIntEntry[] entries = (TIntIntEntry[]) array;
        		int numElemProc = array.length/numProcs;
            	threads = new Thread[numProcs];
            	int numMin, numMax;
            	for (int i=0; i<numProcs; i++)	{
            		numMin = limiteMin (i, numElemProc, 0);
            		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
            		threads[i] = new Thread (new AddAll2 (numMin, numMax, entries));
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
        	return oldSize != _size;
        }
        
        public boolean contains (Object o)	{
        	if (o instanceof Entry<?,?>)	{
        		Entry<?,?> entry = (Entry<?,?>) o;
        		if ((entry.getKey() instanceof Integer) &&(entry.getValue() instanceof Integer))	{
        			Integer _key = (Integer)entry.getKey();
        			Integer _value = (Integer)entry.getValue();
        			if (TIntIntHashMap.this.get(_key.intValue()) == _value.intValue())	return true;
        		}
        	}
        	if (o instanceof TIntIntEntry)	{
        		TIntIntEntry entry = (TIntIntEntry) o;
        		if (TIntIntHashMap.this.get(entry.getKey()) == entry.getValue())
        			return true;
        	}
        	return false;
        }
        public boolean containsPar (Object o, int numProcs)	{
        	return contains (o);
        }
        public boolean containsAll (Collection<?> collection)	{
        	Entry<?,?>[] array = (Entry<?,?>[])collection.toArray();
        	for (int i=0; i<array.length; i++)	{
        		if (!contains(array[i]))	return false;
        	}
        	return true;
        }
        private class ContainsAll1 implements Runnable	{
        	int min, max;
        	Entry<?,?>[] array;
        	ContainsAll1 (int a, int b, Entry<?,?>[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run()	{
        		for (int i=min; i<max; i++)	{
        			if (!boolGlobal)	return;
        			if (!contains(array[i]))	{
        				boolGlobal = false;
        				return;
        			}
        		}
        	}
        }
        public boolean containsAllPar (Collection<?> collection, int numProcs)	{
        	boolGlobal = true;
        	Entry<?,?>[] array = (Entry<?,?>[])collection.toArray();
        	int numElemProc = array.length/numProcs;
        	threads = new Thread[numProcs];
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        		threads[i] = new Thread(new ContainsAll1 (numMin, numMax, array));
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
        public boolean containsAll (TObjectCollection collection)	{
        	TIntIntEntry[] array = (TIntIntEntry[])collection.toArray();
        	for (int i=0; i<array.length; i++)	{
        		if (!contains(array[i]))
        			return false;
        	}
        	return true;
        }
        private class ContainsAll2 implements Runnable	{
        	int min, max;
        	TIntIntEntry[] array;
        	ContainsAll2 (int a, int b, TIntIntEntry[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run()	{
        		for (int i=min; i<max; i++)	{
        			if (!boolGlobal)
        				return;
        			if (!contains (array[i]))	{
        				boolGlobal = false;
        				return;
        			}
        		}
        	}
        }
        public boolean containsAllPar (TObjectCollection collection, int numProcs)	{
        	boolGlobal = true;
        	TIntIntEntry[] array = (TIntIntEntry[])collection.toArray();
        	int numElemProc = array.length/numProcs;
        	threads = new Thread[numProcs];
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        		threads[i] = new Thread(new ContainsAll2 (numMin, numMax, array));
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
        public boolean containsAll (Object[] array)	{
        	for (int i=0; i<array.length; i++)	{
        		if (!contains(array[i]))
        			return false;
        	}
        	return true;
        }
        public boolean containsAllPar (Object[] array, int numProcs)	{
        	boolGlobal = true;
        	if (array[0] instanceof Entry<?,?>)	{
        		Entry<?,?>[] entries = (Entry<?,?>[]) array;
        		int numElemProc = array.length/numProcs;
            	threads = new Thread[numProcs];
            	int numMin, numMax;
            	for (int i=0; i<numProcs; i++)	{
            		numMin = limiteMin (i, numElemProc, 0);
            		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
            		threads[i] = new Thread(new ContainsAll1 (numMin, numMax, entries));
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
        	if (array[0] instanceof TIntIntEntry)	{
        		TIntIntEntry[] entries = (TIntIntEntry[]) array;
        		int numElemProc = array.length/numProcs;
            	threads = new Thread[numProcs];
            	int numMin, numMax;
            	for (int i=0; i<numProcs; i++)	{
            		numMin = limiteMin (i, numElemProc, 0);
            		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
            		threads[i] = new Thread(new ContainsAll2 (numMin, numMax, entries));
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
        
        public boolean equals (Object o)	{
        	return boolGlobal;
        }
        public boolean equalsPar (Object o, int numProcs)	{
        	return boolGlobal;
        }
        
        public void forEach (Consumer<?> action)	{
        	
        }
        public void forEachPar (Consumer<?> action, int numProcs)	{
        	
        }
        
        public Object getNoEntryValue()	{
        	TIntIntEntry entry = new TIntIntEntry (no_entry_key, no_entry_value);
        	return entry;
        }
        
        public int hashCode ()	{
        	return TIntIntHashMap.this.hashCode();
        }
        public int hashCodePar (int numProcs)	{
        	return TIntIntHashMap.this.hashCodePar(numProcs);
        }
        
        public boolean isEmpty()	{
        	return _size > 0;
        }
        
        //public TObjectIterator iterator()	{
        //	return new TObjectIterator();
        //}
        
        public boolean removeAll (Collection<?> collection)	{
        	int oldSize = _size;
        	Entry<?,?>[] array = (Entry<?,?>[])collection.toArray();
        	for (int i=0; i<array.length; i++)	{
        		TIntIntHashMap.this.remove ((Integer)array[i].getKey(), (Integer)array[i].getValue());
        	}
        	return oldSize != _size;
        }
        private class RemoveAll1 implements Runnable	{
        	int min, max;
        	Integer[] keys, values;
        	RemoveAll1 (int a, int b, Integer[] c, Integer[] d)	{
        		min = a; max = b; keys = c; values = d;
        	}
        	public void run()	{
        		int removed = 0;
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				int pos = Arrays.binarySearch(keys, _set[i]);
        				if (pos >= 0)	{
        					if (values[pos] == _values[i])	{
        						_states[i] = REMOVED;
        						removed++;
        					}
        				}
        			}
        		}
        		atIntGlobal.addAndGet(removed);
        	}
        }
        public boolean removeAllPar (Collection<?> collection, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	Entry<?,?>[] array = (Entry<?,?>[])collection.toArray();
        	Integer[] keys = new Integer[array.length];
        	Integer[] values = new Integer[array.length];
        	int numElemProc = _set.length/numProcs;
        	threads = new Thread[numProcs];
        	int numMin, numMax;
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        		threads[i] = new Thread (new RemoveAll1 (numMin, numMax, keys, values));
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
        	return oldSize != _size;
        }
        
        public boolean remove (Object o)	{
        	if (o instanceof Entry<?,?>)	{
        		Entry<?,?> entry = (Entry<?,?>) o;
        		return TIntIntHashMap.this.remove ((Integer)entry.getKey(), (Integer)entry.getValue());
        	}
        	if (o instanceof TIntIntEntry)	{
        		TIntIntEntry entry = (TIntIntEntry) o;
        		return TIntIntHashMap.this.remove (entry.getKey(), entry.getValue());
        	}
        	return false;
        }
        public boolean removePar (Object o, int numProcs)	{
        	return remove(o);
        }
        
        public boolean removeAll (TObjectCollection collection)	{
        	return boolGlobal;
        }
        public boolean removeAllPar (TObjectCollection collection, int numProcs)	{
        	return boolGlobal;
        }
        public boolean removeAll (Object[] array)	{
        	return boolGlobal;
        }
        public boolean removeAllPar (Object[] array, int numProcs)	{
        	return boolGlobal;
        }
        
        public boolean retainAll (Collection<?> collection)	{
        	int oldSize = _size;
        	return oldSize != _size;
        }
        public boolean retainAllPar (Collection<?> collection, int numProcs)	{
        	int oldSize = _size;
        	return oldSize != _size;
        }
        public boolean retainAll (TObjectCollection collection)	{
        	int oldSize = _size;
        	return oldSize != _size;
        }
        public boolean retainAllPar (TObjectCollection collection, int numProcs)	{
        	int oldSize = _size;
        	return oldSize != _size;
        }
        public boolean retainAll (Object[] array)	{
        	int oldSize = _size;
        	return oldSize != _size;
        }
        public boolean retainAllPar (Object[] array, int numProcs)	{
        	int oldSize = _size;
        	return oldSize != _size;
        }
        
        public Object[] toArray ()	{
        	return new Object[0];
        }
        public Object[] toArrayPar (int numProcs)	{
        	return new Object[0];
        }
        public Object[] toArray (Object[] dest)	{
        	return new Object[0];
        }
        public Object[] toArrayPar (Object[] dest, int numProcs)	{
        	return new Object[0];
        }
        public String toString()	{
        	return TIntIntHashMap.this.toString();
        }
        public String toStringPar (int numProcs)	{
        	return TIntIntHashMap.this.toStringPar(numProcs);
        }
    }*/

    private class Keys1 implements Runnable	{
    	int min, max, idProc;
    	Keys1 (int a, int b, int c)	{
    		min=a; max=b; idProc=c;
    	}
    	public void run()	{
        	short[] shortsLocal = new short[max-min];
    		int j=0;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL) {
    				shortsLocal[j++] = _set[i];
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
    			System.arraycopy(shortsLocal, 0, shortsGlobal, size, j);
        	}
    		else 	{
    			atIntGlobal.addAndGet(j);
        		intsGlobal2[idProc] = j;
    			System.arraycopy(shortsLocal, 0, shortsGlobal, 0, j);
    		}
    	}
    }
    protected short[] keysPar(int numProcs)	{
    	int numElemProc = _set.length/numProcs;
    	atIntGlobal = new AtomicInteger(0);
    	shortsGlobal = new short[size()];
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
    	return shortsGlobal;
    }
    /** {@inheritDoc} */
    protected short[] keys() {
        short[] keys = new short[size()];
        short[] k = _set;
        byte[] states = _states;

        for (int i = k.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            keys[j++] = k[i];
          }
        }
        return keys;
    }
    /** {@inheritDoc} */
    protected short[] keysPar (short[] array, int numProcs) {
        int size = size();
        if (array.length < size()) {
            array = new short[size];
        }
        int numElemProc = _set.length/numProcs;
    	intGlobal = 0;
    	shortsGlobal = new short[size()];
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
    	System.arraycopy(shortsGlobal, 0, array, 0, intGlobal);
    	return shortsGlobal;
    }

    /** {@inheritDoc} */
    protected short[] keys (short[] array) {
        int size = size();
        if (array.length < size) {
            array = new short[size];
        }

        short[] keys = _set;
        byte[] states = _states;

        for (int i = keys.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            array[j++] = keys[i];
          }
        }
        return array;
    }

    /** {@inheritDoc} */
    protected float[] toArrayValues() {
        float[] vals = new float[size()];
        float[] v = _values;
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
    		float[] floatsLocal = new float[max-min];
    		int j=0;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL) {
    				floatsLocal[j++] = _values[i];
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
    			System.arraycopy(floatsLocal, 0, floatsGlobal, k, j);
    		}
    		else	{
    			//System.out.println ("Procesador 0");
        		intsGlobal2[idProc] = j;
    			System.arraycopy(floatsLocal, 0, floatsGlobal, 0, j);
    			//System.out.println ("Ha acabado el procesador 0");
    		}
    	}
    }
    /** {@inheritDoc} */
    protected float[] toArrayValuesPar (int numProcs)	{
    	atIntGlobal = new AtomicInteger(0);
    	floatsGlobal = new float[size()];
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
    	return floatsGlobal;
    }
    /** {@inheritDoc} */
    protected float[] toArrayValuesPar (float[] array, int numProcs)	{
    	if (array.length < size())
    		array = new float[size()];
    	floatsGlobal = new float[_size];
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
    	System.arraycopy(floatsGlobal, 0, array, 0, intGlobal);
    	//System.out.print ("array dest: ");
		//for (int i=0; i<array.length; i++)	{
		//	System.out.print (", " + array[i]);
		//}
		//System.out.println();
    	return array;
    }
    /** {@inheritDoc} */
    protected float[] toArrayValues (float[] array) {
        int size = size();
        if (array.length < size) {
            array = new float[size];
        }

        float[] v = _values;
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
    

    




    public TShortFloatIterator iterator() {
        return new TShortFloatHashIterator(this);
    }


    /** {@inheritDoc} */
    protected void forEachKey (Consumer<? super Short> action)	{
    	for (int i=0; i<_set.length; i++)	{
    		if (_states[i] == FULL)
    			action.accept(_set[i]);
    	}
    }
    private class ForEachKey1 implements Runnable	{
    	int min, max;
    	Consumer<? super Short> action;
    	ForEachKey1 (int a, int b, Consumer<? super Short> c)	{
    		min = a; max = b; action = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)
    			if (_states[i] == FULL)
    				action.accept(_set[i]);
    	}
    }
    /** {@inheritDoc} */
    protected void forEachKeyPar (Consumer<? super Short> action, int numProcs)	{
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
    	Consumer<? super Float> action;
    	ForEachValue1 (int a, int b, Consumer<? super Float> c)	{
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
    protected void forEachValuePar (Consumer<? super Float> action, int numProcs)	{
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
    protected void forEachValue (Consumer<? super Float> action)	{
    	if (action == null)
    		throw new NullPointerException();
    	if (_size > 0)	{
    		for (int i=0; i<_values.length; i++)	{
    			action.accept(_values[i]);
    		}
    	}
    }
    
    /**public Integer computeIfPresentPar (int key, BiFunction<? super Integer, 
    		? super Integer, ? extends Integer> remappingFunction, int numProcs)	{
    	int index = index( key );
    	Integer value = null;;
    	if (index > 0)	value = remappingFunction.apply((Integer)key, (Integer)_values[index]);
    	if (index > 0) {
    		if (value != null)	{
    			//put (key, value.intValue());
    			putPar (key, value.intValue());
    			return (Integer) value;
    		}
    		else	removeAt (index);
    	}
    	return null;
    }*/

    
    /**public Integer computePar (int key,	BiFunction<? super Integer, 
    		? super Integer, ? extends Integer> remappingFunction, int numProcs)	{
	int index = index( key );
	Integer value = null;
	if (index > 0)	value = remappingFunction.apply((Integer)key, (Integer)_values[index]);
	if (value != null)	{
		//put (key, value.intValue());
		putPar (key, value.intValue());
		return (Integer) value;
	}
	else if (index > 0)	removeAt (index);
    return null;
}*/
    
    /** {@inheritDoc} */
    public boolean increment (short key) {
        return adjustValue(key, 1);
    }


    /** {@inheritDoc} */
    public boolean adjustValue (short key, float amount) {
        int index = index(key);
        if (index < 0) {
            return false;
        } 
        else {
            _values[index] += amount;
            return true;
        }
    }


    /** {@inheritDoc} */
    public float adjustOrPutValue (short key, float adjust_amount, float put_amount) {
        int index = insertKey(key);
        final boolean isNewMapping;
        final float newValue;
        if (index < 0) {
            index = -index -1;
            newValue = (_values[index] += adjust_amount);
            isNewMapping = false;
        }
        else {
            newValue = (_values[index] = put_amount);
            isNewMapping = true;
        }

        if (isNewMapping) {
            postInsertHook(consumeFreeSlot);
        }

        return newValue;
    }


    


    


   
    class TShortFloatValueHashIterator extends THashPrimitiveIterator implements TFloatIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveHash</tt> we will be iterating over.
         */
        TShortFloatValueHashIterator (TPrimitiveHash hash) {
            super(hash);
        }

        /** {@inheritDoc} */
        public float next() {
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
                TShortFloatHashMap.this.removeAt(_index);
            }
            finally {
                _hash.reenableAutoCompaction(false);
            }

            _expectedSize--;
        }
    }


    class TShortFloatHashIterator extends THashPrimitiveIterator implements TShortFloatIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param map the <tt>TShortFloatHashMap</tt> we will be iterating over.
         */
        TShortFloatHashIterator (TShortFloatHashMap map) {
            super(map);
        }

        /** {@inheritDoc} */
        public void advance() {
            moveToNextIndex();
        }

        /** {@inheritDoc} */
        public short key() {
            return _set[_index];
        }

        /** {@inheritDoc} */
        public float value() {
            return _values[_index];
        }

        /** {@inheritDoc} */
        public float setValue(float val) {
            float old = value();
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
                TShortFloatHashMap.this.removeAt(_index);
            }
            finally {
                _hash.reenableAutoCompaction(false);
            }
            _expectedSize--;
        }
    }

    


    
/**
    private class WriteExternal1 implements Runnable	{
    	int min, max;
    	ObjectOutput out;
    	WriteExternal1 (int a, int b, ObjectOutput o)	{
    		min=a; max=b; out=o;
    	}
    	public void run ()	{
        	for (int i=min; i<max; i++) {
                if ( _states[i] == FULL ) {
    				try {
                    	out.writeInt( _set[i] );
                    	out.writeInt( _values[i] );
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
                }
            }
    	}
    }
    public void writeExternalPar (ObjectOutput out, int numProcs) throws IOException	{
    	//VERSION
    	out.writeByte(0);
    	//SUPER
    	super.writeExternal(out);
    	//NUMBER OF ENTRIES
    	out.writeInt (_size);
    	//ENTRIES
    	int numElemProc = _set.length/numProcs;
    	int numMin, numMax;
        threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
    		threads[i] = new Thread (new WriteExternal1 (numMin, numMax, out));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    }
    public void writeExternal(ObjectOutput out) throws IOException {
        // VERSION
    	out.writeByte( 0 );

        // SUPER
    	super.writeExternal( out );

    	// NUMBER OF ENTRIES
    	out.writeInt( _size );

    	// ENTRIES
    	for ( int i = _states.length; i-- > 0; ) {
            if ( _states[i] == FULL ) {
                out.writeInt( _set[i] );
                out.writeInt( _values[i] );
            }
        }
    }


    private class ReadExternal1 implements Runnable	{
    	int min, max;
    	ObjectInput in;
    	ReadExternal1 (int a, int b, ObjectInput input)	{
    		min=a; max=b; in=input;
    	}
    	public void run ()	{
            int i = max;
            int key = 0;
            int val = 0;
    		while (i-- > min) {
    			if (_states[i] == FULL)	{
				try {
					key = in.readInt();
					val = in.readInt();
					put(key, val);
				} catch (IOException e) {
					e.printStackTrace();
				}}
            }
    	}
    }
    public void readExternalPar(ObjectInput in, int numProcs) throws IOException, ClassNotFoundException {
        // VERSION
    	in.readByte();

        // SUPER
    	super.readExternal( in );

    	// NUMBER OF ENTRIES
    	int size = in.readInt();
    	setUp( size );

    	// ENTRIES
    	int numElemProc = size/numProcs;
    	int numMin, numMax;
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _set.length);
        	threads[i] = new Thread (new ReadExternal1(numMin, numMax, in));
        	threads[i].start();
        }
        for (int i=0; i<numProcs; i++)
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    }
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        // VERSION
    	in.readByte();

        // SUPER
    	super.readExternal( in );

    	// NUMBER OF ENTRIES
    	int size = in.readInt();
    	setUp( size );

    	// ENTRIES
        while (size-- > 0) {
            int key = in.readInt();
            int val = in.readInt();
            put(key, val);
        }
    }*/
    
    public short lastKey ()	{
    	for (int i=_set.length-1; i>=0; i--)	{
    		if (_states[i] == FULL)
    			return _set[i];
    	}
    	return no_entry_key;
    }
    
    public float lastValue ()	{
    	for (int i=_values.length-1; i>=0; i--)	{
    		if (_states[i] == FULL)
    			return _values[i];
    	}
    	return no_entry_value;
    }
} // TIntIntHashMap