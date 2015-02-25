package com.ibiscus.shopnchek.application.shopmetrics;

public class ShopmetricsUserDto {

  private String login;
  private String lastName;
  private String name;

  public ShopmetricsUserDto(final String theLogin, final String theLastName,
      final String theName) {
    login = theLogin;
    lastName = theLastName;
    name = theName;
  }

  /**
   * @return the login
   */
  public String getLogin() {
    return login;
  }

  /**
   * @return the lastName
   */
  public String getLastName() {
    return lastName;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }
}
