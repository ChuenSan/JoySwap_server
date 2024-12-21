package com.sgq.market.model.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
public class CommentVo {
  
  @ApiModelProperty("")
  @TableId(value = "id")
  private String id;
  
  /**
   * 父级id
   */
  @ApiModelProperty("父级id")
  private String parentId;
  
  /**
   * 发布者id
   */
  @ApiModelProperty("发布者id")
  private String pubUserId;
  
  /**
   * 发布者昵称
   */
  @ApiModelProperty("发布者昵称")
  private String pubNickname;
  private String pubProvince;
  private String pubAvatar;
  /**
   * 父级用户id
   */
  @ApiModelProperty("父级用户id")
  private String parentUserId;
  
  @ApiModelProperty("")
  private String parentUserNickname;
  
  /**
   * 评论内容
   */
  @ApiModelProperty("评论内容")
  private String content;
  
  /**
   * 创建时间
   */
  @ApiModelProperty(value = "创建时间", example = "0")
  private Long createTime;
  
  private List<CommentVo> commentChildren;
}