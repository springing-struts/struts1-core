package org.apache.struts.taglib.bean;

import jakarta.servlet.jsp.JspException;
import org.apache.taglibs.standard.tag.common.core.SetSupport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.lang.Nullable;
import org.springframework.util.ResourceUtils;
import springing.util.ServletRequestUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.springframework.util.StringUtils.hasText;

/**
 * Load a web application resource and make it available as a bean.
 * Retrieve the value of the specified web application resource, and make it
 * available as either a `InputStream` or a `String`, depending on the value
 * of the `input` attribute.
 * If a problem occurs while retrieving the specified resource, a request time
 * exception will be thrown.
 */
public class ResourceTag extends SetSupport {

  public ResourceTag() {
    init();
  }

  @Override
  public void release() {
    super.release();
    init();
  }

  private void init() {
    input = false;
    name = null;
  }

  private boolean input = false;
  private @Nullable String name;

  @Override
  public int doEndTag() throws JspException {

    if (!hasText(name)) throw new IllegalStateException(String.format(
      "The name property of this tag [%s] is required.", this.getClass().getName()
    ));

    try (var in = new ClassPathResource(name).getInputStream()) {
      var buff = in.readAllBytes();
      if (this.input) {
        value = new ByteArrayInputStream(buff);
      }
      value = new String(buff, StandardCharsets.UTF_8);
    } catch (IOException e) {
      throw new IllegalStateException(String.format(
        "An error occurred while reading or closing the stream of resource at [%s].", name
      ), e);
    }
    return super.doEndTag();
  }

  /**
   * (Required) Specifies the name of the scripting variable (and associated
   * page scope attribute) that will be made available with the value of the
   * specified web application resource.
   */
  public void setId(String id) {
    setVar(id);
  }

  /**
   * If any arbitrary value for this attribute is specified, the resource will
   * be made available as an `InputStream`.  If this attribute is not
   * specified, the resource will be made available as a `String`.
   */
  public void setInput(String input) {
    this.input = hasText(input);
  }

  /**
   * Module-relative name (starting with a '/') of the web application resource
   * to be loaded and made available.
   */
  public void setName(String name) {
    this.name = name;
  }
}
