<!DOCTYPE html>
<html>
  <head>
    <#import "/spring.ftl" as spring />
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />

    <link rel="stylesheet" href="<@spring.url '/css/jquery-ui/jquery-ui.css'/>">

    <link rel="stylesheet" href="<@spring.url '/css/base.css'/>">
    <link rel="stylesheet" href="<@spring.url '/css/shop.css'/>">
    <link rel="stylesheet" href="<@spring.url '/css/custom.css'/>">

    <link rel="stylesheet" href="<@spring.url '/font-awesome/css/font-awesome.min.css'/>">

    <script src="<@spring.url '/script/jquery.js'/>"></script>
    <script src="<@spring.url '/script/jquery-ui.js'/>"></script>

    <script src="<@spring.url '/script/spin.js'/>"></script>
    <script src="<@spring.url '/script/jquery.spin.js'/>"></script>

    <script src="<@spring.url '/script/pure.min.js'/>"></script>
    <script src="<@spring.url '/script/livevalidation.js'/>"></script>

    <#assign canEdit = model["user"].hasFeature('edit_order') />

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.OrdenPago = function (container) {

  var validations = [];

  var currentTitular = null;

  var initEventListeners = function () {
    container.find("select[name=tipoFactura]").change(function() {
      var tipoFacturaSeleccionada = jQuery(this).val();
      var ivaField = container.find("input[name=iva]");
      if (tipoFacturaSeleccionada == 'A') {
        ivaField.val('21');
      } else {
        ivaField.val('0');
      }
      var tipoFacturaHint = container.find(".js-tipo-factura-hint");
      if (currentTitular && currentTitular.factura != tipoFacturaSeleccionada) {
        tipoFacturaHint.show();
      } else {
        tipoFacturaHint.hide();
      }
    });

    container.find(".js-save-order").click(function () {
      if (LiveValidation.massValidate(validations)) {
        container.submit();
      }
    });
  };

  var initValidators = function () {
    var fechaPagoValidation = new LiveValidation("fechaPago");
    fechaPagoValidation.add(Validate.Presence, {
        failureMessage: "La fecha de pago es obligatoria"
    });
    validations.push(fechaPagoValidation);

    var titularValidation = new LiveValidation("titular");
    titularValidation.add(Validate.Presence, {
        failureMessage: "El titular es obligatorio"
    });
    validations.push(titularValidation);

    var ivaValidation = new LiveValidation("iva");
    ivaValidation.add(Validate.Presence, {
        failureMessage: "El porcentaje de IVA es obligatorio"
    });
    validations.push(ivaValidation);
  };

  var onTitularSelected = function (titularObject) {
    currentTitular = titularObject;

    container.find("input[name=cuit]").val(titularObject.cuit);
    container.find("input[name=cbu]").val(titularObject.cbu);
    container.find("input[name=banco]").val(titularObject.banco);
    container.find("input[name=accountNumber]").val(titularObject.number);
    container.find(".js-billing").html(titularObject.billingName);
    if (titularObject.factura) {
      var tipoFacturaField = container.find("select[name=tipoFactura]");
      tipoFacturaField.val(titularObject.factura);
      tipoFacturaField.change();
    }
  };

  return {
    render: function () {
      var titularSelector = App.widget.TitularSelector(container.find(".js-titular-selector"),
          "<@spring.url '/titular/suggest'/>", onTitularSelected);
      titularSelector.render();

      container.find(".js-date" ).datepicker({
        //minDate: new Date(),
        dateFormat: 'dd/mm/yy',
        onSelect: function(dateText, datePicker) {
          $(this).attr('value', dateText);
        }
      });
      initEventListeners();
      initValidators();
    }
  };
}


      jQuery(document).ready(function() {
        var orderContainer = jQuery(".js-orden-pago");
        var orderEditor = App.widget.OrdenPago(orderContainer);
        orderEditor.render();
      });

    </script>

    <script src="<@spring.url '/script/TitularSelector.js'/>"></script>
    <script src="<@spring.url '/script/LoadingIndicator.js'/>"></script>

<style>
.LV_validation_message.LV_valid {
  display: none;
}

.LV_invalid {
  color:#CC0000;
}
    
.LV_valid_field,
input.LV_valid_field:hover, 
input.LV_valid_field:active,
textarea.LV_valid_field:hover, 
textarea.LV_valid_field:active {
  border: 1px solid #00CC00;
}
    
.LV_invalid_field, 
input.LV_invalid_field:hover, 
input.LV_invalid_field:active,
textarea.LV_invalid_field:hover, 
textarea.LV_invalid_field:active {
  border: 1px solid #CC0000;
}

.titular-selector ul {
  width: 200px;
}

</style>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
        <h2 class="container-tit">Orden de pago</h2>

        <#assign action = "create" />
        <#if model["ordenPago"]??>
          <#assign order = model["ordenPago"] />
          <#assign action = "../save" />
        </#if>
        <form action="${action}" method="POST" class="form-shop form-shop-big js-orden-pago">
          <!-- FILA 1 -->
          <div class="cell box-gray">
            <fieldset>
              <div class="form-shop-row">
                <label>N&uacute;mero</label>
                <input type="text" name="numeroOrden" readOnly="true" value="${(order.numero?c)!''}"/>
              </div>
              <div class="form-shop-row titular-selector js-titular-selector">
                <label class="mandatory">Titular</label>
                <input id="titular" type="text" value="${model['titularNombre']!''}" class="item-field js-titular-nombre" />
                <input type="hidden" name="titularId" value="${(order.proveedor?c)!''}" class="js-titular-id" />
                <input type="hidden" name="tipoTitular" value="${(order.tipoProveedor?c)!''}" class="js-titular-tipo" />
              </div>
              <div class="form-shop-row">
                <label for="fechaPago" class="mandatory">Fecha pago</label>
                <input type="text" id="fechaPago" name="fechaPago" class="js-date" value="${(order.fechaPago?string('dd/MM/yyyy'))!''}" <#if !canEdit>disabled="true"</#if>/>
              </div>
            </fieldset>
            <fieldset>
              <legend>Datos de facturaci&oacute;n:</legend>
              <div class="form-shop-row">
                <label>Titular cuenta: </label><span class="js-billing"></span>
              </div>
              <div class="form-shop-row">
                <label for="tipoFactura" class="mandatory">Factura</label>
                <select id="tipoFactura" name="tipoFactura">
                  <option value="S/F" <#if ((order.tipoFactura)!'') == 'S/F'>selected="selected"</#if>>S/F</option>
                  <option value="A" <#if ((order.tipoFactura)!'') == 'A'>selected="selected"</#if>>A</option>
                  <option value="C" <#if ((order.tipoFactura)!'') == 'C'>selected="selected"</#if>>C</option>
                  <option value="M" <#if ((order.tipoFactura)!'') == 'M'>selected="selected"</#if>>M</option>
                </select>
                <span class="hint js-tipo-factura-hint" style="display:none">El tipo de factura seleccionado no es el sugerido para este titular</span>
              </div>
              <div class="form-shop-row">
                <label for="numeroFactura">Factura N&deg;</label>
                <input type="number" name="numeroFactura" id="numeroFactura" value="${(order.numeroFactura)!''}"/>
              </div>
              <div class="form-shop-row">
                <label for="iva" class="mandatory">IVA %</label>
                <input type="text" name="iva" id="iva" value="${(order.iva)!'0'}"/>
              </div>
              <div class="form-shop-row">
                <label for="cuit">CUIT/CUIL</label>
                <input type="text" name="cuit" id="cuit" value="${(order.cuit)!''}"/>
              </div>
              <div class="form-shop-row">
                <label for="banco">Banco</label>
                <input type="text" name="banco" id="banco" value="${(order.banco)!''}"/>
              </div>
              <div class="form-shop-row">
                <label for="cbu">CBU/Alias</label>
                <input type="text" name="cbu" id="cbu" value="${(order.cbu)!''}"/>
              </div>
              <div class="form-shop-row">
                <label for="accountNumber">Nro de Cuenta</label>
                <input type="text" name="accountNumber" id="accountNumber" value="${(order.accountNumber)!''}"/>
              </div>
            </fieldset>
            <fieldset>
              <legend>Otros datos:</legend>
              <div class="form-shop-row">
                <label for="localidad">Localidad</label>
                <select id="localidad" name="localidad">
                  <option value="Buenos Aires" <#if ((order.localidad)!'') == 'Buenos Aires'>selected="selected"</#if>>Buenos Aires</option>
                  <option value="Interior" <#if ((order.localidad)!'') == 'Interior'>selected="selected"</#if>>Interior</option>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="observaciones">Observaciones</label>
                <textarea id="observaciones" name="observaciones" class="item-field">${(order.observaciones)!''}</textarea>
              </div>
              <div class="form-shop-row">
                <label for="observacionesShopper">Obs. p/shopper</label>
                <textarea id="observacionesShopper" name="observacionesShopper" class="item-field">${(order.observacionesShopper)!''}</textarea>
              </div>
            </fieldset>
          </div>
          <ul class="action-columns">
            <li><input type="button" class="btn-shop-small js-save-order" value="Guardar" <#if !canEdit>disabled="true"</#if>></li>
          </ul>
      </form>
    </div>
  </body>
</html>