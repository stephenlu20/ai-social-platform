package com.aisocial.platform.controller;

import com.aisocial.platform.entity.FactCheck;
import com.aisocial.platform.entity.FactCheckStatus;
import com.aisocial.platform.service.AIFactCheckService;
import com.aisocial.platform.service.FactCheckService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("FactCheckController Tests")
class FactCheckControllerTest {

    private FactCheckService service;
    private AIFactCheckService aiFactCheckService;
    private FactCheckController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(FactCheckService.class);
        aiFactCheckService = Mockito.mock(AIFactCheckService.class);
        controller = new FactCheckController(service, aiFactCheckService);
    }

    @Test
    @DisplayName("Should return all fact checks")
    void testGetAll() {
        FactCheck fc1 = new FactCheck();
        fc1.setId(UUID.randomUUID());
        FactCheck fc2 = new FactCheck();
        fc2.setId(UUID.randomUUID());

        when(service.findAll()).thenReturn(Arrays.asList(fc1, fc2));

        ResponseEntity<List<FactCheck>> response = controller.getAll();
        assertEquals(2, response.getBody().size());
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return fact check by ID if exists")
    void testGetByIdFound() {
        UUID id = UUID.randomUUID();
        FactCheck fc = new FactCheck();
        fc.setId(id);

        when(service.findById(id)).thenReturn(Optional.of(fc));

        ResponseEntity<FactCheck> response = controller.getById(id);
        assertEquals(fc, response.getBody());
        verify(service, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return 404 if fact check by ID not found")
    void testGetByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<FactCheck> response = controller.getById(id);
        assertEquals(404, response.getStatusCode().value());
        verify(service, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should create a new fact check")
    void testCreate() {
        FactCheck fc = new FactCheck();
        fc.setId(UUID.randomUUID());
        fc.setStatus(FactCheckStatus.UNCHECKED);

        when(service.save(any(FactCheck.class))).thenReturn(fc);

        ResponseEntity<FactCheck> response = controller.create(fc);
        assertEquals(fc, response.getBody());
        verify(service, times(1)).save(fc);
    }

    @Test
    @DisplayName("Should update fact check if exists")
    void testUpdateFound() {
        UUID id = UUID.randomUUID();
        FactCheck updated = new FactCheck();
        updated.setId(id);
        updated.setStatus(FactCheckStatus.VERIFIED);

        when(service.update(id, updated.getStatus(), updated.getOverallScore(), updated.getClaims()))
                .thenReturn(updated);

        ResponseEntity<FactCheck> response = controller.update(id, updated);
        assertEquals(updated, response.getBody());
        verify(service, times(1))
                .update(id, updated.getStatus(), updated.getOverallScore(), updated.getClaims());
    }

    @Test
    @DisplayName("Should return 404 when updating non-existing fact check")
    void testUpdateNotFound() {
        UUID id = UUID.randomUUID();
        FactCheck updated = new FactCheck();
        updated.setStatus(FactCheckStatus.DISPUTED);

        when(service.update(id, updated.getStatus(), updated.getOverallScore(), updated.getClaims()))
                .thenReturn(null);

        ResponseEntity<FactCheck> response = controller.update(id, updated);
        assertEquals(404, response.getStatusCode().value());
        verify(service, times(1))
                .update(id, updated.getStatus(), updated.getOverallScore(), updated.getClaims());
    }

    @Test
    @DisplayName("Should delete fact check if exists")
    void testDeleteFound() {
        UUID id = UUID.randomUUID();
        when(service.delete(id)).thenReturn(true);

        ResponseEntity<Void> response = controller.delete(id);
        assertEquals(204, response.getStatusCode().value());
        verify(service, times(1)).delete(id);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existing fact check")
    void testDeleteNotFound() {
        UUID id = UUID.randomUUID();
        when(service.delete(id)).thenReturn(false);

        ResponseEntity<Void> response = controller.delete(id);
        assertEquals(404, response.getStatusCode().value());
        verify(service, times(1)).delete(id);
    }
}
