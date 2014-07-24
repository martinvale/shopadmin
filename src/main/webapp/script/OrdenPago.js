App.widget.OrdenPago2 = function (container) {

  var initEventListeners = function () {
  };

  var initValidators = function () {
    var tipoFacturaValidation = new LiveValidation( "tipoFactura");
    tipoFacturaValidation.add(Validate.Presence, 
        {failureMessage: "El tipo de factura es obligatorio"});
  };

  return {
    render: function () {
      initEventListeners();
      initValidators();
    }
  };
}
