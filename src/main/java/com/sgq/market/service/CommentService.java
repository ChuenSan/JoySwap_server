package com.sgq.market.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sgq.market.entity.Comment;
import com.sgq.market.model.dto.SystemCommentPageDto;

import java.util.HashMap;

public interface CommentService extends IService<Comment> {
  HashMap getCommentList(String productId);
  
  void saveComment(String productId, String parentId, String content);
  
  Page getCommentPage(SystemCommentPageDto dto);
}
