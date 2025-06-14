package com.nexage.app.dto;

import java.io.Serializable;

public class CreativeFileReferenceDTO implements Serializable {

  private static final long serialVersionUID = 1L;

  public enum CreativeSizeType {
    CUSTOM(-1, -1),
    MMA_120x20(120, 20),
    MMA_168x28(168, 28),
    MMA_216x36(216, 36),
    MMA_300x50(300, 50),
    MMA_320x50(320, 50),
    SMARTPHONE(320, 50),
    TABLET_BANNER(728, 90),
    MEDIUM_RECTANGLE(300, 250);

    private final int width;

    private final int height;

    CreativeSizeType(int width, int height) {
      this.width = width;
      this.height = height;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }
  }

  private byte[] image;

  private String extension;

  private CreativeSizeType creativeSizeType;

  public CreativeFileReferenceDTO() {}

  public CreativeFileReferenceDTO(
      byte[] image, String extension, CreativeSizeType creativeSizeType) {
    this.image = image;
    this.extension = extension;
    this.creativeSizeType = creativeSizeType;
  }

  /** @return the data */
  public byte[] getImage() {
    return image;
  }

  /** @param image the data to set */
  public void setImage(byte[] image) {
    this.image = image;
  }

  /** @return the extension */
  public String getExtension() {
    return extension;
  }

  /** @param extension the extension to set */
  public void setExtension(String extension) {
    this.extension = extension;
  }

  /** @return the sizeType */
  public CreativeSizeType getCreativeSizeType() {
    return creativeSizeType;
  }

  /** @param creativeSizeType the sizeType to set */
  public void setCreativeSizeType(CreativeSizeType creativeSizeType) {
    this.creativeSizeType = creativeSizeType;
  }
}
