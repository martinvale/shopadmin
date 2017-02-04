package com.ibiscus.shopnchek.application.shopper;

import org.apache.commons.lang.Validate;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.application.Command;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

public class SaveShopperCommand implements Command<Shopper> {

	private ShopperRepository shopperRepository;

	private String shopperDni;

	private String login;

	public SaveShopperCommand() {
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Shopper execute() {
		Validate.notNull(shopperDni, "The shopper DNI cannot be null");
		Shopper shopper = shopperRepository.findByDni(shopperDni);
		shopper.update(login);
		return shopper;
	}

	public void setShopperRepository(final ShopperRepository shopperRepository) {
		this.shopperRepository = shopperRepository;
	}

	public void setShopperDni(final String shopperDni) {
		this.shopperDni = shopperDni;
	}

	public void setLogin(final String login) {
		this.login = login;
	}

}
