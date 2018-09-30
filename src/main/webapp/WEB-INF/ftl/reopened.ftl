<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">
    <meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />

    <link rel="stylesheet" href="../css/jquery-ui/jquery-ui.css">

    <link rel="stylesheet" href="../css/base.css">
    <link rel="stylesheet" href="../css/shop.css">
    <link rel="stylesheet" href="../css/custom.css">

    <link rel="stylesheet" href="../font-awesome/css/font-awesome.min.css">

    <script src="../script/jquery.js"></script>
    <script src="../script/jquery-ui.js"></script>

    <script src="../script/livevalidation.js"></script>

    <script type="text/javascript">


      jQuery(document).ready(function() {
        jQuery(".js-fecha" ).datepicker({
          dateFormat: 'dd/mm/yy',
          onSelect: function(dateText, datePicker) {
            $(this).attr('value', dateText);
          }
        });

      });
    </script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">B&uacute;squeda de ordenes reabiertas</h2>
      <form action="reopened" class="form-shop form-shop-big js-search-form" method="GET">
        <div class="cell box-green buscador">
          <div class="field">
            <label for="fechaDesde">Desde: </label>
            <input type="text" id="fechaDesde" name="fechaDesde" value="${(model['fechaDesde'])!''}" class="js-fecha" />
          </div>
          <div class="field">
            <label for="fechaHasta">Hasta: </label>
            <input type="text" id="fechaHasta" name="fechaHasta" value="${(model['fechaHasta'])!''}" class="js-fecha" />
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Buscar" class="btn-shop-small"></li>
        </ul>
      </form>
      <div class="js-ordenes">
        <table summary="Listado de ordenes a pagar" class="table-form">
          <thead>
            <tr>
              <th scope="col" style="width: 10%">Nro de orden</th>
              <th scope="col">Titular</th>
              <th scope="col" style="width: 20%">Cantidad de veces reabiertas</th>
            </tr>
          </thead>
          <tbody>
            <#assign totalPago = 0 />
            <#list model["result"].items as item>
            <tr>
              <td><a href="${item.numero?c}" target="_blank">${item.numero?c}</a></td>
              <td>${item.titular!''}</td>
              <td style="text-align: right">${item.timesReopened}</td>
            </tr>
            </#list>
          </tbody>
        </table>
      </div>
    </div>

  </body>
</html>