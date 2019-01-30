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

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/WEB-INF/applicationContext.xml")
public class SaveShopperCommandTest {

    @Autowired
    private CreateShopperCommand createShopperCommand;

    @Autowired
    private SaveShopperCommand saveShopperCommand;

    @Test
    public void execute() {
        NewShopper newShopper = NewShopperBuilder.newBuilder().build();

        Shopper shopper = createShopperCommand.execute(newShopper);

        EditedShopper editedShopper = EditedShopperBuilder.newBuilder()
                .withId(shopper.getId())
                .build();

        shopper = saveShopperCommand.execute(editedShopper);
        assertThat(shopper, isEditedFrom(editedShopper));
    }

    private Matcher<Shopper> isEditedFrom(final EditedShopper editedShopper) {
        return new BaseMatcher<Shopper>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("is ").appendValue(editedShopper);
            }

            @Override
            public boolean matches(Object item) {
                Shopper shopper = (Shopper) item;
                if (!Objects.equal(shopper.getId(), editedShopper.getId())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getIdentityType(), editedShopper.getIdentityType())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getIdentityId(), editedShopper.getIdentityId())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getName(), editedShopper.getSurname())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getFirstName(), editedShopper.getFirstName())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getAddress(), editedShopper.getAddress())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getNeighborhood(), editedShopper.getNeighborhood())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getRegion(), editedShopper.getRegion())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getState(), editedShopper.getState())) {
                    return false;
                }
                if (!Objects.equal(shopper.getCountry(), editedShopper.getCountry())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getPostalCode(), editedShopper.getPostalCode())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getWorkPhone(), editedShopper.getWorkPhone())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getParticularPhone(), editedShopper.getParticularPhone())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getCellPhone(), editedShopper.getCellPhone())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getEmail(), editedShopper.getEmail())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getEmail2(), editedShopper.getEmail2())) {
                    return false;
                }
                if (!Objects.equal(shopper.getCobraSf(), editedShopper.getCobraSf())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getReferrer(), editedShopper.getReferrer())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getObservations(), editedShopper.getObservations())) {
                    return false;
                }
                if (!Objects.equal(shopper.getBirthDate(), editedShopper.getBirthDate())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getGender().name(), editedShopper.getGender())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getEducation(), editedShopper.getEducation())) {
                    return false;
                }
                if (!Objects.equal(shopper.getConfidentiality(), editedShopper.getConfidentiality())) {
                    return false;
                }
                if (!StringUtils.equals(shopper.getLoginShopmetrics(), editedShopper.getLoginShopmetrics())) {
                    return false;
                }
                if (!Objects.equal(shopper.getEnabled(), editedShopper.getEnabled())) {
                    return false;
                }
                return true;
            }
        };
    }

}