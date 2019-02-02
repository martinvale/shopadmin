package com.ibiscus.shopnchek.application.shopper;

import com.google.common.base.Objects;
import com.ibiscus.shopnchek.domain.admin.Shopper;
import org.apache.commons.lang.StringUtils;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/WEB-INF/applicationContext.xml")
public class CreateShopperCommandTest {

    @Autowired
    private CreateShopperCommand command;

    @Test
    public void execute() {
        NewShopper newShopper = getNewShopper();

        Shopper shopper = command.execute(newShopper);

        assertThat(shopper, isCreatedFrom(newShopper));
    }

    private NewShopper getNewShopper() {
        return new NewShopperBuilder()
            .build();
    }

    private Matcher<Shopper> isCreatedFrom(final NewShopper newShopper) {
        return new BaseMatcher<Shopper>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("is ").appendValue(newShopper);
            }

            @Override
            public boolean matches(Object item) {
                Shopper shopper = (Shopper) item;
                if (!StringUtils.equals(shopper.getIdentityType(), newShopper.getIdentityType())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getIdentityId(), newShopper.getIdentityId())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getSurname(), newShopper.getSurname())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getFirstName(), newShopper.getFirstName())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getAddress(), newShopper.getAddress())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getNeighborhood(), newShopper.getNeighborhood())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getRegion(), newShopper.getRegion())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getState(), newShopper.getState())) {
                    return false;
                }
                if (!Objects.equal(shopper.getCountry(), newShopper.getCountry())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getPostalCode(), newShopper.getPostalCode())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getWorkPhone(), newShopper.getWorkPhone())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getParticularPhone(), newShopper.getParticularPhone())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getCellPhone(), newShopper.getCellPhone())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getEmail(), newShopper.getEmail())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getEmail2(), newShopper.getEmail2())) {
                    return false;
                }
                if (!Objects.equal(shopper.getCobraSf(), newShopper.getCobraSf())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getReferrer(), newShopper.getReferrer())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getObservations(), newShopper.getObservations())) {
                    return false;
                }
                if (!Objects.equal(shopper.getBirthDate(), newShopper.getBirthDate())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getGender().name(), newShopper.getGender())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getEducation(), newShopper.getEducation())) {
                    return false;
                }
                if (!Objects.equal(shopper.getConfidentiality(), newShopper.getConfidentiality())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getLoginShopmetrics(), newShopper.getLoginShopmetrics())) {
                    return false;
                }
                if (!Objects.equal(shopper.getEnabled(), newShopper.getEnabled())) {
                    return false;
                }
                return true;
            }
        };
    }

}