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
package org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.repository.jpa;

import org.eclipse.tractusx.traceability.shelldescriptor.domain.model.ShellDescriptor;
import org.eclipse.tractusx.traceability.shelldescriptor.infrastructure.model.ShellDescriptorEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShellDescriptorRepositoryImplTest {

    @Mock
    private JpaShellDescriptorRepository repository;

    @InjectMocks
    private ShellDescriptorRepositoryImpl shellDescriptorRepository;


    @Test
    void testFindAll() {
        // Given
        List<ShellDescriptorEntity> entities = new ArrayList<>();
        entities.add(new ShellDescriptorEntity(/* create entity */));
        entities.add(new ShellDescriptorEntity(/* create entity */));

        when(repository.findAll()).thenReturn(entities);

        shellDescriptorRepository = new ShellDescriptorRepositoryImpl(repository);

        // Act
        shellDescriptorRepository.findAll();

        // Then
        verify(repository, times(1)).findAll();

    }

    @Test
    void testUpdate() {
        // Given
        ShellDescriptor descriptor = ShellDescriptor.builder().id(1L).build();
        ShellDescriptorEntity entity = new ShellDescriptorEntity(/* create entity */);

        when(repository.findById(descriptor.getId())).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);

        shellDescriptorRepository = new ShellDescriptorRepositoryImpl(repository);

        // When
        shellDescriptorRepository.update(descriptor);

        // Then
        verify(repository, times(1)).findById(descriptor.getId());
        verify(repository, times(1)).save(entity);
    }

    @Test
    void testSaveAll() {
        // Given
        List<ShellDescriptor> descriptors = new ArrayList<>();
        descriptors.add(ShellDescriptor.builder().build());
        descriptors.add(ShellDescriptor.builder().build());

        shellDescriptorRepository = new ShellDescriptorRepositoryImpl(repository);

        // When
        shellDescriptorRepository.saveAll(descriptors);

        // Then
        verify(repository, times(1)).saveAll(anyList());
    }

    @Test
    void testSave() {
        // Given
        ShellDescriptor descriptors = ShellDescriptor.builder().build();
        shellDescriptorRepository = new ShellDescriptorRepositoryImpl(repository);

        // When
        shellDescriptorRepository.save(descriptors);

        // Then
        verify(repository, times(1)).save(any());
    }

    @Test
    void testRemoveDescriptorsByUpdatedBefore() {
        // Given
        ZonedDateTime now = ZonedDateTime.now();

        shellDescriptorRepository = new ShellDescriptorRepositoryImpl(repository);

        // When
        shellDescriptorRepository.removeDescriptorsByUpdatedBefore(now);

        // Then
        verify(repository, times(1)).deleteAllByUpdatedBefore(now);
    }
}
