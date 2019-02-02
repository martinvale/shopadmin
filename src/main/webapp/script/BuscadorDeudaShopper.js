App.widget.BuscadorDeudaShopper = function (container) {

  var shopperSelector = new App.widget.ShopperSelector(container.find(".js-shopper-selector"));

  var rows = $p("table .items");

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
          '.empresa': function (a) {
            content = a.item.empresa;
            /*content += ' <a id="add-visita-' + a.pos + '" href="#" class="action js-add-visita">agregar</a>';
            content += '<a id="remove-visita-' + a.pos + '" href="#" class="action js-remove-visita" style="display:none;">quitar</a>';
            if (a.item.observacion) {
              content += ' <a href="javascript:void(0);" class="action js-view-observation">avisos</a>';
            }
            if (a.item.usuario) {
              content += ' <a href="javascript:void(0);" class="action js-view-user">autorizaci&oacute;n</a>';
            }*/
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
        },
        sort: function(a, b){ // same kind of sort as the usual Array sort
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
        }
      }
    }
    rowsTemplate = rows.compile(directives);
  }

  var initEventListeners = function () {
    container.find(".js-print").click(function () {
      var currentShopper = shopperSelector.getCurrentShopper();
      var includeGac = container.find("input[name='gac']");
      var includeMcd = container.find("input[name='mcd']");
      var includeAdicionales = container.find("input[name='adicionales']");
      var includeShopmetrics = container.find("input[name='shopmetrics']");
      var applyDate = container.find("input[name='applyDate']");
      var desdeField = container.find("input[name='desde']");
      var hastaField = container.find("input[name='hasta']");

      if (currentShopper) {
        var url = "printDebt?dniShopper=" + currentShopper.identityId + '&includeMcd='
            + includeMcd.is(':checked') + '&includeGac=' + includeGac.is(':checked') + '&includeAdicionales='
            + includeAdicionales.is(':checked') + '&includeShopmetrics=' + includeShopmetrics.is(':checked')
            + '&applyDate=' + applyDate.is(':checked') + '&desde=' + desdeField.val()
            + '&hasta=' + hastaField.val();
        window.open(url, "", "width=1000, height=600");
      }
    });

    container.find(".js-download").click(function () {
      var currentShopper = shopperSelector.getCurrentShopper();
      var includeGac = container.find("input[name='gac']");
      var includeMcd = container.find("input[name='mcd']");
      var includeAdicionales = container.find("input[name='adicionales']");
      var includeShopmetrics = container.find("input[name='shopmetrics']");
      var applyDate = container.find("input[name='applyDate']");
      var desdeField = container.find("input[name='desde']");
      var hastaField = container.find("input[name='hasta']");

      if (currentShopper) {
        location.href = "exportDeuda?dniShopper=" + currentShopper.identityId + '&includeMcd='
            + includeMcd.is(':checked') + '&includeGac=' + includeGac.is(':checked') + '&includeAdicionales='
            + includeAdicionales.is(':checked') + '&includeShopmetrics=' + includeShopmetrics.is(':checked')
            + '&applyDate=' + applyDate.is(':checked') + '&desde=' + desdeField.val()
            + '&hasta=' + hastaField.val();
      }
    });

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
          url: "../item/deuda/" + currentShopper.identityId,
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
          refreshSummary();
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
      //container.find(".js-table-items tbody").html(itemsTableTemplate({'items': items}));
      rows = rows.render({'itemsOrden': visitas}, rowsTemplate);
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
    });

    container.find(".js-date" ).datepicker({
      dateFormat: 'dd/mm/yy',
      onSelect: function(dateText, datePicker) {
        $(this).attr('value', dateText);
      }
    });
  };

  var showObservation = function (index) {
    var visita = visitas[index];
    alert(visita.observacion);
  };

  var showUser = function (index) {
    var visita = visitas[index];
    alert(visita.usuario);
  };

  var refreshSummary = function () {
    var totalReintegros = 0;
    var totalHonorarios = 0;
    var totalOtrosGastos = 0;
    jQuery.each(visitas, function(index, visita) {
      var importe = parseFloat(visita.importe);
      if (visita.tipoPago === 1) {
        totalHonorarios += importe;
      } else if (visita.tipoPago === 2) {
        totalReintegros += importe;
      } else {
        totalOtrosGastos += importe;
      }
    });
    container.find(".js-honorarios").val('$ ' + totalHonorarios.toFixed(2).replace('.', ','));
    container.find(".js-reintegros").val('$ ' + totalReintegros.toFixed(2).replace('.', ','));
    container.find(".js-otros-gastos").val('$ ' + totalOtrosGastos.toFixed(2).replace('.', ','));
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
    }
  };
}
