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
        var form = jQuery(".js-import-form");
        var loadingIndicator = new App.widget.LoadingIndicator(form);
        form.find("input[type=submit]").click(function () {
          loadingIndicator.start();
        });
      });

    </script>

    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">Importaci&oacute;n de Shoppers</h2>
      <form action="import" class="form-shop form-shop-big js-import-form" enctype="multipart/form-data" method="POST">
        <div class="cell box-green buscador">
          <div class="field">
            <label for="name">Archivo a importar: </label>
            <input type="file" id="file" name="file" />
          </div>
          <ul class="action-columns">
            <li><input type="submit" value="Importar" class="btn-shop-small"></li>
          </ul>
        </div>
      </form>
      <table summary="Resultado de la importacion" class="table-form js-results">
        <thead>
          <tr>
            <th scope="col">Nombre</th>
            <th scope="col">Detalle</th>
            <th scope="col">Estado</th>
          </tr>
        </thead>
        <tbody>
          <#list model["items"] as item>
          <tr>
            <td>${item.identifier}</td>
            <td>${item.detail}</td>
            <td>${item.status}</td>
            </tr>
          </#list>
        </tbody>
      </table>
    </div>
  </body>
</html>