package com.nexage.app.services.impl;

import com.nexage.app.error.ServerErrorCodes;
import com.nexage.app.services.FileSystemService;
import com.ssp.geneva.common.error.exception.GenevaAppRuntimeException;
import java.io.File;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FileUtils;
import org.springframework.security.access.prepost.PreAuthorize;

@Log4j2
@PreAuthorize(
    "@loginUserContext.isOcUserNexage() or @loginUserContext.isOcUserSeller() "
        + "or @loginUserContext.isOcUserBuyer() or @loginUserContext.isOcUserSeatHolder()")
public class PhysicalFilesystemService implements FileSystemService {

  @Override
  public void write(String dir, String fileName, byte[] data) {
    boolean writeSucceeded = false;
    int retryCount = 0;
    while (!writeSucceeded && retryCount < 3) {
      try {
        File file = new File(dir + fileName);
        FileUtils.writeByteArrayToFile(file, data);
        if (log.isDebugEnabled()) {
          log.debug("Data written to disk at [" + file.getAbsolutePath() + "]");
        }
        writeSucceeded = true;
      } catch (Exception e) {
        log.info("Failed to save data on first try at [ {} ] ... retrying", fileName, e);
      }
      retryCount++;
    }
    if (!writeSucceeded) {
      log.info("Failed to save data 3 times at [ {} ]", fileName);
      throw new GenevaAppRuntimeException(ServerErrorCodes.SERVER_FILE_SYSTEM_WRITE_ERROR);
    }
  }

  @Override
  public byte[] read(String fileName) {
    throw new UnsupportedOperationException();
  }
}
