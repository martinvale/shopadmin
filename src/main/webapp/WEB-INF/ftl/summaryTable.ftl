<#macro renderTable model>
  <table summary="Reporte" class="table-form">
    <thead>
      <tr>
        <th scope="col">A&ntilde;o</th>
        <th scope="col">Mes</th>
      <#if model["includeEmpresa"]!false>
        <th scope="col">Empresa</th>
      </#if>
        <!--th scope="col">Dia</th>
        <th scope="col">Tipo item</th-->
        <th scope="col">Honorarios</th>
        <th scope="col">Reintegros</th>
        <th scope="col">Otros gastos</th>
        <th scope="col">Total</th>
      </tr>
    </thead>
    <tbody>
      <#assign anioAnt = 0 />
      <#assign honorariosAnio = 0 />
      <#assign reintegrosAnio = 0 />
      <#assign otrosAnio = 0 />
      <#assign honorariosTotal = 0 />
      <#assign reintegrosTotal = 0 />
      <#assign otrosTotal = 0 />
      <#assign imprimirResto = false />
      <#list model["rows"] as row>
        <#assign imprimirResto = true />
        <tr>
          <td>
            <#if row.getValue("year") != anioAnt>
              ${row.getValue("year")?c}
              <#assign anioAnt = row.getValue("year") />
            <#else>
              &nbsp;
            </#if>
          </td>
          <td>${row.getValue("month")?c}</td>
        <#if model["includeEmpresa"]!false>
          <td>${row.getValue("name")!''}</td>
        </#if>
          <td>${row.getValue("honorarios")?string.currency}</td>
          <#assign honorariosAnio = honorariosAnio + row.getValue("honorarios") />
          <td>${row.getValue("reintegros")?string.currency}</td>
          <#assign reintegrosAnio = reintegrosAnio + row.getValue("reintegros") />
          <td>${row.getValue("otros")?string.currency}</td>
          <#assign otrosAnio = otrosAnio + row.getValue("otros") />
          <td>${(row.getValue("honorarios") + row.getValue("reintegros") + row.getValue("otros"))?string.currency}</td>
        </tr>
        <#if row.getValue("month") == 12>
          <#assign imprimirResto = false />

        <tr>
          <td class="total">Total ${row.getValue("year")?c}</td>
          <td>&nbsp;</td>
        <#if model["includeEmpresa"]!false>
          <td>&nbsp;</td>
        </#if>
          <td>${honorariosAnio?string.currency}</td>
          <#assign honorariosTotal = honorariosTotal + honorariosAnio />
          <td>${reintegrosAnio?string.currency}</td>
          <#assign reintegrosTotal = reintegrosTotal + reintegrosAnio />
          <td>${otrosAnio?string.currency}</td>
          <#assign otrosTotal = otrosTotal + otrosAnio />
          <td>${(honorariosAnio + reintegrosAnio + otrosAnio)?string.currency}</td>
          <#assign honorariosAnio = 0 />
          <#assign reintegrosAnio = 0 />
          <#assign otrosAnio = 0 />
        </tr>

        </#if>
      </#list>
      <#if model["rows"]?size &gt; 0 && imprimirResto>
        <tr>
          <td class="total">Total ${anioAnt?c}</td>
          <td>&nbsp;</td>
        <#if model["includeEmpresa"]!false>
          <td>&nbsp;</td>
        </#if>
          <td>${honorariosAnio?string.currency}</td>
          <#assign honorariosTotal = honorariosTotal + honorariosAnio />
          <td>${reintegrosAnio?string.currency}</td>
          <#assign reintegrosTotal = reintegrosTotal + reintegrosAnio />
          <td>${otrosAnio?string.currency}</td>
          <#assign otrosTotal = otrosTotal + otrosAnio />
          <td>${(honorariosAnio + reintegrosAnio + otrosAnio)?string.currency}</td>
        </tr>
      </#if>
      <tr>
        <td class="total">Total</td>
        <td>&nbsp;</td>
      <#if model["includeEmpresa"]!false>
        <td>&nbsp;</td>
      </#if>
        <td>${honorariosTotal?string.currency}</td>
        <td>${reintegrosTotal?string.currency}</td>
        <td>${otrosTotal?string.currency}</td>
        <td>${(honorariosTotal + reintegrosTotal + otrosTotal)?string.currency}</td>
      </tr>
    </tbody>
  </table>
</#macro>