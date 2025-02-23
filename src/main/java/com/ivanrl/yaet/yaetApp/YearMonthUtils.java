package com.ivanrl.yaet.yaetApp;

import java.time.YearMonth;

public class YearMonthUtils {

    public static YearMonth getPrevious(int year, int month) {
        return YearMonth.of(year, month).minusMonths(1);
    }
}
