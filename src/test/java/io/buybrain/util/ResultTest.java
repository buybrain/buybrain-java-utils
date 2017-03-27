package io.buybrain.util;

import lombok.val;
import org.testng.annotations.Test;

import static io.buybrain.util.Result.err;
import static io.buybrain.util.Result.ok;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ResultTest {
    @Test
    public void testAndThenFunctionOk() throws Throwable {
        val res = ok(41).andThen(val -> ok(val + 1));
        assertThat(res.isOk(), is(true));
        assertThat(res.get(), is(42));
    }

    @Test
    public void testAndThenFunctionError() {
        val res = ok(41).andThen(val -> err(new Exception("meh")));
        assertThat(res.isOk(), is(false));
        assertThat(res.getError().getMessage(), is("meh"));
    }

    @Test(expectedExceptions = Exception.class)
    public void testAndThenFunctionRethrow() {
        ok(41).andThen(val -> {
            if (val > 0) {
                throw new Exception("meh");
            }
            return ok(42);
        });
    }

    @Test(expectedExceptions = Exception.class)
    public void testGetOnErrorResult() throws Exception {
        err(new Exception("meh")).get();
    }

    @Test
    public void testAndThenTrySuccess() {
        val res = ok(41).andThenTry(val -> val + 1);
        assertThat(res, is(ok(42)));
    }

    @Test
    public void testAndThenTryError() throws Throwable {
        val res = ok(41).andThenTry(val -> {
            if (val > 0) {
                throw new Exception("meh");
            }
            return 42;
        });
        assertThat(res.isOk(), is(false));
    }

    @Test
    public void testAndThenSupplierOk() {
        val res = ok().andThen(() -> ok(42));
        assertThat(res, is(ok(42)));
    }

    @Test
    public void testAndThenSupplierError() {
        val res = ok().andThen(() -> err(new Exception("meh")));
        assertThat(res.getError().getMessage(), is("meh"));
    }

    @Test(expectedExceptions = Exception.class)
    public void testAndThenSupplierRethrow() {
        ok().andThen(() -> {
            throw new Exception("meh");
        });
    }

    @Test
    public void testOrElseFunctionOk() {
        assertThat(ok(42).orElse(err -> ok(43)), is(ok(42)));
        assertThat(err(new Exception("meh")).orElse(err -> ok(43)), is(ok(43)));
    }

    @Test(expectedExceptions = Exception.class)
    public void testOrElseSupplierRethrow() {
        err(new Exception("meh")).orElse(err -> {
            throw err;
        });
    }

    @Test
    public void testOrElseDefaultValue() {
        assertThat(err(new Exception("meh")).orElse(42), is(ok(42)));
    }
}
