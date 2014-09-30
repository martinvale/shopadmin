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
    <script src="../script/pure.min.js"></script>
    <script src="../script/livevalidation.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

      jQuery(document).ready(function() {
      });

    </script>

    <!--script src="../script/ShopperSelector.js"></script-->

  </head>
  <body>
    <#include "header.ftl" />

    <#if model["proveedor"]??>
      <#assign proveedor = model["proveedor"] />
    </#if>
    <div class="container-box-plantilla">
      <h2 class="container-tit">Proveedor</h2>
    </div>
  </body>
</html>