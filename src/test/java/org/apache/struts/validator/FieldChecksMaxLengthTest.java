package org.apache.struts.validator;

import static org.apache.struts.validator.FieldChecks.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
public class FieldChecksMaxLengthTest {

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

  void prepareFixture(String maxLength, @Nullable Object fieldValue) {
    when(mockField.getRequiredVarValueAsLong("maxlength")).thenReturn(
      Long.parseLong(maxLength)
    );
    when(mockField.getValueOf(mockBean)).thenReturn(fieldValue);
    mockErrors = mock(ActionMessages.class);
  }

  void shouldPass() {
    assertThat(
      validateMaxLength(
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
      validateMaxLength(
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
  void itRejectsIfFieldLengthExceedsMaxLength() throws Exception {
    prepareFixture("4", "12345");
    shouldReject();

    prepareFixture("5", "12345");
    shouldPass();

    prepareFixture("6", "12345");
    shouldPass();
  }

  @Test
  void itPassesNullField() throws Exception {
    prepareFixture("4", null);
    shouldPass();

    prepareFixture("4", "");
    shouldPass();
  }
}
