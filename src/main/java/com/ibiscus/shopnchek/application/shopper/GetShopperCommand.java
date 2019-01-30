package com.ibiscus.shopnchek.application.shopper;

import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import org.springframework.transaction.annotation.Transactional;

public class GetShopperCommand {

	private ShopperRepository shopperRepository;

    GetShopperCommand() {
    }

	@Transactional(readOnly = true)
	public Shopper execute(Long id) {
		return shopperRepository.get(id);
	}

    public void setShopperRepository(ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }
}
