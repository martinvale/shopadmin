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
    <script src="/script/pure.min.js"></script>
    <script src="/script/livevalidation.js"></script>

    <#assign user = model["user"] />
    <#assign canEdit = false />
    <#list user.authorities as role>
      <#assign canEdit = (role.authority == 'ADMIN') />
    </#list>
    <#assign ordenAbierta = true />
    <#assign estadoOrden = "ABIERTA" />

    <#assign numero = "" />
    <#assign tipoFactura = "" />
    <#assign fechaPago = "" />
    <#assign medioPagoId = "" />
    <#assign state = "" />
    <#assign iva = "" />
    <#assign tipoTitular = 1 />
    <#assign titularId = "" />
    <#assign facturaNumero = "" />
    <#assign fechaCheque = "" />
    <#assign localidad = "" />
    <#assign numeroChequera = "" />
    <#assign numeroCheque = "" />
    <#assign transferId = "" />
    <#assign observaciones = "" />
    <#assign observacionesShopper = "" />
    <#assign totalHonorarios = "" />
    <#assign totalReintegros = "" />
    <#assign totalOtrosGastos = "" />
    <#assign ivaHonorarios = "" />
    <#assign totalHonorariosConIva = "" />
    <#assign total = "" />
    <#if model["ordenPago"]??>
      <#assign numero = "${model['ordenPago'].numero?c}" />
      <#assign tipoFactura = "${model['ordenPago'].tipoFactura}" />
      <#assign fechaPago = "${model['ordenPago'].fechaPago?string('dd/MM/yyyy')}" />
      <#if model['ordenPago'].medioPago??>
        <#assign medioPagoId = "${model['ordenPago'].medioPago.id?c}" />
      </#if>
      <#assign state = "${model['ordenPago'].estado.id?c}" />
      <#assign ordenAbierta = model['ordenPago'].estado.description == 'Abierta' />
      <#assign estadoOrden = "${model['ordenPago'].estado.description}" />
      <#assign iva = "${model['ordenPago'].iva?c}" />
      <#assign tipoTitular = model['ordenPago'].tipoProveedor />
      <#assign titularId = "${model['ordenPago'].proveedor}" />
      <#assign facturaNumero = "${model['ordenPago'].numeroFactura!''}" />
      <#if model['ordenPago'].fechaCheque??>
        <#assign fechaCheque = "${model['ordenPago'].fechaCheque?string('dd/MM/yyyy')}" />
      </#if>
      <#assign localidad = "${model['ordenPago'].localidad}" />
      <#assign numeroChequera = "${model['ordenPago'].numeroChequera!''}" />
      <#assign numeroCheque = "${model['ordenPago'].numeroCheque!''}" />
      <#assign transferId = "${model['ordenPago'].idTransferencia!''}" />
      <#assign observaciones = "${model['ordenPago'].observaciones!''}" />
      <#assign observacionesShopper = "${model['ordenPago'].observacionesShopper!''}" />
      <#assign totalHonorarios = model['ordenPago'].honorarios />
      <#assign totalReintegros = model['ordenPago'].reintegros />
      <#assign totalOtrosGastos = model['ordenPago'].otrosGastos />
      <#assign ivaHonorarios = (totalHonorarios * (model['ordenPago'].iva / 100)) />
      <#assign totalHonorariosConIva = (totalHonorarios + ivaHonorarios) />
      <#assign total = (totalReintegros + totalOtrosGastos + totalHonorariosConIva) />
    </#if>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.ShopperSelector = function (container) {

  var selector = container.find(".js-shopper");

  var currentShopper = null;

  var initEventListeners = function () {
    var filter = selector.autocomplete({
      source: "/services/shoppers/suggest",
      minLength: 2,
      select: function(event, ui) {
        currentShopper = ui.item;
        selector.val(ui.item.name);
        container.find(".js-shopper-id").val(ui.item.id);
        container.find(".js-shopper-dni").val(ui.item.dni);
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
    },
    getCurrentShopper: function () {
      return currentShopper;
    },
    reset: function () {
      currentShopper = null;
      selector.val('');
      container.find(".js-shopper-id").val('');
      container.find(".js-shopper-dni").val('');
    }
  };
};

App.widget.ItemsSelector = function (container, numeroOrden, callback) {

  var itemDialog = container.dialog({
    autoOpen: false,
    width: 900,
    close: callback
  });

  var shopperSelector = new App.widget.ShopperSelector(container.find(".js-shopper-selector"));

  var rows = $p("#" + container.prop("id") + " .js-mcd-items .items");

  var rowsTemplate = null;

  var visitas = [];

  var rowsAdicionales = $p("#" + container.prop("id") + " .js-items-adicionales .items");

  var rowsAdicionalesTemplate = null;

  var adicionales = [];

  var formVisita;

  var dialogVisita = $("#ammount-confirmation").dialog({
    autoOpen: false,
    width: 350,
    modal: true,
    callback: function () {},
    buttons: {
      "Ok": function() {
        var importe = formVisita.find(".js-importe").val();
        var index = parseInt(formVisita.find(".js-index").val());
        dialogVisita.callback(index, importe);
      },
      Cancel: function() {
        dialogVisita.dialog("close");
      }
    },
    close: function() {
      formVisita.find("js-importe").val();
      formVisita.find("js-index").val();
      //allFields.removeClass( "ui-state-error" );
    }
  });

  var initialize = function () {
    itemDialog.find(".js-tabs").tabs();
    shopperSelector.render();
    var directives = {
      'tr': {
        'itemOrden<-itemsOrden': {
          '.programa': function (a) {
            return a.item.programa + ' <a href="#" class="action js-add-visita">agregar</a>';
          },
          '.local': 'itemOrden.local',
          '.mes': 'itemOrden.mes',
          '.anio': 'itemOrden.anio',
          '.fecha': 'itemOrden.fecha',
          '.descripcion': 'itemOrden.descripcion',
          '.importe': 'itemOrden.importe',
          '.fechaCobro': 'itemOrden.fechaCobro',
          '.asignacion': 'itemOrden.asignacion'
        }
      }
    }
    rowsTemplate = rows.compile(directives);

    directives = {
      'tr': {
        'itemOrden<-itemsOrden': {
          '.pago': 'itemOrden.pago',
          '.cliente': function (a) {
            return a.item.cliente + ' <a href="#" class="action js-add-visita">agregar</a>';
          },
          '.sucursal': 'itemOrden.sucursal',
          '.mes': 'itemOrden.mes',
          '.anio': 'itemOrden.anio',
          '.fecha': 'itemOrden.fecha',
          '.observaciones': 'itemOrden.observacion',
          '.importe': 'itemOrden.importe'
        }
      }
    }
    rowsAdicionalesTemplate = rowsAdicionales.compile(directives);

    formVisita = dialogVisita.find("form").on("submit", function(event) {
      event.preventDefault();
      var importe = formVisita.find(".js-importe").val();
      var index = parseInt(formVisita.find(".js-index").val());
      dialogVisita.callback(index, importe);
    });
  }

  var initEventListeners = function () {
    container.find(".js-mcd-items .js-buscar" ).click(function () {
      var currentShopper = shopperSelector.getCurrentShopper();
      jQuery.ajax({
        url: "/item/mdc/" + currentShopper.dni
      }).done(function (data) {
        visitas = data;
        rows = rows.render({'itemsOrden': data}, rowsTemplate);
        var actions = container.find(".js-mcd-items .js-add-visita").click(function (event) {
          event.preventDefault();
          var index = actions.index(this);
          addVisita(index);
        })
      })
    });

    container.find(".js-items-adicionales .js-buscar" ).click(function () {
      var currentShopper = shopperSelector.getCurrentShopper();
      jQuery.ajax({
        url: "/item/adicionales/" + currentShopper.dni
      }).done(function (data) {
        adicionales = data;
        rowsAdicionales = rowsAdicionales.render({'itemsOrden': data}, rowsAdicionalesTemplate);
        var actions = container.find(".js-items-adicionales .js-add-visita").click(function (event) {
          event.preventDefault();
          var index = actions.index(this);
          addAdicional(index);
        })
      })
    });
  };

  var createVisita = function (index, importe) {
    var visita = visitas[index];
    visita.importe = importe;
    var currentShopper = shopperSelector.getCurrentShopper();
    jQuery.ajax({
      url: "/item/create",
      type: 'POST',
      data: {
        ordenNro: numeroOrden,
        tipoPago: visita.tipoPago,
        asignacion: visita.asignacion,
        shopperDni: currentShopper.dni,
        importe: visita.importe,
        cliente: visita.programa,
        sucursal: visita.local,
        mes: visita.mes,
        anio: visita.anio,
        fecha: visita.fecha
      }
    }).done(function (data) {
      dialogVisita.dialog("close");
      visitas.splice(index, 1);
      rows = rows.render({'itemsOrden': visitas}, rowsTemplate);
    });
  };

  var createAdicional = function (index, importe) {
    var adicional = adicionales[index];
    adicional.importe = importe;
    var currentShopper = shopperSelector.getCurrentShopper();
    jQuery.ajax({
      url: "/item/createAdicional",
      type: 'POST',
      data: {
        ordenNro: numeroOrden,
        tipoPago: adicional.tipoPago,
        asignacion: adicional.id,
        shopperDni: currentShopper.dni,
        importe: adicional.importe,
        cliente: adicional.cliente,
        sucursal: adicional.sucursal,
        descripcion: adicional.observacion,
        mes: adicional.mes,
        anio: adicional.anio,
        fecha: adicional.fecha
      }
    }).done(function (data) {
      dialogVisita.dialog("close");
      adicionales.splice(index, 1);
      rowsAdicionales = rowsAdicionales.render({'itemsOrden': adicionales}, rowsAdicionalesTemplate);
    });
  };

  var addVisita = function (index) {
    var visita = visitas[index];
    formVisita.find(".js-index").val(index);
    formVisita.find(".js-importe").val(visita.importe);
    dialogVisita.callback = createVisita;
    dialogVisita.dialog("open");
  };

  var addAdicional = function (index) {
    var adicional = adicionales[index];
    formVisita.find(".js-index").val(index);
    formVisita.find(".js-importe").val(adicional.importe);
    dialogVisita.callback = createAdicional;
    dialogVisita.dialog("open");
  };

  var reset = function () {
    shopperSelector.reset();
    visitas = [];
    rows = rows.render({'itemsOrden': []}, rowsTemplate);
  };

  return {
    render: function () {
      initialize();
      initEventListeners();
    },
    open: function () {
      reset();
      itemDialog.dialog("open");
    }
  };
}

App.widget.OrdenPago = function (container, numeroOrden) {

  var itemSelector;

  var titularSelector;

  /*var itemsTable = $p("#items-table-template");

  var itemsTableTemplate = null;*/

  var refreshOrden = function () {
    location.href = location.href;
  }

  var initialize = function () {
    /*var directives = {
      'tr': {
        'itemOrden<-itemsOrden': {
          '.programa': function (a) {
            return a.item.programa + ' <a href="#" class="action js-add-visita">agregar</a>';
          },
          '.local': 'itemOrden.local',
          '.mes': 'itemOrden.mes',
          '.anio': 'itemOrden.anio',
          '.fecha': 'itemOrden.fecha',
          '.descripcion': 'itemOrden.descripcion',
          '.importe': 'itemOrden.importe',
          '.fechaCobro': 'itemOrden.fechaCobro',
          '.asignacion': 'itemOrden.asignacion'
        }
      }
    }
    itemsTableTemplate = itemsTable.compile(directives);*/

    itemSelector = new App.widget.ItemsSelector(jQuery("#item-selector"),
        numeroOrden, refreshOrden);

    container.find(".js-date" ).datepicker({
      onSelect: function(dateText, datePicker) {
        $(this).attr('value', dateText);
      }
    });
    itemSelector.render();
  }

  var initEventListeners = function () {
    var medioDefault = container.find(".js-medio-pago-predeterminado");
    var sinMedioSeleccionado = container.find(".js-sin-medio-pago");
    var asociarMedio = container.find(".js-asociar-medio");

    container.find(".js-add-item" ).click(function () {
      itemSelector.open();
    });

    container.find(".js-medio-pago").change(function (event) {
      medioDefault.hide();
      sinMedioSeleccionado.hide();
      asociarMedio.show();
    });

    asociarMedio.click(function (event) {
      event.preventDefault();

      var medioPagoSeleccionado = container.find(".js-medio-pago").val();
      var titular = titularSelector.getTitularSelected();

      jQuery.ajax({
        url: "asociarMedioPago",
        data: {
          tipoProveedor: titular.tipo,
          titularId: titular.id,
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

    container.find(".js-delete-item" ).click(function (event) {
      event.preventDefault();
      $( "#dialog-confirm" ).dialog({
        resizable: false,
        height:140,
        modal: true,
        buttons: {
          "Delete all items": function() {
            $( this ).dialog( "close" );
          },
          Cancel: function() {
            $( this ).dialog( "close" );
          }
        }
      });
      var itemId = event.target.id.substr(5);
      jQuery.ajax({
        url: numeroOrden + "/item/" + itemId,
        method: 'DELETE'
      }).done(function (data) {
        location.href = location.href;
      })
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
      titularSelector = new App.widget.TitularSelector(
          container.find(".js-titular-selector"));
      titularSelector.render();

      initialize();
      initEventListeners();
      initValidators();
    }
  };
}


      jQuery(document).ready(function() {

        var ordenPago = new App.widget.OrdenPago(jQuery(".js-orden-pago"), ${numero});
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
    <header>
      <div class="header-box">
        <h1>Shopnchek<span class="tag-intranet">intranet</span></h1>
        <p class="user"> ${user.username} <a href="/logout">Salir</a></p>
      </div>
    </header>
    <div class="container-box-plantilla">
        <h2 class="container-tit">Orden de pago</h2>
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
                    <input type="radio" name="tipoTitular" id="shopper" value="1" class="js-shopper" <#if tipoTitular == 1>checked="checked"</#if>>
                    <label for="shopper">Shopper</label>
                  </li>
                  <li class="form-shop">
                    <input type="radio" name="tipoTitular" id="proveedor" value="2" class="js-proveedor" <#if tipoTitular == 2>checked="checked"</#if>>
                    <label for="proveedor">Proveedor</label>
                  </li>
                </ul>
                <div class="combo-titular">
                  <#assign titularNombre = "${model['titularNombre']!''}" />
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
                <input type="text" id="fechaPago" name="fechaPago" class="js-date" value="${fechaPago}" <#if !canEdit>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row">
                <label for="medioPago" class="mandatory">M. de pago</label>
                <select id="medioPago" name="medioPagoId" class="js-medio-pago">
                  <option value="Seleccionar">Seleccionar</option>
                  <#list model["mediosPago"] as medioPago>
                    <option value="${medioPago.id}" <#if medioPago.id?c == medioPagoId>selected="selected"</#if>>${medioPago.description}</option>
                  </#list>
                </select>
                <div class="js-medio-pago-asociado">
                  <#if model["medioPagoPredeterminado"]??>
                    <span class="medio-pago js-medio-pago-predeterminado">Medio de pago predeterminado: ${model["medioPagoPredeterminado"]}</span>
                    <a href="#" class="asociar-medio js-asociar-medio" style="display:none">Asociar medio de pago al titular</a>
                  <#else>
                    <span class="medio-pago js-medio-pago-predeterminado" style="display:none">Medio de pago predeterminado: </span>
                    <#if medioPagoId != "">
                      <a href="#" class="asociar-medio js-asociar-medio">Asociar medio de pago al titular</a>
                    <#else>
                      <span class="sin-medio-pago js-sin-medio-pago">(El titular no tiene un medio de pago asociado)</span>
                      <a href="#" class="asociar-medio js-asociar-medio" style="display:none">Asociar medio de pago al titular</a>
                    </#if>
                  </#if>
                </div>
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="numeroFactura">Factura N&deg;</label>
                <input type="number" name="numeroFactura" id="numeroFactura" value="${facturaNumero}"/>
              </div>
              <div class="form-shop-row">
                <label for="state" class="mandatory">Estado</label>
                <select id="state" name="estadoId" <#if !canEdit>disabled="true"</#if>>
                  <#list model["orderStates"] as orderState>
                    <option value="${orderState.id}" <#if orderState.id?c == state>selected="selected"</#if>>${orderState.description}</option>
                  </#list>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="numeroChequera">Chequera N&deg;</label>
                <input type="number" name="numeroChequera" id="numeroChequera" value="${numeroChequera}" <#if !canEdit || estadoOrden == 'Abierta' || estadoOrden == 'En Espera'>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row">
                <label for="transferId">ID Transfer</label>
                <input type="number" name="transferId" id="transferId" value="${transferId}" <#if !canEdit || estadoOrden == 'Abierta' || estadoOrden == 'En Espera'>disabled="true"</#if>/>
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
                <input type="number" name="numeroCheque" id="numeroCheque" value="${numeroCheque}" <#if !canEdit || estadoOrden == 'Abierta' || estadoOrden == 'En Espera'>disabled="true"</#if>/>
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="fechaCheque">Fecha cheque</label>
                <input type="text" id="fechaCheque" name="fechaCheque" class="js-date" value="${fechaCheque}" <#if !canEdit || estadoOrden == 'Abierta' || estadoOrden == 'En Espera'>disabled="true"</#if>/>
              </div>
            </li>
          </ul>
          <p class="observacion">
            <label for="observaciones">Observaciones</label>
            <textarea id="observaciones" name="observaciones">${observaciones}</textarea>
          </p>
          <p class="observacion">
            <label for="observacionesShopper">Obs. p/shopper</label>
            <textarea id="observacionesShopper" name="observacionesShopper">${observacionesShopper}</textarea>
          </p>
          <ul class="action-columns">
            <li> <input type="submit" class="btn-shop-small" value="Guardar" <#if !canEdit>disabled="true"</#if>></li>
            <li> <input type="button" class="btn-shop-small" value="Car&aacute;tula" <#if !model["ordenPago"]?? || !canEdit>disabled="true"</#if>></li>
          </ul>
          <!-- FIN FILA 2 -->

         <!-- FILA 3 -->
         <h2 class="subtitulo">Items</h2>
         <div class="scroll-table">
             <table summary="Listado de items de la orden de pago" class="table-form ">
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
                        <th scope="col">Tipo de Item</th>
                    </tr>
                </thead>
                <tbody>
                  <#if model["ordenPago"]??>
                    <#list model["ordenPago"].items as item>
                    <tr>
                        <#if item.shopper??>
                          <#assign shopperDescription = "${item.shopper.name} (${item.shopper.username})">
                        </#if>
                        <td>${shopperDescription!'No encontrado'} <#if canEdit && ordenAbierta><a id="item-${item.id?c}" href="#" class="js-delete-item">borrar</a></#if></td>
                        <td class="js-cliente">${item.cliente!''}</td>
                        <td class="js-mes">${item.mes!''}</td>
                        <td class="js-anio">${(item.anio?c)!''}</td>
                        <td class="js-sucursal">${item.sucursal!''}</td>
                        <td>${item.tipoPago.description}</td>
                        <td class="js-importe">${item.importe}</td>
                        <td class="js-dni">${item.shopperDni}</td>
                        <td class="js-asignacion">${(item.asignacion?c)!''}</td>
                        <td class="js-fecha">${item.fecha!''}</td>
                        <td class="js-tipo-item">${item.tipoItem?c}</td>
                    </tr>
                    </#list>
                  </#if>
                </tbody>
            </table>
        </div>
        <ul class="action-columns">
            <li><input type="button" class="btn-shop-small js-add-item" value="Agregar" <#if !canEdit || !ordenAbierta>disabled="true"</#if>></li>
            <li><input type="submit" class="btn-shop-small" value="Deuda Shopper" disabled="true"></li>
        </ul>

  <!-- FIN FILA 3 -->
  <!-- FILA 4 -->
  <h2 class="subtitulo"> Totales </h2>
         <ul class="columnas-form col-three">
            <li>
                <div class="form-shop-row">
                    <label for="subt0">Subt. honorarios</label>
                    <input type="text" id="sbt0" value="${totalHonorarios?string.currency}">
                </div>
                <div class="form-shop-row">
                    <label for="sbt1">Subt. reintegros</label>
                    <input type="text" id="sbt1" value="${totalReintegros?string.currency}">
                </div>
            </li>
            <li>
                <div class="form-shop-row">
                    <label for="iva-honorarios">IVA $</label>
                     <input type="text" id="iva-honorarios" value="${ivaHonorarios?string.currency}">
                </div>
                <div class="form-shop-row">
                   <label for="sbt3">Subt. otros gastos</label>
                    <input type="text" id="sbt3" value="${totalOtrosGastos?string.currency}">
                </div>
            </li>
            <li>
                  <div class="form-shop-row">
                   <label for="honorarios">Honorarios c/IVA</label>
                    <input type="text" id="honorarios" value="${totalHonorariosConIva?string.currency}">
                </div>
                
                <div class="form-shop-row">
                    <label for="total">Total general</label>
                    <input type="text" id="total" value="${total?string.currency}">
                </div>
            </li>
         </ul>

        <div class="actions-form">
          <ul class="action-columns">
            <li><input type="submit" class="btn-shop" value="Imprimir" disabled="true"></li>
            <li><input type="submit" class="btn-shop" value="Imprimir Detalle" disabled="true"></li>
            <li><input type="submit" class="btn-shop-action" value="Eliminar Shopper" disabled="true"></li>
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
                        <input type="hidden" name="shopperDni" value="" class="js-shopper-dni" />
                      </div>
                    </div>
                    <div class="tabs_holder js-tabs">
                        <ul>
                            <!--li><a href="#your-tab-id-1">I Plan </a></li-->
                            <li><a href="#your-tab-id-2">MCD</a></li>
                            <!--li><a href="#your-tab-id-3">GAC</a></li-->
                            <li><a href="#your-tab-id-4">Adicionales</a></li>
                            <!--li><a href="#your-tab-id-5">Manuales</a></li-->
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
                                            <th scope="col">Programa</th>
                                            <th scope="col">Local</th>
                                            <th scope="col">A&ntilde;o</th>
                                            <th scope="col">Mes</th>
                                            <th scope="col">Fecha</th>
                                            <th scope="col">Pago</th>
                                            <th scope="col">Importe</th>
                                            <th scope="col">Fecha Cobro</th>
                                            <th scope="col">Asignaci&oacute;n</th>
                                        </tr>
                                    </thead>
                                    <tbody class="items">
                                       <tr>
                                          <td class="programa"></td>
                                          <td class="local"></td>
                                          <td class="anio"></td>
                                          <td class="mes"></td>
                                          <td class="fecha"></td>
                                          <td class="descripcion"></td>
                                          <td class="importe"></td>
                                          <td class="fechaCobro"></td>
                                          <td class="asignacion"></td>
                                      </tr>
                                    </tbody>
                                </table>
                            </div>
                          </div>
                          <!--div id="your-tab-id-3">
                           Some content tab 3
                          </div-->
                          <div id="your-tab-id-4" class="js-items-adicionales">
                            <ul class="action-columns">
                                <li><input type="button" class="btn-shop-small js-buscar" value="Buscar"></li>
                            </ul>
                            <div class="scroll-table">
                               <table summary="Lista de items" class="table-form ">
                                  <thead>
                                      <tr>
                                          <th scope="col">Pago</th>
                                          <th scope="col">Cliente</th>
                                          <th scope="col">Sucursal</th>
                                          <th scope="col">Mes</th>
                                          <th scope="col">A&ntilde;o</th>
                                          <th scope="col">Importe</th>
                                          <th scope="col">Fecha</th>
                                          <th scope="col">Observaciones</th>
                                      </tr>
                                  </thead>
                                  <tbody class="items">
                                     <tr>
                                        <td class="pago"></td>
                                        <td class="cliente"></td>
                                        <td class="sucursal"></td>
                                        <td class="mes"></td>
                                        <td class="anio"></td>
                                        <td class="importe"></td>
                                        <td class="fecha"></td>
                                        <td class="observaciones"></td>
                                    </tr>
                                  </tbody>
                              </table>
                            </div>
                          </div>
                          <!--div id="your-tab-id-5">
                           Some content tab 5
                          </div-->
                        </div>
                    </div>

      <!-- FIN FILA 3 -->
        </form>
        </div>

    </div>
    <div id="ammount-confirmation" title="Importe">
      <form>
        <label for="importe-asignacion">Ingrese el importe de la asignaci&oacute;n seleccionada</label>
        <input id="importe-asignacion" type="text" value="" class="js-importe" />
        <input type="hidden" value="" class="js-index" />
        <input type="submit" tabindex="-1" style="position:absolute; top:-1000px" />
      </form>
    </div>
    <div id="confirm-delete-item" title="Borrar el item?">
      <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Esta seguro que desea borrar el item?</p>
    </div>
    <!--div id="items-table-template" style="display:none">
         <table summary="Listado de items de la orden de pago" class="table-form ">
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
                <td class="js-cliente"></td>
                <td class="js-mes"></td>
                <td class="js-anio"></td>
                <td class="js-sucursal"></td>
                <td></td>
                <td class="js-importe"></td>
                <td class="js-dni"></td>
                <td class="js-asignacion"></td>
                <td class="js-fecha"></td>
              </tr>
            </tbody>
        </table>
    </div-->
  </body>
</html>