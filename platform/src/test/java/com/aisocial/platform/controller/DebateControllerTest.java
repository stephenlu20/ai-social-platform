package com.aisocial.platform.controller;

import com.aisocial.platform.dto.CreateDebateRequestDTO;
import com.aisocial.platform.dto.DebateDTO;
import com.aisocial.platform.dto.SubmitArgumentRequestDTO;
import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.DebateStatus;
import com.aisocial.platform.entity.User;
import com.aisocial.platform.repository.DebateRepository;
import com.aisocial.platform.repository.UserRepository;
import com.aisocial.platform.service.DebateService;
import com.aisocial.platform.service.DebateStateMachine;
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

    @Mock
    private DebateStateMachine debateStateMachine;

    @Mock
    private DebateRepository debateRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DebateController debateController;

    private ObjectMapper objectMapper;
    private UUID challengerId;
    private UUID defenderId;
    private UUID debateId;
    private DebateDTO debateDTO;
    private User challenger;
    private User defender;
    private Debate debate;

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

        challenger = new User("challenger", "Challenger", "Bio");
        challenger.setId(challengerId);

        defender = new User("defender", "Defender", "Bio");
        defender.setId(defenderId);

        debate = new Debate("Is Java better than Python?", challenger, defender);
        debate.setId(debateId);
        debate.setStatus(DebateStatus.ACTIVE);
        debate.setWhoseTurn(challenger);
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

    @Test
    @DisplayName("Should submit argument when it's user's turn")
    void shouldSubmitArgumentWhenUsersTurn() throws Exception {
        SubmitArgumentRequestDTO request = new SubmitArgumentRequestDTO("My argument content");
        DebateArgument argument = new DebateArgument(debate, challenger, 1, "My argument content");
        argument.setId(UUID.randomUUID());

        when(debateRepository.findById(debateId)).thenReturn(Optional.of(debate));
        when(userRepository.findById(challengerId)).thenReturn(Optional.of(challenger));
        when(debateStateMachine.submitArgument(debate, challenger, "My argument content"))
                .thenReturn(argument);

        mockMvc.perform(post("/api/debates/{id}/arguments", debateId)
                        .header("X-User-Id", challengerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("My argument content"))
                .andExpect(jsonPath("$.roundNumber").value(1));
    }

    @Test
    @DisplayName("Should return 403 when not user's turn")
    void shouldReturn403WhenNotUsersTurn() throws Exception {
        SubmitArgumentRequestDTO request = new SubmitArgumentRequestDTO("My argument content");

        when(debateRepository.findById(debateId)).thenReturn(Optional.of(debate));
        when(userRepository.findById(defenderId)).thenReturn(Optional.of(defender));
        when(debateStateMachine.submitArgument(debate, defender, "My argument content"))
                .thenThrow(new IllegalStateException("Not user's turn"));

        mockMvc.perform(post("/api/debates/{id}/arguments", debateId)
                        .header("X-User-Id", defenderId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should return 404 when debate not found")
    void shouldReturn404WhenDebateNotFoundForArgument() throws Exception {
        SubmitArgumentRequestDTO request = new SubmitArgumentRequestDTO("My argument content");
        UUID nonExistentDebateId = UUID.randomUUID();

        when(debateRepository.findById(nonExistentDebateId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/debates/{id}/arguments", nonExistentDebateId)
                        .header("X-User-Id", challengerId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 when user not found")
    void shouldReturn400WhenUserNotFound() throws Exception {
        SubmitArgumentRequestDTO request = new SubmitArgumentRequestDTO("My argument content");
        UUID nonExistentUserId = UUID.randomUUID();

        when(debateRepository.findById(debateId)).thenReturn(Optional.of(debate));
        when(userRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/debates/{id}/arguments", debateId)
                        .header("X-User-Id", nonExistentUserId.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 403 when user is not a participant")
    void shouldReturn403WhenNotParticipant() throws Exception {
        SubmitArgumentRequestDTO request = new SubmitArgumentRequestDTO("My argument content");
        User outsider = new User("outsider", "Outsider", "Bio");
        outsider.setId(UUID.randomUUID());

        when(debateRepository.findById(debateId)).thenReturn(Optional.of(debate));
        when(userRepository.findById(outsider.getId())).thenReturn(Optional.of(outsider));
        when(debateStateMachine.submitArgument(debate, outsider, "My argument content"))
                .thenThrow(new IllegalStateException("User is not a participant"));

        mockMvc.perform(post("/api/debates/{id}/arguments", debateId)
                        .header("X-User-Id", outsider.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
}
