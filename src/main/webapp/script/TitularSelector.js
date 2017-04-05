App.widget.TitularSelector = function (container, searchUrl, callback) {

  var currentTitular = null;

  var selector = container.find(".js-titular-nombre");

  var onSelect = callback || function () {};

  var initEventListeners = function () {
  };

  var reset = function () {
    selector.val('');
    container.find(".js-titular-id").val('');
  };

  var highlight = function (value, term) {
    var matcher = new RegExp("(" + $.ui.autocomplete.escapeRegex(term) + ")", "ig");
    return value.replace(matcher, "<strong>$1</strong>");
  };

  return {
    render: function () {
      var me = this;
      var filter = selector.autocomplete({
        source: searchUrl,
        minLength: 2,
        select: function(event, ui) {
          selector.val(ui.item.name);
          container.find(".js-titular-id").val(ui.item.titularId);
          container.find(".js-titular-tipo").val(ui.item.titularTipo);
          container.find(".js-titular-nombre").val(ui.item.name);
          currentTitular = ui.item;
          onSelect(currentTitular);
          return false;
        }
      });
      filter.data( "ui-autocomplete" )._renderItem = function(ul, item) {
        var display = ((item.name) ? item.name : item.description);
        display = highlight(display, selector.val());
        return $("<li>")
          .append("<a>" + display + "</a>")
          .appendTo(ul);
      };

      initEventListeners();
    },
    getTitularSelected: function() {
      return currentTitular;
    }
  };
}
