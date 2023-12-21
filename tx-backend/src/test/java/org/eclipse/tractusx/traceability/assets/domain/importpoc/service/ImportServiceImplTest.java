package org.eclipse.tractusx.traceability.assets.domain.importpoc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.tractusx.traceability.assets.domain.asbuilt.repository.AssetAsBuiltRepository;
import org.eclipse.tractusx.traceability.assets.domain.asplanned.repository.AssetAsPlannedRepository;


import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.io.InputStream;

@ExtendWith(MockitoExtension.class)
class ImportServiceImplTest {

    @InjectMocks
    private ImportServiceImpl importService;

    @Mock
    private AssetAsPlannedRepository assetAsPlannedRepository;
    @Mock
    private AssetAsBuiltRepository assetAsBuiltRepository;

    @BeforeEach
    public void testSetup(){
        ObjectMapper objectMapper = new ObjectMapper();
        importService = new ImportServiceImpl(objectMapper, assetAsPlannedRepository, assetAsBuiltRepository);

    }
    @Test
    void testService() throws IOException {

        InputStream file = ImportServiceImplTest.class.getResourceAsStream("/testdata/import-request.json");
        // Convert the file to a MockMultipartFile
        MockMultipartFile multipartFile = new MockMultipartFile(
                "file",               // Parameter name in the multipart request
                "import-request",             // Original file name
                "application/json",   // Content type
                file
        );

        importService.importAssets(multipartFile);


    }

}
