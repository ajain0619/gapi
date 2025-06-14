package com.nexage.admin.core.sparta.jpa.model;

import java.io.Serializable;

public class AdSourceLogoFileReference implements Serializable {
  private static final long serialVersionUID = 1L;

  private byte[] data;
  private String extension;

  public byte[] getData() {
    return data;
  }

  public void setData(byte[] data) {
    this.data = data;
  }

  public String getExtension() {
    return extension;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }
}
