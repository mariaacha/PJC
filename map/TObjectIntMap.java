package PJC.map;

import java.util.Map;
import java.util.function.*;

import PJC.collection.TIntCollection;
import PJC.map.iterator.TObjectIntIterator;
import PJC.set.TObjectSet;


/**
 * Interface for a primitive map of Object keys and int values.
 */
public interface TObjectIntMap {

    /**
     * Removes all of the mappings from this map. The map will be 
     * empty after this call returns 
     */
    public void clear();
    
    /**
     * Attempts to compute a mapping for the specified key and its current mapped
     * value (or null if there is no current mapping). If the function returns null,
     * the mapping is removed (or remain absent if initially absent). If the function
     * itself throws an exception, the exception is rethrown, and the current mapping
     * is left unchanged.
     * @param key key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if non
     */
    public int compute (Object key, BiFunction<? super Object, ? super Integer,
    		? extends Integer> remappingFunction);
    
    /**
     * If the specified key is not already associated with a value (or is mapped to
     * null), attempts to compute its value using the given mapping function ante enters
     * it into this map unless null.
     * If the function returns null no mapping is recorded. If the function itself throws
     * an exception, the exception is rethrown, and no mapping is recorded. The most
     * common usage is to construct a new object serving as an initial mapped value or
     * memorized result.
     * @param key key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the current (existing or computed) value is to be associated
     */
    public int comptueIfAbsent (Object key, Function<? super Object,
    		? extends Integer> remappingFunction);

    /**
     * If the value for the specified key is present and non-null, attempts to compute
     * a new mapping given the key and its current mapped value.
     * If the function returns null, the mapping is removed. If the function itself throws
     * an exception, the exception is rethrown, and the current mapping is left unchanged.
     * @param key key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     */
    public int computeIfPresent (Object key, BiFunction<? super Object, ? super Integer,
    		? extends Integer> remappingFunction);
    
    /**
     * Returns true if this map contains a mapping for the specified key. There
     * can be at most one such mapping.
     * @param key key whose presence in this map is to be tested
     * @return true if this map contains a mapping for the specified key
     */
    public boolean containsKey (Object key);
    
    /**
     * Returns true if this map maps one or more keys to the specified value.
     * @param value value whose presence in this map is to be tested
     * @return true if this map maps one or more keys to the specified value
     */
    public boolean containsValue (int value);
    
    /**
     * Returns true if this map maps one or more keys to the specified value.
     * @param value value whose presence in this map is to be tested
     * @return true if this map maps one or more keys to the specified value
     */
    public boolean containsValuePar (int value, int numProcs);

    /**
     * Compares the specified object with this map for equality. Returns true if
     * the given object is also a map and the two maps represent the same mappings.
     * This ensures that the equals method works properly across different 
     * implementations of the <code>Map</code> and <code>TIntIntMap</code> interfaces.
     * @param o object to be compared for equality with this map
     * @return true if the specified object is equal to this map
     */
    boolean equals (Object o);
    /**
     * Compares the specified object with this map for equality. Returns true if
     * the given object is also a map and the two maps represent the same mappings.
     * This ensures that the equals method works properly across different 
     * implementations of the <code>Map</code> and <code>TIntIntMap</code> interfaces.
     * @param o object to be compared for equality with this map
     * @param numProcs number of threads that will be used
     * @return true if the specified object is equal to this map
     */
    boolean equalsPar (Object o, int numProcs);

    /**
     * Performs the given action for each pair in this map until all of them have been
     * processed or the action throws an exception. Exceptions thrown by the action are
     * replayed to the caller.
     * @param action the action to be performed for each entry
     */
    public void forEach (BiConsumer<? super Object, ? super Integer> action);
    
    /**
     * Performs the given action for each pair in this map until all of them have been
     * processed or the action throws an exception. Exceptions thrown by the action are
     * replayed to the caller.
     * @param action the action to be performed for each entry
     * @param numProcs number of threads that will be used
     */
    public void forEachPar (BiConsumer<? super Object, ? super Integer> action, int numProcs);

    /**
     * Returns the value to which the specified key is mapped, or null if this map
     * contains no mapping for the key. There can be at most one such mapping.
     * If this map permits null values, then a return value of null does not 
     * necessarily indicates that the map contains no mapping for the key; it's also
     * possible that the map explicitly maps the key to null. The <code>containsKey</code>
     * operation may be used to distinguish these two cases.
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped, or null if this map
     * contains no mapping for the key
     */
    public int get (Object key);

    /**
     * Returns the value that will be returned from {@link #get} or {@link #put} if no
     * entry exists for a given key. The default value is generally zero, but can be
     * changed during construction of the collection.
     *
     * @return the value that represents a null key in this collection.
     */
    public Object getNoEntryKey();

    /**
     * Returns the value that will be returned from {@link #get} or {@link #put} if no
     * entry exists for a given key. The default value is generally zero, but can be
     * changed during construction of the collection.
     *
     * @return the value that represents a null value in this collection.
     */
    public int getNoEntryValue();
    
    /**
     * Returns the value to which the specified key is mapped, or defaultValue if this
     * map contains no mapping for the key.
     * @param key the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or defaultValue if
     * this map contains no mapping for the key
     */
    public int getOrDefault (Object key, int defaultValue);
    
    /**
     * Returns the hash code value for this map. The hash code of a map is defined to be
     * the sum of the hash codes of each entry in the map.
     * @return the hash code value for this map
     */
    public int hashCode();
    
    /**
     * Returns the hash code value for this map. The hash code of a map is defined to be
     * the sum of the hash codes of each entry in the map.
     * @param numProcs number of threads that will be used
     * @return the hash code value for this map
     */
    public int hashCodePar (int numProcs);

    /**
     * Returns true if this map contains no key-value mappings.
     * @return true if this map contains no key-value mappings
     */
    public boolean isEmpty();

    /**
     * Returns a TIntSet view of the keys contained in this map. The set is backed by
     * the map, so changes to the map are reflected in the set, and vice-versa. If the
     * map is modified while an iteration over the set is in progress, the results of
     * the iteration are undefined. The set supports element removal, which removes the
     * corresponding mapping from the map. It does not support the add or addAll operations.
     * @return a set view of the keys contained in this map.
     */
    public TObjectSet keySet();
    
    public TObjectIntIterator iterator();
    
    /**
     * If the specified key is not already associated with a value or is associated with
     * null, associates it with the given non-null value. Otherwise, replaces the associated
     * value with the results of the given remapping function, ore removes if the result
     * is null. This method may be of use when combining multiple mapped  values for a key.
     * @param key key with which the resulting value is to be associated
     * @param value the non-null value to be merged with the existing value associated with
     * the key, or, if no existing value or a null value is associated with the key, to be
     * associated with the key
     * @param remappingFunction the function to recompute a value if present
     * @return the new value associated with the specified key, or null if no value is
     * associated with the key
     */
    public int merge (Object key, int value, BiFunction<? super Object, ? super Integer,
    		? extends Integer> remappingFunction);
     
    /**
     * Associates the specified value with the specified key in this map. If the map previously
     * contained a mapping for the key, the old value is replaced by the specified value. A
     * map m is said to contain a mapping for a key k if and only if <code>m.containsKey(k)</code>
     * would return true.
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with key, or null if there was no mapping for key. A
     * null return can also indicate that the map previously associated null with key, if the
     * implementation supports null values
     */
    public int put(Object key, int value );

    /**
     * Copies all of the mappings from the specified map to this map. The effect of this call
     * is equivalent to that of calling <code>put (k, v)</code> on this map once for each
     * mapping from key k to value v in the specified map. The behavior of this operation is
     * undefined if the specified map is modified while the operation is in progress.
     * @param m mappings to be stored in this map
     */
    public void putAll (Map<? extends Object, ? extends Integer> m);
    /**
     * Copies all of the mappings from the specified map to this map. The effect of this call
     * is equivalent to that of calling <code>put (k, v)</code> on this map once for each
     * mapping from key k to value v in the specified map. The behavior of this operation is
     * undefined if the specified map is modified while the operation is in progress.
     * @param m mappings to be stored in this map
     * @param numProcs number of threads that will be used
     */
    public void putAllPar (Map<? extends Object, ? extends Integer> m, int numProcs);

    /**
     * Copies all of the mappings from the specified map to this map. The effect of this call
     * is equivalent to that of calling <code>put (k, v)</code> on this map once for each
     * mapping from key k to value v in the specified map. The behavior of this operation is
     * undefined if the specified map is modified while the operation is in progress.
     * @param m mappings to be stored in this map
     */
    public void putAll (TObjectIntMap m);
    /**
     * Copies all of the mappings from the specified map to this map. The effect of this call
     * is equivalent to that of calling <code>put (k, v)</code> on this map once for each
     * mapping from key k to value v in the specified map. The behavior of this operation is
     * undefined if the specified map is modified while the operation is in progress.
     * @param m mappings to be stored in this map
     * @param numProcs number of threads that will be used
     */
    public void putAllPar (TObjectIntMap m, int numProcs);
    
    /**
     * Copies all of the mappings from the specified arrays of keys and values to this map. 
     * The effect of this call is equivalent to that of calling <code>put (k, v)</code> on this 
     * map once for each mapping from key k to value v in the specified map. The behavior of 
     * this operation is undefined if at least one of the specified arrays are modified while 
     * the operation is in progress.
     * @param keys array of keys to be stored in this map
     * @param values array of values to be stored in this map
     */
    public void putAll (Object[] keys, int[] values);
    /**
     * Copies all of the mappings from the specified arrays of keys and values to this map. 
     * The effect of this call is equivalent to that of calling <code>put (k, v)</code> on this 
     * map once for each mapping from key k to value v in the specified map. The behavior of 
     * this operation is undefined if at least one of the specified arrays are modified while 
     * the operation is in progress.
     * @param keys array of keys to be stored in this map
     * @param values array of values to be stored in this map
     * @param numProcs number of threads that will be used
     */
    public void putAllPar (Object[] keys, int[] values, int numProcs);
    
    /**
     * If the specified key is not already associated with a value (or is mapped to null)
     * associates it with the given value and returns null; else, returns the current value.
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or null if there was no
     * mapping for the key. A null return can also indicates that the map previously associated
     * null with the key, if the implementation supports null values
     */
    public int putIfAbsent (Object key, int value );

    /**
     * Removes the mapping for a key from this map if it is present. Returns the value to
     * which this map previously associated the key, or null if the map contained no mapping
     * for the key.
     * If this map permits null values, then a return value of null does not necessarily indicate
     * that the map contained no mapping for the key; it's also possible that the map explicitly
     * mapped the key to null. The map will no contain a mapping for the specified key one the call
     * returns. 
     * @param key key whose mapping is to be removed from the map 
     * @return the previous value associated with key, or null if there was no mapping for key
     */
    public int remove (Object key );
    
    /**
     * Removes the entry for the specified key only if it's currently mapped to the specified value.
     * @param key key with which the specified value is to be associated
     * @param value value expected to be associated with the specified key
     * @return true if the value was removed
     */
    public boolean remove (Object key, int value);
    
    /**
     * Replaces the entry for the specified key only if it's currently mapped to some value.
     * @param key key with which the specified value is associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or null if there was no
     * mapping for the key. A null return can also indicates that the map previously associated
     * null with the key, if the implementation supports null values.
     */
    public int replace (Object key, int value);
    
    /**
     * Replaces the entry for the specified key only if currently mapped to the specified value.
     * @param key key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return true if the value was replace
     */
    public boolean replace (Object key, int oldValue, int newValue);
    
    /**
     * Replaces each entry's value with the result of invoking the given function on that
     * entry until all of them have been processed or the function throws an exception.
     * Exceptions thrown by the function are relayed to the caller.
     * @param function the function to apply to each entry
     */
    public void replaceAll (BiFunction<? super Object, ? super Integer,
    		? extends Integer> function);
    
    /**
     * Replaces each entry's value with the result of invoking the given function on that
     * entry until all of them have been processed or the function throws an exception.
     * Exceptions thrown by the function are relayed to the caller.
     * @param function the function to apply to each entry
     * @param numProcs number of threads that will be used
     */
    public void replaceAllPar (BiFunction<? super Object, ? super Integer,
    		? extends Integer> function, int numProcs);

    /**
     * Returns the number of key-value mappings in this map. If hte map contains more than
     * Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
     * @return the number of key-value mappings in this map
     */
    public int size();
    
    /**
     * Returns a Collection view of the values contained in this map. The collection is
     * backed by the map, so changes to the map are reflected in the collection, and vice-versa.
     * If the map is modified while an iteration over the collection is in progress, the results
     * of the iteration are undefined. The collection supports elemental removal, which removes
     * the corresponding mapping from the map. It does not support the add or addAll operations.
     * @return a collection view of the values contained in this map
     */
    public TIntCollection values();

}