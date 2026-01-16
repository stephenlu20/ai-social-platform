# CANDOR
## *An AI-Augmented Social Platform for Trust and Accountability*

### User Stories

- **Post Messages:** Users can share short-form posts (280 characters) to express thoughts and start conversations.

- **Reply & Thread:** Users can reply to posts creating threaded discussions for organized conversations.

- **Repost Content:** Users can repost others' content to amplify messages to their followers.

- **Like Posts:** Users can like posts to show appreciation and boost engagement metrics.

- **Follow Users:** Users can follow other users to curate a personalized feed of content.

- **Trust Scoring:** Users build a trust score (0-100) based on verified posts and debate performance, displayed publicly.

- **Structured Debates:** Users can challenge others to 3-round debates on topics, with turn-based arguments.

- **Debate Voting:** Users can vote on active debates (Challenger/Defender/Tie) to determine winners.

- **Fact-Check Posts:** Users can request AI-powered fact-checks on posts with status indicators (Verified, Disputed, False).

---

### Additional Features

- **Personalized Feed:** Home feed displays posts from followed users, sorted by newest first.

- **Search:** Users can search posts by content/author and users by username/trust score.

- **Trust Breakdown:** Users can view detailed breakdowns of how trust scores are calculated.

- **Debate State Machine:** Debates progress through PENDING → ACTIVE → VOTING → COMPLETED with enforced turn-taking.

---

### Tech Stack

- **Backend:** Spring Boot 3.5.9 (Java 17)
- **Frontend:** React 19 with Vite & TailwindCSS
- **Database:** SQLite (development) / PostgreSQL (production)

---

**Group Members:** Marc McGough, Stephen Lu, Frank Montgomery
