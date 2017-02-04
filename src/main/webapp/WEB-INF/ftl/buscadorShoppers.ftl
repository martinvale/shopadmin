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

        var formShopper = jQuery(".js-update-shopper");

        var onSelect = function(shopper) {
          formShopper.find("input[name='dni']").val(shopper.dni);
          formShopper.find("input[name='login']").val(shopper.login);
        };

        var shopperSelector = new App.widget.ShopperSelector(jQuery(".js-shopper-selector"), true, null, onSelect);
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
      <h2 class="container-tit">B&uacute;squeda de shoppers</h2>
      <form action="search" class="form-shop form-shop-big js-search-form" method="GET">
          <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                  <div class="ch-popover-content">
                      Revisa los datos. Debes completar campos "Número" y "Factura".
                  </div>
          </div-->
        <div class="cell box-green buscador">
          <div class="field shopper-widget js-shopper-selector">
            <label for="shopper">Shooper: </label>
            <input type="text" id="shopper" name="shopper" value="${(model['shopper'].name)!''}" class="js-shopper" />
            <a id="clear-shopper" href="#" class="clear js-clear">limpiar</a>
            <input type="hidden" name="shopperId" value="" class="js-shopper-id" />
            <input type="hidden" name="shopperDni" value="${model['shopperDni']!''}" class="js-shopper-dni" />
          </div>
        </div>
      </form>

      <form class="form-shop form-shop-big js-update-shopper" action="update" method="POST">
        <input type="hidden" name="dni" value="" />
        <div class="cell">
          <div class="box-green">
            <div class="field">
              <label class="field-name" for="login">Login shopmetrics: </label>
              <input type="text" id="login" name="login" value="" />
            </div>
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Guardar" class="btn-shop-small"></li>
        </ul>
      </form>

    </div>
  </body>
</html>