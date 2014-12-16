    <#assign user = model["user"] />
    <header>
      <div class="header-box">
        <h1>Shopnchek<span class="tag-intranet">intranet</span></h1>
        <nav>
          <ul class="menu">
            <li><a href="#"><i class="icon-home"></i>Orden de pago</a>
              <ul class="sub-menu">
                <li><a href="../orden/">Nueva</a></li>
                <li><a href="../orden/search">Buscar</a></li>
                <li><a href="../item/debtSearch">Deuda por shopper</a></li>
                <li><a href="../orden/export">Exportar &oacute;rdenes de pago</a></li>
              </ul>
            </li>
            <li><a href="#"><i class="icon-user"></i>Items de orden de pago</a>
              <ul class="sub-menu">
                <#assign puedeAutorizarAdicional = true />
                <#list model["user"].authorities as role>
                  <#assign puedeAutorizarAdicional = puedeAutorizarAdicional || (role.authority == 'ADITIONAL') />
                </#list>
                <#if puedeAutorizarAdicional>
                  <li><a href="../adicional/autorizacion">Autorizar adicional</a></li>
                  <li><a href="../adicional/search">Buscar adicional</a></li>
                </#if>
              </ul>
            </li>
            <li><a href="#"><i class="icon-home"></i>Reportes</a>
              <ul class="sub-menu">
                <li><a href="../report/debtSummary">Resumen de deuda</a></li>
                <li><a href="../report/paySummary">Resumen de pagos</a></li>
                <li><a href="../report/prodSummary">Resumen de producci&oacute;n</a></li>
              </ul>
            </li>
            <li><a href="#"><i class="icon-home"></i>Importaci&oacute;n</a>
              <ul class="sub-menu">
                <li><a href="../import/shopmetrics">Shopmetrics</a></li>
              </ul>
            </li>
            <li><a href="#"><i class="icon-user"></i>Administraci&oacute;n</a>
              <ul class="sub-menu">
                <li><a href="../users/">Usuarios</a></li>
                <li><a href="../proveedores/">Proveedores</a></li>
              </ul>
            </li>
          </ul>
        </nav>
        <p class="user"> ${user.username} <a href="../j_spring_security_logout">Salir</a></p>
      </div>
    </header>
