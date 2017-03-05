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

      App.widget.Table = function (container, order, form) {

        var orderBy = order.orderBy;

        var ascending = order.ascending;

        var setOrder = function (field) {
          var isCurrentField = false;
          if (field == orderBy) {
            ascending = !ascending;
          } else {
            orderBy = field;
          }
          return {'orderBy': orderBy, 'ascending': ascending};
        }

        var initEventListeners = function() {
          container.find(".js-order").click(function () {
            event.preventDefault();
            var header = jQuery(event.target);
            var arrow = header.find(".fa");
            var field = event.currentTarget.id.substring(6);
            var orderDefinition = setOrder(field);

            container.find(".js-order").removeClass("selected");
            header.addClass("selected");
            if (orderDefinition.ascending) {
              arrow.removeClass("fa-angle-down");
              arrow.addClass("fa-angle-up");
            } else {
              arrow.removeClass("fa-angle-up");
              arrow.addClass("fa-angle-down");
            };
            form.setOrder(field, ascending);
          });
        };

        return {
          render: function() {
            initEventListeners();
          }
        }
      }

      App.widget.AdicionalEditor = function (container) {

        var loadingIndicator = new App.widget.LoadingIndicator(container);

        var dateFrom = container.find(".js-date-from" );

        var dateTo = container.find(".js-date-to" );

        var initEventListeners = function() {
          container.find("input[type=submit]").click(function () {
            loadingIndicator.start();
          });
        };

        return {
          render: function() {
            dateFrom.datepicker({
              dateFormat: 'dd/mm/yy',
              onSelect: function(dateText, datePicker) {
                $(this).attr('value', dateText);
              }
            });

            dateTo.datepicker({
              dateFormat: 'dd/mm/yy',
              onSelect: function(dateText, datePicker) {
                $(this).attr('value', dateText);
              }
            });
            initEventListeners();
          },
          setOrder: function (orderBy, ascending) {
            container.find("input[name='orderBy']").val(orderBy);
            container.find("input[name='ascending']").val(ascending);
            container.submit();
          }
        }

      }

      jQuery(document).ready(function() {
        var shopperSelector = new App.widget.ShopperSelector(jQuery(".js-shopper-selector"), true);
        shopperSelector.render();

        var formElement = jQuery(".js-search-form");
        var form = App.widget.AdicionalEditor(formElement);
        form.render();

        var tableElement = jQuery(".js-results");
        var orderBy = '${model['orderBy']}';
        var ascending = ${model['ascending']?c};
        var table = App.widget.Table(tableElement, {'orderBy': orderBy, 'ascending': ascending}, form);
        table.render();
      });

    </script>

    <script src="../script/ShopperSelector.js"></script>
    <script src="../script/LoadingIndicator.js"></script>

  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
      <h2 class="container-tit">B&uacute;squeda de adicionales</h2>
      <form action="" class="form-shop form-shop-big js-search-form" method="GET">
        <input type="hidden" name="orderBy" value="${model['orderBy']!'fecha'}" class="js-order-by" />
        <input type="hidden" name="ascending" value="${model['ascending']?c}" class="js-ascending" />
          <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                  <div class="ch-popover-content">
                      Revisa los datos. Debes completar campos "Número" y "Factura".
                  </div>
          </div-->
        <div class="cell box-green buscador">
          <div class="field shopper-widget js-shopper-selector">
            <label for="shopper">Shooper: </label>
            <input type="text" id="shopper" name="shopper" value="${(model['shopper'].name)!''}" class="js-shopper" />
            <a id="clear-shopper" href="#" class="clear js-clear">limpiar</a>
            <input type="hidden" name="shopperId" value="" class="js-shopper-id" />
            <input type="hidden" name="shopperDni" value="${model['shopperDni']!''}" class="js-shopper-dni" />
          </div>
        <#if model["showItemType"]!true>
          <div class="field">
            <label for="tipoItem">Tipo de item: </label>
            <select id="tipoItem" name="tipoItem">
              <option value="">Todos</option>
            <#list model["tiposItem"] as tipoItem>
              <option value="${tipoItem}" <#if tipoItem == model['tipoItem']!''>selected="true"</#if>>${tipoItem.description}</option>
            </#list>
            </select>
          </div>
        </#if>
          <div class="field">
            <label for="tipoPago">Tipo de Pago: </label>
            <select id="tipoPago" name="tipoPago">
              <option value="">Todos</option>
            <#list model["tiposPago"] as tipoPago>
              <option value="${tipoPago}" <#if tipoPago == model['tipoPago']!''>selected="true"</#if>>${tipoPago.description}</option>
            </#list>
            </select>
          </div>
          <div class="field">
            <label for="from">Desde: </label>
            <input type="text" id="from" name="from" value="${(model['from']?string('dd/MM/yyyy'))!''}" class="js-date js-date-from" />
            <label for="to">Hasta: </label>
            <input type="text" id="to" name="to" value="${(model['to']?string('dd/MM/yyyy'))!''}" class="js-date js-date-to" />
          </div>
        </div>
        <ul class="action-columns">
          <li><input type="submit" value="Buscar" class="btn-shop-small"></li>
        </ul>
      </form>
      <table summary="Lista de adicionales" class="table-form js-results">
        <thead>
          <tr>
            <th scope="col">Shopper</th>
            <th scope="col"><a id="order-clientDescription" href="#" class="js-order <#if model['orderBy'] == 'clientDescription'>selected</#if>">Cliente <i class="fa <#if model['ascending']>fa-angle-up<#else>fa-angle-down</#if>"></i></a></th>
            <th scope="col">Sucursal</th>
            <th scope="col"><a id="order-tipoPago" href="#" class="js-order <#if model['orderBy'] == 'tipoPago'>selected</#if>">Pago <i class="fa <#if model['ascending']>fa-angle-up<#else>fa-angle-down</#if>"></i></a></th>
            <th scope="col">Importe</th>
            <th scope="col"><a id="order-fecha" href="#" class="js-order <#if model['orderBy'] == 'fecha'>selected</#if>">F. Visita <i class="fa <#if model['ascending']>fa-angle-up<#else>fa-angle-down</#if>"></i></a></th>
            <th scope="col">Autoriza</th>
            <th scope="col">Nro OP</th>
          </tr>
        </thead>
        <tbody>
          <#assign resultSet = model["result"] />
          <#list resultSet.items as item>
          <tr>
            <td>${(item.shopperDni)!''} <a href="view/${item.id?c}">editar</a></td>
            <td>${(item.client.name)!(item.clientDescription)!''}</td>
            <td>${(item.branch.address)!(item.branchDescription)!''}</td>
            <td>${item.tipoPago.description}</td>
            <td>$ ${item.importe?string["0.##"]?replace(',', '.')}</td>
            <td>${item.fecha?string('dd/MM/yyyy')}</td>
            <td>${item.usuario!''}</td>
            <td>${(item.nroOperacion?c)!''}</td>
          </tr>
          </#list>
        </tbody>
      </table>

      <div class="paginator">
        <#assign parameters = "shopperDni=${model['shopperDni']!''}&tipoItem=${model['tipoItem']!''}&tipoPago=${model['tipoPago']!''}&from=${model['from']?string('dd/MM/yyyy')!''}&to=${model['to']?string('dd/MM/yyyy')!''}" />

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