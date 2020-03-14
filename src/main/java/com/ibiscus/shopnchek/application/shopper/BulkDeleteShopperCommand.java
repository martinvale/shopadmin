package com.ibiscus.shopnchek.application.shopper;

import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public class BulkDeleteShopperCommand {

	private ShopperRepository shopperRepository;

    BulkDeleteShopperCommand() {
    }

	@Transactional
	public Boolean execute(List<String> shopperIds) {
    	for (String shopperId : shopperIds) {
    		Shopper shopper = shopperRepository.findByLogin(shopperId);
    		if (shopper != null) {
				shopper.disable();
			}
		}
		return Boolean.TRUE;
	}

    public void setShopperRepository(ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }
}
