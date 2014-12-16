App.widget.OrdenPago = function (container, numeroOrden, canEdit, items) {

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

  var deleteConfirmDialog = jQuery( "#confirm-delete-item" ).dialog({
    resizable: false,
    autoOpen: false,
    height:140,
    modal: true,
    buttons: {
      "Ok": function() {
        var itemId = deleteConfirmDialog.currentId;
        jQuery.ajax({
          url: numeroOrden + "/item/" + itemId,
          method: 'DELETE'
        }).done(function (data) {
          location.href = location.href;
        })
      },
      Cancel: function() {
        $( this ).dialog( "close" );
      }
    }
  });

  var itemsTable = $p("#items-table-template");

  var itemsTableTemplate = null;

  var refreshOrden = function () {
    location.href = location.href;
  }

  var initialize = function () {
    var directives = {
      'tr': {
        'itemOrden<-items': {
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
            return '$' + a.item.importe.toFixed(2).replace('.', ',');
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

    itemSelector = new App.widget.ItemsSelector(jQuery("#item-selector"),
        numeroOrden, refreshOrden, container.width());

    deudaShopperSelector = new App.widget.DeudaShopperSelector(jQuery("#deuda-shopper"),
        numeroOrden, refreshOrden, container.width());

    container.find(".js-date" ).datepicker({
      minDate: new Date(),
      dateFormat: 'dd/mm/yy',
      onSelect: function(dateText, datePicker) {
        $(this).attr('value', dateText);
      }
    });
    itemSelector.render();
    deudaShopperSelector.render();
  }

  var initEventListeners = function () {
    var medioDefault = container.find(".js-medio-pago-predeterminado");
    var sinMedioSeleccionado = container.find(".js-sin-medio-pago");
    var asociarMedio = container.find(".js-asociar-medio");

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

    container.find(".js-add-item").click(function () {
      itemSelector.open();
    });

    container.find(".js-buscar-deuda").click(function () {
      deudaShopperSelector.open();
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
