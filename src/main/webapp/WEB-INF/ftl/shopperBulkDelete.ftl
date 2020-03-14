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

    <script src="../script/jquery.js"></script>
    <script src="../script/jquery-ui.js"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">Shopper</h2>
      <form class="form-shop form-shop-big" action="bulkDelete" method="POST">
        <div class="cell">
          <div class="box-green">
            <fieldset>
              <legend>Ingrese la lista de shoppers (utilizando el login de Shopmetrics) que desea deshabilitar:</legend>
              <div class="field">
                <textarea id="shoppers" name="shoppers" rows="25" class="item-field"></textarea>
              </div>
            </fieldset>
          </div>
        </div>
        <ul class="action-columns">
          <li><a href="../." class="btn-shop-small">Cancelar</a></li>
          <li><input type="submit" value="Ejecutar" class="btn-shop-small"></li>
        </ul>
      </form>
    </div>
  </body>
</html>