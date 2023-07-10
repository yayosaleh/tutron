package com.example.tutron;

import org.junit.Test;
import static org.junit.Assert.*;

public class StringCapitalizerUnitTest {
    @Test
    public void singleWordString_isCorrect() {
        String input, expected;
        input = "hello";
        expected = "Hello";
        assertEquals(expected, TopicManagerActivity.capitalizeFirstLetterOfEachWord(input));
    }

    @Test
    public void multiWordString_isCorrect() {
        String input, expected;
        input = "hello world!";
        expected = "Hello World!";
        assertEquals(expected, TopicManagerActivity.capitalizeFirstLetterOfEachWord(input));
    }

    @Test
    public void emptyString_isCorrect() {
        String input, expected;
        input = "";
        expected = "";
        assertEquals(expected, TopicManagerActivity.capitalizeFirstLetterOfEachWord(input));
    }

    @Test
    public void nullString_isCorrect() {
        String input, expected;
        input = null;
        expected = null;
        assertEquals(expected, TopicManagerActivity.capitalizeFirstLetterOfEachWord(input));
    }
}
