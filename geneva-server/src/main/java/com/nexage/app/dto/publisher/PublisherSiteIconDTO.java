package com.nexage.app.dto.publisher;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PublisherSiteIconDTO {

  private String extension;
  private byte[] image;
  private String path;

  public PublisherSiteIconDTO() {}

  private PublisherSiteIconDTO(Builder builder) {
    extension = builder.extension;
    image = builder.image;
    path = builder.path;
  }

  public String getExtension() {
    return extension;
  }

  public String getPath() {
    return path;
  }

  public byte[] getImage() {
    return image;
  }

  public void setExtension(String extension) {
    this.extension = extension;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public static Builder newBuilder() {
    return new Builder();
  }

  public static final class Builder {
    private String extension;
    private byte[] image;
    private String path;

    public Builder withExtension(String extension) {
      this.extension = extension;
      return this;
    }

    public Builder withPath(String path) {
      this.path = path;
      return this;
    }

    public Builder withImage(byte[] image) {
      this.image = image;
      return this;
    }

    public PublisherSiteIconDTO build() {
      return new PublisherSiteIconDTO(this);
    }
  }
}
