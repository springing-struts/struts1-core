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

import static org.apache.struts.validator.FieldChecks.validateDouble;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class FieldChecksDoubleTest {

  @Mock Field mockField;
  @Mock ActionMessages mockErrors;
  @Mock HttpServletRequest mockRequest;
  @Mock Validator mockValidator;
  @Mock ValidatorAction mockAction;
  @Mock Object mockBean;

  void prepareFixture(@Nullable Object fieldValue) {
    when(mockField.getValueOf(mockBean)).thenReturn(fieldValue);
    mockErrors = mock(ActionMessages.class);
  }

  void shouldPass() {
    assertThat(
      validateDouble(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isTrue();
    verify(mockErrors, never()).addValidationError(any(), any());
  }

  void shouldReject() {
    assertThat(
      validateDouble(mockBean, mockAction, mockField, mockErrors, mockValidator, mockRequest)
    ).isFalse();
    verify(mockErrors).addValidationError(mockField, mockAction);
  }

  @Test
  void itRejectsFieldValuesThatCouldNotBeConvertedToDouble() {

    prepareFixture("aa");
    shouldReject();

    prepareFixture("0");
    shouldPass();

    prepareFixture("１２.３４");
    shouldPass();

    prepareFixture("１２．３４");
    shouldReject();

    prepareFixture("12.34a");
    shouldReject();

    prepareFixture("0.123");
    shouldPass();

    prepareFixture("-0.123");
    shouldPass();

    prepareFixture("-1.23E-2");
    shouldPass();

    prepareFixture("+1.23E+2");
    shouldPass();

    prepareFixture("-1.23E-2.0");
    shouldReject();

    prepareFixture("3.40282346638528860E38");
    shouldPass();

    prepareFixture("3.41282346638528860E39");
    shouldPass();

    prepareFixture("+1.7e+308");
    shouldPass();

    prepareFixture("+1.8e+308");
    shouldReject();
  }

  @Test
  void itPassesNullField() {
    prepareFixture(null);
    shouldPass();

    prepareFixture("");
    shouldPass();
  }
}
