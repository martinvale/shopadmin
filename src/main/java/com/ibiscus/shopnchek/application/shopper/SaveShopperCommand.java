package com.ibiscus.shopnchek.application.shopper;

import org.springframework.transaction.annotation.Transactional;

import com.ibiscus.shopnchek.domain.admin.Shopper;
import com.ibiscus.shopnchek.domain.admin.ShopperRepository;

public class SaveShopperCommand {

	private ShopperRepository shopperRepository;

	SaveShopperCommand() {
	}

	@Transactional
	public Shopper execute(EditedShopper editedShopper) {
		Shopper shopper = shopperRepository.get(editedShopper.getId());
		shopper.update(editedShopper.getIdentityType(), editedShopper.getIdentityId(), editedShopper.getSurname(),
				editedShopper.getFirstName(), editedShopper.getAddress(), editedShopper.getNeighborhood(), editedShopper.getRegion(),
				editedShopper.getState(), editedShopper.getCountry(), editedShopper.getPostalCode(), editedShopper.getWorkPhone(),
				editedShopper.getParticularPhone(), editedShopper.getCellPhone(), editedShopper.getEmail(), editedShopper.getEmail2(),
				editedShopper.getCobraSf(), editedShopper.getReferrer(), editedShopper.getObservations(), editedShopper.getBirthDate(),
				Shopper.GENDER.valueOf(editedShopper.getGender()), editedShopper.getEducation(), editedShopper.getConfidentiality(),
				editedShopper.getLoginShopmetrics(), editedShopper.getEnabled());

		return shopper;
	}

    public void setShopperRepository(ShopperRepository shopperRepository) {
        this.shopperRepository = shopperRepository;
    }
}
