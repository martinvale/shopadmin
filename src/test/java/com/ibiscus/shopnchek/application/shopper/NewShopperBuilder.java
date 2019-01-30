package com.ibiscus.shopnchek.application.shopper;

import java.util.Date;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang.math.RandomUtils.nextBoolean;
import static org.apache.commons.lang.math.RandomUtils.nextInt;

public class NewShopperBuilder {

    protected String identityType = randomAlphabetic(4);
    protected String identityId = randomNumeric(7);
    protected String surname = randomAlphabetic(10);
    protected String firstName = randomAlphabetic(10);
    protected String address = randomAlphabetic(15);
    protected String neighborhood = randomAlphabetic(15);
    protected String region = randomAlphabetic(10);
    protected String state = randomAlphabetic(10);
    protected Integer country = nextInt();
    protected String postalCode = randomNumeric(5);
    protected String workPhone = randomNumeric(8);
    protected String particularPhone = randomNumeric(8);
    protected String cellPhone = randomNumeric(8);
    protected String email = randomAlphabetic(10);
    protected String email2 = randomAlphabetic(10);
    protected Boolean cobraSf = nextBoolean();
    protected String referrer = randomAlphabetic(15);
    protected String observations = randomAlphabetic(50);
    protected Date birthDate = new Date();
    protected String gender = ((nextInt() % 2) == 0) ? "MALE" : "FEMALE";
    protected String education = randomAlphabetic(10);
    protected Boolean confidentiality = nextBoolean();
    protected String loginShopmetrics = randomAlphabetic(10);
    protected Boolean enabled = nextBoolean();

    public NewShopper build() {
        NewShopper newShopper = new NewShopper();
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

    public static NewShopperBuilder newBuilder() {
        return new NewShopperBuilder();
    }
}
