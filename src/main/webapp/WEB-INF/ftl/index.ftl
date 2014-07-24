<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="css/base.css">
    <link rel="stylesheet" href="css/shop.css">

    <link rel="stylesheet" href="/css/jquery-ui/jquery-ui.css">

    <script src="/script/jquery.js"></script>
    <script src="/script/jquery-ui.js"></script>
    <script src="/script/livevalidation.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.OrdenPago = function (container) {

  var initEventListeners = function () {
  };

  var initValidators = function () {
    var tipoFacturaValidation = new LiveValidation("tipoFactura");
    tipoFacturaValidation.add(Validate.Exclusion, {
        within: ["Seleccionar"],
        failureMessage: "El tipo de factura es obligatorio"
    });
    var ivaValidation = new LiveValidation("iva");
    ivaValidation.add(Validate.Presence, {
        failureMessage: "El porcentaje de IVA es obligatorio"
    });
    var fechaPagoValidation = new LiveValidation("fechaPago");
    fechaPagoValidation.add(Validate.Presence, {
        failureMessage: "La fecha de pago es obligatoria"
    });
    var medioPagoValidation = new LiveValidation("medioPago");
    medioPagoValidation.add(Validate.Exclusion, {
        within: ["Seleccionar"],
        failureMessage: "El medio de pago es obligatorio"
    });
  };

  return {
    render: function () {
      initEventListeners();
      initValidators();
    }
  };
}


      jQuery(document).ready(function() {

        var titularSelectorWidget = new App.widget.TitularSelector(jQuery(".js-titular-selector"));
        titularSelectorWidget.render();

        var ordenPago = new App.widget.OrdenPago(jQuery(".js-orden-pago"));
        ordenPago.render();
      });
    </script>

    <script src="/script/TitularSelector.js"></script>

<style>
.LV_validation_message.LV_valid {
  display: none;
}

.LV_invalid {
  color:#CC0000;
}
    
.LV_valid_field,
input.LV_valid_field:hover, 
input.LV_valid_field:active,
textarea.LV_valid_field:hover, 
textarea.LV_valid_field:active {
  border: 1px solid #00CC00;
}
    
.LV_invalid_field, 
input.LV_invalid_field:hover, 
input.LV_invalid_field:active,
textarea.LV_invalid_field:hover, 
textarea.LV_invalid_field:active {
  border: 1px solid #CC0000;
}
</style>
  </head>
  <body>
    <#assign user=model["user"] />
    <header>
      <div class="header-box">
        <h1>Shopnchek<span class="tag-intranet">intranet</span></h1>
        <p class="user"> ${user.username} <a href="/logout">Salir</a></p>
      </div>
    </header>
    <div class="container-box-plantilla">
        <h2 class="container-tit">Orden de pago</h2>
        <form action="orden" method="POST" class="form-shop form-shop-big js-orden-pago">
          <!-- FILA 1 -->
          <div class="cell">
            <div class="box-green cell-c1">
              <div class="form-shop-row-left">
                <label for="number">N&uacute;mero</label>
                <input type="number" placeholder="30" name="number" id="number">
              </div>
            </div>
            <div class="box-green cell-c2">
              <div class="form-shop-options-left js-titular-selector">
                <p> Titular</p>
                <ul>
                  <li class="form-shop">
                    <input type="radio" name="tipoTitular" id="shopper" value="1" class="js-shopper" checked="checked">
                    <label for="shopper">Shopper</label>
                  </li>
                  <li class="form-shop">
                    <input type="radio" name="tipoTitular" id="proveedor" value="2" class="js-proveedor">
                    <label for="proveedor">Proveedor</label>
                  </li>
                </ul>
                <div class="combo-titular">
                  <input type="text" value="" class="js-titulares" />
                  <input type="hidden" name="titularId" value="" class="js-titular-id" />
                </div>
              </div>
            </div>
          </div>
          <!-- FIN FILA 1 -->
          <!-- FIN FILA 2 -->
          <ul class="columnas-form">
            <li>
              <div class="form-shop-row">
                <label for="tipoFactura">Factura</label>
                <select id="tipoFactura" name="tipoFactura">
                  <option value="Seleccionar">Seleccionar</option>
                  <option value="S/F">S/F</option>
                  <option value="A">A</option>
                  <option value="C">C</option>
                  <option value="M">M</option>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="fechaPago">Fecha pago</label>
                <input type="text" placeholder="11/10/2014" id="fechaPago" name="fechaPago">
              </div>
              <div class="form-shop-row">
                <label for="medioPago">M. de pago</label>
                <select id="medioPago" name="medioPagoId">
                  <option value="Seleccionar">Seleccionar</option>
                  <#list model["mediosPago"] as medioPago>
                    <option value="${medioPago.id}">${medioPago.description}</option>
                  </#list>
                </select>
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="numeroFactura">Factura N&deg;</label>
                <input type="number" name="numeroFactura" id="numeroFactura" />
              </div>
              <div class="form-shop-row">
                <label for="state">Estado</label>
                <select id="state" name="estadoId">
                  <#list model["orderStates"] as orderState>
                    <option value="${orderState.id}">${orderState.description}</option>
                  </#list>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="numeroChequera">Chequera N&deg;</label>
                <input type="number" name="numeroChequera" id="numeroChequera" />
              </div>
              <div class="form-shop-row">
                <label for="transferId">ID Transfer</label>
                <input type="number" name="transferId" id="transferId" />
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="iva">IVA %</label>
                <input type="text" name="iva" id="iva" />
              </div>
              <div class="form-shop-row">
                <label for="localidad">Localidad</label>
                <select id="localidad" name="localidad">
                  <option value="0">Seleccionar</option>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="numeroCheque">Cheque N&deg;</label>
                <input type="number" name="numeroCheque" id="numeroCheque">
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="fechaCheque">Fecha cheque</label>
                <input type="text" placeholder="11/10/2014" id="fechaCheque" name="fechaCheque">
              </div>
            </li>
          </ul>
          <ul class="action-columns">
            <li> <input type="submit" class="btn-shop-small" value="Guardar" name="save"></li>
            <li> <input type="submit" class="btn-shop-small" value="Carátula" name="carta"></li>
            <li> <input type="submit" class="btn-shop-small" value="Observaciones" name="obs"></li>
            <li> <input type="submit" class="btn-shop-small" value="Obsp/Shopper" name="shop"></li>
          </ul>
          <!-- FIN FILA 2 -->

         <!-- FILA 3 -->
         <h2 class="subtitulo">Items</h2>
         <div class="scroll-table">
             <table summary="Listado de cobros en MercadoPago" class="table-form ">
            
                <thead>
                    <tr>
                        <th scope="col">Shopper</th>
                        <th scope="col">Cliente</th>
                        <th scope="col">Mes</th>
                        <th scope="col">Año</th>
                        <th scope="col">Sucursal</th>
                        <th scope="col">Pago</th>
                        <th scope="col">Importe</th>
                        <th scope="col">DNI</th>
                        <th scope="col">Asignación</th>
                        <th scope="col">Fecha</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        
                    </tr>
                     <tr>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        <td></td>
                        
                    </tr>
                </tbody>
            </table>
        </div>
        <ul class="action-columns">
            <li> <input type="submit" class="btn-shop-small" value="Agregar" name="ass"></li>
            <li> <input type="submit" class="btn-shop-small" value="Deuda Shopper" name="deusa"></li>
            <li> <input type="submit" class="btn-shop-small" value="Eliminar item" name="delete"></li>
        </ul>

  <!-- FIN FILA 3 -->
  <!-- FILA 4 -->
  <h2 class="subtitulo"> Totales </h2>
         <ul class="columnas-form col-three">
            <li>
                <div class="form-shop-row">
                    <label for="subt0">Subt. honorarios</label>
                    <input type="text" id="sbt0">
                </div>
                <div class="form-shop-row">
                    <label for="sbt1">Subt. reintegros</label>
                    <input type="text" id="sbt1">
                </div>
            </li>
            <li>
                <div class="form-shop-row">
                    <label for="facN">IVA $</label>
                     <input type="text" id="sbt1">
                </div>
                <div class="form-shop-row">
                   <label for="sbt3">Subt. otros gastos</label>
                    <input type="text" id="sbt3">
                </div>
            </li>
            <li>
                  <div class="form-shop-row">
                   <label for="honorarios">Honorarios c/IVA</label>
                    <input type="text" id="honorarios">
                </div>
                
                <div class="form-shop-row">
                    <label for="tot">Total general</label>
                    <input type="number"  max="99" min="18" name="tot" id="tot">
                </div>
            </li>
         </ul>

    <div class="actions-form">
          <ul class="action-columns">
            <li> <input type="submit" class="btn-shop" value="Imprimir" name="imp"></li>
            <li> <input type="submit" class="btn-shop" value="Imprimir Detalle" name="detail"></li>
            <li> <input type="submit" class="btn-shop-action" value="Eliminar Shopper" name="deleteshop"></li>
            <li> <input type="submit" class="btn-shop-action" value="Cerrar" name="cerrar"></li>
        </ul>
    </div>
    </form>
    </div>
  </body>
</html>