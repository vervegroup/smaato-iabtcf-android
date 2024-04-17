package com.smaato.iabtcf.test.utils;

import com.smaato.iabtcf.utils.IntIterable;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.Arrays;
import java.util.Set;

public class IntIterableMatcher extends BaseMatcher<IntIterable> {

    private final Matcher<Iterable<? extends Integer>> baseM;

    public static IntIterableMatcher matchInts(Set<Integer> values) {
        return new IntIterableMatcher(values);
    }

    public static IntIterableMatcher matchInts(int... values) {
        return new IntIterableMatcher(values);
    }

    private IntIterableMatcher(Set<Integer> values) {
        baseM = org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder(values.toArray(new Integer[] {}));
    }

    private IntIterableMatcher(int... values) {
        baseM = org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder(Arrays.stream(values).boxed().toArray(Integer[]::new));
    }

    @Override
    public void describeTo(Description description) {
        baseM.describeTo(description);
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        super.describeMismatch(((IntIterable) item).toSet(), description);
    }

    @Override
    public boolean matches(Object item) {
        return baseM.matches(((IntIterable) item).toSet());
    }
}
