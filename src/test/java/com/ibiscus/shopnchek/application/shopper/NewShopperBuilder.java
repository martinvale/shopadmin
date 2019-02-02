package com.ibiscus.shopnchek.application.shopper;

import com.ibiscus.shopnchek.domain.util.Row;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;

import static org.apache.commons.lang.RandomStringUtils.randomAlphabetic;
import static org.apache.commons.lang.RandomStringUtils.randomNumeric;
import static org.apache.commons.lang.StringUtils.isNotEmpty;
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

    public NewShopperBuilder withIdentityType(String value) {
        this.identityType = value;
        return this;
    }

    public NewShopperBuilder withIdentityId(String value) {
        this.identityId = value;
        return this;
    }

    public NewShopperBuilder withSurname(String value) {
        this.surname = value;
        return this;
    }

    public NewShopperBuilder withFirstName(String value) {
        this.firstName = value;
        return this;
    }

    public NewShopperBuilder withAddress(String value) {
        this.address = value;
        return this;
    }

    public NewShopperBuilder withNeighborhood(String value) {
        this.neighborhood = value;
        return this;
    }

    public NewShopperBuilder withRegion(String value) {
        this.region = value;
        return this;
    }

    public NewShopperBuilder withState(String value) {
        this.state = value;
        return this;
    }

    public NewShopperBuilder withCountry(Integer value) {
        this.country = value;
        return this;
    }

    public NewShopperBuilder withPostalCode(String value) {
        this.postalCode = value;
        return this;
    }

    public NewShopperBuilder withWorkPhone(String value) {
        this.workPhone = value;
        return this;
    }

    public NewShopperBuilder withParticularPhone(String value) {
        this.particularPhone = value;
        return this;
    }

    public NewShopperBuilder withCellPhone(String value) {
        this.cellPhone = value;
        return this;
    }

    public NewShopperBuilder withEmail(String value) {
        this.email = value;
        return this;
    }

    public NewShopperBuilder withEmail2(String value) {
        this.email2 = value;
        return this;
    }

    public NewShopperBuilder withCobraSf(Boolean value) {
        this.cobraSf = value;
        return this;
    }

    public NewShopperBuilder withReferrer(String value) {
        this.referrer = value;
        return this;
    }

    public NewShopperBuilder withObservations(String value) {
        this.observations = value;
        return this;
    }

    public NewShopperBuilder withBirthDate(Date value) {
        this.birthDate = value;
        return this;
    }

    public NewShopperBuilder withGender(String value) {
        this.gender = value;
        return this;
    }

    public NewShopperBuilder withEducation(String value) {
        this.education = value;
        return this;
    }

    public NewShopperBuilder withConfidentiality(Boolean value) {
        this.confidentiality = value;
        return this;
    }

    public NewShopperBuilder withLoginShopmetrics(String value) {
        this.loginShopmetrics = value;
        return this;
    }

    public NewShopperBuilder withEnabled(Boolean value) {
        this.enabled = value;
        return this;
    }

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
