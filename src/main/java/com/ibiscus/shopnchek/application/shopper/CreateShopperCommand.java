package com.ibiscus.shopnchek.application.shopper;

import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.Shopper.GENDER;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;
import org.springframework.transaction.annotation.Transactional;

public class CreateShopperCommand {

	private ShopperRepository shopperRepository;

    CreateShopperCommand() {
    }

	@Transactional
	public Shopper execute(NewShopper newShopper) {
		Shopper shopper = new Shopper(newShopper.getIdentityType(), newShopper.getIdentityId(), newShopper.getSurname(),
				newShopper.getFirstName(), newShopper.getAddress(), newShopper.getNeighborhood(), newShopper.getRegion(),
				newShopper.getState(), newShopper.getCountry(), newShopper.getPostalCode(), newShopper.getWorkPhone(),
				newShopper.getParticularPhone(), newShopper.getCellPhone(), newShopper.getEmail(), newShopper.getEmail2(),
				newShopper.getCobraSf(), newShopper.getReferrer(), newShopper.getObservations(), newShopper.getBirthDate(),
				GENDER.valueOf(newShopper.getGender()), newShopper.getEducation(), newShopper.getConfidentiality(),
				newShopper.getLoginShopmetrics(), newShopper.getEnabled());
		shopperRepository.save(shopper);
		return shopper;
	}

    public void setShopperRepository(ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }
}
