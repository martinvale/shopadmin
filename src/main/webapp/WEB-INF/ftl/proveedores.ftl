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

App.widget.Proveedores = function (container, proveedores) {

  var deleteConfirmDialog = jQuery("#confirm-delete-proveedor").dialog({
    resizable: false,
    autoOpen: false,
    height: 140,
    width: 400,
    modal: true,
    buttons: {
      "Ok": function() {
        var proveedorId = deleteConfirmDialog.proveedorId;
        jQuery.ajax({
          url: "proveedor/" + proveedorId,
          method: 'DELETE'
        }).done(function (data) {
          container.find("#proveedor-" + proveedorId).remove();
          deleteConfirmDialog.dialog("close");
        })
      },
      Cancel: function() {
        $(this).dialog("close");
      }
    }
  });

  var initEventListeners = function () {
    jQuery.each(proveedores, function(index, proveedor) {
      container.find(".js-delete-" + proveedor.id).click(function (event) {
        event.preventDefault();
        deleteConfirmDialog.proveedorId = proveedor.id;
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
        var proveedores = [
        <#list model["proveedores"].items as proveedor>
          {
            id: ${proveedor.id?c},
            name: '${proveedor.name!''}',
          }
        <#if proveedor_has_next>,</#if></#list>
        ];

        var proveedores = new App.widget.Proveedores(jQuery(".js-proveedores"), proveedores);
        proveedores.render();
      });

    </script>

    <!--script src="../script/ShopperSelector.js"></script-->

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">Proveedores</h2>
      <form class="form-shop form-shop-big" action="search" method="GET">
          <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                  <div class="ch-popover-content">
                      Revisa los datos. Debes completar campos "Número" y "Factura".
                  </div>
          </div-->
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
          <li><a href="new" class="btn-shop-small">Nuevo proveedor</a></li>
        </ul>
      </form>
      <table summary="Lista de items" class="table-form js-proveedores">
        <thead>
          <tr>
            <th scope="col">Nombre</th>
          </tr>
        </thead>
        <tbody>
          <#list model["proveedores"].items as proveedor>
          <tr id="proveedor-${proveedor.id?c}">
            <td><a href="${proveedor.id?c}">${proveedor.description}</a> <a class="js-delete-${proveedor.id?c}" href="#">borrar</a></td>
          </tr>
          </#list>
        </tbody>
      </table>
      <div class="paginator">
        <a href="search?page=1&name=${model["name"]!''}">&lt;&lt;</a>
        <#list 1..model["proveedores"].count as i>
          <#assign page = (i - 1) / model["pageSize"] />
          <#if (i - 1) % model["pageSize"] == 0>
            <a href="search?page=${page + 1}&name=${model["name"]!''}">${page + 1}</a>
          </#if>
        </#list>
        <#assign lastPage = (model["proveedores"].count / model["pageSize"])?int />
        <#if model["proveedores"].count % model["pageSize"] &gt; 0>
          <#assign lastPage = lastPage + 1 />
        </#if>
        <a href="search?page=${lastPage}&name=${model["name"]!''}">&gt;&gt;</a>

        <#assign maxIndex = model["page"] * model["pageSize"] />
        <#if maxIndex &gt; model["proveedores"].count>
          <#assign maxIndex = model["proveedores"].count />
        </#if>
        <span class="resultset">Usuarios de ${model["start"]?c} a ${maxIndex} de ${model["proveedores"].count}</span>
      </div>
    </div>
    <div id="confirm-delete-proveedor" title="Borrar proveedor">
      <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Esta seguro que desea borrar el proveedor seleccionado?</p>
    </div>
  </body>
</html>