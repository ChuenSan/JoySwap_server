package com.sgq.market.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.sgq.market.entity.ChatMessage;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ChatListVo {
  
  @ApiModelProperty("")
  @TableId(value = "id")
  private String id;
  
  @ApiModelProperty("")
  private String fromUserId;
  
  @ApiModelProperty("")
  private String toUserId;
  
  @ApiModelProperty("")
  private String fromUserNick;
  private String fromUserAvatar;
  private String productId;
  private String productImage;
  @ApiModelProperty("")
  private String toUserNick;
  private String toUserAvatar;
  @ApiModelProperty(value = "", example = "0")
  private Long createTime;
  
  @ApiModelProperty(value = "", example = "0")
  private Long updateTime;
  private ChatMessage chatMessage;
  private Integer noReadCount;
}
