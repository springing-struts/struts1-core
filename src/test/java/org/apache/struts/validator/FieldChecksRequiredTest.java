package org.apache.struts.validator;

import static org.apache.struts.validator.FieldChecks.validateRequired;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

import org.apache.commons.validator.Field;
import org.apache.commons.validator.Validator;
import org.apache.commons.validator.ValidatorAction;
import org.apache.struts.action.ActionMessages;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class FieldChecksRequiredTest {

  @Mock Field mockField;
  @Mock ActionMessages mockErrors;
  @Mock HttpServletRequest mockRequest;
  @Mock Validator mockValidator;
  @Mock ValidatorAction mockAction;
  @Mock Object mockBean;

  void prepareFixture(@Nullable String fieldValue) {
    when(mockField.getValueOf(mockBean)).thenReturn(fieldValue);
    mockErrors = mock(ActionMessages.class);
  }

  void shouldPass() {
    assertThat(
      validateRequired(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isTrue();
    verify(mockErrors, never()).addValidationError(any(), any());
  }

  void shouldReject() {
    assertThat(
      validateRequired(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isFalse();
    verify(mockErrors).addValidationError(mockField, mockAction);
  }

  @Test void itPassesNonNullFieldValue() throws Exception {
    prepareFixture("Valid Value");
    shouldPass();
  }

  @Test void itRejectsEmptyField() throws Exception {
    prepareFixture("   ");
    shouldReject();
  }

  @Test
  public void itRejectsNullField() throws Exception {
    prepareFixture(null);
    shouldReject();
  }
}
