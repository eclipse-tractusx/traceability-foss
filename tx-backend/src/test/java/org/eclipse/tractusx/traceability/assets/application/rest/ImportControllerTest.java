package org.eclipse.tractusx.traceability.assets.application.rest;

import org.eclipse.tractusx.traceability.assets.application.ImportController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
class ImportControllerTest {

    private final ImportController importController;

    ImportControllerTest(ImportController importController) {
        this.importController = importController;
    }

/*    @Test
    public void test_import_valid_json_file() throws IOException {
        // GIVEN
        ImportController importController = new ImportController();
        MockMultipartFile file = new MockMultipartFile("file", "test.json", MediaType.APPLICATION_JSON_VALUE, "{\"key\":Test\"value\":Test}".getBytes());

        // WHEN
        importController.importJson(file);

        //TODO: test_import_valid_json_file

    }*/

    @Test
    public void testImportJson() throws Exception {
        // Load your JSON content from a file
        String jsonContent = "{ \"assets\": [ /* your JSON content here */ ] }";

        // Create a MockMultipartFile with the JSON content
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.json",
                MediaType.APPLICATION_JSON_VALUE,
                jsonContent.getBytes()
        );

        // Create a MockMvc instance
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(importController).build();

        // Perform the file upload and expect a successful response
        mockMvc.perform(MockMvcRequestBuilders.multipart("/assets/import")
                        .file(file))
                .andExpect(status().isOk());
    }
}
