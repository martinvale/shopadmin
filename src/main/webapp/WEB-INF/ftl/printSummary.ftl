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
      <h2 class="container-tit">${model["title"]}</h2>
      <table summary="${model["title"]}" class="table-form">
        <thead>
          <tr>
            <th scope="col">A&ntilde;o</th>
            <th scope="col">Mes</th>
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
              <td>${honorariosAnio?string.currency}</td>
              <td>${reintegrosAnio?string.currency}</td>
              <td>${otrosAnio?string.currency}</td>
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
              <td>${honorariosAnio?string.currency}</td>
              <td>${reintegrosAnio?string.currency}</td>
              <td>${otrosAnio?string.currency}</td>
              <td>${(honorariosAnio + reintegrosAnio + otrosAnio)?string.currency}</td>
            </tr>
          </#if>
        </tbody>
      </table>
    </div>
  </body>
</html>