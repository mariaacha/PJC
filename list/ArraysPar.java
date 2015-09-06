package PJC.list;

import java.lang.reflect.Array;

public class ArraysPar {
    
    protected static Thread[] threads;
    
    private static byte[] bytesGlobal;
    private static char[] charsGlobal;
    private static double[] doublesGlobal;
    private static float[] floatsGlobal;
    private static int[] intsGlobal;
    private static long[] longsGlobal;
    private static Object[] objectsGlobal;
    private static short[] shortsGlobal;
    
    public static int limiteMin (int i, int numElem, int offset)	{
		return i*numElem+offset;
	}

	public static int limiteMax (int i, int numElem, int offset, int numProc, int numMax)	{
		if ((i+1)==numProc) return numMax;
		return (i+1)*numElem+offset;
	}

    private ArraysPar() {}

    /**
     * A comparator that implements the natural ordering of a group of
     * mutually comparable elements. May be used when a supplied
     * comparator is null. To simplify code-sharing within underlying
     * implementations, the compare method only declares type Object
     * for its second argument.
     *
     * Arrays class implementor's note: It is an empirical matter
     * whether ComparableTimSort offers any performance benefit over
     * TimSort used with this comparator.  If not, you are better off
     * deleting or bypassing ComparableTimSort.  There is currently no
     * empirical case for separating them for parallel sorting, so all
     * public Object parallelSort methods use the same comparator
     * based implementation.
     */
    /**static final class NaturalOrder implements Comparator<Object> {
        @SuppressWarnings("unchecked")
        public int compare(Object first, Object second) {
            return ((Comparable<Object>)first).compareTo(second);
        }
        static final NaturalOrder INSTANCE = new NaturalOrder();
    }*/

    /**
     * Checks that {@code fromIndex} and {@code toIndex} are in
     * the range and throws an exception if they aren't.
     */
    private static void rangeCheck(int arrayLength, int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException(
                    "fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
        }
        if (fromIndex < 0) {
            throw new ArrayIndexOutOfBoundsException(fromIndex);
        }
        if (toIndex > arrayLength) {
            throw new ArrayIndexOutOfBoundsException(toIndex);
        }
    }


    /**
     * Assigns the specified long value to each element of the specified array
     * of longs.
     *
     * @param a the array to be filled
     * @param val the value to be stored in all elements of the array
     */
    public static void fill(long[] a, long val) {
        for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }
    
    private static class Fill1 implements Runnable	{
    	int min, max;
    	long val;
    	long[] array;
    	Fill1 (int a, int b, long c, long[] d)	{
    		min = a; max = b; val = c; array = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			array[i] = val;
            }
    	}
    }
    
    public static void fillPar(long[] a, long val, int numProcs) {
        /**for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;*/
    	int numElemProc = a.length/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = a.length;
    		threads[i] = new Thread (new Fill1 (i*numElemProc, numMax, val, a));
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
     * Assigns the specified long value to each element of the specified
     * range of the specified array of longs.  The range to be filled
     * extends from index <tt>fromIndex</tt>, inclusive, to index
     * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex==toIndex</tt>, the
     * range to be filled is empty.)
     *
     * @param a the array to be filled
     * @param fromIndex the index of the first element (inclusive) to be
     *        filled with the specified value
     * @param toIndex the index of the last element (exclusive) to be
     *        filled with the specified value
     * @param val the value to be stored in all elements of the array
     * @throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt> or
     *         <tt>toIndex &gt; a.length</tt>
     */
    public static void fill(long[] a, int fromIndex, int toIndex, long val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }
    
    public static void fillPar (long[] a, int fromIndex, int toIndex, long val, int numProcs)	{
    	rangeCheck (a.length, fromIndex, toIndex);
    	int numElemProc = (toIndex - fromIndex)/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = fromIndex + (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = toIndex;
    		threads[i] = new Thread (new Fill1 (i*numElemProc + fromIndex, numMax, val, a));
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
     * Assigns the specified int value to each element of the specified array
     * of ints.
     *
     * @param a the array to be filled
     * @param val the value to be stored in all elements of the array
     */
    public static void fill(int[] a, int val) {
        for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }
    
    private static class Fill2 implements Runnable	{
    	int min, max, val;
    	int[] array;
    	Fill2 (int a, int b, int c, int[] d)	{
    		min = a; max = b; val = c; array = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			array[i] = val;
            }
    	}
    }
    
    public static void fillPar(int[] a, int val, int numProcs) {
        /**for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;*/
    	int numElemProc = a.length/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = a.length;
    		threads[i] = new Thread (new Fill2 (i*numElemProc, numMax, val, a));
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
     * Assigns the specified int value to each element of the specified
     * range of the specified array of ints.  The range to be filled
     * extends from index <tt>fromIndex</tt>, inclusive, to index
     * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex==toIndex</tt>, the
     * range to be filled is empty.)
     *
     * @param a the array to be filled
     * @param fromIndex the index of the first element (inclusive) to be
     *        filled with the specified value
     * @param toIndex the index of the last element (exclusive) to be
     *        filled with the specified value
     * @param val the value to be stored in all elements of the array
     * @throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt> or
     *         <tt>toIndex &gt; a.length</tt>
     */
    public static void fill(int[] a, int fromIndex, int toIndex, int val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }
    
    public static void fillPar (int[] a, int fromIndex, int toIndex, int val, int numProcs)	{
    	rangeCheck (a.length, fromIndex, toIndex);
    	int numElemProc = (toIndex - fromIndex)/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = fromIndex + (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = toIndex;
    		threads[i] = new Thread (new Fill2 (i*numElemProc + fromIndex, numMax, val, a));
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
     * Assigns the specified short value to each element of the specified array
     * of shorts.
     *
     * @param a the array to be filled
     * @param val the value to be stored in all elements of the array
     */
    public static void fill(short[] a, short val) {
        for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }
    
    private static class Fill3 implements Runnable	{
    	int min, max;
    	short val;
    	short[] array;
    	Fill3 (int a, int b, short c, short[] d)	{
    		min = a; max = b; val = c; array = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			array[i] = val;
            }
    	}
    }
    
    public static void fillPar(short[] a, short val, int numProcs) {
        /**for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;*/
    	int numElemProc = a.length/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = a.length;
    		threads[i] = new Thread (new Fill3 (i*numElemProc, numMax, val, a));
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
     * Assigns the specified short value to each element of the specified
     * range of the specified array of shorts.  The range to be filled
     * extends from index <tt>fromIndex</tt>, inclusive, to index
     * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex==toIndex</tt>, the
     * range to be filled is empty.)
     *
     * @param a the array to be filled
     * @param fromIndex the index of the first element (inclusive) to be
     *        filled with the specified value
     * @param toIndex the index of the last element (exclusive) to be
     *        filled with the specified value
     * @param val the value to be stored in all elements of the array
     * @throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt> or
     *         <tt>toIndex &gt; a.length</tt>
     */
    public static void fill(short[] a, int fromIndex, int toIndex, short val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }
    
    public static void fillPar (short[] a, int fromIndex, int toIndex, short val, int numProcs)	{
    	rangeCheck (a.length, fromIndex, toIndex);
    	int numElemProc = (toIndex - fromIndex)/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = fromIndex + (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = toIndex;
    		threads[i] = new Thread (new Fill3 (i*numElemProc + fromIndex, numMax, val, a));
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
     * Assigns the specified char value to each element of the specified array
     * of chars.
     *
     * @param a the array to be filled
     * @param val the value to be stored in all elements of the array
     */
    public static void fill(char[] a, char val) {
        for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }
    
    private static class Fill4 implements Runnable	{
    	int min, max;
    	char val;
    	char[] array;
    	Fill4 (int a, int b, char c, char[] d)	{
    		min = a; max = b; val = c; array = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			array[i] = val;
            }
    	}
    }
    
    public static void fillPar(char[] a, char val, int numProcs) {
        /**for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;*/
    	int numElemProc = a.length/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = a.length;
    		threads[i] = new Thread (new Fill4 (i*numElemProc, numMax, val, a));
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
     * Assigns the specified char value to each element of the specified
     * range of the specified array of chars.  The range to be filled
     * extends from index <tt>fromIndex</tt>, inclusive, to index
     * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex==toIndex</tt>, the
     * range to be filled is empty.)
     *
     * @param a the array to be filled
     * @param fromIndex the index of the first element (inclusive) to be
     *        filled with the specified value
     * @param toIndex the index of the last element (exclusive) to be
     *        filled with the specified value
     * @param val the value to be stored in all elements of the array
     * @throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt> or
     *         <tt>toIndex &gt; a.length</tt>
     */
    public static void fill(char[] a, int fromIndex, int toIndex, char val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }
    
    public static void fillPar (char[] a, int fromIndex, int toIndex, char val, int numProcs)	{
    	rangeCheck (a.length, fromIndex, toIndex);
    	int numElemProc = (toIndex - fromIndex)/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = fromIndex + (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = toIndex;
    		threads[i] = new Thread (new Fill4 (i*numElemProc + fromIndex, numMax, val, a));
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
     * Assigns the specified byte value to each element of the specified array
     * of bytes.
     *
     * @param a the array to be filled
     * @param val the value to be stored in all elements of the array
     */
    public static void fill(byte[] a, byte val) {
        for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }
    
    private static class Fill5 implements Runnable	{
    	int min, max;
    	byte val;
    	byte[] array;
    	Fill5 (int a, int b, byte c, byte[] d)	{
    		min = a; max = b; val = c; array = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			array[i] = val;
            }
    	}
    }
    
    public static void fillPar(byte[] a, byte val, int numProcs) {
        /**for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;*/
    	int numElemProc = a.length/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = a.length;
    		threads[i] = new Thread (new Fill5 (i*numElemProc, numMax, val, a));
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
     * Assigns the specified byte value to each element of the specified
     * range of the specified array of bytes.  The range to be filled
     * extends from index <tt>fromIndex</tt>, inclusive, to index
     * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex==toIndex</tt>, the
     * range to be filled is empty.)
     *
     * @param a the array to be filled
     * @param fromIndex the index of the first element (inclusive) to be
     *        filled with the specified value
     * @param toIndex the index of the last element (exclusive) to be
     *        filled with the specified value
     * @param val the value to be stored in all elements of the array
     * @throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt> or
     *         <tt>toIndex &gt; a.length</tt>
     */
    public static void fill(byte[] a, int fromIndex, int toIndex, byte val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }
    
    public static void fillPar (byte[] a, int fromIndex, int toIndex, byte val, int numProcs)	{
    	rangeCheck (a.length, fromIndex, toIndex);
    	int numElemProc = (toIndex - fromIndex)/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = fromIndex + (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = toIndex;
    		threads[i] = new Thread (new Fill5 (i*numElemProc + fromIndex, numMax, val, a));
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
     * Assigns the specified boolean value to each element of the specified
     * array of booleans.
     *
     * @param a the array to be filled
     * @param val the value to be stored in all elements of the array
     */
    public static void fill(boolean[] a, boolean val) {
        for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }
    
    private static class Fill6 implements Runnable	{
    	int min, max;
    	boolean val;
    	boolean[] array;
    	Fill6 (int a, int b, boolean c, boolean[] d)	{
    		min = a; max = b; val = c; array = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			array[i] = val;
            }
    	}
    }
    
    public static void fillPar(boolean[] a, boolean val, int numProcs) {
        /**for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;*/
    	int numElemProc = a.length/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = a.length;
    		threads[i] = new Thread (new Fill6 (i*numElemProc, numMax, val, a));
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
     * Assigns the specified boolean value to each element of the specified
     * range of the specified array of booleans.  The range to be filled
     * extends from index <tt>fromIndex</tt>, inclusive, to index
     * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex==toIndex</tt>, the
     * range to be filled is empty.)
     *
     * @param a the array to be filled
     * @param fromIndex the index of the first element (inclusive) to be
     *        filled with the specified value
     * @param toIndex the index of the last element (exclusive) to be
     *        filled with the specified value
     * @param val the value to be stored in all elements of the array
     * @throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt> or
     *         <tt>toIndex &gt; a.length</tt>
     */
    public static void fill(boolean[] a, int fromIndex, int toIndex,
                            boolean val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }
    
    public static void fillPar (boolean[] a, int fromIndex, int toIndex, boolean val, int numProcs)	{
    	rangeCheck (a.length, fromIndex, toIndex);
    	int numElemProc = (toIndex - fromIndex)/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = fromIndex + (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = toIndex;
    		threads[i] = new Thread (new Fill6 (i*numElemProc + fromIndex, numMax, val, a));
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
     * Assigns the specified double value to each element of the specified
     * array of doubles.
     *
     * @param a the array to be filled
     * @param val the value to be stored in all elements of the array
     */
    public static void fill(double[] a, double val) {
        for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }
    
    private static class Fill7 implements Runnable	{
    	int min, max;
    	double val;
    	double[] array;
    	Fill7 (int a, int b, double c, double[] d)	{
    		min = a; max = b; val = c; array = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			array[i] = val;
            }
    	}
    }
    
    public static void fillPar(double[] a, double val, int numProcs) {
        /**for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;*/
    	int numElemProc = a.length/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = a.length;
    		threads[i] = new Thread (new Fill7 (i*numElemProc, numMax, val, a));
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
     * Assigns the specified double value to each element of the specified
     * range of the specified array of doubles.  The range to be filled
     * extends from index <tt>fromIndex</tt>, inclusive, to index
     * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex==toIndex</tt>, the
     * range to be filled is empty.)
     *
     * @param a the array to be filled
     * @param fromIndex the index of the first element (inclusive) to be
     *        filled with the specified value
     * @param toIndex the index of the last element (exclusive) to be
     *        filled with the specified value
     * @param val the value to be stored in all elements of the array
     * @throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt> or
     *         <tt>toIndex &gt; a.length</tt>
     */
    public static void fill(double[] a, int fromIndex, int toIndex,double val){
        rangeCheck(a.length, fromIndex, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }
    
    public static void fillPar (double[] a, int fromIndex, int toIndex, double val, int numProcs)	{
    	rangeCheck (a.length, fromIndex, toIndex);
    	int numElemProc = (toIndex - fromIndex)/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = fromIndex + (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = toIndex;
    		threads[i] = new Thread (new Fill7 (i*numElemProc + fromIndex, numMax, val, a));
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
     * Assigns the specified float value to each element of the specified array
     * of floats.
     *
     * @param a the array to be filled
     * @param val the value to be stored in all elements of the array
     */
    public static void fill(float[] a, float val) {
        for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }
    
    private static class Fill8 implements Runnable	{
    	int min, max;
    	float val;
    	float[] array;
    	Fill8 (int a, int b, float c, float[] d)	{
    		min = a; max = b; val = c; array = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			array[i] = val;
            }
    	}
    }
    
    public static void fillPar(float[] a, float val, int numProcs) {
        /**for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;*/
    	int numElemProc = a.length/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = a.length;
    		threads[i] = new Thread (new Fill8 (i*numElemProc, numMax, val, a));
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
     * Assigns the specified float value to each element of the specified
     * range of the specified array of floats.  The range to be filled
     * extends from index <tt>fromIndex</tt>, inclusive, to index
     * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex==toIndex</tt>, the
     * range to be filled is empty.)
     *
     * @param a the array to be filled
     * @param fromIndex the index of the first element (inclusive) to be
     *        filled with the specified value
     * @param toIndex the index of the last element (exclusive) to be
     *        filled with the specified value
     * @param val the value to be stored in all elements of the array
     * @throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt> or
     *         <tt>toIndex &gt; a.length</tt>
     */
    public static void fill(float[] a, int fromIndex, int toIndex, float val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }
    
    public static void fillPar (float[] a, int fromIndex, int toIndex, float val, int numProcs)	{
    	rangeCheck (a.length, fromIndex, toIndex);
    	int numElemProc = (toIndex - fromIndex)/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = fromIndex + (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = toIndex;
    		threads[i] = new Thread (new Fill8 (i*numElemProc + fromIndex, numMax, val, a));
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
     * Assigns the specified Object reference to each element of the specified
     * array of Objects.
     *
     * @param a the array to be filled
     * @param val the value to be stored in all elements of the array
     * @throws ArrayStoreException if the specified value is not of a
     *         runtime type that can be stored in the specified array
     */
    public static void fill(Object[] a, Object val) {
        for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;
    }
    
    private static class Fill9 implements Runnable	{
    	int min, max;
    	Object val;
    	Object[] array;
    	Fill9 (int a, int b, Object c, Object[] d)	{
    		min = a; max = b; val = c; array = d;
    	}
    	public void run ()	{
    		for (int i=min; i<max; i++) {
    			array[i] = val;
            }
    	}
    }
    
    public static void fillPar(Object[] a, Object val, int numProcs) {
        /**for (int i = 0, len = a.length; i < len; i++)
            a[i] = val;*/
    	int numElemProc = a.length/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = a.length;
    		threads[i] = new Thread (new Fill9 (i*numElemProc, numMax, val, a));
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
     * Assigns the specified Object reference to each element of the specified
     * range of the specified array of Objects.  The range to be filled
     * extends from index <tt>fromIndex</tt>, inclusive, to index
     * <tt>toIndex</tt>, exclusive.  (If <tt>fromIndex==toIndex</tt>, the
     * range to be filled is empty.)
     *
     * @param a the array to be filled
     * @param fromIndex the index of the first element (inclusive) to be
     *        filled with the specified value
     * @param toIndex the index of the last element (exclusive) to be
     *        filled with the specified value
     * @param val the value to be stored in all elements of the array
     * @throws IllegalArgumentException if <tt>fromIndex &gt; toIndex</tt>
     * @throws ArrayIndexOutOfBoundsException if <tt>fromIndex &lt; 0</tt> or
     *         <tt>toIndex &gt; a.length</tt>
     * @throws ArrayStoreException if the specified value is not of a
     *         runtime type that can be stored in the specified array
     */
    public static void fill(Object[] a, int fromIndex, int toIndex, Object val) {
        rangeCheck(a.length, fromIndex, toIndex);
        for (int i = fromIndex; i < toIndex; i++)
            a[i] = val;
    }
    
    public static void fillPar (Object[] a, int fromIndex, int toIndex, Object val, int numProcs)	{
    	rangeCheck (a.length, fromIndex, toIndex);
    	int numElemProc = (toIndex - fromIndex)/numProcs;
    	threads = new Thread [numProcs];
    	for (int i=0; i<numProcs; i++)	{
    		int numMax = fromIndex + (i+1) * numElemProc;
    		if ((i+1) == numProcs)	numMax = toIndex;
    		threads[i] = new Thread (new Fill9 (i*numElemProc + fromIndex, numMax, val, a));
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

    // Cloning

    /**
     * Copies the specified array, truncating or padding with nulls (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain <tt>null</tt>.
     * Such indices will exist if and only if the specified length
     * is greater than that of the original array.
     * The resulting array is of exactly the same class as the original array.
     *
     * @param <T> the class of the objects in the array
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @return a copy of the original array, truncated or padded with nulls
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copyOf(T[] original, int newLength) {
        return (T[]) copyOf(original, newLength, original.getClass());
    }

    /**
     * Copies the specified array, truncating or padding with nulls (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain <tt>null</tt>.
     * Such indices will exist if and only if the specified length
     * is greater than that of the original array.
     * The resulting array is of the class <tt>newType</tt>.
     * @param <U>
     *
     * @param <U> the class of the objects in the original array
     * @param <T>
     * @param <T> the class of the objects in the returned array
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @param newType the class of the copy to be returned
     * @return a copy of the original array, truncated or padded with nulls
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @throws ArrayStoreException if an element copied from
     *     <tt>original</tt> is not of a runtime type that can be stored in
     *     an array of class <tt>newType</tt>
     * @since 1.6
     */
    
    /**private static class CopyOf1<U, T> implements Runnable	{
    	int min, max;
    	U[] original;
    	T[] copy;
    	CopyOf1 (int a, int b, U[] c, T[] d)	{
    		min = a; max = b;
    		original = c; copy = d;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, copy, min, max-min);
    	}
    }
    public static <T,U> T[] copyOfPar(U[] original, int newLength, Class<? extends T[]> newType, int numProcs) {
        @SuppressWarnings("unchecked")
        T[] copy = ((Object)newType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        int numMin, numMax;
        int numElemProc = Math.min(original.length, newLength)/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, Math.min(original.length, newLength));
        	threads[i] = new Thread (new CopyOf1 (numMin, numMax, original, copy));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        return copy;
    }*/
    
    private static class CopyOf1 implements Runnable	{
    	int min, max;
    	byte[] original;
    	CopyOf1 (int a, int b, byte[] c)	{
    		min = a; max = b;
    		original = c;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, intsGlobal, min, max-min);
    	}
    }
    private static class CopyOf2 implements Runnable	{
    	int min, max;
    	char[] original;
    	CopyOf2 (int a, int b, char[] c)	{
    		min = a; max = b;
    		original = c;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, intsGlobal, min, max-min);
    	}
    }
    private static class CopyOf3 implements Runnable	{
    	int min, max;
    	double[] original;
    	CopyOf3 (int a, int b, double[] c)	{
    		min = a; max = b;
    		original = c;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, intsGlobal, min, max-min);
    	}
    }
    private static class CopyOf4 implements Runnable	{
    	int min, max;
    	float[] original;
    	CopyOf4 (int a, int b, float[] c)	{
    		min = a; max = b;
    		original = c;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, intsGlobal, min, max-min);
    	}
    }
    private static class CopyOf5 implements Runnable	{
    	int min, max;
    	int[] original;
    	CopyOf5 (int a, int b, int[] c)	{
    		min = a; max = b;
    		original = c;
    	}
    	public void run ()	{
    		//System.out.println ("Copiando desde " + min + " hasta " + max);
    		//Long time_start = System.currentTimeMillis();
    		System.arraycopy(original, min, intsGlobal, min, max-min);
    		//time_start-=System.currentTimeMillis();
    		//System.out.println ("Tiempo: " + time_start);
    	}
    }
    private static class CopyOf6 implements Runnable	{
    	int min, max;
    	long[] original;
    	CopyOf6 (int a, int b, long[] c)	{
    		min = a; max = b;
    		original = c;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, intsGlobal, min, max-min);
    	}
    }
    private static class CopyOf7 implements Runnable	{
    	int min, max;
    	Object[] original;
    	CopyOf7 (int a, int b, Object[] c)	{
    		min = a; max = b;
    		original = c;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, intsGlobal, min, max-min);
    	}
    }
    private static class CopyOf8 implements Runnable	{
    	int min, max;
    	short[] original;
    	CopyOf8 (int a, int b, short[] c)	{
    		min = a; max = b;
    		original = c;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, intsGlobal, min, max-min);
    	}
    }
    public static byte[] copyOfPar(byte[] original, int newLength, int numProcs) {
        bytesGlobal = new byte[newLength];
        int numMin, numMax;
        int max = Math.min(original.length, newLength);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, max);
        	threads[i] = new Thread (new CopyOf1 (numMin, numMax, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        return bytesGlobal;
    }
    public static char[] copyOfPar(char[] original, int newLength, int numProcs) {
        charsGlobal = new char[newLength];
        int numMin, numMax;
        int max = Math.min(original.length, newLength);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, max);
        	threads[i] = new Thread (new CopyOf2 (numMin, numMax, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        return charsGlobal;
    }
    public static double[] copyOfPar(double[] original, int newLength, int numProcs) {
        doublesGlobal = new double[newLength];
        int numMin, numMax;
        int max = Math.min(original.length, newLength);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, max);
        	threads[i] = new Thread (new CopyOf3 (numMin, numMax, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        return doublesGlobal;
    }
    public static float[] copyOfPar(float[] original, int newLength, int numProcs) {
        floatsGlobal = new float[newLength];
        int numMin, numMax;
        int max = Math.min(original.length, newLength);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, max);
        	threads[i] = new Thread (new CopyOf4 (numMin, numMax, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        return floatsGlobal;
    }
    public static int[] copyOfPar(int[] original, int newLength, int numProcs) {
        intsGlobal = new int[newLength];
        int numMin, numMax;
        int max = Math.min(original.length, newLength);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, max);
        	threads[i] = new Thread (new CopyOf5 (numMin, numMax, original));
        	threads[i].start();
        }
        for (int i=0; i<numProcs; i++)	{
        	try {
    			threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        }
        return intsGlobal;
    }
    public static long[] copyOfPar(long[] original, int newLength, int numProcs) {
        longsGlobal = new long[newLength];
        int numMin, numMax;
        int max = Math.min(original.length, newLength);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, max);
        	threads[i] = new Thread (new CopyOf6 (numMin, numMax, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        return longsGlobal;
    }
    public static Object[] copyOfPar(Object[] original, int newLength, int numProcs) {
        objectsGlobal = new Object[newLength];
        int numMin, numMax;
        int max = Math.min(original.length, newLength);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, max);
        	threads[i] = new Thread (new CopyOf7 (numMin, numMax, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        return objectsGlobal;
    }
    public static short[] copyOfPar(short[] original, int newLength, int numProcs) {
        shortsGlobal = new short[newLength];
        int numMin, numMax;
        int max = Math.min(original.length, newLength);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, 0);
    		numMax = limiteMax (i, numElemProc, 0, numProcs, max);
        	threads[i] = new Thread (new CopyOf8 (numMin, numMax, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        return shortsGlobal;
    }
    
    public static <T,U> T[] copyOf(U[] original, int newLength, Class<? extends T[]> newType) {
        @SuppressWarnings("unchecked")
        T[] copy = ((Object)newType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }
    

    /**
     * Copies the specified array, truncating or padding with zeros (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain <tt>(byte)0</tt>.
     * Such indices will exist if and only if the specified length
     * is greater than that of the original array.
     *
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @return a copy of the original array, truncated or padded with zeros
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static byte[] copyOf(byte[] original, int newLength) {
        byte[] copy = new byte[newLength];
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

    /**
     * Copies the specified array, truncating or padding with zeros (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain <tt>(short)0</tt>.
     * Such indices will exist if and only if the specified length
     * is greater than that of the original array.
     *
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @return a copy of the original array, truncated or padded with zeros
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static short[] copyOf(short[] original, int newLength) {
        short[] copy = new short[newLength];
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

    /**
     * Copies the specified array, truncating or padding with zeros (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain <tt>0</tt>.
     * Such indices will exist if and only if the specified length
     * is greater than that of the original array.
     *
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @return a copy of the original array, truncated or padded with zeros
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static int[] copyOf(int[] original, int newLength) {
        int[] copy = new int[newLength];
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

    /**
     * Copies the specified array, truncating or padding with zeros (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain <tt>0L</tt>.
     * Such indices will exist if and only if the specified length
     * is greater than that of the original array.
     *
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @return a copy of the original array, truncated or padded with zeros
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static long[] copyOf(long[] original, int newLength) {
        long[] copy = new long[newLength];
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

    /**
     * Copies the specified array, truncating or padding with null characters (if necessary)
     * so the copy has the specified length.  For all indices that are valid
     * in both the original array and the copy, the two arrays will contain
     * identical values.  For any indices that are valid in the copy but not
     * the original, the copy will contain <tt>'\\u000'</tt>.  Such indices
     * will exist if and only if the specified length is greater than that of
     * the original array.
     *
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @return a copy of the original array, truncated or padded with null characters
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static char[] copyOf(char[] original, int newLength) {
        char[] copy = new char[newLength];
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

    /**
     * Copies the specified array, truncating or padding with zeros (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain <tt>0f</tt>.
     * Such indices will exist if and only if the specified length
     * is greater than that of the original array.
     *
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @return a copy of the original array, truncated or padded with zeros
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static float[] copyOf(float[] original, int newLength) {
        float[] copy = new float[newLength];
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

    /**
     * Copies the specified array, truncating or padding with zeros (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain <tt>0d</tt>.
     * Such indices will exist if and only if the specified length
     * is greater than that of the original array.
     *
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @return a copy of the original array, truncated or padded with zeros
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static double[] copyOf(double[] original, int newLength) {
        double[] copy = new double[newLength];
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

    /**
     * Copies the specified array, truncating or padding with <tt>false</tt> (if necessary)
     * so the copy has the specified length.  For all indices that are
     * valid in both the original array and the copy, the two arrays will
     * contain identical values.  For any indices that are valid in the
     * copy but not the original, the copy will contain <tt>false</tt>.
     * Such indices will exist if and only if the specified length
     * is greater than that of the original array.
     *
     * @param original the array to be copied
     * @param newLength the length of the copy to be returned
     * @return a copy of the original array, truncated or padded with false elements
     *     to obtain the specified length
     * @throws NegativeArraySizeException if <tt>newLength</tt> is negative
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static boolean[] copyOf(boolean[] original, int newLength) {
        boolean[] copy = new boolean[newLength];
        System.arraycopy(original, 0, copy, 0,
                         Math.min(original.length, newLength));
        return copy;
    }

    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>null</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     * <p>
     * The resulting array is of exactly the same class as the original array.
     *
     * @param <T> the class of the objects in the array
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with nulls to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] copyOfRange(T[] original, int from, int to) {
        return copyOfRange(original, from, to, (Class<? extends T[]>) original.getClass());
    }
    
    private static class CopyOfRange1 implements Runnable	{
    	int min, max, offset;
    	byte[] original;
    	CopyOfRange1 (int a, int b, int c, byte[] d)	{
    		min = a; max = b; offset = c; original = d;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, bytesGlobal, min - offset, max-min);
    	}
    }
    private static class CopyOfRange2 implements Runnable	{
    	int min, max, offset;
    	char[] original;
    	CopyOfRange2 (int a, int b, int c, char[] d)	{
    		min = a; max = b; offset = c; original = d;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, charsGlobal, min - offset, max-min);
    	}
    }
    private static class CopyOfRange3 implements Runnable	{
    	int min, max, offset;
    	double[] original;
    	CopyOfRange3 (int a, int b, int c, double[] d)	{
    		min = a; max = b; offset = c; original = d;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, doublesGlobal, min - offset, max-min);
    	}
    }
    private static class CopyOfRange4 implements Runnable	{
    	int min, max, offset;
    	float[] original;
    	CopyOfRange4 (int a, int b, int c, float[] d)	{
    		min = a; max = b; offset = c; original = d;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, floatsGlobal, min - offset, max-min);
    	}
    }
    private static class CopyOfRange5 implements Runnable	{
    	int min, max, offset;
    	int[] original;
    	CopyOfRange5 (int a, int b, int c, int[] d)	{
    		min = a; max = b; offset = c; original = d;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, intsGlobal, min - offset, max-min);
    	}
    }
    private static class CopyOfRange6 implements Runnable	{
    	int min, max, offset;
    	long[] original;
    	CopyOfRange6 (int a, int b, int c, long[] d)	{
    		min = a; max = b; offset = c; original = d;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, longsGlobal, min - offset, max-min);
    	}
    }
    private static class CopyOfRange7 implements Runnable	{
    	int min, max, offset;
    	Object[] original;
    	CopyOfRange7 (int a, int b, int c, Object[] d)	{
    		min = a; max = b; offset = c; original = d;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, objectsGlobal, min - offset, max-min);
    	}
    }
    private static class CopyOfRange8 implements Runnable	{
    	int min, max, offset;
    	short[] original;
    	CopyOfRange8 (int a, int b, int c, short[] d)	{
    		min = a; max = b; offset = c; original = d;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, shortsGlobal, min - offset, max-min);
    	}
    }
    public static byte[] copyOfRangePar (byte[] original, int from, int to, int numProcs) {
    	bytesGlobal = new byte[to-from];
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new CopyOfRange1 (numMin, numMax, from, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
    	return bytesGlobal;
    }
    public static char[] copyOfRangePar (char[] original, int from, int to, int numProcs) {
    	charsGlobal = new char[to-from];
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new CopyOfRange2 (numMin, numMax, from, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
    	return charsGlobal;
    }
    public static double[] copyOfRangePar (double[] original, int from, int to, int numProcs) {
    	doublesGlobal = new double[to-from];
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new CopyOfRange3 (numMin, numMax, from, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
    	return doublesGlobal;
    }
    public static float[] copyOfRangePar (float[] original, int from, int to, int numProcs) {
    	floatsGlobal = new float[to-from];
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new CopyOfRange4 (numMin, numMax, from, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
    	return floatsGlobal;
    }
    public static int[] copyOfRangePar (int[] original, int from, int to, int numProcs) {
    	intsGlobal = new int[to-from];
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new CopyOfRange5 (numMin, numMax, from, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
    	return intsGlobal;
    }
    public static long[] copyOfRangePar (long[] original, int from, int to, int numProcs) {
    	longsGlobal = new long[to-from];
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new CopyOfRange6 (numMin, numMax, from, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
    	return longsGlobal;
    }
    public static Object[] copyOfRangePar (Object[] original, int from, int to, int numProcs) {
    	objectsGlobal = new Object[to-from];
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new CopyOfRange7 (numMin, numMax, from, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
    	return objectsGlobal;
    }
    public static short[] copyOfRangePar (short[] original, int from, int to, int numProcs) {
    	shortsGlobal = new short[to-from];
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new CopyOfRange8 (numMin, numMax, from, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
    	return shortsGlobal;
    }
    
    private static class MoveOfRange1 implements Runnable	{
    	int min, max, inicio, offset;
    	byte[] original;
    	MoveOfRange1 (int a, int b, int c, int d, byte[] e)	{
    		min = a; max = b; offset = c; inicio = d; original = e;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, bytesGlobal, offset+min+inicio, max-min);
    		//System.arraycopy (original, from, dest, offset, to-from);
    	}
    }
    private static class MoveOfRange2 implements Runnable	{
    	int min, max, inicio, offset;
    	char[] original;
    	MoveOfRange2 (int a, int b, int c, int d, char[] e)	{
    		min = a; max = b; offset = c; inicio = d; original = e;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, charsGlobal, offset+min+inicio, max-min);
    		//System.arraycopy (original, from, dest, offset, to-from);
    	}
    }
    private static class MoveOfRange3 implements Runnable	{
    	int min, max, inicio, offset;
    	double[] original;
    	MoveOfRange3 (int a, int b, int c, int d, double[] e)	{
    		min = a; max = b; offset = c; inicio = d; original = e;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, doublesGlobal, offset+min+inicio, max-min);
    		//System.arraycopy (original, from, dest, offset, to-from);
    	}
    }
    private static class MoveOfRange4 implements Runnable	{
    	int min, max, inicio, offset;
    	float[] original;
    	MoveOfRange4 (int a, int b, int c, int d, float[] e)	{
    		min = a; max = b; offset = c; inicio = d; original = e;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, floatsGlobal, offset+min+inicio, max-min);
    		//System.arraycopy (original, from, dest, offset, to-from);
    	}
    }
    private static class MoveOfRange5 implements Runnable	{
    	int min, max, inicio, offset;
    	int[] original;
    	MoveOfRange5 (int a, int b, int c, int d, int[] e)	{
    		min = a; max = b; offset = c; inicio = d; original = e;
    	}
    	public void run ()	{
    		//System.out.println("Mover en original desde " + min + "a destino desde" + (offset+min+inicio) + ", "+ (max-min) + " elementos");
    		//System.out.println("Parámetros de arraycopy: " + min + ", " + (offset+min+inicio) + ", " + (max-min+1));
    		System.arraycopy(original, min, intsGlobal, offset+min+inicio, max-min+1);
    		//System.arraycopy(original, 0, intsGlobal, 1, 10);
    		//System.arraycopy (original, from, dest, offset, to-from);
    		//System.out.println ("Copiando en " + (offset+inicio+max) + " lo que hay en " + (max));
    		//intsGlobal[offset+inicio+max] = original[max];
    	}
    }
    private static class MoveOfRange6 implements Runnable	{
    	int min, max, inicio, offset;
    	long[] original;
    	MoveOfRange6 (int a, int b, int c, int d, long[] e)	{
    		min = a; max = b; offset = c; inicio = d; original = e;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, longsGlobal, offset+min+inicio, max-min);
    		//System.arraycopy (original, from, dest, offset, to-from);
    	}
    }
    private static class MoveOfRange7 implements Runnable	{
    	int min, max, inicio, offset;
    	Object[] original;
    	MoveOfRange7 (int a, int b, int c, int d, Object[] e)	{
    		min = a; max = b; offset = c; inicio = d; original = e;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, objectsGlobal, offset+min+inicio, max-min);
    		//System.arraycopy (original, from, dest, offset, to-from);
    	}
    }
    private static class MoveOfRange8 implements Runnable	{
    	int min, max, inicio, offset;
    	short[] original;
    	MoveOfRange8 (int a, int b, int c, int d, short[] e)	{
    		min = a; max = b; offset = c; inicio = d; original = e;
    	}
    	public void run ()	{
    		System.arraycopy(original, min, shortsGlobal, offset+min+inicio, max-min);
    		//System.arraycopy (original, from, dest, offset, to-from);
    	}
    }
    public static byte[] moveOfRangePar (byte[] original, byte[] dest, int from, int to, int offset, int numProcs) {
    	bytesGlobal = dest;
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		System.out.println ("Desde: " + numMin);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
    		System.out.println ("Hasta: " + numMax);
        	threads[i] = new Thread (new MoveOfRange1 (numMin, numMax, from, offset, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        dest = bytesGlobal;
    	return bytesGlobal;
    }
    public static char[] moveOfRangePar (char[] original, char[] dest, int from, int to, int offset, int numProcs) {
    	charsGlobal = dest;
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new MoveOfRange2 (numMin, numMax, from, offset, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        dest = charsGlobal;
    	return charsGlobal;
    }
    public static double[] moveOfRangePar (double[] original, double[] dest, int from, int to, int offset, int numProcs) {
    	doublesGlobal = dest;
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new MoveOfRange3(numMin, numMax, from, offset, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        dest = doublesGlobal;
    	return doublesGlobal;
    }
    public static float[] moveOfRangePar (float[] original, float[] dest, int from, int to, int offset, int numProcs) {
    	floatsGlobal = dest;
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new MoveOfRange4 (numMin, numMax, from, offset, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        dest = floatsGlobal;
    	return floatsGlobal;
    }
    public static int[] moveOfRangePar2 (int[] original, int[] dest, int from, int to, int offset, int numProcs) {
    	int[] origin;
    	if (original.equals(dest))	{
    		origin = new int[original.length];
    		System.arraycopy (original, 0, origin, 0, original.length);
    		intsGlobal = dest;
    	}
    	else	{
    		origin = original;
    		intsGlobal = dest;
    	}
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new MoveOfRange5 (numMin, numMax-1, from, offset, origin));
        	threads[i].start();
        }
        for (int i=0; i<numProcs; i++)	{
        	try {
    			threads[i].join();
    	} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
        }
        /**System.out.print ("Origen antes de acabar los threads: ");
        for (int i=0; i<10; i++)
        	System.out.print (original[i] + ", ");
        System.out.println();*/
        //System.arraycopy (original, from, intsGlobal, offset, to-from);
        /**System.out.print ("Origen al acabar los threads: ");
        for (int i=0; i<10; i++)
        	System.out.print (original[i] + ", ");
        System.out.println();*/
        /**for (int i=0; i<numProcs; i++)	{
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	intsGlobal[from+offset+numMax-1] = original [numMax-1];
        	//System.out.println ("Se ha movido " + original[numMax-1]);
        	//System.out.println ("Moviendo a " + (from+offset+numMax-1) + " lo que hay en " + (numMax-1));
        }*/
        dest = intsGlobal;
        /**System.out.print ("Destino: ");
        for (int i=0; i<10; i++)
        	System.out.print (dest[i] + ", ");
    	System.out.println();*/
    	return intsGlobal;
    }
    //Copy the original array into the dest array, from the position from to the position to of the original array,
    //starting copy at the offset position in the dest array
    /**Copy the original array into the dest array, from the position from to the position to of the original array, 
     * starting copy at the offset position in the dest array
     * 
     * @param original the array that will be copied
     * @param dest the array where the other array will be copied
     * @param from the first position to copy in the original array
     * @param to the last position to copy in the original array
     * @param offset the first position of the dest array where the original will be copied
     * @param numProcs number of threads that will be created at the same time
     * @return the result of the copy
     */
    public static int[] moveOfRangePar (int[] original, int[] dest, int from, int to, int offset, int numProcs) {
    	int[] origin;
    	if (original.equals(dest))	{
    		origin = ArraysPar.copyOfPar(original, original.length, numProcs);
    		//origin = new int[original.length];
    		//System.arraycopy (original, 0, origin, 0, original.length);
    		intsGlobal = dest;
    	}
    	else	{
    		origin = original;
    		intsGlobal = dest;
    	}
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new MoveOfRange5 (numMin, numMax-1, from, offset, origin));
        	threads[i].start();
        }
        for (int i=0; i<numProcs; i++)	{
        	try {
    			threads[i].join();
    	} catch (InterruptedException e) {
    			e.printStackTrace();
    		}
        }
        dest = intsGlobal;
    	return intsGlobal;
    }
    public static long[] moveOfRangePar (long[] original, long[] dest, int from, int to, int offset, int numProcs) {
    	longsGlobal = dest;
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new MoveOfRange6 (numMin, numMax, from, offset, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        dest = longsGlobal;
    	return longsGlobal;
    }
    public static Object[] moveOfRangePar (Object[] original, Object[] dest, int from, int to, int offset, int numProcs) {
    	objectsGlobal = dest;
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new MoveOfRange7 (numMin, numMax, from, offset, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        dest = objectsGlobal;
    	return objectsGlobal;
    }
    public static short[] moveOfRangePar (short[] original, short[] dest, int from, int to, int offset, int numProcs) {
    	shortsGlobal = dest;
    	int numMin, numMax;
        int max = Math.min(original.length, to-from);
        int numElemProc = max/numProcs;
        threads = new Thread[numProcs];
        for (int i=0; i<numProcs; i++)	{
    		numMin = limiteMin (i, numElemProc, from);
    		numMax = limiteMax (i, numElemProc, from, numProcs, max);
        	threads[i] = new Thread (new MoveOfRange8 (numMin, numMax, from, offset, original));
        	threads[i].start();
        }
        	for (int i=0; i<numProcs; i++)	{
        		try {
    				threads[i].join();
    		} catch (InterruptedException e) {
    				e.printStackTrace();
    		}
        	}
        dest = shortsGlobal;
    	return shortsGlobal;
    }

    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>null</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     * The resulting array is of the class <tt>newType</tt>.
     *
     * @param <U> the class of the objects in the original array
     * @param <T> the class of the objects in the returned array
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @param newType the class of the copy to be returned
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with nulls to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @throws ArrayStoreException if an element copied from
     *     <tt>original</tt> is not of a runtime type that can be stored in
     *     an array of class <tt>newType</tt>.
     * @since 1.6
     */
    public static <T,U> T[] copyOfRange(U[] original, int from, int to, Class<? extends T[]> newType) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        @SuppressWarnings("unchecked")
        T[] copy = ((Object)newType == (Object)Object[].class)
            ? (T[]) new Object[newLength]
            : (T[]) Array.newInstance(newType.getComponentType(), newLength);
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }

    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>(byte)0</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with zeros to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static byte[] copyOfRange(byte[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }

    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>(short)0</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with zeros to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static short[] copyOfRange(short[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        short[] copy = new short[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }

    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>0</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with zeros to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static int[] copyOfRange(int[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        int[] copy = new int[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }

    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>0L</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with zeros to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static long[] copyOfRange(long[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        long[] copy = new long[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }

    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>'\\u000'</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with null characters to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static char[] copyOfRange(char[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        char[] copy = new char[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }

    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>0f</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with zeros to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static float[] copyOfRange(float[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        float[] copy = new float[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }

    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>0d</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with zeros to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static double[] copyOfRange(double[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        double[] copy = new double[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }

    /**
     * Copies the specified range of the specified array into a new array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>false</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     *
     * @param original the array from which a range is to be copied
     * @param from the initial index of the range to be copied, inclusive
     * @param to the final index of the range to be copied, exclusive.
     *     (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     *     truncated or padded with false elements to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *     or {@code from > original.length}
     * @throws IllegalArgumentException if <tt>from &gt; to</tt>
     * @throws NullPointerException if <tt>original</tt> is null
     * @since 1.6
     */
    public static boolean[] copyOfRange(boolean[] original, int from, int to) {
        int newLength = to - from;
        if (newLength < 0)
            throw new IllegalArgumentException(from + " > " + to);
        boolean[] copy = new boolean[newLength];
        System.arraycopy(original, from, copy, 0,
                         Math.min(original.length - from, newLength));
        return copy;
    }

    
}