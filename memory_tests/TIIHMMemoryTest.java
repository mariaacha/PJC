package PJC.memory_tests;

import PJC.map.hashMap.*;

public class TIIHMMemoryTest {
	private static long currentUsedMemory()	{
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
	
	public static void main(String[] args) {
		long initialMemory = currentUsedMemory();
		TIntIntHashMap iihm1 = new TIntIntHashMap();
		System.out.println ("Used memory after creating an empty TIntIntHashMap:" + (initialMemory - currentUsedMemory()));
		for (int i=0; i<1000000; i++)
			iihm1.put(i*10,i);
		System.out.println ("Used memory after adding 10^7 elements to the TIntIntHashMap:" + (initialMemory - currentUsedMemory()));
	}
}
