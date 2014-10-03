App.widget.ShopperSelector = function (container, skipValidation) {

  var selector = container.find(".js-shopper");

  var clearButton = container.find('.js-clear');

  var currentShopper = null;

  var initValidators = function () {
    if (!skipValidation) {
      var shopperValidation = new LiveValidation("shopper",
          {onlyOnSubmit: true, insertAfterWhatNode: 'clear-shopper'});
      shopperValidation.add(Validate.Presence, {
        failureMessage: "Debe seleccionar un shopper"
      });
    }
  };

  var initEventListeners = function () {
    var filter = selector.autocomplete({
      source: "../services/shoppers/suggest",
      minLength: 2,
      select: function(event, ui) {
        currentShopper = ui.item;
        selector.val(ui.item.name);
        container.find(".js-shopper-id").val(ui.item.id);
        container.find(".js-shopper-dni").val(ui.item.dni);
        return false;
      }
    });
    filter.data( "ui-autocomplete" )._renderItem = function(ul, item) {
      return $("<li>")
        .append("<a>" + item.name + "</a>")
        .appendTo(ul);
    };
    clearButton.click(function (event) {
      event.preventDefault();
      reset();
    });
  };

  var reset = function () {
    currentShopper = null;
    selector.val('');
    container.find(".js-shopper-id").val('');
    container.find(".js-shopper-dni").val('');
  };

  return {
    render: function () {
      initEventListeners();
      initValidators();
    },
    getCurrentShopper: function () {
      return currentShopper;
    },
    reset: function () {
      reset();
    }
  };
};
