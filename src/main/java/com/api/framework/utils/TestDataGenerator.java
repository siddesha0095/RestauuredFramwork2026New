package com.api.framework.utils;

import com.github.javafaker.Faker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * TestDataGenerator - Generates realistic random test data using Java Faker.
 * All methods are static for easy access from test classes.
 */
public class TestDataGenerator {

    private static final Logger log = LogManager.getLogger(TestDataGenerator.class);
    private static final Faker faker = new Faker(Locale.ENGLISH);
    private static final Random random = new Random();

    private TestDataGenerator() {}

    // ==================== USER DATA ====================

    public static String getRandomEmail() {
        String email = faker.internet().emailAddress();
        log.debug("Generated email: {}", email);
        return email;
    }

    public static String getRandomPassword() {
        return faker.internet().password(8, 16, true, true, true);
    }

    public static String getRandomFirstName() {
        return faker.name().firstName();
    }

    public static String getRandomLastName() {
        return faker.name().lastName();
    }

    public static String getRandomFullName() {
        return faker.name().fullName();
    }

    public static String getRandomUsername() {
        return faker.name().username();
    }

    public static String getRandomPhoneNumber() {
        return faker.phoneNumber().phoneNumber();
    }

    public static String getRandomJobTitle() {
        return faker.job().title();
    }

    // ==================== ADDRESS DATA ====================

    public static String getRandomCity() {
        return faker.address().city();
    }

    public static String getRandomCountry() {
        return faker.address().country();
    }

    public static String getRandomZipCode() {
        return faker.address().zipCode();
    }

    // ==================== POST / CONTENT DATA ====================

    public static String getRandomTitle() {
        return faker.book().title();
    }

    public static String getRandomBody() {
        return faker.lorem().paragraph(3);
    }

    public static String getRandomSentence() {
        return faker.lorem().sentence();
    }

    // ==================== NUMERIC DATA ====================

    public static int getRandomPositiveInt(int max) {
        return random.nextInt(max) + 1;
    }

    public static int getRandomUserId() {
        return getRandomPositiveInt(12); // ReqRes has users 1-12
    }

    public static int getRandomPostId() {
        return getRandomPositiveInt(100); // JSONPlaceholder has posts 1-100
    }

    // ==================== COMPOSITE DATA MAPS ====================

    public static Map<String, Object> getRandomUserPayload() {
        Map<String, Object> user = new HashMap<>();
        user.put("name", getRandomFullName());
        user.put("job", getRandomJobTitle());
        return user;
    }

    public static Map<String, Object> getRandomPostPayload() {
        Map<String, Object> post = new HashMap<>();
        post.put("title", getRandomTitle());
        post.put("body", getRandomBody());
        post.put("userId", getRandomUserId());
        return post;
    }

    public static Map<String, String> getLoginPayload(String email, String password) {
        Map<String, String> creds = new HashMap<>();
        creds.put("email", email);
        creds.put("password", password);
        return creds;
    }

    // Known valid ReqRes credentials
    public static Map<String, String> getValidReqResLoginPayload() {
        return getLoginPayload("eve.holt@reqres.in", "cityslicka");
    }

    public static Map<String, String> getValidReqResRegisterPayload() {
        return getLoginPayload("eve.holt@reqres.in", "pistol");
    }

    public static Map<String, String> getInvalidLoginPayload() {
        return getLoginPayload(getRandomEmail(), getRandomPassword());
    }

    // ==================== SPECIAL CHARS / EDGE CASES ====================

    public static String getStringWithSpecialChars() {
        return "Test!@#$%^&*()_+-=[]{}|;':\",./<>?";
    }

    public static String getVeryLongString(int length) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length) {
            sb.append(faker.lorem().word()).append(" ");
        }
        return sb.toString().trim().substring(0, Math.min(length, sb.length()));
    }

    public static String getEmptyString() {
        return "";
    }

    public static String getNullAsString() {
        return "null";
    }
}
