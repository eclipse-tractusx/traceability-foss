package org.eclipse.tractusx.traceability.assets.application.rest;

import org.eclipse.tractusx.traceability.assets.application.ImportController;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;

public class ImportJsonControllerTest {

    @Test
    public void test_import_valid_json_file() throws IOException {
        // GIVEN
        ImportController importController = new ImportController();
        MockMultipartFile file = new MockMultipartFile("file", "test.json", MediaType.APPLICATION_JSON_VALUE, "{\"key\":Test\"value\":Test}".getBytes());

        // WHEN
        importController.importJson(file);

        //TODO: test_import_valid_json_file

    }
}
