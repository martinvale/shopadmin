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

      jQuery(document).ready(function() {
        var titularSelector = App.widget.TitularSelector(jQuery(".js-billing-selector"),
            "<@spring.url '/titular/suggest'/>");
        titularSelector.render();
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
        <h2 class="container-tit">Titular</h2>

        <#assign titular = model["titular"] />
        <form action="update" method="POST" class="form-shop form-shop-big js-orden-pago">
          <!-- FILA 1 -->
          <div class="cell box-gray">
            <fieldset>
              <div class="form-shop-row titular-selector">
                <label for="name" class="mandatory">Titular</label>
                <input id="name" name="name" <#if titular.titularTipo == 1>readonly="true"</#if> type="text" value="${titular.name}" class="item-field" />
                <input type="hidden" name="titularId" value="${titular.titularId?c}" />
                <input type="hidden" name="titularTipo" value="${titular.titularTipo?c}" />
              </div>
              <#if titular.titularTipo?c == '1'>
              <div class="form-shop-row">
                <label for="login">Login shopmetrics</label>
                <input id="login" type="text" name="login" value="${titular.loginShopmetrics!''}" />
              </div>
              <#else>
              <div class="form-shop-row">
                <label for="email">Email</label>
                <input id="email" type="text" name="email" value="${titular.email!''}" />
              </div>
              </#if>
            </fieldset>
            <fieldset>
              <legend>Datos de facturaci&oacute;n:</legend>
              <div>
                <input type="radio" name="linked" value="true" <#if titular.linked>checked="checked"</#if>> Vinculado a otro titular <br/>
                <div class="form-shop-row js-billing-selector">
                  <label for="billingname">Nombre</label>
                  <input id="billingName" name="billingName" type="text" value="${titular.billingName!''}" class="item-field js-titular-nombre" />
                  <input type="hidden" name="billingId" value="${(titular.billingId?c)!''}" class="js-titular-id" />
                  <input type="hidden" name="billingTipo" value="${(titular.billingTipo?c)!''}" class="js-titular-tipo" />
                </div>
              </div>
              <div>
                <input type="radio" name="linked" value="false" <#if !titular.linked>checked="checked"</#if>> Usando mi propia cuenta
                <div class="form-shop-row">
                  <label for="cuit">CUIT/CUIL</label>
                  <input id="cuit" type="text" name="cuit" value="${titular.cuit!''}" />
                </div>
                <div class="form-shop-row">
                  <label for="tipoFactura">Factura</label>
                  <select id="tipoFactura" name="tipoFactura">
                    <option value=""></option>
                    <option value="A" <#if ((titular.factura)!'') == 'A'>selected="selected"</#if>>A</option>
                    <option value="C" <#if ((titular.factura)!'') == 'C'>selected="selected"</#if>>C</option>
                    <option value="M" <#if ((titular.factura)!'') == 'M'>selected="selected"</#if>>M</option>
                  </select>
                </div>
                <div class="form-shop-row">
                  <label for="banco">Banco</label>
                  <input id="banco" type="text" name="banco" value="${titular.banco!''}" />
                </div>
                <div class="form-shop-row">
                  <label for="cbu">CBU/Alias</label>
                  <input type="text" name="cbu" id="cbu" value="${titular.cbu!''}"/>
                </div>
                <div class="form-shop-row">
                  <label for="number">Nro de cuenta</label>
                  <input type="text" name="number" id="number" value="${titular.number!''}"/>
                </div>
              </divs>
            </fieldset>
          </div>
          <ul class="action-columns">
            <li><input type="submit" class="btn-shop-small" value="Guardar"></li>
            <li><input type="button" class="btn-shop-small js-cancel" onclick="history.back();" value="Cancelar"></li>
          </ul>
      </form>
    </div>
  </body>
</html>