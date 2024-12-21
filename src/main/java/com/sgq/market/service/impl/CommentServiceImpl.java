package com.sgq.market.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.Comment;
import com.sgq.market.entity.User;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.mapper.CommentMapper;
import com.sgq.market.model.dto.SystemCommentPageDto;
import com.sgq.market.model.vo.CommentVo;
import com.sgq.market.service.CommentService;
import com.sgq.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
  @Autowired
  private UserService userService;
  
  @Override
  public HashMap getCommentList(String productId) {
    ArrayList<CommentVo> resultList = new ArrayList<>();
    List<Comment> commentList = lambdaQuery().eq(Comment::getProductId, productId).orderByAsc(Comment::getCreateTime).list();
    List<CommentVo> commentVoList = commentList.stream().map(item -> {
      CommentVo bean = BeanUtil.toBean(item, CommentVo.class);
      if (!BeanUtil.isEmpty(bean)) {
        User user = userService.getById(item.getPubUserId());
        bean.setPubAvatar(user.getAvatar());
        bean.setPubProvince(user.getProvince());
      }
      return bean;
    }).collect(Collectors.toList());
    for (CommentVo commentVo : commentVoList) {
      if (StrUtil.isNotEmpty(commentVo.getParentId())) {
        getParentComment(commentVo, commentVoList);
      } else {
        resultList.add(commentVo);
      }
    }
    HashMap<Object, Object> map = new HashMap<>();
    map.put("commentList", resultList);
    map.put("commentCount", commentList.size());
    return map;
  }
  
  @Override
  public void saveComment(String productId, String parentId, String content) {
    Comment comment = new Comment();
    comment.setProductId(productId);
    comment.setContent(content);
    comment.setCreateTime(new Date().getTime());
    String loginId = StpUtil.getLoginIdAsString();
    comment.setPubUserId(loginId);
    User user = userService.getById(loginId);
    comment.setPubNickname(user.getNickName());
    if (StrUtil.isNotEmpty(parentId)) {
      Comment parentComment = getById(parentId);
      comment.setParentUserId(parentComment.getPubUserId());
      comment.setParentUserNickname(parentComment.getPubNickname());
      if (StrUtil.isNotEmpty(parentComment.getParentId())) {
        comment.setParentId(parentComment.getParentId());
      } else {
        comment.setParentId(parentId);
      }
    }
    boolean save = save(comment);
    if (!save) throw new ServiceException(ResultCode.SaveError);
  }
  
  @Override
  public Page getCommentPage(SystemCommentPageDto dto) {
    Page<Comment> page = lambdaQuery()
      .like(StrUtil.isNotEmpty(dto.getKey()), Comment::getPubNickname, dto.getKey()).or()
      .like(StrUtil.isNotEmpty(dto.getKey()), Comment::getParentUserId, dto.getKey()).or()
      .like(StrUtil.isNotEmpty(dto.getKey()), Comment::getProductId, dto.getKey()).or()
      .like(StrUtil.isNotEmpty(dto.getKey()), Comment::getContent, dto.getKey())
      .orderByDesc(Comment::getCreateTime)
      .page(new Page<>(dto.getPageNumber(), dto.getPageSize()));
    return page;
  }
  
  private void getParentComment(CommentVo commentVo, List<CommentVo> commentVoList) {
    for (CommentVo vo : commentVoList) {
      if (commentVo.getParentId().equals(vo.getId())) {
        if (vo.getCommentChildren() == null) {
          ArrayList<CommentVo> list = new ArrayList<>();
          list.add(commentVo);
          vo.setCommentChildren(list);
        } else {
          vo.getCommentChildren().add(commentVo);
        }
      }
    }
  }
}
