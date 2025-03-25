package org.apache.commons.validator;

import java.util.HashMap;
import java.util.Map;

/**
 * This contains the results of a set of validation rules processed on a
 * JavaBean.
 */
public class ValidationResults {

  public ValidationResults() {
    results = new HashMap<>();
  }

  private final Map<String, ValidationResult> results;

  public void merge(ValidationResults anotherResults) {
    anotherResults.results.forEach((key, anotherResult) -> {
      var existingResult = results.get(key);
      if (existingResult == null) {
        results.put(key, anotherResult);
      } else {
        existingResult.merge(anotherResult);
      }
    });
  }
}
