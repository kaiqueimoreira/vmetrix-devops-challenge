package com.vmetrix.misc;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class StringUtilsTest {

    @Test void reverse_normal()      { assertEquals("olleh", StringUtils.reverse("hello")); }
    @Test void reverse_null()        { assertEquals("", StringUtils.reverse(null)); }
    @Test void palindrome_true()     { assertTrue(StringUtils.isPalindrome("A man a plan a canal Panama")); }
    @Test void palindrome_false()    { assertFalse(StringUtils.isPalindrome("hello")); }
    @Test void wordCount_basic()     { assertEquals(3, StringUtils.wordCount("one two three")); }
    @Test void wordCount_blank()     { assertEquals(0, StringUtils.wordCount("   ")); }
    @Test void titleCase()           { assertEquals("Hello World", StringUtils.toTitleCase("hello world")); }
    @Test void truncate_short()      { assertEquals("hi", StringUtils.truncate("hi", 10)); }
    @Test void truncate_long()       { assertEquals("hel...", StringUtils.truncate("hello world", 3)); }
    @Test void join_list()           { assertEquals("a, b, c", StringUtils.join(List.of("a","b","c"))); }
    @Test void join_empty()          { assertEquals("", StringUtils.join(List.of())); }
}
