package io.github.artsok.tasks;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetTaskAlgorithmQA {

    /**
     * Imagine that you have input arrays like this:
     * [10, 9, 10, 9, 8]
     * [3, 4, 5, 6]
     * [1, 1, 1, 3]
     *
     * and your must to delete unique elements.
     *
     * The result must be:
     * [10, 9, 10, 9, 8] -> [10, 9, 10, 9]
     *
     * What function do you set for this?
     */


    /**
     * This way i use brute-force O(n2)
     * I get first element and compare it with all elements of array except itself.
     * To compare element with each other in this method using two for-loop (Take elements before current element and
     * after it).
     *
     */
    @Test
    void algorithmBruteForce(){
        int[] array = {10, 9, 10, 9, 8};
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < array.length; i++) {
            for (int j = i + 1; j < array.length; j++) {
                if(array[i] == array[j]) {
                    result.add(array[i]);
                    break;
                }
            }
            for (int k = i - 1; k >= 0 ; k--) {
                if (array[i] == array[k]) {
                    result.add(array[i]);
                    break;
                }
            }
        }
        System.out.println(result);
    }


    @Test
    void enhancedAlgorithm() {
        int[] array = {10, 9, 10, 9, 8};
        Map<Integer, List<Integer>> map = new HashMap<>();

        for (int i = 0; i < array.length ; i++) {
            List<Integer> index = new ArrayList<>(1);
            index.add(i);

            map.merge(array[i], index, (list1, list2) -> Stream.of(list1, list2)
                    .flatMap(Collection::stream)
                    .collect(Collectors.toList()));;
        }


        System.out.println(map);

    }
}
