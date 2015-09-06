package PJC.memory_tests;

import PJC.list.arrayList.*;

public class TIALMemoryTest {
	private static long currentUsedMemory()	{
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
	
	public static void main(String[] args) {
		long initialMemory = currentUsedMemory();
		TIntArrayList ial1 = new TIntArrayList();
		System.out.println ("Used memory after creating an empty TIntArrayList:" + (initialMemory - currentUsedMemory()));
		for (int i=0; i<10000000; i++)
			ial1.add(i);
		System.out.println ("Used memory after adding 10^7 elements to the TIntArrayList:" + (initialMemory - currentUsedMemory()));
		
	}
}
