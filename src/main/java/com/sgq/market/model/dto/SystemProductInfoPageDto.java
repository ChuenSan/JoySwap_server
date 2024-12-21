package com.sgq.market.model.dto;

import com.sgq.market.model.Page;
import lombok.Data;

@Data
public class SystemProductInfoPageDto extends Page {
  private String key;
  private String status;
}
