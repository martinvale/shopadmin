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

    <script src="../script/spin.js"></script>
    <script src="../script/jquery.spin.js"></script>

    <script src="../script/pure.min.js"></script>
    <script src="../script/livevalidation.js"></script>

    <#assign canEdit = false />
    <#list model["user"].authorities as role>
      <#assign canEdit = canEdit || (role.authority == 'ADMIN') />
    </#list>
    <#assign ordenAbierta = true />
    <#assign estadoOrden = "Abierta" />

    <#assign tipoFactura = "" />
    <#assign medioPagoId = "" />
    <#assign state = "" />
    <#assign iva = "" />
    <#assign tipoTitular = 1 />
    <#assign titularId = "" />
    <#assign facturaNumero = "" />
    <#assign fechaCheque = "" />
    <#assign localidad = "" />
    <#assign numeroChequera = "" />
    <#assign numeroCheque = "" />
    <#assign transferId = "" />
    <#assign observaciones = "" />
    <#assign observacionesShopper = "" />
    <#assign totalHonorarios = 0 />
    <#assign totalReintegros = 0 />
    <#assign totalOtrosGastos = 0 />
    <#assign ivaHonorarios = 0 />
    <#assign totalHonorariosConIva = 0 />
    <#assign total = 0 />
    <#if model["ordenPago"]??>
      <#assign numero = "${model['ordenPago'].numero?c}" />
      <#assign tipoFactura = "${model['ordenPago'].tipoFactura}" />
      <#assign fechaPago = "${model['ordenPago'].fechaPago?string('dd/MM/yyyy')}" />
      <#if model['ordenPago'].medioPago??>
        <#assign medioPagoId = "${model['ordenPago'].medioPago.id?c}" />
      </#if>
      <#assign state = "${model['ordenPago'].estado.id?c}" />
      <#assign ordenAbierta = model['ordenPago'].estado.description == 'Abierta' />
      <#assign estadoOrden = "${model['ordenPago'].estado.description}" />
      <#assign iva = "${model['ordenPago'].iva?string['0.##']}" />
      <#assign tipoTitular = model['ordenPago'].tipoProveedor />
      <#assign titularId = "${model['ordenPago'].proveedor?c}" />
      <#assign facturaNumero = "${model['ordenPago'].numeroFactura!''}" />
      <#if model['ordenPago'].fechaCheque??>
        <#assign fechaCheque = "${model['ordenPago'].fechaCheque?string('dd/MM/yyyy')}" />
      </#if>
      <#assign localidad = "${model['ordenPago'].localidad}" />
      <#assign numeroChequera = "${model['ordenPago'].numeroChequera!''}" />
      <#assign numeroCheque = "${model['ordenPago'].numeroCheque!''}" />
      <#assign transferId = "${model['ordenPago'].idTransferencia!''}" />
      <#assign observaciones = "${model['ordenPago'].observaciones!''}" />
      <#assign observacionesShopper = "${model['ordenPago'].observacionesShopper!''}" />
      <#assign totalHonorarios = model['ordenPago'].honorarios />
      <#assign totalReintegros = model['ordenPago'].reintegros />
      <#assign totalOtrosGastos = model['ordenPago'].otrosGastos />
      <#assign ivaHonorarios = (totalHonorarios * (model['ordenPago'].iva / 100)) />
      <#assign totalHonorariosConIva = (totalHonorarios + ivaHonorarios) />
      <#assign total = (totalReintegros + totalOtrosGastos + totalHonorariosConIva) />
    </#if>

    <script type="text/javascript">

      window.App = window.App || {};

      App.widget = App.widget || {};

      jQuery(document).ready(function() {

        var items = [
          <#if model["ordenPago"]??>
            <#list model["ordenPago"].items as item>
            {
              id: ${item.id?c},
              <#if item.shopper??>
                <#assign shopperDescription = "${item.shopper.name}">
              </#if>
              shopper: "${(shopperDescription?json_string)!'No encontrado'}",
              cliente: "${(item.cliente?json_string)!''}",
              sucursal: "${(item.sucursal?json_string)!''}",
              tipoPago: "${item.tipoPago.description?substring(0, 1)}",
              importe: ${item.importe?c},
              fecha: "${item.fecha!''}"
            }<#if item_has_next>,</#if>
            </#list>
          </#if>
        ];

        var canEdit = <#if canEdit && ordenAbierta>true<#else>false</#if>;
        var ordenPago = new App.widget.OrdenPago(jQuery(".js-orden-pago"),
            ${numero!"null"}, canEdit, items);
        ordenPago.render();
      });
    </script>

    <script src="../script/TitularSelector.js"></script>
    <script src="../script/ShopperSelector.js"></script>
    <script src="../script/ItemSelector.js"></script>
    <script src="../script/DeudaShopperSelector.js"></script>
    <script src="../script/OrdenPago.js"></script>
    <script src="../script/LoadingIndicator.js"></script>

<style>
.LV_validation_message.LV_valid {
  display: none;
}

.LV_invalid {
  color:#CC0000;
}
    
.LV_valid_field,
input.LV_valid_field:hover, 
input.LV_valid_field:active,
textarea.LV_valid_field:hover, 
textarea.LV_valid_field:active {
  border: 1px solid #00CC00;
}
    
.LV_invalid_field, 
input.LV_invalid_field:hover, 
input.LV_invalid_field:active,
textarea.LV_invalid_field:hover, 
textarea.LV_invalid_field:active {
  border: 1px solid #CC0000;
}
</style>
  </head>
  <body>
    <#include "header.ftl" />

    <div class="container-box-plantilla">
        <h2 class="container-tit">Orden de pago</h2>
        <#assign action = "create" />
        <#if model["ordenPago"]??>
          <#assign action = "save" />
        </#if>
        <form action="${action}" method="POST" class="form-shop form-shop-big js-orden-pago">
          <!-- FILA 1 -->
          <div class="cell">
            <div class="box-green cell-c1">
              <div class="form-shop-row-left">
                <label for="number">N&uacute;mero</label>
                <input type="text" name="numeroOrden" readOnly=true id="number" value="${numero!''}"/>
              </div>
            </div>
            <div class="box-green cell-c2">
              <div class="form-shop-options-left js-titular-selector">
                <p class="mandatory">Titular</p>
                <ul>
                  <li class="form-shop">
                    <input type="radio" name="tipoTitular" id="tipoShopper" value="1" class="js-shopper" <#if tipoTitular == 1>checked="checked"</#if>>
                    <label for="tipoShopper">Shopper</label>
                  </li>
                  <li class="form-shop">
                    <input type="radio" name="tipoTitular" id="tipoProveedor" value="2" class="js-proveedor" <#if tipoTitular == 2>checked="checked"</#if>>
                    <label for="tipoProveedor">Proveedor</label>
                  </li>
                </ul>
                <div class="combo-titular">
                  <#assign titularNombre = "${model['titularNombre']!''}" />
                  <input type="text" value="${titularNombre}" class="js-titulares" />
                  <input type="hidden" name="titularId" value="${titularId}" class="js-titular-id" />
                </div>
              </div>
            </div>
          </div>
          <!-- FIN FILA 1 -->
          <!-- FIN FILA 2 -->
          <ul class="columnas-form">
            <li>
              <div class="form-shop-row">
                <label for="tipoFactura" class="mandatory">Factura</label>
                <select id="tipoFactura" name="tipoFactura">
                  <option value="Seleccionar">Seleccionar</option>
                  <option value="S/F" <#if tipoFactura == 'S/F'>selected="selected"</#if>>S/F</option>
                  <option value="A" <#if tipoFactura == 'A'>selected="selected"</#if>>A</option>
                  <option value="C" <#if tipoFactura == 'C'>selected="selected"</#if>>C</option>
                  <option value="M" <#if tipoFactura == 'M'>selected="selected"</#if>>M</option>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="fechaPago" class="mandatory">Fecha pago</label>
                <input type="text" id="fechaPago" name="fechaPago" class="js-date" value="${fechaPago!''}" <#if !canEdit>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row">
                <label for="medioPago" class="mandatory">M. de pago</label>
                <select id="medioPago" name="medioPagoId" class="js-medio-pago">
                  <option value="Seleccionar">Seleccionar</option>
                  <#list model["mediosPago"] as medioPago>
                    <option value="${medioPago.id}" <#if medioPago.id?c == medioPagoId>selected="selected"</#if>>${medioPago.description}</option>
                  </#list>
                </select>
                <div class="js-medio-pago-asociado">
                  <#if model["medioPagoPredeterminado"]??>
                    <span class="medio-pago js-medio-pago-predeterminado">Medio de pago predeterminado: ${model["medioPagoPredeterminado"]}</span>
                    <a href="#" class="asociar-medio js-asociar-medio" style="display:none">Asociar medio de pago al titular</a>
                  <#else>
                    <span class="medio-pago js-medio-pago-predeterminado" style="display:none">Medio de pago predeterminado: </span>
                    <#if medioPagoId != "">
                      <a href="#" class="asociar-medio js-asociar-medio">Asociar medio de pago al titular</a>
                    <#else>
                      <span class="sin-medio-pago js-sin-medio-pago">(El titular no tiene un medio de pago asociado)</span>
                      <a href="#" class="asociar-medio js-asociar-medio" style="display:none">Asociar medio de pago al titular</a>
                    </#if>
                  </#if>
                </div>
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="numeroFactura">Factura N&deg;</label>
                <input type="number" name="numeroFactura" id="numeroFactura" value="${facturaNumero}"/>
              </div>
              <div class="form-shop-row">
                <label for="state" class="mandatory">Estado</label>
                <select id="state" name="estadoId" <#if !canEdit>disabled="true"</#if>>
                  <#list model["orderStates"] as orderState>
                    <option value="${orderState.id}" <#if orderState.id?c == state>selected="selected"</#if>>${orderState.description}</option>
                  </#list>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="numeroChequera">Chequera N&deg;</label>
                <input type="number" name="numeroChequera" id="numeroChequera" value="${numeroChequera}" <#if !canEdit || estadoOrden != 'Abierta'>disabled="true"</#if>/>
              </div>
              <div class="form-shop-row">
                <label for="transferId">ID Transfer</label>
                <input type="number" name="transferId" id="transferId" value="${transferId}" <#if !canEdit || estadoOrden != 'Abierta'>disabled="true"</#if>/>
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="iva" class="mandatory">IVA %</label>
                <input type="text" name="iva" id="iva" value="${iva}"/>
              </div>
              <div class="form-shop-row">
                <label for="localidad">Localidad</label>
                <select id="localidad" name="localidad">
                  <option value="">Seleccionar</option>
                  <option value="Buenos Aires" <#if localidad == 'Buenos Aires'>selected="selected"</#if>>Buenos Aires</option>
                  <option value="Interior" <#if localidad == 'Interior'>selected="selected"</#if>>Interior</option>
                </select>
              </div>
              <div class="form-shop-row">
                <label for="numeroCheque">Cheque N&deg;</label>
                <input type="number" name="numeroCheque" id="numeroCheque" value="${numeroCheque}" <#if !canEdit || estadoOrden != 'Abierta'>disabled="true"</#if>/>
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="fechaCheque">Fecha cheque</label>
                <input type="text" id="fechaCheque" name="fechaCheque" class="js-date" value="${fechaCheque}" <#if !canEdit || estadoOrden != 'Abierta'>disabled="true"</#if>/>
              </div>
            </li>
          </ul>
          <p class="observacion">
            <label for="observaciones">Observaciones</label>
            <textarea id="observaciones" name="observaciones">${observaciones}</textarea>
          </p>
          <p class="observacion">
            <label for="observacionesShopper">Obs. p/shopper</label>
            <textarea id="observacionesShopper" name="observacionesShopper">${observacionesShopper}</textarea>
          </p>
          <ul class="action-columns">
            <li> <input type="submit" class="btn-shop-small" value="Guardar" <#if !canEdit>disabled="true"</#if>></li>
            <li> <input type="button" class="btn-shop-small js-caratula" value="Car&aacute;tula" <#if !model["ordenPago"]??>disabled="true"</#if>></li>
          </ul>
          <!-- FIN FILA 2 -->

          <!-- FILA 3 -->
          <h2 class="subtitulo">Items</h2>
          <div class="items-container">
            <table summary="Listado de items de la orden de pago" class="table-form js-table-items">
              <thead>
                <tr>
                  <th scope="col" style="width:25%"><a id="order-shopper" href="#" class="js-order">Shopper <i class="fa fa-angle-down"></i></a></th>
                  <th scope="col" style="width:25%"><a id="order-cliente" href="#" class="js-order">Cliente <i class="fa fa-angle-down"></i></a></th>
                  <th scope="col" style="width:26%"><a id="order-sucursal" href="#" class="js-order">Sucursal <i class="fa fa-angle-down"></i></a></th>
                  <th scope="col" style="width:4%"><a id="order-tipoPago" href="#" class="js-order">Pago <i class="fa fa-angle-down"></i></a></th>
                  <th scope="col" style="width:12%"><a id="order-importe" href="#" class="js-order">Importe <i class="fa fa-angle-down"></i></a></th>
                  <th scope="col" style="width:8%"><a id="order-fecha" href="#" class="js-order">Fecha <i class="fa fa-angle-down"></i></a></th>
                </tr>
              </thead>
              <tbody>
                <#if model["ordenPago"]??>
                  <#list model["ordenPago"].items as item>
                  <tr id="js-item-${item.id?c}">
                    <#assign shopperDescription = "" />
                    <#if item.shopper??>
                      <#assign shopperDescription = "${item.shopper.name}">
                    </#if>
                    <#if shopperDescription?length &gt; 25>
                      <#assign shopperDescription = "${shopperDescription?substring(0, 25)}...">
                    </#if>
                    <td>${shopperDescription} <#if canEdit && ordenAbierta><a id="item-${item.id?c}" href="#" class="js-delete-item">borrar</a></#if></td>
                    <td class="js-cliente">${item.cliente!''}</td>
                    <td class="js-sucursal">${item.sucursal!''}</td>
                    <td>${item.tipoPago.description?substring(0, 1)}</td>
                    <td class="js-importe" style="text-align: right;">${item.importe?string.currency} <#if canEdit && ordenAbierta><a class="js-item-value-${item.id?c}" href="#">editar</a></#if></td>
                    <td class="js-fecha">${item.fecha!''}</td>
                  </tr>
                  </#list>
                </#if>
              </tbody>
            </table>
          </div>
          <ul class="action-columns">
            <!--li><input type="button" class="btn-shop-small js-add-item" value="Agregar" <#if !model["ordenPago"]?? || !canEdit>disabled="true"</#if>></li-->
            <li><input type="button" class="btn-shop-small js-buscar-deuda" value="Deuda Shopper" <#if !model["ordenPago"]?? || !canEdit || estadoOrden != 'Abierta'>disabled="true"</#if>></li>
          </ul>

          <!-- FIN FILA 3 -->
          <!-- FILA 4 -->
          <h2 class="subtitulo"> Totales </h2>
          <ul class="columnas-form col-three">
            <li>
              <div class="form-shop-row">
                <label for="total-honorarios">Subt. honorarios ($)</label>
                <input type="text" id="total-honorarios"
                    class="js-total-honorarios"
                    value="${totalHonorarios?string['0.##']?replace('.', ',')}">
              </div>
              <div class="form-shop-row">
                <label for="total-reintegros">Subt. reintegros ($)</label>
                <input type="text" id="total-reintegros"
                    class="js-total-reintegros"
                    value="${totalReintegros?string['0.##']?replace('.', ',')}">
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="total-iva">IVA ($)</label>
                 <input type="text" id="total-iva" class="js-total-iva"
                    value="${ivaHonorarios?string['0.##']?replace('.', ',')}">
              </div>
              <div class="form-shop-row">
                <label for="total-otros-gastos">Subt. otros gastos ($)</label>
                <input type="text" id="total-otros-gastos"
                    class="js-total-otros-gastos"
                    value="${totalOtrosGastos?string['0.##']?replace('.', ',')}">
              </div>
            </li>
            <li>
              <div class="form-shop-row">
                <label for="total-honorarios-con-iva">Honorarios c/IVA ($)</label>
                <input type="text" id="total-honorarios-con-iva" 
                    class="js-total-honorarios-con-iva"
                    value="${totalHonorariosConIva?string['0.##']?replace('.', ',')}">
              </div>
              <div class="form-shop-row">
                <label for="total">Total general ($)</label>
                <input type="text" id="total" class="js-total"
                    value="${total?string['0.##']?replace('.', ',')}">
              </div>
            </li>
          </ul>

        <div class="actions-form">
          <ul class="action-columns">
            <li><input type="button" class="btn-shop js-remito" value="Imprimir" <#if !model["ordenPago"]?? || !canEdit>disabled="true"</#if>></li>
            <li><input type="button" class="btn-shop js-detail" value="Imprimir Detalle" <#if !model["ordenPago"]?? || !canEdit>disabled="true"</#if>></li>
            <li><input type="button" class="btn-shop js-detail-shopper" value="Detalle Shopper" <#if !model["ordenPago"]??>disabled="true"</#if>></li>
          </ul>
        </div>
        <div style="display:none">
          <div class="js-edit-importe" title="Modificar el importe">
            <label for="importe-item">Ingrese el importe de este item de la factura</label>
            <input id="importe-item" type="text" value="" class="js-importe-item" />
          </div>
        </div>
      </form>
    </div>
    <div id="item-selector" style="display:none;">
        <div class="container-box-plantilla">
            <h2 class="container-tit">Items de la orden de pago</h2>
            <form class="">
              <!--div role="alert" class="form-error-txt" aria-hidden="false"><i class="ch-icon-remove"></i>
                <div class="ch-popover-content">
                    Revisa los datos. Debes completar campos "Número" y "Factura".
                </div>
              </div-->
            <!-- FILA 1 -->
                <div class="cell">
                    <div class="box-green">
                      <div class="form-shop-row-left shopper-widget js-shopper-selector">
                        <label for="shopper">Shooper: </label>
                        <input type="text" value="" id="shopper" class="js-shopper" />
                        <a id="clear-shopper" href="#" class="clear js-clear">limpiar</a>
                        <input type="hidden" name="shopperId" value="" class="js-shopper-id" />
                        <input type="hidden" name="shopperDni" value="" class="js-shopper-dni" />
                      </div>
                    </div>
                    <div class="tabs_holder js-tabs">
                        <ul>
                            <!--li><a href="#your-tab-id-1">I Plan </a></li-->
                            <li><a href="#your-tab-id-2">MCD</a></li>
                            <!--li><a href="#your-tab-id-3">GAC</a></li-->
                            <li><a href="#your-tab-id-4">Adicionales</a></li>
                            <!--li><a href="#your-tab-id-5">Manuales</a></li-->
                        </ul>
                        <div class="content_holder">
                          <div id="your-tab-id-2" class="js-mcd-items">
                            <ul class="action-columns">
                                <li><input type="button" class="btn-shop-small js-buscar" value="Buscar"></li>
                            </ul>
                            <table summary="Lista de items" class="table-form ">
                              <thead>
                                <tr>
                                  <th scope="col">Programa</th>
                                  <th scope="col">Local</th>
                                  <th scope="col">Fecha</th>
                                  <th scope="col">Pago</th>
                                  <th scope="col">Importe</th>
                                  <th scope="col">Fecha Cobro</th>
                                </tr>
                              </thead>
                              <tbody class="items">
                                <tr>
                                  <td class="programa"></td>
                                  <td class="local"></td>
                                  <td class="fecha"></td>
                                  <td class="descripcion"></td>
                                  <td class="importe"></td>
                                  <td class="fechaCobro"></td>
                                </tr>
                              </tbody>
                            </table>
                          </div>
                          <div id="your-tab-id-4" class="js-items-adicionales">
                            <ul class="action-columns">
                              <li><input type="button" class="btn-shop-small js-buscar" value="Buscar"></li>
                            </ul>
                            <table summary="Lista de items" class="table-form ">
                              <thead>
                                <tr>
                                  <th scope="col">Pago</th>
                                  <th scope="col">Cliente</th>
                                  <th scope="col">Sucursal</th>
                                  <th scope="col">Importe</th>
                                  <th scope="col">Fecha</th>
                                  <th scope="col">Observaciones</th>
                                </tr>
                              </thead>
                              <tbody class="items">
                                <tr>
                                  <td class="pago"></td>
                                  <td class="cliente"></td>
                                  <td class="sucursal"></td>
                                  <td class="importe"></td>
                                  <td class="fecha"></td>
                                  <td class="observaciones"></td>
                                </tr>
                              </tbody>
                            </table>
                          </div>
                        </div>
                    </div>

      <!-- FIN FILA 3 -->
        </form>
        </div>

    </div>
    <#include "buscadorDeudaShopper.ftl" />
    <div id="ammount-confirmation" title="Importe">
      <form>
        <label for="importe-asignacion">Ingrese el importe de la asignaci&oacute;n seleccionada</label>
        <input id="importe-asignacion" type="text" value="" class="js-importe" />
        <input type="hidden" value="" class="js-index" />
        <input type="submit" tabindex="-1" style="position:absolute; top:-1000px" />
      </form>
    </div>
    <div id="confirm-delete-item" title="Borrar item de la orden de pago">
      <p><span class="ui-icon ui-icon-alert" style="float:left; margin:0 7px 20px 0;"></span>Esta seguro que desea borrar el item?</p>
    </div>
    <table id="items-table-template" style="display:none">
      <tr>
        <td class="js-shopper"></td>
        <td class="js-cliente"></td>
        <td class="js-sucursal"></td>
        <td class="js-tipo-pago"></td>
        <td class="js-importe" style="text-align: right;"></td>
        <td class="js-fecha"></td>
      </tr>
    </table>
  </body>
</html>