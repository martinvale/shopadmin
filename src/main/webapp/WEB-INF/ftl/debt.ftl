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

App.widget.AdicionalEditor = function (container) {

  var branchSelector = container.find(".js-sucursales");

  var shopperSelector = new App.widget.ShopperSelector(
      container.find(".js-shopper-selector"), false, "<@spring.url '/services/shoppers/suggest' />");

  var validations = [];

  var initValidations = function() {
    var fechaValidation = new LiveValidation("fecha");
    fechaValidation.add(Validate.Presence, {
      failureMessage: "La fecha del adicional es obligatoria"
    });
    validations.push(fechaValidation);

    var importeValidation = new LiveValidation("importe");
    importeValidation.add(Validate.Presence, {
      failureMessage: "El importe es obligatorio"
    });
    importeValidation.add(Validate.Numericality, {
      minimum: 0,
      notANumberMessage: "El importe debe ser un numero mayor que 0",
      tooLowMessage: "El importe debe ser un numero mayor que 0"
    });
    validations.push(importeValidation);

    /*var tipoPagoValidation = new LiveValidation("tipoPago");
    tipoPagoValidation.add(Validate.Custom, {
      against: function (value, args) {
        var tipoPago = container.find("select[name='tipoPagoId']").val();
        var isValid = true;
        if (tipoPago !== '3' && itemId === '') {
          isValid = jQuery.inArray(tipoPago, adicionales) === -1;
        }
        return isValid;
      },
      failureMessage: "Este tipo de pago no esta disponible para el mismo dia"
    });*/
  };

  var initEventListeners = function() {
    container.find(".js-fecha-visita" ).datepicker({
      dateFormat: 'dd/mm/yy',
      onSelect: function(dateText, datePicker) {
        $(this).attr('value', dateText);
      }
    });

    container.find(".js-add" ).click(function () {
      //resetVisitaValidations();
      if (LiveValidation.massValidate(validations)) {
        container.submit();
      }
    });

  };

  var updateBranchs = function (branchs) {
    branchSelector.empty();
    jQuery.each(branchs, function (index, branch) {
      branchSelector.append(jQuery("<option>", {
        value: branch.id,
        text: branch.address
      }));
    });
    branchSelector.prop("disabled", false);
  }

  return {
    render: function() {
      shopperSelector.render();

      initEventListeners();
      initValidations();
    }
  }

}

      jQuery(document).ready(function() {
        var editorContainer = jQuery(".js-editor-adicional");
        var editor = App.widget.AdicionalEditor(editorContainer);
        editor.render();
      });

    </script>

    <script src="<@spring.url '/script/ShopperSelector.js'/>"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">Autorizaci&oacute;n de adicionales</h2>

      <#assign actionUrl = "create" />
      <#assign readOnly = model["readOnly"]!true />
      <#if model["debt"]??>
        <#assign debt = model["debt"] />
        <#assign actionUrl = "../update/${debt.id?c}" />
      </#if>
      <form action="${actionUrl}" method="POST" class="form-shop form-shop-big js-editor-adicional">
       <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
          <div class="ch-popover-content">
            Revisa los datos. Debes completar campos "Número" y "Factura".
          </div>
        </div-->
        <h2 class="subtitulo">Item a pagar</h2>
        <!-- FILA 1 -->
        <div class="cell js-visita">
          <div class="box-gray">
            <fieldset>
              <input type="hidden" name="tipoItem" value="${(debt.tipoItem)!'manual'}" />
              <div class="form-shop-row js-shopper-selector">
                <label for="shopper">Shopper</label>
                <input id="shopper" type="text" value="${(debt.shopper.name)!''}" class="item-field js-shopper" <#if readOnly>disabled="true"</#if> />
                <a id="clear-shopper" href="#" class="clear js-clear">limpiar</a>
                <input type="hidden" name="shopperId" value="${(debt.shopper.id?c)!''}" class="js-shopper-id" />
                <input type="hidden" name="shopperDni" value="${(debt.shopperDni)!''}" class="js-shopper-dni" />
              </div>
              <div class="form-shop-row">
                <label for="client">Cliente</label>
                <select id="client" name="clientId" class="item-field js-client-id" <#if readOnly>disabled="true"</#if>>
                <#list model["clients"] as client>
                  <option value="${client.id?c}" <#if ((debt.client.id)!0) == client.id>selected="selected"</#if>>${client.name}</option>
                </#list>
                <select>
              </div>
              <div class="form-shop-row">
                <label for="sucursal">Sucursal</label>
                <select id="sucursal" name="branchId" class="item-field js-sucursales" <#if readOnly>disabled="true"</#if>>
                <#if debt?? && debt.branch??>
                  <option value="${debt.branch.id?c}">${debt.branch.address}</option>
                </#if>
                </select>
              </div>
              <div class="form-shop-row">
                <label>&nbsp;</label>
                <input type="text" name="branchDescription" value="${(debt.branchDescription)!''}" class="item-field" <#if readOnly>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row">
                <label for="route">Recorrido</label>
                <input type="text" id="route" name="route" value="${(debt.route)!''}" class="item-field" <#if readOnly>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row">
                <label for="fecha">Fecha de la visita:</label>
                <input type="text" id="fecha" name="fecha" value="${(debt.fecha?string('dd/MM/yyyy'))!""}" class="item-field js-date js-fecha-visita" <#if readOnly>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row">
                <label for="tipoPago">Tipo de pago:</label>
                <select id="tipoPago" name="tipoPago" class="item-field" <#if readOnly>disabled="true"</#if>>
                <#list model["tiposPago"] as tipoPago>
                  <option value="${tipoPago}" <#if debt?? && tipoPago == debt.tipoPago>selected="true"</#if>>${tipoPago.description}</option>
                </#list>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="importe">Importe ($):</label>
                <input type="text" id="importe" name="importe" value="${(debt.importe?string['0.##']?replace(',', '.'))!""}" class="item-field js-importe" <#if readOnly>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row">
                <label for="observaciones">Observaciones:</label>
                <textarea id="observaciones" name="observaciones" class="item-field" <#if readOnly>disabled="true"</#if>>${(debt.observaciones)!""}</textarea>
              </div>
            </fieldset>
          </div>
          <ul class="action-columns">
            <li>
              <#if debt?? && readOnly>
                <a href="<@spring.url '/debt/edit/${debt.id?c}'/>" class="btn-shop-small">Editar</a>
              <#else>
                <input type="button" class="btn-shop-small js-add" value="Guardar" />
              </#if>
            </li>
          </ul>
        </div>
        <!-- FIN FILA 1 -->
      </form>
    </div>
  </body>
</html>