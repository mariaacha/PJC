package PJC.memory_tests;

import java.util.*;

public class HMMemoryTestJava {
	private static long currentUsedMemory()	{
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
	
	public static void main(String[] args) {
		long initialMemory = currentUsedMemory();
		HashMap<Integer,Integer> hm1 = new HashMap<Integer,Integer>();
		System.out.println ("Used memory after creating an empty HashMap:" + (initialMemory - currentUsedMemory()));
		for (int i=0; i<1000000; i++)
			hm1.put(i*10,i);
		System.out.println ("Used memory after adding 10^7 elements to the HashMap:" + (initialMemory - currentUsedMemory()));
		
	}
}
