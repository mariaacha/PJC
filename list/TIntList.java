package PJC.list;

import java.util.*;
import java.util.function.*;

import PJC.collection.TIntCollection;
import PJC.iterator.TIntIterator;
import PJC.list.iterator.TIntListIterator;

public interface TIntList extends TIntCollection {


    /**
     * Appends the specified element to the end of this list.
     *
     * @param e element to be appended to this list
     * @return true
     */
    public boolean add (int e);

    /**
     * Inserts the specified element at the specified position in this list.
     * Shifts the element currently at that position (if any) and any subsequent
     * elements to the right (adds one to their indices).
     *
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     */
     public void add (int index, int element);
     
     /**
      * Appends all of the elements in the specified <tt>Collection</tt> to the
      * end of this list, in the same order. The behavior of this operation is
      * undefined if the specified collection is modified while the operation is
      * in progress.
      * @param c <tt>Collection</tt> containing elements to be added to this list
      */
     public boolean addAll (Collection<? extends Integer> c);

     /**
      * Appends all of the elements in the specified <tt>TIntCollection</tt> to the
      * end of this list, in the same order. The behavior of this operation is
      * undefined if the specified collection is modified while the operation is
      * in progress.
      * @param c <tt>TIntCollection</tt> containing elements to be added to this list
      */
     public boolean addAll (TIntCollection c);
     
     /**
      * Appends all of the elements in the specified array of ints to the end of 
      * this list, in the same order. The behavior of this operation is undefined 
      * if the specified array is modified while the operation is in progress.
      * @param array array of ints containing elements to be added to this list
      */
     public boolean addAll (int[] array);
     
     /**
      * Inserts all of the elements in the specified <tt>Collection</tt> into this
      * list at the specified position. Shifts the element currently at that position
      * (if any) and any subsequent elements will appear in this list in the same order.
      * The behavior of this operation is undefined if the specified collection is
      * modified while the operation is in progress.
      * @param index index at which to insert the first element from the specified collection
      * @param c collection containing elements to be added to this list
      * @return true if this list changed as a result of the call
      */
     public boolean addAll (int index, Collection<? extends Integer> c);
     
     /**
      * Inserts all of the elements in the specified <tt>TIntCollection</tt> into this
      * list at the specified position. Shifts the element currently at that position
      * (if any) and any subsequent elements will appear in this list in the same order.
      * The behavior of this operation is undefined if the specified collection is
      * modified while the operation is in progress.
      * @param index index at which to insert the first element from the specified collection
      * @param c collection containing elements to be added to this list
      * @return true if this list changed as a result of the call
      */
     public boolean addAll (int index, TIntCollection c);
     
     /**
      * Inserts all of the elements in the specified array of ints into this list at the
      * specified position. Shifts the element currently at that position (if any) and 
      * any subsequent elements will appear in this list in the same order.
      * The behavior of this operation is undefined if the specified collection is
      * modified while the operation is in progress.
      * @param index index at which to insert the first element from the specified collection
      * @param array array of ints containing elements to be added to this list
      * @return true if this list changed as a result of the call
      */
     public boolean addAll (int index, int[] array);

     /**
      * Removes all of the elements from this list. The list will be empty after the
      * call returns.
      */
     public void clear();
     
     /**
      * Returns true if this list contains the specified element.
      */
     public boolean contains (int o);
     
     /**
      * Returns true if this list contains all of the elements of the specified collection.
      * @param c <tt>Collection</tt> to be checked for containment in this list
      * @return true if this list contains all of the elements of the specified collection
      */
     public boolean containsAll (Collection<?> c);
     
     /**
      * Returns true if this list contains all of the elements of the specified collection.
      * @param c <tt>TIntCollection</tt> to be checked for containment in this list
      * @return true if this list contains all of the elements of the specified collection
      */
     public boolean containsAll (TIntCollection c);
     
     /**
      * Returns true if this list contains all of the elements of the specified array.
      * @param array array of ints to be checked for containment in this list
      * @return true if this list contains all of the elements of the specified array
      */
     public boolean containsAll (int[] array);
     
     /**
      * Compares the specified object with this list for equality. Returns true if and
      * only if the specified object is a <tt>List</tt> or a <tt>TIntList</tt>,
      * both have the same size, and all corresponding pairs of elements in the two lists
      * are equal. In other words, two lists are defined to be equal if the contain the same
      * elements in the same order. This definition ensures that the equals method works
      * properly across different implementations of the <tt>List</tt> interface.
      * @param o the object to be compared for equality with this list
      * @return true if the specified object is equal to this list
      */
     public boolean equals (Object o);    

    /**
     * Returns the element at the specified position in this list.
     * @param index index of the element to return
     * @return the element at the specified position in this list
     */
    public int get (int index);
    
    /**
     * Returns the value that is used to represent null.
     * @return the value that represents null
     */
    public int getNoEntryValue();

    /**
     * Returns the hash code value for this list. The hash code of a list is defined
     * to be the result of the sum of the hash code of its elements.
     */
    public int hashCode();
    
    /**
     * Returns the index of the first occurrence of the specified element in this
     * list, or -1 if this list does not contain the element.
     * @param o element to search for
     * @return the index of the first occurrence of the specified element in this
     * list, or -1 if this list does not contain the element
     */
    public int indexOf (int o);
    
    /**
     * Returns true if this list contains no elements.
     * true if this list contains no elements
     */
    public boolean isEmpty();

    /**
     * Returns an iterator over the elements in this list in proper sequence.
     * @return an iterator over the elements in this list in proper sequence
     */
    public TIntIterator iterator();

    /**
     * Returns the index of the last occurrence of the specified element in this
     * list, or -1 if this list does not contain the element.
     * @param o element to search for
     * @return the index of the last occurrence of the specified element in this
     * list, or -1 if this list does not contain the element.
     */
    public int lastIndexOf (int o);
    
    /**
     * Returns a list iterator over the elements in this list (in proper sequence).
     * @return a list iterator over the elements in this list (in proper sequence)
     */
    public TIntListIterator listIterator();
    
    /**
     * Returns a list iterator over the elements in this list (in proper sequence),
     * starting at the specified position in the list. The specified index indicates
     * the first element that would be returned by an initial call to next. An initial
     * call to previous would return the element with the specified index minus one.
     * @param index index of the first element to be returned from the list iterator
     * (by a call to next).
     * @return a list iterator over the elements in this list (in proper sequence),
     * starting at the specified position in the list
     */
    public TIntListIterator listIterator (int index);

    /**
     * Removes the first occurrence of the specified element from this list, if
     * it is present. If this list does not contain the element, it is unchanged.
     * @param o element to be removed from this list, if present.
     * @return true if this list contained the specified element
     */
    public boolean remove (int o);

    /**
     * Removes the element at the specified position in this list. Shifts any
     * subsequent elements to the left (subtracts one from their indices). Returns
     * the element that was removed from the list.
     * @param index the index of the element to be removed
     * @return the element previously at the specified position
     */
    public int removeAt (int index);
    
    /**
     * Removes from this list all of its elements that are contained in the specified
     * collection. This call will order the elements of the specified collection.
     * @param c <tt>Collection</tt> containing elements to be removed from this list
     * @return true if this list changed as a result of the call
     */
    public boolean removeAll (Collection<?> c);
    
    /**
     * Removes from this list all of its elements that are contained in the specified
     * collection. This call will order the elements of the specified collection.
     * @param c <tt>TIntCollection</tt> containing elements to be removed from this list
     * @return true if this list changed as a result of the call
     */
    public boolean removeAll (TIntCollection c);
    
    /**
     * Removes from this list all of its elements that are contained in the specified
     * array. This call will order the elements of the specified array.
     * @param array array of ints containing elements to be removed from this list
     * @return true if this list changed as a result of the call
     */
    public boolean removeAll (int[] array);
    
    /**
     * Replaces each element of this list with the result of applying the operator to
     * that element. Errors or runtime exceptions thrown by the operator are delayed
     * to the caller.
     * @param operator the operator to apply to each element
     */
    public void replaceAll (UnaryOperator<Integer> operator);
    
    /**
     * Retains only the elements in this list that are contained in the specified
     * <tt>Collection</tt>. In other words, removes from this list all of its elements
     * that are not contained in the specified collection. This call will order the elements
     * of the specified collection.
     * @param c <tt>Collection</tt> containing elements to be retained in this list
     * @return true if this list changed as a result of the call
     */
    public boolean retainAll (Collection<?> c);
    
    /**
     * Retains only the elements in this list that are contained in the specified
     * <tt>TIntCollection</tt>. In other words, removes from this list all of its elements
     * that are not contained in the specified collection. This call will order the elements
     * of the specified collection.
     * @param c <tt>TIntCollection</tt> containing elements to be retained in this list
     * @return true if this list changed as a result of the call
     */
    public boolean retainAll (TIntCollection c);
    
    /**
     * Retains only the elements in this list that are contained in the specified
     * array of ints. In other words, removes from this list all of its elements
     * that are not contained in the specified collection. This call will order the elements
     * of the specified array.
     * @param array array of ints containing elements to be retained in this list
     * @return true if this list changed as a result of the call
     */
    public boolean retainAll (int[] array);
    
    /**
     * Replaces the element at the specified position in this list with the specified
     * element.
     * @param index index of the element to replace
     * @param element element to be stored at the specified position
     * @return the element previously at the specified position
     */
    public int set (int index, int element);

    /**
     * Returns the number of elements in this list.
     */
    public int size();
    
    /**
     * Sorts this list according to the order induced by the specified <tt>Comparator</tt>.
     * @param c the comparator used to compare list elements. A null value indicates that
     * the element's natural ordering should be used
     */
    public void sort(Comparator<? super Integer> c);

    /**
     * Returns a view of the portion of list between the specified fromIndex, indlusive,
     * and toIndex, exclusive (if fromIndex and toIndex are equal, the returned list is
     * empty). The returned list is backed by this list, so non-structural changes in
     * the returned list are reflected in this list, and vice-versa. The returned list
     * supports all of the optional list operations by this list.
     * @param fromIndex low endpoint (inclusive) of the subList
     * @param toIndex high endpoint (exclusive) of the subList
     * @return
     */
    public TIntList subList (int fromIndex, int toIndex);
    
   /**
    * Returns an array containing all of the elements in this list in proper sequence
    * (from first to last element). The returned array will be "safe" in that no references
    * to it are mantained by this list. (In other words, this method must allocate a new
    * array even if this list is backed by an array). The caller is thus free to modify
    * the returned array.
    * @return an array containing all of the elements in this list in proper sequence
    */
   public int[] toArray();

   /**
    * Returns an array containing all of the elements in this list in proper sequence (from
    * first to last element); the runtime type of the returned array is that of the
    * specified array. If the list fits in the specified array, it is returned therein.
    * Otherwise, a new array is allocated with the size of this list.
    * @param a the array into which the elements of this list are to be stored, if it is
    * big enough; otherwise, a new array is allocated for this purpose
    * @return an array containing the elements of this list
    */
   public int[] toArray (int[] a);


   

   //Operations that are not included in the Java implementation,
   //but can be interesting.


   /**
    * Fills every slot in the list with the specified value.
    *
    * @param val the value to use when filling
    */
   public void fill( int val );


   /**
    * Fills a range in the list with the specified value.
    *
    * @param fromIndex the offset at which to start filling (inclusive)
    * @param toIndex the offset at which to stop filling (exclusive)
    * @param val the value to use when filling
    */
   public void fill( int fromIndex, int toIndex, int val );
   
   /**
    * Finds the maximum value in the list.
    *
    * @return the largest value in the list.
    * @exception IllegalStateException if the list is empty
    */
   public int max();


   /**
    * Finds the minimum value in the list.
    *
    * @return the smallest value in the list.
    * @exception IllegalStateException if the list is empty
    */
   public int min();
   
   /**
     * Reverse the order of the elements in the list.
     */
    public void reverse();

    /**
     * Reverse the order of the elements in the range of the list.
     *
     * @param from the inclusive index at which to start reversing
     * @param to the exclusive index at which to stop reversing
     */
    public void reverse( int from, int to );


    /**
     * Shuffle the elements of the list using the specified random
     * number generator.
     *
     * @param rand a <code>Random</code> value
     */
    public void shuffle( Random rand );

    /**
     * Calculates the sum of all the values in the list.
     *
     * @return the sum of the values in the list (zero if the list is empty).
     */
    public int sum();
}