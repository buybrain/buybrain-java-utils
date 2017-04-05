package io.buybrain.util.time;

import lombok.NonNull;
import lombok.val;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

/**
 * Operations that can be applied to {@link Period} instances
 */
public class Periods {
    /**
     * Merge a list of periods such that periods that are overlapping or adjacent are collapsed into a single period
     *
     * @param periods the periods to merge
     * @return the list of merged periods, sorted ascending
     */
    public static List<Period> merge(@NonNull List<Period> periods) {
        // Sort periods by start date
        periods.sort(comparing(Period::getFrom));

        val result = new ArrayList<Period>();

        ZonedDateTime curStart = null;
        ZonedDateTime curEnd = null;

        for (Period period : periods) {
            if (curStart == null) {
                // New period
                curStart = period.getFrom();
                curEnd = period.getTo();
            } else {
                // Try to merge the next period; we can do this if it start no later than the current merged period ends
                if (!period.getFrom().isAfter(curEnd)) {
                    // Can be merged. Extend the end date if the current item has a later end date
                    if (period.getTo().isAfter(curEnd)) {
                        curEnd = period.getTo();
                    }
                } else {
                    // Could not merge. Export the current merged period and start a new one
                    result.add(new Period(curStart, curEnd));
                    curStart = period.getFrom();
                    curEnd = period.getTo();
                }
            }
        }

        if (curStart != null) {
            // The last element was merged, export it.
            result.add(new Period(curStart, curEnd));
        }

        return result;
    }

    /**
     * Remove one period from a list of periods according to {@link #subtract(Period, Period)}
     *
     * @param subjects the periods to subtract from
     * @param toSubtract the period to subtract
     * @return the results of the subtraction
     */
    public static List<Period> subtract(@NonNull List<Period> subjects, @NonNull Period toSubtract) {
        return subjects.stream()
            .flatMap(p -> subtract(p, toSubtract).stream())
            .collect(toList());
    }

    /**
     * Remove one period from another period. This operation can yield 0, 1 or 2 resulting periods. If the subject is
     * fully covered by the subtracted period, there is no period left. If part of the inside of the subject is
     * subtracted (but not the start and end), it will be cut into 2 periods. Otherwise, one period is returned.
     *
     * @param subject the period to subtract from
     * @param toSubtract the period to subtract
     * @return the result of the subtraction
     */
    public static List<Period> subtract(@NonNull Period subject, @NonNull Period toSubtract) {
        val result = new ArrayList<Period>();
        // If the subtraction start is after the subject start, the first part of the subject will yield a result
        if (toSubtract.getFrom().isAfter(subject.getFrom())) {
            result.add(new Period(subject.getFrom(), min(toSubtract.getFrom(), subject.getTo())));
        }

        // If the subtraction end is before the subject end, the last part of the subject will yield a result
        if (toSubtract.getTo().isBefore(subject.getTo())) {
            result.add(new Period(max(toSubtract.getTo(), subject.getFrom()), subject.getTo()));
        }

        return result;
    }

    /**
     * Get the intersection (overlapping period) of two periods
     *
     * @param a first period
     * @param b second period
     * @return Optional with the overlapping period or empty if the periods do not overlap
     */
    public static Optional<Period> intersect(@NonNull Period a, @NonNull Period b) {
        val start = max(a.getFrom(), b.getFrom());
        val end = min(a.getTo(), b.getTo());
        if (end == null || end.isAfter(start)) {
            return Optional.of(new Period(start, end));
        }
        return Optional.empty();
    }

    private static ZonedDateTime min(ZonedDateTime a, ZonedDateTime b) {
        return a.isBefore(b) ? a : b;
    }

    private static ZonedDateTime max(ZonedDateTime a, ZonedDateTime b) {
        return a.isBefore(b) ? b : a;
    }
}
