package com.sgq.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sgq.market.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService extends IService<ChatMessage> {
  boolean saveList(List<String> messageList);
  
  List<ChatMessage> getChatMessageList(String chatListId);
  
  void updateChatMessageIsRead(String chatListId);
  
}
