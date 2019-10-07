package io.github.artsok.tasks;

import org.junit.jupiter.api.Test;

class LeetCode {

    @Test
    void testSumVariant1() {
        int array[] = {-10, 2, 1, 14, 7, 15};
        int target = 16;

        loop: for (int i = 0; i < array.length; i++) {
            if(array[i] > target) {
                continue;
            }
            int a = target - array[i];
            for (int k = i; k < array.length; k++) {
                 if(a == array[k]) {
                     System.out.println(array[i] + " " + array[k]);
                     break loop;
                 }
                System.out.println("false " + k);
            }
        }
    }


    //TODO: сделать поиск через бинарный рекурсивный поиск
    @Test
    void testSumVariant2() {
        int array[] = {-10, 1, 2, 7, 11, 15};
        int target = 16;

        loop: for (int i = 0; i < array.length; i++) {
            if(array[i] > target) {
                continue;
            }
            int a = target - array[i];
            System.out.println(a);
            //int number = binarySearch(array, array[i], array[array.length - 1], a);

            //System.out.println(number);


//            for (int k = i; k < array.length; k++) {
//                if(a == array[k]) {
//                    System.out.println(array[i] + " " + array[k]);
//                    break loop;
//                }
//                System.out.println("false " + k);
//            }
        }
    }



    private static int binarySearch( Comparable [ ] a, Comparable x,

                                     int low, int high )

    {

        if( low > high )

            return -1;



        int mid = ( low + high ) / 2;



        if( a[ mid ].compareTo( x ) < 0 )

            return binarySearch( a, x, mid + 1, high );

        else if( a[ mid ].compareTo( x ) > 0 )

            return binarySearch( a, x, low, mid - 1 );

        else

            return mid;

    }
}
