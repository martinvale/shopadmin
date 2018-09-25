<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />

    <link rel="stylesheet" href="../css/jquery-ui/jquery-ui.css">

    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/shop.css">
    <link rel="stylesheet" href="../css/custom.css">

    <script src="../script/jquery.js"></script>
    <script src="../script/jquery-ui.js"></script>
    <script src="../script/pure.min.js"></script>
    <script src="../script/livevalidation.js"></script>

    <script src="../script/spin.js"></script>
    <script src="../script/jquery.spin.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

      jQuery(document).ready(function() {
        var form = jQuery(".form-shop");
        var loadingIndicator = new App.widget.LoadingIndicator(form);
        form.find("input[type=submit]").click(function () {
          loadingIndicator.start();
        });

        form.find(".js-date" ).datepicker({
          //minDate: new Date(),
          dateFormat: 'dd/mm/yy',
          onSelect: function(dateText, datePicker) {
            $(this).attr('value', dateText);
          }
        });

      });

    </script>

    <script src="../script/LoadingIndicator.js"></script>

<style>

@media print {
  .form-shop, .actions-form, header {
    display: none;
  }

}

.table-form thead th {
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
    <#include "header.ftl" />
    <#setting locale="es_AR">

    <div class="container-box-plantilla">
      <h2 class="container-tit">${model["title"]}</h2>
      <form class="form-shop form-shop-big" action="${model["form"]}" method="GET">
        <div class="cell">
          <div class="box-green">
            <div class="field">
              <label for="desde">Desde: </label>
              <input type="text" id="desde" name="desde" class="js-date" value="${(model["desde"]?string('dd/MM/yyyy'))!''}" />
            </div>
            <div class="field">
              <label for="hasta">Hasta: </label>
              <input type="text" id="hasta" name="hasta" class="js-date" value="${(model["hasta"]?string('dd/MM/yyyy'))!''}" />
            </div>
            <div class="field groups">
              <label>Agrupado por: </label>
              <input type="checkbox" checked="checked" disabled="true"/><span>A&ntilde;o</span>
              <input type="checkbox" checked="checked" disabled="true"/><span>Mes</span>
              <input type="checkbox" name="includeEmpresa" value="true" <#if model["includeEmpresa"]!false>checked="checked"</#if> /><span>Empresa</span>
              <input type="checkbox" name="includeShopper" value="true" <#if model["includeShopper"]!false>checked="checked"</#if> /><span>Shopper</span>
            </div>
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Buscar" class="btn-shop-small"></li>
        </ul>
      </form>
      <table summary="Reporte" class="table-form">
        <thead>
          <tr>
            <th scope="col" width="5%" rowspan="2">A&ntilde;o</th>
            <th scope="col" width="5%" rowspan="2">Mes</th>
          <#if model["includeEmpresa"]!false>
            <th scope="col" width="10%" rowspan="2">Empresa</th>
          </#if>
          <#if model["includeShopper"]!false>
            <th scope="col" width="10%" rowspan="2">Shopper</th>
          </#if>
            <th scope="col" colspan="3">${model["titulo1"]}</th>
            <th scope="col" rowspan="2">${model["total1"]}</th>
            <th scope="col" colspan="3">${model["titulo2"]}</th>
            <th scope="col" rowspan="2">${model["total2"]}</th>
            <th scope="col" colspan="3">${model["titulo3"]}</th>
            <th scope="col" rowspan="2">${model["total3"]}</th>
          </tr>
          <tr>
            <th scope="col">Honorarios</th>
            <th scope="col">Reintegros</th>
            <th scope="col">Otros gastos</th>
            <th scope="col">Honorarios</th>
            <th scope="col">Reintegros</th>
            <th scope="col">Otros gastos</th>
            <th scope="col">Honorarios</th>
            <th scope="col">Reintegros</th>
            <th scope="col">Otros gastos</th>
          </tr>
        </thead>
        <tbody>
          <#assign anioAnt = 0 />
          <#assign honorariosAnio = 0 />
          <#assign reintegrosAnio = 0 />
          <#assign otrosAnio = 0 />
          <#assign sustraendoHonorariosAnio = 0 />
          <#assign sustraendoReintegrosAnio = 0 />
          <#assign sustraendoOtrosAnio = 0 />
          <#assign restoHonorariosAnio = 0 />
          <#assign restoReintegrosAnio = 0 />
          <#assign restoOtrosAnio = 0 />
          <#assign honorariosTotal = 0 />
          <#assign reintegrosTotal = 0 />
          <#assign otrosTotal = 0 />
          <#assign sustraendoHonorariosTotal = 0 />
          <#assign sustraendoReintegrosTotal = 0 />
          <#assign sustraendoOtrosTotal = 0 />
          <#assign restoHonorariosTotal = 0 />
          <#assign restoReintegrosTotal = 0 />
          <#assign restoOtrosTotal = 0 />
          <#assign imprimirResto = false />
        <#if model["report"]??>
          <#list model["report"].values as row>
            <#assign imprimirResto = true />
            <tr>
              <td>
                <#if row.getYear() != anioAnt>
                  ${row.getYear()?c}
                  <#assign anioAnt = row.getYear() />
                <#else>
                  &nbsp;
                </#if>
              </td>
              <td>${row.getMonth()?c}</td>
            <#if model["includeEmpresa"]!false>
              <td>${row.getKey().getClient()!''}</td>
            </#if>
            <#if model["includeShopper"]!false>
              <td><a href="#" onclick="window.open('debtDetails?shopperDni=${row.getKey().getShopperDni()!''}&clientId=${row.getKey().getClientId()!''}&desde=${(model["desde"]?string('dd/MM/yyyy'))!''}&hasta=${(model["hasta"]?string('dd/MM/yyyy'))!''}','_blank', 'location=yes,height=570,width=820,scrollbars=yes,status=yes');">${row.getKey().getShopper()!''}</a></td>
            </#if>
              <td>${row.getHonorariosMinuendo()?string.currency}</td>
              <#assign honorariosAnio = honorariosAnio + row.getHonorariosMinuendo() />
              <td>${row.getReintegrosMinuendo()?string.currency}</td>
              <#assign reintegrosAnio = reintegrosAnio + row.getReintegrosMinuendo() />
              <td>${row.getOtrosMinuendo()?string.currency}</td>
              <#assign otrosAnio = otrosAnio + row.getOtrosMinuendo() />
              <td>${(row.getHonorariosMinuendo() + row.getReintegrosMinuendo() + row.getOtrosMinuendo())?string.currency}</td>
              <td>${row.getHonorariosSustraendo()?string.currency}</td>
              <#assign sustraendoHonorariosAnio = sustraendoHonorariosAnio + row.getHonorariosSustraendo() />
              <td>${row.getReintegrosSustraendo()?string.currency}</td>
              <#assign sustraendoReintegrosAnio = sustraendoReintegrosAnio + row.getReintegrosSustraendo() />
              <td>${row.getOtrosSustraendo()?string.currency}</td>
              <#assign sustraendoOtrosAnio = sustraendoOtrosAnio + row.getOtrosSustraendo() />
              <td>${(row.getHonorariosSustraendo() + row.getReintegrosSustraendo() + row.getOtrosSustraendo())?string.currency}</td>
              <#assign restoHonorariosAnio = restoHonorariosAnio + row.getHonorariosResto() />
              <td>${row.getHonorariosResto()?string.currency}</td>
              <#assign restoReintegrosAnio = restoReintegrosAnio + row.getReintegrosResto() />
              <td>${row.getReintegrosResto()?string.currency}</td>
              <#assign restoOtrosAnio = restoOtrosAnio + row.getOtrosResto() />
              <td>${row.getOtrosResto()?string.currency}</td>
              <td>${(row.getHonorariosResto() + row.getReintegrosResto() + row.getOtrosResto())?string.currency}</td>
            </tr>
            <#if row.getMonth() == 12>
              <#assign imprimirResto = false />

            <tr>
              <td class="total">Total ${row.getYear()?c}</td>
              <td>&nbsp;</td>
            <#if model["includeEmpresa"]!false>
              <td>&nbsp;</td>
            </#if>
            <#if model["includeShopper"]!false>
              <td>&nbsp;</td>
            </#if>
              <td>${honorariosAnio?string.currency}</td>
              <#assign honorariosTotal = honorariosTotal + honorariosAnio />
              <td>${reintegrosAnio?string.currency}</td>
              <#assign reintegrosTotal = reintegrosTotal + reintegrosAnio />
              <td>${otrosAnio?string.currency}</td>
              <#assign otrosTotal = otrosTotal + otrosAnio />
              <td>${(honorariosAnio + reintegrosAnio + otrosAnio)?string.currency}</td>
              <td>${sustraendoHonorariosAnio?string.currency}</td>
              <#assign sustraendoHonorariosTotal = sustraendoHonorariosTotal + sustraendoHonorariosAnio />
              <td>${sustraendoReintegrosAnio?string.currency}</td>
              <#assign sustraendoReintegrosTotal = sustraendoReintegrosTotal + sustraendoReintegrosAnio />
              <td>${sustraendoOtrosAnio?string.currency}</td>
              <#assign sustraendoOtrosTotal = sustraendoOtrosTotal + sustraendoOtrosAnio />
              <td>${(sustraendoHonorariosAnio + sustraendoReintegrosTotal + sustraendoOtrosTotal)?string.currency}</td>
              <#assign honorariosAnio = 0 />
              <#assign reintegrosAnio = 0 />
              <#assign otrosAnio = 0 />
              <#assign sustraendoHonorariosAnio = 0 />
              <#assign sustraendoReintegrosAnio = 0 />
              <#assign sustraendoOtrosAnio = 0 />
            </tr>

            </#if>
          </#list>
          <#if imprimirResto>
            <tr>
              <td class="total">Total ${anioAnt?c}</td>
              <td>&nbsp;</td>
            <#if model["includeEmpresa"]!false>
              <td>&nbsp;</td>
            </#if>
            <#if model["includeShopper"]!false>
              <td>&nbsp;</td>
            </#if>
              <td>${honorariosAnio?string.currency}</td>
              <#assign honorariosTotal = honorariosTotal + honorariosAnio />
              <td>${reintegrosAnio?string.currency}</td>
              <#assign reintegrosTotal = reintegrosTotal + reintegrosAnio />
              <td>${otrosAnio?string.currency}</td>
              <#assign otrosTotal = otrosTotal + otrosAnio />
              <td>${(honorariosAnio + reintegrosAnio + otrosAnio)?string.currency}</td>
              <td>${sustraendoHonorariosAnio?string.currency}</td>
              <#assign sustraendoHonorariosTotal = sustraendoHonorariosTotal + sustraendoHonorariosAnio />
              <td>${sustraendoReintegrosAnio?string.currency}</td>
              <#assign sustraendoReintegrosTotal = sustraendoReintegrosTotal + sustraendoReintegrosAnio />
              <td>${sustraendoOtrosAnio?string.currency}</td>
              <#assign sustraendoOtrosTotal = sustraendoOtrosTotal + sustraendoOtrosAnio />
              <td>${(sustraendoHonorariosAnio + sustraendoReintegrosTotal + sustraendoOtrosTotal)?string.currency}</td>
              <td>${restoHonorariosAnio?string.currency}</td>
              <#assign restoHonorariosTotal = restoHonorariosTotal + restoHonorariosAnio />
              <td>${restoReintegrosAnio?string.currency}</td>
              <#assign restoReintegrosTotal = restoReintegrosTotal + restoReintegrosAnio />
              <td>${restoOtrosAnio?string.currency}</td>
              <#assign restoOtrosTotal = restoOtrosTotal + restoOtrosAnio />
              <td>${(restoHonorariosAnio + restoReintegrosTotal + restoOtrosTotal)?string.currency}</td>
            </tr>
          </#if>
          <tr>
            <td class="total">Total</td>
            <td>&nbsp;</td>
          <#if model["includeEmpresa"]!false>
            <td>&nbsp;</td>
          </#if>
          <#if model["includeShopper"]!false>
            <td>&nbsp;</td>
          </#if>
            <td>${honorariosTotal?string.currency}</td>
            <td>${reintegrosTotal?string.currency}</td>
            <td>${otrosTotal?string.currency}</td>
            <td>${(honorariosTotal + reintegrosTotal + otrosTotal)?string.currency}</td>
            <td>${sustraendoHonorariosTotal?string.currency}</td>
            <td>${sustraendoReintegrosTotal?string.currency}</td>
            <td>${sustraendoOtrosTotal?string.currency}</td>
            <td>${(sustraendoHonorariosTotal + sustraendoReintegrosTotal + sustraendoOtrosTotal)?string.currency}</td>
            <td>${restoHonorariosTotal?string.currency}</td>
            <td>${restoReintegrosTotal?string.currency}</td>
            <td>${restoOtrosTotal?string.currency}</td>
            <td>${(restoHonorariosTotal + restoReintegrosTotal + restoOtrosTotal)?string.currency}</td>
          </tr>
        </#if>
        </tbody>
      </table>
    </div>
    <div class="actions-form">
      <ul class="action-columns">
        <li><button class="btn-shop" onclick="window.print()">Imprimir</button></li>
      </ul>
    </div>
  </body>
</html>