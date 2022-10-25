package org.wso2.apk.apimgt.rest.api.publisher.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;



public class APIScopeDTO   {
  
    private ScopeDTO scope = null;
    private Boolean shared = null;

  /**
   **/
  public APIScopeDTO scope(ScopeDTO scope) {
    this.scope = scope;
    return this;
  }

  
  @ApiModelProperty(required = true, value = "")
      @Valid
  @JsonProperty("scope")
  @NotNull
  public ScopeDTO getScope() {
    return scope;
  }
  public void setScope(ScopeDTO scope) {
    this.scope = scope;
  }

  /**
   * States whether scope is shared. This will not be honored when updating/adding scopes to APIs or when adding/updating Shared Scopes. 
   **/
  public APIScopeDTO shared(Boolean shared) {
    this.shared = shared;
    return this;
  }

  
  @ApiModelProperty(example = "true", value = "States whether scope is shared. This will not be honored when updating/adding scopes to APIs or when adding/updating Shared Scopes. ")
  @JsonProperty("shared")
  public Boolean isShared() {
    return shared;
  }
  public void setShared(Boolean shared) {
    this.shared = shared;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    APIScopeDTO apIScope = (APIScopeDTO) o;
    return Objects.equals(scope, apIScope.scope) &&
        Objects.equals(shared, apIScope.shared);
  }

  @Override
  public int hashCode() {
    return Objects.hash(scope, shared);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class APIScopeDTO {\n");
    
    sb.append("    scope: ").append(toIndentedString(scope)).append("\n");
    sb.append("    shared: ").append(toIndentedString(shared)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
