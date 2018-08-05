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
      <h2 class="container-tit">Resumen de deuda general</h2>
      <form class="form-shop form-shop-big" action="debtSummary" method="GET">
          <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                  <div class="ch-popover-content">
                      Revisa los datos. Debes completar campos "Número" y "Factura".
                  </div>
          </div-->
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
            <th scope="col">A&ntilde;o</th>
            <th scope="col">Mes</th>
          <#if model["includeEmpresa"]!false>
            <th scope="col">Empresa</th>
          </#if>
          <#if model["includeShopper"]!false>
            <th scope="col">Shopper</th>
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
            <#if model["includeShopper"]!false>
              <td>${row.getValue("apellido_y_nombre")!''}</td>
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
          </tr>
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