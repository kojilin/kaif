INSERT INTO ZoneInfo (zone, aliasname, theme, voteauthority, debateauthority, writeauthority,
                      adminaccountids, hidefromtop, createtime)
VALUES
  ('programming', '程設', 'z-theme-default', 'CITIZEN', 'CITIZEN', 'CITIZEN', '{}', FALSE, now());

INSERT INTO ZoneInfo (zone, aliasname, theme, voteauthority, debateauthority, writeauthority,
                      adminaccountids, hidefromtop, createtime)
VALUES
  ('kaif-faq', 'Kaif 常見問題', 'z-theme-kaif', 'CITIZEN', 'CITIZEN', 'FORBIDDEN', '{}', TRUE, now());