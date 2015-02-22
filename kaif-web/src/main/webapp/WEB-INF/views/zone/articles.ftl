<#import "/spring.ftl" as spring />
<#import "../macros/template.ftl" as template>
<#import "../macros/url.ftl" as url>
<#import "../macros/comp.ftl" as comp>
<#import "../macros/aside.ftl" as aside>

<#assign headContent>

<title>${zoneInfo.aliasName} | kaif.io</title>

<#-- TODO description and open graph, twitter card...etc -->

<meta name="description" content="${zoneInfo.aliasName} ${zoneInfo.name} | kaif.io">

</#assign>

<@template.page
layout='full'
head=headContent
applyZoneTheme=true
>
    <@template.zone data=zoneInfo>

    <div class="grid">
        <div class="grid-body">
            <@comp.articleList data=articlePage></@comp.articleList>
        </div>

        <aside class="grid-aside">
            <@aside.createArticle zoneInfo=zoneInfo />
            <@aside.recommendZones zoneInfos=recommendZones />
        </aside>
    </div>

    </@template.zone>
</@template.page>
