package io.buybrain.util.time;

import lombok.NonNull;
import lombok.val;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class PeriodsTest {
    @Test
    public void testMergePeriods() {
        val result = Periods.merge(asList(
            period("2017-01-01", "2017-01-02"),
            period("2017-01-03", "2017-01-04"),
            period("2017-01-04", "2017-01-05"),
            period("2017-01-10", "2017-01-12"),
            period("2017-01-09", "2017-01-11"),
            period("2017-01-15", "2017-01-17"),
            period("2017-01-14", "2017-01-18"),
            period("2017-01-15", "2017-01-17")
        ));

        val expected = asList(
            period("2017-01-01", "2017-01-02"),
            period("2017-01-03", "2017-01-05"),
            period("2017-01-09", "2017-01-12"),
            period("2017-01-14", "2017-01-18")
        );

        assertThat(result, is(expected));
    }

    @Test
    public void testSubtractSingle() {
        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-01-01", "2017-01-02")),
            is(asList(period("2017-02-01", "2017-03-01")))
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-01-01", "2017-02-01")),
            is(asList(period("2017-02-01", "2017-03-01")))
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-01-01", "2017-02-15")),
            is(asList(period("2017-02-15", "2017-03-01")))
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-01-01", "2017-03-01")),
            is(asList())
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-01-01", "2017-03-15")),
            is(asList())
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-02-01", "2017-02-15")),
            is(asList(period("2017-02-15", "2017-03-01")))
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-02-01", "2017-03-01")),
            is(asList())
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-02-01", "2017-03-15")),
            is(asList())
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-02-15", "2017-02-16")),
            is(asList(period("2017-02-01", "2017-02-15"), period("2017-02-16", "2017-03-01")))
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-02-15", "2017-03-01")),
            is(asList(period("2017-02-01", "2017-02-15")))
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-02-15", "2017-03-15")),
            is(asList(period("2017-02-01", "2017-02-15")))
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-03-01", "2017-03-15")),
            is(asList(period("2017-02-01", "2017-03-01")))
        );

        assertThat(
            Periods.subtract(period("2017-02-01", "2017-03-01"), period("2017-03-15", "2017-03-16")),
            is(asList(period("2017-02-01", "2017-03-01")))
        );
    }

    @Test
    public void testIntersectSingle() {
        assertThat(
            Periods.intersect(period("2017-01-01", "2017-02-01"), period("2017-01-15", "2017-02-15")),
            is(Optional.of(period("2017-01-15", "2017-02-01")))
        );

        assertThat(
            Periods.intersect(period("2017-01-01", "2017-02-01"), period("2017-02-01", "2017-02-15")),
            is(Optional.empty())
        );
    }

    @Test
    public void testIntersectMultiple() {
        val first = asList(
            period("2017-01-01", "2017-02-01"),
            period("2017-02-15", "2017-02-16"),
            period("2017-02-20", "2017-02-25")
        );

        val second = asList(
            period("2017-01-02", "2017-01-10"),
            period("2017-01-20", "2017-02-25")
        );

        val expected = asList(
            period("2017-01-02", "2017-01-10"),
            period("2017-01-20", "2017-02-01"),
            period("2017-02-15", "2017-02-16"),
            period("2017-02-20", "2017-02-25")
        );

        val result = Periods.intersect(first, second);

        assertThat(result, is(expected));
    }

    private static Period period(@NonNull String from, String to) {
        return new Period(utc(from), to == null ? null : utc(to));
    }

    private static ZonedDateTime utc(@NonNull String format) {
        try {
            return utc(LocalDateTime.parse(format));
        } catch (DateTimeParseException ex) {
            return utc(LocalDate.parse(format).atStartOfDay());
        }
    }

    private static ZonedDateTime utc(@NonNull LocalDateTime local) {
        return local.atZone(ZoneId.of("UTC"));
    }
}
