package org.wso2.carbon.apimgt.rest.api.store.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.NotificationDTO;
import org.wso2.carbon.apimgt.rest.api.store.v1.dto.PaginationDTO;
import javax.validation.constraints.*;

/**
 * A list of notifications.
 **/

import io.swagger.annotations.*;
import java.util.Objects;

import javax.xml.bind.annotation.*;
import org.wso2.carbon.apimgt.rest.api.common.annotations.Scope;
import com.fasterxml.jackson.annotation.JsonCreator;

import javax.validation.Valid;

@ApiModel(description = "A list of notifications.")

public class NotificationListDTO   {
  
    private Integer count = null;
    private Integer unreadCount = null;
    private List<NotificationDTO> list = new ArrayList<NotificationDTO>();
    private PaginationDTO pagination = null;

  /**
   * Number of notifications returned.
   **/
  public NotificationListDTO count(Integer count) {
    this.count = count;
    return this;
  }

  
  @ApiModelProperty(example = "1", value = "Number of notifications returned.")
  @JsonProperty("count")
  public Integer getCount() {
    return count;
  }
  public void setCount(Integer count) {
    this.count = count;
  }

  /**
   * Number of unread notifications returned.
   **/
  public NotificationListDTO unreadCount(Integer unreadCount) {
    this.unreadCount = unreadCount;
    return this;
  }

  
  @ApiModelProperty(example = "5", value = "Number of unread notifications returned.")
  @JsonProperty("unreadCount")
  public Integer getUnreadCount() {
    return unreadCount;
  }
  public void setUnreadCount(Integer unreadCount) {
    this.unreadCount = unreadCount;
  }

  /**
   **/
  public NotificationListDTO list(List<NotificationDTO> list) {
    this.list = list;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("list")
  public List<NotificationDTO> getList() {
    return list;
  }
  public void setList(List<NotificationDTO> list) {
    this.list = list;
  }

  /**
   **/
  public NotificationListDTO pagination(PaginationDTO pagination) {
    this.pagination = pagination;
    return this;
  }

  
  @ApiModelProperty(value = "")
      @Valid
  @JsonProperty("pagination")
  public PaginationDTO getPagination() {
    return pagination;
  }
  public void setPagination(PaginationDTO pagination) {
    this.pagination = pagination;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    NotificationListDTO notificationList = (NotificationListDTO) o;
    return Objects.equals(count, notificationList.count) &&
        Objects.equals(unreadCount, notificationList.unreadCount) &&
        Objects.equals(list, notificationList.list) &&
        Objects.equals(pagination, notificationList.pagination);
  }

  @Override
  public int hashCode() {
    return Objects.hash(count, unreadCount, list, pagination);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class NotificationListDTO {\n");
    
    sb.append("    count: ").append(toIndentedString(count)).append("\n");
    sb.append("    unreadCount: ").append(toIndentedString(unreadCount)).append("\n");
    sb.append("    list: ").append(toIndentedString(list)).append("\n");
    sb.append("    pagination: ").append(toIndentedString(pagination)).append("\n");
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

