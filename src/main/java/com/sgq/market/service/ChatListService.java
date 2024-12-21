package com.sgq.market.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sgq.market.entity.ChatList;
import com.sgq.market.model.dto.ChatListCreateDto;
import com.sgq.market.model.vo.ChatListVo;

import java.util.List;

public interface ChatListService extends IService<ChatList> {
  String create(ChatListCreateDto dto);
  
  List<ChatListVo> getMyChatList();
  
  
  int getNoReadTotal();
  
}
