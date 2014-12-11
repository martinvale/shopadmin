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
    <script src="../script/livevalidation.js"></script>

    <script src="../script/spin.js"></script>
    <script src="../script/jquery.spin.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

      jQuery(document).ready(function() {
        var form = jQuery(".form-shop");
        var loadingIndicator = new App.widget.LoadingIndicator(form);

        form.find(".js-date" ).datepicker({
          onSelect: function(dateText, datePicker) {
            $(this).attr('value', dateText);
          }
        });
        form.find(".js-download").click(function () {
          var desdeField = form.find("input[name='desde']");
          var hastaField = form.find("input[name='hasta']");

          loadingIndicator.start();
          location.href = 'download?desde=' + desdeField.val() + '&hasta='
            + hastaField.val();
          loadingIndicator.stop();
        });
      });

    </script>

    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">Exportar &oacute;rdenes de pago</h2>
      <form class="form-shop form-shop-big" action="shopmetrics" enctype="multipart/form-data" method="POST">
      <#if model["status"]??>
        <div role="success" class="form-success-txt" aria-hidden="false">
          <i class="ch-icon-remove"></i>
          <div class="ch-popover-content">${model["status"]}</div>
        </div>
      </#if>
        <!--div role="alert" class="form-error-txt" aria-hidden="false">
          <i class="ch-icon-remove"></i>
          <div class="ch-popover-content">
            Revisa los datos. Debes completar campos "Número" y "Factura".
          </div>
        </div-->
        <div class="box-green">
          <label for="desde">Desde: </label>
          <input id="desde" type="text" name="desde" class="js-date"/>
          <label for="hasta">Hasta: </label>
          <input id="hasta" type="text" name="hasta" class="js-date"/>
        </div>
        <ul class="action-columns">
          <li><input type="button" value="Descargar" class="btn-shop-small js-download"></li>
        </ul>
      </form>
    </div>
  </body>
</html>