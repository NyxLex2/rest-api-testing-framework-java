package com.automation.testing.utils;

import java.util.concurrent.atomic.AtomicLong;

import com.automation.testing.enums.GenderEnum;
import com.automation.testing.enums.RoleEnum;
import com.github.javafaker.Faker;

public class TestDataGenerator {
    private static final Faker faker = new Faker();
    private static final AtomicLong counter = new AtomicLong(System.currentTimeMillis());

    public static final int MIN_VALID_AGE = 16;
    public static final int MAX_VALID_AGE = 60;

    public static String getUniqueLogin() {
        return faker.name().username() + "_" + System.currentTimeMillis();
    }

    public static String getUniqueScreenName() {
        return faker.pokemon().name() + faker.lorem().word() + "_" + System.currentTimeMillis();
    }

    public static String getValidPassword() {
        return faker.internet().password(8, 16, true, true, true);
    }

    public static String getInvalidPassword() {
        return faker.internet().password(1, 7); // Too short
    }

    public static int getRandomAge(int min, int max) {
        return faker.number().numberBetween(min, max);
    }

    public static String getRandomGender() {
        return faker.options().option(GenderEnum.MALE, GenderEnum.FEMALE).getValue();
    }

    public static String getRandomRole() {
        return faker.options().option(RoleEnum.USER, RoleEnum.ADMIN).getValue();
    }

    public static int getPlayerId() {
        return faker.number().numberBetween(1, 100000);
    }

    // Legacy method names for backward compatibility
    public static String generateUniqueLogin() {
        return getUniqueLogin();
    }

    public static String generateUniqueScreenName() {
        return getUniqueScreenName();
    }

    public static String generateValidPassword() {
        return getValidPassword();
    }

    public static Integer generateValidAge() {
        return getRandomAge(MIN_VALID_AGE, MAX_VALID_AGE);
    }

    public static GenderEnum generateRandomGender() {
        return faker.options().option(GenderEnum.MALE, GenderEnum.FEMALE);
    }

    public static RoleEnum generateRandomRole() {
        return faker.options().option(RoleEnum.USER, RoleEnum.ADMIN);
    }

    public static String generateInvalidPasswordTooShort() {
        return faker.regexify("[a-zA-Z0-9]{3,7}");
    }

    public static String generateInvalidPasswordTooLong() {
        return faker.regexify("[a-zA-Z0-9]{16,25}");
    }

    public static String generateInvalidPasswordNoDigits() {
        return faker.regexify("[a-zA-Z]{8,12}");
    }

    public static String generateInvalidPasswordNoUppercase() {
        return faker.regexify("[a-z0-9]{8,12}");
    }

    public static Integer generateInvalidAgeTooYoung() {
        return faker.number().numberBetween(1, 15);
    }

    public static Integer generateInvalidAgeTooOld() {
        return faker.number().numberBetween(61, 120);
    }

    public static String generateInvalidGender() {
        String[] invalidGenders = {"unknown", "other", "non-binary", "invalid", "123", ""};
        return invalidGenders[faker.number().numberBetween(0, invalidGenders.length)];
    }

    public static String generateInvalidRole() {
        String[] invalidRoles = {"manager", "guest", "moderator", "invalid", "123", ""};
        return invalidRoles[faker.number().numberBetween(0, invalidRoles.length)];
    }

    public static Long generateNonExistentPlayerId() {
        return faker.number().numberBetween(999999L, 9999999L);
    }

    public static Long generateInvalidPlayerId() {
        return faker.number().numberBetween(-1000L, 1L);
    }
}
