<!DOCTYPE html>
<html>
  <head>
    <#import "/spring.ftl" as spring />
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="<@spring.url '/css/jquery-ui/jquery-ui.css'/>">

    <link rel="stylesheet" href="<@spring.url '/css/base.css'/>">
    <link rel="stylesheet" href="<@spring.url '/css/shop.css'/>">
    <link rel="stylesheet" href="<@spring.url '/css/custom.css'/>">

    <script src="<@spring.url '/script/jquery.js'/>"></script>
    <script src="<@spring.url '/script/jquery-ui.js'/>"></script>
    <script src="<@spring.url '/script/pure.min.js'/>"></script>
    <script src="<@spring.url '/script/livevalidation.js'/>"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.FeedEditor = function (container) {

  var validations = [];

  var initValidations = function() {
    var fechaValidation = new LiveValidation("from");
    fechaValidation.add(Validate.Presence, {
      failureMessage: "La fecha del adicional es obligatoria"
    });
    validations.push(fechaValidation);
  };

  var initEventListeners = function() {
    container.find("input[name='from']" ).datepicker({
      dateFormat: 'dd/mm/yy',
      onSelect: function(dateText, datePicker) {
        $(this).attr('value', dateText);
      }
    });

    container.find(".js-reset" ).click(function () {
      if (LiveValidation.massValidate(validations)) {
        container.submit();
      }
    });

  };

  return {
    render: function() {
      initEventListeners();
      initValidations();
    }
  }

}

      jQuery(document).ready(function() {
        var formContainer = jQuery(".js-feed-reset");
        var form = App.widget.FeedEditor(formContainer);
        form.render();
      });

    </script>

    <script src="<@spring.url '/script/ShopperSelector.js'/>"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">Feeds</h2>

      <#assign feed = model["feed"] />
      <form action="${feed.code}" method="POST" class="form-shop form-shop-big js-feed-reset">
       <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
          <div class="ch-popover-content">
            Revisa los datos. Debes completar campos "Número" y "Factura".
          </div>
        </div-->
        <h2 class="subtitulo">Deuda de MCD</h2>
        <!-- FILA 1 -->
        <div class="cell js-visita">
          <div class="box-gray">
            <fieldset>
              <div class="form-shop-row">
                <label for="from">Fecha de reset</label>
                <input id="from" type="text" name="from" value="" class="item-field" />
              </div>
            </fieldset>
          </div>
          <ul class="action-columns">
            <li>
              <input type="button" class="btn-shop-small js-reset" value="Reset" />
            </li>
          </ul>
        </div>
        <!-- FIN FILA 1 -->
      </form>
    </div>
  </body>
</html>