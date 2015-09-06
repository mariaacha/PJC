package PJC.map.hashMap;
public final class HashFunctions {
    public static int hash(float value) {
        assert !Float.isNaN(value) : "Values of NaN are not supported.";

        return Float.floatToIntBits(value*663608941.737f);
    }

    /**
     * Returns a hashcode for the specified value.
     *
     * @return  a hash code value for the specified value.
     */
    public static int hash(int value) {
		return value;
    }

    /**
     * Returns a hashcode for the specified value.
     *
     * @return  a hash code value for the specified value.
     */
    public static int hash(long value) {
        return ((int)(value ^ (value >>> 32)));
    }

    /**
     * Returns a hashcode for the specified object.
     *
     * @return  a hash code value for the specified object.
     */
    public static int hash(Object object) {
        return object==null ? 0 : object.hashCode();
    }


    /**
     * In profiling, it has been found to be faster to have our own local implementation
     * of "ceil" rather than to call to {@link Math#ceil(double)}.
     */
    public static int fastCeil( float v ) {
        int possible_result = ( int ) v;
        if ( v - possible_result > 0 ) possible_result++;
        return possible_result;
    }
}