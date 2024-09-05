<#import "source_set_selector.ftl" as source_set_selector>
<#macro display>
<div class="navigation-wrapper" id="navigation-wrapper">
  <div id="leftToggler"><span class="icon-toggler"></span></div>
  <div class="library-name">
    <@template_cmd name="pathToRoot">
      <a href="${pathToRoot}index.html">
        <@template_cmd name="projectName">
          <span>Zowe Client Kotlin SDK</span>
        </@template_cmd>
      </a>
    </@template_cmd>
  </div>
  <div>
    <#-- This can be handled by the versioning plugin -->
    <@version/>
  </div>
</div>
</#macro>
