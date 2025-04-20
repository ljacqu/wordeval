package ch.jalu.wordeval.util;

import org.slf4j.Logger;

/**
 * Logs messages with the elapsed time in seconds since the last message.
 */
public class TimeLogger {

  private final Logger logger;
  private final long startTime;
  private long lastTime;

  /**
   * Constructor.
   *
   * @param logger the logger to log with
   */
  public TimeLogger(Logger logger) {
    this.logger = logger;
    this.startTime = this.lastTime = System.currentTimeMillis();
  }

  /**
   * Logs the given message, prepended with the elapsed time in seconds since the last message.
   *
   * @param logMessage the message to log
   */
  public void lap(String logMessage) {
    double diff = (System.currentTimeMillis() - lastTime) / 1000.0;
    logger.info("{}  {}", diff, logMessage);
    lastTime = System.currentTimeMillis();
  }

  /**
   * Logs the given message with the elapsed time in seconds since this object has been created.
   *
   * @param logMessage the message to log
   */
  public void logWithOverallTime(String logMessage) {
    double diff = (System.currentTimeMillis() - startTime) / 1000.0;
    logger.info("{} {}", diff, logMessage);
  }
}
