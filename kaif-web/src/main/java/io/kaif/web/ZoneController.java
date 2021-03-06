package io.kaif.web;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import io.kaif.flake.FlakeId;
import io.kaif.model.article.Article;
import io.kaif.model.article.ArticleList;
import io.kaif.model.debate.Debate;
import io.kaif.model.debate.DebateList;
import io.kaif.model.zone.Zone;
import io.kaif.model.zone.ZoneInfo;
import io.kaif.service.ArticleService;
import io.kaif.service.HonorRollService;
import io.kaif.service.ZoneService;

@Controller
@RequestMapping("/z")
public class ZoneController {

  static class ZonePageModelView extends ModelAndView {
    public ZonePageModelView(ZoneInfo zoneInfo, ZoneService zoneService) {
      super("/zone/zone-page");
      addObject("zoneInfo", zoneInfo);
      addObject("recommendZones", zoneService.listRecommendZones());
      addObject("administrators", zoneService.listAdministratorsWithCache(zoneInfo.getZone()));
    }
  }
  @Autowired
  private ZoneService zoneService;
  @Autowired
  private ArticleService articleService;
  @Autowired
  private HonorRollService honorRollService;

  @RequestMapping("/{zone}")
  public Object hotArticles(@PathVariable("zone") String rawZone,
      @RequestParam(value = "start", required = false) FlakeId startArticleId,
      HttpServletRequest request) throws IOException {
    return resolveZone(request, rawZone, zoneInfo -> {
      return new ZonePageModelView(zoneInfo, zoneService).addObject("articleList",
          new ArticleList(articleService.listHotZoneArticles(zoneInfo.getZone(), startArticleId)));
    });
  }

  @RequestMapping("/{zone}/hot.rss")
  public Object rssFeed(@PathVariable("zone") String rawZone, HttpServletRequest request) {
    return resolveZone(request, rawZone, zoneInfo -> {
      request.getRequestURL();
      ModelAndView modelAndView = new ModelAndView().addObject("zoneInfo", zoneInfo)
          .addObject("articles",
              articleService.listRssHotZoneArticlesWithCache(zoneInfo.getZone()));
      modelAndView.setView(new HotArticleRssContentView());
      return modelAndView;
    });
  }

  private Object resolveZone(HttpServletRequest request,
      String decodedRawZone,
      Function<ZoneInfo, ModelAndView> onZoneInfo) {
    // note that decodedRawZone already do http url decode, and PathVariable already trim()
    // space of value
    return Zone.tryFallback(decodedRawZone).map(zone -> {
      if (!zone.value().equals(decodedRawZone)) {
        String orgUrl = request.getRequestURL().toString();
        // replace pattern is combine of fallback pattern and valid pattern
        // TODO refactor replace rule to Zone
        String location = orgUrl.replaceFirst("/z/[a-zA-Z0-9_\\-]+", "/z/" + zone);
        //check if fallback success, this prevent infinite redirect loop
        if (!location.equals(orgUrl)) {
          RedirectView redirectView = new RedirectView(location);
          redirectView.setPropagateQueryParams(true);
          redirectView.setExpandUriTemplateVariables(false);
          redirectView.setExposeModelAttributes(false);
          redirectView.setExposeContextBeansAsAttributes(false);
          redirectView.setExposePathVariables(false);
          redirectView.setContextRelative(true);
          redirectView.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
          return redirectView;
        }
      }
      return onZoneInfo.apply(zoneService.loadZone(zone));
    }).orElseThrow(() -> new EmptyResultDataAccessException("no such zone: " + decodedRawZone, 1));

  }

  @RequestMapping("/{zone}/new")
  public Object newArticles(@PathVariable("zone") String rawZone,
      @RequestParam(value = "start", required = false) FlakeId startArticleId,
      HttpServletRequest request) {
    return resolveZone(request, rawZone, zoneInfo -> {
      return new ZonePageModelView(zoneInfo, zoneService).addObject("articleList",
          new ArticleList(articleService.listLatestZoneArticles(zoneInfo.getZone(),
              startArticleId)));
    });
  }

  @RequestMapping("/{zone}/new-debate")
  public Object newDebates(@PathVariable("zone") String rawZone,
      @RequestParam(value = "start", required = false) FlakeId startDebateId,
      HttpServletRequest request) {
    return resolveZone(request, rawZone, zoneInfo -> {
      List<Debate> debates = articleService.listLatestZoneDebates(zoneInfo.getZone(),
          startDebateId);
      List<Article> articles = articleService.listArticlesByDebatesWithCache(debates.stream()
          .map(Debate::getDebateId)
          .collect(Collectors.toList()));
      return new ZonePageModelView(zoneInfo, zoneService).addObject("debateList",
          new DebateList(debates, articles));
    });
  }

  @RequestMapping({ "/{zone}/article/create-link", "/{zone}/article/create-speak" })
  public Object createArticle(@PathVariable("zone") String rawZone, HttpServletRequest request) {
    return resolveZone(request,
        rawZone,
        zoneInfo -> new CreateArticleModelAndView(zoneService, zoneInfo));
  }

  @RequestMapping("/{zone}/honor")
  public Object zoneHonors(@PathVariable("zone") String rawZone, HttpServletRequest request) {
    return resolveZone(request,
        rawZone,
        zoneInfo -> new ZonePageModelView(zoneInfo, zoneService).addObject("honorRolls",
            honorRollService.listHonorRollsByZone(zoneInfo.getZone())));
  }

  @RequestMapping("/{zone}/debates/{articleId}")
  public Object articleDebates(@PathVariable("zone") String rawZone,
      @PathVariable("articleId") FlakeId articleFlakeId,
      HttpServletRequest request) throws IOException {
    return resolveZone(request, rawZone, zoneInfo -> {
      return new ModelAndView("article/debates")//
          .addObject("zoneInfo", zoneInfo)
          .addObject("recommendZones", zoneService.listRecommendZones())
          .addObject("article", articleService.loadArticle(articleFlakeId))
          .addObject("zoneAdmins",
              zoneService.listAdministratorsWithCache(zoneInfo.getZone())
                  .stream()
                  .collect(Collectors.joining(",")))
          .addObject("debateTree", articleService.listBestDebates(articleFlakeId, null));
    });
  }

  @RequestMapping("/{zone}/debates/{articleId}/{parentDebateId}")
  public Object childDebates(@PathVariable("zone") String rawZone,
      @PathVariable("articleId") FlakeId articleFlakeId,
      @PathVariable("parentDebateId") FlakeId debateFlakeId,
      HttpServletRequest request) throws IOException {
    return resolveZone(request, rawZone, zoneInfo -> {
      return new ModelAndView("article/debates")//
          .addObject("zoneInfo", zoneInfo)
          .addObject("article", articleService.loadArticle(articleFlakeId))
          .addObject("recommendZones", zoneService.listRecommendZones())
          .addObject("parentDebate", articleService.loadDebateWithoutCache(debateFlakeId))
          .addObject("debateTree", articleService.listBestDebates(articleFlakeId, debateFlakeId));
    });
  }
}
