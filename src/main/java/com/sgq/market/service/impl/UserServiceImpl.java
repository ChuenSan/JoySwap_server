package com.sgq.market.service.impl;

import cn.dev33.satoken.secure.SaSecureUtil;
import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.entity.ChatList;
import com.sgq.market.entity.ChatMessage;
import com.sgq.market.entity.User;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.mapper.ChatListMapper;
import com.sgq.market.mapper.ChatMessageMapper;
import com.sgq.market.model.R;
import com.sgq.market.model.dto.UpdateUserInfoDto;
import com.sgq.market.model.dto.UserAdminPageDto;
import com.sgq.market.model.dto.UserLoginDto;
import com.sgq.market.service.UserService;
import com.sgq.market.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author sgq
 * @description 针对表【user】的数据库操作Service实现
 * @createDate 2024-01-28 16:52:33
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
  @Autowired
  private StringRedisTemplate redisTemplate;
  @Autowired
  private ChatMessageMapper chatMessageMapper;
  @Autowired
  private ChatListMapper chatListMapper;
  
  @Override
  public SaTokenInfo login(UserLoginDto request) {
    String userCode = request.getCode();
    String phone = request.getPhone();
    String redis_code = redisTemplate.opsForValue().get("check:code:" + phone);
    if(StrUtil.isEmpty(redis_code) ) throw new ServiceException(ResultCode.Fail, "验证码错误");
    if ( !redis_code.equals(userCode)) throw new ServiceException(ResultCode.Fail, "验证码错误");
    //1.查询用户是否存在
    User user = lambdaQuery().eq(User::getPhone, request.getPhone()).one();
    //2.不存在 自动创建用户
    if (null == user) {
      user = new User();
      String numbers = RandomUtil.randomNumbers(9);
      user.setNumber(numbers);
      user.setPhone(Long.valueOf(request.getPhone()));
      user.setProvince(request.getProvince());
      user.setCity(request.getCity());
      user.setAvatar("https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png");
      user.setNickName("用户"+ numbers);
      user.setCreateTime(new Date().getTime());
      user.setUpdateTime(new Date().getTime());
      user.setStatus(9);
      boolean save = save(user);
      if (!save) throw new ServiceException(ResultCode.Fail);
    }else{
      if (user.getStatus() == 0) {
        throw new ServiceException(ResultCode.Fail,"账户被禁用");
      }
      user.setProvince(request.getProvince());
      user.setCity(request.getCity());
      user.setUpdateTime(new Date().getTime());
      boolean update = updateById(user);
      if (!update) throw new ServiceException(ResultCode.Fail);
    }
    StpUtil.login(user.getId());
    redisTemplate.delete("check:code:" + request.getPhone());
    //返回token
    return StpUtil.getTokenInfo();
  }
  
  @Override
  public SaTokenInfo loginPwd(UserLoginDto request) {
    User user = lambdaQuery().eq(User::getPhone, request.getPhone()).one();
    if(BeanUtil.isEmpty(user))throw new ServiceException(ResultCode.NotFindError,"第一次登陆，请选择手机号登录");
    String md5 = SaSecureUtil.md5(request.getPassword());
    if(!md5.equals(user.getPassword())) throw new ServiceException(ResultCode.Fail, "手机号或密码错误");
    user.setProvince(request.getProvince());
    user.setCity(request.getCity());
    user.setUpdateTime(new Date().getTime());
    boolean update = updateById(user);
    if (!update) throw new ServiceException(ResultCode.Fail);
    StpUtil.login(user.getId());
    return StpUtil.getTokenInfo();
  }
  
  /**
   * 更新用户信息方法
   * 此方法用于根据当前登录用户提交的信息来更新其个人资料
   * 它首先获取当前登录用户的ID，然后根据该ID获取用户对象
   * 如果用户对象为空，则抛出服务异常，表明未找到用户
   * 接着，方法将用户提交的昵称、简介和头像信息设置到用户对象中
   * 并将审核状态设置为0，表示待审核
   * 最后，方法尝试更新用户信息如果更新失败，则抛出服务异常，表明更新错误
   *
   * @param dto 包含用户提交的新信息的数据传输对象
   */
  @Override
  @Transactional
  public void updateUserInfo(UpdateUserInfoDto dto) {
    // 获取当前登录用户的ID
    String id = StpUtil.getLoginIdAsString();
    // 根据用户ID获取用户对象
    User user = getById(id);
    // 如果用户对象为空，则抛出服务异常，表明未找到用户
    if (BeanUtil.isEmpty(user)) throw new ServiceException(ResultCode.NotFindError);
    // 将用户提交的昵称设置到用户对象中
    user.setCheckNickName(dto.getNickName());
    // 将用户提交的简介设置到用户对象中
    user.setCheckIntro(dto.getIntro());
    // 将用户提交的头像设置到用户对象中
    user.setCheckAvatar(dto.getAvatar());
    // 将审核状态设置为0，表示待审核
    user.setCheckStatus(0);
    // 尝试更新用户信息
    boolean update = updateById(user);
    // 根据id更新chat_message
    updateChatMessages(id, dto);

    // 根据id更新chat_list
    updateChatLists(id, dto);

    // 如果更新失败，则抛出服务异常，表明更新错误
    if (!update) throw new ServiceException(ResultCode.UpdateError);
  }

  private void updateChatMessages(String userId, UpdateUserInfoDto dto) {
    List<ChatMessage> chatMessages = chatMessageMapper.selectList(
            Wrappers.<ChatMessage>lambdaQuery().eq(ChatMessage::getFromUserId, userId)
                    .or().eq(ChatMessage::getToUserId, userId)
    );
    for (ChatMessage chatMessage : chatMessages) {
      if (chatMessage.getToUserId().equals(userId)) {
        chatMessage.setToUserNick(dto.getNickName());
      } else {
        chatMessage.setFromUserNick(dto.getNickName());
      }
      chatMessageMapper.updateById(chatMessage);
    }
  }

  private void updateChatLists(String userId, UpdateUserInfoDto dto) {
    List<ChatList> chatLists = chatListMapper.selectList(
            Wrappers.<ChatList>lambdaQuery().eq(ChatList::getFromUserId, userId)
                    .or().eq(ChatList::getToUserId, userId)
    );
    for (ChatList chatList : chatLists) {
      if (chatList.getFromUserId().equals(userId)) {
        chatList.setFromUserNick(dto.getNickName());
        chatList.setFromUserAvatar(dto.getAvatar());
      } else {
        chatList.setToUserNick(dto.getNickName());
        chatList.setToUserAvatar(dto.getAvatar());
      }
      chatListMapper.updateById(chatList);
    }
  }

  @Override
  public Map getNum1UserStat() {
  return null;
  }
  
  @Override
  public List<Map> getUserStat() {
    LocalDate today = LocalDate.now();
    LocalDate monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    
    List<Map> weeklyUserData = new ArrayList<>();
    String[] weekDays = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};
    
    for (int i = 0; i < 7; i++) {
      LocalDate currentDate = monday.plusDays(i);
      
      long newUsers = __getNewUsersCount(currentDate);
      long activeUsers = __getActiveUsersCount(currentDate);
      
      Map<String, Object> userData = new HashMap<>();
      userData.put("date", weekDays[i]);
      userData.put("new", newUsers);
      userData.put("active", activeUsers);
      
      weeklyUserData.add(userData);
    }
    
    return weeklyUserData;
  }
  
  private long __getActiveUsersCount(LocalDate currentDate) {
    //根据currentDate 获取当日0点到24点的时间戳
    long start = currentDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
    long end = currentDate.atStartOfDay().plusDays(1).toEpochSecond(ZoneOffset.UTC) * 1000;
    Long count = lambdaQuery().between(User::getUpdateTime, start, end).count();
    return count;
  }
  
  private long __getNewUsersCount(LocalDate currentDate) {
    //根据currentDate 获取当日0点到24点的时间戳
    long start = currentDate.atStartOfDay().toEpochSecond(ZoneOffset.UTC) * 1000;
    long end = currentDate.atStartOfDay().plusDays(1).toEpochSecond(ZoneOffset.UTC) * 1000;
    Long count = lambdaQuery().between(User::getCreateTime, start, end).count();
    return count;
  }
  
  
  @Override
  public R<User> getUserInfo() {
    Object loginId = StpUtil.getLoginId();
    User user = lambdaQuery().eq(User::getId, loginId).one();
    if (user == null) {
      throw new ServiceException(ResultCode.NotFindError, "用户不存在");
    }
    return R.ok(user);
  }
  
  @Override
  public void getLoginCode(String phone) {
    redisTemplate.opsForValue().set("check:code:" + phone, "123456", 120, TimeUnit.SECONDS);
  }
  
  @Override
  public R<User> getUserInfo(String userId) {
    User user = lambdaQuery().eq(User::getId, userId).one();
    if (user == null) {
      throw new ServiceException(ResultCode.NotFindError, "用户不存在");
    }
    return R.ok(user);
  }
  
  @Override
  public void updateUserInfoDetail(UpdateUserInfoDto dto) {
    String id = StpUtil.getLoginIdAsString();
    User user = getById(id);
    if(BeanUtil.isEmpty(user))  throw new ServiceException(ResultCode.NotFindError);
    if(StrUtil.isNotEmpty(dto.getPassword())){
      if (!dto.getPassword().equals(dto.getPasswordCheck())) throw new ServiceException(ResultCode.ValidateError,"输入密码不一致");
      String md5 = SaSecureUtil.md5(dto.getPassword());
      user.setPassword(md5);
    }
    boolean update = updateById(user);
    if(!update) throw new ServiceException(ResultCode.UpdateError);
  }
  
  @Override
  public Page getUserList(UserAdminPageDto dto) {
    Page<User> page = lambdaQuery()
      .like(StrUtil.isNotEmpty(dto.getKey()), User::getNickName, dto.getKey()).or()
      .like(StrUtil.isNotEmpty(dto.getKey()), User::getNumber, dto.getKey())
      .eq(null != dto.getCheckStatus(), User::getCheckStatus, dto.getCheckStatus())
      .orderByDesc(User::getCreateTime)
      .page(new Page<>(dto.getPageNumber(), dto.getPageSize()));
    return page;
  }
  

}




