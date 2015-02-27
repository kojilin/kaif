<#import "/spring.ftl" as spring />
<#import "../macros/template.ftl" as template>
<#import "../macros/url.ftl" as url>
<#import "../macros/comp.ftl" as comp>
<#import "../macros/util.ftl" as util>
<#import "../macros/aside.ftl" as aside>

<#assign headContent>

<title>${article.title} | ${zoneInfo.aliasName} | kaif.io</title>

<#-- TODO description and open graph, twitter card...etc -->

<meta name="description"
      content="${article.title} | ${zoneInfo.aliasName}">

</#assign>

<@template.page
layout='full'
head=headContent
applyZoneTheme=true
>
    <#assign debateMenus>
        <@util.menuLink '/z/${zoneInfo.name}/debates/${article.articleId}' '討論'/>
    </#assign>

    <@template.zone data=zoneInfo menus=debateMenus>

    <div class="grid">

        <div class="grid-body">
            <div class="debate-tree" debate-tree>
                <#if parentDebate??>

                <#-- tree parent is Debate, we still create article component for dart,
                     but it is invisible to user -->
                    <@comp.article data=article hidden=true />
                    <@comp.debate data=parentDebate parentMode=true/>
                <#else>

                <#-- tree parent is Article -->
                    <@comp.article data=article parentMode=true />
                    <div class="grid-center-row debate-form-container">
                    <#-- place holder debate-form will replace by comp-template
                         keep place holder looks the same as comp-template
                         -->
                        <div class="pure-form pure-g" debate-form>
                            <div class="pure-u-1">
                                <textarea rows="3" class="pure-input-1-2 kmark-input"></textarea>
                            </div>
                            <div class="pure-u-1">
                                <button type="submit" class="pure-button pure-button-primary">留言
                                </button>
                            </div>
                        </div>
                    </div>
                </#if>
                <#list debateTree.children as debateNode>
                    <@comp.debateNode data=debateNode />
                </#list>
            </div>
        </div>
        <aside class="grid-aside">
            <@aside.createArticle zoneInfo=zoneInfo />
            <@aside.recommendZones zoneInfos=recommendZones />
        </aside>
    </div>
    </@template.zone>

<#-- component templates -->
<form class="pure-form hidden debate-form" comp-template="debate-form">
    <input type="hidden" name="zoneInput" value="${article.zone}">
    <input type="hidden" name="articleInput" value="${article.articleId}">

    <div>
        <div kmark-previewer class="hidden kmark kmark-preview"></div>
        <textarea name="contentInput" class="pure-input-1-2 kmark-input" maxlength="4096"
                  rows="3"></textarea>
    </div>
    <div class="form-action-bar">
        <button type="submit" class="pure-button pure-button-primary">留言</button>
        <button class="pure-button" kmark-debate-cancel>取消</button>
        <button class="pure-button"
                kmark-preview><@spring.messageText "kmark.preview" "Preview" /></button>
    </div>
</form>

<form class="pure-form hidden debate-form" comp-template="edit-kmark-form">
    <div>
        <div kmark-previewer class="hidden kmark kmark-preview"></div>
        <textarea name="contentInput" class="pure-input-1 kmark-input" maxlength="4096"
                  rows="3"></textarea>
    </div>
    <div class="form-action-bar">
        <button type="submit" class="pure-button pure-button-primary">修改</button>
        <button class="pure-button" kmark-cancel>取消</button>
        <button class="pure-button"
                kmark-preview><@spring.messageText "kmark.preview" "Preview" /></button>
    </div>
</form>
</@template.page>
