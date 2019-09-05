package org.ggz.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ReporterTest {

  @Test
  void test_print() {
    StringBuilder builder = new StringBuilder();
    Reporter reporter = new Reporter(builder::append);
    reporter.print("hello ").print("world");
    assertEquals("hello world", builder.toString());
  }

  @Test
  void test_format() {
    StringBuilder builder = new StringBuilder();
    Reporter reporter = new Reporter(builder::append);
    reporter.print("hello ").format("%3d worlds each scoring %1.7f", 5, 11.1);
    assertEquals("hello   5 worlds each scoring 11.1000000", builder.toString());
  }

  @Test
  void test_print_with_prefix() {
    StringBuilder builder = new StringBuilder();
    Reporter reporter = new Reporter(builder::append, "TEST");
    reporter.print("hello || ").print("world");
    assertEquals("TEST: hello || TEST: world", builder.toString());
  }

  @Test
  void test_print_with_escapes_enabled() {
    StringBuilder builder = new StringBuilder();
    Reporter reporter = new Reporter(builder::append, Reporter.BLUE + "TEST" + Reporter.RESET,
        true);
    reporter.print(Reporter.GREEN + "hello world" + Reporter.RESET);
    assertEquals("\u001B[34mTEST\u001B[0m: \u001B[32mhello world\u001B[0m", builder.toString());
  }

  @Test
  void test_print_with_default_escapes() {
    StringBuilder builder = new StringBuilder();
    Reporter reporter = new Reporter(builder::append, Reporter.BLUE + "TEST" + Reporter.RESET);
    reporter.print(Reporter.GREEN + "hello world" + Reporter.RESET);
    assertEquals("TEST: hello world", builder.toString());
  }

  @Test
  void test_console_print_with_escapes() {
    // Not a real test -- you have to check your console to see if it's correct
    Reporter reporter = new Reporter(Reporter.CONSOLE,
        Reporter.RED + "I should be red " + Reporter.RESET);
    reporter.print(Reporter.GREEN + "and I should be green" + Reporter.RESET);
  }


}
