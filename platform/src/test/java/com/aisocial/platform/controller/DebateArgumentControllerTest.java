package com.aisocial.platform.controller;

import com.aisocial.platform.dto.DebateArgumentUpdateDTO;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.service.DebateArgumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@DisplayName("DebateArgumentController Unit Tests")
class DebateArgumentControllerTest {

    private DebateArgumentService debateArgumentService;
    private DebateArgumentController controller;

    private User user;
    private DebateArgument argument;

    @BeforeEach
    void setUp() {
        debateArgumentService = mock(DebateArgumentService.class);
        controller = new DebateArgumentController(debateArgumentService);

        user = new User("testuser", "Test User", "Bio");
        argument = new DebateArgument(null, user, 1, "Test Argument");
        argument.setId(UUID.randomUUID());
    }

    @Test
    @DisplayName("GET /api/debate-arguments - should return all arguments")
    void getAll_ShouldReturnAll() {
        when(debateArgumentService.findAll()).thenReturn(List.of(argument));

        ResponseEntity<List<DebateArgument>> response = controller.getAll();

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).contains(argument);
        verify(debateArgumentService, times(1)).findAll();
    }

    @Test
    @DisplayName("GET /api/debate-arguments/{id} - found")
    void getById_Found() {
        when(debateArgumentService.findById(argument.getId())).thenReturn(Optional.of(argument));

        ResponseEntity<DebateArgument> response = controller.getById(argument.getId());

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(argument);
        verify(debateArgumentService, times(1)).findById(argument.getId());
    }

    @Test
    @DisplayName("GET /api/debate-arguments/{id} - not found")
    void getById_NotFound() {
        UUID id = UUID.randomUUID();
        when(debateArgumentService.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<DebateArgument> response = controller.getById(id);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
        verify(debateArgumentService, times(1)).findById(id);
    }

    @Test
    @DisplayName("POST /api/debate-arguments - create")
    void create_ShouldReturnSaved() {
        when(debateArgumentService.save(argument)).thenReturn(argument);

        ResponseEntity<DebateArgument> response = controller.create(argument);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo(argument);
        verify(debateArgumentService, times(1)).save(argument);
    }

    @Test
    @DisplayName("PUT /api/debate-arguments/{id} - update found")
    void update_Found() {
        DebateArgumentUpdateDTO dto = new DebateArgumentUpdateDTO();
        dto.setContent("Updated Content");
        dto.setRoundNumber(2);

        DebateArgument updatedArgument = new DebateArgument(null, user, 2, "Updated Content");
        updatedArgument.setId(argument.getId());

        when(debateArgumentService.update(argument.getId(), dto.getContent(), dto.getRoundNumber()))
                .thenReturn(updatedArgument);

        ResponseEntity<DebateArgument> response = controller.update(argument.getId(), dto);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody().getContent()).isEqualTo("Updated Content");
        assertThat(response.getBody().getRoundNumber()).isEqualTo(2);
        verify(debateArgumentService, times(1))
                .update(argument.getId(), dto.getContent(), dto.getRoundNumber());
    }

    @Test
    @DisplayName("PUT /api/debate-arguments/{id} - update not found")
    void update_NotFound() {
        DebateArgumentUpdateDTO dto = new DebateArgumentUpdateDTO();
        dto.setContent("Updated Content");
        dto.setRoundNumber(2);

        UUID id = UUID.randomUUID();
        when(debateArgumentService.update(id, dto.getContent(), dto.getRoundNumber()))
                .thenReturn(null);

        ResponseEntity<DebateArgument> response = controller.update(id, dto);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
        verify(debateArgumentService, times(1))
                .update(id, dto.getContent(), dto.getRoundNumber());
    }

    @Test
    @DisplayName("DELETE /api/debate-arguments/{id} - found")
    void delete_Found() {
        when(debateArgumentService.delete(argument.getId())).thenReturn(true);

        ResponseEntity<Void> response = controller.delete(argument.getId());

        assertThat(response.getStatusCode().value()).isEqualTo(204);
        verify(debateArgumentService, times(1)).delete(argument.getId());
    }

    @Test
    @DisplayName("DELETE /api/debate-arguments/{id} - not found")
    void delete_NotFound() {
        UUID id = UUID.randomUUID();
        when(debateArgumentService.delete(id)).thenReturn(false);

        ResponseEntity<Void> response = controller.delete(id);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        verify(debateArgumentService, times(1)).delete(id);
    }
}
