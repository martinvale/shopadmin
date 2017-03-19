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

App.widget.TitularSelector = function (container, callback) {

  var currentTitular = null;

  var selector = container.find(".js-titulares");

  var endpoints = {
    1: "<@spring.url '/services/shoppers/suggest'/>",
    2: "<@spring.url '/proveedores/suggest'/>"
  };

  var tipoTitularSelector = container.find(".js-tipo-titular");

  var filter;

  var onSelect = callback || function () {};

  var initEventListeners = function () {
    tipoTitularSelector.change(function (event) {
      reset();
      var tipoTitular = jQuery(this).val();
      filter = selector.autocomplete('option', 'source', endpoints[tipoTitular]);
    });
  };

  var reset = function () {
    selector.val('');
    container.find(".js-titular-id").val('');
  };

  var highlight = function (value, term) {
    var matcher = new RegExp("(" + $.ui.autocomplete.escapeRegex(term) + ")", "ig");
    return value.replace(matcher, "<strong>$1</strong>");
  };

  return {
    render: function () {
      var me = this;
      var suggestEndpoint = endpoints[tipoTitularSelector.val()];
      filter = selector.autocomplete({
        source: suggestEndpoint,
        minLength: 2,
        select: function(event, ui) {
          currentTitular = ui.item;
          if (ui.item.name) {
            selector.val(ui.item.name);
          } else {
            selector.val(ui.item.description);
          }
          container.find(".js-titular-id").val(ui.item.id);
          onSelect(me.getTitularSelected(), currentTitular);
          return false;
        }
      });
      filter.data( "ui-autocomplete" )._renderItem = function(ul, item) {
        var display = ((item.name) ? item.name : item.description);
        display = highlight(display, selector.val());
        return $("<li>")
          .append("<a>" + display + "</a>")
          .appendTo(ul);
      };

      initEventListeners();
    },
    getTitularSelected: function() {
      current = null;
      if (container.find(".js-titular-id").val()) {
        current = {
          id: container.find(".js-titular-id").val(),
          tipo: tipoTitularSelector.val(),
          name: selector.val()
        }
      }
      return current;
    }
  };
}

      jQuery(document).ready(function() {
        var form = jQuery(".js-search-form");

        var titularSelector = App.widget.TitularSelector(form.find(".js-titular-selector"),
            function () {});
        titularSelector.render();

        var loadingIndicator = new App.widget.LoadingIndicator(form);
        form.find("input[type=submit]").click(function () {
          loadingIndicator.start();
        });
      });

    </script>

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
            <select name="titularTipo" class="js-tipo-titular">
              <option value="1" <#if ((order.tipoProveedor?c)!'') == '1'>selected="selected"</#if>>Shopper</option>
              <option value="2" <#if ((order.tipoProveedor?c)!'') == '2'>selected="selected"</#if>>Proveedor</option>
            </select>
            <#assign titularNombre = "${model['titularNombre']!''}" />
            <input id="titular" type="text" value="${titularNombre}" class="item-field js-titulares" />
            <input type="hidden" name="titularId" value="${(order.proveedor?c)!''}" class="js-titular-id" />
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Editar" class="btn-shop-small"></li>
        </ul>
      </form>

      <!--form class="form-shop form-shop-big js-update-shopper" action="update" method="POST">
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
      </form-->

    </div>
  </body>
</html>