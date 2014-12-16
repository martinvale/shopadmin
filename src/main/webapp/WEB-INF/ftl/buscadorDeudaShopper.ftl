  <div id="deuda-shopper" style="display:none;">
    <div class="container-box-plantilla">
      <form>
        <div class="cell">
          <div class="box-green">
            <div class="form-shop-row-left shopper-widget js-shopper-selector">
              <label for="shopper">Shooper: </label>
              <input type="text" value="" id="shopper" class="shopper-name js-shopper" />
              <a id="clear-shopper" href="#" class="clear js-clear">limpiar</a>
              <input type="hidden" name="shopperId" value="" class="js-shopper-id" />
              <input type="hidden" name="shopperDni" value="" class="js-shopper-dni" />
            </div>
            <ul class="options">
              <li>
                <input type="checkbox" name="gac" id="gac" value="1" checked="checked">
                <label for="gac">GAC</label>
              </li>
              <li>
                <input type="checkbox" name="mcd" id="mcd" value="1" checked="checked">
                <label for="gac">MCD</label>
              </li>
              <li>
                <input type="checkbox" name="adicionales" id="adicionales" value="1" checked="checked">
                <label for="adicionales">ADICIONALES</label>
              </li>
              <li>
                <input type="checkbox" name="shopmetrics" id="shopmetrics" value="1" checked="checked">
                <label for="shopmetrics">SHOPMETRICS</label>
              </li>
            </ul>
            <ul class="date">
              <li class="check">
                <input type="checkbox" name="applyDate" id="applyDate" value="1" checked="checked">
                <label for="applyDate">Filtrar fecha</label>
              </li>
              <li>
                <label for="desde">Desde</label>
                <input type="text" name="desde" id="desde" class="js-date" value="01/01/2014">
              </li>
              <li>
                <label for="hasta">Hasta</label>
                <input type="text" name="hasta" id="hasta" class="js-date" value="01/01/2015">
              </li>
            </ul>
          </div>
          <ul class="action-columns">
            <li><input type="button" class="btn-shop-small js-buscar" value="Buscar"></li>
          </ul>
          <div class="items-container">
            <table summary="Lista de items" class="table-form js-items">
              <thead>
                <tr>
                  <th scope="col" style="width:25%"><a id="order-empresa" href="#" class="js-order">Empresa <i class="fa fa-angle-down"></i></a></th>
                  <th scope="col" style="width:25%"><a id="order-programa" href="#" class="js-order">Subcuestionario <i class="fa fa-angle-down"></i></a></th>
                  <th scope="col" style="width:26%"><a id="order-local" href="#" class="js-order">Local <i class="fa fa-angle-down"></i></a></th>
                  <th scope="col" style="width:8%"><a id="order-importe" href="#" class="js-order">Importe <i class="fa fa-angle-down"></i></a></th>
                  <th scope="col" style="width:8%"><a id="order-fecha" href="#" class="js-order">Fecha <i class="fa fa-angle-down"></i></a></th>
                  <th scope="col" style="width:8%"><a id="order-descripcion" href="#" class="js-order">Pago <i class="fa fa-angle-down"></i></a></th>
                </tr>
              </thead>
              <tbody class="items">
                <tr>
                  <td class="empresa"></td>
                  <td class="subcuestionario"></td>
                  <td class="local"></td>
                  <td class="importe"></td>
                  <td class="fecha"></td>
                  <td class="pago"></td>
                </tr>
              </tbody>
            </table>
          </div>
          <div class="summary">
            <label>Reintegros</label>
            <input type="text" class="js-reintegros" />
            <label>Honorarios</label>
            <input type="text" class="js-honorarios" />
            <label>Otros gastos</label>
            <input type="text" class="js-otros-gastos" />
          </div>
        </div>
      </form>
    </div>
    <div class="js-confirmation" title="Importe">
      <form>
        <label for="importe-asignacion">Ingrese el importe de la asignaci&oacute;n seleccionada</label>
        <input id="importe-asignacion" type="text" value="" class="js-importe" />
        <input type="hidden" value="" class="js-index" />
        <input type="submit" tabindex="-1" style="position:absolute; top:-1000px" />
      </form>
    </div>
  </div>