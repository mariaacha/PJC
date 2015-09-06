package PJC.list.arrayList;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.concurrent.atomic.*;
import java.io.*;

import PJC.Constants;
import PJC.collection.TByteCollection;
import PJC.iterator.TByteIterator;
import PJC.list.ArraysPar;
import PJC.list.TByteList;
import PJC.list.iterator.TByteListIterator;
import PJC.map.hashMap.HashFunctions;
import PJC.spliterator.TByteSpliterator;

public class TByteArrayList implements TByteList, RandomAccess, Serializable, Cloneable {
	static final long serialVersionUID = 1L;

    /** the data of the list */
    protected byte[] _data;

    /** the index after the last entry in the list */
    protected int _pos;

    /** the default capacity for new lists */
    protected static final int DEFAULT_CAPACITY = Constants.DEFAULT_CAPACITY;

    /** the byte value that represents null */
    protected byte no_entry_value;
    
    /** boolean variable shared by the threads*/
    protected boolean boolGlobal;
    
    /** int variable used for the threads to share their results */
    protected int intGlobal;
    
    /** byte variable used for the threads to share their results */
    protected byte byteGlobal;
    
    /** array of ints used for the threads to share their results */
    protected int[] intsGlobal;
    
    /** array of bytes used for the threads to share their results */
    protected byte[] bytesGlobal;
    
    /** stringBuilder used for the threads to share their results */
    protected StringBuilder sbGlobal;
    
    /** array with the threads */
    protected Thread[] threads;
    
    /** Object used to synchronize the threads */
    protected Object objectSync = new Object();
    
    protected AtomicInteger atIntGlobal;
    
    /** Calculates the first element that a thread should process
     * 
     * @param i index of the processor
     * @param numElem number of elements to each processor
     * @param offset first position
     * @return the first element assigned to a thread
     */
    public int limiteMin (int i, int numElem, int offset)	{
		return i*numElem+offset;
	}

    /** Calculates the last element that a thread should process
     * 
     * @param i index of the processor
     * @param numElem number of elements to each processor
     * @param offset first position
     * @param numProcs total number of processors
     * @param numMax length of the data
     * @return the last element assigned to a thread
     */
	public int limiteMax (int i, int numElem, int offset, int numProcs, int numMax)	{
		if ((i+1)==numProcs) return numMax;
		return (i+1)*numElem+offset;
	}


    /**
     * Constructs an empty <tt>TByteArrayList</tt>
     * with a default initial capacity.
     */
    public TByteArrayList() {
        this( DEFAULT_CAPACITY, ( byte ) 0 );
    }


    /**
     * Constructs an empty <tt>TByteArrayList</tt>
     * with the specified initial capacity.
     *
     * @param capacity the capacity of the new list
     */
    public TByteArrayList( int capacity ) {
        this( capacity, ( byte ) 0 );
    }


    /**
     * Constructs an empty <tt>TByteArrayList</tt> with
     * the specifieds initial capacity and no_entry_value.
     *
     * @param capacity the capacity of the new list
     * @param no_entry_value an <tt>byte</tt> value that represents null.
     */
    public TByteArrayList( int capacity, byte no_entry_value ) {
        _data = new byte[ capacity ];
        _pos = 0;
        this.no_entry_value = no_entry_value;
    }

    /**
     * Constructs a <tt>TByteArrayList</tt> containing the elements
     * of the specified collection, in the same order.
     *
     * @param collection the <tt>TByteCollection</tt> to copy
     */
    public TByteArrayList ( TByteCollection collection ) {
        this( collection.size() );
        addAll( collection ); 
    }
    
    /**
     * Constructs a <tt>TByteArrayList</tt> containing the elements
     * of the specified collection, in the same order.
     *
     * @param collection the <tt>Collection</tt> to copy
     */
    public TByteArrayList ( Collection<? extends Byte> collection ) {
        this( collection.size() );
        addAll( collection ); 
    }


    /**
     * Constructs a <tt>TByteArrayList</tt> containing the elements
     * of the specified array, in the same order.
     *
     * @param values the array of bytes to copy
     */
    public TByteArrayList( byte[] values ) {
        this( values.length );
        addAll( values );
    }
    
    
    /** {@inheritDoc} */
    public boolean add (byte e) {
        ensureCapacity(_pos+1 );
        _data[_pos++] = e;
        return true;
    }
    
    /** {@inheritDoc} */
    public void add( int index, byte element ) {
        if ( index == _pos ) {
            add( element );
            return;
        }
        ensureCapacity( _pos + 1 );
        System.arraycopy(_data, index, _data, index + 1, _pos-index);
        _data[index] = element;
        _pos++;
    }    
    private class Add1 implements Runnable	{
    	int min, max;
    	Add1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run()	{
    		System.arraycopy(_data, min, _data, min+1, max-min);
    	}
    }
    /**
     * Inserts the specified element at the specified position in this list,
     * using the giving number of threads.
     * @param index index at which the specified element is to be inserted
     * @param element element to be inserted
     * @param numProcs number of threads that will be created
     */
    public void addPar( int index, byte element, int numProcs) {
    	if ( index == _pos ) {
    		add( element );
    		return;
    	}
    	ensureCapacity(_pos+1);
    	int numElemProc = (_pos-index)/numProcs;
    	threads = new Thread[numProcs];
    	intsGlobal = new int[numProcs];
    	int numMin, numMax;
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, index);
    		numMax = limiteMax (i, numElemProc, index, numProcs, _pos);
        	threads[i] = new Thread (new Add1 (numMin, numMax));
        	intsGlobal[i] = -1;
        	threads[i].start();
        }
        for (int i=0; i<numProcs; i++)	{
        	try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        _data[index] = element;
    }

    /** {@inheritDoc} */
    public boolean addAll( Collection<? extends Byte> c ) {
        int oldPos = _pos;
        for (Byte element : c) {
            byte e = element.byteValue();
            add(e);
        }
        return (_pos > oldPos);
    }
    private class AddAll1 implements Runnable	{
    	int min, max, posInicial;
    	Byte[] array;
    	AddAll1 (int a, int b, Byte[] c, int d)	{
    		min = a; max = b;
    		array = c;
    		posInicial = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			_data[i+posInicial] = array[i].byteValue();
            }
    	}
    }
    /**
     * Appends all of the elements in the specified collection to the end
     * of the list, in the same order.
     * @param c collection containing elements to be added to this list
     * @param numProcs number of threads that will be used
     * @return true if this list changed as a result of the call
     */
    public boolean addAllPar (Collection<? extends Byte> c, int numProcs)	{
    	int oldPos = _pos;
    	Byte[] array = c.toArray (new Byte[c.size()]);
    	int numElemProc = array.length/numProcs;
    	int posInicial = _pos;
    	ensureCapacity (_pos+array.length);
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, array.length);
    		threads[i] = new Thread (new AddAll1 (numMin, numMax, array, posInicial));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos += array.length;
    	return _pos > oldPos;
    }

    /** {@inheritDoc} */
    public boolean addAll (TByteCollection c ) {
        byte[] array = c.toArray (new byte[c.size()]);
        return addAll(array);
    }
    /**
     * Appends all of the elements in the specified <tt>TByteCollection</tt>
     * to the end of the list, in the same order. The behavior of 
     * this operation is undefined if the specified <tt>TByteCollection</tt>
     * is modified while the operation is in progress (this implies that 
     * the behavior of this call is undefined if the specified collection
     * is this list, and this list is nonempty.
     * @param c <tt>TByteCollection</tt> containing elements to be added to this list
     * @param numProcs number of threads that will be used
     * @return true if this list changed as a result of the call
     */
    public boolean addAllPar (TByteCollection collection, int numProcs)	{
    	byte[] array = collection.toArray(new byte[collection.size()]);
    	return addAllPar (array, numProcs);
    }

    /** {@inheritDoc} */
    public boolean addAll (byte[] array) {
    	if (array.length == 0)
    		return false;
        ensureCapacity (_pos + array.length);
        System.arraycopy(array, 0, _data, _pos, array.length);
        _pos += array.length;
        return true;
    }
    private class AddAll2 implements Runnable	{
    	int min, max;
    	byte[] array;
    	AddAll2 (int a, int b, byte[] c)	{
    		min = a; max = b; array = c;
    	}
    	public void run ()	{
    		System.arraycopy(array, min, _data, min+_pos, max-min);
    	}
    }
    /**
     * Appends all of the elements in the specified array of bytes to the end
     * of the list, in the same order.
     * @param array array containing elements to be added to this list
     * @param numProcs number of threads that will be used
     * @return true if this list changed as a result of the call
     */
    public boolean addAllPar (byte[] array, int numProcs)	{
    	boolGlobal = false;
    	ensureCapacity (_pos+array.length);
    	int numElemProc = array.length/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, array.length);
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
    	_pos += array.length;
    	return boolGlobal;
    }
    
    public boolean addAll(int index, Collection<? extends Byte> c) {
    	if (index > _pos || index < 0)
            throw new IndexOutOfBoundsException("Out of bounds: " + index);

        Byte[] a = c.toArray(new Byte[c.size()]);
        int numNew = a.length;
        ensureCapacity(_pos + numNew);
        int numMoved = _pos - index;
        if (numMoved > 0)
            System.arraycopy(_data, index, _data, index + numNew, numMoved);
        for (int i=0; i<numNew; i++)	{
        	_data[index+i] = (a[i]).byteValue();
        }
        _pos += numNew;
        return numNew != 0;
    }
    
    /**
     * Inserts all the elements in the specified <tt>Collection</tt>
     * to the list, starting at the specified position. Shifts the element
     * currently at that position (if any) and any subsequent to the right
     * (increases their indices). The new elements will appear in the list
     * in the same order they are at the specified collection.
     * @param index index at which to insert the first element
     * @param c <tt>Collection</tt> containing elements to be added
     * @param numProcs number of threads that will be created
     * @return true if this list changed as a result of the call
     */
    public boolean addAllPar(int index, Collection<? extends Byte> c, int numProcs) {
    	if (index > _pos || index < 0)
            throw new IndexOutOfBoundsException("Out of bounds: " + index);
    	Byte[] array = c.toArray (new Byte[c.size()]);
        int numNew = array.length;
        ensureCapacity(_pos + numNew);

        int numMoved = _pos - index;
        if (numMoved > 0)
        	ArraysPar.moveOfRangePar(_data, _data, index, _pos, index+numNew, numProcs);
        
        boolGlobal = false;
    	int numElemProc = numMoved/numProcs;
    	int posInicial = index;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, index);
    		numMax = limiteMax (i, numElemProc, index, numProcs, numMoved);
    		threads[i] = new Thread (new AddAll1 (numMin, numMax, array, posInicial));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos += numNew;
    	return boolGlobal;
    }

    /** {@inheritDoc} */
    public boolean addAll(int index, TByteCollection c) {
    	if (index > _pos || index < 0)
            throw new IndexOutOfBoundsException("Out of bounds: " + index);
        byte[] a = c.toArray();
        int numNew = a.length;
        ensureCapacity(_pos + numNew);  // Increments modCount

        int numMoved = _pos - index;
        if (numMoved > 0)
            System.arraycopy(_data, index, _data, index + numNew,
                             numMoved);

        System.arraycopy(a, 0, _data, index, numNew);
        _pos += numNew;
        return numNew != 0;
    }
    
    /**
     * Inserts all the elements in the specified <tt>TByteCollection</tt>
     * to the list, starting at the specified position. Shifts the element
     * currently at that position (if any) and any subsequent to the right
     * (increases their indices). The new elements will appear in the list
     * in the same order they are at the specified collection.
     * @param index index at which to insert the first element
     * @param c <tt>TByteCollection</tt> containing elements to be added
     * @param numProcs number of threads that will be created
     * @return true if this list changed as a result of the call
     */
    public boolean addAllPar(int index, TByteCollection c, int numProcs) {
    	if (index > _pos || index < 0)
            throw new IndexOutOfBoundsException("Out of bounds: " + index);

        byte[] array = c.toArray();
        int numNew = array.length;
        ensureCapacity(_pos + numNew);

        int numMoved = _pos - index;
        if (numMoved > 0)
            //System.arraycopy(elementData, index, elementData, index + numNew, numMoved);
        	ArraysPar.moveOfRangePar(_data, _data, index, _pos, index+numNew, numProcs);
      //System.arraycopy (original, from, dest, offset, to-from);
        //to-from=numMoved=_pos-index-> to = from+_pos-index -> (from=index)-> to=_pos

        //System.arraycopy(a, 0, elementData, index, numNew);
        //a=original
        //0=from
        //elementData=dest
        //index=offset
        //numNew=to-from -> to=numNew+from=numNew
        ArraysPar.moveOfRangePar(array, _data, 0, numNew, index, numProcs);
        _pos += numNew;
        return numNew != 0;
    }
    
    public boolean addAll(int index, byte[] a) {
    	if (index > _pos || index < 0)
            throw new IndexOutOfBoundsException("Out of bounds: " + index);
    	
        int numNew = a.length;
        ensureCapacity(_pos + numNew);  // Increments modCount

        int numMoved = _pos - index;
        if (numMoved > 0)
            System.arraycopy(_data, index, _data, index + numNew,
                             numMoved);

        System.arraycopy(a, 0, _data, index, numNew);
        _pos += numNew;
        return numNew != 0;
    }
    
    /**
     * 
     * @param index first position to add
     * @param array array whose elements will be added
     * @param numProcs number of processors that will be used
     * @return true if all the elements were added successfully
     */
    public boolean addAllPar(int index, byte[] array, int numProcs) {
    	if (index > _pos || index < 0)
            throw new IndexOutOfBoundsException("Out of bounds: " + index);

    	int numNew = array.length;
        ensureCapacity(_pos + numNew);

        int numMoved = _pos - index;
        if (numMoved > 0)
        	ArraysPar.moveOfRangePar(_data, _data, index, _pos, index+numNew, numProcs);
        ArraysPar.moveOfRangePar(array, _data, 0, numNew, index, numProcs);
        _pos += numNew;
        return numNew != 0;
    }

    /** {@inheritDoc} */
    public void clear() {
        _data = new byte[0];
        _pos = 0;
    }
    
    /**
     * Returns a shallow copy of this <tt>TByteArrayList</tt> instance.
     * (The elements themselves are not copied.)
     *
     * @return a clone of this <tt>TByteArrayList</tt> instance
     */
    public TByteArrayList clone() {
        try {
            TByteArrayList v = (TByteArrayList) super.clone();
            v._data = Arrays.copyOf(_data, _pos);
            //v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
    /**
     * Returns a shallow copy of this <tt>TByteArrayList</tt> instance.
     * (The elements themselves are not copied.)
     *
     * @return a clone of this <tt>TByteArrayList</tt> instance
     */
    public TByteArrayList clonePar(int numProcs) {
        try {
            TByteArrayList v = (TByteArrayList) super.clone();
            v._data = ArraysPar.copyOfPar(_data, _pos, numProcs);
            //v.modCount = 0;
            return v;
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
    
    /** {@inheritDoc} */
    public boolean contains( byte o ) {
        return lastIndexOf( o ) >= 0;
    }
    /**
     * Returns true if this <tt>TByteArrayList</tt> contains the specified
     * element. More formally, returns true if and only if this list
     * contains at least one element byte such that.
     * @param o element whose presence in this list is to be tested
     * @param numProcs number of threads that will be created
     */
    public boolean containsPar ( byte o, int numProcs) {
        return indexOfPar( o, numProcs) >= 0;
    }
    

    /** {@inheritDoc} */
    public boolean containsAll( Collection<?> c ) {
    	byte[] searching = this.toArray();
    	Arrays.sort(searching);
    	for (Object element : c)	{
    		if (element instanceof Byte)	{
    			byte value = ((Byte) element).byteValue();
    			if (Arrays.binarySearch(searching, value) < 0)
    				return false;
    		}
    		else return false;
    	}
        return true;
    }    
    private class ContainsAll1 implements Runnable	{
    	int min, max;
    	Byte[] array;
    	byte[] searching;
    	ContainsAll1 (int a, int b, Byte[] c, byte[] d)	{
    		min = a; max = b; array = c; searching = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			if (!boolGlobal) return;
    			if (Arrays.binarySearch(searching, array[i].byteValue()) < 0)	{
    				boolGlobal = false;
    				return;
    			}
            }
    	}
    }
    /**
     * Returns true if this collection contains all of the elements in the
     * specified <tt>Collection</tt>
     * @param c <tt>Collection</tt> to be checked for containment in this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection contains all of the elements in the
     * specified <tt>Collection</tt>
     */
    public boolean containsAllPar (Collection<?> collection, int numProcs)	{
    	boolGlobal = true;
    	Byte[] array = collection.toArray(new Byte[collection.size()]);
    	int numElemProc = collection.size()/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	byte[] searching = this.toArrayPar(numProcs);
    	Arrays.sort(searching);
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, collection.size());
    		threads[i] = new Thread (new ContainsAll1 (numMin, numMax, array, searching));
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
    public boolean containsAll( TByteCollection collection ) {
        if ( this == collection ) {
            return true;
        }
        byte[] searching = this.toArray();
        Arrays.sort(searching);
        TByteIterator iter = collection.iterator();
        while ( iter.hasNext() ) {
            byte element = iter.next();
            if (Arrays.binarySearch(searching, element) < 0)
            	return false;
        }
        return true;
    }
    private class ContainsAll2 implements Runnable	{
    	int min, max;
    	byte[] array, searching;
    	ContainsAll2 (int a, int b, byte[] c, byte[] d)	{
    		min = a; max = b; array = c; searching = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			if (!boolGlobal) return;
    			if (Arrays.binarySearch(searching, array[i]) < 0)	{
    				boolGlobal = false;
    				return;
    			}
            }
    	}
    }
    /**
     * Returns true if this collection contains all of the elements in the
     * specified <tt>TByteCollection</tt>
     * @param c <tt>TByteCollection</tt> to be checked for containment in this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection contains all of the elements in the
     * specified <tt>TByteCollection</tt>
     */
    public boolean containsAllPar (TByteCollection collection, int numProcs)	{
    	if (this == collection)	return true;
    	boolGlobal = true;
    	byte[] array = collection.toArray(new byte[collection.size()]);
    	int numElemProc = collection.size()/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	byte[] searching = this.toArrayPar(numProcs);
    	Arrays.sort(searching);
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, collection.size());
    		threads[i] = new Thread (new ContainsAll2 (numMin, numMax, array, searching));
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
    public boolean containsAll( byte[] array ) {
    	byte[] searching = this.toArray();
    	Arrays.sort(searching);
        for (int i=0; i<array.length; i++) {
            if (Arrays.binarySearch(searching, array[i]) < 0)
                return false;
        }
        return true;
    }
    /**
     * Returns true if this collection contains all of the elements in the
     * specified array of bytes
     * @param array array of bytes to be checked for containment in this collection
     * @param numProcs number of threads that will be used
     * @return true if this collection contains all of the elements in the
     * specified array of bytes
     */
    public boolean containsAllPar (byte[] array, int numProcs)	{
    	int numElemProc = array.length/numProcs;
    	boolGlobal = true;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	byte[] searching = this.toArrayPar (numProcs);
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, array.length);
    		threads[i] = new Thread (new ContainsAll2 (numMin, numMax, array, searching));
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
    @Override
    public boolean equals( Object o ) {
        if ( o == this ) {
            return true;
        }
        else if ( o instanceof TByteArrayList ) {
            TByteArrayList that = ( TByteArrayList )o;
            if ( that.size() != this.size() ) return false;
            else {
                for ( int i = _pos; i-- > 0; ) {
                    if ( this._data[ i ] != that._data[ i ] ) {
                        return false;
                    }
                }
                return true;
            }
        }
        else if (o instanceof List<?>)	{
			List<?> that = (List<?>) o;
			if (that.size() != _pos)	return false;
			else	{
				for (int i=0; i<_pos; i++)	{
					if (that.get(i) instanceof Byte)	{
	    				if (_data[i] != ((Byte) that.get(i)).byteValue())	{
	    					return false;
	    				}
	    			}
					else	return false;
				}
			}
        	return true;
        }
        else return false;
    }
    private class Equals1 implements Runnable	{
    	int min, max;
    	TByteArrayList that;
    	Equals1 (int a, int b, TByteArrayList c)	{
    		min = a; max = b; that = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			if (!boolGlobal)	return;
                if (_data[i] != that._data[i]) {
                    boolGlobal = false;
                    return;
                }
            }
    	}
    }
    private class Equals2 implements Runnable	{
    	int min, max;
    	List<?> that;
    	Equals2 (int a, int b, List<?> c)	{
    		min = a; max = b; that = c;
    	}
    	public void run ()	{
    		byte byteLocal;
    		for (int i=min; i<max; i++) {
    			if (!boolGlobal)	{
    				return;
    			}
    			if (that.get(i) instanceof Byte)	{
    				byteLocal = ((Byte) that.get(i)).byteValue();
    				if (_data[i] != byteLocal)	{
    					boolGlobal = false;
    					return;
    				}
    			}
    			else	{
    				boolGlobal = false;
    				return;
    			}
            }
    	}
    }
    /**
     * Compares the specified object with this list for equality. Returns true
     * if and only if the specified object is an <tt>ArrayList<Byte></tt> or a
     * <tt>TByteArrayList</tt> instance, both lists have the same size, and all
     * corresponding pairs of elements in the two lists are equal. In other words,
     * two lists are defined to be equal if they contain the same elements in the
     * same order.
     * @param o the object to be compared for equality with this list
     * @return true if the specified object is equal to this list
     */
    public boolean equalsPar (Object o, int numProcs)	{
    	if (o == this)	return true;
    	else if (o instanceof List)	{
    		TByteArrayList that = (TByteArrayList) o;
    		if (that.size() != this.size())	{
    			return false;
    		}
    		else	{
    			boolGlobal = true;
    			int numElemProc = _pos/numProcs;
    			threads = new Thread[numProcs];
    			int numMin, numMax;
    			for (int i=0; i<numProcs; i++)	{
    				numMin = limiteMin (i, numElemProc, 0);
    				numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
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
    		}
    	}
    	else if (o instanceof List<?>)	{
			List<?> that = (List<?>) o;
    		if (that.size() == 0)
    			return (_pos == 0);
    		if (that.size() != this.size())	{
    			return false;
    		}
    		else	{
    			boolGlobal = true;
    			int numElemProc = _pos/numProcs;
    			threads = new Thread[numProcs];
    			int numMin, numMax;
    			for (int i=0; i<numProcs; i++)	{
    				numMin = limiteMin (i, numElemProc, 0);
    				numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    				threads[i] = new Thread (new Equals2 (numMin, numMax, that));
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
    	}
    	else return false;
    }
    
    /** {@inheritDoc} */
    public void ensureCapacity( int capacity ) {
        if ( capacity > _data.length ) {
            int newCap = Math.max( _data.length << 1, capacity );
            byte[] tmp = new byte[ newCap ];
            System.arraycopy( _data, 0, tmp, 0, _data.length );
            _data = tmp;
        }
    }
    private class EnsureCapacity1 implements Runnable	{
    	int min, max;
    	byte[] tmp; 
    	EnsureCapacity1 (int a, int b, byte[] c)	{
    		min = a; max = b; tmp = c;
    	}
    	public void run()	{
    		System.arraycopy(_data, min, tmp, min, max-min);
    	}
    }
    /**
     * Increases the capacity of the <tt>TByteArrayList</tt> instance,
     * if necessary, to ensure that it can hold at least the number
     * of elements specified by the minimum capacity argument.
     * @param minCapacity the minimum capacity
     * @param numProcs number of threads that will be created
     */
    public void ensureCapacityPar( int minCapacity, int numProcs) {
        if ( minCapacity > _data.length ) {
            int newCap = Math.max( _data.length << 1, minCapacity );
            byte [] tmp = new byte[ newCap ];
            Thread[] threads = new Thread[numProcs];
            int numElemProc = _pos/numProcs;
            int numMin, numMax;
            for (int i=0; i<numProcs; i++)	{
            	numMin = limiteMin (i, numElemProc, 0);
        		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
        		threads[i] = new Thread(new EnsureCapacity1 (numMin, numMax, tmp));
        		threads[i].start();
            }
            for (int i=0; i<numProcs; i++)	{
            	try {
					threads[i].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }
            _data = tmp;
        }
    }

    /** {@inheritDoc} */
    public void forEach (Consumer<? super Byte> action)	{
    	for (int i=0; i<_pos; i++)	{
    		action.accept(_data[i]);
    	}
    }
    private class ForEach1 implements Runnable	{
    	int min, max;
    	Consumer<? super Byte> action;
    	ForEach1 (int a, int b, Consumer<? super Byte> c)	{
    		min = a; max = b; action = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			action.accept(_data[i]);
            }
    	}
    }
    /**
     * Performs the given action for each element of the <tt>Iterable</tt>
     * until all elements have been processed or the action throws an exception.
     * The order of the actions are executed is no guaranteed.
     * @param action the action to be performed for each element
     * @param numProcs number of threads that will be created
     */
    public void forEachPar (Consumer<? super Byte> action, int numProcs)	{
    	boolGlobal = true;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new ForEach1 (numMin, numMax, action));
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
    public byte get (int index) {
        if (index>=_pos) {
            throw new ArrayIndexOutOfBoundsException (index);
        }
        return _data[index];
    }
    
    /** {@inheritDoc} */
    public byte getNoEntryValue() {
        return no_entry_value;
    }

    @Override
    /** {@inheritDoc} */
    public int hashCode() {
        int h = 0;
        for (int i=0; i<_pos; i++) {
            h += HashFunctions.hash(_data[i]);
        }
        return h;
    }
    private class HashCode1 implements Runnable	{
    	int min, max;
    	HashCode1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		int hash = 0;
    		for (int i=min; i<max; i++)	{
    			hash += HashFunctions.hash(_data[i]);
    		}
    		atIntGlobal.addAndGet(hash);
    	}
    }
    /**
     * Returns the hash code value for this list
     * @param numProcs number of threads that will be used
     * @return the hash code value for this list
     */
    public int hashCodePar(int numProcs)	{
    	atIntGlobal = new AtomicInteger(0);
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
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
    
    /** {@inheritDoc} */
    public int indexOf (byte o) {
        for (int i=0; i<_pos; i++) {
            if (_data[i] == o)
                return i;
        }
        return -1;
    }
    private class IndexOf1 implements Runnable	{
    	int min, max;
    	byte value;
    	IndexOf1 (int a, int b, byte c)	{
    		min = a; max = b; value = c;
    	}
    	public void run ()	{
    		for ( int i = min; i < max; i++ ) {
    			if ((atIntGlobal.get() != -1)||(atIntGlobal.get() < i))	return;
                if ( _data[ i ] == value ) {
                    atIntGlobal.set(i);;
                    return;
                }
            }
    	}
    }
    /**
     * Returns the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contains the
     * element.
     * @param o element to search for 
     * @param numProcs number of threads that will be used
     * @return the index of the first occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     */
    public int indexOfPar (byte o, int numProcs)	{
    	atIntGlobal = new AtomicInteger(-1);
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new IndexOf1 (numMin, numMax, o));
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
    
    /** {@inheritDoc} */
    public boolean isEmpty() {
        return _pos == 0;
    }
    
    /** {@inheritDoc} */
    public TByteIterator iterator() {
        return new TByteArrayIterator ();
    }
    /** TByteArrayList iterator */
    class TByteArrayIterator implements TByteIterator {
    	int cursor;       // index of next element to return
        int lastRet = -1; // index of last element returned; -1 if no such

        /** {@inheritDoc} */
        public boolean hasNext() {
            return cursor != size();
	    }

        /** {@inheritDoc} */
        public byte next() {
        	int i = cursor;
            if (i >= size())
                throw new NoSuchElementException();
            byte[] elementData = TByteArrayList.this._data;
            if (i >= elementData.length)
                throw new ConcurrentModificationException();
            cursor = i + 1;
            return elementData[lastRet = i];
        }


        /** {@inheritDoc} */
        public void remove() {
            if (lastRet < 0)
                throw new IllegalStateException();
            try {
                TByteArrayList.this.removeAt(lastRet);
                cursor = lastRet;
                lastRet = -1;
            }
            catch (IndexOutOfBoundsException ex) {
                throw new ConcurrentModificationException();
            }
        }
        
        /** {@inheritDoc} */
        public void forEachRemaining (Consumer<? super Byte> consumer) {
            Objects.requireNonNull(consumer);
            final int size = TByteArrayList.this._pos;
            int i = cursor;
            if (i >= size) {
                return;
            }
            final byte[] elementData = TByteArrayList.this._data;
            while (i != size) {
                consumer.accept(elementData[i++]);
            }
            // update once at end of iteration to reduce heap write traffic
            cursor = i;
            lastRet = i - 1;
        }
        
        private class ForEachRemaining1 implements Runnable	{
        	int min, max;
        	Consumer<? super Byte> consumer;
        	ForEachRemaining1 (int a, int b, Consumer<? super Byte> c)	{
        		min = a; max = b; consumer = c;
        	}
        	public void run()	{
        		for (int i=min; i<max; i++)
        			consumer.accept(_data[i]);
        	}
        }
        /**
         * Performs the given action for each remaining element until all elements
         * have been processed or the action throws an exception. The actions can be
         * processed in any order of iteration. Exceptions thrown by the action are
         * relayed to the caller.
         * @param consumer the action to be performed for each element
         * @param numProcs number of threads that will be used
         */
        public void forEachRemainingPar (Consumer<? super Byte> consumer, int numProcs)	{
        	Objects.requireNonNull(consumer);
        	final int size = TByteArrayList.this._pos;
        	if (cursor >= size)
        		return;
        	int numElemProc = (_pos-cursor)/numProcs;
        	int numMin, numMax;
        	threads = new Thread[numProcs];
        	for (int i=0; i<numProcs; i++)	{
        		numMin = limiteMin (i, numElemProc, cursor);
        		numMax = limiteMax (i, numElemProc, cursor, numProcs, _pos);
        		threads[i] = new Thread(new ForEachRemaining1 (numMin, numMax, consumer));
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
    public int lastIndexOf( byte o ) {
        for (int i=_pos-1; i>=0; i--) {
            if (_data[i] == o)
                return i;
        }
        return -1;
    }
    private class LastIndexOf1 implements Runnable	{
    	int min, max;
    	byte value;
    	LastIndexOf1 (int a, int b, byte c)	{
    		min = a; max = b; value = c;
    	}
    	public void run ()	{
    		for (int i=max-1; i>=min; i--) {
    			if ((atIntGlobal.get() != -1)&&(atIntGlobal.get() > i))	return;
                if (_data[i] == value) {
                    atIntGlobal.set(i);
                    return;
                }
            }
    	}
    }
    /**
     * Returns the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element.
     * @param o element to search for
     * @param numProcs number of threads that will be used
     * @return the index of the last occurrence of the specified element
     * in this list, or -1 if this list does not contain the element
     */
    public int lastIndexOfPar (byte o, int numProcs) {
    	atIntGlobal = new AtomicInteger(-1);
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new LastIndexOf1 (numMin, numMax, o));
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
    
    public TByteListIterator listIterator()	{
    	return new TByteArrayListIterator(0);
    }

    /** {@inheritDoc} */
    public TByteListIterator listIterator (int index)	{
    	return new TByteArrayListIterator (index);
    }
    
    private class TByteArrayListIterator extends TByteArrayIterator implements TByteListIterator	{

    	TByteArrayListIterator (int index)	{
    		super();
    		cursor = index;
    	}

        /** {@inheritDoc} */
		public boolean hasPrevious()	{
			return cursor != 0;
		}

	    /** {@inheritDoc} */
		public int nextIndex()	{
			return cursor;
		}

	    /** {@inheritDoc} */
		public int previousIndex()	{
			return cursor-1;
		}

	    /** {@inheritDoc} */
		public byte previous()	{
			int i = cursor-1;
			if (i<0)
				throw new NoSuchElementException();
			if (i>= _data.length)
				throw new ConcurrentModificationException();
			cursor = i;
			return _data[lastRet=i];
		}

	    /** {@inheritDoc} */
		public void set (byte e)	{
			if (lastRet < 0)
				throw new IllegalStateException();
			try	{
				TByteArrayList.this.set(lastRet, e);
			}
			catch (IndexOutOfBoundsException ex)	{
				throw new ConcurrentModificationException();
			}
		}

	    /** {@inheritDoc} */
		public void add (byte e)	{
			try	{
				int i = cursor;
				TByteArrayList.this.add(i,e);
				cursor = i+1;
				lastRet=-1;
			}
			catch (IndexOutOfBoundsException ex)	{
				throw new ConcurrentModificationException();
			}
		}
    }

    /** {@inheritDoc} */
    public boolean remove( byte o ) {
        for (int index=0; index<_pos; index++) {
            if (o == _data[index]) {
            	removeAt (index);
                return true;
            }
        }
        return false;
    }
    private class Remove1 implements Runnable	{
    	int min, max;
    	byte value;
    	Remove1 (int a, int b, byte c)	{
    		min = a; max = b; value = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			if (boolGlobal)	return;
                if ( value == _data[i]  ) {
                	if ((i>=0)&&(i<(_pos-1)))
                		System.arraycopy(_data, i+1, _data, i, _pos-i-1);
                	_pos--;
                    boolGlobal = true;
                    return;
                }
            }
    	}
    }
    /**
     * Removes the first occurrence of the specified element from this list, if
     * it is present. If the list does not contain the element, it is unchanged.
     * Returns true if this list contained the specified element.
     * @param o element to be removed from this list, if present
     * @param numProcs number of threads that will be used
     * @return true if this list contained the specified element
     */
    public boolean removePar (byte value, int numProcs)	{
    	int numElemProc = _pos/numProcs;
    	boolGlobal = false;
    	threads = new Thread [numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new Remove1 (numMin, numMax, value));
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
    public byte removeAt (int index) {
        byte old = get(index);
        if (index<0 || index>=_pos) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (index != _pos-1)
        	System.arraycopy(_data, index+1, _data, index, _pos-index+1);
        return old;
    }
    
    /** {@inheritDoc} */
    public boolean removeAll( Collection<?> c ) {
    	int oldPos = _pos;
    	Object array[] = c.toArray();
    	byte[] newData =  new byte[_pos];
    	Arrays.sort(array);
    	int j=0;
    	for (int i=0; i<_data.length; i++)	{
    		if (array[i] instanceof Byte)	{
    			if (Arrays.binarySearch(array, ((Byte)_data[i]).byteValue()) < 0)	{
    				newData[j++] = _data[i];
    	        }
    		}
    	}
    	_data = newData;
    	_pos = j;
    	return oldPos > _pos;
    }
    private class RemoveAll1 implements Runnable	{
    	int min, max, idProc;
    	Object[] array;
    	RemoveAll1 (int a, int b, Object[] c, int d)	{
    		min = a; max = b; array = c; idProc = d;
    	}
    	public void run ()	{
    		int j=0;
    		byte[] bytesLocal = new byte[max-min];
    		for (int i=min; i<max; i++) {
    			if (Arrays.binarySearch(array, (Byte)_data[i]) < 0)	{
    				bytesLocal[i-j-min] = _data[i];
    	        }
    	        else	{
    	        	j++;
    	        }
    	    }
    		int numRemaining = max-min-j;
    		atIntGlobal.addAndGet(numRemaining);
    		if (idProc > 0)	{
    			while (intsGlobal[idProc-1] == -1)	{
    				System.out.print("");
    			}
        		intsGlobal[idProc] = numRemaining;
    			j = 0;
    			for (int k=0; k<idProc; k++)	j += intsGlobal[k];
    			System.arraycopy(bytesLocal, 0, _data, j, numRemaining);
    		}
    		else 	{
        		intsGlobal[idProc] = numRemaining;
    			System.arraycopy(bytesLocal, 0, _data, 0, numRemaining);
    		}
    	}
    }
    /**
     * Removes from this list all of its elements that are contained in the
     * specified <tt>Collection</tt>.
     * @param c <tt>Collection</tt> containing elements to be removed from this list.
     * Its elements will be ordered
     * @param numProcs number of threads that will be used
     * @return true if this list changed as a result of the call
     */
    public boolean removeAllPar (Collection<?> c, int numProcs)	{
    	atIntGlobal = new AtomicInteger(0);
    	intsGlobal = new int[numProcs];
    	int oldPos = _pos;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	Object array[] = c.toArray();
    	Arrays.sort(array);
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new RemoveAll1 (numMin, numMax, array, i));
    		intsGlobal[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos = atIntGlobal.get();
    	return oldPos > _pos;
    }
    /** {@inheritDoc} */
    public boolean removeAll(TByteCollection c ) {
    	int oldPos = _pos;
    	byte array[] = c.toArray();
    	byte[] newData =  new byte[_pos];
    	Arrays.sort(array);
    	int j=0;
    	for (int i=0; i<_data.length; i++)	{
    		if (Arrays.binarySearch(array, _data[i]) < 0)	{
    			newData[j++] = _data[i];
    		}
    	}
    	_data = newData;
    	_pos = j;
    	return oldPos > _pos;
    }
    private class RemoveAll2 implements Runnable	{
    	int min, max, idProc;
    	byte[] array;
    	RemoveAll2 (int a, int b, byte[] c, int d)	{
    		min = a; max = b; array = c; idProc = d;
    	}
    	public void run ()	{
    		int j=0;
    		byte[] bytesLocal = new byte[max-min];
    		for (int i=min; i<max; i++) {
    	        if (Arrays.binarySearch(array, _data[i]) < 0)	{
    				bytesLocal[i-j-min] = _data[i];
    	        }
    	        else	{
    	        	j++;
    	        }
    	    }
    		int numRemaining = max-min-j;
    		atIntGlobal.addAndGet(numRemaining);
    		if (idProc > 0)	{
    			while (intsGlobal[idProc-1] == -1)	{
    				System.out.print("");
    				}
    			intsGlobal[idProc] = numRemaining;
    			j = 0;
    			for (int k=0; k<idProc; k++)	j += intsGlobal[k];
    			System.arraycopy(bytesLocal, 0, _data, j, numRemaining);
    		}
    		else 	{
    			intsGlobal[idProc] = numRemaining;
        		System.arraycopy(bytesLocal, 0, _data, 0, numRemaining);
    		}
    	}
    }
    /**
     * Removes from this list all of its elements that are contained in the
     * specified <tt>TByteCollection</tt>.
     * @param c <tt>TByteCollection</tt> containing elements to be removed from this list.
     * Its elements will be ordered
     * @param numProcs number of threads that will be used
     * @return true if this list changed as a result of the call
     */
    public boolean removeAllPar (TByteCollection collection, int numProcs)	{
    	atIntGlobal = new AtomicInteger(0);
    	intsGlobal = new int[numProcs];
    	int oldPos = _pos;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	byte array[] = collection.toArrayPar(numProcs);
    	Arrays.sort(array);
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new RemoveAll2 (numMin, numMax, array, i));
    		intsGlobal[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos = atIntGlobal.get();
    	return oldPos > _pos;
    }
    /** {@inheritDoc} */
    public boolean removeAll(byte[] array) {
    	int oldPos = _pos;
    	byte[] newData =  new byte[_pos];
    	Arrays.sort(array);
    	int j=0;
    	for (int i=0; i<_data.length; i++)	{
    		if (Arrays.binarySearch(array, _data[i]) < 0)	{
    			newData[j++] = _data[i];
    		}
    	}
    	_data = newData;
    	_pos = j;
    	return oldPos > _pos;
    }
    /**
     * Removes from this list all of its elements that are contained in the
     * specified array of byte.
     * @param c array of byte containing elements to be removed from this list.
     * Its elements will be ordered
     * @param numProcs number of threads that will be used
     * @return true if this list changed as a result of the call
     */
    public boolean removeAllPar (byte[] array, int numProcs) {
    	atIntGlobal = new AtomicInteger(0);
    	intsGlobal = new int[numProcs];
    	int oldPos = _pos;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	Arrays.sort(array);
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new RemoveAll2 (numMin, numMax, array, i));
    		intsGlobal[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos = atIntGlobal.get();
    	return oldPos > _pos;
    }
    
    /**
     * Removes all of the elements of this collection that satisfy the given
     * predicate. Errors or runtime exceptions thrown during iteration or by
     * the predicate are relayed to the caller.
     * @param filter a predicate which returns true for elements to be removed
     * @return true if any elements were removed
     */
    public boolean removeIf (Predicate<? super Byte> filter) {
        Objects.requireNonNull(filter);
        int j=0;
        for (int i=0; i<_pos; i++)	{
        	if (filter.test(_data[i]))	{
        		j++;
        	}
        	else if (j>0)	_data[i] = _data[i+j];
        }
        return (j>0);
    }
    
    private class RemoveIf1 implements Runnable	{
    	int min, max, idProc;
    	Predicate<? super Byte> filter;
    	RemoveIf1 (int a, int b, Predicate<? super Byte> c, int d)	{
    		min = a; max = b; filter = c; idProc = d;;
    	}
    	public void run ()	{
    		int j = 0;
    		byte[] bytesLocal = new byte[max-min];
    		for (int i=min; i<max; i++)	{
    			if (!filter.test(_data[i]))	{
    				bytesLocal[i-j-min] = _data[i];
    			}
    			else j++;
    		}
    		int numRemaining = max-min-j;
    		atIntGlobal.addAndGet(numRemaining);
    		if (idProc > 0)	{
    			while (intsGlobal[idProc-1] == -1)	{System.out.print("");}
        		intsGlobal[idProc] = numRemaining;
    			j = 0;
    			for (int k=0; k<idProc; k++)	j += intsGlobal[k];
    			System.arraycopy(bytesLocal, 0, _data, j, numRemaining);
    		}
    		else	{
    			intsGlobal[idProc] = numRemaining;
    			System.arraycopy(bytesLocal, 0, _data, 0, numRemaining);
    		}
    	}
    }
    /**
     * Removes all the elements of this collection that satisfy the given predicate.
     * Errors or runtime exceptions thrown during iteration or by the predicate
     * are relayed to the caller.
     * @param filter a predicate which returns true for elements to be removed
     * @param numProcs number of threads that will be used
     * @return true if any elements were removed
     */
    public boolean removeIfPar (Predicate<? super Byte> filter, int numProcs)	{
    	atIntGlobal = new AtomicInteger(0);
    	intsGlobal = new int[numProcs];
    	int oldPos = _pos;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new RemoveIf1 (numMin, numMax, filter, i));
    		intsGlobal[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos = atIntGlobal.get();
    	return (oldPos > _pos);
    }
    
    /** {@inheritDoc} */
    protected void removeRange(int fromIndex, int toIndex) {
        //modCount++;
        int numMoved = _pos - toIndex;
        System.arraycopy(_data, toIndex, _data, fromIndex,
                         numMoved);

        // clear to let GC do its work
        int newSize = _pos - (toIndex-fromIndex);
        /**for (int i = newSize; i < _pos; i++) {
            _data[i] = null;
        }*/
        _pos = newSize;
    }
    /**
     * Removes from this list all of the elements whose index is between fromIndex,
     * inclusive, and toIndex, exclusive. Shifts any succeeding elements to the
     * left (reduces their index). This call shortens the list by (toIndex-fromIndex).
     * @param fromIndex index of the first element to be removed
     * @param toIndex index after last element to be removed
     * @param numProcs number of threads that will be used
     */
    protected void removeRangePar (int fromIndex, int toIndex, int numProcs)	{
    	//int numMoved = _pos - toIndex;
    	//System.arraycopy(_data, toIndex, _data, fromIndex, numMoved);
    	ArraysPar.moveOfRangePar(_data, _data, toIndex, _pos, fromIndex, numProcs);
    	//from = toIndex
    	//offset = fromIndex
    	//to-from = numMoved = _pos - toIndex -> to = _pos + from - toIndex -> 
    	//(from = toIndex) -> to = _pos
    	//System.arraycopy (original, from, dest, offset, to-from);
    	
    }
    
    /** {@inheritDoc} */
    public void replaceAll(UnaryOperator<Byte> operator) {
        Objects.requireNonNull(operator);
        for (int i=0; i < _pos; i++) {
            _data[i] = operator.apply(_data[i]);
        }
    }
    private class ReplaceAll1 implements Runnable	{
    	int min, max;
    	UnaryOperator<Byte> operator;
    	ReplaceAll1 (int a, int b, UnaryOperator<Byte> c)	{
    		min = a; max = b; operator = c;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++)	{
    			_data[i] = operator.apply(_data[i]);
    		}
    	}
    }
    /**
     * Replaces each element of this <tt>ArrayList</tt> with the result of
     * applying the operator to that element. Errors or runtime exceptions
     * thrown by the operator are relayed to the caller.
     * @param operator the operator to apply to each element
     * @param numProcs number of threads that will be used
     */
    public void replaceAllPar(UnaryOperator<Byte> operator, int numProcs) {
        Objects.requireNonNull(operator);
        int numMin, numMax;
        int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new ReplaceAll1 (numMin, numMax, operator));
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
    public boolean retainAll( Collection<?> c ) {
    	int oldPos = _pos;
    	Object array[] = c.toArray();
    	byte[] newData =  new byte[_pos];
    	Arrays.sort(array);
    	int j=0;
    	for (int i=0; i<_data.length; i++)	{
    		if (array[i] instanceof Byte)	{
    			if (Arrays.binarySearch(array, ((Byte)_data[i]).byteValue()) >= 0)	{
    				newData[j++] = _data[i];
    	        }
    		}
    	}
    	_data = newData;
    	_pos = j;
    	return oldPos > _pos;
    }
    private class RetainAll1 implements Runnable	{
    	int min, max, idProc;
    	Object[] array;
    	RetainAll1 (int a, int b, Object[] c, int d)	{
    		min = a; max = b; array = c; idProc = d;
    	}
    	public void run ()	{
    		int j=0;
    		byte[] bytesLocal = new byte[max-min];
    		for (int i=min; i<max; i++) {
    			if (Arrays.binarySearch(array, (Byte)_data[i]) >= 0)	{
    				bytesLocal[i-j-min] = _data[i];
    	        }
    	        else	{
    	        	j++;
    	        }
    	    }
    		int numRemaining = max-min-j;
    		atIntGlobal.addAndGet(numRemaining);
    		if (idProc > 0)	{
    			while (intsGlobal[idProc-1] == -1)	{
    				System.out.print("");
    			}
        		intsGlobal[idProc] = numRemaining;
    			j = 0;
    			for (int k=0; k<idProc; k++)	j += intsGlobal[k];
    			System.arraycopy(bytesLocal, 0, _data, j, numRemaining);
    		}
    		else 	{
        		intsGlobal[idProc] = numRemaining;
    			System.arraycopy(bytesLocal, 0, _data, 0, numRemaining);
    		}
    	}
    }
    /**
     * Retains only the elements in this list that are contained in the
     * specified collection. In other words, removes from this list all of
     * its elements that are not contained in the specified.
     * @param c <tt>Collection</tt> containing elements to be retained in this list.
     * Its elements will be ordered.
     * @param numProcs number of threads that will be used
     * @return true if this list changed as a result of the call
     */
    public boolean retainAllPar (Collection<?> c, int numProcs)	{
    	atIntGlobal = new AtomicInteger(0);
    	intsGlobal = new int[numProcs];
    	int oldPos = _pos;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	Object array[] = c.toArray();
    	Arrays.sort(array);
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new RetainAll1 (numMin, numMax, array, i));
    		intsGlobal[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos = atIntGlobal.get();
    	return oldPos > _pos;
    }
    /** {@inheritDoc} */
    public boolean retainAll(TByteCollection c ) {
    	int oldPos = _pos;
    	byte array[] = c.toArray();
    	byte[] newData =  new byte[_pos];
    	Arrays.sort(array);
    	int j=0;
    	for (int i=0; i<_data.length; i++)	{
    		if (Arrays.binarySearch(array, _data[i]) >= 0)	{
    			newData[j++] = _data[i];
    		}
    	}
    	_data = newData;
    	_pos = j;
    	return oldPos > _pos;
    }
    private class RetainAll2 implements Runnable	{
    	int min, max, idProc;
    	byte[] array;
    	RetainAll2 (int a, int b, byte[] c, int d)	{
    		min = a; max = b; array = c; idProc = d;
    	}
    	public void run ()	{
    		int j=0;
    		byte[] bytesLocal = new byte[max-min];
    		for (int i=min; i<max; i++) {
    			if (Arrays.binarySearch(array, _data[i]) >= 0)	{
    				bytesLocal[i-j-min] = _data[i];
    	        }
    	        else	{
    	        	j++;
    	        }
    	    }
    		int numRemaining = max-min-j;
    		atIntGlobal.addAndGet(numRemaining);
    		if (idProc > 0)	{
    			while (intsGlobal[idProc-1] == -1)	{
    				System.out.print("");
    			}
        		intsGlobal[idProc] = numRemaining;
    			j = 0;
    			for (int k=0; k<idProc; k++)	j += intsGlobal[k];
    			System.arraycopy(bytesLocal, 0, _data, j, numRemaining);
    		}
    		else 	{
        		intsGlobal[idProc] = numRemaining;
    			System.arraycopy(bytesLocal, 0, _data, 0, numRemaining);
    		}
    	}
    }
    /**
     * Retains only the elements in this list that are contained in the
     * specified collection. In other words, removes from this list all of
     * its elements that are not contained in the specified.
     * @param c <tt>TByteCollection</tt> containing elements to be retained in this list.
     * Its elements will be ordered.
     * @param numProcs number of threads that will be used
     * @return true if this list changed as a result of the call
     */
    public boolean retainAllPar (TByteCollection collection, int numProcs)	{
    	atIntGlobal = new AtomicInteger(0);
    	intsGlobal = new int[numProcs];
    	int oldPos = _pos;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	byte array[] = collection.toArrayPar(numProcs);
    	Arrays.sort(array);
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new RetainAll2 (numMin, numMax, array, i));
    		intsGlobal[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos = atIntGlobal.get();
    	return oldPos > _pos;
    }

    /** {@inheritDoc} */
    public boolean retainAll( byte[] array ) {
    	int oldPos = _pos;
    	byte[] newData =  new byte[_pos];
    	Arrays.sort(array);
    	int j=0;
    	for (int i=0; i<_data.length; i++)	{
    		if (Arrays.binarySearch(array, _data[i]) >= 0)	{
    			newData[j++] = _data[i];
    		}
    	}
    	_data = newData;
    	_pos = j;
    	return oldPos > _pos;
    }
    /**
     * Retains only the elements in this list that are contained in the
     * specified array. In other words, removes from this list all of
     * its elements that are not contained in the specified.
     * @param c array of bytes containing elements to be retained in this list.
     * Its elements will be ordered.
     * @param numProcs number of threads that will be used
     * @return true if this list changed as a result of the call
     */
    public boolean retainAllPar (byte[] array, int numProcs) {
    	atIntGlobal = new AtomicInteger(0);
    	intsGlobal = new int[numProcs];
    	int oldPos = _pos;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	Arrays.sort(array);
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new RetainAll2 (numMin, numMax, array, i));
    		intsGlobal[i] = -1;
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	_pos = atIntGlobal.get();
    	return oldPos > _pos;
    }
    
    /** {@inheritDoc} */
    public byte set( int index, byte element ) {
        if (index>=_pos ) {
            throw new ArrayIndexOutOfBoundsException (index);
        }

		byte prev_val = _data[index];
        _data[index] = element;
		return prev_val;
    }
    
    /** {@inheritDoc} */
    public int size() {
        return _pos;
    }

    /** {@inheritDoc} */
    public void sort(Comparator <? super Byte> c)	{
    	//Arrays.sort(_data, c);
    }

    /** {@inheritDoc} */
    public TByteSpliterator spliterator()	{
    	return new TByteArrayListSpliterator(this, 0, -1);
    }
    static final class TByteArrayListSpliterator implements TByteSpliterator	{
    	private final TByteArrayList list;
        private int index; // current index, modified on advance/split
        private int fence; // -1 until used; then one past last index

        TByteArrayListSpliterator (TByteArrayList list, int origin, int fence)	{
        	this.list = list;
        	this.index = origin;
        	this.fence = fence;
        }
        
        private int getFence()	{
        	int hi;
        	TByteArrayList lst;
        	if ((hi=fence) < 0)	{
        		if ((lst =list) == null)
        			hi = fence = 0;
        		else	{
        			hi = fence = lst.size();
        		}
        	}
        return hi;
        }
        
        public TByteArrayListSpliterator trySplit()	{
        	int hi = getFence(), lo = index, mid = (lo+hi) >>> 1;
    	return (lo >= mid) ? null : new TByteArrayListSpliterator
    			(list, lo, index = mid);
    	}
        
        public boolean tryAdvance (Consumer <? super Byte> action)	{
        	if (action == null)
        		throw new NullPointerException();
        	int hi = getFence(), i = index;
        	if (i < hi)	{
        		index = i+1;
        		byte e = list._data[i];
        		action.accept(e);
        		return true;
        	}
        	return false;
        }
        
        public void forEachRemaining (Consumer<? super Byte> action)	{
        	int i, hi;
        	TByteArrayList lst; byte[] a;
        	if (action == null)
        		throw new NullPointerException();
        	if ((lst = list) != null && (a = lst._data) != null)	{
        		if ((hi = fence) < 0)
        			hi = lst.size();
        		if ((i=index) <= 0 && (index=hi) <= a.length)	{
        			for (; i<hi; ++i)	{
        				byte e = a[i];
        				action.accept(e);
        			}
        		}
        	}
        	throw new ConcurrentModificationException();
        }
        
        public long estimateSize()	{
        	return (long) (getFence() - index);
        }
        
        public int characteristics ()	{
        	return TByteSpliterator.ORDERED | TByteSpliterator.SIZED |
        			TByteSpliterator.SUBSIZED;
        }
    }
    
    /** {@inheritDoc} */
    public TByteList subList (int fromIndex, int toIndex)	{
        return new TByteSubList(this, fromIndex, toIndex);
    }
    private class TByteSubList extends TByteArrayList implements RandomAccess {
    	static final long serialVersionUID = 1L;
    	private final TByteArrayList parent;
        private final int from;
        private final int to;
        int size;

        TByteSubList(TByteArrayList parent, int fromIndex, int toIndex) {
            this.parent = parent;
            this.from = fromIndex;
            this.to = toIndex;
            size=from-to;
        }
        public byte set(int index, byte e) {
            rangeCheck (index);
            byte oldValue = TByteArrayList.this._data[from + index];
            TByteArrayList.this._data[from + index] = e;
            return oldValue;
        }
        public byte get(int index) {
        	rangeCheck (index);
            return TByteArrayList.this._data[from+index];
        }
        public int size()	{
        	return this.size;
        }
        public void add(int index, byte e) {
            rangeCheckForAdd(index);
            parent.add(from + index, e);
            this.size++;
        }
        public byte removeAt(int index) {
            rangeCheck(index);
            byte result = parent.removeAt(from + index);
            this.size--;
            return result;
        }
        protected void removeRange(int fromIndex, int toIndex) {
            parent.removeRange(from + fromIndex,
                               from + toIndex);
            this.size -= toIndex - fromIndex;
        }
        protected void removeRangePar (int fromIndex, int toIndex, int numProcs)	{
        	parent.removeRangePar(from + fromIndex, from + toIndex, numProcs);
        }
        public boolean addAll(Collection<? extends Byte> c) {
            return addAll(this.size, c);
        }
        public boolean addAllPar(Collection<? extends Byte> c, int numProcs) {
            return addAllPar(this.size, c, numProcs);
        }
        public boolean addAll(int index, Collection<? extends Byte> c) {
            rangeCheckForAdd(index);
        	if ((index < from) || (index > to))
        		return false;
            int cSize = c.size();
            if (cSize==0)
                return false;
            parent.addAll(from + index, c);
            this.size += cSize;
            return true;
        }
        public boolean addAllPar(int index, Collection<? extends Byte> c, int numProcs) {
            rangeCheckForAdd(index);
        	if ((index < from) || (index > to))
        		return false;
            int cSize = c.size();
            if (cSize==0)
                return false;
            parent.addAllPar(from + index, c, numProcs);
            this.size += cSize;
            return true;
        }
        public boolean addAll(TByteCollection c) {
            return addAll(this.size, c);
        }
        public boolean addAllPar(TByteCollection c, int numProcs) {
            return addAllPar(this.size, c, numProcs);
        }
        public boolean addAll(int index, TByteCollection c) {
            rangeCheckForAdd(index);
        	if ((index < from) || (index > to))
        		return false;
            int cSize = c.size();
            if (cSize==0)
                return false;
            parent.addAll(from + index, c);
            this.size += cSize;
            return true;
        }
        public boolean addAllPar(int index, TByteCollection c, int numProcs) {
            rangeCheckForAdd(index);
        	if ((index < from) || (index > to))
        		return false;
            int cSize = c.size();
            if (cSize==0)
                return false;
            parent.addAllPar(from + index, c, numProcs);
            this.size += cSize;
            return true;
        }
        public boolean addAll(byte[] c) {
            return addAll(this.size, c);
        }
        public boolean addAllPar(byte[] c, int numProcs) {
            return addAllPar(this.size, c, numProcs);
        }
        public boolean addAll(int index, byte[] c) {
            rangeCheckForAdd(index);
        	if ((index < from) || (index > to))
        		return false;
            int cSize = c.length;
            if (cSize==0)
                return false;
            parent.addAll(from + index, c);
            this.size += cSize;
            return true;
        }
        public boolean addAllPar(int index, byte[] c, int numProcs) {
            rangeCheckForAdd(index);
        	if ((index < from) || (index > to))
        		return false;
            int cSize = c.length;
            if (cSize==0)
                return false;
            parent.addAllPar(from + index, c, numProcs);
            this.size += cSize;
            return true;
        }
        
        public TByteListIterator listIterator (final int index)	{
        	rangeCheckForAdd(index);
        	return new TByteListIterator ()	{
        		int cursor = index;
        		int lastRet = -1;
        		
        		public boolean hasNext()	{
        			return cursor != (TByteSubList.this.size());
        		}
        		
        		public byte next()	{
        			int i = cursor;
        			if (i >= TByteSubList.this.size())
        				throw new NoSuchElementException();
        			byte[] data = TByteArrayList.this._data;
        			if (i >= data.length)
        				throw new ConcurrentModificationException();
        			cursor = i+1;
        			return data[lastRet=i];
        		}
        		
        		public boolean hasPrevious ()	{
        			return cursor != 0;
        		}
        		
        		public byte previous()	{
        			int i = cursor -1;
        			if (i<0)
        				throw new NoSuchElementException();
        			byte[] data = TByteArrayList.this._data;
        			if (i >= data.length)
        				throw new ConcurrentModificationException();
        			cursor = i;
        			return data[lastRet=i];
        		}
        		
        		public void forEachRemaining (Consumer<? super Byte> consumer)	{
        			Objects.requireNonNull(consumer);
        			final int size = TByteSubList.this.size();
        			int i = cursor;
        			if (i >= size)
        				return;
        			final byte[] data = TByteArrayList.this._data;
        			if (i >= data.length)
        				throw new ConcurrentModificationException();
        			while (i!= size)
        				consumer.accept(data[i++]);
        			lastRet = cursor = i;
        		}
        		
        		public void forEachRemainingPar (Consumer<? super Byte> consumer, int numProcs)	{
        			Objects.requireNonNull(consumer);
        			final int size = TByteSubList.this.size();
        			int i = cursor;
        			if (i >= size)
        				return;
        			final byte[] data = TByteArrayList.this._data;
        			if (i >= data.length)
        				throw new ConcurrentModificationException();
        			int numElemProc = (size-cursor)/numProcs;
        			threads = new Thread[numProcs];
        			int numMin, numMax;
        			for (int j=0; j<numProcs; i++)	{
        	    		numMin = limiteMin (i, numElemProc, cursor);
        	    		numMax = limiteMax (i, numElemProc, cursor, numProcs, size);
        	    		threads[i] = new Thread (new ForEach1 (numMin, numMax, consumer));
        	    		threads[i].start();
        			}
        			for (int j=0; j<numProcs; j++)	{
        				try {
							threads[j].join();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
        			}
        		}
        		
        		public int nextIndex()	{
        			return cursor;
        		}
        		
        		public int previousIndex ()	{
        			return cursor -1;
        		}
        		
        		public void remove()	{
        			if (lastRet < 0)
        				throw new IllegalStateException();
        			try	{
        				TByteSubList.this.removeAt(lastRet);
        				cursor = lastRet;
        				lastRet = -1;
        			}
        			catch (IndexOutOfBoundsException ex)	{
        				throw new ConcurrentModificationException();
        			}
        		}
        		
        		public void set (byte e)	{
        			if (lastRet < 0)
        				throw new IllegalStateException();
        			try	{
        				TByteArrayList.this.set(lastRet,e);
        			}
        			catch (IndexOutOfBoundsException ex)	{
        				throw new ConcurrentModificationException();
        			}
        		}
        		
        		public void add (byte e)	{
        			try	{
        				int i=cursor;
        				TByteSubList.this.add(i,e);
        				cursor = i+1;
        				lastRet=-1;
        			}
        			catch (IndexOutOfBoundsException ex)	{
        				throw new ConcurrentModificationException();
        			}
        		}
        	};
        }
       	
        public TByteList subList (int fromIndex, int toIndex)	{
        	if (fromIndex < 0)
        		throw new IndexOutOfBoundsException ("fromIndex = " + fromIndex);
        	if (toIndex > size)
        		throw new IndexOutOfBoundsException ("toIndex = " + toIndex);
        	if (fromIndex > toIndex)
        		throw new IllegalArgumentException ("fromIndex(" + fromIndex +
                        ") > toIndex(" + toIndex + ")");
        	return new TByteSubList (this, fromIndex, toIndex);
        }

        private void rangeCheck(int index) {
            if (index < 0 || index >= this.size)
                throw new IndexOutOfBoundsException("Index: " + index +
                		", Size: " + this.size);
        }

        private void rangeCheckForAdd(int index) {
            if (index < 0 || index > this.size)
                throw new IndexOutOfBoundsException("Index: " + index +
                		", Size: " + this.size);
        }
    }

    /** {@inheritDoc} */
    public byte[] toArray() {
        byte[] array = new byte[_pos];
        System.arraycopy(_data, 0, array, 0, _pos);
        return array;
    }
    /**
     * Returns an array containing all of the elements in this list in
     * proper sequence (from first to last element).
     * @param numProcs number of threads that will be used
     * @return an array containing all of the elements in this list
     *  in proper sequence
     */
    public byte[] toArrayPar(int numProcs) {
        return ArraysPar.copyOfPar(_data, _pos, numProcs);
    }

    /** {@inheritDoc} */
    public byte[] toArray (byte[] a) {
    	if (a.length < _pos)
    		a = new byte[_pos];
        System.arraycopy(_data, 0, a, 0, _pos);
        return a;
    }
    /**
     * Returns an array containing all of the elements in this list in proper
     * sequence (from first to last element). If the list fits in the specified
     * array, it is returned therein. Otherwise, a new array is allocated with
     * the size of this list.
     * @param a the array into which the elements of this list are to be stored,
     * if it is be enough; otherwise, a new array is allocated for this purpose
     * @return an array containing the elements of this list
     */
    public byte[] toArrayPar (byte[] a, int numProcs) {
        if (a.length < _pos)
        	a = new byte[_pos];
        return ArraysPar.moveOfRangePar(_data, a, 0, _pos, 0, numProcs);
    }

    @Override
    /** {@inheritDoc} */
    public String toString() {
        final StringBuilder buf = new StringBuilder( "[" );
        if (_pos>0)	{
        	buf.append(_data[0]);
        	for (int i=1; i<_pos; i++) {
        		buf.append(", ");
        		buf.append(_data[i]);
        	}	
        }
        buf.append( "]" );
        return buf.toString();
    }
    private class ToString1 implements Runnable	{
    	int min, max, numThread;
    	ToString1 (int a, int b, int c)	{
    		min = a; max = b; numThread = c;
    	}
    	public void run ()	{
            final StringBuilder buf = new StringBuilder( "" );
    		for (int i=min; i<max; i++) {
                buf.append( _data[ i ] );
                buf.append( ", " );
            }
    		if (numThread>0)
				try {
					threads[numThread-1].join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		sbGlobal .append(buf);
    	}
    }
    /**
     * Returns a string representation of this collection. The string
     * representation consists of a list of the collection's elements in
     * the order they are returned by its iterator, enclosed in square
     * brackets("[]"). Adjacent elements are separated by te characters ", "
     * (comma and space). 
     * @param numProcs number of threads that will be used
     * @return a string representation of this collection
     */
    public String toStringPar (int numProcs)	{
    	sbGlobal = new StringBuilder();
    	sbGlobal.append('[');
    	int numElemProc = (_pos-1)/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos-1);
    		threads[i] = new Thread (new ToString1 (numMin, numMax, i));
    		threads[i].start();
    	}
    	try {
			threads[numProcs-1].join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	if ( size() > 0 ) {
            sbGlobal.append( _data[ _pos - 1 ] );
        }
        sbGlobal.append( "]" );
    	return sbGlobal.toString();
    }
        
    /** {@inheritDoc} */
    public void trimToSize() {
        if (_data.length > _pos) {
            byte[] tmp = new byte[_pos];
            toArray (tmp);
            _data = tmp;
        }
    }
    /**
     * Trims the capacity of this <tt>ArrayList</tt> instance to be the list's
     * current size. An application can use this operation to minimize the storage
     * of an <tt>ArrayList</tt> instance.
     * @param numProcs number of threads that will be used
     */
    public void trimToSizePar (int numProcs)	{
    	if (_data.length > size())	{
    		_data = ArraysPar.copyOfPar(_data, size(), numProcs);
    	}
    }




    
    


   


    
    //Operations that are not included in the Java implementation,
    //but can be interesting.
    
    
    /**
     * 
     * @param val value to fill the collection with
     * @param numProcs number of processors that will be used
     */
    public void fillPar (byte val, int numProcs) {
        //Arrays.fill( _data, 0, _pos, val );
    	ArraysPar.fillPar(_data, 0, _pos, val, numProcs);
    }
    /** {@inheritDoc} */
    public void fill( byte val ) {
        Arrays.fill( _data, 0, _pos, val );
    }

    public void fillPar( int fromIndex, int toIndex, byte val, int numProcs) {
        if ( toIndex > _pos ) {
          ensureCapacity( toIndex );
          _pos = toIndex;
        }
        //Arrays.fill( _data, fromIndex, toIndex, val );
        ArraysPar.fillPar(_data, fromIndex, toIndex, val, numProcs);
    }

    /** {@inheritDoc} */
    public void fill( int fromIndex, int toIndex, byte val ) {
        if ( toIndex > _pos ) {
          ensureCapacity( toIndex );
          _pos = toIndex;
        }
        Arrays.fill( _data, fromIndex, toIndex, val );
    }
    
    private class Max1 implements Runnable	{
    	int min, max;
    	Max1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		byte maximum = byteGlobal;
    		for (int i=min; i<max; i++) {
            	if ( _data[ i ] > maximum ) {
            		maximum = _data[ i ];
            	}
            }
    		synchronized (objectSync)	{
    			if (maximum > byteGlobal)	byteGlobal = maximum;
    		}
    	}
    }
    /**
     * 
     * @param numProcs number of processors that will be used
     * @return the value of the maximum element in the collection
     */
    public byte maxPar(int numProcs)	{
    	if (size() == 0)	{
    		throw new IllegalStateException ("cannot find maximum of an empty list");
    	}
    	byteGlobal = Byte.MIN_VALUE;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new Max1 (numMin, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return byteGlobal;
    }
    /** {@inheritDoc} */
    public byte max() {
        if ( size() == 0 ) {
            throw new IllegalStateException("cannot find maximum of an empty list");
        }
        byte max = Byte.MIN_VALUE;
        for ( int i = 0; i < _pos; i++ ) {
        	if ( _data[ i ] > max ) {
        		max = _data[ i ];
        	}
        }
        return max;
    }

    private class Min1 implements Runnable	{
    	int min, max;
    	Min1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		byte minimum = byteGlobal;
    		for (int i=min; i<max; i++) {
            	if ( _data[ i ] < minimum ) {
            		minimum = _data[ i ];
            	}
            }
    		synchronized (objectSync)	{
    			if (minimum < byteGlobal)	byteGlobal = minimum;
    		}
    	}
    }
    /**
     * 
     * @param numProcs number of processors that will be used
     * @return the value of the minimum element in the collection
     */
    public byte minPar(int numProcs)	{
    	if (size() == 0)	{
    		throw new IllegalStateException ("cannot find maximum of an empty list");
    	}
    	byteGlobal = Byte.MAX_VALUE;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new Min1 (numMin, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return byteGlobal;
    }
    /** {@inheritDoc} */
    public byte min() {
        if ( size() == 0 ) {
            throw new IllegalStateException( "cannot find minimum of an empty list" );
        }
        byte min = Byte.MAX_VALUE;
        for ( int i = 0; i < _pos; i++ ) {
        	if ( _data[i] < min ) {
        		min = _data[i];
        	}
        }
        return min;
    }
    
    /** {@inheritDoc} */
    public void reverse() {
        reverse(0, _pos);
    }
   /**
     * 
     * @param numProcs number of processors that will be used
     */
    public void reversePar(int numProcs) {
        reversePar( 0, _pos, numProcs);
    }
    /** {@inheritDoc} */
    public void reverse( int from, int to ) {
        if ( from == to ) {
            return;             // nothing to do
        }
        if ( from > to ) {
            throw new IllegalArgumentException( "from cannot be greater than to" );
        }
        for ( int i = from, j = to - 1; i < j; i++, j-- ) {
            swap( i, j );
        }
    }
    private class Reverse1 implements Runnable	{
    	int min, max;
    	Reverse1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		int j=_pos-min-1;
    		for (int i = min; i<max; i++) {
                swap( i, j-- );
            }
    	}
    }
    /**
     * 
     * @param from first position to be reversed
     * @param to last position to be reversed
     * @param numProcs number of processors that will be used
     */
    public void reversePar (int from, int to, int numProcs)	{
    	if (from == to)	return;
    	if (from > to)	throw new IllegalArgumentException ("from cannot be greater than to");
    	bytesGlobal = new byte[to-from];
    	//int numElemProc = (to-from)/numProcs;
    	int numElemProc = (to-from)/(numProcs*2);
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, to/2);
    		threads[i] = new Thread (new Reverse1 (numMin, numMax));
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
     * Swap the values at offsets <tt>i</tt> and <tt>j</tt>.
     *
     * @param i an offset into the data array
     * @param j an offset into the data array
     */
    private void swap( int i, int j ) {
        byte tmp = _data[ i ];
        _data[ i ] = _data[ j ];
        _data[ j ] = tmp;
    }

    private class Sum1 implements Runnable	{
    	int min, max;
    	Sum1 (int a, int b)	{
    		min = a; max = b;
    	}
    	public void run ()	{
    		byte suma = 0;
    		for (int i=min; i<max; i++)	{
    			suma += _data[i];
    		}
    		synchronized (objectSync)	{
    			byteGlobal += suma;
    		}
    	}
    }
    /**
     * 
     * @param numProcs number of processors that will be used
     * @return sum of the values of all the elements of the collection
     */
    public byte sumPar (int numProcs)	{
    	byteGlobal = (byte)0;
    	int numElemProc = _pos/numProcs;
    	threads = new Thread[numProcs];
    	int numMin, numMax;
    	for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, _pos);
    		threads[i] = new Thread (new Sum1 (numMin, numMax));
    		threads[i].start();
    	}
    	for (int i=0; i<numProcs; i++)	{
    		try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    	}
    	return byteGlobal;
    }
    /** {@inheritDoc} */
    public byte sum() {
        byte sum = 0;
        for ( int i = 0; i < _pos; i++ ) {
			sum += _data[ i ];
        }
        return sum;
    }    
} // TByteArrayList
