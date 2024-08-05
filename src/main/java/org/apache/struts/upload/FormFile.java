package org.apache.struts.upload;

import java.io.IOException;
import java.io.InputStream;

/**
 * This interface represents a file that has been uploaded by a client. It is
 * the only interface or class in upload package which is typically referenced
 * directly by a Struts application.
 */
public interface FormFile {

  /**
   * Returns the content type for this file.
   */
  String getContentType();

  /**
   * Returns the size of this file in bytes.
   */
  long getFileLength();


  /**
   * Returns the size of this file, in bytes.
   * An IllegalStateException will be thrown if size is greater than 2GB.
   */
  int getFileSize() throws IllegalStateException;

  /**
   * Returns the file name of this file. This is the base name of the file, as
   * supplied by the user when the file was uploaded.
   */
  String getFileName();

  /**
   * Returns the data for the entire file as byte array. Care is needed when
   * using this method, since a large upload could easily exhaust available
   * memory. The preferred method for accessing the file data is
   * `getInputStream()`.
   */
  byte[] getFileData() throws IOException;

  /**
   * Returns an input stream for this file. The caller must close the stream
   * when it is no longer needed.
   */
  InputStream getInputStream() throws IOException;

  /**
   * Destroys all content for the uploaded file, including any underlying data
   * files.
   */
  void destroy();
}
