package org.eclipse.tractusx.ssi.lib.exception.did;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.tractusx.ssi.lib.exception.resolver.DidWebException;
import org.junit.jupiter.api.Test;

class DidWebExceptionTest {

  @Test
  void testDidWebExceptionWithCause() {
    RuntimeException runtimeException = new RuntimeException("Some error occurred");
    DidWebException exception = new DidWebException(runtimeException);
    assertEquals(runtimeException.getMessage(), exception.getCause().getMessage());
  }

  @Test
  void testDidWebException() {
    DidWebException exception = new DidWebException("test message");
    assertEquals("test message", exception.getMessage());
  }

  @Test
  void testDidWebExceptionWithCauseAndMessage() {
    Throwable cause = new Throwable("test cause");
    DidWebException exception = new DidWebException("test message", cause);
    assertEquals("test message", exception.getMessage());
    assertEquals(cause, exception.getCause());
  }

  @Test
  void testDidWebExceptionWithAllParams() {
    Throwable cause = new Throwable("test cause");
    boolean enableSuppression = true;
    boolean writableStackTrace = true;
    DidWebException exception =
        new DidWebException("test message", cause, enableSuppression, writableStackTrace);
    assertEquals("test message", exception.getMessage());
    assertEquals(cause, exception.getCause());
    assertEquals(enableSuppression, exception.getSuppressed().length == 0);
    assertEquals(writableStackTrace, exception.getStackTrace().length > 0);
  }
}
