# Technical Specification (V1)

## 1. System Architecture
Candor follows a **three-tier architecture**:

React Frontend -> Spring Boot REST API -> SQL Database

- **Frontend (React):**  
  - Provides user interface for posting, browsing, debating, and interacting with AI features  
  - Consumes RESTful endpoints from the backend  

- **Backend (Spring Boot):**  
  - Implements business logic, API endpoints, and AI orchestration  
  - Manages data persistence, post and debate lifecycle, and trust score calculation  

- **Database (SQL - PostgreSQL / MySQL / SQLite):**  
  - Stores users, posts, likes, follows, debates, debate arguments, votes, and fact-check data  
  - Maintains relational integrity and supports analytic queries for trust scores and AI processing  

---

## 2. Data Model

### users
Primary entity representing platform participants.

**Fields**
- `id` (PK)  
- `username`  
- `display_name`  
- `bio`  
- `avatar_url`  
- `trust_score` (AI-derived)  
- `posts_fact_checked`, `posts_verified`, `posts_false`  
- `debates_won`, `debates_lost`  
- `created_at`  

---

### posts
Stores user-generated content, including replies and reposts.

**Fields**
- `id` (PK)  
- `author_id` (FK → users)  
- `content` (≤280 characters)  
- `reply_to_id` (FK → posts)  
- `repost_of_id` (FK → posts)  
- `style` (JSONB, optional formatting metadata)  
- `fact_check_status` (enum: verified, likely_true, disputed, false, unchecked)  
- `fact_check_score`  
- `fact_check_data` (JSON)  
- `was_checked_before`  
- `like_count`, `reply_count`, `repost_count`  
- `created_at`  

---

### likes
Tracks user interactions with posts.

**Fields**
- `user_id` (PK, FK → users)  
- `post_id` (PK, FK → posts)  
- `created_at`  

---

### follows
Tracks directed follow relationships between users.

**Fields**
- `follower_id` (PK, FK → users)  
- `following_id` (PK, FK → users)  
- `created_at`  

---

### debates
Structured, opt-in discussions between two users.

**Fields**
- `id` (PK)  
- `topic`  
- `challenger_id` (FK → users)  
- `defender_id` (FK → users)  
- `status` (enum: pending, active, voting, completed)  
- `current_round`  
- `whose_turn` (FK → users)  
- `winner_id` (FK → users)  
- `votes_challenger`, `votes_defender`, `votes_tie`  
- `voting_ends_at`  
- `created_at`  

---

### debate_arguments
Stores each user’s argument in a debate round.

**Fields**
- `id` (PK)  
- `debate_id` (FK → debates)  
- `user_id` (FK → users)  
- `round_number`  
- `content`  
- `fact_check_status`  
- `fact_check_score`  
- `fact_check_data` (JSON)  
- `created_at`  

---

### debate_votes
Tracks community voting for debates.

**Fields**
- `debate_id` (PK, FK → debates)  
- `user_id` (PK, FK → users)  
- `vote` (enum: challenger, defender, tie)  
- `created_at`  

---

### fact_checks
Stores AI verification results for posts or debate arguments.

**Fields**
- `id` (PK)  
- `post_id` (FK → posts, nullable)  
- `debate_arg_id` (FK → debate_arguments, nullable)  
- `requested_by` (FK → users)  
- `status`  
- `overall_score`  
- `claims` (JSON)  
- `created_at`  

---

## 3. AI Agent System

### Pre-Publish Fact Check
- Evaluates posts and debate arguments before publication  
- Outputs status, confidence score, and optional structured reasoning  
- Content can still be published, with AI warnings displayed  

### On-Demand Fact Check
- Evaluates existing posts or debate arguments upon request  
- Results stored in `fact_checks` for transparency and audit  

### Trust Score Agent
- Calculates heuristic trust score based on:  
  - Post fact-check outcomes  
  - Ratio of verified vs. false posts  
  - Debate wins/losses  
- Output displayed on user profiles  

---

## 4. Relationships Overview

| From / To | Type | Notes |
|------------|------|-------|
| users → posts | One-to-Many | Author creates posts |
| users ↔ users | Many-to-Many | Via follows table |
| users ↔ posts | Many-to-Many | Via likes table |
| posts → posts | Self-referential | reply_to_id, repost_of_id |
| users → debates | One-to-Many | Challenger, defender, winner |
| debates → debate_arguments | One-to-Many | Argument per round |
| debates → debate_votes | One-to-Many | Community votes |
| posts / debate_arguments → fact_checks | One-to-Many | AI verification |

---

## 5. Feed & Search

- **Home Feed:** Chronological list of posts from user and followed accounts  
- **Search:** Users (username, display_name), Posts (content, author, optional fact-check filter)  

---

## 6. Technology Stack

- **Frontend:** React, component-driven UI  
- **Backend:** Spring Boot, REST API endpoints  
- **Database:** PostgreSQL / MySQL / SQLite  
- **AI:** LLM-based agents via API  
- **Hosting:** Local or cloud-based development environment  

---

## 7. Security & Access Control
- No authentication in V1  
- Database integrity enforced via foreign keys and composite primary keys  
- AI outputs are visible to all users; no private user data exposed  