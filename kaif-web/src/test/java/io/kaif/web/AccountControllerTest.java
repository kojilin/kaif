package io.kaif.web;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Optional;

import org.junit.Test;

import io.kaif.flake.FlakeId;
import io.kaif.model.account.Account;
import io.kaif.model.account.AccountAccessToken;
import io.kaif.model.account.Authorization;
import io.kaif.model.article.Article;
import io.kaif.model.debate.Debate;
import io.kaif.model.feed.FeedAsset;
import io.kaif.model.zone.Zone;
import io.kaif.test.MvcIntegrationTests;

public class AccountControllerTest extends MvcIntegrationTests {

  @Test
  public void settingsPart() throws Exception {
    Account account = accountTourist("foo");
    String token = prepareAccessToken(account);

    when(accountService.findMe(isA(Authorization.class))).thenReturn(Optional.of(account));

    mockMvc.perform(get("/account/settings.part").header(AccountAccessToken.HEADER_KEY, token))
        .andExpect(view().name("account/settings.part"))
        .andExpect(content().string(containsString("foo@example.com")))
        .andExpect(content().string(containsString("foo")));
  }

  @Test
  public void debateReplies() throws Exception {
    mockMvc.perform(get("/account/debate-replies")).andExpect(containsDebateFormTemplate());
  }

  @Test
  public void newsFeed() throws Exception {
    mockMvc.perform(get("/account/news-feed")).andExpect(containsDebateFormTemplate());
  }

  @Test
  public void newsFeedPart() throws Exception {
    Account account = accountCitizen("bar111");
    String token = prepareAccessToken(account);

    Article article = article(Zone.valueOf("xyz123"), "title1");
    Debate d1 = debate(article, "reply 00001", null);
    Debate d2 = debate(article, "reply 00002", null);
    FeedAsset f1 = assetReply(d1);
    FeedAsset f2 = assetReply(d2);
    when(feedService.listFeeds(isA(Authorization.class), isNull(FlakeId.class))).thenReturn(asList(
        f1,
        f2));

    when(articleService.listDebatesById(asList(d1.getDebateId(), d2.getDebateId()))).thenReturn(
        asList(d1, d2));

    when(articleService.listArticlesByDebates(asList(d1.getDebateId(),
        d2.getDebateId()))).thenReturn(asList(article));
    mockMvc.perform(get("/account/news-feed.part").header(AccountAccessToken.HEADER_KEY, token))
        .andExpect(view().name("account/news-feed.part"))
        .andExpect(model().attribute("isFirstPage", true))
        .andExpect(content().string(containsString("reply 00001")))
        .andExpect(content().string(containsString("reply 00002")));
  }

  @Test
  public void newsFeedPart_paging() throws Exception {
    Account account = accountCitizen("bar111");
    String token = prepareAccessToken(account);
    Article article = article(Zone.valueOf("xyz123"), "title1");

    Debate d1 = debate(article, "reply 00001", null);
    FeedAsset f1 = assetReply(d1);
    FlakeId start = FlakeId.fromString("goodId");
    when(feedService.listFeeds(isA(Authorization.class), eq(start))).thenReturn(asList(f1));

    when(articleService.listDebatesById(asList(d1.getDebateId()))).thenReturn(asList(d1));

    when(articleService.listArticlesByDebates(asList(d1.getDebateId()))).thenReturn(asList(article));
    mockMvc.perform(get("/account/news-feed.part").param("startAssetId", "goodId")
        .header(AccountAccessToken.HEADER_KEY, token))
        .andExpect(view().name("account/news-feed.part"))
        .andExpect(model().attribute("isFirstPage", false));
  }

  @Test
  public void debateRepliesPart() throws Exception {
    Account account = accountCitizen("bar111");
    String token = prepareAccessToken(account);

    Article article = article(Zone.valueOf("xyz123"), "title1");
    Debate d1 = debate(article, "reply 00001", null);
    Debate d2 = debate(article, "reply 00002", null);
    when(articleService.listReplyToDebates(isA(Authorization.class),
        isNull(FlakeId.class))).thenReturn(asList(d1, d2));

    when(articleService.listArticlesByDebates(asList(d1.getDebateId(),
        d2.getDebateId()))).thenReturn(asList(article));
    mockMvc.perform(get("/account/debate-replies.part").header(AccountAccessToken.HEADER_KEY,
        token))
        .andExpect(view().name("article/debate-replies.part"))
        .andExpect(content().string(containsString("reply 00001")))
        .andExpect(content().string(containsString("reply 00002")));
  }

  @Test
  public void activation() throws Exception {
    when(accountService.activate("abc")).thenReturn(true);

    mockMvc.perform(get("/account/activation?key=abc"))
        .andExpect(view().name("account/activation"))
        .andExpect(model().attribute("success", true))
        .andExpect(cookie().secure("force-logout", true))
        .andExpect(cookie().path("force-logout", "/"))
        .andExpect(cookie().value("force-logout", "true"));
  }
}