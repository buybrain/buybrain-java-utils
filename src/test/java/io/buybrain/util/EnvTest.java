package io.buybrain.util;

import org.testng.annotations.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class EnvTest {
    private final Env SUT = new Env(new HashMap<String, String>() {{
        put("STR", "hello");
        put("NUM", "123");
    }});

    @Test
    public void testGetString() {
        assertThat(SUT.getString("STR"), is("hello"));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetStringNotExists() {
        SUT.getString("NOPE");
    }

    @Test
    public void testGetStringWithDefault() {
        assertThat(SUT.getString("NOPE", "bar"), is("bar"));
    }

    @Test
    public void testGetInt() {
        assertThat(SUT.getInt("NUM"), is(123));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testGetIntNotExists() {
        SUT.getInt("NOPE");
    }

    @Test
    public void testGetIntWithDefault() {
        assertThat(SUT.getInt("NOPE", 1234), is(1234));
    }

    @Test(expectedExceptions = NumberFormatException.class)
    public void testGetIntInvalidValue() {
        SUT.getInt("STR");
    }
}
