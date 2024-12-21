package com.sgq.market.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.bean.BeanUtil;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.ChatList;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.ChatListCreateDto;
import com.sgq.market.model.vo.ChatListVo;
import com.sgq.market.service.ChatListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/list")
@SaCheckLogin
public class ChatListController {
  @Autowired
  private ChatListService chatListService;

  @PostMapping
  public R<String> create(@RequestBody ChatListCreateDto dto){
   String chatListId =  chatListService.create(dto);
   return R.ok(chatListId);
  }
  @GetMapping("/all")
  public R<List<ChatListVo>> getMyChatList(){
    List<ChatListVo> list =  chatListService.getMyChatList();
    return R.ok(list);
  }
  
  @GetMapping("/one")
  public R<ChatList> getMyChatList(String chatListId) {
    ChatList chatList = chatListService.getById(chatListId);
    if(BeanUtil.isEmpty(chatList)) throw new ServiceException(ResultCode.NotFindError);
    return R.ok(chatList);
  }
  @GetMapping("/noRead/total")
  public R getNoReadTotal(){
    int total = chatListService.getNoReadTotal();
    return R.ok(total);
  }
}
