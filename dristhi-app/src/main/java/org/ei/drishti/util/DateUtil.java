package org.ei.drishti.util;

import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

public class DateUtil {
    private static String DEFAULT_FORMAT_DDMMYYYY = "dd/MM/yyyy";
    private static String DATE_FORMAT_FOR_TIMELINE_EVENT = "dd-MM-yyyy";

    private static DateUtility dateUtility = new RealDate();

    public static void fakeIt(LocalDate fakeDayAsToday) {
        dateUtility = new MockDate(fakeDayAsToday);
    }

    public static LocalDate today() {
        return dateUtility.today();
    }

    public static String formatDateForTimelineEvent(String unformattedDate) {
        return formatDate(unformattedDate, DATE_FORMAT_FOR_TIMELINE_EVENT);
    }

    public static String formatDate(String unformattedDate) {
        return formatDate(unformattedDate, DEFAULT_FORMAT_DDMMYYYY);
    }

    public static String formatDate(String date, String pattern) {
        try {
            return LocalDate.parse(date).toString(pattern);
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatDate(LocalDate date, String pattern) {
        try {
            return date.toString(pattern);
        } catch (Exception e) {
            return "";
        }
    }

    public static String formatFromISOString(String date, String pattern) {
        try {
            return getLocalDate(date).toString(pattern);
        } catch (Exception e) {
            return "";
        }
    }

    public static LocalDate getLocalDate(String date) {
        try {
            return LocalDateTime.parse(date).toLocalDate();
        } catch (Exception e) {
            return null;
        }
    }

    public static int dayDifference(LocalDate startDate, LocalDate endDate) {
        try {
            return Math.abs(Days.daysBetween(startDate, endDate).getDays());
        } catch (Exception e) {
            return 0;
        }
    }

}

interface DateUtility {
    LocalDate today();
}

class RealDate implements DateUtility {
    @Override
    public LocalDate today() {
        return LocalDate.now();
    }
}

class MockDate implements DateUtility {
    private LocalDate fakeDay;

    MockDate(LocalDate fakeDay) {
        this.fakeDay = fakeDay;
    }

    @Override
    public LocalDate today() {
        return fakeDay;
    }
}
