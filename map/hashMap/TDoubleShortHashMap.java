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

import PJC.collection.TDoubleCollection;
import PJC.collection.TShortCollection;
import PJC.hash.TDoubleShortHash;
import PJC.iterator.TDoubleIterator;
import PJC.iterator.TShortIterator;
import PJC.list.ArraysPar;
import PJC.map.TDoubleShortMap;
import PJC.map.hashMap.HashFunctions;
import PJC.map.iterator.TDoubleShortIterator;
import PJC.set.TDoubleSet;
import PJC.spliterator.TDoubleSpliterator;
import PJC.spliterator.TShortSpliterator;

public class TDoubleShortHashMap extends TDoubleShortHash implements TDoubleShortMap, Serializable, Cloneable {
    static final long serialVersionUID = 1L;

    /** the values of the map */
    protected transient short[] _values;
    
    /**boolean used for the threads to share a boolean result*/
    private boolean boolGlobal;
    
    /** int used for the threads to share a boolean result*/
    //private int intGlobal;
    
    /**array of ints used for the threads to share a int[] result*/
    private double[] doublesGlobal;
    private short[] shortsGlobal;
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
     * Creates a new <code>TDoubleShortHashMap</code> instance with the default
     * capacity and load factor.
     */
    public TDoubleShortHashMap() {
        super();
    }


    /**
     * Creates a new <code>TDoubleShortHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the default load factor.
     *
     * @param initialCapacity an <code>int</code> value
     */
    public TDoubleShortHashMap (int initialCapacity) {
        super(initialCapacity);
    }


    /**
     * Creates a new <code>TDoubleShortHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>float</code> value
     */
    public TDoubleShortHashMap (int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }


    /**
     * Creates a new <code>TDoubleShortHashMap</code> instance with a prime
     * capacity equal to or greater than <tt>initialCapacity</tt> and
     * with the specified load factor.
     *
     * @param initialCapacity an <code>int</code> value
     * @param loadFactor a <code>short</code> value
     * @param noEntryKey a <code>double</code> value that represents
     *                   <tt>null</tt> for the Key set.
     * @param noEntryValue a <code>float</code> value that represents
     *                   <tt>null</tt> for the Value set.
     */
    public TDoubleShortHashMap (int initialCapacity, float loadFactor,
        double noEntryKey, short noEntryValue) {
        super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
    }


    /**
     * Creates a new <code>TDoubleShortHashMap</code> instance containing
     * all of the entries in the map passed in.
     *
     * @param keys a <tt>double</tt> array containing the keys for the matching values.
     * @param values a <tt>short</tt> array containing the values.
     */
    public TDoubleShortHashMap (double[] keys, short[] values) {
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
    public TDoubleShortHashMap (double[] keys, short[] values, int numProcs) {
        super(Math.max(keys.length, values.length));

        //int size = Math.min( keys.length, values.length );
        /**for ( int i = 0; i < size; i++ ) {
            this.put( keys[i], values[i] );
        }*/
        this.putAllPar(keys, values, numProcs);
    }

    /**
     * Creates a new <code>TDoubleShortHashMap</code> instance containing
     * all of the entries in the map passed in.
     *
     * @param map a <tt>TDoubleShortMap</tt> that will be duplicated.
     */
    public TDoubleShortHashMap (TDoubleShortMap map) {
        super(map.size());
        if (map instanceof TDoubleShortHashMap) {
            TDoubleShortHashMap hashmap = (TDoubleShortHashMap) map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != (double) 0) {
                Arrays.fill(_set, this.no_entry_key);
            }
            if (this.no_entry_value != (short) 0) {
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
    public TDoubleShortHashMap(TDoubleShortMap map, int numProcs) {
        super(map.size());
        if (map instanceof TDoubleShortHashMap) {
            TDoubleShortHashMap hashmap = (TDoubleShortHashMap) map;
            this._loadFactor = hashmap._loadFactor;
            this.no_entry_key = hashmap.no_entry_key;
            this.no_entry_value = hashmap.no_entry_value;
            if (this.no_entry_key != (double)0) {
            	ArraysPar.fillPar(_set, this.no_entry_key, numProcs);
            }
            if (this.no_entry_value != (short)0) {
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
    public TDoubleShortHashMap clone() {
        TDoubleShortHashMap result;
        try {
            result = (TDoubleShortHashMap)super.clone();
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
    public TDoubleShortHashMap clonePar(int numProcs) {
        TDoubleShortHashMap result;
        try {
            result = (TDoubleShortHashMap)super.clone();
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
    public short compute (double key,
    			BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction)	{
    	int index = index (key);
    	Short value = null;
    	if (index > 0)	value = remappingFunction.apply((Double)key, (Short)_values[index]);
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
    public short comptueIfAbsent (double key, Function<? super Double,
    		? extends Short> remappingFunction)	{
    	int index = index(key);
    	Short value = remappingFunction.apply(key);
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
    public short computeIfPresent (double key,
    				BiFunction<? super Double, ? super Short, ? extends Short> remappingFunction)	{
    	if (remappingFunction == null)
    		throw new NullPointerException(); 
    	int index = index (key);
    	
    	if ((index> 0) && (_values[index] != no_entry_value))	{
    		short value = remappingFunction.apply(key, _values[index]);
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
    public boolean containsKey (double key) {
        return contains (key);
    }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     */
    public boolean containsValue (short value) {
        byte[] states = _states;
        short[] vals = _values;

        for (int i=0; i<_values.length; i++) {
            if (states[i] == FULL && value == vals[i]) {
                return true;
            }
        }
        return false;
    }
    
    private class ContainsValue1 implements Runnable	{
    	private int min, max;
    	private short val;
    	ContainsValue1 (short value, int a, int b)	{
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
    public boolean containsValuePar (short value, int numProcs){
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
    	TDoubleShortMap that;
    	int min, max;
    	Equals1 (TDoubleShortMap obj, int a, int b)	{
    		that=obj; min=a; max=b;
    	}
    	public void run ()	{
            short this_no_entry_value = getNoEntryValue();
            short that_no_entry_value = that.getNoEntryValue();
    		for (int i=min; i<max; i++) {
                if (_states[i] == FULL) {
                    double key = _set[i];
                    short that_value = that.get(key);
                    short this_value = _values[i];
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
            short this_no_entry_value = getNoEntryValue();
            //int that_no_entry_value = that.getNoEntryValue();
    		for (int i=min; i<max; i++) {
                if (_states[i] == FULL) {
                    double key = _set[i];
                    //int that_value = that.get( key );
                    Object that_value = that.get(key);
                    if (!(that_value instanceof Short))	{
                    	boolGlobal = false;
                    	return;
                    }
                    short this_value = _values[i];
                    if ((this_value != (Short)that_value) &&
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
    	if (!((other instanceof TDoubleShortMap)||(other instanceof Map<?,?>)))	{
    		return false;
    	}
		boolGlobal = true;
    	if (other instanceof TDoubleShortMap)	{
    		TDoubleShortMap that = (TDoubleShortMap) other;
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
        if (!(other instanceof TDoubleShortMap)) {
            return false;
        }
        TDoubleShortMap that = (TDoubleShortMap) other;
        if (that.size() != this.size()) {
            return false;
        }
        short[] values = _values;
        byte[] states = _states;
        short this_no_entry_value = getNoEntryValue();
        short that_no_entry_value = that.getNoEntryValue();
        for (int i = values.length; i-- > 0;) {
            if (states[i] == FULL) {
                double key = _set[i];
                short that_value = that.get(key);
                short this_value = values[i];
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
    public void forEach(BiConsumer<? super Double, ? super Short> action) {
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
    	BiConsumer<? super Double, ? super Short> action;
    	ForEachPar1 (int a, int b, BiConsumer<? super Double, ? super Short> c)	{
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
    public void forEachPar (BiConsumer<? super Double, ? super Short> action, int numProcs)	{
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
    public short get (double key) {
        int index = index(key);
        return index < 0 ? no_entry_value : _values[index];
    }
    
    /**
     * Returns the value to which the specified key is mapped, or defaultValue 
     * if this map contains no mapping for the key.
     */
    public short getOrDefault (double key, short defaultValue)	{
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
            atIntGlobal.addAndGet(hashCode);
    	}
    }
    /** {@inheritDoc} */
    public int hashCodePar(int numProcs)	{
    	atIntGlobal = new AtomicInteger(0);
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
    	return atIntGlobal.get();
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
    
    static class TDoubleShortHashMapSpliterator {
        final TDoubleShortHashMap map;
        double currentKey;				// current key
        short currentValue;			// current value
        int index;                  // current index, modified on advance/split
        int fence;                  // one past last index
        int est;                    // size estimate
        double _no_entry_key;			// int key that represents null
        short _no_entry_value;			// int value that represents null

        TDoubleShortHashMapSpliterator(TDoubleShortHashMap m, int origin,
                           int fence, int est, double no_entry_key, short no_entry_value) {
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
                TDoubleShortHashMap m = map;
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

    static final class TDoubleShortKeySpliterator
        extends TDoubleShortHashMapSpliterator
        implements TDoubleSpliterator {
        TDoubleShortKeySpliterator(TDoubleShortHashMap m, int origin, int fence, int est, double no_entry, short no_value) {
            super(m, origin, fence, est, no_entry, no_value);
        }

        public TDoubleShortKeySpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || currentKey != _no_entry_key) ? null :
            	new TDoubleShortKeySpliterator(map, lo, index = mid, est >>>= 1,
                _no_entry_key, _no_entry_value);
        }


        public void forEachRemaining (Consumer<? super Double> action)	{
        	int i, hi;
        	TDoubleShortHashMap m;
        	double[] a;
        	if (action == null)
        		throw new NullPointerException();
        	if ((m = map) != null && (a = m._set) != null)	{
        		if ((hi = fence) < 0)
        			hi = m.size();
        		if ((i=index) <= 0 && (index=hi) <= a.length)	{
        			for (; i<hi; ++i)	{
        				double e = a[i];
        				if (m._states[i] == FULL)	{
        					action.accept(e);
        				}
        			}
        		}
        	}
        	throw new ConcurrentModificationException();
        }

        public boolean tryAdvance(Consumer<? super Double> action) {
        	if (action == null)
        		throw new NullPointerException();
        	int hi = getFence(), i = index;
        	if (i < hi)	{
        		index = i+1;
        		double e = map._set[i];
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

    static final class TDoubleShortValueSpliterator
        extends TDoubleShortHashMapSpliterator
        implements TShortSpliterator {
        TDoubleShortValueSpliterator(TDoubleShortHashMap m, int origin, int fence, int est,
                         double no_entry_key, short no_entry_value) {
            super(m, origin, fence, est, no_entry_key, no_entry_value);
        }

        public TDoubleShortValueSpliterator trySplit() {
            int hi = getFence(), lo = index, mid = (lo + hi) >>> 1;
            return (lo >= mid || currentValue != _no_entry_value) ? null :
                new TDoubleShortValueSpliterator(map, lo, index = mid, est >>>= 1,
                                          _no_entry_key, _no_entry_value);
        }

        public void forEachRemaining (Consumer<? super Short> action)	{
        	int i, hi;
        	TDoubleShortHashMap m;
        	short[] a;
        	if (action == null)
        		throw new NullPointerException();
        	if ((m = map) != null && (a = m._values) != null)	{
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
        		short e = map._values[i];
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
    public TDoubleSet keySet() {
        return new TKeyView();
    }
    
    /** a view onto the keys of the map. */
    protected class TKeyView implements TDoubleSet {

        /** {@inheritDoc} */
        public TDoubleIterator iterator() {
            return new TDoubleShortKeyHashIterator (TDoubleShortHashMap.this);
        }
        
        /** {@inheritDoc} */
        public TDoubleSpliterator spliterator() {
        	return new TDoubleShortKeySpliterator(TDoubleShortHashMap.this, 0, -1, 0, no_entry_key, no_entry_value);
        }

        /** {@inheritDoc} */
        public double getNoEntryValue() {
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
        public boolean contains (double entry) {
            return TDoubleShortHashMap.this.contains(entry);
        }
        public boolean containsPar (double entry, int numProcs)	{
        	return TDoubleShortHashMap.this.contains(entry);
        }


        /** {@inheritDoc} */
        public double[] toArray() {
            return TDoubleShortHashMap.this.keys();
        }

        /** {@inheritDoc} */
        public double[] toArrayPar (int numProcs) {
            return TDoubleShortHashMap.this.keysPar(numProcs);
        }

        /** {@inheritDoc} */
        public double[] toArray (double[] dest) {
            return TDoubleShortHashMap.this.keys(dest);
        }
        public double[] toArrayPar (double[] dest, int numProcs)	{
        	return TDoubleShortHashMap.this.keysPar(dest, numProcs);
        }


        /**
         * Unsupported when operating upon a Key Set view of a TDoubleShortMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean add (double entry) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean remove (double entry) {
            return no_entry_value != TDoubleShortHashMap.this.remove(entry);
        }
        /** {@inheritDoc} */
        public boolean removePar (double entry, int numProcs)	{
        	return no_entry_value != TDoubleShortHashMap.this.remove (entry);
        }

        private class ContainsAll1 implements Runnable	{
        	int min, max;
        	Double[] array;
        	ContainsAll1 (int a, int b, Double[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (array[i] instanceof Double) {
                        double ele = ((Double)array[i]).doubleValue();
                        if (!TDoubleShortHashMap.this.containsKey(ele)) {
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
        	Double[] array = collection.toArray(new Double [collection.size()]);
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
                if (element instanceof Double) {
                    double ele = ((Double) element).doubleValue();
                    if (!TDoubleShortHashMap.this.containsKey(ele)) {
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
        	double[] array;
        	ContainsAll2 (int a, int b, double[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (!TDoubleShortHashMap.this.containsKey (array[i])) {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAllPar (TDoubleCollection collection, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	double[] array = collection.toArray (new double[collection.size()]);
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
        public boolean containsAll (TDoubleCollection collection) {
            TDoubleIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (!TDoubleShortHashMap.this.containsKey(iter.next())) {
                    return false;
                }
            }
            return true;
        }

        private class ContainsAll3 implements Runnable	{
        	int min, max;
        	double[] array;
        	ContainsAll3 (int a, int b, double[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (!TDoubleShortHashMap.this.contains(array[i])) {
                        boolGlobal = false;
                        return;
                    }
                }
        	}
        }
        /** {@inheritDoc} */
        public boolean containsAllPar (double[] array, int numProcs)	{
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
        public boolean containsAll (double[] array) {
            for (double element : array) {
                if (!TDoubleShortHashMap.this.contains(element)) {
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
        public boolean addAll (Collection<? extends Double> collection) {
            throw new UnsupportedOperationException();
        }
        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAllPar (Collection<? extends Double> collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAll (TDoubleCollection collection) {
            throw new UnsupportedOperationException();
        }
        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAllPar (TDoubleCollection collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAll (double[] array) {
            throw new UnsupportedOperationException();
        }
        /**
         * Unsupported when operating upon a Key Set view of a TIntIntMap
         * <p/>
         * {@inheritDoc}
         */
        public boolean addAllPar (double[] array, int numProcs) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Removes all of the elements of this map whose key satisfy the given
         * predicate. Errors or runtime exceptions thrown during iteration or by
         * the predicate are relayed to the caller.
         * @param filter a predicate which returns true for elements to be removed
         * @return true if any elements were removed
         */
        public boolean removeIf (Predicate<? super Double> filter) {
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
        	Predicate<? super Double> filter;
        	RemoveIf1 (int a, int b, Predicate<? super Double> c, int d)	{
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
        public boolean removeIfPar (Predicate<? super Double> filter, int numProcs)	{
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
                    	if (Arrays.binarySearch(array, (Double)_set[i]) < 0)	{
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
            TDoubleIterator iter = iterator();
            while (iter.hasNext()) {
                if (!collection.contains(Double.valueOf(iter.next()))) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }


        private class RetainAll2 implements Runnable	{
        	int min, max;
        	double[] array;
        	RetainAll2 (int a, int b, double[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed = 0;
        		for (int i=min; i<max; i++) {
        			//if (_states[i] == FULL)	{
        			int pos = TDoubleShortHashMap.this.index(array[i]);
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
        public boolean retainAllPar (TDoubleCollection collection, int numProcs)	{
        	if (this == collection)	return false;
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	double[] array = collection.toArrayPar (numProcs);
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
        public boolean retainAll (TDoubleCollection collection) {
            if (this == collection) {
                return false;
            }
            boolean modified = false;
            TDoubleIterator iter = iterator();
            while (iter.hasNext()) {
                if (!collection.contains(iter.next())) {
                    iter.remove();
                    modified = true;
                }
            }
            return modified;
        }

        /** {@inheritDoc} */
        public boolean retainAllPar (double[] array, int numProcs)	{
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
        public boolean retainAll (double[] array) {
            boolean changed = false;
            Arrays.sort(array);
            double[] set = _set;
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
                    	if (Arrays.binarySearch(array, (Double)_set[i]) >= 0)	{
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
        				int pos = TDoubleShortHashMap.this.index((Double)array[i]);
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
                if (element instanceof Double) {
                    double c = ((Double) element).doubleValue();
                    if (remove(c)) {
                        changed = true;
                    }
                }
            }
            return changed;
        }

        /** {@inheritDoc} */
        public boolean removeAll (TDoubleCollection collection) {
            if (this == collection) {
                clear();
                return true;
            }
            boolean changed = false;
            TDoubleIterator iter = collection.iterator();
            while (iter.hasNext()) {
                double element = iter.next();
                if (remove(element)) {
                    changed = true;
                }
            }
            return changed;
        }
        private class RemoveAll2 implements Runnable	{
        	int min, max;
        	double[] array;
        	RemoveAll2 (int a, int b, double[] c)	{
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
        public boolean removeAllPar (TDoubleCollection collection, int numProcs)	{
        	if (this == collection)	{
        		clear();
        		return true;
        	}
        	atIntGlobal = new AtomicInteger(0);
        	int oldSize = _size;
        	double[] array = collection.toArrayPar (numProcs);
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
        public boolean removeAll (double[] array) {
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
        	double[] array;
        	RemoveAll3 (int a, int b, double[] c)	{
        		min = a; max = b; array = c;
        	}
        	public void run ()	{
        		int removed = 0;
        		for (int i=min; i<max; i++) {
        			if (_states[i] == FULL)	{
        				//if (Arrays.binarySearch(array, _set[i]) >= 0)	{
        				int pos = TDoubleShortHashMap.this.index(array[i]);
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
        public boolean removeAllPar (double[] array, int numProcs)	{
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
        
        public boolean removeAllPar2 (double[] array, int numProcs)	{
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
            TDoubleShortHashMap.this.clear();
        }
        /** {@inheritDoc} */
        public void clearPar(int numProcs)	{
        	TDoubleShortHashMap.this.clearPar(numProcs);
        }


        /** {@inheritDoc} */
        public void forEach (Consumer<? super Double> action)	{
        	TDoubleShortHashMap.this.forEachKey (action);
        }
        /** {@inheritDoc} */
        public void forEachPar (Consumer<? super Double> action, int numProcs)	{
        	TDoubleShortHashMap.this.forEachKeyPar (action, numProcs);
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
        	TDoubleSet that;
        	Equals1 (int a, int b, TDoubleSet c)	{
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
        	if (o instanceof TDoubleSet)	{
        		TDoubleSet that = (TDoubleSet) o;
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
            if (!(other instanceof TDoubleSet)) {
                return false;
            }
            final TDoubleSet that = (TDoubleSet) other;
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
    public short merge (double key, short value, BiFunction<? super Double, ? super Short,
    		? extends Short> remappingFunction)	{
    	if (value == no_entry_value)
    		throw new NullPointerException();
    	if (remappingFunction == null)
    		throw new NullPointerException();
    	int index = index (key);
    	if ((index < 0) || (_values[index] == no_entry_value))	{
    		put (key, value);
    		return value;
    	}
    	Short newValue = remappingFunction.apply (_set[index], value);
    	if (newValue != null)	{
    		put (key, newValue);
    		return newValue;
    	}
    	removeAt (index);
    	return no_entry_value;
    }
    
    /** {@inheritDoc} */
    public short put (double key, short value) {
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
     public short putPar (double key, short value) {
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
    private short doPut (double key, short value, int index) {
        short previous = no_entry_value;
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
     public short putParAll (double key, short value) {
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
    private short doPutAll(double key, short value, int index) {
        short previous = no_entry_value;
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
    public void putAll (Map<? extends Double, ? extends Short> map) {
        ensureCapacity(map.size());
        // could optimize this for cases when map instanceof THashMap
        for (Map.Entry<? extends Double, ? extends Short> entry : map.entrySet()) {
            this.put(entry.getKey().doubleValue(), entry.getValue().shortValue());
        }
    }
     private class PutAll1 implements Runnable	{
     	int min, max;
     	Entry<?,?>[] entradas;
     	PutAll1 (int a, int b, Entry<?, ?>[] e)	{
     		min=a; max=b; entradas = e;
     	}
     	public void run ()	{
     		double clave;
     		short valor;
     		for (int i=min; i<max; i++)	{
     			if (entradas[i].getKey() instanceof Integer)	{
     				clave = ((Double) entradas[i].getKey()).doubleValue();
     				valor = ((Short) entradas[i].getValue()).shortValue();
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
     public void putAllPar(Map<? extends Double, ? extends Short> map, int numProcs) {
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
     	double[] keys;
     	short[] values;
     	PutAll2 (int a, int b, double[] c, short[] d)	{
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
     public void putAll (TDoubleShortMap map) {
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
     public void putAllPar (TDoubleShortMap map, int numProcs) {
         ensureCapacity(map.size());
         int numMin, numMax;
         threads = new Thread[numProcs];
         double[] set = map.keySet().toArrayPar(numProcs);
         short[] values = map.values().toArrayPar(numProcs);
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
     public void putAll (double[] keys, short[] values)	{
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
     public void putAllPar(double[] keys, short[] values, int numProcs) {	
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
    public short putIfAbsent (double key, short value) {
        int index = insertKey(key);
        if (index < 0)
            return _values[-index - 1];
        return doPut(key, value, index);
    }
    
    /** {@inheritDoc} */
    public short remove(double key) {
        short prev = no_entry_value;
        int index = index(key);
        if (index >= 0) {
            prev = _values[index];
            removeAt(index);    // clear key,state; adjust size
        }
        return prev;
    }

    /** {@inheritDoc} */
    public boolean remove (double key, short value)	{
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
    public short replace (double key, short value)	{
    	int index = index (key);
    	if (index > 0)	{
    		short oldValue = _values[index];
    		_values[index] = value;
    		return oldValue;
    	}
    	return no_entry_value;
    }

    /** {@inheritDoc} */
    public boolean replace (double key, short oldValue, short newValue)	{
    	int index = index (key);
    	if ((index > 0) && (_values[index] == oldValue))	{
    		_values[index] = newValue;
    		return true;
    	}
    	return false;
    }

    /** {@inheritDoc} */
    public void replaceAll (BiFunction 
    			<? super Double, ? super Short, ? extends Short> function)	{
    	for (int i=0; i<_values.length; i++)	{
    		if (_states[i] == FULL)	{
    			short newValue = function.apply(_set[i], _values[i]);
    			_values[i] = newValue;
    		}
    	}
    }
    private class ReplaceAll1 implements Runnable	{
    	int min, max;
    	BiFunction <? super Double, ? super Short, ? extends Short> function;
    	ReplaceAll1 (int a, int b, BiFunction <? super Double, ? super Short,
			? extends Short> c)	{
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
    public void replaceAllPar (BiFunction <? super Double, ? super Short,
    			? extends Short> function, int numProcs)	{
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
    public TShortCollection values ()	{
    	TShortCollection c = new TValueView ();
    	return c;
    }
    /** a view onto the values of the map. */
    protected class TValueView implements TShortCollection {

        /** {@inheritDoc} */
        public TShortIterator iterator() {
            return new TDoubleShortValueHashIterator(TDoubleShortHashMap.this);
        }

        /** {@inheritDoc} */
        public TShortSpliterator spliterator() {
        	return new TDoubleShortValueSpliterator(TDoubleShortHashMap.this, 0, -1, 0, no_entry_key, no_entry_value);
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
        	if (o instanceof TShortCollection)	{
        		TShortCollection that = (TShortCollection) o;
        		if (this.size() != that.size())	{
        			return false;
        		}
        		threads = new Thread[numProcs];
        		boolGlobal = true;
        		int numElemProc = _values.length/numProcs;
        		int numMin, numMax;
        		short[] array = that.toArrayPar(numProcs);
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
        	TShortCollection that;
        	Equals3 (int a, int b, TShortCollection c)	{
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
        	short[] that;
        	Equals1 (int a, int b, short[] c)	{
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
        	if (o instanceof TShortCollection)	{
        		TShortCollection that = (TShortCollection) o;
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
        	if (o instanceof TShortCollection)	{
        		TShortCollection that = (TShortCollection) o;
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
        public short getNoEntryValue() {
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
        public boolean contains (short entry) {
            return TDoubleShortHashMap.this.containsValue(entry);
        }
        @Override
        /** {@inheritDoc} */
        public boolean containsPar(short entry, int numProcs) {
            return TDoubleShortHashMap.this.containsValuePar(entry, numProcs);
        }


        /** {@inheritDoc} */
        public short[] toArray() {
            return TDoubleShortHashMap.this.toArrayValues();
        }

        /** {@inheritDoc} */
        public short[] toArrayPar(int numProcs) {
            return TDoubleShortHashMap.this.toArrayValuesPar(numProcs);
        }

        /** {@inheritDoc} */
        public short[] toArray(short[] dest) {
            return TDoubleShortHashMap.this.toArrayValues(dest);
        }
        /** {@inheritDoc} */
        public short[] toArrayPar(short[] dest, int numProcs) {
            return TDoubleShortHashMap.this.toArrayValuesPar(dest, numProcs);
        }

        public boolean add(short entry) {
            throw new UnsupportedOperationException();
        }
        private class Remove1 implements Runnable	{
        	int min, max;
        	short entry;
        	Remove1 (int a, int b, short c)	{
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
        public boolean removePar (short entry, int numProcs)	{
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
        public boolean remove(short entry) {
            short[] values = _values;
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
        	short[] values;
        	ContainsAll1 (int a, int b, Object[] c, short[] d)	{
        		min = a; max = b; array = c; values = d;
        	}
        	public void run ()	{
        		for (int i=min; i<max; i++) {
        			if (!boolGlobal)	return;
                    if (array[i] instanceof Short) {
                        short ele = ((Short) array[i]).shortValue();
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
        	short[] values = TDoubleShortHashMap.this.values().toArrayPar(numProcs);
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
                if (element instanceof Short) {
                    short ele = ((Short) element).shortValue();
                    if (!TDoubleShortHashMap.this.containsValue(ele)) {
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
        	short[] array, values;
        	ContainsAll2 (int a, int b, short[] c, short[] d)	{
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
        public boolean containsAllPar (TShortCollection collection, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = collection.size()/numProcs;
        	short[] array = collection.toArray (new short[collection.size()]);
        	short[] values = TDoubleShortHashMap.this.values().toArrayPar(numProcs);
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
        public boolean containsAll (TShortCollection collection) {
            TShortIterator iter = collection.iterator();
            while (iter.hasNext()) {
                if (!TDoubleShortHashMap.this.containsValue(iter.next())) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        public boolean containsAllPar (short[] array, int numProcs)	{
        	boolGlobal = true;
        	int numElemProc = array.length/numProcs;
        	short[] values = TDoubleShortHashMap.this.values().toArrayPar(numProcs);
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
        public boolean containsAll (short[] array) {
            for (short element : array) {
                if (!TDoubleShortHashMap.this.containsValue(element)) {
                    return false;
                }
            }
            return true;
        }


        /** {@inheritDoc} */
        public boolean addAll (Collection<? extends Short> collection) {
            throw new UnsupportedOperationException();
        }
        
        public boolean addAllPar (Collection<? extends Short> collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean addAll (TShortCollection collection) {
            throw new UnsupportedOperationException();
        }
        
        public boolean addAllPar (TShortCollection collection, int numProcs) {
            throw new UnsupportedOperationException();
        }


        /** {@inheritDoc} */
        public boolean addAll (short[] array) {
            throw new UnsupportedOperationException();
        }
        
        public boolean addAllPar (short[] array, int numProcs) {
            throw new UnsupportedOperationException();
        }
        
        /**
         * Removes all of the elements of this map whose value satisfy the given
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
        	Predicate<? super Short> filter;
        	RemoveIf1 (int a, int b, Predicate<? super Short> c, int d)	{
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
        		for (int i=min; i<max; i++)	{
        			if (_states[i] == FULL)	{
        				if (Arrays.binarySearch(array, (Short)_values[i]) < 0)	{
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
        public boolean retainAllPar (TShortCollection collection, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	int numElemProc = _values.length/numProcs;
        	short[] array = collection.toArrayPar(numProcs);
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
        public boolean retainAll (short[] array) {
            boolean changed = false;
            Arrays.sort (array);
            short[] values = _values;
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
        				if (Arrays.binarySearch(array, (Short)_values[i]) >= 0)	{
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
                if (element instanceof Short) {
                    short c = ((Short)element).shortValue();
                    if (remove(c)) {
                        changed = true;
                    }
                }
            }
            return changed;
        }
        
        private class RemoveAll2 implements Runnable	{
        	int min, max;
        	short[] array;
        	RemoveAll2 (int a, int b, short[] c)	{
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
        public boolean removeAllPar (TShortCollection collection, int numProcs)	{
        	int oldSize = _size;
        	atIntGlobal = new AtomicInteger(0);
        	int numElemProc = _values.length/numProcs;
        	short[] array = collection.toArrayPar(numProcs);
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

        /** {@inheritDoc} */
        public boolean removeAll (short[] array) {
            boolean changed = false;
            for (int i = array.length; i-- >0;) {
                if (remove(array[i])) {
                    changed = true;
                }
            }
            return changed;
        }
        /** {@inheritDoc} */
        public boolean removeAllPar (short[] array, int numProcs)	{
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
            TDoubleShortHashMap.this.clear();
        }


        /** {@inheritDoc} */
        public void forEach (Consumer<? super Short> action)	{
        	TDoubleShortHashMap.this.forEachValue (action);
        }
        /** {@inheritDoc} */
        public void forEachPar (Consumer<? super Short> action, int numProcs)	{
        	TDoubleShortHashMap.this.forEachValuePar (action, numProcs);
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


    class TDoubleShortKeyHashIterator extends THashPrimitiveIterator implements TDoubleIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveHash</tt> we will be iterating over.
         */
        TDoubleShortKeyHashIterator (TPrimitiveHash hash) {
            super(hash);
        }

        /** {@inheritDoc} */
        public double next() {
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
                TDoubleShortHashMap.this.removeAt(_index);
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
        _values = new short[capacity];
        return capacity;
    }
    
    
    private class Rehash1 implements Runnable	{
    	int min, max;
    	double[] oldKeys;
    	short[] oldValues;
    	byte[] oldStates;
    	Rehash1 (int a, int b, double[] oldK, short[] oldV, byte[] oldS){
    		min=a; max=b; oldKeys=oldK; oldValues=oldV; oldStates=oldS;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
                if(oldStates[i] == FULL) {
                    double o = oldKeys[i];
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
    	double oldKeys[] = _set;
    	short oldValues[] = _values;
    	byte oldStates[] = _states;
    	
    	_set = new double[newCapacity];
    	_values = new short[newCapacity];
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
        
        double oldKeys[] = _set;
        short oldVals[] = _values;
        byte oldStates[] = _states;

        _set = new double[newCapacity];
        _values = new short[newCapacity];
        _states = new byte[newCapacity];

        for (int i = oldCapacity; i-- > 0;) {
            if(oldStates[i] == FULL) {
                double o = oldKeys[i];
                int index = insertKey(o);
                _values[index] = oldVals[i];
            }
        }
    }    	

    void reinitialize() {
        /**_set = null;
        _values = null;
        _states = null;*/
    	_set = new double[0];
    	_values = new short[0];
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
        	double[] doublesLocal = new double[max-min];
    		int j=0;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL) {
    				doublesLocal[j++] = _set[i];
    			}
    		}
    		if (idProc > 0)	{
        		while (intsGlobal2[idProc-1] == -1)	{
    				System.out.print("");
    			}
    			int size = atIntGlobal.get();
    			atIntGlobal.addAndGet(j);
        		intsGlobal2[idProc] = j;
        		if (j>0)
    			System.arraycopy(doublesLocal, 0, doublesGlobal, size, j);
        	}
    		else 	{
    			atIntGlobal.addAndGet(j);
        		intsGlobal2[idProc] = j;
    			System.arraycopy(doublesLocal, 0, doublesGlobal, 0, j);
    		}
    	}
    }
    protected double[] keysPar(int numProcs)	{
    	int numElemProc = _set.length/numProcs;
    	atIntGlobal = new AtomicInteger(0);
    	doublesGlobal = new double[size()];
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
    	return doublesGlobal;
    }
    /** {@inheritDoc} */
    protected double[] keys() {
        double[] keys = new double[size()];
        double[] k = _set;
        byte[] states = _states;

        for (int i = k.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            keys[j++] = k[i];
          }
        }
        return keys;
    }
    /** {@inheritDoc} */
    protected double[] keysPar (double[] array, int numProcs) {
        int size = size();
        if (array.length < size()) {
            array = new double[size];
        }
        int numElemProc = _set.length/numProcs;
    	atIntGlobal = new AtomicInteger(0);
    	doublesGlobal = new double[size()];
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
    	System.arraycopy(doublesGlobal, 0, array, 0, atIntGlobal.get());
    	return doublesGlobal;
    }

    /** {@inheritDoc} */
    protected double[] keys (double[] array) {
        int size = size();
        if (array.length < size) {
            array = new double[size];
        }

        double[] keys = _set;
        byte[] states = _states;

        for (int i = keys.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            array[j++] = keys[i];
          }
        }
        return array;
    }

    /** {@inheritDoc} */
    protected short[] toArrayValues() {
        short[] vals = new short[size()];
        short[] v = _values;
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
    		short[] shortsLocal = new short[max-min];
    		int j=0;
    		for (int i=min; i<max; i++) {
    			if (_states[i] == FULL) {
    				shortsLocal[j++] = _values[i];
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
    			System.arraycopy(shortsLocal, 0, shortsGlobal, k, j);
    		}
    		else	{
    			//System.out.println ("Procesador 0");
        		intsGlobal2[idProc] = j;
    			System.arraycopy(shortsLocal, 0, shortsGlobal, 0, j);
    			//System.out.println ("Ha acabado el procesador 0");
    		}
    	}
    }
    /** {@inheritDoc} */
    protected short[] toArrayValuesPar (int numProcs)	{
    	atIntGlobal = new AtomicInteger(0);
    	shortsGlobal = new short[size()];
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
    	return shortsGlobal;
    }
    /** {@inheritDoc} */
    protected short[] toArrayValuesPar (short[] array, int numProcs)	{
    	if (array.length < size())
    		array = new short[size()];
    	shortsGlobal = new short[_size];
    	intsGlobal2 = new int[numProcs];
    	atIntGlobal = new AtomicInteger(0);
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
    	System.arraycopy(shortsGlobal, 0, array, 0, atIntGlobal.get());
    	//System.out.print ("array dest: ");
		//for (int i=0; i<array.length; i++)	{
		//	System.out.print (", " + array[i]);
		//}
		//System.out.println();
    	return array;
    }
    /** {@inheritDoc} */
    protected short[] toArrayValues (short[] array) {
        int size = size();
        if (array.length < size) {
            array = new short[size];
        }

        short[] v = _values;
        byte[] states = _states;

        for (int i = v.length, j = 0; i-- > 0;) {
          if (states[i] == FULL) {
            array[j++] = v[i];
          }
        }
        return array;
    }
    
    public TDoubleShortIterator iterator() {
        return new TDoubleShortHashIterator(this);
    }


    /** {@inheritDoc} */
    protected void forEachKey (Consumer<? super Double> action)	{
    	for (int i=0; i<_set.length; i++)	{
    		if (_states[i] == FULL)
    			action.accept(_set[i]);
    	}
    }
    private class ForEachKey1 implements Runnable	{
    	int min, max;
    	Consumer<? super Double> action;
    	ForEachKey1 (int a, int b, Consumer<? super Double> c)	{
    		min = a; max = b; action = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)
    			if (_states[i] == FULL)
    				action.accept(_set[i]);
    	}
    }
    /** {@inheritDoc} */
    protected void forEachKeyPar (Consumer<? super Double> action, int numProcs)	{
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
    	Consumer<? super Short> action;
    	ForEachValue1 (int a, int b, Consumer<? super Short> c)	{
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
    protected void forEachValuePar (Consumer<? super Short> action, int numProcs)	{
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
    protected void forEachValue (Consumer<? super Short> action)	{
    	if (action == null)
    		throw new NullPointerException();
    	if (_size > 0)	{
    		for (int i=0; i<_values.length; i++)	{
    			action.accept(_values[i]);
    		}
    	}
    }
    
    /** {@inheritDoc} */
    public boolean increment (double key) {
        return adjustValue(key, (short)1);
    }


    /** {@inheritDoc} */
    public boolean adjustValue (double key, short amount) {
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
    public short adjustOrPutValue (double key, short adjust_amount, short put_amount) {
        int index = insertKey(key);
        final boolean isNewMapping;
        final short newValue;
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


    


    


   
    class TDoubleShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param hash the <tt>TPrimitiveHash</tt> we will be iterating over.
         */
        TDoubleShortValueHashIterator (TPrimitiveHash hash) {
            super(hash);
        }

        /** {@inheritDoc} */
        public short next() {
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
                TDoubleShortHashMap.this.removeAt(_index);
            }
            finally {
                _hash.reenableAutoCompaction(false);
            }

            _expectedSize--;
        }
    }


    class TDoubleShortHashIterator extends THashPrimitiveIterator implements TDoubleShortIterator {

        /**
         * Creates an iterator over the specified map
         *
         * @param map the <tt>TDoubleShortHashMap</tt> we will be iterating over.
         */
        TDoubleShortHashIterator (TDoubleShortHashMap map) {
            super(map);
        }

        /** {@inheritDoc} */
        public void advance() {
            moveToNextIndex();
        }

        /** {@inheritDoc} */
        public double key() {
            return _set[_index];
        }

        /** {@inheritDoc} */
        public short value() {
            return _values[_index];
        }

        /** {@inheritDoc} */
        public short setValue(short val) {
            short old = value();
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
                TDoubleShortHashMap.this.removeAt(_index);
            }
            finally {
                _hash.reenableAutoCompaction(false);
            }
            _expectedSize--;
        }
    }

    public double lastKey ()	{
    	for (int i=_set.length-1; i>=0; i--)	{
    		if (_states[i] == FULL)
    			return _set[i];
    	}
    	return no_entry_key;
    }
    
    public short lastValue ()	{
    	for (int i=_values.length-1; i>=0; i--)	{
    		if (_states[i] == FULL)
    			return _values[i];
    	}
    	return no_entry_value;
    }
} // TIntIntHashMap