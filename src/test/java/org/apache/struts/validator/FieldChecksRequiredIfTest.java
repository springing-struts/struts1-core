package org.apache.struts.validator;

import org.apache.commons.validator.*;
import org.apache.struts.action.ActionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import javax.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.struts.validator.FieldChecks.validateRequiredIf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FieldChecksRequiredIfTest {

  @Mock Field mockField;
  @Mock ValidatorAction mockValidatorAction;
  @Mock ActionMessages mockErrors;
  @Mock Validator mockValidator;
  @Mock HttpServletRequest mockRequest;
  @Mock Form mockForm;
  @Mock Object mockBean;

  void prepareFixtures(Map<String, Object> fieldVars, Map<String, Object> formFieldValues) {
    Map<String, Var> vars = fieldVars.entrySet().stream().collect(Collectors.toMap(
      Map.Entry::getKey,
      it -> new Var(it.getKey(), Objects.toString(it.getValue(), ""), null, null)
    ));
    when(mockField.getVars()).thenReturn(vars);
    when(mockField.getForm()).thenReturn(mockForm);
    vars.forEach((k, v) -> {
      when(mockField.getVarValue(k)).thenReturn(v.getValue());
      when(mockField.getVarValue(eq(k), any())).thenReturn(v.getValue());
      when(mockField.getRequiredVarValue(k)).thenReturn(v.getValue());
      when(mockField.getRequiredVarValue(eq(k), any())).thenReturn(v.getValue());
    });
    when(mockField.getValueOf(mockBean)).thenReturn(null);
    formFieldValues.forEach((name, value) -> {
      var field = mock(Field.class);
      when(field.getValueOf(mockBean)).thenReturn(value);
      when(mockForm.getFieldByName(name)).thenReturn(field);
    });
    mockErrors = mock(ActionMessages.class);
  }

  void shouldPass() {
    assertThat(
      validateRequiredIf(mockBean, mockValidatorAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isTrue();
    verify(mockErrors, never()).addValidationError(any(), any());
  }

  void shouldReject() {
    assertThat(
      validateRequiredIf(mockBean, mockValidatorAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isFalse();
    verify(mockErrors).addValidationError(mockField, mockValidatorAction);
  }

  @Test void itDoValidationWhenAllConditionsAreMetInTheAndJoinMode() {
    Map<String, Object> fieldVars = Map.of(
      "fieldJoin", "AND",
      "field[0]", "field0",
      "fieldTest[0]", "NOTNULL",
      "field[1]", "field1",
      "fieldTest[1]", "NULL",
      "field[2]", "field2",
      "fieldTest[2]", "EQUAL",
      "fieldValue[2]", "[VALUE@field2]"
    );
    var formFieldValues = new HashMap<String, Object>() {{
      put("field0", "[VALUE@field0]"); // ok
      put("field1", null); // ok
      put("field2", "[VALUE@field2]"); // ok
    }};

    prepareFixtures(fieldVars, formFieldValues);
    shouldReject();

    prepareFixtures(fieldVars, new HashMap<>() {{
      put("field0", "[VALUE@field0]"); // ok
      put("field1", null); // ok
      put("field2", "[ANOTHER_VALUE@field2]"); // ng
    }});
    shouldPass();
  }

  @Test void itDoValidationWhenAnyConditionAreMetInTheOrJoinMode() {
    Map<String, Object> fieldVars = Map.of(
      "fieldJoin", "OR",
      "field[0]", "field0",
      "fieldTest[0]", "NOTNULL",
      "field[1]", "field1",
      "fieldTest[1]", "NULL",
      "field[2]", "field2",
      "fieldTest[2]", "EQUAL",
      "fieldValue[2]", "[VALUE@field2]"
    );
    var formFieldValues = new HashMap<String, Object>() {{
      put("field0", null); // ng
      put("field1", "[VALUE@field1]"); // ng
      put("field2", "[VALUE@field2]"); // ok
    }};
    prepareFixtures(fieldVars, formFieldValues);
    shouldReject();

    prepareFixtures(fieldVars, new HashMap<>() {{
      put("field0", null); // ng
      put("field1", "[VALUE@field1]"); // ng
      put("field2", "[ANOTHER_VALUE@field2]"); // ng
    }});
    shouldPass();
  }

  @Test void itThrowsErrorIfInvalidJoinTypeOrTestTypeWasAssigned() {
    prepareFixtures(Map.of(
      "fieldJoin", "INVALID_JOIN_TYPE",
      "field[0]", "field0",
      "fieldTest[0]", "NOTNULL"
    ), new HashMap<String, Object>() {{
      put("field0", null); // ng
    }});

    try {
      validateRequiredIf(mockBean, mockValidatorAction, mockField, mockErrors, mockValidator, mockRequest);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).contains("Unknown requiredIf validation join type");
    }

    prepareFixtures(Map.of(
      "fieldJoin", "OR",
      "field[0]", "field0",
      "fieldTest[0]", "INVALID_TEST_TYPE"
    ), new HashMap<String, Object>() {{
      put("field0", null); // ng
    }});

    try {
      validateRequiredIf(mockBean, mockValidatorAction, mockField, mockErrors, mockValidator, mockRequest);
      fail();
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).contains("Unknown requiredIf validation test type");
    }
  }
}
