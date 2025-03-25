package org.apache.struts.validator;

import static org.apache.struts.validator.FieldChecks.validateDate;
import static org.apache.struts.validator.FieldChecks.validateIntRange;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.Mockito.*;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.lang.Nullable;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FieldChecksIntRangeTest {

  @Mock
  Field mockField;

  @Mock
  ActionMessages mockErrors;

  @Mock
  HttpServletRequest mockRequest;

  @Mock
  Validator mockValidator;

  @Mock
  ValidatorAction mockAction;

  @Mock
  Object mockBean;

  void prepareFixture(
    @Nullable Object fieldValue,
    @Nullable String min,
    @Nullable String max
  ) {
    when(mockField.getVarValueAsLong("min")).thenReturn(
      min == null ? null : Long.parseLong(min)
    );
    when(mockField.getVarValueAsLong("max")).thenReturn(
      max == null ? null : Long.parseLong(max)
    );
    when(mockField.getValueOf(mockBean)).thenReturn(fieldValue);
    mockErrors = mock(ActionMessages.class);
  }

  void shouldPass() {
    assertThat(
      validateIntRange(
        mockBean,
        mockAction,
        mockField,
        mockErrors,
        mockValidator,
        mockRequest
      )
    ).isTrue();
    verify(mockErrors, never()).addValidationError(any(), any());
  }

  void shouldReject() {
    assertThat(
      validateDate(
        mockBean,
        mockAction,
        mockField,
        mockErrors,
        mockValidator,
        mockRequest
      )
    ).isFalse();
    verify(mockErrors).addValidationError(mockField, mockAction);
  }

  @Test
  void itRejectsInvalidIntegerValues() {
    prepareFixture("  ", null, null);
    shouldReject();

    prepareFixture("aaa", null, null);
    shouldReject();

    prepareFixture("0.1", null, null);
    shouldReject();
  }

  @Test
  void itRejectsIntegerValuesThatIsOutOfTheGivenRange() {
    prepareFixture("42", null, null);
    shouldPass();

    prepareFixture("42", "0", null);
    shouldPass();

    prepareFixture("42", "41", null);
    shouldPass();

    prepareFixture("42", "42", null);
    shouldPass();

    prepareFixture("42", "43", null);
    shouldReject();

    prepareFixture("-1", "0", null);
    shouldReject();

    prepareFixture("42", null, "41");
    shouldReject();

    prepareFixture("42", null, "42");
    shouldPass();

    prepareFixture("42", null, "43");
    shouldReject();

    prepareFixture("42", "41", "43");
    shouldPass();

    prepareFixture("42", "0", "41");
    shouldReject();

    prepareFixture("42", "43", "100");
    shouldReject();

    prepareFixture("42", "42", "42");
    shouldPass();
  }

  @Test
  void itPassesNullField() {
    prepareFixture(null, "4", "16");
    shouldPass();

    prepareFixture("", null, null);
    shouldPass();
  }
}
