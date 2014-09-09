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
