package com.ibiscus.shopnchek.application.shopper;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class NewShopper {

  private String identityType;
  private String identityId;
  private String surname;
  private String firstName;
  private String address;
  private String neighborhood;
  private String region;
  private String state;
  private Integer country;
  private String postalCode;
  private String workPhone;
  private String particularPhone;
  private String cellPhone;
  private String email;
  private String email2;
  private Boolean cobraSf;
  private String referrer;
  private String observations;
  @DateTimeFormat(pattern="dd/MM/yyyy")
  private Date birthDate;
  private String gender;
  private String education;
  private Boolean confidentiality;
  private String loginShopmetrics;
  private Boolean enabled;

  NewShopper() {
  }

  public String getIdentityType() {
    return identityType;
  }

  public void setIdentityType(String identityType) {
    this.identityType = identityType;
  }

  public String getIdentityId() {
    return identityId;
  }

  public void setIdentityId(String identityId) {
    this.identityId = identityId;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getNeighborhood() {
    return neighborhood;
  }

  public void setNeighborhood(String neighborhood) {
    this.neighborhood = neighborhood;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public Integer getCountry() {
    return country;
  }

  public void setCountry(Integer country) {
    this.country = country;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getWorkPhone() {
    return workPhone;
  }

  public void setWorkPhone(String workPhone) {
    this.workPhone = workPhone;
  }

  public String getParticularPhone() {
    return particularPhone;
  }

  public void setParticularPhone(String particularPhone) {
    this.particularPhone = particularPhone;
  }

  public String getCellPhone() {
    return cellPhone;
  }

  public void setCellPhone(String cellPhone) {
    this.cellPhone = cellPhone;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getEmail2() {
    return email2;
  }

  public void setEmail2(String email2) {
    this.email2 = email2;
  }

  public Boolean getCobraSf() {
    return cobraSf;
  }

  public void setCobraSf(Boolean cobraSf) {
    this.cobraSf = cobraSf;
  }

  public String getReferrer() {
    return referrer;
  }

  public void setReferrer(String referrer) {
    this.referrer = referrer;
  }

  public String getObservations() {
    return observations;
  }

  public void setObservations(String observations) {
    this.observations = observations;
  }

  public Date getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(Date birthDate) {
    this.birthDate = birthDate;
  }

  public String getGender() {
    return gender;
  }

  public void setGender(String gender) {
    this.gender = gender;
  }

  public String getEducation() {
    return education;
  }

  public void setEducation(String education) {
    this.education = education;
  }

  public Boolean getConfidentiality() {
    return confidentiality;
  }

  public void setConfidentiality(Boolean confidentiality) {
    this.confidentiality = confidentiality;
  }

  public String getLoginShopmetrics() {
    return loginShopmetrics;
  }

  public void setLoginShopmetrics(String loginShopmetrics) {
    this.loginShopmetrics = loginShopmetrics;
  }

  public Boolean getEnabled() {
    return enabled;
  }

  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  @Override
  public String toString() {
    return "NewShopper{" +
            "identityType='" + identityType + '\'' +
            ", identityId='" + identityId + '\'' +
            ", surname='" + surname + '\'' +
            ", firstName='" + firstName + '\'' +
            ", address='" + address + '\'' +
            ", neighborhood='" + neighborhood + '\'' +
            ", region='" + region + '\'' +
            ", state='" + state + '\'' +
            ", country=" + country +
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
            ", gender='" + gender + '\'' +
            ", education='" + education + '\'' +
            ", confidentiality=" + confidentiality +
            ", loginShopmetrics='" + loginShopmetrics + '\'' +
            ", enabled=" + enabled +
            '}';
  }

}
