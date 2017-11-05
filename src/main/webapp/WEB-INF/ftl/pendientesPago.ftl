<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />

    <link rel="stylesheet" href="../css/jquery-ui/jquery-ui.css">

    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/shop.css">
    <link rel="stylesheet" href="../css/custom.css">

    <link rel="stylesheet" href="../font-awesome/css/font-awesome.min.css">

    <script src="../script/jquery.js"></script>
    <script src="../script/jquery-ui.js"></script>

    <script src="../script/spin.js"></script>
    <script src="../script/jquery.spin.js"></script>

    <script src="../script/pure.min.js"></script>
    <script src="../script/livevalidation.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.PayDialog = function (container) {

  var itemDialog;

  var loadingIndicator = new App.widget.LoadingIndicator(container);

  var closeCallback = {};

  var saveCallback = {};

  var initialize = function () {
    itemDialog = container.dialog({
      autoOpen: false,
      title: 'Confirmar el pago de esta orden',
      width: 500,
      modal: true,
      buttons: {
        'Guardar': function() {
          loadingIndicator.start();
          var order = {};
          var numero = itemDialog.find("input[name=numero]").val();
          var numeros = numero.split(",");
          order['numero[]'] = [];
          jQuery.each(numeros, function (index, value) {
            order['numero[]'].push(value.trim());
          })
          order['transferId'] = itemDialog.find("input[name=transferid]").val();
          order['observaciones'] = itemDialog.find("textarea[name=observaciones]").val();
          order['observacionesShopper'] = itemDialog.find("textarea[name=obsshopper]").val();

          jQuery.ajax({
            url: "savepaydata",
            type: 'POST',
            data: order
          }).done(function () {
            loadingIndicator.stop();
            saveCallback(order);
            itemDialog.dialog("close");
          });
        },
        'Pagar': function() {
          loadingIndicator.start();
          var order = {};
          var numero = itemDialog.find("input[name=numero]").val();
          var numeros = numero.split(",");
          order['numero[]'] = [];
          jQuery.each(numeros, function (index, value) {
            order['numero[]'].push(value.trim());
          })
          order['transferId'] = itemDialog.find("input[name=transferid]").val();
          order['observaciones'] = itemDialog.find("textarea[name=observaciones]").val();
          order['observacionesShopper'] = itemDialog.find("textarea[name=obsshopper]").val();
          order['sendMail'] = itemDialog.find("input[name=sendmail]").prop("checked");
          order['receivers'] = itemDialog.find("input[name=receivers]").val();

          jQuery.ajax({
            url: "silentpay",
            type: 'POST',
            data: order
          }).done(function () {
            loadingIndicator.stop();
            closeCallback(order['numero[]']);
            itemDialog.dialog("close");
          });
        },
        'Cancelar': function() {
          itemDialog.dialog("close");
        }
      }
    });
  };

  return {
    render: function () {
      initialize();
    },
    onClose: function (callback) {
      closeCallback = callback;
    },
    onSave: function (callback) {
      saveCallback = callback;
    },
    open: function (orders) {
      var order = orders[0];
      var numerosOrden;
      var importe = 0;
      var importeConIva = 0;
      jQuery.each(orders, function (index, value) {
        if (numerosOrden) {
          numerosOrden += ", " + value.numero;
        } else {
          numerosOrden = value.numero;
        }
        importe += parseFloat(value.importe.replace(",", "."));
        importeConIva += parseFloat(value.importeConIva.replace(",", "."));
      });
      itemDialog.find("input[name=numero]").val(numerosOrden);
      itemDialog.find("input[name=titular]").val(order.titular);
      itemDialog.find("input[name=titularCuenta]").val(order.titularCuenta);
      itemDialog.find("input[name=cuit]").val(order.cuit);
      itemDialog.find("input[name=banco]").val(order.banco);
      itemDialog.find("input[name=cbu]").val(order.cbu);
      itemDialog.find("input[name=importe]").val(importe.toFixed(2).replace(".", ","));
      itemDialog.find("input[name=importeConIva]").val(importeConIva.toFixed(2).replace(".", ","));
      itemDialog.find(".js-iva").text(order.iva);
      itemDialog.find("input[name=transferid]").val(order.transferId);
      itemDialog.find("textarea[name=observaciones]").val(order.observaciones);
      itemDialog.find("textarea[name=obsshopper]").val(order.observacionesShopper);
      itemDialog.find("input[name=sendmail]").prop("checked", "checked");
      itemDialog.find("input[name=receivers]").val("");
      itemDialog.dialog("open");
    }
  };

}

App.widget.PayAdmin = function (container, itemDialog, orders) {

  var itemDialog;

  var initEventListeners = function () {
    for (var i = 0; i < orders.length; i++) {
      var orderContainer = container.find(".js-order-" + orders[i].numero);
      orderContainer.find(".js-pay").click(function (event) {
        event.preventDefault();
        var index = this.id.substring(9);
        var order = orders[index];
        itemDialog.open([order]);
      });
    }
    container.find(".js-pay-selected").click(function (event) {
        var selectedOrders = [];
        container.find(":checked").each(function (index) {
          var index = this.id.substring(18);
          selectedOrders.push(orders[index]);
        });
        itemDialog.open(selectedOrders);
    });
    container.find(":checkbox").change(function (event) {
      var checkedOrders = container.find(":checked");
      var currentCheckbox = this;
      if (this.checked) {
        container.find(".js-pay-selected").prop("disabled", false);
        var titularCuenta;
        jQuery.each(checkedOrders, function (index, value) {
          if (titularCuenta) {
            var index = value.id.substring(18);
            var order = orders[index];
            if (titularCuenta != orders[index].titularCuenta) {
              alert('No se pueden seleccionar cuentas de diferentes titulares');
              event.preventDefault();
              currentCheckbox.checked = false;
            }
          } else {
            titularCuenta = orders[index].titularCuenta;
          }
        })
      } else {
        container.find(".js-pay-selected").prop("disabled", checkedOrders.length == 0);
      }
    });
    itemDialog.onClose(function (orders) {
      jQuery.each(orders, function (index, value) {
        container.find(".js-order-" + value).hide("slow");
      })
    });
    itemDialog.onSave(function (paymentData) {
      jQuery.each(paymentData['numero[]'], function (index, numeroOrden) {
        jQuery.each(orders, function (index, order) {
          if (order.numero == numeroOrden) {
            order.transferId = paymentData.transferId;
            order.observaciones = paymentData.observaciones;
            order.observacionesShopper = paymentData.observacionesShopper;
          }
        })
      })
    });
  };

  return {
    render: function () {
      initEventListeners();
    }
  };

}

      jQuery(document).ready(function() {
        var orders = [];
        <#list model["result"].items as item>
        orders.push({
          numero: ${item.numero?c},
          titular: "${item.titular!''}",
          titularCuenta: "${item.titularCuenta!''}",
          cuit: "${item.cuit!''}",
          banco: "${item.banco!''}",
          cbu: "${item.cbu!''}",
          importe: "${item.importe}",
          iva: "${item.iva}",
          importeConIva: "${item.importeConIva}",
          transferId: "${item.transferId!''}",
          observaciones: "${item.observaciones}",
          observacionesShopper: "${item.observacionesShopper}"
        });
        </#list>

        var dialogTemplate = jQuery("#pay-detail");
        var payDialog = App.widget.PayDialog(dialogTemplate);
        payDialog.render();

        var ordenesContainer = jQuery(".js-ordenes");
        var payAdmin = App.widget.PayAdmin(ordenesContainer, payDialog, orders);
        payAdmin.render();

        jQuery(".js-fecha" ).datepicker({
          dateFormat: 'dd/mm/yy',
          onSelect: function(dateText, datePicker) {
            $(this).attr('value', dateText);
          }
        });

      });

    </script>

    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">B&uacute;squeda de ordenes a pagar</h2>
      <form action="pending" class="form-shop form-shop-big js-search-form" method="GET">
        <div class="cell box-green buscador">
          <div class="field">
            <label for="fechaDesde">Desde: </label>
            <input type="text" id="fechaDesde" name="fechaDesde" value="${(model['fechaDesde'])!''}" class="js-fecha" />
          </div>
          <div class="field">
            <label for="fechaHasta">Hasta: </label>
            <input type="text" id="fechaHasta" name="fechaHasta" value="${(model['fechaHasta'])!''}" class="js-fecha" />
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Buscar" class="btn-shop-small"></li>
        </ul>
      </form>
      <div class="js-ordenes">
        <div class="box-green">
          <input type="button" value="Pagar las seleccionadas" class="btn-shop-small js-pay-selected" disabled="true" />
        </div>
        <table summary="Listado de ordenes a pagar" class="table-form">
          <thead>
            <tr>
              <th scope="col" style="width: 2%">&nbsp;</th>
              <th scope="col" style="width: 10%">Nro de orden</th>
              <th scope="col" style="width: 73%">Titular</th>
              <th scope="col" style="width: 10%">Importe c/IVA</th>
              <th scope="col" style="width: 5%">&nbsp;</th>
            </tr>
          </thead>
          <tbody>
            <#assign totalPago = 0 />
            <#list model["result"].items as item>
            <tr class="js-order-${item.numero?c}">
              <td><input type="checkbox" id="js-selected-order-${item_index}" /></td>
              <td>${item.numero?c}</td>
              <td>${item.titular!''}</td>
              <td style="text-align: right">$ ${item.importeConIva}</td>
              <td><a id="js-order-${item_index}" href="#" class="js-pay">pagar</a></td>
              <#assign totalPago = totalPago + item.importeConIva?replace(",", ".")?number />
            </tr>
            </#list>
          </tbody>
        </table>
      </div>
      <p>&nbsp;</p>
      <p style="text-align: right">Total a pagar: $ ${totalPago?c?replace(".", ",")}</p>
    </div>

    <div id="pay-detail" title="Confirmar el pago de la orden">
      <div class="field">
        <label for="numero">Numero de orden: </label>
        <input type="text" id="numero" name="numero" readOnly="true" value="" />
      </div>
      <div class="field">
        <label for="titular">Titular: </label>
        <input type="text" id="titular" name="titular" readOnly="true" value="" />
      </div>
      <div class="field">
        <label for="titularCuenta">Titular Cuenta: </label>
        <input type="text" id="titularCuenta" name="titularCuenta" readOnly="true" value="" />
      </div>
      <div class="field">
        <label for="cuit">CUIT: </label>
        <input type="text" id="cuit" name="cuit" readOnly="true" value="" />
      </div>
      <div class="field">
        <label for="banco">Banco: </label>
        <input type="text" id="banco" name="banco" readOnly="true" value="" />
      </div>
      <div class="field">
        <label for="cbu">CBU/Alias: </label>
        <input type="text" id="cbu" name="cbu" readOnly="true" value="" />
      </div>
      <div class="field">
        <label for="importe">Importe: </label>
        <input type="text" id="importe" name="importe" readOnly="true" value="" />
      </div>
      <div class="field">
        <label for="importeConIva">Total c/IVA (<span class="js-iva"></span>%): </label>
        <input type="text" id="importeConIva" name="importeConIva" readOnly="true" value="" />
      </div>
      <div class="field">
        <label for="transferid">ID Transfer: </label>
        <input type="text" id="transferid" name="transferid" value="" />
      </div>
      <div class="field">
        <label for="observaciones" style="margin-top: 8px;">Observaciones: </label>
        <textarea id="observaciones" name="observaciones" style="width:300px; vertical-align:top;" value=""></textarea>
      </div>
      <div class="field">
        <label for="obsshopper" style="margin-top: 8px;">Obs. p/shopper: </label>
        <textarea id="obsshopper" name="obsshopper" style="width:300px; vertical-align:top;" value=""></textarea>
      </div>
      <div class="field">
        <label for="sendmail">Enviar email: </label>
        <input type="checkbox" id="sendmail" name="sendmail" checked="checked" />
      </div>
      <div class="field">
        <label for="receivers">Otros e-mails: </label>
        <input type="text" id="receivers" name="receivers" value="" />
        <p class="note">Ej: juan@gmail.com;maria@gmail.com</p>
      </div>
    </div>

  </body>
</html>