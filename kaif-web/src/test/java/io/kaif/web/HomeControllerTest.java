package io.kaif.web;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;

import io.kaif.model.zone.ZoneInfo;
import io.kaif.test.MvcIntegrationTests;

public class HomeControllerTest extends MvcIntegrationTests {

  ZoneInfo funZone = zoneDefault("fun");
  ZoneInfo toyZone = zoneDefault("toy");

  @Before
  public void setUp() throws Exception {
    when(zoneService.listRecommendZones()).thenReturn(asList(zoneDefault("recommend1"),
        zoneDefault("recommend2")));
  }

  @Test
  public void index() throws Exception {
    when(articleService.listTopArticles(null)).thenReturn(//
        asList(article(funZone.getZone(), "joke xyz 1")));
    mockMvc.perform(get("/"))
        .andExpect(content().encoding("UTF-8"))
        .andExpect(content().string(containsString("joke xyz 1")))
        .andExpect(content().string(containsString("recommend1")))
        .andExpect(content().string(containsString("recommend2")))
        .andExpect(content().string(containsString("/snapshot/css/kaif.css")))
        .andExpect(content().string(containsString("/snapshot/web/main.dart.js")));
  }

  @Test
  public void zones() throws Exception {
    ImmutableMap<String, List<ZoneInfo>> zones = ImmutableMap.of("F",
        asList(funZone, zoneDefault("fortran")),
        "T",
        asList(toyZone, zoneDefault("tcl")));
    when(zoneService.listZoneAtoZ()).thenReturn(zones);

    mockMvc.perform(get("/zone/a-z"))
        .andExpect(content().string(containsString("fortran")))
        .andExpect(content().string(containsString("tcl")));
  }

  @Test
  public void listLatestArticles() throws Exception {
    when(articleService.listLatestArticles(null)).thenReturn(//
        asList(article(funZone.getZone(), "double 00"), article(toyZone.getZone(), "gundam")));
    mockMvc.perform(get("/new"))
        .andExpect(model().attributeExists("recommendZones"))
        .andExpect(content().string(containsString("double 00")))
        .andExpect(content().string(containsString("gundam")));
  }
}