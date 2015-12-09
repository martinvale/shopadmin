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
          var includeEmpresa = jQuery("input[name='includeEmpresa']").prop("checked");
          var url = "printProdSummary?mesDesde=" + mesDesde.val() + '&anioDesde='
              + anioDesde.val() + '&mesHasta=' + mesHasta.val() + '&anioHasta='
              + anioHasta.val() + '&includeEmpresa=' + includeEmpresa;
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
    <#import "summaryTable.ftl" as summary/>
    <#setting locale="es_AR">

    <div class="container-box-plantilla">
      <h2 class="container-tit">Reporte de producci&oacute;n</h2>
      <form class="form-shop form-shop-big" action="prodSummary" method="GET">
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
              <#list 2006..2017 as anio>
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
              <#list 2006..2017 as anio>
                <option value="${anio?c}" <#if anio == anioHasta>selected="true"</#if>>${anio?c}</option>
              </#list>
              </select>
            </div>
            <div class="field groups">
              <label>Agrupado por: </label>
              <input type="checkbox" checked="checked" disabled="true"/><span>A&ntilde;o</span>
              <input type="checkbox" checked="checked" disabled="true"/><span>Mes</span>
              <input type="checkbox" name="includeEmpresa" value="true" <#if model["includeEmpresa"]!false>checked="checked"</#if> /><span>Empresa</span>
            </div>
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Buscar" class="btn-shop-small"></li>
        </ul>
      </form>
      <@summary.renderTable model />
    </div>
    <div class="actions-form">
      <ul class="action-columns">
        <li><input type="button" class="btn-shop js-print" value="Imprimir"></li>
      </ul>
    </div>
  </body>
</html>