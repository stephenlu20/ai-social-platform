# Candor

An AI-augmented micro-social platform for short-form discussion, fact-checking, and structured debate.

Candor is a Twitter-style social media application built to explore how AI agents can augment trust, credibility, and discourse in online conversations. Users can post short messages, reply in threads, participate in structured debates, and view AI-generated fact-checks and trust signals, while retaining full control over what they publish.

This project is intentionally scoped as a V1 demonstration platform: lightweight, readable, and focused on core social interactions plus AI-powered insights.

---

## Core Features

### Posts and Threads

* Short-form posts (up to 280 characters)
* Threaded replies
* Optional reposting
* Like counts, reply counts, and repost counts

### Social Graph

* Follow other users (directional)
* Personalized home feed (user and followed accounts)

### AI Fact-Checking

* Pre-publish checks on posts and debate arguments
* On-demand fact checks requested by users
* Visible fact-check status and confidence scores
* Historical fact-check records for transparency

### Structured Debates

* Opt-in, text-only debates between two users
* Three-round format with alternating turns
* Community voting (challenger, defender, or tie)
* AI fact-checking applied to individual arguments

### Trust Scoring

* AI-derived trust score displayed on user profiles
* Based on verified versus false posts
* Incorporates fact-check outcomes and debate performance

---

## System Architecture

```
React Frontend  ->  Spring Boot REST API  ->  SQL Database
```

* Frontend: React (component-driven UI)
* Backend: Spring Boot (REST API, business logic, AI orchestration)
* Database: PostgreSQL, MySQL, or SQLite
* AI: LLM-based agents via external API

---

## Data Model Overview

Key entities include:

* Users: profiles, trust scores, and activity counters
* Posts: short-form content, replies, reposts, and fact-check metadata
* Likes and Follows: social interactions and graph relationships
* Debates: structured discussions with rounds and voting
* Debate Arguments: per-round arguments with independent fact checks
* Fact Checks: persistent AI verification results for auditability

Relational integrity is enforced via foreign keys and composite primary keys.

---

## Feed and Search

* Home Feed: Chronological posts from the user and followed accounts
* Search:

  * Users by username or display name
  * Posts by content, author, or fact-check status

---

## Non-Goals (V1)

The following features are intentionally excluded to keep scope focused:

* Authentication or user verification
* Media uploads (images, video, audio)
* Notifications, direct messaging, or bookmarks
* Hashtags, polls, or post editing
* Moderation, abuse prevention, or reporting
* Advanced analytics or recommendations

---

## Security and Access Control

* No authentication in V1
* All data is public and visible
* Database integrity enforced at the schema level
* AI outputs are transparent and non-authoritative

---

## Project Goals

This project is successful if:

* Core social interactions feel fast and intuitive
* AI fact-checking and trust scores are clearly understandable
* Debates function correctly with structured rounds and voting
* The platform effectively demonstrates AI-augmented social features

---

## Technology Stack

| Layer    | Technology                       |
| -------- | -------------------------------- |
| Frontend | React                            |
| Backend  | Spring Boot                      |
| Database | PostgreSQL, MySQL, SQLite        |
| AI       | LLM-based agents via API         |
| Hosting  | Local or cloud-based development |

---

## Getting Started

Setup instructions may vary depending on environment.

1. Clone the repository
2. Start the Spring Boot backend
3. Start the React frontend
4. Access the application locally

---

## Disclaimer

Candor is a demonstration and learning project. AI-generated fact checks and trust scores are heuristic and should not be interpreted as authoritative judgments.

---

## License

MIT License (or project-specific license)
