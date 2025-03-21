package springing.struts1.upload;

import org.apache.struts.upload.FormFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public class FormFileWrapper implements FormFile {

  public FormFileWrapper(MultipartFile file) {
    this.file = file;
  }
  private final MultipartFile file;

  @Override
  public String getContentType() {
    return file.getContentType();
  }

  @Override
  public long getFileLength() {
    return file.getSize();
  }

  @Override
  public int getFileSize() throws IllegalStateException {
    return (int) file.getSize();
  }

  @Override
  public String getFileName() {
    return file.getName();
  }

  @Override
  public byte[] getFileData() throws IOException {
    return file.getBytes();
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return file.getInputStream();
  }

  @Override
  public void destroy() {
    //NOP
  }
}
