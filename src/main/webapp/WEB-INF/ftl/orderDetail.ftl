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

    <#setting locale="es_AR">
    <#assign canEdit = true />
    <#assign order = model["ordenPago"] />
    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.OrderItemsEditor = function (container, numeroOrden, items) {

  var itemSelector;

  var currentItemFieldSort;

  var fieldSort = {
    'shopper': {
      order: 'desc',
      type: 'string'
    },
    'cliente': {
      order: 'desc',
      type: 'string'
    },
    'sucursal': {
      order: 'desc',
      type: 'string'
    },
    'tipoPago': {
      order: 'desc',
      type: 'string'
    },
    'importe': {
      order: 'desc',
      type: 'number'
    },
    'fecha': {
      order: 'desc',
      type: 'date'
    }
  };

  var itemsTable = $p("#items-table-template");

  var itemsTableTemplate = null;

  var initialize = function () {
    var directives = {
      'tr': {
        'itemOrden<-items': {
          '.@id' : function (a) {
            return 'js-item-' + a.item.id;
          },
          '.js-shopper': function (a) {
            var cellContent = a.item.shopper;
            return cellContent;
          },
          '.js-cliente': 'itemOrden.cliente',
          '.js-sucursal': 'itemOrden.sucursal',
          '.js-tipo-pago': 'itemOrden.tipoPago',
          '.js-importe': function (a) {
            return '$' + a.item.importe.toFixed(2).replace('.', ',')
                + ' <a class="js-item-value-' + a.item.id + '" href="#">editar</a>';
          },
          '.js-fecha': 'itemOrden.fecha'
        },
        sort: function(a, b){ // same kind of sort as the usual Array sort
          var cmp = function (x, y){
            return x > y ? 1 : x < y ? -1 : 0;
          };
          var first = a[currentItemFieldSort];
          var second = b[currentItemFieldSort];
          if (fieldSort[currentItemFieldSort].type === 'string') {
            first = first.toLowerCase();
            second = second.toLowerCase();
          } else if (fieldSort[currentItemFieldSort].type === 'date') {
            first = new Date(new Date(first.substring(6, 10),
              new Number(first.substring(3, 5)) - 1, first.substring(0, 2)));
            second = new Date(new Date(second.substring(6, 10),
              new Number(second.substring(3, 5)) - 1, second.substring(0, 2)));
          }
          if (fieldSort[currentItemFieldSort].order === 'asc') {
            return cmp(first, second);
          } else {
            return cmp(second, first);
          }
        }
      }
    }
    itemsTableTemplate = itemsTable.compile(directives);
  }

  var initEventListeners = function () {
    container.find(".js-caratula" ).click(function () {
      window.open('../caratula/' + numeroOrden, "", "width=700, height=600");
    });

    container.find(".js-remito" ).click(function () {
      window.open('../remito/' + numeroOrden, "", "width=700, height=600");
    });
    container.find(".js-detail" ).click(function () {
      window.open('../printdetail/' + numeroOrden, "", "width=1000, height=600");
    });
    container.find(".js-detail-shopper").click(function () {
      window.open('../printshopper/' + numeroOrden, "", "width=1000, height=600");
    });
    container.find(".js-reopen-order").click(function () {
      jQuery.ajax({
        url: "../open/${order.numero?c}",
        type: 'POST'
      }).done(function () {
        location.href = "../${order.numero?c}";
      });
    });

    container.find(".js-order").click(function (event) {
      event.preventDefault();
      var header = jQuery(event.target);
      var arrow = header.find(".fa");
      var field = event.currentTarget.id.substring(6);
      container.find(".js-order").removeClass("selected");
      header.addClass("selected");
      currentItemFieldSort = field;
      if (fieldSort[currentItemFieldSort].order === 'asc') {
        fieldSort[currentItemFieldSort].order = 'desc';
        arrow.removeClass("fa-angle-up");
        arrow.addClass("fa-angle-down");
      } else {
        fieldSort[currentItemFieldSort].order = 'asc'
        arrow.removeClass("fa-angle-down");
        arrow.addClass("fa-angle-up");
      }
      container.find(".js-table-items tbody").html(itemsTableTemplate({'items': items}));
      container.find(".js-delete-item").click(function (event) {
        event.preventDefault();
        deleteConfirmDialog.currentId = event.target.id.substr(5);
        deleteConfirmDialog.dialog("open");
      });

      jQuery.each(items, function(index, item) {
        container.find('.js-item-value-' + item.id).click(function (event) {
          event.preventDefault();
          var importe = item.importe.toFixed(2).replace('.', ',');
          dialogContainer.find(".js-importe-item").val(importe)
          var dialogEditImporte = dialogContainer.dialog({
            autoOpen: true,
            width: 350,
            modal: true,
            buttons: {
              "Ok": function() {
                var nuevoImporte = dialogContainer.find(".js-importe-item").val();
                jQuery.ajax({
                  url: "../../item/updateImporte",
                  type: 'POST',
                  data: {
                    'itemId': item.id,
                    'importe': nuevoImporte.replace(',', '.')
                  }
                }).done(function () {
                  refreshOrden();
                });
              },
              Cancel: function() {
                dialogEditImporte.dialog("close");
              }
            }
          });
        });
      });

    });

    jQuery.each(items, function(index, item) {
      container.find('.js-item-value-' + item.id).click(function (event) {
        event.preventDefault();
        var importe = item.importe.toFixed(2).replace('.', ',');
        dialogContainer.find(".js-importe-item").val(importe)
        var dialogEditImporte = dialogContainer.dialog({
          autoOpen: true,
          width: 350,
          modal: true,
          buttons: {
            "Ok": function() {
              var nuevoImporte = dialogContainer.find(".js-importe-item").val();
              jQuery.ajax({
                url: "../../item/updateImporte",
                type: 'POST',
                data: {
                  'itemId': item.id,
                  'importe': nuevoImporte.replace(',', '.')
                }
              }).done(function () {
                refreshOrden();
              });
            },
            Cancel: function() {
              dialogEditImporte.dialog("close");
            }
          }
        });
      });
    });

  };

  var refreshSummary = function () {
    var totalReintegros = 0;
    var totalHonorarios = 0;
    var totalOtrosGastos = 0;
    jQuery.each(items, function(index, item) {
      var importe = parseFloat(item.importe);
      if (item.tipoPago === "H") {
        totalHonorarios += importe;
      } else if (item.tipoPago === "R") {
        totalReintegros += importe;
      } else {
        totalOtrosGastos += importe;
      }
    });
    var ivaValue = container.find("input[name='iva']").val();
    var iva = new Number(ivaValue.replace(',', '.'));
    var ivaHonorarios = (totalHonorarios / 100) * iva;
    var totalHonorariosConIva = totalHonorarios + ivaHonorarios;
    var total = totalHonorariosConIva + totalReintegros + totalOtrosGastos;

    container.find(".js-total-honorarios")
        .val(totalHonorarios.toFixed(2).replace('.', ','));
    container.find(".js-total-iva")
        .val(ivaHonorarios.toFixed(2).replace('.', ','));
    container.find(".js-total-honorarios-con-iva")
        .val(totalHonorariosConIva.toFixed(2).replace('.', ','));
    container.find(".js-total-reintegros")
        .val(totalReintegros.toFixed(2).replace('.', ','));
    container.find(".js-total-otros-gastos")
        .val(totalOtrosGastos.toFixed(2).replace('.', ','));
    container.find(".js-total")
        .val(total.toFixed(2).replace('.', ','));
  };

  return {
    render: function () {
      initialize();
      initEventListeners();
    }
  };
}



      jQuery(document).ready(function() {

        var items = [
          <#list order.items as item>
          {
            id: ${item.id?c},
            <#if item.shopper??>
              <#assign shopperDescription = "${item.shopper.name}">
            </#if>
            shopper: "${(shopperDescription?json_string)!'No encontrado'}",
            cliente: "${(item.cliente?json_string)!''}",
            sucursal: "${(item.sucursal?json_string)!''}",
            tipoPago: "${item.tipoPago.description?substring(0, 1)}",
            importe: ${item.importe?c},
            fecha: "${item.fecha!''}"
          }<#if item_has_next>,</#if>
          </#list>
        ];

        var orderItemsEditor = new App.widget.OrderItemsEditor(jQuery(".js-orden-pago"),
            ${order.numero?c}, items);
        orderItemsEditor.render();
      });
    </script>

    <script src="<@spring.url '/script/ShopperSelector.js'/>"></script>
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
</style>
  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla js-orden-pago">
      <h2 class="container-tit">Orden de pago ${order.numero?c} (${order.estado.description})</h2>
      <!-- FILA 1 -->
      <div class="cell">
        <div class="box-green">
          <div class="form-shop-row-left">
            <table width="100%">
              <tr>
                <td width="33%"><label>Titular: </label>${model["titularNombre"]!''}</td>
                <td width="33%"><label>Tipo de factura: </label>${order.tipoFactura!''}</td>
                <td width="33%"><label>Fecha de pago: </label>${order.fechaPago?string('dd/MM/yyyy')}</td>
              </tr>
              <tr>
                <td width="33%"><label>IVA: </label>${order.iva?string['0.##']}%</td>
                <td width="33%"><label>Localidad: </label>${order.localidad!''}</td>
                <td width="33%"><label>Forma de pago: </label>${(order.medioPago.description)!''}</td>
              </tr>
              <#if order.medioPago.id = 3>
              <tr>
                <td width="100%" colspan="2"><label>ID Transfer: </label>${order.idTransferencia!''}</td>
              </tr>
              <#else>
              <tr>
                <td width="33%"><label>Chequera Nro: </label>${order.numeroChequera!''}</td>
                <td width="33%"><label>Cheque Nro: </label>${order.numeroCheque!''}</td>
                <td width="33%"><label>Fecha cheque: </label>${(order.fechaCheque?string('dd/MM/yyyy'))!''}</td>
              </tr>
              </#if>
              <tr>
                <td width="100%" colspan="2"><label>Observaciones: </label>${order.observaciones!''}</td>
              </tr>
              <tr>
                <td width="100%" colspan="2"><label>Observaciones p/shopper: </label>${order.observacionesShopper!''}</td>
              </tr>
            </table>
          </div>
        </div>
      </div>
      <!-- FIN FILA 1 -->

      <!-- FILA 3 -->
      <h2 class="subtitulo">Items</h2>
        <div class="items-container">
          <table summary="Listado de items de la orden de pago" class="table-form js-table-items">
            <thead>
              <tr>
                <th scope="col" style="width:25%"><a id="order-shopper" href="#" class="js-order">Shopper <i class="fa fa-angle-down"></i></a></th>
                <th scope="col" style="width:25%"><a id="order-cliente" href="#" class="js-order">Cliente <i class="fa fa-angle-down"></i></a></th>
                <th scope="col" style="width:26%"><a id="order-sucursal" href="#" class="js-order">Sucursal <i class="fa fa-angle-down"></i></a></th>
                <th scope="col" style="width:4%"><a id="order-tipoPago" href="#" class="js-order">Pago <i class="fa fa-angle-down"></i></a></th>
                <th scope="col" style="width:12%"><a id="order-importe" href="#" class="js-order">Importe <i class="fa fa-angle-down"></i></a></th>
                <th scope="col" style="width:8%"><a id="order-fecha" href="#" class="js-order">Fecha <i class="fa fa-angle-down"></i></a></th>
              </tr>
            </thead>
            <tbody>
            <#list order.items as item>
              <tr id="js-item-${item.id?c}">
                <#assign shopperDescription = "" />
                <#if item.shopper??>
                  <#assign shopperDescription = "${item.shopper.name}">
                </#if>
                <#if shopperDescription?length &gt; 25>
                  <#assign shopperDescription = "${shopperDescription?substring(0, 25)}...">
                </#if>
                <td>${shopperDescription}</td>
                <td class="js-cliente">${item.cliente!''}</td>
                <td class="js-sucursal">${item.sucursal!''}</td>
                <td>${item.tipoPago.description?substring(0, 1)}</td>
                <td class="js-importe" style="text-align: right;">${item.importe?string.currency}</td>
                <td class="js-fecha">${item.fecha!''}</td>
              </tr>
            </#list>
            </tbody>
          </table>
        </div>
        <ul class="action-columns">
        <#if order.estado.id != 6 && order.estado.id != 5>
          <li><input type="button" class="btn-shop-small js-remito" value="Imprimir" <#if !canEdit>disabled="true"</#if> /></li>
          <li><input type="button" class="btn-shop-small js-detail" value="Imprimir Detalle" <#if !canEdit>disabled="true"</#if> /></li>
          <li><input type="button" class="btn-shop-small js-detail-shopper" value="Detalle Shopper" /></li>
          <li><input type="button" class="btn-shop-small js-caratula" value="Car&aacute;tula" /></li>
        </#if>
        </ul>
        <!-- FIN FILA 3 -->

        <!-- FILA 4 -->
        <h2 class="subtitulo">Totales</h2>
        <#assign totalHonorarios = order.honorarios />
        <#assign totalReintegros = order.reintegros />
        <#assign totalOtrosGastos = order.otrosGastos />
        <#assign ivaHonorarios = (totalHonorarios * (order.iva / 100)) />
        <#assign totalHonorariosConIva = (totalHonorarios + ivaHonorarios) />
        <#assign total = (totalReintegros + totalOtrosGastos + totalHonorariosConIva) />
        <ul class="columnas-form col-three">
          <li>
            <div class="form-shop-row">
              <label for="total-honorarios">Subt. honorarios ($)</label>
              <input type="text" id="total-honorarios"
                  class="js-total-honorarios"
                  value="${totalHonorarios?string.currency}">
            </div>
            <div class="form-shop-row">
              <label for="total-reintegros">Subt. reintegros ($)</label>
              <input type="text" id="total-reintegros"
                  class="js-total-reintegros"
                  value="${totalReintegros?string.currency}">
            </div>
          </li>
          <li>
            <div class="form-shop-row">
              <label for="total-iva">IVA ($)</label>
               <input type="text" id="total-iva" class="js-total-iva"
                  value="${ivaHonorarios?string.currency}">
            </div>
            <div class="form-shop-row">
              <label for="total-otros-gastos">Subt. otros gastos ($)</label>
              <input type="text" id="total-otros-gastos"
                  class="js-total-otros-gastos"
                  value="${totalOtrosGastos?string.currency}">
            </div>
          </li>
          <li>
            <div class="form-shop-row">
              <label for="total-honorarios-con-iva">Honorarios c/IVA ($)</label>
              <input type="text" id="total-honorarios-con-iva" 
                  class="js-total-honorarios-con-iva"
                  value="${totalHonorariosConIva?string.currency}">
            </div>
            <div class="form-shop-row">
              <label for="total">Total general ($)</label>
              <input type="text" id="total" class="js-total"
                  value="${total?string.currency}">
            </div>
          </li>
        </ul>

        <div class="actions-form">
          <ul class="action-columns">
          <#if order.estado.id == 5>
            <li><input type="button" class="btn-shop js-reopen-order" value="Reabrir" /></li>
          </#if>
          <#if order.estado.id == 4 && model["user"].hasFeature("reopen_order")>
            <li><input type="button" class="btn-shop js-reopen-order" value="Reabrir" /></li>
          </#if>
          </ul>
        </div>
      </form>
    </div>
    <table id="items-table-template" style="display:none">
      <tr>
        <td class="js-shopper"></td>
        <td class="js-cliente"></td>
        <td class="js-sucursal"></td>
        <td class="js-tipo-pago"></td>
        <td class="js-importe" style="text-align: right;"></td>
        <td class="js-fecha"></td>
      </tr>
    </table>
  </body>
</html>