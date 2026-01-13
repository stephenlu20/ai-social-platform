-- Seed data for Veritas demo users
-- Run this after the app creates the tables

-- Clear existing data (optional, comment out if you want to keep existing data)
DELETE FROM follows;
DELETE FROM users;

-- Insert 8 demo users with varied trust scores and stats
INSERT INTO users (id, username, display_name, bio, avatar_url, trust_score, posts_fact_checked, posts_verified, posts_false, debates_won, debates_lost, created_at)
VALUES
    -- High trust users
    ('550e8400-e29b-41d4-a716-446655440001', 'scientist', 'Dr. Sarah Chen', 'Climate researcher at MIT. Facts matter. 🔬', 'https://api.dicebear.com/7.x/avataaars/svg?seed=scientist', 92.00, 25, 21, 2, 8, 2, datetime('now', '-30 days')),
    
    ('550e8400-e29b-41d4-a716-446655440002', 'journalist', 'Mike Thompson', 'Investigative journalist. 15 years at NYT. Seeking truth.', 'https://api.dicebear.com/7.x/avataaars/svg?seed=journalist', 78.00, 40, 18, 6, 5, 4, datetime('now', '-25 days')),
    
    ('550e8400-e29b-41d4-a716-446655440007', 'historian', 'Dr. Emily Park', 'History professor. Context is everything. 📚', 'https://api.dicebear.com/7.x/avataaars/svg?seed=historian', 85.00, 18, 15, 1, 6, 1, datetime('now', '-28 days')),
    
    -- Medium trust users
    ('550e8400-e29b-41d4-a716-446655440003', 'techie', 'Alex Rivera', 'Software engineer & tech blogger. Hot takes on AI.', 'https://api.dicebear.com/7.x/avataaars/svg?seed=techie', 65.00, 15, 10, 3, 3, 5, datetime('now', '-20 days')),
    
    ('550e8400-e29b-41d4-a716-446655440004', 'skeptic', 'Jordan Hayes', 'Professional skeptic. Question everything. 🤔', 'https://api.dicebear.com/7.x/avataaars/svg?seed=skeptic', 55.00, 30, 8, 5, 12, 8, datetime('now', '-15 days')),
    
    ('550e8400-e29b-41d4-a716-446655440008', 'analyst', 'Sam Nakamura', 'Data analyst. Numbers dont lie, but people do. 📊', 'https://api.dicebear.com/7.x/avataaars/svg?seed=analyst', 70.00, 22, 12, 4, 4, 3, datetime('now', '-18 days')),
    
    -- Lower trust users
    ('550e8400-e29b-41d4-a716-446655440005', 'newbie', 'Pat Morrison', 'Just joined! Learning the ropes.', 'https://api.dicebear.com/7.x/avataaars/svg?seed=newbie', 50.00, 2, 0, 0, 0, 1, datetime('now', '-5 days')),
    
    ('550e8400-e29b-41d4-a716-446655440006', 'contrarian', 'Chris Watts', 'Playing devils advocate since 1985. Debate me!', 'https://api.dicebear.com/7.x/avataaars/svg?seed=contrarian', 35.00, 20, 5, 8, 4, 10, datetime('now', '-10 days'));

-- Insert follow relationships
-- Format: follower follows following
INSERT INTO follows (id, follower_id, following_id, created_at)
VALUES
    -- journalist follows scientist
    ('660e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', datetime('now', '-20 days')),
    -- techie follows scientist
    ('660e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', datetime('now', '-18 days')),
    -- newbie follows scientist
    ('660e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440001', datetime('now', '-3 days')),
    -- historian follows scientist
    ('660e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440001', datetime('now', '-25 days')),
    
    -- scientist follows journalist
    ('660e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440002', datetime('now', '-22 days')),
    -- skeptic follows journalist
    ('660e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440002', datetime('now', '-12 days')),
    -- analyst follows journalist
    ('660e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440002', datetime('now', '-15 days')),
    
    -- contrarian follows techie
    ('660e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440003', datetime('now', '-8 days')),
    
    -- techie follows skeptic
    ('660e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440004', datetime('now', '-10 days')),
    -- contrarian follows skeptic
    ('660e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440004', datetime('now', '-7 days')),
    
    -- contrarian follows scientist
    ('660e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', datetime('now', '-6 days')),
    -- contrarian follows journalist
    ('660e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440002', datetime('now', '-5 days')),
    
    -- scientist follows historian
    ('660e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440007', datetime('now', '-24 days')),
    -- journalist follows historian
    ('660e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440007', datetime('now', '-23 days')),
    
    -- analyst follows scientist
    ('660e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440001', datetime('now', '-16 days')),
    -- analyst follows historian
    ('660e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440007', datetime('now', '-14 days')),
    -- techie follows analyst
    ('660e8400-e29b-41d4-a716-446655440017', '550e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440008', datetime('now', '-12 days'));

-- Summary:
-- scientist:   5 followers (journalist, techie, newbie, historian, analyst), follows 2 (journalist, historian)
-- journalist:  3 followers (scientist, skeptic, analyst), follows 2 (scientist, historian)
-- historian:   3 followers (scientist, journalist, analyst), follows 1 (scientist)
-- techie:      1 follower (contrarian), follows 3 (scientist, skeptic, analyst)
-- skeptic:     2 followers (techie, contrarian), follows 1 (journalist)
-- analyst:     1 follower (techie), follows 3 (journalist, scientist, historian)
-- newbie:      0 followers, follows 1 (scientist)
-- contrarian:  0 followers, follows 4 (scientist, journalist, techie, skeptic)

-- ============================================================================
-- POSTS (50 total)
-- ============================================================================

DELETE FROM posts;

INSERT INTO posts (id, author_id, content, reply_to_id, repost_of_id, style, fact_check_status, fact_check_score, was_checked_before, like_count, reply_count, repost_count, created_at)
VALUES
    -- ========================================
    -- SCIENTIST (8 posts) - mostly verified
    -- ========================================
    ('770e8400-e29b-41d4-a716-446655440001', '550e8400-e29b-41d4-a716-446655440001', 
     'New study confirms global temperatures rose 1.1°C since pre-industrial levels. The data is clear.', 
     NULL, NULL, NULL, 'VERIFIED', 0.95, 1, 45, 12, 8, datetime('now', '-29 days')),
    
    ('770e8400-e29b-41d4-a716-446655440002', '550e8400-e29b-41d4-a716-446655440001', 
     'Peer-reviewed research shows Arctic ice loss accelerating at 13% per decade. This matches satellite observations.', 
     NULL, NULL, NULL, 'VERIFIED', 0.92, 1, 38, 8, 5, datetime('now', '-25 days')),
    
    ('770e8400-e29b-41d4-a716-446655440003', '550e8400-e29b-41d4-a716-446655440001', 
     'Common misconception: weather is not climate. A cold winter doesn''t disprove warming trends.', 
     NULL, NULL, NULL, 'VERIFIED', 0.98, 1, 52, 15, 12, datetime('now', '-20 days')),
    
    ('770e8400-e29b-41d4-a716-446655440004', '550e8400-e29b-41d4-a716-446655440001', 
     'Our lab just published findings on ocean acidification. pH levels dropped 0.1 units - that''s a 30% increase in acidity.', 
     NULL, NULL, NULL, 'VERIFIED', 0.94, 1, 29, 6, 4, datetime('now', '-15 days')),
    
    ('770e8400-e29b-41d4-a716-446655440005', '550e8400-e29b-41d4-a716-446655440001', 
     'Excited to present at AGU next month. Will be sharing 5 years of temperature anomaly data.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 18, 3, 1, datetime('now', '-10 days')),
    
    ('770e8400-e29b-41d4-a716-446655440006', '550e8400-e29b-41d4-a716-446655440001', 
     'For those asking: yes, renewable energy CAN meet global demand. See IPCC report section 4.3.', 
     NULL, NULL, NULL, 'LIKELY_TRUE', 0.82, 1, 33, 9, 6, datetime('now', '-7 days')),
    
    ('770e8400-e29b-41d4-a716-446655440007', '550e8400-e29b-41d4-a716-446655440001', 
     'Thread: Let me explain the greenhouse effect in simple terms... 🧵', 
     NULL, NULL, NULL, 'VERIFIED', 0.99, 1, 67, 4, 15, datetime('now', '-5 days')),
    
    ('770e8400-e29b-41d4-a716-446655440008', '550e8400-e29b-41d4-a716-446655440001', 
     'Coffee is essential for science. This is my most verified claim. ☕', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 89, 22, 3, datetime('now', '-1 days')),

    -- ========================================
    -- JOURNALIST (8 posts) - mix verified/likely true
    -- ========================================
    ('770e8400-e29b-41d4-a716-446655440009', '550e8400-e29b-41d4-a716-446655440002', 
     'BREAKING: City council votes 7-2 to approve new transit funding. $2.3B over 10 years.', 
     NULL, NULL, NULL, 'VERIFIED', 0.97, 1, 56, 18, 22, datetime('now', '-24 days')),
    
    ('770e8400-e29b-41d4-a716-446655440010', '550e8400-e29b-41d4-a716-446655440002', 
     'Investigation update: Documents show company knew about safety issues 3 years before recall.', 
     NULL, NULL, NULL, 'VERIFIED', 0.91, 1, 78, 25, 31, datetime('now', '-22 days')),
    
    ('770e8400-e29b-41d4-a716-446655440011', '550e8400-e29b-41d4-a716-446655440002', 
     'Sources tell me the merger announcement is coming next week. Still confirming details.', 
     NULL, NULL, NULL, 'LIKELY_TRUE', 0.75, 1, 42, 14, 8, datetime('now', '-18 days')),
    
    ('770e8400-e29b-41d4-a716-446655440012', '550e8400-e29b-41d4-a716-446655440002', 
     'Fact check: No, the new policy does NOT ban all imports. Here''s what it actually says...', 
     NULL, NULL, NULL, 'VERIFIED', 0.96, 1, 61, 8, 19, datetime('now', '-14 days')),
    
    ('770e8400-e29b-41d4-a716-446655440013', '550e8400-e29b-41d4-a716-446655440002', 
     'Interview with the whistleblower dropping tomorrow. 15 years of silence, finally speaking out.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 93, 31, 12, datetime('now', '-11 days')),
    
    ('770e8400-e29b-41d4-a716-446655440014', '550e8400-e29b-41d4-a716-446655440002', 
     'Unemployment figures out: 3.7% nationally, down from 3.9%. Regional breakdown in thread.', 
     NULL, NULL, NULL, 'VERIFIED', 0.99, 1, 37, 5, 14, datetime('now', '-8 days')),
    
    ('770e8400-e29b-41d4-a716-446655440015', '550e8400-e29b-41d4-a716-446655440002', 
     'Always verify before sharing. I''ve seen 3 fake screenshots today alone. Check your sources.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 112, 19, 45, datetime('now', '-4 days')),
    
    ('770e8400-e29b-41d4-a716-446655440016', '550e8400-e29b-41d4-a716-446655440002', 
     'After 15 years in journalism: the truth is usually boring. Exciting claims need extra scrutiny.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 156, 28, 52, datetime('now', '-2 days')),

    -- ========================================
    -- HISTORIAN (6 posts) - mostly verified
    -- ========================================
    ('770e8400-e29b-41d4-a716-446655440017', '550e8400-e29b-41d4-a716-446655440007', 
     'Actually, the Great Wall of China is NOT visible from space with the naked eye. Common myth debunked by astronauts.', 
     NULL, NULL, NULL, 'VERIFIED', 0.97, 1, 73, 12, 28, datetime('now', '-27 days')),
    
    ('770e8400-e29b-41d4-a716-446655440018', '550e8400-e29b-41d4-a716-446655440007', 
     'Napoleon was not short. At 5''7" he was average height for his era. British propaganda created the myth.', 
     NULL, NULL, NULL, 'VERIFIED', 0.95, 1, 89, 15, 35, datetime('now', '-23 days')),
    
    ('770e8400-e29b-41d4-a716-446655440019', '550e8400-e29b-41d4-a716-446655440007', 
     'The Library of Alexandria wasn''t destroyed in one event. It declined over centuries due to budget cuts and neglect.', 
     NULL, NULL, NULL, 'VERIFIED', 0.88, 1, 64, 21, 18, datetime('now', '-19 days')),
    
    ('770e8400-e29b-41d4-a716-446655440020', '550e8400-e29b-41d4-a716-446655440007', 
     'History doesn''t repeat, but it rhymes. Current economic patterns mirror 1920s more than people realize.', 
     NULL, NULL, NULL, 'LIKELY_TRUE', 0.72, 1, 48, 33, 11, datetime('now', '-13 days')),
    
    ('770e8400-e29b-41d4-a716-446655440021', '550e8400-e29b-41d4-a716-446655440007', 
     'Medieval people DID bathe. The "dirty middle ages" is a Renaissance-era smear campaign.', 
     NULL, NULL, NULL, 'VERIFIED', 0.91, 1, 95, 8, 42, datetime('now', '-6 days')),
    
    ('770e8400-e29b-41d4-a716-446655440022', '550e8400-e29b-41d4-a716-446655440007', 
     'Reading primary sources this weekend. Nothing humbles you like handwriting from 500 years ago.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 34, 7, 2, datetime('now', '-3 days')),

    -- ========================================
    -- ANALYST (6 posts) - data-focused
    -- ========================================
    ('770e8400-e29b-41d4-a716-446655440023', '550e8400-e29b-41d4-a716-446655440008', 
     'Ran the numbers on housing prices: median home now costs 5.8x median income. In 1980 it was 3.1x.', 
     NULL, NULL, NULL, 'VERIFIED', 0.94, 1, 87, 29, 33, datetime('now', '-17 days')),
    
    ('770e8400-e29b-41d4-a716-446655440024', '550e8400-e29b-41d4-a716-446655440008', 
     'That viral chart is misleading. Y-axis starts at 50%, making a 2% change look massive. Always check the axes.', 
     NULL, NULL, NULL, 'VERIFIED', 0.96, 1, 124, 18, 56, datetime('now', '-14 days')),
    
    ('770e8400-e29b-41d4-a716-446655440025', '550e8400-e29b-41d4-a716-446655440008', 
     'Correlation: 0.89 between screen time and reported anxiety in teens. But correlation ≠ causation. Need more research.', 
     NULL, NULL, NULL, 'LIKELY_TRUE', 0.78, 1, 56, 24, 12, datetime('now', '-11 days')),
    
    ('770e8400-e29b-41d4-a716-446655440026', '550e8400-e29b-41d4-a716-446655440008', 
     'Sample size matters! That study everyone''s sharing? n=47. Draw your own conclusions.', 
     NULL, NULL, NULL, 'VERIFIED', 0.92, 1, 67, 11, 23, datetime('now', '-8 days')),
    
    ('770e8400-e29b-41d4-a716-446655440027', '550e8400-e29b-41d4-a716-446655440008', 
     'Built a model predicting Q3 earnings. 73% accuracy so far. Sharing methodology in comments.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 29, 15, 4, datetime('now', '-5 days')),
    
    ('770e8400-e29b-41d4-a716-446655440028', '550e8400-e29b-41d4-a716-446655440008', 
     'Data viz tip: if you can''t explain your chart in one sentence, it''s too complicated.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 78, 6, 31, datetime('now', '-2 days')),

    -- ========================================
    -- TECHIE (7 posts) - tech opinions, some disputed
    -- ========================================
    ('770e8400-e29b-41d4-a716-446655440029', '550e8400-e29b-41d4-a716-446655440003', 
     'Hot take: AI will replace 40% of jobs within 10 years. The automation wave is just starting.', 
     NULL, NULL, NULL, 'DISPUTED', 0.45, 1, 134, 67, 28, datetime('now', '-19 days')),
    
    ('770e8400-e29b-41d4-a716-446655440030', '550e8400-e29b-41d4-a716-446655440003', 
     'Just tested GPT-5. It passed the Turing test in my completely unscientific experiment.', 
     NULL, NULL, NULL, 'LIKELY_TRUE', 0.65, 1, 89, 43, 15, datetime('now', '-16 days')),
    
    ('770e8400-e29b-41d4-a716-446655440031', '550e8400-e29b-41d4-a716-446655440003', 
     'Unpopular opinion: Most blockchain projects are solutions looking for problems.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 156, 82, 19, datetime('now', '-13 days')),
    
    ('770e8400-e29b-41d4-a716-446655440032', '550e8400-e29b-41d4-a716-446655440003', 
     'Moore''s Law isn''t dead, it''s just moved to specialized chips. Look at TPU performance curves.', 
     NULL, NULL, NULL, 'LIKELY_TRUE', 0.71, 1, 45, 18, 9, datetime('now', '-10 days')),
    
    ('770e8400-e29b-41d4-a716-446655440033', '550e8400-e29b-41d4-a716-446655440003', 
     'The average app has 17 trackers. Your phone knows more about you than your therapist.', 
     NULL, NULL, NULL, 'VERIFIED', 0.87, 1, 201, 34, 78, datetime('now', '-7 days')),
    
    ('770e8400-e29b-41d4-a716-446655440034', '550e8400-e29b-41d4-a716-446655440003', 
     'Quantum computing will break current encryption within 5 years. Banks are not ready.', 
     NULL, NULL, NULL, 'DISPUTED', 0.38, 1, 78, 51, 12, datetime('now', '-4 days')),
    
    ('770e8400-e29b-41d4-a716-446655440035', '550e8400-e29b-41d4-a716-446655440003', 
     'Code review tip: if you can''t explain why the code works, you don''t understand it well enough.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 234, 12, 89, datetime('now', '-1 days')),

    -- ========================================
    -- SKEPTIC (6 posts) - questions everything
    -- ========================================
    ('770e8400-e29b-41d4-a716-446655440036', '550e8400-e29b-41d4-a716-446655440004', 
     'Why does every new study contradict the last one? Maybe we should wait for replication before sharing.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 89, 45, 11, datetime('now', '-14 days')),
    
    ('770e8400-e29b-41d4-a716-446655440037', '550e8400-e29b-41d4-a716-446655440004', 
     'That "95% of scientists agree" stat? I looked up the original study. The methodology is... questionable.', 
     NULL, NULL, NULL, 'DISPUTED', 0.52, 1, 67, 89, 8, datetime('now', '-12 days')),
    
    ('770e8400-e29b-41d4-a716-446655440038', '550e8400-e29b-41d4-a716-446655440004', 
     'I''m not saying the experts are wrong. I''m saying blind trust in experts is also wrong.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 145, 62, 23, datetime('now', '-9 days')),
    
    ('770e8400-e29b-41d4-a716-446655440039', '550e8400-e29b-41d4-a716-446655440004', 
     'Follow the funding. Who paid for that research? Always relevant context.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 78, 29, 14, datetime('now', '-6 days')),
    
    ('770e8400-e29b-41d4-a716-446655440040', '550e8400-e29b-41d4-a716-446655440004', 
     'Prediction markets are more accurate than expert panels. The data supports this consistently.', 
     NULL, NULL, NULL, 'LIKELY_TRUE', 0.76, 1, 56, 33, 9, datetime('now', '-3 days')),
    
    ('770e8400-e29b-41d4-a716-446655440041', '550e8400-e29b-41d4-a716-446655440004', 
     'Changed my mind on vaccine efficacy after reviewing the phase 3 data. Being wrong is fine. Staying wrong isn''t.', 
     NULL, NULL, NULL, 'VERIFIED', 0.89, 1, 178, 24, 45, datetime('now', '-1 days')),

    -- ========================================
    -- NEWBIE (3 posts) - learning
    -- ========================================
    ('770e8400-e29b-41d4-a716-446655440042', '550e8400-e29b-41d4-a716-446655440005', 
     'Just joined this platform! Excited to learn how fact-checking works. Any tips?', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 23, 8, 0, datetime('now', '-4 days')),
    
    ('770e8400-e29b-41d4-a716-446655440043', '550e8400-e29b-41d4-a716-446655440005', 
     'I heard that goldfish have 3-second memories. Is that true or a myth?', 
     NULL, NULL, NULL, 'FALSE', 0.12, 1, 45, 12, 2, datetime('now', '-3 days')),
    
    ('770e8400-e29b-41d4-a716-446655440044', '550e8400-e29b-41d4-a716-446655440005', 
     'TIL goldfish actually have memories lasting months! Thanks for the corrections everyone. This platform is great!', 
     NULL, NULL, NULL, 'VERIFIED', 0.94, 1, 67, 5, 8, datetime('now', '-2 days')),

    -- ========================================
    -- CONTRARIAN (6 posts) - hot takes, some false
    -- ========================================
    ('770e8400-e29b-41d4-a716-446655440045', '550e8400-e29b-41d4-a716-446655440006', 
     'The moon landing was real but the footage was faked because the real footage was too boring.', 
     NULL, NULL, NULL, 'FALSE', 0.08, 1, 34, 89, 5, datetime('now', '-9 days')),
    
    ('770e8400-e29b-41d4-a716-446655440046', '550e8400-e29b-41d4-a716-446655440006', 
     'Hot take: Most "healthy eating" advice will be debunked in 20 years. Remember when fat was evil?', 
     NULL, NULL, NULL, 'LIKELY_TRUE', 0.68, 1, 123, 56, 18, datetime('now', '-7 days')),
    
    ('770e8400-e29b-41d4-a716-446655440047', '550e8400-e29b-41d4-a716-446655440006', 
     'Diamonds are worthless. The entire market is artificial scarcity created by De Beers.', 
     NULL, NULL, NULL, 'DISPUTED', 0.55, 1, 89, 67, 12, datetime('now', '-5 days')),
    
    ('770e8400-e29b-41d4-a716-446655440048', '550e8400-e29b-41d4-a716-446655440006', 
     'We only use 10% of our brains. I saw it in a documentary so it must be true. 🧠', 
     NULL, NULL, NULL, 'FALSE', 0.05, 1, 12, 78, 3, datetime('now', '-4 days')),
    
    ('770e8400-e29b-41d4-a716-446655440049', '550e8400-e29b-41d4-a716-446655440006', 
     'Debate me: pineapple on pizza is actually the optimal topping combination. I have data.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 267, 134, 23, datetime('now', '-2 days')),
    
    ('770e8400-e29b-41d4-a716-446655440050', '550e8400-e29b-41d4-a716-446655440006', 
     'Fine, I was wrong about the 10% brain thing. But I stand by my pizza opinions.', 
     NULL, NULL, NULL, 'UNCHECKED', NULL, 0, 89, 23, 7, datetime('now', '-1 days'));

-- ============================================================================
-- POST SUMMARY
-- ============================================================================
-- Total: 50 posts
--
-- By User:
--   scientist:   8 posts
--   journalist:  8 posts
--   historian:   6 posts
--   analyst:     6 posts
--   techie:      7 posts
--   skeptic:     6 posts
--   newbie:      3 posts
--   contrarian:  6 posts
--
-- By Fact-Check Status:
--   VERIFIED:    19 posts
--   LIKELY_TRUE:  8 posts
--   DISPUTED:     5 posts
--   FALSE:        3 posts
--   UNCHECKED:   15 posts