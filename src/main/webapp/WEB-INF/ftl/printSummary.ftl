<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="../css/jquery-ui/jquery-ui.css">

    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/shop.css">
    <link rel="stylesheet" href="../css/custom.css">

    <#setting locale="es_AR">

<style>
@media print {
  #print-button {
    display: none;
  }
}

#print-button {
  text-align: center;
}

.table-form td {
  text-align: right;
}

.table-form td.total {
  text-align: left;
}

</style>
  </head>
  <body>
    <p id="print-button"><button onclick="window.print()">Imprimir</button></p>

    <div class="container-box-plantilla">
      <h2 class="container-tit">${model["title"]} (impreso el ${.now})</h2>
      <table summary="${model["title"]}" class="table-form">
        <thead>
          <tr>
            <th scope="col">A&ntilde;o</th>
            <th scope="col">Mes</th>
          <#if model["includeEmpresa"]!false>
            <th scope="col">Empresa</th>
          </#if>
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

            <#if row.getValue("year") != anioAnt && anioAnt != 0>
              <#assign imprimirResto = false />

              <tr>
                <td class="total">Total ${anioAnt?c}</td>
                <td>&nbsp;</td>
              <#if model["includeEmpresa"]!false>
                <td>&nbsp;</td>
              </#if>
                <td>$ ${honorariosAnio?string(",##0.00")}</td>
                <td>$ ${reintegrosAnio?string(",##0.00")}</td>
                <td>$ ${otrosAnio?string(",##0.00")}</td>
                <#assign honorariosTotal = honorariosTotal + honorariosAnio />
                <#assign reintegrosTotal = reintegrosTotal + reintegrosAnio />
                <#assign otrosTotal = otrosTotal + otrosAnio />
                <td>$ ${(honorariosAnio + reintegrosAnio + otrosAnio)?string(",##0.00")}</td>
                <#assign honorariosAnio = 0 />
                <#assign reintegrosAnio = 0 />
                <#assign otrosAnio = 0 />
              </tr>

            </#if>

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
              <td>${row.getValue("empresa")!''}</td>
            </#if>
              <td>$ ${row.getValue("honorarios")?string(",##0.00")}</td>
              <#assign honorariosAnio = honorariosAnio + row.getValue("honorarios") />
              <td>$ ${row.getValue("reintegros")?string(",##0.00")}</td>
              <#assign reintegrosAnio = reintegrosAnio + row.getValue("reintegros") />
              <td>$ ${row.getValue("otros")?string(",##0.00")}</td>
              <#assign otrosAnio = otrosAnio + row.getValue("otros") />
              <td>$ ${(row.getValue("honorarios") + row.getValue("reintegros") + row.getValue("otros"))?string(",##0.00")}</td>
            </tr>
          </#list>
          <#if model["rows"]?size &gt; 0 && imprimirResto>
            <tr>
              <td class="total">Total ${anioAnt?c}</td>
              <td>&nbsp;</td>
            <#if model["includeEmpresa"]!false>
              <td>&nbsp;</td>
            </#if>
              <td>$ ${honorariosAnio?string(",##0.00")}</td>
              <td>$ ${reintegrosAnio?string(",##0.00")}</td>
              <td>$ ${otrosAnio?string(",##0.00")}</td>
              <#assign honorariosTotal = honorariosTotal + honorariosAnio />
              <#assign reintegrosTotal = reintegrosTotal + reintegrosAnio />
              <#assign otrosTotal = otrosTotal + otrosAnio />
              <td>$ ${(honorariosAnio + reintegrosAnio + otrosAnio)?string(",##0.00")}</td>
            </tr>
          </#if>
          <tr>
            <td class="total">Total</td>
            <td>&nbsp;</td>
          <#if model["includeEmpresa"]!false>
            <td>&nbsp;</td>
          </#if>
            <td>$ ${honorariosTotal?string(",##0.00")}</td>
            <td>$ ${reintegrosTotal?string(",##0.00")}</td>
            <td>$ ${otrosTotal?string(",##0.00")}</td>
            <td>$ ${(honorariosTotal + reintegrosTotal + otrosTotal)?string(",##0.00")}</td>
          </tr>
        </tbody>
      </table>
    </div>
  </body>
</html>