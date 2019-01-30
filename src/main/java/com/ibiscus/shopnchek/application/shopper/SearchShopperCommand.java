package com.ibiscus.shopnchek.application.shopper;

import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class SearchShopperCommand {

	private ShopperRepository shopperRepository;

    SearchShopperCommand() {
    }

	@Transactional(readOnly = true)
	public List<Shopper> execute(String name) {
		return shopperRepository.find(name);
	}

    public void setShopperRepository(ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }
}
