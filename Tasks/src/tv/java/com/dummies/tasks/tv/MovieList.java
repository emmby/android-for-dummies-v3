package com.dummies.tasks.tv;

import java.util.Calendar;

public final class MovieList {
    public static final Object[] MOVIE_CATEGORY[] = {
            new Object[]{ "Today", Calendar.HOUR_OF_DAY },
            new Object[]{ "This Week", Calendar.DAY_OF_WEEK},
            new Object[]{ "This Month", Calendar.DAY_OF_MONTH },
            new Object[]{ "This Year", Calendar.DAY_OF_YEAR },
    };
}
