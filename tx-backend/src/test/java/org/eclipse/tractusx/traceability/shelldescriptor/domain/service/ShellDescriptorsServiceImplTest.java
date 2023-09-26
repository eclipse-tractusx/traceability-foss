
/********************************************************************************
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
package org.eclipse.tractusx.traceability.shelldescriptor.domain.service;

import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.domain.repository.ShellDescriptorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShellDescriptorsServiceImplTest {

    @Mock
    private ShellDescriptorRepository shellDescriptorRepository;

    @InjectMocks
    private ShellDescriptorsServiceImpl shellDescriptorsService;

    @Test
    void testDetermineExistingShellDescriptorsAndUpdate() {
        // Given
        List<ShellDescriptor> ownShellDescriptors = new ArrayList<>();
        ShellDescriptor existingDescriptor = ShellDescriptor.builder().globalAssetId("existing-id").build();
        ownShellDescriptors.add(existingDescriptor);
        ShellDescriptor newDescriptor = ShellDescriptor.builder().globalAssetId("new-id").build();
        ownShellDescriptors.add(newDescriptor);

        when(shellDescriptorRepository.findAll()).thenReturn(List.of(existingDescriptor));


        // When
        shellDescriptorsService.determineExistingShellDescriptorsAndUpdate(ownShellDescriptors);

        // Then
        verify(shellDescriptorRepository, times(1)).save(newDescriptor);
        verify(shellDescriptorRepository, times(1)).removeDescriptorsByUpdatedBefore(any(ZonedDateTime.class));
        verify(shellDescriptorRepository, times(1)).update(existingDescriptor);
    }
}
