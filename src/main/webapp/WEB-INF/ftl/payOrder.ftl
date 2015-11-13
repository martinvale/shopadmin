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

    <#assign canPay = model["user"].hasFeature('pay_order') />
    <#assign order = model["ordenPago"] />
    <#assign estadoOrden = "Abierta" />

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.EditorPagarOrden = function (container) {

  var medioPagoValidation;

  var chequeraValidation;

  var chequeValidation;

  var fechaChequeValidation;

  var transferIdValidation;

  var initEventListeners = function () {
    var medioDefault = container.find(".js-medio-pago-predeterminado");
    var sinMedioSeleccionado = container.find(".js-sin-medio-pago");
    var asociarMedio = container.find(".js-asociar-medio");

    container.find(".js-medio-pago" ).change(function () {
      medioDefault.hide();
      sinMedioSeleccionado.hide();
      asociarMedio.show();
      if (jQuery(this).val() == '1' || jQuery(this).val() == '2') {
        container.find(".js-medio-cheque").show();
        container.find(".js-medio-transfer").hide();
      } else {
        container.find(".js-medio-cheque").hide();
        container.find(".js-medio-transfer").show();
      }
    });

    asociarMedio.click(function (event) {
      event.preventDefault();

      var medioPagoSeleccionado = container.find(".js-medio-pago").val();

      jQuery.ajax({
        url: "../asociarMedioPago",
        data: {
          numeroOrden: ${order.numero?c},
          medioPagoId: medioPagoSeleccionado
        },
        method: 'POST'
      }).done(function (data) {
        asociarMedio.hide();
        sinMedioSeleccionado.hide();
        medioDefault.text('Medio de pago predeterminado: ' + data);
        medioDefault.show();
      })
    });

    container.find(".js-pay-order").click(function () {
      var validations = [];
      validations.push(medioPagoValidation);
      var tipoPagoSeleccionado = container.find(".js-medio-pago").val();
      if (tipoPagoSeleccionado == '1' || tipoPagoSeleccionado == '2') {
        validations.push(chequeraValidation);
        validations.push(chequeValidation);
        validations.push(fechaChequeValidation);
      } else {
        validations.push(transferIdValidation);
      }
      if (LiveValidation.massValidate(validations)) {
        container.submit();
      }
    });

    container.find(".js-caratula" ).click(function () {
      window.open('../caratula/${order.numero?c}', "", "width=700, height=600");
    });
  };

  var initValidators = function () {
    medioPagoValidation = new LiveValidation("medioPago");
    medioPagoValidation.add(Validate.Exclusion, {
        within: ["Seleccionar"],
        failureMessage: "El medio de pago es obligatorio"
    });

    chequeraValidation = new LiveValidation("numeroChequera");
    chequeraValidation.add(Validate.Presence, {
        failureMessage: "El nro de la chequera es obligatorio"
    });

    chequeValidation = new LiveValidation("numeroCheque");
    chequeValidation.add(Validate.Presence, {
        failureMessage: "El nro del cheque es obligatorio"
    });

    fechaChequeValidation = new LiveValidation("fechaCheque");
    fechaChequeValidation.add(Validate.Presence, {
        failureMessage: "La fecha del cheque es obligatoria"
    });

    transferIdValidation = new LiveValidation("transferId");
    transferIdValidation.add(Validate.Presence, {
        failureMessage: "El ID de la transferencia es obligatorio"
    });
  };

  return {
    render: function () {
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
        var editorContainer = jQuery(".js-pagar-orden-editor");
        var orderEditor = App.widget.EditorPagarOrden(editorContainer);
        orderEditor.render();
      });

    </script>

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
        <h2 class="container-tit">Completar la forma de pago de la orden ${order.numero?c}</h2>

        <form action="${order.numero?c}" method="POST" class="form-shop form-shop-big js-pagar-orden-editor">
          <!-- FILA 1 -->
          <div class="cell box-gray">
            <fieldset>
              <div class="form-shop-row">
                <label>N&uacute;mero</label>
                <input type="text" name="numeroOrden" readOnly="true" value="${(order.numero?c)!''}"/>
              </div>
              <div class="form-shop-row">
                <label for="medioPago" class="mandatory">M. de pago</label>
                <select id="medioPago" name="medioPagoId" class="js-medio-pago">
                  <option value="Seleccionar">Seleccionar</option>
                  <#list model["mediosPago"] as medioPago>
                    <option value="${medioPago.id}" <#if medioPago.id?c == ((order.medioPago.id?c)!'')>selected="selected"</#if>>${medioPago.description}</option>
                  </#list>
                </select>
                <div class="js-medio-pago-asociado">
                  <#if model["medioPagoPredeterminado"]??>
                    <span class="medio-pago js-medio-pago-predeterminado">Medio de pago predeterminado: ${model["medioPagoPredeterminado"]}</span>
                    <a href="#" class="asociar-medio js-asociar-medio" style="display:none">Asociar medio de pago al titular</a>
                  <#else>
                    <span class="medio-pago js-medio-pago-predeterminado" style="display:none">Medio de pago predeterminado: </span>
                    <#if ((order.medioPago.id?c)!'') != "">
                      <a href="#" class="asociar-medio js-asociar-medio">Asociar medio de pago al titular</a>
                    <#else>
                      <span class="sin-medio-pago js-sin-medio-pago">(El titular no tiene un medio de pago asociado)</span>
                      <a href="#" class="asociar-medio js-asociar-medio" style="display:none">Asociar medio de pago al titular</a>
                    </#if>
                  </#if>
                </div>
              </div>
              <div class="form-shop-row js-medio-cheque" <#if ((order.medioPago.id?c)!'') != '1' && ((order.medioPago.id?c)!'') != '2'>style="display:none;"</#if>>
                <label for="numeroChequera">Chequera N&deg;</label>
                <input type="text" name="numeroChequera" id="numeroChequera" value="${(order.numeroChequera)!''}" <#if !canPay || estadoOrden != 'Abierta'>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row js-medio-cheque" <#if ((order.medioPago.id?c)!'') != '1' && ((order.medioPago.id?c)!'') != '2'>style="display:none;"</#if>>
                <label for="numeroCheque">Cheque N&deg;</label>
                <input type="text" name="numeroCheque" id="numeroCheque" value="${(order.numeroCheque)!''}" <#if !canPay || estadoOrden != 'Abierta'>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row js-medio-cheque" <#if ((order.medioPago.id?c)!'') != '1' && ((order.medioPago.id?c)!'') != '2'>style="display:none;"</#if>>
                <label for="fechaCheque">Fecha cheque</label>
                <input type="text" id="fechaCheque" name="fechaCheque" class="js-date" value="${(order.fechaCheque?string('dd/MM/yyyy'))!''}" <#if !canPay || estadoOrden != 'Abierta'>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row js-medio-transfer" <#if ((order.medioPago.id?c)!'') != '3'>style="display:none;"</#if>>
                <label for="transferId">ID Transfer</label>
                <input type="text" name="transferId" id="transferId" value="${(order.idTransferencia)!''}" <#if !canPay || estadoOrden != 'Abierta'>disabled="true"</#if>/>
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
            <li><input type="button" class="btn-shop-small js-pay-order" value="Pagar" <#if !canPay>disabled="true"</#if>></li>
            <li><a href="../items/${order.numero?c}" class="btn-shop-small">Volver a editar los items</a></li>
            <li><input type="button" class="btn-shop-small js-caratula" value="Car&aacute;tula" /></li>
          </ul>
      </form>
    </div>
  </body>
</html>