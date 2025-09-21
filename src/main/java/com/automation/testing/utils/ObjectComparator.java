package com.automation.testing.utils;

import java.lang.reflect.Field;
import java.util.Objects;

public class ObjectComparator {

    public static void assertEquals(Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        }

        if (expected == null || actual == null) {
            throw new AssertionError("Object comparison failed: " +
                    "expected=" + expected + ", actual=" + actual);
        }

        if (!expected.getClass().equals(actual.getClass())) {
            throw new AssertionError("Object type mismatch: " +
                    "expected=" + expected.getClass().getSimpleName() +
                    ", actual=" + actual.getClass().getSimpleName());
        }

        Field[] fields = expected.getClass().getDeclaredFields();
        StringBuilder mismatches = new StringBuilder();
        int mismatchCount = 0;

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object expectedValue = field.get(expected);
                Object actualValue = field.get(actual);

                if (!Objects.equals(expectedValue, actualValue)) {
                    if (mismatchCount > 0) {
                        mismatches.append(", ");
                    }
                    mismatches.append(field.getName()).append("(expected=").append(expectedValue)
                            .append(", actual=").append(actualValue).append(")");
                    mismatchCount++;
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access field: " + field.getName(), e);
            }
        }

        if (mismatchCount > 0) {
            StringBuilder errorMessage = new StringBuilder();
            errorMessage.append("Object comparison failed with ").append(mismatchCount)
                    .append(" field mismatch(es):\n");

            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object expectedValue = field.get(expected);
                    Object actualValue = field.get(actual);

                    if (!Objects.equals(expectedValue, actualValue)) {
                        errorMessage.append("  ").append(field.getName())
                                .append(": expected=").append(expectedValue)
                                .append(", actual=").append(actualValue).append("\n");
                    }
                } catch (IllegalAccessException e) {
                    // Already handled above
                }
            }

            throw new AssertionError(errorMessage.toString());
        }
    }

    public static void assertEqualsIgnoreNull(Object expected, Object actual) {
        if (expected == null && actual == null) {
            return;
        }

        if (expected == null || actual == null) {
            throw new AssertionError("Object comparison failed: " +
                    "expected=" + expected + ", actual=" + actual);
        }

        if (!expected.getClass().equals(actual.getClass())) {
            throw new AssertionError("Object type mismatch: " +
                    "expected=" + expected.getClass().getSimpleName() +
                    ", actual=" + actual.getClass().getSimpleName());
        }

        Field[] fields = expected.getClass().getDeclaredFields();
        StringBuilder mismatches = new StringBuilder();
        StringBuilder ignoredNulls = new StringBuilder();
        int mismatchCount = 0;
        int ignoredCount = 0;

        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object expectedValue = field.get(expected);
                Object actualValue = field.get(actual);

                if (actualValue == null && expectedValue != null) {
                    if (ignoredCount > 0) {
                        ignoredNulls.append(", ");
                    }
                    ignoredNulls.append(field.getName()).append("(expected=").append(expectedValue)
                            .append(", actual=null - ignored)");
                    ignoredCount++;
                    continue;
                }

                if (!Objects.equals(expectedValue, actualValue)) {
                    if (mismatchCount > 0) {
                        mismatches.append(", ");
                    }
                    mismatches.append(field.getName()).append("(expected=").append(expectedValue)
                            .append(", actual=").append(actualValue).append(")");
                    mismatchCount++;
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException("Cannot access field: " + field.getName(), e);
            }
        }

        if (mismatchCount > 0 || ignoredCount > 0) {
            StringBuilder errorMessage = new StringBuilder();

            if (mismatchCount > 0) {
                errorMessage.append("Object comparison failed with ").append(mismatchCount)
                        .append(" field mismatch(es):\n");

                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        Object expectedValue = field.get(expected);
                        Object actualValue = field.get(actual);

                        if (actualValue != null && !Objects.equals(expectedValue, actualValue)) {
                            errorMessage.append("  ").append(field.getName())
                                    .append(": expected=").append(expectedValue)
                                    .append(", actual=").append(actualValue).append("\n");
                        }
                    } catch (IllegalAccessException e) {
                        // Already handled above
                    }
                }
            }

            if (ignoredCount > 0) {
                if (mismatchCount > 0) {
                    errorMessage.append("\n");
                }
                errorMessage.append("Ignored ").append(ignoredCount).append(" null field(s):\n");

                for (Field field : fields) {
                    field.setAccessible(true);
                    try {
                        Object expectedValue = field.get(expected);
                        Object actualValue = field.get(actual);

                        if (actualValue == null && expectedValue != null) {
                            errorMessage.append("  ").append(field.getName())
                                    .append(": expected=").append(expectedValue)
                                    .append(", actual=null (ignored)\n");
                        }
                    } catch (IllegalAccessException e) {
                        // Already handled above
                    }
                }
            }

            if (mismatchCount > 0) {
                throw new AssertionError(errorMessage.toString());
            }
        }
    }

}
