package com.aisocial.platform.controller;

import com.aisocial.platform.dto.CreateDebateRequestDTO;
import com.aisocial.platform.dto.DebateDTO;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.service.DebateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Debate Controller Tests")
class DebateControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DebateService debateService;

    @InjectMocks
    private DebateController debateController;

    private ObjectMapper objectMapper;
    private UUID challengerId;
    private UUID defenderId;
    private UUID debateId;
    private DebateDTO debateDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        mockMvc = MockMvcBuilders.standaloneSetup(debateController).build();

        challengerId = UUID.randomUUID();
        defenderId = UUID.randomUUID();
        debateId = UUID.randomUUID();

        debateDTO = new DebateDTO();
        debateDTO.setId(debateId);
        debateDTO.setTopic("Is Java better than Python?");
        debateDTO.setStatus(DebateStatus.PENDING);
    }

    @Test
    @DisplayName("Should create a debate challenge")
    void shouldCreateChallenge() throws Exception {
        CreateDebateRequestDTO request = new CreateDebateRequestDTO(defenderId, "Test topic");

        when(debateService.createChallenge(eq(challengerId), eq(defenderId), eq("Test topic")))
                .thenReturn(debateDTO);

        mockMvc.perform(post("/api/debates")
                        .header("X-User-Id", challengerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(debateId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("Should get debate by ID")
    void shouldGetDebateById() throws Exception {
        when(debateService.getDebateById(debateId)).thenReturn(Optional.of(debateDTO));

        mockMvc.perform(get("/api/debates/{id}", debateId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(debateId.toString()))
                .andExpect(jsonPath("$.topic").value("Is Java better than Python?"));
    }

    @Test
    @DisplayName("Should return 404 when debate not found")
    void shouldReturn404WhenNotFound() throws Exception {
        when(debateService.getDebateById(debateId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/debates/{id}", debateId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get active debates")
    void shouldGetActiveDebates() throws Exception {
        debateDTO.setStatus(DebateStatus.ACTIVE);
        when(debateService.getActiveDebates()).thenReturn(List.of(debateDTO));

        mockMvc.perform(get("/api/debates"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(debateId.toString()))
                .andExpect(jsonPath("$[0].status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should get voting debates")
    void shouldGetVotingDebates() throws Exception {
        debateDTO.setStatus(DebateStatus.VOTING);
        when(debateService.getVotingDebates()).thenReturn(List.of(debateDTO));

        mockMvc.perform(get("/api/debates/voting"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("VOTING"));
    }

    @Test
    @DisplayName("Should accept challenge")
    void shouldAcceptChallenge() throws Exception {
        debateDTO.setStatus(DebateStatus.ACTIVE);
        when(debateService.acceptChallenge(debateId, defenderId)).thenReturn(debateDTO);

        mockMvc.perform(post("/api/debates/{id}/accept", debateId)
                        .header("X-User-Id", defenderId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    @DisplayName("Should decline challenge")
    void shouldDeclineChallenge() throws Exception {
        debateDTO.setStatus(DebateStatus.PENDING);
        when(debateService.declineChallenge(debateId, defenderId)).thenReturn(debateDTO);

        mockMvc.perform(post("/api/debates/{id}/decline", debateId)
                        .header("X-User-Id", defenderId.toString()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should get debates by user")
    void shouldGetDebatesByUser() throws Exception {
        when(debateService.getDebatesByUser(challengerId)).thenReturn(List.of(debateDTO));

        mockMvc.perform(get("/api/debates/user/{userId}", challengerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(debateId.toString()));
    }

    @Test
    @DisplayName("Should get pending challenges")
    void shouldGetPendingChallenges() throws Exception {
        when(debateService.getPendingChallengesForUser(defenderId)).thenReturn(List.of(debateDTO));

        mockMvc.perform(get("/api/debates/pending")
                        .header("X-User-Id", defenderId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(debateId.toString()));
    }
}
