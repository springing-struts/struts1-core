package org.apache.struts.validator;

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

import javax.servlet.http.HttpServletRequest;

import static org.apache.struts.validator.FieldChecks.validateMask;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FieldChecksMaskTest {

  @Mock Field mockField;
  @Mock ActionMessages mockErrors;
  @Mock HttpServletRequest mockRequest;
  @Mock Validator mockValidator;
  @Mock ValidatorAction mockAction;
  @Mock Object mockBean;

  void prepareFixture(@Nullable String mask, @Nullable Object fieldValue) {
    when(mockField.getRequiredVarValue("mask")).thenReturn(mask);
    when(mockField.getValueOf(mockBean)).thenReturn(fieldValue);
    mockErrors = mock(ActionMessages.class);
  }

  void shouldPass() {
    assertThat(
      validateMask(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isTrue();
    verify(mockErrors, never()).addValidationError(any(), any());
  }

  void shouldReject() {
    assertThat(
      validateMask(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isFalse();
    verify(mockErrors).addValidationError(mockField, mockAction);
  }

  @Test
  void itRejectsIfFieldLengthExceedsMinLength() {
    prepareFixture("^[0-9]+$", "abcde");
    shouldReject();

    prepareFixture("^[0-9]+$", "12345");
    shouldPass();

    prepareFixture("^[^\\s]+$", "  ");
    shouldReject();
  }

  @Test
  void itPassesNullField() {
    prepareFixture("^[0-9]+$", null);
    shouldPass();

    prepareFixture("^[0-9]+$", "");
    shouldPass();
  }
}
