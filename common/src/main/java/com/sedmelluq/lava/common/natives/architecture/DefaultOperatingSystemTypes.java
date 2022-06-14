package com.sedmelluq.lava.common.natives.architecture;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public enum DefaultOperatingSystemTypes implements OperatingSystemType {
  LINUX("linux", "lib", ".so"),
  LINUX_MUSL("linux-musl", "lib", ".so"),
  WINDOWS("win", "", ".dll"),
  DARWIN("darwin", "lib", ".dylib"),
  SOLARIS("solaris", "lib", ".so");

  private final String identifier;
  private final String libraryFilePrefix;
  private final String libraryFileSuffix;

  DefaultOperatingSystemTypes(String identifier, String libraryFilePrefix, String libraryFileSuffix) {
    this.identifier = identifier;
    this.libraryFilePrefix = libraryFilePrefix;
    this.libraryFileSuffix = libraryFileSuffix;
  }

  @Override
  public String identifier() {
    return identifier;
  }

  @Override
  public String libraryFilePrefix() {
    return libraryFilePrefix;
  }

  @Override
  public String libraryFileSuffix() {
    return libraryFileSuffix;
  }

  private static boolean isMusl() throws IOException {
    Process proc = Runtime.getRuntime().exec("ldd --version");

    BufferedReader std = new BufferedReader(new InputStreamReader(proc.getInputStream()));

    String s;
    boolean isMusl = false;
    while ((s = std.readLine()) != null) {
      if (s.contains("musl") || s.contains("ld-musl")) isMusl = true;
    }

    return isMusl;
  }

  public static OperatingSystemType detect() {
    String osFullName = System.getProperty("os.name");

    if (osFullName.startsWith("Windows")) {
      return WINDOWS;
    } else if (osFullName.startsWith("Mac OS X")) {
      return DARWIN;
    } else if (osFullName.startsWith("Solaris")) {
      return SOLARIS;
    } else if (osFullName.toLowerCase().startsWith("linux")) {
      try {
        if (isMusl()) {
          return LINUX_MUSL;
        } else {
          return LINUX;
        }
      } catch (IOException e) {
        return LINUX;
      }
    } else {
      throw new IllegalArgumentException("Unknown operating system: " + osFullName);
    }
  }
}
