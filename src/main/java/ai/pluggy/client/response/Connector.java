package ai.pluggy.client.response;

import java.util.List;

public class Connector {
  private Integer id;
  private String name;
  private String primaryColor;
  private String institutionUrl;
  private String country;
  private String type;
  private List<CredentialLabel> credentials;
  private String imageUrl;


  public void setId(Integer id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPrimaryColor(String primaryColor) {
    this.primaryColor = primaryColor;
  }

  public void setInstitutionUrl(String institutionUrl) {
    this.institutionUrl = institutionUrl;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setType(String type) {
    this.type = type;
  }

  public void setCredentials(List<CredentialLabel> credentials) {
    this.credentials = credentials;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}
