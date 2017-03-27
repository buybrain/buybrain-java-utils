package io.buybrain.util;

import lombok.val;
import org.testng.annotations.Test;

import static io.buybrain.util.Result.err;
import static io.buybrain.util.Result.ok;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ResultTest {
    @Test
    public void testMapFunctionOk() throws Throwable {
        val res = ok(41).map(val -> ok(val + 1));
        assertThat(res.isOk(), is(true));
        assertThat(res.get(), is(42));
    }

    @Test
    public void testMapFunctionError() {
        val res = ok(41).map(val -> err(new Exception("meh")));
        assertThat(res.isOk(), is(false));
        assertThat(res.getError().getMessage(), is("meh"));
    }

    @Test(expectedExceptions = Exception.class)
    public void testMapFunctionRethrow() {
        ok(41).map(val -> {
            throw new Exception("meh");
        });
    }

    @Test(expectedExceptions = Exception.class)
    public void testGetOnErrorResult() throws Exception {
        err(new Exception("meh")).get();
    }

    @Test
    public void testAndThenTrySuccess() {
        val res = ok(41).tryMap(val -> val + 1);
        assertThat(res, is(ok(42)));
    }

    @Test
    public void testAndThenTryError() throws Throwable {
        val res = ok(41).tryMap(val -> {
            throw new Exception("meh");
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
        assertThat(ok(42).mapErr(err -> ok(43)), is(ok(42)));
        assertThat(err(new Exception("meh")).mapErr(err -> ok(43)), is(ok(43)));
    }

    @Test(expectedExceptions = Exception.class)
    public void testOrElseSupplierRethrow() {
        err(new Exception("meh")).mapErr(err -> {
            throw err;
        });
    }

    @Test
    public void testOrElseDefaultValue() {
        assertThat(err(new Exception("meh")).orElse(42), is(ok(42)));
    }
}
