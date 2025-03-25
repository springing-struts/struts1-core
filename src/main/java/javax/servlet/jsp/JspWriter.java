package javax.servlet.jsp;

import java.io.IOException;

public abstract class JspWriter extends jakarta.servlet.jsp.JspWriter {

  public static JspWriter toJavaxNamespace(
    jakarta.servlet.jsp.JspWriter jakarta
  ) {
    return new JspWriter(jakarta.getBufferSize(), jakarta.isAutoFlush()) {
      @Override
      public void newLine() throws IOException {
        jakarta.newLine();
      }

      @Override
      public void print(boolean b) throws IOException {
        jakarta.print(b);
      }

      @Override
      public void print(char c) throws IOException {
        jakarta.print(c);
      }

      @Override
      public void print(int i) throws IOException {
        jakarta.print(i);
      }

      @Override
      public void print(long l) throws IOException {
        jakarta.print(l);
      }

      @Override
      public void print(float f) throws IOException {
        jakarta.print(f);
      }

      @Override
      public void print(double d) throws IOException {
        jakarta.print(d);
      }

      @Override
      public void print(char[] s) throws IOException {
        jakarta.print(s);
      }

      @Override
      public void print(String s) throws IOException {
        jakarta.print(s);
      }

      @Override
      public void print(Object obj) throws IOException {
        jakarta.print(obj);
      }

      @Override
      public void println() throws IOException {
        jakarta.println();
      }

      @Override
      public void println(boolean x) throws IOException {
        jakarta.println(x);
      }

      @Override
      public void println(char x) throws IOException {
        jakarta.println(x);
      }

      @Override
      public void println(int x) throws IOException {
        jakarta.println(x);
      }

      @Override
      public void println(long x) throws IOException {
        jakarta.println(x);
      }

      @Override
      public void println(float x) throws IOException {
        jakarta.println(x);
      }

      @Override
      public void println(double x) throws IOException {
        jakarta.println(x);
      }

      @Override
      public void println(char[] x) throws IOException {
        jakarta.println(x);
      }

      @Override
      public void println(String x) throws IOException {
        jakarta.println(x);
      }

      @Override
      public void println(Object x) throws IOException {
        jakarta.println(x);
      }

      @Override
      public void clear() throws IOException {
        jakarta.clear();
      }

      @Override
      public void clearBuffer() throws IOException {
        jakarta.clearBuffer();
      }

      @Override
      public void flush() throws IOException {
        jakarta.flush();
      }

      @Override
      public void close() throws IOException {
        jakarta.close();
      }

      @Override
      public int getRemaining() {
        return jakarta.getRemaining();
      }

      @Override
      public void write(char[] cbuf, int off, int len) throws IOException {
        jakarta.write(cbuf, off, len);
      }
    };
  }

  protected JspWriter(int bufferSize, boolean autoFlush) {
    super(bufferSize, autoFlush);
  }
}
