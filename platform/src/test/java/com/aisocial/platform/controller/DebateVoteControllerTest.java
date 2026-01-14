package com.aisocial.platform.controller;

import com.aisocial.platform.entity.DebateVote;
import com.aisocial.platform.entity.VoteType;
import com.aisocial.platform.service.DebateVoteService;
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

@DisplayName("DebateVoteController Tests")
class DebateVoteControllerTest {

    private DebateVoteService service;
    private DebateVoteController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(DebateVoteService.class);
        controller = new DebateVoteController(service);
    }

    @Test
    @DisplayName("Should return all votes")
    void testGetAll() {
        DebateVote vote1 = new DebateVote();
        vote1.setId(UUID.randomUUID());
        vote1.setDebateId(UUID.randomUUID());
        vote1.setUserId(UUID.randomUUID());
        vote1.setVote(VoteType.CHALLENGER);

        DebateVote vote2 = new DebateVote();
        vote2.setId(UUID.randomUUID());
        vote2.setDebateId(UUID.randomUUID());
        vote2.setUserId(UUID.randomUUID());
        vote2.setVote(VoteType.DEFENDER);

        when(service.findAll()).thenReturn(Arrays.asList(vote1, vote2));

        ResponseEntity<List<DebateVote>> response = controller.getAll();
        assertEquals(2, response.getBody().size());
        verify(service, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return vote by ID if exists")
    void testGetByIdFound() {
        UUID id = UUID.randomUUID();
        DebateVote vote = new DebateVote();
        vote.setId(id);
        vote.setVote(VoteType.CHALLENGER);

        when(service.findById(id)).thenReturn(Optional.of(vote));

        ResponseEntity<DebateVote> response = controller.getById(id);
        assertEquals(vote, response.getBody());
        verify(service, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should return 404 if vote by ID not found")
    void testGetByIdNotFound() {
        UUID id = UUID.randomUUID();
        when(service.findById(id)).thenReturn(Optional.empty());

        ResponseEntity<DebateVote> response = controller.getById(id);
        assertEquals(404, response.getStatusCode().value());
        verify(service, times(1)).findById(id);
    }

    @Test
    @DisplayName("Should create a new vote")
    void testCreate() {
        DebateVote vote = new DebateVote();
        vote.setId(UUID.randomUUID());
        vote.setVote(VoteType.TIE);

        when(service.save(any(DebateVote.class))).thenReturn(vote);

        ResponseEntity<DebateVote> response = controller.create(vote);
        assertEquals(vote, response.getBody());
        verify(service, times(1)).save(vote);
    }

    @Test
    @DisplayName("Should return 403 when participant tries to vote on own debate")
    void testCreateReturns403WhenParticipantVotes() {
        DebateVote vote = new DebateVote();
        vote.setDebateId(UUID.randomUUID());
        vote.setUserId(UUID.randomUUID());
        vote.setVote(VoteType.CHALLENGER);

        when(service.save(any(DebateVote.class)))
                .thenThrow(new IllegalArgumentException("Debate participants cannot vote on their own debate"));

        ResponseEntity<DebateVote> response = controller.create(vote);
        assertEquals(403, response.getStatusCode().value());
        verify(service, times(1)).save(vote);
    }

    @Test
    @DisplayName("Should update a vote if exists")
    void testUpdateFound() {
        UUID id = UUID.randomUUID();
        DebateVote updated = new DebateVote();
        updated.setId(id);
        updated.setVote(VoteType.DEFENDER);

        when(service.update(id, VoteType.DEFENDER)).thenReturn(updated);

        ResponseEntity<DebateVote> response = controller.update(id, updated);
        assertEquals(updated, response.getBody());
        verify(service, times(1)).update(id, VoteType.DEFENDER);
    }

    @Test
    @DisplayName("Should return 404 when updating non-existing vote")
    void testUpdateNotFound() {
        UUID id = UUID.randomUUID();
        DebateVote updated = new DebateVote();
        updated.setVote(VoteType.DEFENDER);

        when(service.update(id, VoteType.DEFENDER)).thenReturn(null);

        ResponseEntity<DebateVote> response = controller.update(id, updated);
        assertEquals(404, response.getStatusCode().value());
        verify(service, times(1)).update(id, VoteType.DEFENDER);
    }

    @Test
    @DisplayName("Should delete vote if exists")
    void testDeleteFound() {
        UUID id = UUID.randomUUID();
        when(service.delete(id)).thenReturn(true);

        ResponseEntity<Void> response = controller.delete(id);
        assertEquals(204, response.getStatusCode().value());
        verify(service, times(1)).delete(id);
    }

    @Test
    @DisplayName("Should return 404 when deleting non-existing vote")
    void testDeleteNotFound() {
        UUID id = UUID.randomUUID();
        when(service.delete(id)).thenReturn(false);

        ResponseEntity<Void> response = controller.delete(id);
        assertEquals(404, response.getStatusCode().value());
        verify(service, times(1)).delete(id);
    }
}
