# One-Pager (V1)

## Product Vision
Veritas is an AI-augmented social media platform that allows users to post short messages, interact in threaded conversations, and participate in structured debates. AI agents provide fact-checking, credibility assessment, and trust scoring, helping users navigate information reliability while fostering engagement and meaningful discussion.

The goal is to integrate social interaction and AI-supported truth verification in a seamless, user-centric platform.

---

## Core Problems This Solves
- Traditional social media platforms prioritize speed and virality over accuracy  
- Users lack tools to quickly verify information in posts or debates  
- Credibility and trust metrics are often opaque or gamified  
- Structured, moderated debates are uncommon on mainstream platforms  

---

## Key Concepts

### Users
- Each user has:
  - Username, display name, avatar URL, and bio  
  - AI-derived trust score and counters for verified vs. false posts  
  - Debate win/loss records  
- Users are the primary actors in posting, liking, following, and debating

### Posts
- Short-form messages (≤280 characters) authored by users  
- Support threaded replies and optional reposting  
- Include AI-driven fact-check status and score  
- Optional style metadata (JSONB) for demonstration purposes  
- Track likes, replies, and repost counts

### Likes & Follows
- Users can like posts (one like per user per post)  
- Follow relationships are directional, enabling a personalized feed

### Debates
- Opt-in, structured, text-only discussions between two users  
- Three-round format with alternating turns  
- Community voting determines a winner  
- Arguments are stored and fact-checked separately  
- Voting tracks support for challenger, defender, or tie

### AI Agents
- **Pre-Publish Fact Check:** Evaluates posts or debate arguments before publication  
- **On-Demand Fact Check:** Users can request verification on any post or debate argument  
- **Trust Score Agent:** Aggregates fact-check results and debate performance to calculate a heuristic score

### Feed & Search
- Chronological home feed showing user and followed posts  
- Search by users (username/display name) or posts (content, author, fact-check status)

---

## Guiding Principles
- **Transparency:** AI-driven trust scores and fact checks are clearly displayed  
- **Structured Interaction:** Debates and threads provide organized, meaningful conversation  
- **User Control:** Users retain autonomy to post content even if flagged by AI  
- **Simplicity:** Core social functions remain lightweight and intuitive  

---

## Non-Goals (V1)
- Real authentication or user verification  
- Media uploads (images, videos)  
- Notifications, direct messaging, or bookmarks  
- Hashtags, polls, or post editing  
- Moderation or abuse prevention  
- Cross-user templates or complex analytics  

---

## Success Criteria
The product succeeds if:
- Users can post, reply, and interact fluidly with minimal friction  
- AI fact-checking and trust scoring are visible and understandable  
- Debates function correctly, with structured rounds and community voting  
- Core social interactions (likes, follows, reposts) operate reliably  
- Stakeholders can observe AI-augmented features in action during demonstration