package springing.struts1.validator;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import org.apache.commons.beanutils.DynaBean;

public class DynaBeanSerializer extends StdSerializer<DynaBean> {

  protected DynaBeanSerializer() {
    super(DynaBean.class);
  }

  @Override
  public void serialize(
    DynaBean dynaBean,
    JsonGenerator json,
    SerializerProvider serializerProvider
  ) throws IOException {
    var mapper = (ObjectMapper) json.getCodec();
    var clazz = dynaBean.getDynaClass();
    json.writeStartObject();
    for (var prop : clazz.getDynaProperties()) {
      var name = prop.getName();
      var value = dynaBean.get(name);
      json.writeFieldName(name);
      json.writeRawValue(mapper.writeValueAsString(value));
    }
    json.writeEndObject();
  }
}
