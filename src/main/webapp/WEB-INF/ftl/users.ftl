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

App.widget.Users = function (container, users) {

  var deleteConfirmDialog = jQuery("#confirm-delete-user").dialog({
    resizable: false,
    autoOpen: false,
    height: 140,
    width: 400,
    modal: true,
    buttons: {
      "Ok": function() {
        var userId = deleteConfirmDialog.userId;
        jQuery.ajax({
          url: "user/" + userId,
          method: 'DELETE'
        }).done(function (data) {
          container.find("#user-" + userId).remove();
          deleteConfirmDialog.dialog("close");
        })
      },
      Cancel: function() {
        $(this).dialog("close");
      }
    }
  });

  var initEventListeners = function () {
    jQuery.each(users, function(index, user) {
      container.find(".js-delete-" + user.id).click(function (event) {
        event.preventDefault();
        deleteConfirmDialog.userId = user.id;
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
        var users = [
        <#list model["users"].data as user>
          {
            id: ${user.id?c},
            name: '${user.name!''}',
          }
        <#if user_has_next>,</#if></#list>
        ];

        var users = new App.widget.Users(jQuery(".js-users"), users);
        users.render();
      });

    </script>

    <!--script src="../script/ShopperSelector.js"></script-->

  </head>
  <body>
    <#assign user = model["user"] />
    <header>
      <div class="header-box">
        <h1>Shopnchek<span class="tag-intranet">intranet</span></h1>
        <#include "header.ftl" />
        <p class="user"> ${user.username} <a href="../j_spring_security_logout">Salir</a></p>
      </div>
    </header>
    <div class="container-box-plantilla">
      <h2 class="container-tit">Usuarios</h2>
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
          <li><a href="new" class="btn-shop-small">Nuevo usuario</a></li>
        </ul>
      </form>
      <table summary="Lista de items" class="table-form js-users">
        <thead>
          <tr>
            <th scope="col">Usuario</th>
            <th scope="col">Nombre</th>
          </tr>
        </thead>
        <tbody>
          <#list model["users"].data as user>
          <tr id="user-${user.id?c}">
            <td><a href="${user.id?c}">${user.username}</a> <a class="js-delete-${user.id?c}" href="#">borrar</a></td>
            <td>${user.name!''}</td>
          </tr>
          </#list>
        </tbody>
      </table>
      <div class="paginator">
        <a href="search?page=1&name=${model["name"]!''}">&lt;&lt;</a>
        <#list 1..model["users"].size as i>
          <#assign page = (i - 1) / model["pageSize"] />
          <#if (i - 1) % model["pageSize"] == 0>
            <a href="search?page=${page + 1}&name=${model["name"]!''}">${page + 1}</a>
          </#if>
        </#list>
        <#assign lastPage = (model["users"].size / model["pageSize"])?int />
        <#if model["users"].size % model["pageSize"] &gt; 0>
          <#assign lastPage = lastPage + 1 />
        </#if>
        <a href="search?page=${lastPage}&name=${model["name"]!''}">&gt;&gt;</a>

        <#assign maxIndex = model["page"] * model["pageSize"] />
        <#if maxIndex &gt; model["users"].size>
          <#assign maxIndex = model["users"].size />
        </#if>
        <span class="resultset">Usuarios de ${model["start"]?c} a ${maxIndex} de ${model["users"].size}</span>
      </div>
    </div>
    <div id="confirm-delete-user" title="Borrar usuario">
      <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Esta seguro que desea borrar el usuario seleccionado?</p>
    </div>
  </body>
</html>