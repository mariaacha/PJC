package PJC.memory_tests;

import java.util.*;

public class ALMemoryTestJava {
	private static long currentUsedMemory()	{
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
	
	public static void main(String[] args) {
		long initialMemory = currentUsedMemory();
		ArrayList<Integer> al1 = new ArrayList<Integer>();
		System.out.println ("Used memory after creating an empty ArrayList:" + (initialMemory - currentUsedMemory()));
		for (int i=0; i<10000000; i++)
			al1.add(i);
		System.out.println ("Used memory after adding 10^7 elements to the ArrayList:" + (initialMemory - currentUsedMemory()));
		
	}
}
