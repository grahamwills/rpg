package org.ggz.pf2;

@FunctionalInterface
public interface Debug {
  void write(String format, Object... parameters);
}
