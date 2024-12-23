package com.sgq.market.model.dto;

import com.sgq.market.model.Page;
import lombok.Data;

/**
 * @author sgq
 * @version 1.0
 * @description TODO
 * @date 2024/3/3 18:21
 */
@Data
public class ProductInfoPageDto extends Page {
  private String typeCode;
  private String key;
  private String status;
}
