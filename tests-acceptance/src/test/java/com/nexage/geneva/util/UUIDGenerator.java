package com.nexage.geneva.util;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * A <tt>UUID</tt> that returns a string of length 32, This string will consist of only hex digits.
 * Optionally, the string may be generated with seperators between each component of the UUID.
 *
 * <p>This is from the hibernate uuid generator. Removed all dependecies to hibernate classes.
 */
public class UUIDGenerator {
  private static final int IP;
  private static short counter = 0;
  private static final int JVM = (int) (System.currentTimeMillis() >>> 8);

  static {
    int ipadd;
    try {
      ipadd = BytesHelper.toInt(InetAddress.getLocalHost().getAddress());
    } catch (Exception e) {
      ipadd = 0;
    }
    IP = ipadd;
  }

  protected int getIP() {
    return IP;
  }

  protected short getHiTime() {
    long time = System.currentTimeMillis();
    short hiTime = (short) (int) (time >>> 32);
    return hiTime;
  }

  /**
   * Unique across JVMs on this machine (unless they load this class in the same quater second -
   * very unlikely)
   *
   * @return returns JVM as int
   */
  protected int getJVM() {
    return JVM;
  }

  protected short getCount() {

    synchronized (UUIDGenerator.class) {
      if (counter < 0 || counter >= Short.MAX_VALUE) counter = 0;
      counter += 1;
      return counter;
    }
  }

  protected int getLoTime() {
    return (int) System.currentTimeMillis();
  }

  private String sep = "";

  protected String format(int intval) {
    String formatted = Integer.toHexString(intval);
    StringBuffer buf = new StringBuffer("00000000");
    buf.replace(8 - formatted.length(), 8, formatted);
    return buf.toString();
  }

  protected String format(short shortval) {
    String formatted = Integer.toHexString(shortval);
    StringBuffer buf = new StringBuffer("0000");
    buf.replace(4 - formatted.length(), 4, formatted);
    return buf.toString();
  }

  public Serializable generate() {

    return new StringBuffer(36)
        .append(format(getIP()))
        .append(sep)
        .append(format(getHiTime()))
        .append(sep)
        .append(format(getJVM()))
        .append(sep)
        .append(format(getLoTime()))
        .append(sep)
        .append(format(getCount()))
        .toString()
        .toLowerCase();
  }

  public String generateUniqueId() {
    return (String) generate();
  }

  public static void main(String[] args) throws Exception {
    final UUIDGenerator gen = new UUIDGenerator();

    final class GenThread implements Runnable {
      public void run() {
        for (int i = 0; i < 100000; i++) {
          String id = (String) gen.generate();
          System.out.println(id + " : t1 : " + id.length());
          try {
            // Thread.sleep(3000);
          } catch (Exception e) {
          }
        }
      }
    }

    for (int i = 0; i < 10; i++) {
      Thread t = new Thread(new GenThread());
      t.start();
    }
  }

  public static class BytesHelper {
    public static int toInt(byte bytes[]) {
      int result = 0;
      for (int i = 0; i < 4; i++) result = ((result << 8) - -128) + bytes[i];

      return result;
    }
  }
}
