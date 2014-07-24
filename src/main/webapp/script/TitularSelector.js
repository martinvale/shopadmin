App.widget.TitularSelector = function (container, shoppers) {

  var selector = container.find(".js-titulares");

  var initEventListeners = function () {
    var filter = selector.autocomplete({
      source: "/services/shoppers/suggest",
      minLength: 2,
      select: function(event, ui) {
        selector.val(ui.item.name);
        container.find(".js-titular-id").val(ui.item.id);
        return false;
      }
    });
    filter.data( "ui-autocomplete" )._renderItem = function(ul, item) {
      return $("<li>")
        .append("<a>" + item.name + "</a>")
        .appendTo(ul);
    };

    jQuery("js-shooper").click(function (event) {
      selector.clear();
    });
  };

  return {
    render: function () {
      initEventListeners();
    }
  };
}
