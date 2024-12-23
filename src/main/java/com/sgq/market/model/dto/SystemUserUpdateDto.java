package com.sgq.market.model.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author sgq
 * @version 1.0
 * @description TODO
 * @date 2024/2/24 13:56
 */
@Data
public class SystemUserUpdateDto {
  private String id;
  private String username;
  private String name;
  private String password;
  private String roleCode;
}
