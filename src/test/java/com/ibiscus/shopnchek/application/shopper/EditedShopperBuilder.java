package com.ibiscus.shopnchek.application.shopper;

import java.util.Date;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;
import static org.apache.commons.lang.math.RandomUtils.nextLong;

public class EditedShopperBuilder extends NewShopperBuilder {

    private Long id = nextLong();

    public EditedShopperBuilder withId(Long value) {
        this.id = value;
        return this;
    }

    public EditedShopper build() {
        EditedShopper newShopper = new EditedShopper();
        newShopper.setId(id);
        newShopper.setIdentityType(identityType);
        newShopper.setIdentityId(identityId);
        newShopper.setSurname(surname);
        newShopper.setFirstName(firstName);
        newShopper.setAddress(address);
        newShopper.setNeighborhood(neighborhood);
        newShopper.setRegion(region);
        newShopper.setState(state);
        newShopper.setCountry(country);
        newShopper.setPostalCode(postalCode);
        newShopper.setWorkPhone(workPhone);
        newShopper.setParticularPhone(particularPhone);
        newShopper.setCellPhone(cellPhone);
        newShopper.setEmail(email);
        newShopper.setEmail2(email2);
        newShopper.setCobraSf(cobraSf);
        newShopper.setReferrer(referrer);
        newShopper.setObservations(observations);
        newShopper.setBirthDate(birthDate);
        newShopper.setGender(gender);
        newShopper.setEducation(education);
        newShopper.setConfidentiality(confidentiality);
        newShopper.setLoginShopmetrics(loginShopmetrics);
        newShopper.setEnabled(enabled);
        return newShopper;
    }

    public static EditedShopperBuilder newBuilder() {
        return new EditedShopperBuilder();
    }
}
