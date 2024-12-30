package org.apache.struts.upload;

import javax.servlet.http.HttpServletRequest;

/**
 * MultipartRequestHandler provides a standard interface for struts to deal
 * with file uploads from forms with enctypes of "multipart/form-data".
 * Providers must provide a no-argument constructor for initialization.
 */
public interface MultipartRequestHandler {

  /**
   * This is the ServletRequest attribute that should be set when a multipart
   * request is being read and the maximum length is exceeded. The value is a
   * Boolean. If the maximum length isn't exceeded, this attribute shouldn't be
   * put in the ServletRequest. It's the job of the implementation to put this
   * attribute in the request if the maximum length is exceeded; in the
   * `handleRequest(HttpServletRequest)` method.
   */
  String ATTRIBUTE_MAX_LENGTH_EXCEEDED = "org.apache.struts.upload.MaxLengthExceeded";

  /**
   * After constructed, this is the first method called on by ActionServlet.
   * Use this method for all your data-parsing of the ServletInputStream in the
   * request.
   */
  void handleRequest(HttpServletRequest request);
}
