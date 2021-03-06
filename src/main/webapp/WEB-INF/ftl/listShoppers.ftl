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

    <script src="../script/spin.js"></script>
    <script src="../script/jquery.spin.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

      jQuery(document).ready(function() {
        jQuery(".js-import" ).click(function () {
          jQuery("#shopper-import").show();
          return false;
        });
        var form = jQuery(".js-search-form");
        var loadingIndicator = new App.widget.LoadingIndicator(form);
        form.find("input[type=submit]").click(function () {
          loadingIndicator.start();
        });

      });

    </script>

    <script src="../script/ShopperSelector.js"></script>
    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">B&uacute;squeda de Shoppers</h2>
      <form action="" class="form-shop form-shop-big js-search-form" method="GET">
        <div class="cell box-green buscador">
          <div class="field">
            <label for="name">Nombre: </label>
            <input type="text" id="name" name="name" value="${(model['name'])!''}" />
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Buscar" class="btn-shop-small"></li>
        </ul>
      </form>
      <ul class="action-columns">
        <li><a href="create" class="btn-shop-small">Nuevo shopper</a></li>
        <li><a href="bulkDelete" class="btn-shop-small">Deshabilitar en forma masiva</a></li>
      </ul>
      <table summary="Lista de Shoppers" class="table-form js-results">
        <thead>
          <tr>
            <th scope="col">Apellido</th>
            <th scope="col">Nombre</th>
            <th scope="col">DNI</th>
            <th scope="col">Estado</th>
          </tr>
        </thead>
        <tbody>
          <#assign resultSet = model["shoppers"] />
          <#list resultSet as shopper>
          <tr>
            <td>${(shopper.surname)!''} <a href="${shopper.id?c}">editar</a></td>
            <td>${(shopper.firstName)!''}</td>
            <td>${shopper.identityId!''}</td>
            <td>${shopper.enabled?string("Activo", "Inactivo")}</td>
            </tr>
          </#list>
        </tbody>
      </table>
    </div>
  </body>
</html>