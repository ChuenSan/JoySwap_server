package com.sgq.market.controller.admin;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.SystemCommentPageDto;
import com.sgq.market.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/comment")
@SaCheckLogin
@SaCheckRole(value = {"admin"})
public class CommentAdminController {
  @Autowired
  private CommentService commentService;
  @GetMapping("/page")
  public R<Page> getCommentList(SystemCommentPageDto dto) {
    Page page = commentService.getCommentPage(dto);
    return R.ok(page);
  }
  @DeleteMapping("/{id}")
  public R deleteComment(@PathVariable("id")String id){
    boolean update = commentService.removeById(id);
    if(!update) throw new ServiceException(ResultCode.DeleteError);
    return R.ok();
  }
}