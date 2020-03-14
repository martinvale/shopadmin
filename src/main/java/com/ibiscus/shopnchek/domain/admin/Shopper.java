package com.ibiscus.shopnchek.domain.admin;

import javax.persistence.*;
import java.util.Date;

import static org.apache.commons.lang.StringUtils.isNotBlank;

@Entity
@Table(name="shoppers")
public class Shopper {

  public enum GENDER {
    MALE("M"), FEMALE("F");

    private final String code;

    GENDER(String code) {
      this.code = code;
    }

    public String getCode() {
      return code;
    }

    public static GENDER byCode(String value) {
      for (GENDER gender : values()) {
        if (gender.code.equals(value)) {
          return gender;
        }
      }
      throw new IllegalArgumentException(String.format("Cannot find a gender value for %s", value));
    }
  }

  @Id
  @Column(name = "id")
  @GeneratedValue
  private long id;

  @Column(name = "identity_type")
  private String identityType;

  @Column(name = "identity_id")
  private String identityId;

  @Column(name = "surname", length = 300)
  private String surname;

  @Column(name = "firstname", length = 300)
  private String firstName;

  @Column(name = "address", length = 300)
  private String address;

  @Column(name = "neighborhood", length = 100)
  private String neighborhood;

  @Column(name = "region", length = 200)
  private String region;

  @Column(name = "state", length = 200)
  private String state;

  @Column(name = "country")
  private Integer country;

  @Column(name = "postal_code", length = 50)
  private String postalCode;

  @Column(name = "work_phone", length = 50)
  private String workPhone;

  @Column(name = "particular_phone", length = 50)
  private String particularPhone;

  @Column(name = "cell_phone", length = 50)
  private String cellPhone;

  @Column(name = "email", length = 50)
  private String email;

  @Column(name = "email2", length = 50)
  private String email2;

  @Column(name = "cobra_sf")
  private Boolean cobraSf;

  @Column(name = "referrer", length = 50)
  private String referrer;

  @Column(name = "observations", length = 300)
  private String observations;

  @Column(name = "birth_date")
  private Date birthDate;

  @Column(name = "gender")
  @Enumerated(EnumType.STRING)
  private GENDER gender;

  @Column(name = "education", length = 150)
  private String education;

  @Column(name = "confidentiality")
  private Boolean confidentiality;

  @Column(name = "login_shopmetrics", length = 50)
  private String loginShopmetrics;

  @Column(name = "creation_date")
  private Date creationDate;

  @Column(name = "last_modification_date")
  private Date lastModificationDate;

  @Column(name = "enabled")
  private Boolean enabled;

  Shopper() {
  }

  public Shopper(String loginShopmetrics, String surname, String firstName, String address, String region, String state,
                 Integer country, String postalCode, String workPhone, String particularPhone, String cellPhone,
                 String email, Date birthDate, GENDER gender,  String education, Boolean confidentiality,
                 Date creationDate) {
    this.loginShopmetrics = loginShopmetrics;
    this.surname = surname;
    this.firstName = firstName;
    this.address = address;
    this.region = region;
    this.state = state;
    this.country = country;
    this.postalCode = postalCode;
    this.workPhone = workPhone;
    this.particularPhone = particularPhone;
    this.cellPhone = cellPhone;
    this.email = email;
    this.birthDate = birthDate;
    this.gender = gender;
    this.education = education;
    this.confidentiality = confidentiality;
    this.enabled = true;
    this.creationDate = creationDate;
    this.lastModificationDate = creationDate;
  }

  public Shopper(String identityType, String identityId, String surname, String firstName,
                 String address, String neighborhood, String region, String state,
                 Integer country, String postalCode, String workPhone, String particularPhone,
                 String cellPhone, String email, String email2, Boolean cobraSf,
                 String referrer, String observations, Date birthDate, GENDER gender,
                 String education, Boolean confidentiality, String loginShopmetrics, Boolean enabled) {
    this.identityType = identityType;
    this.identityId = identityId;
    this.surname = surname;
    this.firstName = firstName;
    this.address = address;
    this.neighborhood = neighborhood;
    this.region = region;
    this.state = state;
    this.country = country;
    this.postalCode = postalCode;
    this.workPhone = workPhone;
    this.particularPhone = particularPhone;
    this.cellPhone = cellPhone;
    this.email = email;
    this.email2 = email2;
    this.cobraSf = cobraSf;
    this.referrer = referrer;
    this.observations = observations;
    this.birthDate = birthDate;
    this.gender = gender;
    this.education = education;
    this.confidentiality = confidentiality;
    this.loginShopmetrics = loginShopmetrics;
    this.enabled = enabled;
    this.creationDate = new Date();
    this.lastModificationDate = this.creationDate;
  }

  public long getId() {
    return id;
  }

  public String getName() {
      StringBuilder builder = new StringBuilder(surname);
      if (isNotBlank(firstName)) {
          builder.append(" ");
          builder.append(firstName);
      }
      return builder.toString();
  }

  public String getSurname() {
    return surname;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getIdentityType() {
    return identityType;
  }

  public String getIdentityId() {
    return identityId;
  }

  public String getAddress() {
    return address;
  }

  public String getNeighborhood() {
    return neighborhood;
  }

  public String getRegion() {
    return region;
  }

  public String getState() {
    return state;
  }

  public Integer getCountry() {
    return country;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public String getWorkPhone() {
    return workPhone;
  }

  public String getParticularPhone() {
    return particularPhone;
  }

  public String getCellPhone() {
    return cellPhone;
  }

  public String getEmail() {
    return email;
  }

  public String getEmail2() {
    return email2;
  }

  public Boolean getCobraSf() {
    return cobraSf;
  }

  public String getReferrer() {
    return referrer;
  }

  public String getObservations() {
    return observations;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public GENDER getGender() {
    return gender;
  }

  public String getEducation() {
    return education;
  }

  public Boolean getConfidentiality() {
    return confidentiality;
  }

  public String getLoginShopmetrics() {
    return loginShopmetrics;
  }

  public Date getCreationDate() {
    return creationDate;
  }

  public Date getLastModificationDate() {
    return lastModificationDate;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void update(String identityType, String identityId, String surname, String firstName,
                     String address, String neighborhood, String region, String state,
                     Integer country, String postalCode, String workPhone, String particularPhone,
                     String cellPhone, String email, String email2, Boolean cobraSf,
                     String referrer, String observations, Date birthDate, GENDER gender,
                     String education, Boolean confidentiality, String loginShopmetrics, Boolean enabled) {
    this.identityType = identityType;
    this.identityId = identityId;
    this.surname = surname;
    this.firstName = firstName;
    this.address = address;
    this.neighborhood = neighborhood;
    this.region = region;
    this.state = state;
    this.country = country;
    this.postalCode = postalCode;
    this.workPhone = workPhone;
    this.particularPhone = particularPhone;
    this.cellPhone = cellPhone;
    this.email = email;
    this.email2 = email2;
    this.cobraSf = cobraSf;
    this.referrer = referrer;
    this.observations = observations;
    this.birthDate = birthDate;
    this.gender = gender;
    this.education = education;
    this.confidentiality = confidentiality;
    this.loginShopmetrics = loginShopmetrics;
    this.enabled = enabled;
    this.lastModificationDate = new Date();
  }

  public void updateFromShopmetrics(String surname, String firstName, String address, String region, String state, Integer country,
                                    String postalCode, String workPhone, String particularPhone, String cellPhone,
                                    String email, Date birthDate, GENDER gender,  String education, Boolean confidentiality) {
    this.surname = surname;
    this.firstName = firstName;
    this.address = address;
    this.region = region;
    this.state = state;
    this.country = country;
    this.postalCode = postalCode;
    this.workPhone = workPhone;
    this.particularPhone = particularPhone;
    this.cellPhone = cellPhone;
    this.email = email;
    this.birthDate = birthDate;
    this.gender = gender;
    this.education = education;
    this.confidentiality = confidentiality;
    this.lastModificationDate = new Date();
  }

  public void update(String loginShopmetrics) {
    this.loginShopmetrics = loginShopmetrics;
    this.lastModificationDate = new Date();
  }

  public void disable() {
    this.enabled = Boolean.FALSE;
  }

  @Override
  public String toString() {
    return "Shopper{" +
            "id=" + id +
            ", identityType='" + identityType + '\'' +
            ", identityId='" + identityId + '\'' +
            ", surname='" + surname + '\'' +
            ", firstName='" + firstName + '\'' +
            ", address='" + address + '\'' +
            ", neighborhood='" + neighborhood + '\'' +
            ", region='" + region + '\'' +
            ", state='" + state + '\'' +
            ", pais=" + country +
            ", postalCode='" + postalCode + '\'' +
            ", workPhone='" + workPhone + '\'' +
            ", particularPhone='" + particularPhone + '\'' +
            ", cellPhone='" + cellPhone + '\'' +
            ", email='" + email + '\'' +
            ", email2='" + email2 + '\'' +
            ", cobraSf=" + cobraSf +
            ", referrer='" + referrer + '\'' +
            ", observations='" + observations + '\'' +
            ", birthDate=" + birthDate +
            ", gender=" + gender +
            ", education='" + education + '\'' +
            ", confidentiality=" + confidentiality +
            ", loginShopmetrics='" + loginShopmetrics + '\'' +
            ", creationDate=" + creationDate +
            ", lastModificationDate=" + lastModificationDate +
            ", enabled=" + enabled +
            '}';
  }

}
