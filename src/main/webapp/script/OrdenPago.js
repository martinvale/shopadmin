App.widget.OrdenPago = function (container, numeroOrden) {

  var itemSelector;

  var deudaShopperSelector;

  var titularSelector;

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

    deudaShopperSelector = new App.widget.DeudaShopperSelector(jQuery("#deuda-shopper"),
        numeroOrden, refreshOrden);

    container.find(".js-date" ).datepicker({
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

    container.find(".js-delete-item" ).click(function (event) {
      event.preventDefault();
      deleteConfirmDialog.currentId = event.target.id.substr(5);
      deleteConfirmDialog.dialog("open");
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
