package com.madfree.capstoneproject;

public class Trivia {

    private String question;
    private String answer;
    private String wrong_answer_1;
    private String wrong_answer_2;
    private String wrong_answer_3;
    private String image_url;
    private String category;
    private String difficulty;

    public Trivia(String question, String answer, String wrong_answer_1, String wrong_answer_2,
                  String wrong_answer_3, String image_url, String category, String difficulty) {
        this.question = question;
        this.answer = answer;
        this.wrong_answer_1 = wrong_answer_1;
        this.wrong_answer_2 = wrong_answer_2;
        this.wrong_answer_3 = wrong_answer_3;
        this.image_url = image_url;
        this.category = category;
        this.difficulty = difficulty;
    }

    public Trivia(String question, String answer, String wrong_answer_1, String wrong_answer_2,
                  String wrong_answer_3, String category, String difficulty) {
        this.question = question;
        this.answer = answer;
        this.wrong_answer_1 = wrong_answer_1;
        this.wrong_answer_2 = wrong_answer_2;
        this.wrong_answer_3 = wrong_answer_3;
        this.category = category;
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getWrong_answer_1() {
        return wrong_answer_1;
    }

    public void setWrong_answer_1(String wrong_answer_1) {
        this.wrong_answer_1 = wrong_answer_1;
    }

    public String getWrong_answer_2() {
        return wrong_answer_2;
    }

    public void setWrong_answer_2(String wrong_answer_2) {
        this.wrong_answer_2 = wrong_answer_2;
    }

    public String getWrong_answer_3() {
        return wrong_answer_3;
    }

    public void setWrong_answer_3(String wrong_answer_3) {
        this.wrong_answer_3 = wrong_answer_3;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }
}
