package javax.servlet.jsp.el;

import java.lang.reflect.Method;

public abstract interface FunctionMapper {
  Method resolveFunction(String prefix, String localName);

  void mapFunction(String prefix, String localName, Method method);
}
