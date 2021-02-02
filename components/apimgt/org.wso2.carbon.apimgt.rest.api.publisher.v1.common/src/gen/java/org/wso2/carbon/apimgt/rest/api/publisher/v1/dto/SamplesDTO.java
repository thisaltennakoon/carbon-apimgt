package org.wso2.carbon.apimgt.rest.api.publisher.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.*;


import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.common.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;



public class SamplesDTO   {
  
    private String sample1 = null;
    private String sample2 = null;

  /**
   * Sample 1. 
   **/
  public SamplesDTO sample1(String sample1) {
    this.sample1 = sample1;
    return this;
  }

  
  @ApiModelProperty(example = "“sample 1”", value = "Sample 1. ")
  @JsonProperty("sample1")
  public String getSample1() {
    return sample1;
  }
  public void setSample1(String sample1) {
    this.sample1 = sample1;
  }

  /**
   * Sample 2. 
   **/
  public SamplesDTO sample2(String sample2) {
    this.sample2 = sample2;
    return this;
  }

  
  @ApiModelProperty(example = "“sample 2”", value = "Sample 2. ")
  @JsonProperty("sample2")
  public String getSample2() {
    return sample2;
  }
  public void setSample2(String sample2) {
    this.sample2 = sample2;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SamplesDTO samples = (SamplesDTO) o;
    return Objects.equals(sample1, samples.sample1) &&
        Objects.equals(sample2, samples.sample2);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sample1, sample2);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class SamplesDTO {\n");
    
    sb.append("    sample1: ").append(toIndentedString(sample1)).append("\n");
    sb.append("    sample2: ").append(toIndentedString(sample2)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

