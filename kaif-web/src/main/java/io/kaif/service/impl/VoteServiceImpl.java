package io.kaif.service.impl;

import java.time.Instant;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.kaif.flake.FlakeId;
import io.kaif.model.account.Authorization;
import io.kaif.model.article.ArticleDao;
import io.kaif.model.vote.ArticleVoter;
import io.kaif.model.vote.VoteDao;
import io.kaif.model.vote.VoteDelta;
import io.kaif.model.zone.Zone;
import io.kaif.model.zone.ZoneDao;
import io.kaif.service.VoteService;
import io.kaif.web.support.AccessDeniedException;

@Service
@Transactional
public class VoteServiceImpl implements VoteService {

  @Autowired
  private ArticleDao articleDao;

  @Autowired
  private VoteDao voteDao;

  @Autowired
  private ZoneDao zoneDao;

  @Override
  public void upVoteArticle(Zone zone,
      FlakeId articleId,
      Authorization authorization,
      long previousCount) {

    checkVoteAuthority(zone, authorization);

    VoteDelta voteDelta = voteDao.upVoteArticle(articleId,
        authorization.authenticatedId(),
        previousCount,
        Instant.now());
    articleDao.changeUpVote(zone, articleId, voteDelta.getChangedValue());
  }

  private void checkVoteAuthority(Zone zone, Authorization authorization) {
    //relax article up vote verification, no check zone and account in Database
    if (!zoneDao.loadZone(zone).canUpVote(authorization)) {
      throw new AccessDeniedException("not allow vote in zone: " + zone + " auth:" + authorization);
    }
  }

  @Override
  public List<ArticleVoter> listArticleVotersInRage(Authorization authorization,
      FlakeId startArticleId,
      FlakeId endArticleId) {
    return voteDao.listArticleVotersInRage(authorization.authenticatedId(),
        startArticleId,
        endArticleId);
  }

  @Override
  public void cancelVoteArticle(Zone zone, FlakeId articleId, Authorization authorization) {

    checkVoteAuthority(zone, authorization);

    VoteDelta voteDelta = voteDao.cancelVoteArticle(articleId,
        authorization.authenticatedId(),
        Instant.now());
    articleDao.changeUpVote(zone, articleId, voteDelta.getChangedValue());
  }
}
