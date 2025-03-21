package springing.struts1.controller;

import org.apache.struts.Globals;
import org.springframework.lang.Nullable;

import javax.servlet.http.HttpServletRequest;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RequestTokenManager {

  private @Nullable String token;

  public synchronized boolean isValid(HttpServletRequest request) {
    if (token == null) {
      return true;
    }
    var requestToken = request.getParameter(Globals.TOKEN_KEY);
    if (requestToken == null) {
      return true;
    }
    return token.equals(requestToken);
  }

  public synchronized void reset(HttpServletRequest request) {
    token = null;
  }

  public synchronized void save(HttpServletRequest request) {
    token = generateToken(request);
  }

  private String generateToken(HttpServletRequest request) {
    var sessionId = request.getSession(false).getId();
    var hash = sha256().digest(sessionId.getBytes(StandardCharsets.UTF_8));
    var buff = new StringBuilder();
    for (byte b : hash) {
      var hex = Integer.toHexString(0xff & b);
      if (hex.length() == 1) {
        buff.append('0');
      }
      buff.append(hex);
    }
    return buff.toString();
  }

  private MessageDigest sha256() {
    try {
      return MessageDigest.getInstance("SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }
}
