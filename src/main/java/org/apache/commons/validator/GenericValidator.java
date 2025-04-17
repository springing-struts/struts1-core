package org.apache.commons.validator;

import org.springframework.lang.Nullable;

/**
 * This class contains basic methods for performing validations.
 */
public class GenericValidator {

  /**
   * Checks if the field isn't null and length of the field is greater than
   * zero not including whitespace.
   */
  public static boolean isBlankOrNull(@Nullable String str) {
    return str == null || str.isBlank();
  }
}
