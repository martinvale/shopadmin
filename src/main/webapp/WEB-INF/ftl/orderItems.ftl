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
    <#assign order = model["ordenPago"] />
    <#assign canEdit = order.estaAbierta() />
    <#assign canPay = model["user"].hasFeature('pay_order') />
    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.DeudaShopperSelector = function (container, numeroOrden, callback,
    dialogWidth, customUrlService) {

  var urlService = customUrlService || "<@spring.url '/debt/listjson'/>";

  var dialogContainer = container.find(".js-edit-importe-container");

  var itemDialog = container.dialog({
    autoOpen: false,
    title: 'Busqueda de items adeudados a un shopper',
    width: dialogWidth,
    modal: true,
    buttons: {
      'Agregar': function() {
        loadingIndicator.start();
        createItems();
      },
      'Cancelar': function() {
        itemDialog.dialog("close");
      }
    }
  });

  var shopperSelector = new App.widget.ShopperSelector(container.find(".js-shopper-selector"),
      false, "<@spring.url '/services/shoppers/suggest'/>");

  var createEndpoints = {
    1: 'create',
    2: 'create',
    3: 'createAdicional',
    4: 'create',
    5: 'create'
  };

  var rows = $p("#" + container.prop("id") + " .items");

  var rowsTemplate = null;

  var visitas = [];

  var loadingIndicator = new App.widget.LoadingIndicator(container);

  var currentItemFieldSort;

  var fieldSort = {
    'empresa': {
      order: 'desc',
      type: 'string'
    },
    'programa': {
      order: 'desc',
      type: 'string'
    },
    'local': {
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
    },
    'descripcion': {
      order: 'desc',
      type: 'string'
    }
  };

  var initialize = function () {
    shopperSelector.render();
    var directives = {
      'tr': {
        'itemOrden<-itemsOrden': {
          '.@id' : function (a) {
            return 'js-item-' + a.item.id;
          },
          '.@class': function (a) {
            if (a.item.agregar) {
              return 'selected';
            } else {
              return '';
            }
          },
          '.empresa': function (a) {
            content = a.item.empresa;
            content += ' <a href="#" class="action js-add-visita" ';
            if (a.item.agregar) {
              content += 'style="display:none;"';
            }
            content += '>agregar</a>';
            content += '<a href="#" class="action js-remove-visita" ';
            if (!a.item.agregar) {
              content += 'style="display:none;"';
            }
            content += '>quitar</a>';
            if (a.item.observacion) {
              content += ' <a href="javascript:void(0);" class="action js-view-observation">avisos</a>';
            }
            if (a.item.usuario) {
              content += ' <a href="javascript:void(0);" class="action js-view-user">autorizaci&oacute;n</a>';
            }
            return content;
          },
          '.local': 'itemOrden.local',
          '.fecha': 'itemOrden.fecha',
          '.subcuestionario': 'itemOrden.programa',
          '.pago': function (a) {
            return a.item.descripcion.substring(0, 1);
          },
          '.importe': function (a) {
            return '$ ' + a.item.importe.toFixed(2).replace('.', ',');
          }
        }
      }
    }
    rowsTemplate = rows.compile(directives);

  }

  var bindItemsRows = function() {
    jQuery.each(visitas, function(index, visita) {
      tipoPago = visita.descripcion.substring(0, 1);
      var itemContainer = container.find("#js-item-" + visita.id);
      itemContainer.find(".js-add-visita").click(function (event) {
        event.preventDefault();
        addVisita(visita);
      })
      itemContainer.find(".js-remove-visita").click(function (event) {
        event.preventDefault();
        removeVisita(visita);
      })
      itemContainer.find(".js-view-observation").click(function (event) {
        event.preventDefault();
        showObservation(visita);
      })
      itemContainer.find(".js-view-user").click(function (event) {
        event.preventDefault();
        showUser(visita);
      })
      itemContainer.find(".js-edit-importe").click(function (event) {
        event.preventDefault();
        var importe = visita.importe.toFixed(2);
        dialogContainer.find(".js-importe-item").val(importe);
        var dialogEditImporte = dialogContainer.dialog({
          autoOpen: true,
          width: 350,
          modal: true,
          buttons: {
            "Ok": function() {
              var nuevoImporteValue = dialogContainer.find(".js-importe-item").val();
              jQuery.ajax({
                url: "<@spring.url '/debt/updateImporte'/>",
                type: 'POST',
                data: {
                  'id': visita.id,
                  'importe': nuevoImporteValue
                }
              }).done(function () {
                var nuevoImporte = new Number(nuevoImporteValue);
                visita.importe = nuevoImporte;
                itemContainer.find(".importe").text("$ " + nuevoImporte.toFixed(2).replace('.', ','));
                dialogEditImporte.dialog("close");
              });
            },
            Cancel: function() {
              dialogEditImporte.dialog("close");
            }
          }
        });
      })
    });
  };

  var initEventListeners = function () {
    container.find(".js-buscar").click(function () {
      var currentShopper = shopperSelector.getCurrentShopper();
      var desdeField = container.find("input[name='desde']");
      var hastaField = container.find("input[name='hasta']");
      var tipoPagoField = container.find("select[name='tipoPago']");

      if (currentShopper) {
        loadingIndicator.start();
        container.spin();

        jQuery.ajax({
          url: urlService,
          data: {
            'shopperDni': currentShopper.dni,
            'from': desdeField.val(),
            'to': hastaField.val(),
            'tipoPago': tipoPagoField.val()
          }
        }).done(function (data) {
          visitas = data;
          rows = rows.render({'itemsOrden': data}, rowsTemplate);
          bindItemsRows();
          loadingIndicator.stop();
        })
      }
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
      visitas.sort(function (a, b) {
        var cmp = function (x, y){
          return x > y ? 1 : x < y ? -1 : 0;
        };
        if (!currentItemFieldSort) {
          return 0;
        }
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
      })
      rows = rows.render({'itemsOrden': visitas}, rowsTemplate);
      bindItemsRows();
    });

    container.find(".js-date" ).datepicker({
      dateFormat: 'dd/mm/yy',
      onSelect: function(dateText, datePicker) {
        $(this).attr('value', dateText);
      }
    });
  };

  var createItems = function() {
    var currentShopper = shopperSelector.getCurrentShopper();
    if (currentShopper) {
      var debtItems = [];
      var debtIndex = 0;
      jQuery.each(visitas, function(index, visita) {
        if (visita.agregar) {
          debtItems.push(visita.id);
        }
      });
      jQuery.ajax({
        url: "<@spring.url '/debt/assign'/>",
        type: 'POST',
        data: {
          "nroOrden": numeroOrden,
          "items": debtItems
        }
      }).done(function () {
        visitas = [];
        rows = rows.render({'itemsOrden': visitas}, rowsTemplate);
        callback();
        itemDialog.dialog("close");
      });
    }
  };

  var showObservation = function (visita) {
    alert(visita.observacion);
  };

  var showUser = function (visita) {
    alert(visita.usuario);
  };

  var refreshSummary = function () {
    var totalReintegros = 0;
    var totalHonorarios = 0;
    var totalOtrosGastos = 0;
    jQuery.each(visitas, function(index, visita) {
      var importe;
      if (visita.agregar) {
        importe = parseFloat(visita.importe);
        if (visita.tipoPago === 'honorarios') {
          totalHonorarios += importe;
        } else if (visita.tipoPago === 'reintegros') {
          totalReintegros += importe;
        } else {
          totalOtrosGastos += importe;
        }
      }
    });
    container.find(".js-honorarios").val(totalHonorarios.toFixed(2).replace('.', ','));
    container.find(".js-reintegros").val(totalReintegros.toFixed(2).replace('.', ','));
    container.find(".js-otros-gastos").val(totalOtrosGastos.toFixed(2).replace('.', ','));
  };

  var addVisita = function (visita) {
    visita.agregar = true;

    var itemContainer = container.find("#js-item-" + visita.id);
    itemContainer.find(".js-add-visita").hide();
    itemContainer.find(".js-remove-visita").show();
    itemContainer.addClass("selected");

    refreshSummary();
  };

  var removeVisita = function (visita) {
    visita.agregar = false;

    var itemContainer = container.find("#js-item-" + visita.id);
    itemContainer.find(".js-add-visita").show();
    itemContainer.find(".js-remove-visita").hide();
    itemContainer.removeClass("selected");
    refreshSummary();
  };

  var reset = function () {
    shopperSelector.reset();
    visitas = [];
    rows = rows.render({'itemsOrden': []}, rowsTemplate);
    container.find(".js-honorarios").val('0,00');
    container.find(".js-reintegros").val('0,00');
    container.find(".js-otros-gastos").val('0,00');
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

App.widget.OrderItemsEditor = function (container, numeroOrden, items, canEdit) {

  var itemSelector;

  var deudaShopperSelector;

  var titularSelector;

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

  var dialogContainer = container.find(".js-edit-importe");

  var deleteConfirmDialog = jQuery( "#confirm-delete-item" ).dialog({
    resizable: false,
    autoOpen: false,
    modal: true,
    buttons: {
      "Ok": function() {
        var itemId = deleteConfirmDialog.currentId;
        jQuery.ajax({
          url: "" + numeroOrden + "/item/" + itemId,
          method: 'DELETE'
        }).done(function (data) {
          deleteConfirmDialog.dialog("close");
          removeItem(new Number(itemId));
        })
      },
      Cancel: function() {
        $( this ).dialog( "close" );
      }
    }
  });

  var itemsTable = $p("#items-table-template");

  var itemsTableTemplate = null;

  var removeItem = function (itemId) {
    var newItems = [];
    for (var i = 0; i < items.length; i++) {
      if (items[i].id != itemId) {
        newItems.push(items[i]);
      }
    }
    items = newItems;
    container.find('#js-item-' + itemId).fadeOut(300,
      function () {
        jQuery(this).remove();
        refreshSummary();
      });
  }

  var refreshOrden = function () {
    location.href = location.href;
  }

  var initialize = function () {
    var directives = {
      'tr': {
        'itemOrden<-items': {
          '.@id' : function (a) {
            return 'js-item-' + a.item.id;
          },
          '.js-shopper': function (a) {
            var cellContent = a.item.shopper;
            if (canEdit) {
              cellContent += ' <a id="item-' + a.item.id + '" href="#" class="js-delete-item">borrar</a>';
            }
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
      window.open('caratula/' + numeroOrden, "", "width=700, height=600");
    });

    container.find(".js-remito" ).click(function () {
      window.open('remito/' + numeroOrden, "", "width=700, height=600");
    });
    container.find(".js-detail" ).click(function () {
      window.open('printdetail/' + numeroOrden, "", "width=1000, height=600");
    });
    container.find(".js-detail-shopper" ).click(function () {
      window.open('printshopper/' + numeroOrden, "", "width=1000, height=600");
    });

    container.find(".js-buscar-deuda").click(function () {
      deudaShopperSelector.open();
    });

    container.find(".js-anular-order").click(function () {
      jQuery.ajax({
        url: "cancel/${order.numero?c}",
        type: 'POST'
      }).done(function () {
        location.href = "${order.numero?c}";
      });
    });

    container.find(".js-pausar-order").click(function () {
      jQuery.ajax({
        url: "pause/${order.numero?c}",
        type: 'POST'
      }).done(function () {
        location.href = "${order.numero?c}";
      });
    });

    container.find(".js-verified-order").click(function () {
      jQuery.ajax({
        url: "verified/${order.numero?c}",
        type: 'POST'
      }).done(function () {
        location.href = "${order.numero?c}";
      });
    });

    container.find(".js-reopen-order").click(function () {
      jQuery.ajax({
        url: "open/${order.numero?c}",
        type: 'POST'
      }).done(function () {
        location.href = "${order.numero?c}";
      });
    });

    container.find(".js-delete-item").click(function (event) {
      event.preventDefault();
      deleteConfirmDialog.currentId = event.target.id.substr(5);
      deleteConfirmDialog.dialog("open");
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
                  url: "../item/updateImporte",
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
                url: "../item/updateImporte",
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
      deudaShopperSelector = new App.widget.DeudaShopperSelector(jQuery("#deuda-shopper"),
          numeroOrden, refreshOrden, container.width());

      deudaShopperSelector.render();

      initialize();
      initEventListeners();
    }
  };
}

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
        url: "asociarMedioPago",
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

    jQuery(".js-pay-order").click(function () {
      var validations = [];
      validations.push(medioPagoValidation);
      /*var tipoPagoSeleccionado = container.find(".js-medio-pago").val();
      if (tipoPagoSeleccionado == '1' || tipoPagoSeleccionado == '2') {
        validations.push(chequeraValidation);
        validations.push(chequeValidation);
        validations.push(fechaChequeValidation);
      } else {
        validations.push(transferIdValidation);
      }*/
      if (LiveValidation.massValidate(validations)) {
        container.submit();
      }
    });

    container.find(".js-detail" ).click(function () {
      window.open('printdetail/${order.numero?c}', "width=1000, height=600");
    });

    container.find(".js-caratula" ).click(function () {
      window.open('caratula/${order.numero?c}', "", "width=700, height=600");
    });

    container.find(".js-remito" ).click(function () {
      window.open('remito/${order.numero?c}', "", "width=700, height=600");
    });

    container.find(".js-reopen-order").click(function () {
      jQuery.ajax({
        url: "open/${order.numero?c}",
        type: 'POST'
      }).done(function () {
        location.href = "${order.numero?c}";
      });
    });

    container.find(".js-anular-order").click(function () {
      jQuery.ajax({
        url: "cancel/${order.numero?c}",
        type: 'POST'
      }).done(function () {
        location.href = "${order.numero?c}";
      });
    });

    jQuery(".js-close-order").click(function () {
      var stateField = container.find("input[name='state']");
      stateField.val(3);
      var validations = [];
      //validations.push(medioPagoValidation);
      /*var tipoPagoSeleccionado = container.find(".js-medio-pago").val();
      if (tipoPagoSeleccionado == '1' || tipoPagoSeleccionado == '2') {
        validations.push(chequeraValidation);
        validations.push(chequeValidation);
        validations.push(fechaChequeValidation);
      } else {
        validations.push(transferIdValidation);
      }*/
      if (LiveValidation.massValidate(validations)) {
        container.submit();
      }
    });

    container.find(".js-pausar-order").click(function () {
      jQuery.ajax({
        url: "pause/${order.numero?c}",
        type: 'POST'
      }).done(function () {
        location.href = "${order.numero?c}";
      });
    });

  };

  var initValidators = function () {
    medioPagoValidation = new LiveValidation("medioPago");
    medioPagoValidation.add(Validate.Exclusion, {
        within: ["Seleccionar"],
        failureMessage: "El medio de pago es obligatorio"
    });

    /*chequeraValidation = new LiveValidation("numeroChequera");
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
    });*/
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
            ${order.numero?c}, items, ${canEdit?c});
        orderItemsEditor.render();

        var payFormContainer = jQuery(".js-pagar-orden-editor");
        var orderEditor = App.widget.EditorPagarOrden(payFormContainer);
        orderEditor.render();

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
      <h2 class="container-tit">Orden de pago ${order.numero?c} (${order.estado.description}) <#if order.estaAbierta()><a href="edit/${order.numero?c}">editar</a></#if></h2>
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
                <td width="33%"><label>Factura Nro: </label>${order.numeroFactura!''}</td>
                <td width="33%"><label>Localidad: </label>${order.localidad!''}</td>
              </tr>
            <#if order.estaPagada() >
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
                <td>${shopperDescription} <#if canEdit><a id="item-${item.id?c}" href="#" class="js-delete-item">borrar</a></#if></td>
                <td class="js-cliente">${item.cliente!''}</td>
                <td class="js-sucursal">${item.sucursal!''}</td>
                <td>${item.tipoPago.description?substring(0, 1)}</td>
                <td class="js-importe" style="text-align: right;">${item.importe?string.currency} <#if canEdit><a class="js-item-value-${item.id?c}" href="#">editar</a></#if></td>
                <td class="js-fecha">${item.fecha!''}</td>
              </tr>
            </#list>
            </tbody>
          </table>
        </div>
        <ul class="action-columns">
        <#if order.estaAbierta()>
          <li><input type="button" class="btn-shop-small js-buscar-deuda" value="Deuda Shopper" <#if !canEdit>disabled="true"</#if> /></li>
        </#if>

        <#if !order.estaAnulada() && !order.estaSuspendida()>
          <li><input type="button" class="btn-shop-small js-remito" value="Remito" /></li>
          <li><input type="button" class="btn-shop-small js-detail" value="Imprimir Detalle" /></li>
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

      <#if order.estaVerificada() || order.estaCerrada()>
        <h2 class="subtitulo">Forma de pago</h2>

        <form action="pay/${order.numero?c}" method="POST" class="form-shop form-shop-big js-pagar-orden-editor">
          <!-- FILA 1 -->
          <div class="cell box-gray">
            <fieldset>
              <input type="hidden" name="state" value="4" />
              <div class="form-shop-row">
                <label>N&uacute;mero</label>
                <input type="text" name="numeroOrden" readOnly="true" value="${(order.numero?c)!''}"/>
              </div>
              <div class="form-shop-row">
                <label for="medioPago" class="mandatory">M. de pago</label>
                <select id="medioPago" name="medioPagoId" class="js-medio-pago">
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
                <input type="text" name="numeroChequera" id="numeroChequera" value="${(order.numeroChequera)!''}" <#if !canPay>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row js-medio-cheque" <#if ((order.medioPago.id?c)!'') != '1' && ((order.medioPago.id?c)!'') != '2'>style="display:none;"</#if>>
                <label for="numeroCheque">Cheque N&deg;</label>
                <input type="text" name="numeroCheque" id="numeroCheque" value="${(order.numeroCheque)!''}" <#if !canPay>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row js-medio-cheque" <#if ((order.medioPago.id?c)!'') != '1' && ((order.medioPago.id?c)!'') != '2'>style="display:none;"</#if>>
                <label for="fechaCheque">Fecha cheque</label>
                <input type="text" id="fechaCheque" name="fechaCheque" class="js-date" value="${(order.fechaCheque?string('dd/MM/yyyy'))!''}" <#if !canPay>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row js-medio-transfer" <#if ((order.medioPago.id?c)!'') != '3'>style="display:none;"</#if>>
                <label for="transferId">ID Transfer</label>
                <input type="text" name="transferId" id="transferId" value="${(order.idTransferencia)!''}" <#if !canPay>disabled="true"</#if>/>
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
        </form>
      </#if>

        <div class="actions-form">
          <ul class="action-columns">
          <#if order.estaAbierta() >
            <li><input type="button" class="btn-shop js-anular-order" value="Anular" /></li>
            <li><input type="button" class="btn-shop js-pausar-order" value="En espera" /></li>
            <li><input type="button" class="btn-shop js-verified-order" value="Verificada" /></li>
          </#if>
          <#if order.estaVerificada() >
            <li><input type="button" class="btn-shop js-reopen-order" value="Reabrir" /></li>
            <li><input type="button" class="btn-shop js-pay-order" value="Pagar" <#if !canPay>disabled="true"</#if>></li>
            <li><input type="button" class="btn-shop js-close-order" value="Cerrar" /></li>
          </#if>
          <#if order.estaCerrada() >
            <li><input type="button" class="btn-shop js-pay-order" value="Pagar" <#if !canPay>disabled="true"</#if>></li>
          </#if>
          <#if order.estaSuspendida() >
            <li><input type="button" class="btn-shop js-reopen-order" value="Reabrir" /></li>
          </#if>
          <#if order.estaPagada() && model["user"].hasFeature("reopen_order")>
            <li><input type="button" class="btn-shop js-reopen-order" value="Reabrir" /></li>
          </#if>
          </ul>
        </div>
        <div style="display:none">
          <div class="js-edit-importe" title="Modificar el importe">
            <label for="importe-item">Ingrese el importe de este item de la factura</label>
            <input id="importe-item" type="text" value="" class="js-importe-item" />
          </div>
        </div>
      </form>
    </div>
    <div id="deuda-shopper" style="display:none;">
      <div class="container-box-plantilla">
        <form>
          <div class="cell">
            <div class="box-green">
              <div class="form-shop-row-left shopper-widget js-shopper-selector">
                <label for="shopper">Shooper: </label>
                <input type="text" value="" id="shopper" class="shopper-name js-shopper" />
                <a id="clear-shopper" href="#" class="clear js-clear">limpiar</a>
                <input type="hidden" name="shopperId" value="" class="js-shopper-id" />
                <input type="hidden" name="shopperDni" value="" class="js-shopper-dni" />
              </div>
              <div>
                <label for="tipoPago">Tipo de pago:</label>
                <select id="tipoPago" name="tipoPago">
                  <option value="">Todos</option>
                <#list model["tiposPago"] as tipoPago>
                  <option value="${tipoPago}">${tipoPago.description}</option>
                </#list>
                </select>
              </div>
              <ul class="date">
                <li>
                  <label for="desde">Desde</label>
                  <input type="text" name="desde" id="desde" class="js-date" value="${model['fechaDesde']?string('dd/MM/yyyy')}">
                </li>
                <li>
                  <label for="hasta">Hasta</label>
                  <#assign currentDate = .now />
                  <input type="text" name="hasta" id="hasta" class="js-date" value="${currentDate?string('dd/MM/yyyy')}">
                </li>
              </ul>
            </div>
            <ul class="action-columns">
              <li><input type="button" class="btn-shop-small js-buscar" value="Buscar"></li>
            </ul>
            <div class="items-container">
              <table summary="Lista de items" class="table-form js-items">
                <thead>
                  <tr>
                    <th scope="col" style="width:25%"><a id="order-empresa" href="#" class="js-order">Empresa <i class="fa fa-angle-down"></i></a></th>
                    <th scope="col" style="width:25%"><a id="order-programa" href="#" class="js-order">Subcuestionario <i class="fa fa-angle-down"></i></a></th>
                    <th scope="col" style="width:24%"><a id="order-local" href="#" class="js-order">Local <i class="fa fa-angle-down"></i></a></th>
                    <th scope="col" style="width:10%"><a id="order-importe" href="#" class="js-order">Importe <i class="fa fa-angle-down"></i></a></th>
                    <th scope="col" style="width:8%"><a id="order-fecha" href="#" class="js-order">Fecha <i class="fa fa-angle-down"></i></a></th>
                    <th scope="col" style="width:8%"><a id="order-descripcion" href="#" class="js-order">Pago <i class="fa fa-angle-down"></i></a></th>
                  </tr>
                </thead>
                <tbody class="items">
                  <tr>
                    <td class="empresa"></td>
                    <td class="subcuestionario"></td>
                    <td class="local"></td>
                    <td><span class="importe"></span> <a class="js-edit-importe" href="#">editar</a></td>
                    <td class="fecha"></td>
                    <td class="pago"></td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="summary">
              <label>Reintegros</label>
              <input type="text" class="js-reintegros" />
              <label>Honorarios</label>
              <input type="text" class="js-honorarios" />
              <label>Otros gastos</label>
              <input type="text" class="js-otros-gastos" />
            </div>
          </div>
        </form>
      </div>
      <div style="display:none">
        <div class="js-edit-importe-container" title="Modificar el importe">
          <label for="importe-item">Ingrese el importe de este item de la factura</label>
          <input id="importe-item" type="text" value="" class="js-importe-item" />
        </div>
      </div>
    </div>
    <div id="confirm-delete-item" title="Borrar item de la orden de pago">
      <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Esta seguro que desea borrar el item?</p>
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