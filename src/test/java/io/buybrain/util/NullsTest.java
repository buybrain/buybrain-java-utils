package io.buybrain.util;

import org.testng.annotations.Test;

import static io.buybrain.util.Nulls.coalesce;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class NullsTest {
    @Test
    public void testCoalescce() {
        Double result = coalesce(null, null, 1.0, null, 2.0);
        assertThat(result, is(1.0));

        result = coalesce(asList(null, null, 1.0, null, 2.0));
        assertThat(result, is(1.0));
    }
}
