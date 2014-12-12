<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="../css/jquery-ui/jquery-ui.css">

    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/shop.css">
    <link rel="stylesheet" href="../css/custom.css">

    <link rel="stylesheet" href="../font-awesome/css/font-awesome.min.css">

    <script src="../script/jquery.js"></script>
    <script src="../script/jquery-ui.js"></script>
    <script src="../script/pure.min.js"></script>
    <script src="../script/livevalidation.js"></script>

    <script src="../script/spin.js"></script>
    <script src="../script/jquery.spin.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

      jQuery(document).ready(function() {
        var buscador = new App.widget.BuscadorDeudaShopper(jQuery(".js-debt-search"));
        buscador.render();
      });

    </script>

    <script src="../script/ShopperSelector.js"></script>
    <script src="../script/BuscadorDeudaShopper.js"></script>
    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">B&uacute;squeda de deuda de shopper</h2>
      <form class="form-shop form-shop-big js-debt-search">
        <div class="cell">
          <div class="box-green">
            <div class="form-shop-row-left shopper-widget js-shopper-selector">
              <label for="shopper">Shooper: </label>
              <input type="text" id="shopper" name="shopper" value="${model['shopper']!''}" class="js-shopper" />
              <a id="clear-shopper" href="#" class="clear js-clear">limpiar</a>
              <input type="hidden" name="shopperId" value="" class="js-shopper-id" />
              <input type="hidden" name="shopperDni" value="${model['shopperDni']!''}" class="js-shopper-dni" />
            </div>
            <ul class="options">
              <li>
                <input type="checkbox" name="gac" id="gac" value="1" checked="checked">
                <label for="gac">GAC</label>
              </li>
              <li>
                <input type="checkbox" name="mcd" id="mcd" value="1" checked="checked">
                <label for="gac">MCD</label>
              </li>
              <li>
                <input type="checkbox" name="adicionales" id="adicionales" value="1" checked="checked">
                <label for="adicionales">ADICIONALES</label>
              </li>
              <li>
                <input type="checkbox" name="shopmetrics" id="shopmetrics" value="1" checked="checked">
                <label for="shopmetrics">SHOPMETRICS</label>
              </li>
            </ul>
            <ul class="date">
              <li class="check">
                <input type="checkbox" name="applyDate" id="applyDate" value="1" checked="checked">
                <label for="applyDate">Filtrar fecha</label>
              </li>
              <li>
                <label for="desde">Desde</label>
                <input type="text" name="desde" id="desde" class="js-date" value="01/01/2014">
              </li>
              <li>
                <label for="hasta">Hasta</label>
                <input type="text" name="hasta" id="hasta" class="js-date" value="01/01/2015">
              </li>
            </ul>
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="button" value="Buscar" class="btn-shop-small js-buscar" /></li>
          <li><input type="button" value="Imprimir" class="btn-shop-small js-print" /></li>
        </ul>

        <div class="items-container">
          <table summary="Lista de items" class="table-form js-items">
            <thead>
              <tr>
                <th scope="col"><a id="order-empresa" href="#" class="js-order">Empresa <i class="fa fa-angle-down"></i></a></th>
                <th scope="col"><a id="order-programa" href="#" class="js-order">Subcuestionario <i class="fa fa-angle-down"></i></a></th>
                <th scope="col"><a id="order-local" href="#" class="js-order">Local <i class="fa fa-angle-down"></i></a></th>
                <th scope="col"><a id="order-importe" href="#" class="js-order">Importe <i class="fa fa-angle-down"></i></a></th>
                <th scope="col"><a id="order-fecha" href="#" class="js-order">Fecha <i class="fa fa-angle-down"></i></a></th>
                <th scope="col"><a id="order-descripcion" href="#" class="js-order">Pago <i class="fa fa-angle-down"></i></a></th>
              </tr>
            </thead>
            <tbody class="items">
              <tr>
                <td class="empresa"></td>
                <td class="subcuestionario"></td>
                <td class="local"></td>
                <td class="importe"></td>
                <td class="fecha"></td>
                <td class="pago"></td>
              </tr>
            </tbody>
          </table>
        </div>
        <div class="summary">
          <label>Reintegros</label>
          <input type="text" class="js-reintegros" />
          <label>Honorarios</label>
          <input type="text" class="js-honorarios" />
          <label>Otros gastos</label>
          <input type="text" class="js-otros-gastos" />
        </div>

      </form>
    </div>
  </body>
</html>