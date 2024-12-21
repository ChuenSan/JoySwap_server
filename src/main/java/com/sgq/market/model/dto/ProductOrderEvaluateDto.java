package com.sgq.market.model.dto;

import io.swagger.models.auth.In;
import lombok.Data;

@Data
public class ProductOrderEvaluateDto {
  private String id;
  private Integer score;
  private String content;
}
