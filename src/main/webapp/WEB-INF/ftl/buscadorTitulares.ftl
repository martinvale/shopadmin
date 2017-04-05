<!DOCTYPE html>
<html>
  <head>
    <#import "/spring.ftl" as spring />
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
        var form = jQuery(".js-search-form");

        var titularSelector = App.widget.TitularSelector(form.find(".js-titular-selector"),
            "<@spring.url '/titular/suggest'/>");
        titularSelector.render();

        var loadingIndicator = new App.widget.LoadingIndicator(form);
        form.find("input[type=submit]").click(function () {
          loadingIndicator.start();
        });
      });

    </script>

    <script src="<@spring.url '/script/TitularSelector.js'/>"></script>
    <script src="<@spring.url '/script/LoadingIndicator.js'/>"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">B&uacute;squeda de titulares</h2>
      <form action="edit" class="form-shop form-shop-big js-search-form" method="GET">
        <div class="cell box-green buscador">
          <div class="form-shop-row titular-selector js-titular-selector">
            <label class="mandatory">Titular</label>
            <#assign titularNombre = "${model['titularNombre']!''}" />
            <input id="titular" type="text" value="${titularNombre}" class="item-field js-titular-nombre" />
            <input type="hidden" name="titularId" value="" class="js-titular-id" />
            <input type="hidden" name="titularTipo" value="" class="js-titular-tipo" />
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Editar" class="btn-shop-small"></li>
        </ul>
      </form>

    </div>
  </body>
</html>