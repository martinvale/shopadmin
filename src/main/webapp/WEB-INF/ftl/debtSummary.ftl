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
        jQuery(".js-print" ).click(function () {
          var mesDesde = jQuery("select[name='mesDesde']");
          var anioDesde = jQuery("select[name='anioDesde']");
          var mesHasta = jQuery("select[name='mesHasta']");
          var anioHasta = jQuery("select[name='anioHasta']");
          var url = "printDebtSummary?mesDesde=" + mesDesde.val() + '&anioDesde='
              + anioDesde.val() + '&mesHasta=' + mesHasta.val() + '&anioHasta='
              + anioHasta.val();
          window.open(url, "", "width=1000, height=600");
        });

        var form = jQuery(".form-shop");
        var loadingIndicator = new App.widget.LoadingIndicator(form);
        form.find("input[type=submit]").click(function () {
          loadingIndicator.start();
        });
      });

    </script>

    <script src="../script/LoadingIndicator.js"></script>

<style>

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

    <div class="container-box-plantilla">
      <h2 class="container-tit">Resumen de deuda</h2>
      <form class="form-shop form-shop-big" action="debtSummary" method="GET">
          <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                  <div class="ch-popover-content">
                      Revisa los datos. Debes completar campos "Número" y "Factura".
                  </div>
          </div-->
        <div class="cell">
          <div class="box-green">
            <div class="field">
              <label for="mesDesde">Mes desde: </label>
              <select id="mesDesde" name="mesDesde">
              <#assign mesDesde = model["mesDesde"]!1 />
              <#list 1..12 as mes>
                <option value="${mes}" <#if mes == mesDesde>selected="true"</#if>>${mes}</option>
              </#list>
              </select>
              <label for="anioDesde">A&ntilde;o desde: </label>
              <select id="anioDesde" name="anioDesde">
              <#assign anioDesde = model["anioDesde"]!2006 />
              <#list 2006..2015 as anio>
                <option value="${anio?c}" <#if anio == anioDesde>selected="true"</#if>>${anio?c}</option>
              </#list>
              </select>
            </div>
            <div class="field">
              <label for="mesHasta">Mes hasta: </label>
              <select id="mesHasta" name="mesHasta">
              <#assign mesHasta = model["mesHasta"]!1 />
              <#list 1..12 as mes>
                <option value="${mes}" <#if mes == mesHasta>selected="true"</#if>>${mes}</option>
              </#list>
              </select>
              <label for="anioHasta">A&ntilde;o hasta: </label>
              <select id="anioHasta" name="anioHasta">
              <#assign anioHasta = model["anioHasta"]!2015 />
              <#list 2006..2015 as anio>
                <option value="${anio?c}" <#if anio == anioHasta>selected="true"</#if>>${anio?c}</option>
              </#list>
              </select>
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
            <!--th scope="col">Dia</th>
            <th scope="col">Empresa</th>
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
    <div class="actions-form">
      <ul class="action-columns">
        <li><input type="button" class="btn-shop js-print" value="Imprimir"></li>
      </ul>
    </div>
  </body>
</html>