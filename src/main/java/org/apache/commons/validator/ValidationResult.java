package org.apache.commons.validator;

import org.springframework.lang.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * This contains the results of a set of validation rules processed on a
 * JavaBean.
 */
public class ValidationResult {

  public ValidationResult(
    Field field
  ) {
    this.field = field;
    results = new HashMap<>();
  }
  private final Map<String, ResultStatus> results;

  /**
   * Add the result of a validator action.
   */
  public void add(String validatorName, boolean result) {
    add(validatorName, result, null);
  }

  /**
   * Add the result of a validator action.
   */
  public void add(String validatorName, boolean result, @Nullable Object value) {
    results.put(validatorName, new ResultStatus(result, value));
  }

  public void merge(ValidationResult anotherResult) {
    results.putAll(anotherResult.results);
  }

  /**
   * Field being validated.
   */
  public Field getField() {
    return field;
  }
  private final Field field;

  /**
   * Contains the status of the validation.
   */
  public static class ResultStatus {
    public ResultStatus(
      boolean valid,
      @Nullable Object result
    ) {
      this.valid = valid;
      this.result = result;
    }

    /**
     * Whether the validation passed.
     */
    public boolean isValid() {
      return valid;
    }
    private final boolean valid;

    /**
     * The result returned by a validation method.
     */
    public @Nullable Object getResult() {
      return result;
    }
    private final @Nullable Object result;
  }
}
