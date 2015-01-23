App.widget.DeudaShopperSelector = function (container, numeroOrden, callback,
    dialogWidth) {

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

  var dialogVisita = container.find(".js-confirmation").dialog({
    autoOpen: false,
    width: 350,
    modal: true,
    callback: function () {},
    buttons: {
      "Ok": function() {
        var importe = formVisita.find(".js-importe").val();
        dialogVisita.callback(dialogVisita.visita, importe);
      },
      Cancel: function() {
        dialogVisita.dialog("close");
      }
    },
    close: function() {
      formVisita.find("js-importe").val();
      //allFields.removeClass( "ui-state-error" );
    }
  });

  var initialize = function () {
    shopperSelector.render();
    var directives = {
      'tr': {
        'itemOrden<-itemsOrden': {
          '.@id' : function (a) {
            var tipoPago = a.item.descripcion.substring(0, 1);
            return 'js-item-' + tipoPago + '-' + a.item.asignacion;
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

    formVisita = dialogVisita.find("form").on("submit", function(event) {
      event.preventDefault();
      var importe = formVisita.find(".js-importe").val();
      var index = parseInt(formVisita.find(".js-index").val());
      dialogVisita.callback(index, importe);
    });
  }

  var bindItemsRows = function() {
    jQuery.each(visitas, function(index, visita) {
      tipoPago = visita.descripcion.substring(0, 1);
      var itemContainer = container.find("#js-item-" + tipoPago + '-' + visita.asignacion);
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
    });
  };

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
          jQuery.ajax({
            url: "../item/" + createEndpoints[visita.tipoItem],
            type: 'POST',
            data: {
              ordenNro: numeroOrden,
              tipoItem: visita.tipoItem,
              tipoPago: visita.tipoPago,
              asignacion: visita.asignacion,
              shopperDni: currentShopper.dni,
              importe: visita.importe,
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

  var showObservation = function (visita) {
    alert(visita.observacion);
  };

  var showUser = function (visita) {
    alert(visita.usuario);
  };

  var createVisita = function (visita, importe) {
    tipoPago = visita.descripcion.substring(0, 1);
    var itemContainer = container.find("#js-item-" + tipoPago + '-' + visita.asignacion);

    visita.importe = new Number(importe.replace(',', '.'));
    visita.agregar = true;

    itemContainer.find(".js-add-visita").hide();
    itemContainer.find(".js-remove-visita").show();
    itemContainer.addClass("selected");
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
    container.find(".js-honorarios").val(totalHonorarios.toFixed(2).replace('.', ','));
    container.find(".js-reintegros").val(totalReintegros.toFixed(2).replace('.', ','));
    container.find(".js-otros-gastos").val(totalOtrosGastos.toFixed(2).replace('.', ','));
  };

  var addVisita = function (visita) {
    formVisita.find(".js-importe").val(visita.importe.toFixed(2).replace('.', ','));
    dialogVisita.visita = visita;
    dialogVisita.callback = createVisita;
    dialogVisita.dialog("open");
  };

  var removeVisita = function (visita) {
    tipoPago = visita.descripcion.substring(0, 1);
    var itemContainer = container.find("#js-item-" + tipoPago + '-' + visita.asignacion);

    visita.agregar = false;
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
