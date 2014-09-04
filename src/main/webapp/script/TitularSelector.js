App.widget.TitularSelector = function (container, shoppers) {

  var selector = container.find(".js-titulares");

  var filter;

  var initEventListeners = function () {
    container.find(".js-proveedor").change(function (event) {
      reset();
      filter = selector.autocomplete('option', 'source', "/services/proveedores/suggest");
    });

    container.find(".js-shopper").change(function (event) {
      reset();
      filter = selector.autocomplete('option', 'source', "/services/shoppers/suggest");
    });
  };

  var reset = function () {
    selector.val('');
    container.find(".js-titular-id").val('');
  };

  return {
    render: function () {
      filter = selector.autocomplete({
        source: "/services/shoppers/suggest",
        minLength: 2,
        select: function(event, ui) {
          if (ui.item.name) {
            selector.val(ui.item.name);
          } else {
            selector.val(ui.item.description);
          }
          container.find(".js-titular-id").val(ui.item.id);
          return false;
        }
      });
      filter.data( "ui-autocomplete" )._renderItem = function(ul, item) {
        return $("<li>")
          .append("<a>" + ((item.name) ? item.name : item.description) + "</a>")
          .appendTo(ul);
      };

      initEventListeners();
    },
    getTitularSelected: function() {
      current = null;
      if (container.find(".js-titular-id").val()) {
        current = {
          id: container.find(".js-titular-id").val(),
          tipo: container.find("input:radio[name=tipoTitular]:checked").val(),
          name: selector.val()
        }
      }
      return current;
    }
  };
}
