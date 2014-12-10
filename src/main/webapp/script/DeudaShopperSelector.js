App.widget.DeudaShopperSelector = function (container, numeroOrden, callback,
    dialogWidth) {

  var itemDialog = container.dialog({
    autoOpen: false,
    title: 'Busqueda de items adeudados a un shopper',
    width: dialogWidth,
    modal: true,
    buttons: {
      'Agregar': function() {
        createItems();
      },
      'Cancelar': function() {
        itemDialog.dialog("close");
      }
    }
  });

  var shopperSelector = new App.widget.ShopperSelector(container.find(".js-shopper-selector"));

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

  var formVisita;

  var loadingIndicator = new App.widget.LoadingIndicator(container);

  var dialogVisita = container.find(".js-confirmation").dialog({
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
    shopperSelector.render();
    var directives = {
      'tr': {
        'itemOrden<-itemsOrden': {
          '.empresa': function (a) {
            content = a.item.empresa;
            content += ' <a id="add-visita-' + a.pos + '" href="#" class="action js-add-visita">agregar</a>';
            content += '<a id="remove-visita-' + a.pos + '" href="#" class="action js-remove-visita" style="display:none;">quitar</a>';
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
          '.pago': 'itemOrden.descripcion',
          '.importe': function (a) {
            return '$ ' + a.item.importe;
          }
        }
      }
    }
    rowsTemplate = rows.compile(directives);

    formVisita = dialogVisita.find("form").on("submit", function(event) {
      event.preventDefault();
      var importe = formVisita.find(".js-importe").val();
      var index = parseInt(formVisita.find(".js-index").val());
      dialogVisita.callback(index, importe);
    });
  }

  var initEventListeners = function () {
    container.find(".js-buscar").click(function () {
      var currentShopper = shopperSelector.getCurrentShopper();
      var includeGac = container.find("input[name='gac']");
      var includeMcd = container.find("input[name='mcd']");
      var includeAdicionales = container.find("input[name='adicionales']");
      var includeShopmetrics = container.find("input[name='shopmetrics']");
      var applyDate = container.find("input[name='applyDate']");
      var desdeField = container.find("input[name='desde']");
      var hastaField = container.find("input[name='hasta']");

      if (currentShopper) {
        loadingIndicator.start();
        container.spin();

        jQuery.ajax({
          url: "../item/deuda/" + currentShopper.dni,
          data: {
            'includeIplan': false,
            'includeGac': includeGac.is(':checked'),
            'includeMcd': includeMcd.is(':checked'),
            'includeAdicionales': includeAdicionales.is(':checked'),
            'includeShopmetrics': includeShopmetrics.is(':checked'),
            'applyDate': applyDate.is(':checked'),
            'desde': desdeField.val(),
            'hasta': hastaField.val()
          }
        }).done(function (data) {
          visitas = data;
          rows = rows.render({'itemsOrden': data}, rowsTemplate);
          var actions = container.find(".js-add-visita").click(function (event) {
            event.preventDefault();
            var index = actions.index(this);
            addVisita(index);
          })
          var actionsRemove = container.find(".js-remove-visita").click(function (event) {
            event.preventDefault();
            var index = actionsRemove.index(this);
            removeVisita(index);
          })
          var actionsObservation = container.find(".js-view-observation").click(function (event) {
            event.preventDefault();
            var index = actionsObservation.index(this);
            showObservation(index);
          })
          var actionsUser = container.find(".js-view-user").click(function (event) {
            event.preventDefault();
            var index = actionsUser.index(this);
            showUser(index);
          })
          loadingIndicator.stop();
        })
      }
    });

    container.find(".js-date" ).datepicker({
      onSelect: function(dateText, datePicker) {
        $(this).attr('value', dateText);
      }
    });
  };

  var createItems = function() {
    var currentShopper = shopperSelector.getCurrentShopper();
    var itemsCreated = 0;
    var checkItems = function () {
      if (itemsCreated === 0) {
        visitas = [];
        rows = rows.render({'itemsOrden': visitas}, rowsTemplate);
        callback();
        itemDialog.dialog("close");
      } else {
        setTimeout(checkItems, 500);
      }
    };
    if (currentShopper) {
      jQuery.each(visitas, function(index, visita) {
        if (visita.agregar) {
          itemsCreated++;
          var importe = visita.importe.replace(",", ".");
          jQuery.ajax({
            url: "../item/" + createEndpoints[visita.tipoItem],
            type: 'POST',
            data: {
              ordenNro: numeroOrden,
              tipoItem: visita.tipoItem,
              tipoPago: visita.tipoPago,
              asignacion: visita.asignacion,
              shopperDni: currentShopper.dni,
              importe: importe,
              cliente: visita.empresa,
              sucursal: visita.local,
              mes: visita.mes,
              anio: visita.anio,
              fecha: visita.fecha
            }
          }).done(function () {
            itemsCreated = itemsCreated - 1;
          });
        }
      });
      setTimeout(checkItems, 2000);
    }
  };

  var showObservation = function (index) {
    var visita = visitas[index];
    alert(visita.observacion);
  };

  var showUser = function (index) {
    var visita = visitas[index];
    alert(visita.usuario);
  };

  var createVisita = function (index, importe) {
    var visita = visitas[index];
    visita.importe = importe;
    visita.agregar = true;
    jQuery('#add-visita-' + index).hide();
    jQuery('#remove-visita-' + index).show();
    container.find(".js-items tr:eq(" + (index + 1) + ")").addClass("selected");
    dialogVisita.dialog("close");
    refreshSummary();
  };

  var refreshSummary = function () {
    var totalReintegros = 0;
    var totalHonorarios = 0;
    var totalOtrosGastos = 0;
    jQuery.each(visitas, function(index, visita) {
      var importe;
      if (visita.agregar) {
        importe = parseFloat(visita.importe);
        if (visita.tipoPago === 1) {
          totalHonorarios += importe;
        } else if (visita.tipoPago === 2) {
          totalReintegros += importe;
        } else {
          totalOtrosGastos += importe;
        }
      }
    });
    container.find(".js-honorarios").val(totalHonorarios);
    container.find(".js-reintegros").val(totalReintegros);
    container.find(".js-otros-gastos").val(totalOtrosGastos);
  };

  var addVisita = function (index) {
    var visita = visitas[index];
    formVisita.find(".js-index").val(index);
    formVisita.find(".js-importe").val(visita.importe);
    dialogVisita.callback = createVisita;
    dialogVisita.dialog("open");
  };

  var removeVisita = function (index) {
    var visita = visitas[index];
    visita.agregar = false;
    jQuery('#add-visita-' + index).show();
    jQuery('#remove-visita-' + index).hide();
    container.find(".js-items tr:eq(" + (index + 1) + ")").removeClass("selected");
    refreshSummary();
  };

  var reset = function () {
    shopperSelector.reset();
    visitas = [];
    rows = rows.render({'itemsOrden': []}, rowsTemplate);
    container.find(".js-honorarios").val(0);
    container.find(".js-reintegros").val(0);
    container.find(".js-otros-gastos").val(0);
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
