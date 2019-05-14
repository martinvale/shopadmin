package com.ibiscus.shopnchek.application.order;

import java.util.Collections;
import java.util.Comparator;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.ItemOrden;
import com.ibiscus.shopnchek.domain.admin.OrdenPago;
import com.ibiscus.shopnchek.domain.admin.OrderRepository;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

public class GetOrderCommand implements Command<OrdenPago> {

	private OrderRepository orderRepository;

	private ShopperRepository shopperRepository;

	private long numero;

	@Override
	@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
	public OrdenPago execute() {
		OrdenPago order = orderRepository.get(numero);
		if (order != null) {
			for (ItemOrden itemOrden : order.getItems()) {
				Shopper itemShopper = shopperRepository.findByDni(itemOrden.getShopperDni());
				itemOrden.updateShopper(itemShopper);
			}
			Collections.sort(order.getItems(), new Comparator<ItemOrden>() {
	
				@Override
				public int compare(ItemOrden o1, ItemOrden o2) {
					int result = 0;
					if (o1.getShopper() != null && o2.getShopper() != null) {
						result = o1.getShopper().getName().compareTo(o2.getShopper().getName());
						if (result == 0 && o1.getCliente() != null && o2.getCliente() != null) {
							result = o1.getCliente().compareTo(o2.getCliente());
							if (result == 0 && o1.getSucursal() != null) {
								result = o1.getSucursal().compareTo(o2.getSucursal());
							}
						}
					}
					return result;
				}
			});
		}

		return order;
	}

	public void setOrderRepository(final OrderRepository orderRepository) {
		this.orderRepository = orderRepository;
	}

	public void setShopperRepository(final ShopperRepository shopperRepository) {
		this.shopperRepository = shopperRepository;
	}

	public void setNumero(final long numero) {
		this.numero = numero;
	}

}
