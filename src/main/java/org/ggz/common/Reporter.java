package org.ggz.common;

import java.util.function.Consumer;

/**
 * Reports progress
 */
public class Reporter {

  public static final Consumer<String> CONSOLE = str -> {
    synchronized (System.out) {
      System.out.println(str);
      System.out.flush();
    }
  };

  public static String RESET = "\u001b[0m";
  public static String RED = "\u001b[31m";
  public static String GREEN = "\u001b[32m";
  public static String BLUE = "\u001b[34m";
  public static String GRAY = "\u001b[30;1m";

  private final Consumer<String> destination;             // Where the info eventually goes
  private final String prefix;                            // place before info
  private final boolean allowColorCodes;                  // if false, they are stripped

  /**
   * Create a reporter
   *
   * @param destination where to send the formatted information
   */
  public Reporter(Consumer<String> destination) {
    this(destination, null, null);
  }

  /**
   * Create a reporter
   *
   * @param destination where to send the formatted information
   * @param prefix if non-null, prefixed to each message with a colon
   */
  public Reporter(Consumer<String> destination, String prefix) {
    this(destination, prefix, null);
  }

  /**
   * Create a reporter
   *
   * @param destination where to send the formatted information
   * @param prefix if non-null, prefixed to each message with a colon
   */
  public Reporter(Consumer<String> destination, String prefix, Boolean allowColorCodes) {
    this.destination = destination;
    if (allowColorCodes == null) {
      this.allowColorCodes = destination == CONSOLE && !System.getProperty("os.name").startsWith("Windows");
    } else {
      this.allowColorCodes = allowColorCodes;
    }
    this.prefix = this.allowColorCodes ? prefix : stripANSIEscapeCodes(prefix);
  }

  public Reporter format(String format, Object... values) {
    return print(String.format(format, values));
  }

  public Reporter print(String info) {
    if (!allowColorCodes) {
      info = stripANSIEscapeCodes(info);
    }
    if (prefix != null) {
      destination.accept(prefix + ": " + info);
    } else {
      destination.accept(info);
    }
    return this;
  }

  private String stripANSIEscapeCodes(String info) {
    return info == null ? null : info.replaceAll("\u001b\\[[0-9;]+m", "");
  }
}
