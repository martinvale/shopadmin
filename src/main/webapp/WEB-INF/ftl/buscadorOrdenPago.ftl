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

    <script src="../script/spin.js"></script>
    <script src="../script/jquery.spin.js"></script>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

App.widget.Buscador = function (container) {

  var tabs = container.find(".js-filters").tabs();

  var titularSelector;

  var initEventListeners = function () {
    var loadingIndicator = new App.widget.LoadingIndicator(container);
    container.find("input[type=submit]").click(function () {
      loadingIndicator.start();
    });
  };

  return {
    render: function() {
      titularSelector = new App.widget.TitularSelector(
          container.find(".js-titular-selector"));
      titularSelector.render();
      initEventListeners();
    }
  }
}

      jQuery(document).ready(function() {
        var buscador = new App.widget.Buscador(jQuery(".js-search"));
        buscador.render();
      });
    </script>

    <script src="../script/TitularSelector.js"></script>
    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />
    <#setting locale="es_AR">

    <div class="container-box-plantilla">
      <h2 class="container-tit">Buscar Orden de pago</h2>
      <form action="searchByNumber" method="POST" class="form-shop form-shop-big js-search">
        <!-- FILA 1 -->
        <div class="cell box-green buscador">
          <fieldset>
            <legend>B&uacute;squeda simple</legend>
            <div class="form-shop-row-left">
              <label for="number">N&uacute;mero</label>
              <input type="text" name="numeroOrden" id="number" value="${(model['numeroOrden']?c)!""}" />
            </div>
          </fieldset>
        </div>
        <!-- FIN FILA 1 -->
        <!-- FIN FILA 2 -->
        <ul class="action-columns">
          <li><input type="submit" class="btn-shop-small" value="Buscar"></li>
        </ul>
        <!-- FIN FILA 2 -->
      </form>
      <form action="search" method="POST" class="form-shop form-shop-big js-search">
        <!-- FILA 1 -->
        <div class="cell box-green buscador">
          <fieldset>
            <legend>B&uacute;squeda avanzada</legend>
            <div class="form-shop-options-left titular-widget js-titular-selector">
              <p class="mandatory">Titular</p>
              <ul>
                <#assign tipoTitular = model['tipoTitular']!1 />
                <li class="form-shop">
                  <input type="radio" name="tipoTitular" id="shopper" value="1" class="js-shopper" <#if tipoTitular == 1>checked="checked"</#if>>
                  <label for="shopper">Shopper</label>
                </li>
                <li class="form-shop">
                  <input type="radio" name="tipoTitular" id="proveedor" value="2" class="js-proveedor" <#if tipoTitular == 2>checked="checked"</#if>>
                  <label for="proveedor">Proveedor</label>
                </li>
              </ul>
              <div class="combo-titular">
                <#assign titularId = "${(model['titularId']?c)!''}" />
                <#assign titularNombre = "${model['titularNombre']!''}" />
                <input type="text" value="${titularNombre}" class="nombre-titular js-titulares" />
                <input type="hidden" name="titularId" value="${titularId}" class="js-titular-id" />
              </div>
            </div>
            <div class="field">
              <label for="dniShopper">DNI del shopper</label>
              <input id="dniShopper" type="text" name="dniShopper" value="${model["dniShopper"]!""}" />
            </div>
            <div class="field">
              <label for="numeroCheque">Nro de cheque</label>
              <input id="numeroCheque" type="text" name="numeroCheque" value="${model["numeroCheque"]!""}" />
            </div>
            <div class="field">
              <label for="state">Estado de la orden</label>
              <select id="state" name="estadoId">
                <option value=""></option>
                <#assign state = model["state"]!0 />
                <#list model["orderStates"] as orderState>
                  <option value="${orderState.id}" <#if orderState.id == state>selected="selected"</#if>>${orderState.description}</option>
                </#list>
              </select>
            </div>
          </fieldset>
        </div>
        <!-- FIN FILA 1 -->
        <!-- FIN FILA 2 -->
        <ul class="action-columns">
          <li><input type="submit" class="btn-shop-small" value="Buscar"></li>
        </ul>
        <!-- FIN FILA 2 -->
      </form>

      <h2 class="subtitulo">Ordenes de pago</h2>
      <table summary="Listado de ordenes de pago" class="table-form">
        <thead>
          <tr>
            <th scope="col">Nro de Orden</th>
            <th scope="col">Importe</th>
            <th scope="col">Fecha de Pago</th>
            <th scope="col">Estado</th>
          </tr>
        </thead>
        <tbody>
          <#assign resultSet = model["result"] />
          <#list resultSet.items as orden>
          <tr>
            <td>${orden.numero?c} <a href="${orden.numero?c}">editar</a></td>
            <td class="js-importe">${orden.importe?string.currency}</td>
            <td class="js-fecha">${orden.fechaPago?string("dd/MM/yyyy")}</td>
            <td class="js-estado">${orden.state}</td>
          </tr>
          </#list>
        </tbody>
      </table>

      <div class="paginator">
        <#assign parameters = "shopperDni=${model['shopperDni']!''}&tipoTitular=${model['tipoTitular']!''}&titularId=${model['titularId']!''}&estadoId=${model['state']!''}&numeroCheque=${model['numeroCheque']!''}" />

        <#if model["page"] &gt; 1>
          <a href="?page=${(model['page'] - 1)}&${parameters}">&lt;&lt;</a>
        <#else>
          <span>&lt;&lt;</span>
        </#if>

        <#assign maxIndex = model["page"] * model["pageSize"] />
        <#if maxIndex &gt; resultSet.count>
          <#assign maxIndex = resultSet.count />
          <span>&gt;&gt;</span>
        <#else>
          <a href="?page=${(model['page'] + 1)}&${parameters}">&gt;&gt;</a>
        </#if>

        <#assign start = 0 />
        <#if resultSet.count &gt; 0>
          <#assign start = ((model["page"] - 1) * model["pageSize"]) + 1 />
        </#if>
        <span class="resultset">Adicionales de ${(start)?c} a ${maxIndex} de ${resultSet.count}</span>
        <a class="tool" href="export?${parameters}">descargar</a>
      </div>

    </div>
  </body>
</html>