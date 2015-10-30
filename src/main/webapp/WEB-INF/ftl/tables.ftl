<#macro paginator page pageSize count title parameters="">
  <#if page &gt; 1>
    <a href="?page=${(page - 1)}&${parameters}">&lt;&lt;</a>
  <#else>
    <span>&lt;&lt;</span>
  </#if>

  <#assign maxIndex = (page * pageSize) />
  <#if maxIndex &gt; count>
    <#assign maxIndex = count />
    <span>&gt;&gt;</span>
  <#else>
    <a href="?page=${(page + 1)}&${parameters}">&gt;&gt;</a>
  </#if>

  <#assign start = 0 />
  <#if count &gt; 0>
    <#assign start = ((page - 1) * pageSize) + 1 />
  </#if>
  <span class="resultset">${title} de ${(start)?c} a ${maxIndex} de ${count}</span>
</#macro>