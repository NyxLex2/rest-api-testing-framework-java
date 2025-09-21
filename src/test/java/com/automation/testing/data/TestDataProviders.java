package com.automation.testing.data;

import org.testng.annotations.DataProvider;

import com.automation.testing.enums.GenderEnum;
import com.automation.testing.enums.RoleEnum;

public class TestDataProviders {

    @DataProvider(name = "validRoles")
    public static Object[][] validRolesProvider() {
        return new Object[][]{
                {RoleEnum.USER},
                {RoleEnum.ADMIN}
        };
    }

    @DataProvider(name = "validGenders")
    public static Object[][] validGendersProvider() {
        return new Object[][]{
                {GenderEnum.MALE},
                {GenderEnum.FEMALE}
        };
    }

    @DataProvider(name = "boundaryAges")
    public static Object[][] boundaryAgesProvider() {
        return new Object[][]{
                {17}, {18}, {59}, {60}
        };
    }

    @DataProvider(name = "invalidPlayerIds")
    public static Object[][] invalidPlayerIdsProvider() {
        return new Object[][]{
                {-1L}, {0L}, {999999999L}
        };
    }

    @DataProvider(name = "invalidPasswords")
    public static Object[][] invalidPasswordsProvider() {
        return new Object[][]{
                {""}, {"short"}, {"nouppercase123"}, {"NOLOWERCASE123"}, {"NoDigitsHere"}
        };
    }

    @DataProvider(name = "invalidGenders")
    public static Object[][] invalidGendersProvider() {
        return new Object[][]{
                {"unknown"}, {"other"}, {"invalid"}, {""}, {"123"}
        };
    }

    @DataProvider(name = "invalidRoles")
    public static Object[][] invalidRolesProvider() {
        return new Object[][]{
                {"manager"}, {"guest"}, {"moderator"}, {"invalid"}, {""}, {"123"}
        };
    }

    @DataProvider(name = "invalidAges")
    public static Object[][] invalidAgesProvider() {
        return new Object[][]{
                {-1}, {0}, {15}, {61}, {200}
        };
    }
}
