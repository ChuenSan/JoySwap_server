package com.sgq.market.model.dto;

import com.sgq.market.model.Page;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author sgq
 * @version 1.0
 * @description TODO
 * @date 2024/2/21 21:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SystemUserPageDto extends Page {
  private String key;
  
}
