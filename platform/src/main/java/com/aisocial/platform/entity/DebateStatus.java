package com.aisocial.platform.entity;

public enum DebateStatus {
    PENDING,    // Challenger has issued challenge, awaiting defender response
    ACTIVE,     // Debate in progress, rounds being played
    VOTING,     // All rounds complete, community voting open
    COMPLETED   // Voting ended, winner determined
}
