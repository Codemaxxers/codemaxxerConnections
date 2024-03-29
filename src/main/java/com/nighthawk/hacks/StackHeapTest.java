package com.nighthawk.hacks;

public class StackHeapTest {
    public int n = 5; // primitive data type on heap

    public static void changeInt(int nValue, int nRefN, StackHeapTest nRef) {
        // before
        System.out.println();
        System.out.println(" --- Before Changing the Values (in the function) ---");
        System.out.println("This is nValue: " + nValue);
        System.out.println("This is nRefN: " + nRefN);
        System.out.println("This is nRef.n: " + nRef.n);

        nValue += 10;
        nRefN += 10;
        nRef.n += 10;

        // after
        System.out.println();
        System.out.println(" --- After Changing the Values (in the function) ---");
        System.out.println("This is nValue after change: " + nValue);
        System.out.println("This is nRefN after change: " + nRefN);
        System.out.println("This is nRef.n after change: " + nRef.n);

        
    }

    public static void main(String[] args) {
        int n = 5; // primitive data type on stack

        // practice stack heap test
        StackHeapTest nRef = new StackHeapTest();
        System.out.println();
        System.out.println();

        System.out.println("----- Stack Heap Test Start------");
        
        System.out.println();
        System.out.println("--- Before ChangeInt ---");

        // print each of the parts of the hashmap
        System.out.println("Value of n on stack: " + n);
        System.out.println("before n: " + n + System.identityHashCode(n));
        System.out.println();
        int nValue = nRef.n;
        System.out.println("Value of nRef.n (heap) copied to nValue: " + nValue);
        System.out.println("before nRef: " + nRef + System.identityHashCode(nRef));
        System.out.println();
        int nRefN = nRef.n;
        System.out.println("Value of nRef.n (heap) copied to nRefN: " + nRefN);
        System.out.println("before nRef.n: " + nRef.n + System.identityHashCode(nRef.n));

        changeInt(nValue, nRefN, nRef);

  // stack by value, heap by value, heap by reference
  // after
        System.out.println();
        System.out.println("--- Address of nRef ---");
        System.out.println("Address of nRef in method: " + (System.identityHashCode(nRef)));

        // Print each part of the hashmap after changeInt
        System.out.println();
        System.out.println("--- After ChangeInt ---");
        System.out.println("Value of n on stack: " + n);
        System.out.println("after n: " + n + System.identityHashCode(n));
        System.out.println();
        System.out.println("Value of nRef.n (heap) copied to nValue: " + nValue);
        System.out.println("after nRef: " + nRef + System.identityHashCode(nRef));
        System.out.println();
        System.out.println("Value of nRef.n (heap) copied to nRefN: " + nRefN);
        System.out.println("after nRef.n: " + nRef.n + System.identityHashCode(nRef.n));
        System.out.println();
        System.out.println("Updated value of nRef.n (heap): " + nRef.n);


        System.out.println();
        System.out.println();

        System.out.println("----- Stack Heap Test End------");
        System.out.println();
        System.out.println();


    }
}
