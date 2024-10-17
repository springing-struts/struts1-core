package org.apache.struts.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class LabelValueBean implements Comparable<LabelValueBean>, Serializable {
  public LabelValueBean(String label, String value) {
    this.label = label;
    this.value = value;
  }
  private final String label;
  public String getLabel() {
    return label;
  }

  private final String value;
  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    return (obj instanceof LabelValueBean another)
      && Objects.equals(this.label, another.label);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.label);
  }

  @Override
  public int compareTo(LabelValueBean another) {
    return comparator.compare(this, another);
  }

  private static final Comparator<LabelValueBean> comparator =
    Comparator.comparing(LabelValueBean::getLabel);

  @Override
  public String toString() {
    return "LabelValueBean[" + this.label + ", " + this.value + "]";
  }
}
