package org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.schemamapper;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

@ExtendWith(MockitoExtension.class)
public class JsonToClassGeneratorTest {


    private JsonToClassGenerator jsonToClassGenerator = new JsonToClassGenerator();



    @Test
    public void test() throws IOException {

/*        String packageName = "org.eclipse.tractusx.traceability.assets.infrastructure.base.irs.schemamapper.generated";

        // load input JSON file
        String jsonPath = "src/test/resources/";
        File inputJson = new File(jsonPath + "Batch_2.0.0-schema.json");

        // create the local directory for generating the Java Class file
        String outputPath = "src/test/resources/";
        File outputJavaClassDirectory = new File(outputPath);

        String javaClassName = "Batch";

        jsonToClassGenerator.convertJsonToJavaClass(inputJson.toURI()
                .toURL(), outputJavaClassDirectory, packageName, javaClassName);

        File outputJavaClassPath = new File(outputPath + packageName.replace(".", "/"));
        Assertions.assertTrue(Arrays.stream(Objects.requireNonNull(outputJavaClassPath.listFiles())).peek(System.out::println).anyMatch(file -> (javaClassName+".java").equalsIgnoreCase(file.getName())));*/
    }

}
