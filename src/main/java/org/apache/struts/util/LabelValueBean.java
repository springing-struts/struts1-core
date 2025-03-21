package org.apache.struts.util;

import org.springframework.lang.Nullable;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Objects;

public class LabelValueBean implements Comparable<LabelValueBean>, Serializable {

  public LabelValueBean() {
    this(null, null);
  }

  public LabelValueBean(@Nullable String label, @Nullable String value) {
    this.label = label;
    this.value = value;
  }

  public @Nullable String getLabel() {
    return label;
  }

  public void setLabel(@Nullable String label) {
    this.label = label;
  }

  private @Nullable String label;


  public @Nullable String getValue() {
    return value;
  }

  public void setValue(@Nullable String value) {
    this.value = value;
  }

  private @Nullable String value;

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
