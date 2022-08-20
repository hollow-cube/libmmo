package unnamed.mmo.datagen;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaClient;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class Main {
    public static void main(String[] args) throws Exception {

        try (InputStream inputStream = Main.class.getResourceAsStream("/schema/item.json")) {
            JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));

            Schema schema = SchemaLoader.builder()
                    .schemaClient(SchemaClient.classPathAwareClient())
                    .schemaJson(rawSchema)
                    .resolutionScope("classpath://schema/")
                    .build()
                    .load().build();
            schema.validate(new JSONObject("""
                    {
                        "namespace": "unnamed:diamond_pickaxe",
                        "components": {
                            "abc": {
                                "material": "abc"
                            }
                        },
                        "states": {
                            "[]": {
                            
                            }
                        }
                    }
                    """)); // throws a ValidationException if this object is invalid
        } catch (ValidationException ex) {
            ex.getAllMessages().forEach(System.err::println);
        }
    }
}
