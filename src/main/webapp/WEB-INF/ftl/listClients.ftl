<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8">
    <title>Shopnchek</title>
    <meta http-equiv="cleartype" content="on">

    <link rel="stylesheet" href="../../css/jquery-ui/jquery-ui.css">

    <link rel="stylesheet" href="../../css/base.css">
    <link rel="stylesheet" href="../../css/shop.css">
    <link rel="stylesheet" href="../../css/custom.css">

    <script src="../../script/jquery.js"></script>
    <script src="../../script/jquery-ui.js"></script>
    <script src="../../script/pure.min.js"></script>
    <script src="../../script/livevalidation.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.clientes = function (container, clientes) {

  var deleteConfirmDialog = jQuery("#confirm-delete-cliente").dialog({
    resizable: false,
    autoOpen: false,
    height: 160,
    width: 500,
    modal: true,
    buttons: {
      "Ok": function() {
        var clienteId = deleteConfirmDialog.clienteId;
        jQuery.ajax({
          url: clienteId,
          method: 'DELETE'
        }).done(function (data) {
          if (data == true) {
            container.find("#cliente-" + clienteId).remove();
          } else {
            alert("No se pudo borrar el cliente porque tiene visitas asociadas");
          }
          deleteConfirmDialog.dialog("close");
        })
      },
      Cancel: function() {
        $(this).dialog("close");
      }
    }
  });

  var initEventListeners = function () {
    jQuery.each(clientes, function(index, cliente) {
      container.find(".js-delete-" + cliente.id).click(function (event) {
        event.preventDefault();
        deleteConfirmDialog.clienteId = cliente.id;
        deleteConfirmDialog.dialog("open");
      });
    })
  };

  return {
    render: function () {
      initEventListeners();
    }
  };
};

      jQuery(document).ready(function() {
        var clientes = [
        <#list model["clients"] as cliente>
          {
            id: ${cliente.id?c},
            name: '${cliente.name?js_string!''}',
          }
        <#if cliente_has_next>,</#if></#list>
        ];

        var clientes = new App.widget.clientes(jQuery(".js-clientes"), clientes);
        clientes.render();
      });

    </script>

    <!--script src="../script/ShopperSelector.js"></script-->

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">Clientes</h2>
      <!--form class="form-shop form-shop-big" action="search" method="GET">
        <div class="cell">
          <div class="box-green">
            <div class="form-shop-row-left">
              <label for="name">Nombre: </label>
              <input type="text" id="name" name="name" value="${model['name']!''}" />
              <input type="hidden" name="page" value="1" />
            </div>
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Buscar" class="btn-shop-small"></li>
          <li><a href="new" class="btn-shop-small">Nuevo cliente</a></li>
        </ul>
      </form-->
      <table summary="Lista de items" class="table-form js-clientes">
        <thead>
          <tr>
            <th scope="col">Nombre</th>
            <th scope="col" width="20%">Pais</th>
          </tr>
        </thead>
        <tbody>
          <#list model["clients"] as cliente>
          <tr id="cliente-${cliente.id?c}">
            <td>${cliente.name} <a href="${cliente.id?c}">editar</a> <a href="reassign/${cliente.id?c}">reasignar</a> <a class="js-delete-${cliente.id?c}" href="#">borrar</a></td>
            <td>${cliente.country!''}</td>
          </tr>
          </#list>
        </tbody>
      </table>
    </div>
    <div style="display: none;">
      <div id="confirm-delete-cliente" title="Borrar cliente">
        <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Esta seguro que desea borrar el cliente seleccionado?</p>
      </div>
    </div>
  </body>
</html>