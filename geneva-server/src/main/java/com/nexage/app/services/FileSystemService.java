package com.nexage.app.services;

public interface FileSystemService {
  void write(String dir, String fileName, byte[] data);

  byte[] read(String fileName);
}
