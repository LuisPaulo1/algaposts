package com.algaposts.text_processor.domain.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleWordCountStrategyTest {

    private SimpleWordCountStrategy simpleWordCountStrategy;

    @BeforeEach
    void setUp() {
        simpleWordCountStrategy = new SimpleWordCountStrategy();
    }

    @Test
    void shouldCountWordsInSimpleText() {

        String text = "Hello world";

        int result = simpleWordCountStrategy.countWords(text);

        assertEquals(2, result);
    }

    @Test
    void shouldReturnZeroForNullText() {

        String text = null;

        int result = simpleWordCountStrategy.countWords(text);

        assertEquals(0, result);
    }

    @Test
    void shouldReturnZeroForEmptyText() {

        String text = "";

        int result = simpleWordCountStrategy.countWords(text);

        assertEquals(0, result);
    }

    @Test
    void shouldReturnZeroForWhitespaceOnlyText() {

        String text = "   \t\n  ";

        int result = simpleWordCountStrategy.countWords(text);

        assertEquals(0, result);
    }

    @Test
    void shouldCountWordsWithMultipleSpaces() {

        String text = "Hello    world    test";

        int result = simpleWordCountStrategy.countWords(text);

        assertEquals(3, result);
    }

    @Test
    void shouldCountWordsWithLeadingAndTrailingSpaces() {

        String text = "  Hello world  ";

        int result = simpleWordCountStrategy.countWords(text);

        assertEquals(2, result);
    }

    @Test
    void shouldCountWordsWithNewlinesAndTabs() {

        String text = "Hello\nworld\ttest";

        int result = simpleWordCountStrategy.countWords(text);

        assertEquals(3, result);
    }

    @Test
    void shouldCountSingleWord() {

        String text = "Hello";

        int result = simpleWordCountStrategy.countWords(text);

        assertEquals(1, result);
    }

    @Test
    void shouldCountWordsInLongText() {

        String text = "Lorem ipsum dolor sit amet consectetur adipiscing elit sed do eiusmod tempor incididunt";

        int result = simpleWordCountStrategy.countWords(text);

        assertEquals(13, result);
    }

    @Test
    void shouldCountWordsWithPunctuation() {

        String text = "Hello, world! How are you?";

        int result = simpleWordCountStrategy.countWords(text);

        assertEquals(5, result);
    }
}
