package PJC.set;

import java.util.Collection;

import PJC.collection.TByteCollection;
import PJC.iterator.TByteIterator;
import PJC.spliterator.TByteSpliterator;


public interface TByteSet extends TByteCollection {
    /**
     * Adds the specified element to this set it it is not already present. If this set already contains the element, the call leaves the set unchanged and returns false. In combination with restriction on constructors, this ensures that sets never contain duplicate elements.
     * @param e element to be added to this set
     * @return true if this set not already contain the specified element
    */
    boolean add (byte e);

    /**
     * Adds all of the elements in the specified collection to this set if they're not already present. If the specified collection is also a set, the addAll operation effectively modifies this set so that its value is the union of the two sets. The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
     * @param c collection containing elements to be added to this set
     * @return true if this set changed as a result of the call
    */
    boolean addAll (Collection<? extends Byte> c);
    /**
     * Adds all of the elements in the specified collection to this set if they're not already present. If the specified collection is also a set, the addAll operation effectively modifies this set so that its value is the union of the two sets. The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
     * @param c collection containing elements to be added to this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean addAllPar (Collection<? extends Byte> c, int numProcs);

	/**
     * Adds all of the elements in the specified collection to this set if they're not already present. If the specified collection is also a set, the addAll operation effectively modifies this set so that its value is the union of the two sets. The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
     * @param c collection containing elements to be added to this set
     * @return true if this set changed as a result of the call
    */
    boolean addAll (TByteCollection c);
    /**
     * Adds all of the elements in the specified collection to this set if they're not already present. If the specified collection is also a set, the addAll operation effectively modifies this set so that its value is the union of the two sets. The behavior of this operation is undefined if the specified collection is modified while the operation is in progress.
     * @param c collection containing elements to be added to this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean addAllPar (TByteCollection c, int numProcs);

    /**
     * Adds all of the elements in the specified array to this set if they're not already present. The behavior of this operation is undefined if the specified array is modified while the operation is in progress.
     * @param array array of bytes containing elements to be added to this set
     * @return true if this set changed as a result of the call
    */
    boolean addAll (byte[] array);
    /**
     * Adds all of the elements in the specified array to this set if they're not already present. The behavior of this operation is undefined if the specified array is modified while the operation is in progress.
     * @param array array of bytes containing elements to be added to this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean addAllPar (byte[] array, int numProcs);
    
	/**
     * Removes all of the elements from this set. The set will be empty after this call returns.
    */
    void clear();

    /**
     * Returns true if this set contains the specified element.
     * @param o element whose presence in this set is to be tested
     * @return true if this set contains the specified element
    */
    boolean contains (byte o);
    /**
     * Returns true if this set contains the specified element.
     * @param o element whose presence in this set is to be tested
     * @param numProcs number of threads that will be used
     * @return true if this set contains the specified element
    */
    boolean containsPar (byte o, int numProcs);
    
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
    boolean containsAll (TByteCollection c);
    /**
     * Returns true if this set contains all of the elements of the specified collection. If the specified collection is also a set, this method returns true if it is a subset of this set.
     * @param c collection to be checked for containment in this set
     * @param numProcs number of threads that will be used
     * @return true if this set contains all of the elements of the specified collection
    */
    boolean containsAllPar (TByteCollection c, int numProcs);

	/**
     * Returns true if this set contains all of the elements of the specified array.
     * @param array array of bytes to be checked for containment in this set
     * @return true if this set contains all of the elements of the specified array
    */
    boolean containsAll (byte[] array);
    /**
     * Returns true if this set contains all of the elements of the specified array.
     * @param array array of bytes to be checked for containment in this set
     * @param numProcs number of threads that will be used
     * @return true if this set contains all of the elements of the specified array
    */
    boolean containsAllPar (byte[] array, int numProcs);


    /**
     * Compares the specified object with this set for equality. Returns true if the specified element is a <code>Set</code> or a <code>TByteSet</code>, the two sets have the same size, and every member of the specified set is contained in this set (or equivalently, every member of this set is contained in the specified set). This definition ensures that the equals method works properly across different implementations of the <code>Set</code> and <code>TByteSet</code> interfaces.
     * @param o object to be compared for equality with this set
     * @return true if the specified object is equal to this set
    */
    boolean equals (Object o);
    /**
     * Compares the specified object with this set for equality. Returns true if the specified element is a <code>Set</code> or a <code>TByteSet</code>, the two sets have the same size, and every member of the specified set is contained in this set (or equivalently, every member of this set is contained in the specified set). This definition ensures that the equals method works properly across different implementations of the <code>Set</code> and <code>TByteSet</code> interfaces.
     * @param o object to be compared for equality with this set
     * @param numProcs number of threads that will be used
     * @return true if the specified object is equal to this set
    */
    public boolean equalsPar (Object o, int numProcs);
    
    //public void forEach (Consumer<? super Byte> action);
    //public void forEachPar (Consumer<? super Byte> action, int numProcs);
    
    /**
     * Returns the value that is used to represent null. The default
     * value is generally zero, but can be changed during construction
     * of the collection.
     *
     * @return the value that represents null
     */
    byte getNoEntryValue();
    
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
    TByteIterator iterator();
    
    /**
     * Removes the specified element from this set if it is present. Returns true if this set contained the element. This set will no contain the element once the call returns.
     * @param o object to be removed from this set, if present
     * @return true if this set contained the specified element
    */
    boolean remove (byte o);
    /**
     * Removes the specified element from this set if it is present. Returns true if this set contained the element. This set will no contain the element once the call returns.
     * @param o object to be removed from this set, if present
     * @param numProcs number of threads that will be used
     * @return true if this set contained the specified element
    */
    boolean removePar (byte o, int numProcs);
    
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
    boolean removeAll (TByteCollection c);
    /**
     * Removes from this set all of its elements that are contained in the specified collection. If the specified collection is also a set, this operation effectively modifies this set so that its value is the asymmetric set difference of the two sets.
     * @param c collection containing elements to be removed from this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean removeAllPar (TByteCollection c, int numProcs);

	/**
     * Removes from this set all of its elements that are contained in the specified array.
     * @param array array of bytes containing elements to be removed from this set
     * @return true if this set changed as a result of the call
    */
    public boolean removeAll (byte[] array);
    /**
     * Removes from this set all of its elements that are contained in the specified array.
     * @param array array of bytes containing elements to be removed from this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    public boolean removeAllPar (byte[] array, int numProcs);

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
    boolean retainAll (TByteCollection c);
    /**
     * Retains only the elements in this set that are contained in the specified collection. In other words, removes from this set all of its elements that are not contained in the specified collection. If the specified collection is also a set, this operation effectively modifies this set so that its value is the intersection of the two sets.
     * @param c collection containing elements to be retained in this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean retainAllPar (TByteCollection c, int numProcs);

	/**
     * Retains only the elements in this set that are contained in the specified array. In other words, removes from this set all of its elements that are not contained in the specified array.
     * @param array array of bytes containing elements to be retained in this set
     * @return true if this set changed as a result of the call
    */
    boolean retainAll (byte[] array);
    /**
     * Retains only the elements in this set that are contained in the specified array. In other words, removes from this set all of its elements that are not contained in the specified array.
     * @param array array of bytes containing elements to be retained in this set
     * @param numProcs number of threads that will be used
     * @return true if this set changed as a result of the call
    */
    boolean retainAllPar (byte[] array, int numProcs);

    /**
     * Returns the number of elements in this set (its cardinality. If this set contains more than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
     * @return the number of elements in this set (its cardinality)
    */
    int size();
    
    /**
     * Creates a TByteSpliterator over the elements in this set. The <code>TByteSpliterator</code> reports <code>TByteSpliterator.DISTINCT</code>. Implementations should document the reporting of additional characteristic values.
     * @return a <code>TByteSpliterator</code> over the elements in this set
    */
    TByteSpliterator spliterator();

    /**
     * Returns an array containing all of the elements in this set. The returned array will be "safe" in that no references to it are mantained by this set. In other words, this method must allocate a new array even if this set is backed by an array. The caller is thus free to modify the returned array. This method acts as bridge between array-based and collection-based APIs.
     * @return an array containing all the elements in this set
    */
    byte[] toArray();
    /**
     * Returns an array containing all of the elements in this set. The returned array will be "safe" in that no references to it are mantained by this set. In other words, this method must allocate a new array even if this set is backed by an array. The caller is thus free to modify the returned array. This method acts as bridge between array-based and collection-based APIs.
     * @param numProcs number of threads that will be used
     * @return an array containing all the elements in this set
    */
    byte[] toArrayPar (int numProcs);

    /**
     * Returns an array containing all of the elements in this set. If this set fits in the specified array, it is returned therein. Otherwise, a new array is allocated with the size of this set. If this set fits in the specified array with room to spare, the element in the array immediately following the end of the set is set to null. Like the <code>toArray()</code> method, this method acts as bridge between array-based and collection-based APIs.
     * @param a the array into which the elements of this set are to be stored, if it is big enough; otherwise, a new array is allocated for this purpose
     * @return an array containing the elements in this set
     */
    byte[] toArray (byte[] a);
    /**
     * Returns an array containing all of the elements in this set. If this set fits in the specified array, it is returned therein. Otherwise, a new array is allocated with the size of this set. If this set fits in the specified array with room to spare, the element in the array immediately following the end of the set is set to null. Like the <code>toArray()</code> method, this method acts as bridge between array-based and collection-based APIs.
     * @param a the array into which the elements of this set are to be stored, if it is big enough; otherwise, a new array is allocated for this purpose
     * @param numProcs number of threads that will be used
     * @return an array containing the elements in this set
     */
    byte[] toArrayPar (byte[] a, int numProcs);
    
    public String toStringPar (int numProcs);


} // TByteSet
