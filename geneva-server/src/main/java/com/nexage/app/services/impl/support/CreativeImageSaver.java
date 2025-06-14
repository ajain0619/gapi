package com.nexage.app.services.impl.support;

import com.nexage.app.services.FileSystemService;
import java.io.File;
import org.apache.commons.lang3.RandomStringUtils;

/**
 * @author Nick Ilkevich
 * @since 16.10.2014
 */
public class CreativeImageSaver {

  /** Relative path builder */
  private StringBuilder stringBuilder;

  private final String prefix;
  private FileSystemService fileSaveService;

  public CreativeImageSaver(String prefix, FileSystemService fileSaveService) {
    this.stringBuilder = new StringBuilder();
    this.prefix = prefix;
    this.fileSaveService = fileSaveService;
  }

  public CreativeImageSaver addRandomNumber(int length) {
    stringBuilder.append(RandomStringUtils.randomNumeric(length));
    return this;
  }

  public CreativeImageSaver add(String s) {
    stringBuilder.append(s);
    return this;
  }

  public CreativeImageSaver addExtension(String extension) {
    stringBuilder.append(".");
    stringBuilder.append(extension);
    return this;
  }

  public CreativeImageSaver add(long s) {
    stringBuilder.append(s);
    return this;
  }

  public CreativeImageSaver insertSeparator() {
    stringBuilder.append(File.separator);
    return this;
  }

  public String getRelativePath() {
    return File.separator + stringBuilder.toString();
  }

  public String getRelativePathAsUrl() {
    return getRelativePath().replace('\\', '/');
  }

  public void write(byte[] data) {
    fileSaveService.write(prefix, stringBuilder.toString(), data);
  }
}
