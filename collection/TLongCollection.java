package PJC.collection;
import java.util.Collection;
import java.util.function.*;

import PJC.iterator.TLongIterator;
import PJC.spliterator.TLongSpliterator;

public interface TLongCollection {
	static final long serialVersionUID = 1L;

    /**
     * Ensures that this collection contains the specified element. Returns true if
     * this collection changed as a result of the call. Returns false if this collection
     * does not permit duplicates and already contains the specified element.
     * @param e element whose presence in this collection is to be ensured
     * @return true if this collection changed as a result of the call
     */
    boolean add (long e);
	
    /**
     * Adds all of the elements in the specified collection to this collection. The
     * behavior of this operation is undefined if the specified <tt>Collection</tt>
     * is modified while the operation is in progress.
     * @param c <tt>Collection</tt> containing elements to be added to this collection
     * @return true if this collection changed as a result of the call
     */
    boolean addAll (Collection<? extends Long> c);
    
    /**
     * Adds all of the elements in the specified collection to this collection. The
     * behavior of this operation is undefined if the specified <tt>Collection</tt>
     * is modified while the operation is in progress.
     * @param c <tt>Collection</tt> containing elements to be added to this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection changed as a result of the call
     */
    boolean addAllPar (Collection<? extends Long> c, int numProcs);

    /**
     * Adds all of the elements in the specified collection to this collection. The
     * behavior of this operation is undefined if the specified <tt>TLongCollection</tt>
     * is modified while the operation is in progress.
     * @param c <tt>TLongCollection</tt> containing elements to be added to this collection
     * @return true if this collection changed as a result of the call
     */
    boolean addAll (TLongCollection c);
    
    /**
     * Adds all of the elements in the specified collection to this collection. The
     * behavior of this operation is undefined if the specified <tt>TLongCollection</tt>
     * is modified while the operation is in progress.
     * @param c <tt>TLongCollection</tt> containing elements to be added to this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection changed as a result of the call
     */
    boolean addAllPar (TLongCollection c, int numProcs);

    /**
     * Adds all of the elements in the specified collection to this collection. The
     * behavior of this operation is undefined if the specified array is modified 
     * while the operation is in progress.
     * @param array array of longs containing elements to be added to this collection
     * @return true if this collection changed as a result of the call
     */
    boolean addAll (long[] array);
    
    /**
     * Adds all of the elements in the specified collection to this collection. The
     * behavior of this operation is undefined if the specified array is modified 
     * while the operation is in progress.
     * @param array array of longs containing elements to be added to this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection changed as a result of the call
     */
    boolean addAllPar (long[] array, int numProcs);

    /**
     * Removes all of the elements from this collection. The collection will be empty
     * after this method returns.
     */
    void clear();
    
    /**
     * Returns true if this collection contains the specified element.
     * @param o element whose presence in this collection is to be tested
     * @return true if this collection contains the specified element
     */
    boolean contains (long o);
    
    /**
     * Returns true if this collection contains the specified element.
     * @param o element whose presence in this collection is to be tested
     * @param numProcs number of threads that will be used
     * @return true if this collection contains the specified element
     */
    boolean containsPar (long o, int numProcs);
	
    /**
     * Returns true if this collection contains all of the elements in the specified
     * collection.
     * @param c <tt>Collection</tt> to be checked for containment in this collection
     * @return true if this collection contains all of the elements in the specified
     * collection
     */
    boolean containsAll (Collection<?> c);
    
    /**
     * Returns true if this collection contains all of the elements in the specified
     * collection.
     * @param c <tt>Collection</tt> to be checked for containment in this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection contains all of the elements in the specified
     * collection
     */
    boolean containsAllPar (Collection<?> c, int numProcs);

    /**
     * Returns true if this collection contains all of the elements in the specified
     * collection.
     * @param c <tt>TLongCollection</tt> to be checked for containment in this collection
     * @return true if this collection contains all of the elements in the specified
     * collection
     */
    boolean containsAll (TLongCollection c);
    
    /**
     * Returns true if this collection contains all of the elements in the specified
     * collection.
     * @param c <tt>TLongCollection</tt> to be checked for containment in this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection contains all of the elements in the specified
     * collection
     */
    boolean containsAllPar (TLongCollection c, int numProcs);


    /**
     * Returns true if this collection contains all of the elements in the specified
     * array.
     * @param array array of longs to be checked for containment in this collection
     * @return true if this collection contains all of the elements in the specified
     * collection
     */
    boolean containsAll (long[] array);
    
    /**
     * Returns true if this collection contains all of the elements in the specified
     * array.
     * @param array array of longs to be checked for containment in this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection contains all of the elements in the specified
     * collection
     */
    boolean containsAllPar (long[] array, int numProcs);

    /**
     * Compares the specified object with this collection for equality.
     * @param o object to be compared for equality with this collection
     * @return true if the specified object is equal to this collection
     */
    boolean equals (Object o);
    
    /**
     * Compares the specified object with this collection for equality.
     * @param o object to be compared for equality with this collection
     * @param numProcs number of threads that will be used
     * @return true if the specified object is equal to this collection
     */
    boolean equalsPar (Object o, int numProcs);

    /**
     * Returns the hash code value for this collection.
     * @return the hash code value for this collection
     */
    int hashCode();

    /**
     * Returns true if this collection contains no elements.
     * @return true if this collection contains no elements
     */
    boolean isEmpty();
	
    /**
     * Returns an iterator over the elements in this collection.
     * @return an <tt>Iterator</tt> over the elements in this collection
     */
    TLongIterator iterator();
    
    /**
     * Removes a single instance of the specified element from this collection,
     * if it is present. Returns true if this collection contained the specified
     * element.
     * @param o element to be removed from this collection, if present
     * @return true if an element was removed as a result of this call
     */
    boolean remove (long o);
    
    /**
     * Removes a single instance of the specified element from this collection,
     * if it is present. Returns true if this collection contained the specified
     * element.
     * @param o element to be removed from this collection, if present
     * @param numProcs number of threads that will be used
     * @return true if an element was removed as a result of this call
     */
    boolean removePar (long o, int numProcs);

    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection. After this call returns, the elements of the specified
     * collection will be ordered.
     * @param c <tt>Collection</tt> containing elements to be removed from this collection
     * @return true if this collection changed as a result of the call
     */
    boolean removeAll (Collection<?> c);
    
    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection. After this call returns, the elements of the specified
     * collection will be ordered.
     * @param c <tt>TLongCollection</tt> containing elements to be removed from this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection changed as a result of the call
     */
    boolean removeAllPar (Collection<?> c, int numProcs);


    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection. After this call returns, the elements of the specified
     * collection will be ordered.
     * @param c <tt>TLongCollection</tt> containing elements to be removed from this collection
     * @return true if this collection changed as a result of the call
     */
    boolean removeAll (TLongCollection c);
    
    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection. After this call returns, the elements of the specified
     * collection will be ordered.
     * @param c <tt>TLongCollection</tt> containing elements to be removed from this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection changed as a result of the call
     */
    boolean removeAllPar (TLongCollection c, int numProcs);


    /**
     * Removes all of this array's elements that are also contained in the specified 
     * array. After this call returns, the elements of the specified array will be ordered.
     * @param array array of longs containing elements to be removed from this collection
     * @return true if this collection changed as a result of the call
     */
    boolean removeAll (long[] array);
    
    /**
     * Removes all of this array's elements that are also contained in the specified 
     * array. After this call returns, the elements of the specified array will be ordered.
     * @param array array of longs containing elements to be removed from this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection changed as a result of the call
     */
    boolean removeAllPar (long[] array, int numProcs);

    /**
     * Removes all of the elements of this collection that satisfy the given predicate.
     * Errors or runtime exception thrown during iteration or by the predicate are
     * relayed to the caller.
     * @param filter a predicate which returns true for elements to be removed
     * @return true if any elements were removed
     */
	boolean removeIf (Predicate<? super Long> filter);
	
	/**
	 * Retains only the elements in this collection that are contained in the specified
	 * collection. In other words, removes from this collection all of its elements that
	 * are not contained in the specified collection.
	 * @param c collection containing elements to be retained in this collection
	 * @return true if this collection changed as a result of the call
	 */
    boolean retainAll (Collection<?> c);
    
    /**
	 * Retains only the elements in this collection that are contained in the specified
	 * collection. In other words, removes from this collection all of its elements that
	 * are not contained in the specified collection.
	 * @param c collection containing elements to be retained in this collection
	 * @param numProcs number of threads that will be used
	 * @return true if this collection changed as a result of the call
	 */
    boolean retainAllPar (Collection<?> c, int numProcs);


    /**
	 * Retains only the elements in this collection that are contained in the specified
	 * collection. In other words, removes from this collection all of its elements that
	 * are not contained in the specified collection.
	 * @param c collection containing elements to be retained in this collection
	 * @return true if this collection changed as a result of the call
	 */
    boolean retainAll (TLongCollection c);
    
    /**
	 * Retains only the elements in this collection that are contained in the specified
	 * collection. In other words, removes from this collection all of its elements that
	 * are not contained in the specified collection.
	 * @param c collection containing elements to be retained in this collection
	 * @param numProcs number of threads that will be used
	 * @return true if this collection changed as a result of the call
	 */
    boolean retainAllPar (TLongCollection c, int numProcs);

    /**
	 * Retains only the elements in this collection that are contained in the specified
	 * array of longs. In other words, removes from this collection all of its elements that
	 * are not contained in the specified array.
	 * @param array array of longs containing elements to be retained in this collection
	 * @return true if this collection changed as a result of the call
	 */
    boolean retainAll (long[] array);
    
    /**
	 * Retains only the elements in this collection that are contained in the specified
	 * array of longs. In other words, removes from this collection all of its elements that
	 * are not contained in the specified array.
	 * @param array array of longs containing elements to be retained in this collection
	 * @param numProcs number of threads that will be used
	 * @return true if this collection changed as a result of the call
	 */
    boolean retainAllPar (long[] array, int numProcs);

    /**
     * Returns the number of elements in this collection.
     * @return the number of elements in this collection
     */
    int size();
	
    /**
     * Creates a Spliterator over the elements in this collection.
     * @return a spliterator over the elements in this collection
     */
    TLongSpliterator spliterator();

    /**
     * Returns an array containing all of the elements in this collection. The
     * returned array will be "safe" in that no references to it are maintained
     * by this collection. In other words, this method allocate a new array even
     * if this collection is backed to an array. The caller is thus free to modify
     * the returned array.
     * @return an array containing all of the elements in this collection
     */
    long[] toArray();
    
    /**
     * Returns an array containing all of the elements in this collection. The
     * returned array will be "safe" in that no references to it are maintained
     * by this collection. In other words, this method allocate a new array even
     * if this collection is backed to an array. The caller is thus free to modify
     * the returned array.
     * @param numProcs number of threads that will be used
     * @return an array containing all of the elements in this collection
     */
    long[] toArrayPar(int numProcs);

    /**
     * Returns an array containing all of the elements in this collection. If the
     * collection fits in the specified array, it is returned therein. Otherwise,
     * a new array is allocated with the size of this collection.
     * @param a the array into which the elements of this collection are to be stored,
     * if it is big enough; otherwise, a new array is allocated for this purpose.
     * @return an array containing all of the elements in this collection
     */
    long[] toArray (long[] a);
    
    /**
     * Returns an array containing all of the elements in this collection. If the
     * collection fits in the specified array, it is returned therein. Otherwise,
     * a new array is allocated with the size of this collection.
     * @param a the array into which the elements of this collection are to be stored,
     * if it is big enough; otherwise, a new array is allocated for this purpose.
     * @param numProcs number of threads that will be used
     * @return an array containing all of the elements in this collection
     */
    long[] toArrayPar (long[] a, int numProcs);
	
	
	
	
	
	
	
	
    /**
     * Returns the value that is used to represent null. The default
     * value is generally zero, but can be changed during construction
     * of the collection.
     *
     * @return the value that represents null
     */
    long getNoEntryValue();

} // TLongCollection