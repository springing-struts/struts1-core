package org.apache.struts.upload;

/**
 * MultipartRequestHandler provides a standard interface for struts to deal
 * with file uploads from forms with enctypes of "multipart/form-data".
 * Providers must provide a no-argument constructor for initialization.
 */
public interface MultipartRequestHandler {
  public static final String ATTRIBUTE_MAX_LENGTH_EXCEEDED = "org.apache.struts.upload.MaxLengthExceeded";
}
