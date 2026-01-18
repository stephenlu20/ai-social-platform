package com.aisocial.platform.service;

import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.Debate;
import com.aisocial.platform.repository.DebateArgumentRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class DebateArgumentService {

    private final DebateArgumentRepository debateArgumentRepository;

    public DebateArgumentService(DebateArgumentRepository debateArgumentRepository) {
        this.debateArgumentRepository = debateArgumentRepository;
    }

    public DebateArgument update(UUID id, String newContent, Integer newRoundNumber) {
        DebateArgument existing = debateArgumentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("DebateArgument not found"));

        if (newContent != null) existing.setContent(newContent);
        if (newRoundNumber != null) existing.setRoundNumber(newRoundNumber);

        return debateArgumentRepository.save(existing);
    }

    public List<DebateArgument> findAll() {
        return debateArgumentRepository.findAll();
    }

    // Create or update
    public DebateArgument save(DebateArgument debateArgument) {
        return debateArgumentRepository.save(debateArgument);
    }

    // Find by ID
    public Optional<DebateArgument> findById(UUID id) {
        return debateArgumentRepository.findById(id);
    }

    // Find all arguments for a debate
    public List<DebateArgument> findByDebate(Debate debate) {
        return debateArgumentRepository.findByDebateOrderByRoundNumberAsc(debate);
    }

    // Delete
    public boolean delete(UUID id) {
        Optional<DebateArgument> existing = debateArgumentRepository.findById(id);
        if (existing.isPresent()) {
            debateArgumentRepository.delete(existing.get());
            return true;
        }
        return false;
    }

    // Find all arguments for a debate by debate ID
    public List<DebateArgument> findByDebateId(UUID debateId) {
        return debateArgumentRepository.findByDebateIdOrderByRoundNumberAscCreatedAtAsc(debateId);
    }
}
