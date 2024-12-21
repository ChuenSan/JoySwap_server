package com.sgq.market.model.dto;

import com.sgq.market.model.Page;
import lombok.Data;

@Data
public class UserAdminPageDto extends Page {
  private String key;
  private Integer checkStatus;
}
