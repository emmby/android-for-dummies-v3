package com.dummies.tasks.tv;

import java.util.Calendar;

public final class MovieList {
    public static final Object[] MOVIE_CATEGORY[] = {
            new Object[]{ "Today", new int[]{
                    Calendar.HOUR_OF_DAY,
                    Calendar.MINUTE,
                    Calendar.SECOND
                }
            },
            new Object[]{"This Week", new int[]{
                    Calendar.DAY_OF_WEEK,
                    Calendar.HOUR_OF_DAY,
                    Calendar.MINUTE,
                    Calendar.SECOND
                }
            },
            new Object[]{"This Month", new int[]{
                    Calendar.DAY_OF_MONTH,
                    Calendar.HOUR_OF_DAY,
                    Calendar.MINUTE,
                    Calendar.SECOND
                }
            },
            new Object[]{ "This Year",new int[]{
                    Calendar.DAY_OF_YEAR,
                    Calendar.DAY_OF_MONTH,
                    Calendar.HOUR_OF_DAY,
                    Calendar.MINUTE,
                    Calendar.SECOND
                }
            },
        new Object[]{ "All",new int[]{
                Calendar.YEAR,
                Calendar.DAY_OF_YEAR,
                Calendar.HOUR_OF_DAY,
                Calendar.MINUTE,
                Calendar.SECOND
            }
        }
    };
}
