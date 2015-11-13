    <#import "/spring.ftl" as spring />
    <#assign user = model["user"] />
    <header>
      <div class="header-box">
        <h1>Shopnchek<span class="tag-intranet">intranet</span></h1>
        <nav>
          <ul class="menu">
          <#if model["user"].hasFeature('edit_order')>
            <li><a href="#"><i class="icon-home"></i>Orden de pago</a>
              <ul class="sub-menu">
                <li><a href="<@spring.url '/orden/create'/>">Nueva</a></li>
                <li><a href="<@spring.url '/orden/search'/>">Buscar</a></li>
                <li><a href="<@spring.url '/item/debtSearch'/>">Deuda por shopper</a></li>
                <li><a href="<@spring.url '/orden/export'/>">Exportar &oacute;rdenes de pago</a></li>
              </ul>
            </li>
            <li><a href="#"><i class="icon-user"></i>Items de orden de pago</a>
              <ul class="sub-menu">
                <li><a href="<@spring.url '/item/search'/>">Buscar items pagos</a></li>
              </ul>
            </li>
          </#if>
          <#if model["user"].hasFeature('create_aditional') || model["user"].hasFeature('list_aditional')>
            <li><a href="#"><i class="icon-user"></i>Deuda</a>
              <ul class="sub-menu">
              <#if model["user"].hasFeature('list_aditional')>
                <li><a href="<@spring.url '/debt/list'/>">Buscar deuda</a></li>
              </#if>
              <#if model["user"].hasFeature('create_aditional')>
                <li><a href="<@spring.url '/debt/create'/>">Crear adicional</a></li>
              </#if>
              </ul>
            </li>
          </#if>
          <#if model["user"].hasFeature('edit_order')>
            <li><a href="#"><i class="icon-home"></i>Reportes</a>
              <ul class="sub-menu">
                <li><a href="<@spring.url '/orden/'/>../report/debtSummary">Resumen de deuda</a></li>
                <!--li><a href="../report/debtSummary2">Resumen de deuda (ant)</a></li-->
                <li><a href="<@spring.url '/orden/'/>../report/paySummary">Resumen de pagos</a></li>
                <li><a href="<@spring.url '/orden/'/>../report/prodSummary">Resumen de producci&oacute;n</a></li>
                <!--li><a href="../report/prodSummary2">Resumen de producci&oacute;n (ant)</a></li-->
              </ul>
            </li>
            <li><a href="#"><i class="icon-home"></i>Importaci&oacute;n</a>
              <ul class="sub-menu">
                <li><a href="<@spring.url '/orden/'/>../import/shopmetrics">Shopmetrics</a></li>
              </ul>
            </li>
          </#if>
          <#if model["user"].hasFeature('manage')>
            <li><a href="#"><i class="icon-user"></i>Administraci&oacute;n</a>
              <ul class="sub-menu">
                <li><a href="<@spring.url '/users/list'/>">Usuarios</a></li>
                <li><a href="<@spring.url '/proveedores/'/>">Proveedores</a></li>
              </ul>
            </li>
          </#if>
          </ul>
        </nav>
        <p class="user"> ${user.username} <a href="<@spring.url '/j_spring_security_logout'/>">Salir</a></p>
      </div>
    </header>
