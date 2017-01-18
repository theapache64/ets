package com.theah64.ets.api;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by theapache64 on 18/1/17.
 */
public class Lab {
    public static void main(String[] args) {

        //Set<String> names = new HashSet<>();
        final Queue<String> names = new LinkedList<>();
        names.add("A");
        names.add("B");
        names.add("C");
        names.add("D");

        for (final String name : names) {
            System.out.println(names.poll());
        }

    }
}
