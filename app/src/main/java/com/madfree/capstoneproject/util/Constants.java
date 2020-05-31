package com.madfree.capstoneproject.util;

public class Constants {

    // Auth
    public static final int RC_SIGN_IN = 1;
    public static final String ANONYMOUS = "anonymous";

    // Trivia image
    public static final int RC_PHOTO_PICKER = 2;
    public static final String KEY_IMAGE_URI = "image_uri";
    public static final String KEY_FIREBASE_IMAGE_URL = "firebase_image_url";

    // Trivia text data
    public static final String KEY_QUESTION_STRING = "question_string";
    public static final String KEY_CORRECT_ANSWER_STRING = "correct_answer_string";
    public static final String KEY_WRONG_ANSWER_1_STRING = "wrong_answer_1_string";
    public static final String KEY_WRONG_ANSWER_2_STRING = "wrong_answer_2_string";
    public static final String KEY_WRONG_ANSWER_3_STRING = "wrong_answer_3_string";
    public static final String KEY_CATEGORY_STRING = "category_string";
    public static final String KEY_DIFFICULTY_STRING = "difficulty_string";

    public static final int DEFAULT_QUESTION_LENGTH = 300;
    public static final int DEFAULT_ANSWER_LENGTH = 30;

    // Quiz
    public static final long COUNTDOWN_IN_MILLIS = 10000;
    public static final int  QUIZ_MAX_SIZE = 10;
    public static final String KEY_MILLIS_LEFT = "key_millis_left";
}
