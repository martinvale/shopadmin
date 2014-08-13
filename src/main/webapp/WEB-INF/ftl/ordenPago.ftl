<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="/css/base.css">
    <link rel="stylesheet" href="/css/shop.css">
    <link rel="stylesheet" href="/css/custom.css">

    <link rel="stylesheet" href="/css/jquery-ui/jquery-ui.css">

    <script src="/script/jquery.js"></script>
    <script src="/script/jquery-ui.js"></script>
    <script src="/script/livevalidation.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.ShopperSelector = function (container) {
  var selector = container.find(".js-shopper");

  var initEventListeners = function () {
    var filter = selector.autocomplete({
      source: "/services/shoppers/suggest",
      minLength: 2,
      select: function(event, ui) {
        selector.val(ui.item.name);
        container.find(".js-shopper-id").val(ui.item.id);
        return false;
      }
    });
    filter.data( "ui-autocomplete" )._renderItem = function(ul, item) {
      return $("<li>")
        .append("<a>" + item.name + "</a>")
        .appendTo(ul);
    };
  };

  return {
    render: function () {
      initEventListeners();
    }
  };
};

App.widget.ItemsSelector = function (container) {

  var itemDialog = container.dialog({autoOpen: false, width: 900});

  var shopperSelector = new App.widget.ShopperSelector(container.find(".js-shopper-selector"));

  var initialize = function () {
    itemDialog.find(".js-tabs").tabs();
    shopperSelector.render();
  }

  var initEventListeners = function () {
    container.find(".js-mcd-items .js-buscar" ).click(function () {
      alert("hola");
    });
  };

  return {
    render: function () {
      initialize();
      initEventListeners();
    },
    open: function () {
      itemDialog.dialog("open");
    }
  };
}

App.widget.OrdenPago = function (container) {

  var itemSelector = new App.widget.ItemsSelector(jQuery("#item-selector"));

  var initialize = function () {
    container.find(".js-date" ).datepicker({
      onSelect: function(dateText, datePicker) {
        $(this).attr('value', dateText);
      }
    });
    itemSelector.render();
  }

  var initEventListeners = function () {
    container.find(".js-add-item" ).click(function () {
      itemSelector.open();
    });
  };

  var initValidators = function () {
    var tipoFacturaValidation = new LiveValidation("tipoFactura");
    tipoFacturaValidation.add(Validate.Exclusion, {
        within: ["Seleccionar"],
        failureMessage: "El tipo de factura es obligatorio"
    });
    var ivaValidation = new LiveValidation("iva");
    ivaValidation.add(Validate.Presence, {
        failureMessage: "El porcentaje de IVA es obligatorio"
    });
    var fechaPagoValidation = new LiveValidation("fechaPago");
    fechaPagoValidation.add(Validate.Presence, {
        failureMessage: "La fecha de pago es obligatoria"
    });
    var medioPagoValidation = new LiveValidation("medioPago");
    medioPagoValidation.add(Validate.Exclusion, {
        within: ["Seleccionar"],
        failureMessage: "El medio de pago es obligatorio"
    });
  };

  return {
    render: function () {
      initialize();
      initEventListeners();
      initValidators();
    }
  };
}


      jQuery(document).ready(function() {

        var titularSelectorWidget = new App.widget.TitularSelector(jQuery(".js-titular-selector"));
        titularSelectorWidget.render();

        var ordenPago = new App.widget.OrdenPago(jQuery(".js-orden-pago"));
        ordenPago.render();
      });
    </script>

    <script src="/script/TitularSelector.js"></script>

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
</style>
  </head>
  <body>
    <#assign user = model["user"] />
    <header>
      <div class="header-box">
        <h1>Shopnchek<span class="tag-intranet">intranet</span></h1>
        <p class="user"> ${user.username} <a href="/logout">Salir</a></p>
      </div>
    </header>
    <div class="container-box-plantilla">
        <h2 class="container-tit">Orden de pago</h2>
        <#assign numero = "" />
        <#assign tipoFactura = "" />
        <#assign fechaPago = "" />
        <#assign medioPagoId = "" />
        <#assign state = "" />
        <#assign iva = "" />
        <#assign facturaNumero = "" />
        <#assign fechaCheque = "" />
        <#assign localidad = "" />
        <#assign numeroChequera = "" />
        <#assign numeroCheque = "" />
        <#assign transferId = "" />
        <#if model["ordenPago"]??>
          <#assign numero = "${model['ordenPago'].numero?c}" />
          <#assign tipoFactura = "${model['ordenPago'].tipoFactura}" />
          <#assign fechaPago = "${model['ordenPago'].fechaPago?string('dd/MM/yyyy')}" />
          <#assign medioPagoId = "${model['ordenPago'].medioPago.id?c}" />
          <#assign state = "${model['ordenPago'].estado.id?c}" />
          <#assign iva = "${model['ordenPago'].iva?c}" />
          <#assign facturaNumero = "${model['ordenPago'].numeroFactura}" />
          <#assign fechaCheque = "${model['ordenPago'].fechaCheque?string('dd/MM/yyyy')}" />
          <#assign localidad = "${model['ordenPago'].localidad}" />
          <#assign numeroChequera = "${model['ordenPago'].numeroChequera}" />
          <#assign numeroCheque = "${model['ordenPago'].numeroCheque}" />
          <#assign transferId = "${model['ordenPago'].idTransferencia}" />
        </#if>

        <#assign action = "create" />
        <#if model["ordenPago"]??>
          <#assign action = "save" />
        </#if>
        <form action="${action}" method="POST" class="form-shop form-shop-big js-orden-pago">
          <!-- FILA 1 -->
          <div class="cell">
            <div class="box-green cell-c1">
              <div class="form-shop-row-left">
                <label for="number">N&uacute;mero</label>
                <input type="text" name="numeroOrden" readOnly=true id="number" value="${numero}"/>
              </div>
            </div>
            <div class="box-green cell-c2">
              <div class="form-shop-options-left js-titular-selector">
                <p class="mandatory">Titular</p>
                <ul>
                  <li class="form-shop">
                    <input type="radio" name="tipoTitular" id="shopper" value="1" class="js-shopper" checked="checked">
                    <label for="shopper">Shopper</label>
                  </li>
                  <li class="form-shop">
                    <input type="radio" name="tipoTitular" id="proveedor" value="2" class="js-proveedor">
                    <label for="proveedor">Proveedor</label>
                  </li>
                </ul>
                <div class="combo-titular">
                  <#assign titularId = "" />
                  <#assign titularNombre = "" />
                  <#if model["titular"]??>
                    <#assign titularId = "${model['titular'].id?c}" />
                    <#assign titularNombre = "${model['titular'].name}" />
                  </#if>
                  <input type="text" value="${titularNombre}" class="js-titulares" />
                  <input type="hidden" name="titularId" value="${titularId}" class="js-titular-id" />
                </div>
              </div>
            </div>
          </div>
          <!-- FIN FILA 1 -->
          <!-- FIN FILA 2 -->
          <ul class="columnas-form">
            <li>
              <div class="form-shop-row">
                <label for="tipoFactura" class="mandatory">Factura</label>
                <select id="tipoFactura" name="tipoFactura">
                  <option value="Seleccionar">Seleccionar</option>
                  <option value="S/F">S/F</option>
                  <option value="A" <#if tipoFactura == 'A'>selected="selected"</#if>>A</option>
                  <option value="C" <#if tipoFactura == 'C'>selected="selected"</#if>>C</option>
                  <option value="M" <#if tipoFactura == 'M'>selected="selected"</#if>>M</option>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="fechaPago" class="mandatory">Fecha pago</label>
                <input type="text" id="fechaPago" name="fechaPago" class="js-date" value="${fechaPago}"/>
              </div>
              <div class="form-shop-row">
                <label for="medioPago" class="mandatory">M. de pago</label>
                <select id="medioPago" name="medioPagoId">
                  <option value="Seleccionar">Seleccionar</option>
                  <#list model["mediosPago"] as medioPago>
                    <option value="${medioPago.id}" <#if medioPago.id?c == medioPagoId>selected="selected"</#if>>${medioPago.description}</option>
                  </#list>
                </select>
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="numeroFactura">Factura N&deg;</label>
                <input type="number" name="numeroFactura" id="numeroFactura" value="${facturaNumero}"/>
              </div>
              <div class="form-shop-row">
                <label for="state" class="mandatory">Estado</label>
                <select id="state" name="estadoId">
                  <#list model["orderStates"] as orderState>
                    <option value="${orderState.id}" <#if orderState.id?c == state>selected="selected"</#if>>${orderState.description}</option>
                  </#list>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="numeroChequera">Chequera N&deg;</label>
                <input type="number" name="numeroChequera" id="numeroChequera" value="${numeroChequera}"/>
              </div>
              <div class="form-shop-row">
                <label for="transferId">ID Transfer</label>
                <input type="number" name="transferId" id="transferId" value="${transferId}"/>
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="iva" class="mandatory">IVA %</label>
                <input type="text" name="iva" id="iva" value="${iva}"/>
              </div>
              <div class="form-shop-row">
                <label for="localidad">Localidad</label>
                <select id="localidad" name="localidad">
                  <option value="">Seleccionar</option>
                  <option value="Buenos Aires" <#if localidad == 'Buenos Aires'>selected="selected"</#if>>Buenos Aires</option>
                  <option value="Interior" <#if localidad == 'Interior'>selected="selected"</#if>>Interior</option>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="numeroCheque">Cheque N&deg;</label>
                <input type="number" name="numeroCheque" id="numeroCheque" value="${numeroCheque}"/>
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="fechaCheque">Fecha cheque</label>
                <input type="text" id="fechaCheque" name="fechaCheque" class="js-date" value="${fechaCheque}">
              </div>
            </li>
          </ul>
          <ul class="action-columns">
            <li> <input type="submit" class="btn-shop-small" value="Guardar" name="save"></li>
            <li> <input type="button" class="btn-shop-small" value="Car&aacute;tula" name="carta" <#if !model["ordenPago"]??>disabled="true"></#if></li>
            <li> <input type="button" class="btn-shop-small" value="Observaciones" name="obs" <#if !model["ordenPago"]??>disabled="true"></#if></li>
            <li> <input type="button" class="btn-shop-small" value="Obsp/Shopper" name="shop" <#if !model["ordenPago"]??>disabled="true"></#if></li>
          </ul>
          <!-- FIN FILA 2 -->

         <!-- FILA 3 -->
         <h2 class="subtitulo">Items</h2>
         <div class="scroll-table">
             <table summary="Listado de cobros en MercadoPago" class="table-form ">
                <thead>
                    <tr>
                        <th scope="col">Shopper</th>
                        <th scope="col">Cliente</th>
                        <th scope="col">Mes</th>
                        <th scope="col">A&ntilde;o</th>
                        <th scope="col">Sucursal</th>
                        <th scope="col">Pago</th>
                        <th scope="col">Importe</th>
                        <th scope="col">DNI</th>
                        <th scope="col">Asignaci&oacute;n</th>
                        <th scope="col">Fecha</th>
                    </tr>
                </thead>
                <tbody>
                  <#if model["ordenPago"]??>
                    <#list model["ordenPago"].items as item>
                    <tr>
                        <td></td>
                        <td>${item.cliente!''}</td>
                        <td>${item.mes!''}</td>
                        <td>${item.anio!''}</td>
                        <td>${item.sucursal!''}</td>
                        <td></td>
                        <td>${item.importe}</td>
                        <td>${item.shopperDni}</td>
                        <td>${item.asignacion!''}</td>
                        <td>${item.fecha!''}</td>
                    </tr>
                    </#list>
                  </#if>
                </tbody>
            </table>
        </div>
        <ul class="action-columns">
            <li> <input type="button" class="btn-shop-small js-add-item" value="Agregar" <#if !model["ordenPago"]??>disabled="true"></#if>></li>
            <li> <input type="submit" class="btn-shop-small" value="Deuda Shopper" name="deusa" disabled="true"></li>
            <li> <input type="submit" class="btn-shop-small" value="Eliminar item" name="delete" disabled="true"></li>
        </ul>

  <!-- FIN FILA 3 -->
  <!-- FILA 4 -->
  <h2 class="subtitulo"> Totales </h2>
         <ul class="columnas-form col-three">
            <li>
                <div class="form-shop-row">
                    <label for="subt0">Subt. honorarios</label>
                    <input type="text" id="sbt0">
                </div>
                <div class="form-shop-row">
                    <label for="sbt1">Subt. reintegros</label>
                    <input type="text" id="sbt1">
                </div>
            </li>
            <li>
                <div class="form-shop-row">
                    <label for="facN">IVA $</label>
                     <input type="text" id="sbt1">
                </div>
                <div class="form-shop-row">
                   <label for="sbt3">Subt. otros gastos</label>
                    <input type="text" id="sbt3">
                </div>
            </li>
            <li>
                  <div class="form-shop-row">
                   <label for="honorarios">Honorarios c/IVA</label>
                    <input type="text" id="honorarios">
                </div>
                
                <div class="form-shop-row">
                    <label for="tot">Total general</label>
                    <input type="text" max="99" min="18" name="tot" id="tot">
                </div>
            </li>
         </ul>

        <div class="actions-form">
          <ul class="action-columns">
            <li> <input type="submit" class="btn-shop" value="Imprimir" name="imp" disabled="true"></li>
            <li> <input type="submit" class="btn-shop" value="Imprimir Detalle" name="detail" disabled="true"></li>
            <li> <input type="submit" class="btn-shop-action" value="Eliminar Shopper" name="deleteshop" disabled="true"></li>
            <li> <input type="button" class="btn-shop-action" value="Cerrar" name="cerrar"></li>
          </ul>
        </div>
    </form>
    </div>
    <div id="item-selector" style="display:none;">
        <div class="container-box-plantilla">
            <h2 class="container-tit">Items de la orden de pago</h2>
            <form class="">
              <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                <div class="ch-popover-content">
                    Revisa los datos. Debes completar campos "Número" y "Factura".
                </div>
              </div-->
            <!-- FILA 1 -->
                <div class="cell">
                    <div class="box-green">
                      <div class="form-shop-row-left shopper-widget js-shopper-selector">
                        <label for="shopper">Shooper: </label>
                        <#assign titularId = "" />
                        <#assign titularNombre = "" />
                        <#if model["titular"]??>
                          <#assign titularId = "${model['titular'].id?c}" />
                          <#assign titularNombre = "${model['titular'].name}" />
                        </#if>
                        <input type="text" value="" id="shopper" class="js-shopper" />
                        <input type="hidden" name="shopperId" value="" class="js-shopper-id" />
                      </div>
                    </div>
                    <div class="tabs_holder js-tabs">
                        <ul>
                            <!--li><a href="#your-tab-id-1">I Plan </a></li-->
                            <li><a href="#your-tab-id-2">MCD</a></li>
                            <!--li><a href="#your-tab-id-3">GAC</a></li>
                            <li><a href="#your-tab-id-4">Adicionales</a></li>
                            <li><a href="#your-tab-id-5">Manuales</a></li-->
                        </ul>
                        <div class="content_holder">
                          <!--div id="your-tab-id-1">
                           I Plan
                          </div-->
                          <div id="your-tab-id-2" class="js-mcd-items">
                            <ul class="action-columns">
                                <li><input type="button" class="btn-shop-small js-buscar" value="Buscar"></li>
                            </ul>
                            <div class="scroll-table">
                                 <table summary="Lista de items" class="table-form ">
                                    <thead>
                                        <tr>
                                            <th scope="col">Shopper</th>
                                            <th scope="col">Cliente</th>
                                            <th scope="col">Mes</th>
                                            <th scope="col">A&ntilde;o</th>
                                            <th scope="col">Sucursal</th>
                                            <th scope="col">Pago</th>
                                            <th scope="col">Importe</th>
                                            <th scope="col">DNI</th>
                                            <th scope="col">Asignaci&oacute;n</th>
                                            <th scope="col">Fecha</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                         <tr>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                            <td></td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                          </div>
                          <!--div id="your-tab-id-3">
                           Some content tab 3
                          </div>
                          <div id="your-tab-id-4">
                           Some content tab 4
                          </div>
                           <div id="your-tab-id-5">
                           Some content tab 5
                          </div-->
                        </div>
                    </div>


            <!-- Item -->
             <h2 class="subtitulo">Items</h2>
             <div class="scroll-table">
                 <table summary="Lista de items" class="table-form ">
                    <thead>
                        <tr>
                            <th scope="col">Shopper</th>
                            <th scope="col">Cliente</th>
                            <th scope="col">Mes</th>
                            <th scope="col">A&ntilde;o</th>
                            <th scope="col">Sucursal</th>
                            <th scope="col">Pago</th>
                            <th scope="col">Importe</th>
                            <th scope="col">DNI</th>
                            <th scope="col">Asignaci&oacute;n</th>
                            <th scope="col">Fecha</th>
                        </tr>
                    </thead>
                    <tbody>
                         <tr>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            
                        </tr>
                    </tbody>
                </table>
            </div>
            <ul class="action-columns">
                <li> <input type="button" class="btn-shop-small" value="Agregar" name="ass"></li>
                <li> <input type="button" class="btn-shop-small" value="Deuda Shopper" name="deusa"></li>
                <li> <input type="button" class="btn-shop-small" value="Eliminar item" name="delete"></li>
            </ul>

      <!-- FIN FILA 3 -->
        </form>
        </div>

    </div>
  </body>
</html>