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
        var shopperSelector = new App.widget.ShopperSelector(jQuery(".js-shopper-selector"));
        shopperSelector.render();

        var form = jQuery(".js-search-form");
        var loadingIndicator = new App.widget.LoadingIndicator(form);
        form.find("input[type=submit]").click(function () {
          loadingIndicator.start();
        });
      });

    </script>

    <script src="../script/ShopperSelector.js"></script>
    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">B&uacute;squeda de ordenes de items</h2>
      <form class="form-shop form-shop-big js-search-form" method="POST">
          <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                  <div class="ch-popover-content">
                      Revisa los datos. Debes completar campos "Número" y "Factura".
                  </div>
          </div-->
        <div class="cell">
          <div class="box-green">
            <div class="form-shop-row-left shopper-widget js-shopper-selector">
              <label for="shopper">Shooper: </label>
              <input type="text" id="shopper" name="shopper" value="${model['shopper']!''}" class="js-shopper" />
              <a id="clear-shopper" href="#" class="clear js-clear">limpiar</a>
              <input type="hidden" name="shopperId" value="" class="js-shopper-id" />
              <input type="hidden" name="shopperDni" value="${model['shopperDni']!''}" class="js-shopper-dni" />
            </div>
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Buscar" class="btn-shop-small"></li>
        </ul>
      </form>
      <table summary="Lista de items" class="table-form">
        <thead>
          <tr>
            <th scope="col">Cliente</th>
            <th scope="col">Sucursal</th>
            <th scope="col">Fecha</th>
            <th scope="col">Pago</th>
            <th scope="col">Importe</th>
            <th scope="col">Orden</th>
            <th scope="col">Fecha de Pago</th>
            <th scope="col">Estado</th>
          </tr>
        </thead>
        <tbody>
          <#list model["items"] as item>
          <tr>
            <td>${item.cliente}</td>
            <td>${item.sucursal}</td>
            <td>${item.fecha}</td>
            <td>${item.tipoPago.description}</td>
            <td>${item.importe?string.currency}</td>
            <td>${item.ordenPago.numero?c} <a href="../orden/${item.ordenPago.numero?c}">editar</a></td>
            <td>${item.ordenPago.fechaPago?string('dd/MM/yyyy')}</td>
            <td>${item.ordenPago.estado.description}</td>
          </tr>
          </#list>
        </tbody>
      </table>
    </div>
  </body>
</html>