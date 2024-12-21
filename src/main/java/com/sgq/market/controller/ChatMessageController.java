package com.sgq.market.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.sgq.market.entity.ChatMessage;
import com.sgq.market.model.R;
import com.sgq.market.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat/message")
@SaCheckLogin
public class ChatMessageController {
  @Autowired
  private ChatMessageService chatMessageService;
  
  @GetMapping
  public R<List<ChatMessage>> getChatMessageList(String chatListId) {
    List<ChatMessage> list =  chatMessageService.getChatMessageList(chatListId);
    return R.ok(list);
  }
  @PutMapping("/{chatListId}")
  public R updateChatMessageIsRead(@PathVariable("chatListId") String chatListId){
    chatMessageService.updateChatMessageIsRead(chatListId);
    return R.ok();
  }
}
