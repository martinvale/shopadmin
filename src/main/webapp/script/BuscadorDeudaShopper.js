App.widget.BuscadorDeudaShopper = function (container) {

  var shopperSelector = new App.widget.ShopperSelector(container.find(".js-shopper-selector"));

  var rows = $p("table .items");

  var rowsTemplate = null;

  var visitas = [];

  var loadingIndicator = new App.widget.LoadingIndicator(container);

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
          '.pago': 'itemOrden.descripcion',
          '.importe': function (a) {
            return '$ ' + a.item.importe.toFixed(2).replace('.', ',');
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
        var url = "printDebt?dniShopper=" + currentShopper.dni + '&includeMcd='
            + includeMcd.is(':checked') + '&includeGac=' + includeGac.is(':checked') + '&includeAdicionales='
            + includeAdicionales.is(':checked') + '&includeShopmetrics=' + includeShopmetrics.is(':checked')
            + '&applyDate=' + applyDate.is(':checked') + '&desde=' + desdeField.val()
            + '&hasta=' + hastaField.val();
        window.open(url, "", "width=1000, height=600");
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
          refreshSummary();
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
