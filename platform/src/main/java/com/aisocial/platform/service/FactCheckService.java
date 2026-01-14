package com.aisocial.platform.service;

import com.aisocial.platform.entity.DebateArgument;
import com.aisocial.platform.entity.FactCheck;
import com.aisocial.platform.entity.FactCheckStatus;
import com.aisocial.platform.repository.FactCheckRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class FactCheckService {

    private final FactCheckRepository factCheckRepository;

    public FactCheckService(FactCheckRepository factCheckRepository) {
        this.factCheckRepository = factCheckRepository;
    }

    // ----------------------------
    // Save
    // ----------------------------
    public FactCheck save(FactCheck factCheck) {
        return factCheckRepository.save(factCheck);
    }

    // ----------------------------
    // Find by ID
    // ----------------------------
    public Optional<FactCheck> findById(UUID id) {
        return factCheckRepository.findById(id);
    }

    // ----------------------------
    // Find all
    // ----------------------------
    public List<FactCheck> findAll() {
        return factCheckRepository.findAll();
    }

    // ----------------------------
    // Find by Debate Argument
    // ----------------------------
    public List<FactCheck> findByDebateArg(DebateArgument debateArg) {
        return factCheckRepository.findByDebateArg(debateArg);
    }

    // ----------------------------
    // Update
    // ----------------------------
    public FactCheck update(UUID id, FactCheckStatus status, Double overallScore, String claims) {
        Optional<FactCheck> optional = factCheckRepository.findById(id);
        if (optional.isPresent()) {
            FactCheck fc = optional.get();
            if (status != null) fc.setStatus(status);
            if (overallScore != null) fc.setOverallScore(overallScore);
            if (claims != null) fc.setClaims(claims);
            return factCheckRepository.save(fc);
        }
        return null;
    }

    // ----------------------------
    // Delete by ID
    // ----------------------------
    public boolean delete(UUID id) {
        Optional<FactCheck> optional = factCheckRepository.findById(id);
        if (optional.isPresent()) {
            factCheckRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
