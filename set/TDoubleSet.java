package PJC.set;

import java.util.Collection;

import PJC.collection.TDoubleCollection;
import PJC.iterator.TDoubleIterator;
import PJC.spliterator.TDoubleSpliterator;


public interface TDoubleSet extends TDoubleCollection {
    /**
     * Adds the specified element to this set it it is not already present. If this set already contains the element, the call leaves the set unchanged and returns false. In combination with restriction on constructors, this ensures that sets never contain duplicate elements.
     * @param e element to be added to this set
     * @return true if this set not already contain the specified element
    */
    boolean add (double e);

    /**
     * Adds all of the elements in the specified collection to this set if they're not already present. If the specified collection is also a set, the addAll operation effectively modifies this set so that its value is the union of the two sets. The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
     * @param c collection containing elements to be added to this set
     * @return true if this set changed as a result of the call
    */
    boolean addAll (Collection<? extends Double> c);
    /**
     * Adds all of the elements in the specified collection to this set if they're not already present. If the specified collection is also a set, the addAll operation effectively modifies this set so that its value is the union of the two sets. The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
     * @param c collection containing elements to be added to this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean addAllPar (Collection<? extends Double> c, int numProcs);

	/**
     * Adds all of the elements in the specified collection to this set if they're not already present. If the specified collection is also a set, the addAll operation effectively modifies this set so that its value is the union of the two sets. The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
     * @param c collection containing elements to be added to this set
     * @return true if this set changed as a result of the call
    */
    boolean addAll (TDoubleCollection c);
    /**
     * Adds all of the elements in the specified collection to this set if they're not already present. If the specified collection is also a set, the addAll operation effectively modifies this set so that its value is the union of the two sets. The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
     * @param c collection containing elements to be added to this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean addAllPar (TDoubleCollection c, int numProcs);

    /**
     * Adds all of the elements in the specified array to this set if they're not already present. The behavior of this operation is undefined if the specified array is modified while the operation is in progress.
     * @param array array of doubles containing elements to be added to this set
     * @return true if this set changed as a result of the call
    */
    boolean addAll (double[] array);
    /**
     * Adds all of the elements in the specified array to this set if they're not already present. The behavior of this operation is undefined if the specified array is modified while the operation is in progress.
     * @param array array of doubles containing elements to be added to this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean addAllPar (double[] array, int numProcs);
    
	/**
     * Removes all of the elements from this set. The set will be empty after this call returns.
    */
    void clear();

    /**
     * Returns true if this set contains the specified element.
     * @param o element whose presence in this set is to be tested
     * @return true if this set contains the specified element
    */
    boolean contains (double o);
    /**
     * Returns true if this set contains the specified element.
     * @param o element whose presence in this set is to be tested
     * @param numProcs number of threads that will be used
     * @return true if this set contains the specified element
    */
    boolean containsPar (double o, int numProcs);
    
    /**
     * Returns true if this set contains all of the elements of the specified collection. If the specified collection is also a set, this method returns true if it is a subset of this set.
     * @param c collection to be checked for containment in this set
     * @return true if this set contains all of the elements of the specified collection
    */
    boolean containsAll (Collection<?> c);
    /**
     * Returns true if this set contains all of the elements of the specified collection. If the specified collection is also a set, this method returns true if it is a subset of this set.
     * @param c collection to be checked for containment in this set
     * @param numProcs number of threads that will be used
     * @return true if this set contains all of the elements of the specified collection
    */
    boolean containsAllPar (Collection<?> c, int numProcs);

	/**
     * Returns true if this set contains all of the elements of the specified collection. If the specified collection is also a set, this method returns true if it is a subset of this set.
     * @param c collection to be checked for containment in this set
     * @return true if this set contains all of the elements of the specified collection
    */
    boolean containsAll (TDoubleCollection c);
    /**
     * Returns true if this set contains all of the elements of the specified collection. If the specified collection is also a set, this method returns true if it is a subset of this set.
     * @param c collection to be checked for containment in this set
     * @param numProcs number of threads that will be used
     * @return true if this set contains all of the elements of the specified collection
    */
    boolean containsAllPar (TDoubleCollection c, int numProcs);

	/**
     * Returns true if this set contains all of the elements of the specified array.
     * @param array array of doubles to be checked for containment in this set
     * @return true if this set contains all of the elements of the specified array
    */
    boolean containsAll (double[] array);
    /**
     * Returns true if this set contains all of the elements of the specified array.
     * @param array array of doubles to be checked for containment in this set
     * @param numProcs number of threads that will be used
     * @return true if this set contains all of the elements of the specified array
    */
    boolean containsAllPar (double[] array, int numProcs);


    /**
     * Compares the specified object with this set for equality. Returns true if the specified element is a <code>Set</code> or a <code>TDoubleSet</code>, the two sets have the same size, and every member of the specified set is contained in this set (or equivalently, every member of this set is contained in the specified set). This definition ensures that the equals method works properly across different implementations of the <code>Set</code> and <code>TDoubleSet</code> interfaces.
     * @param o object to be compared for equality with this set
     * @return true if the specified object is equal to this set
    */
    boolean equals (Object o);
    /**
     * Compares the specified object with this set for equality. Returns true if the specified element is a <code>Set</code> or a <code>TDoubleSet</code>, the two sets have the same size, and every member of the specified set is contained in this set (or equivalently, every member of this set is contained in the specified set). This definition ensures that the equals method works properly across different implementations of the <code>Set</code> and <code>TDoubleSet</code> interfaces.
     * @param o object to be compared for equality with this set
     * @param numProcs number of threads that will be used
     * @return true if the specified object is equal to this set
    */
    public boolean equalsPar (Object o, int numProcs);
    
    //public void forEach (Consumer<? super Double> action);
    //public void forEachPar (Consumer<? super Double> action, int numProcs);
    
    /**
     * Returns the value that is used to represent null. The default
     * value is generally zero, but can be changed during construction
     * of the collection.
     *
     * @return the value that represents null
     */
    double getNoEntryValue();
    
    /**
     * Returns the hash code value for this set. The hash code of a set is defined to be the sum of the hash codes of the elements in the set, where the hash code of a null element if defined to be zero.
     * @return the hash code value for this set
    */
    int hashCode();
    /**
     * Returns the hash code value for this set. The hash code of a set is defined to be the sum of the hash codes of the elements in the set, where the hash code of a null element if defined to be zero.
     * @param numProcs number of threads that will be used
     * @return the hash code value for this set
    */
    int hashCodePar (int numProcs);

    /**
     * Returns true if this set contains no elements.
     * @return true if this set contains no elements
    */
    boolean isEmpty();

    /**
     * Returns an iterator over the elements in this set. The elements are returned in no particular order.
     * @return an iterator over the elements in this set
    */
    TDoubleIterator iterator();
    
    /**
     * Removes the specified element from this set if it is present. Returns true if this set contained the element. This set will no contain the element once the call returns.
     * @param o object to be removed from this set, if present
     * @return true if this set contained the specified element
    */
    boolean remove (double o);
    /**
     * Removes the specified element from this set if it is present. Returns true if this set contained the element. This set will no contain the element once the call returns.
     * @param o object to be removed from this set, if present
     * @param numProcs number of threads that will be used
     * @return true if this set contained the specified element
    */
    boolean removePar (double o, int numProcs);
    
    /**
     * Removes from this set all of its elements that are contained in the specified collection. If the specified collection is also a set, this operation effectively modifies this set so that its value is the asymmetric set difference of the two sets.
     * @param c collection containing elements to be removed from this set
     * @return true if this set changed as a result of the call
    */
    boolean removeAll (Collection<?> c);
    /**
     * Removes from this set all of its elements that are contained in the specified collection. If the specified collection is also a set, this operation effectively modifies this set so that its value is the asymmetric set difference of the two sets.
     * @param c collection containing elements to be removed from this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean removeAllPar (Collection<?> c, int numProcs);

    /**
     * Removes from this set all of its elements that are contained in the specified collection. If the specified collection is also a set, this operation effectively modifies this set so that its value is the asymmetric set difference of the two sets.
     * @param c collection containing elements to be removed from this set
     * @return true if this set changed as a result of the call
    */
    boolean removeAll (TDoubleCollection c);
    /**
     * Removes from this set all of its elements that are contained in the specified collection. If the specified collection is also a set, this operation effectively modifies this set so that its value is the asymmetric set difference of the two sets.
     * @param c collection containing elements to be removed from this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean removeAllPar (TDoubleCollection c, int numProcs);

	/**
     * Removes from this set all of its elements that are contained in the specified array.
     * @param array array of doubles containing elements to be removed from this set
     * @return true if this set changed as a result of the call
    */
    public boolean removeAll (double[] array);
    /**
     * Removes from this set all of its elements that are contained in the specified array.
     * @param array array of doubles containing elements to be removed from this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    public boolean removeAllPar (double[] array, int numProcs);

    /**
     * Retains only the elements in this set that are contained in the specified collection. In other words, removes from this set all of its elements that are not contained in the specified collection. If the specified collection is also a set, this operation effectively modifies this set so that its value is the intersection of the two sets.
     * @param c collection containing elements to be retained in this set
     * @return true if this set changed as a result of the call
    */
    boolean retainAll (Collection<?> c);
    /**
     * Retains only the elements in this set that are contained in the specified collection. In other words, removes from this set all of its elements that are not contained in the specified collection. If the specified collection is also a set, this operation effectively modifies this set so that its value is the intersection of the two sets.
     * @param c collection containing elements to be retained in this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean retainAllPar (Collection<?> c, int numProcs);

	/**
     * Retains only the elements in this set that are contained in the specified collection. In other words, removes from this set all of its elements that are not contained in the specified collection. If the specified collection is also a set, this operation effectively modifies this set so that its value is the intersection of the two sets.
     * @param c collection containing elements to be retained in this set
     * @return true if this set changed as a result of the call
    */
    boolean retainAll (TDoubleCollection c);
    /**
     * Retains only the elements in this set that are contained in the specified collection. In other words, removes from this set all of its elements that are not contained in the specified collection. If the specified collection is also a set, this operation effectively modifies this set so that its value is the intersection of the two sets.
     * @param c collection containing elements to be retained in this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean retainAllPar (TDoubleCollection c, int numProcs);

	/**
     * Retains only the elements in this set that are contained in the specified array. In other words, removes from this set all of its elements that are not contained in the specified array.
     * @param array array of doubles containing elements to be retained in this set
     * @return true if this set changed as a result of the call
    */
    boolean retainAll (double[] array);
    /**
     * Retains only the elements in this set that are contained in the specified array. In other words, removes from this set all of its elements that are not contained in the specified array.
     * @param array array of doubles containing elements to be retained in this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean retainAllPar (double[] array, int numProcs);

    /**
     * Returns the number of elements in this set (its cardinality. If this set contains more than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
     * @return the number of elements in this set (its cardinality)
    */
    int size();
    
    /**
     * Creates a TDoubleSpliterator over the elements in this set. The <code>TDoubleSpliterator</code> reports <code>TDoubleSpliterator.DISTINCT</code>. Implementations should document the reporting of additional characteristic values.
     * @return a <code>TDoubleSpliterator</code> over the elements in this set
    */
    TDoubleSpliterator spliterator();

    /**
     * Returns an array containing all of the elements in this set. The returned array will be "safe" in that no references to it are mantained by this set. In other words, this method must allocate a new array even if this set is backed by an array. The caller is thus free to modify the returned array. This method acts as bridge between array-based and collection-based APIs.
     * @return an array containing all the elements in this set
    */
    double[] toArray();
    /**
     * Returns an array containing all of the elements in this set. The returned array will be "safe" in that no references to it are mantained by this set. In other words, this method must allocate a new array even if this set is backed by an array. The caller is thus free to modify the returned array. This method acts as bridge between array-based and collection-based APIs.
     * @param numProcs number of threads that will be used
     * @return an array containing all the elements in this set
    */
    double[] toArrayPar (int numProcs);

    /**
     * Returns an array containing all of the elements in this set. If this set fits in the specified array, it is returned therein. Otherwise, a new array is allocated with the size of this set. If this set fits in the specified array with room to spare, the element in the array immediately following the end of the set is set to null. Like the <code>toArray()</code> method, this method acts as bridge between array-based and collection-based APIs.
     * @param a the array into which the elements of this set are to be stored, if it is big enough; otherwise, a new array is allocated for this purpose
     * @return an array containing the elements in this set
     */
    double[] toArray (double[] a);
    /**
     * Returns an array containing all of the elements in this set. If this set fits in the specified array, it is returned therein. Otherwise, a new array is allocated with the size of this set. If this set fits in the specified array with room to spare, the element in the array immediately following the end of the set is set to null. Like the <code>toArray()</code> method, this method acts as bridge between array-based and collection-based APIs.
     * @param a the array into which the elements of this set are to be stored, if it is big enough; otherwise, a new array is allocated for this purpose
     * @param numProcs number of threads that will be used
     * @return an array containing the elements in this set
     */
    double[] toArrayPar (double[] a, int numProcs);
    
    public String toStringPar (int numProcs);


} // TDoubleSet
