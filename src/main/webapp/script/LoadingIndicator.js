App.widget.LoadingIndicator = function (container) {

  var overlay = jQuery("<div />", {"class": "loading-overlay"});

  return {
    start: function () {
      jQuery(container).prepend(overlay);
      container.spin();
    },
    stop: function () {
      overlay.remove();
      container.spin(false);
    }
  }
}