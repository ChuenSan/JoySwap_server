package com.sgq.market.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.sgq.market.constants.MinioProp;
import com.sgq.market.constants.ResultCode;
import com.sgq.market.constants.SystemConstants;
import com.sgq.market.exception.ServiceException;
import com.sgq.market.model.R;
import com.sgq.market.utils.QiniuUtils;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * @author zhangyihua
 * @version 1.0
 * @description TODO
 * @date 2024/3/1 21:48
 */
@RestController
@RequestMapping("/upload")
@Slf4j
public class UploadController {
  @Autowired
  private QiniuUtils qiniuUtils;
  @Autowired
  private MinioClient minioClient;
  @Autowired
  private MinioProp minioProp;
  
  //minio
  @PostMapping("/image")
  public R uploadImage(@RequestParam("file") MultipartFile image) {
    try {
      String fileName = UUID.randomUUID() + "." + StrUtil.subAfter(image.getOriginalFilename(), ".", true);
      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
      String path = formatter.format(new Date()).replace('-', '/');
      //上传
      minioClient.putObject(PutObjectArgs.builder()
        .bucket(minioProp.getBucket())
        .object(path + '/' + fileName)
        .contentType(image.getContentType())
        .stream(image.getInputStream(), image.getInputStream().available(), -1)
        .build());
//      qiniuUtils.upload(image, path + '/' + fileName);
      // 返回结果
      log.info("文件上传成功，{}", fileName);
      HashMap<String, String> result = new HashMap<>();
      result.put("name", fileName);
      result.put("url", minioProp.getEndpoint()+ '/'+minioProp.getBucket() +'/'+ path + '/' + fileName);
      return R.ok(result);
    } catch (Exception e) {
      log.error(e.toString());
      throw new ServiceException(ResultCode.Fail);
    }
  }
  //七牛云
//  @PostMapping("/image")
//  public R uploadImage(@RequestParam("file") MultipartFile image) {
//    try {
//      String fileName = UUID.randomUUID() + "." + StrUtil.subAfter(image.getOriginalFilename(), ".", true);
//      Date date = new Date(System.currentTimeMillis());
//      SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//      String time = formatter.format(date);
//      String path = time.replace('-', '/');
//      //上传
//      qiniuUtils.upload(image, path + '/' + fileName);
//      // 返回结果
//      log.info("文件上传成功，{}", fileName);
//      HashMap<String, String> result = new HashMap<>();
//      result.put("name", fileName);
//      result.put("url", QiniuUtils.url + path + '/' + fileName);
//      return R.ok(result);
//    } catch (Exception e) {
//      throw new BusinessException(ResultCode.Fail);
//    }
//  }
  //本地
//  @PostMapping("/image")
//  public R uploadImage(@RequestParam("file") MultipartFile image) {
//    try {
//      // 获取原始文件名称
//      String originalFilename = image.getOriginalFilename();
//      // 生成新文件名
//      String fileName = createNewFileName(originalFilename);
//      // 保存文件
//      image.transferTo(new File(SystemConstants.IMAGE_UPLOAD_DIR, fileName));
//      // 返回结果
//      log.info("文件上传成功，{}", fileName);
//      HashMap<String, String> result = new HashMap<>();
//      result.put("name",fileName);
//      result.put("url", SystemConstants.IMAGE_UPLOAD_DIR+fileName);
//      return R.ok(result);
//    } catch (Exception e) {
//      throw new BusinessException(ResultCode.Fail);
//    }
//  }
  
  @GetMapping("/image/delete")
  public R deleteBlogImg(@RequestParam("fileName") String fileName) {
    File file = new File(SystemConstants.IMAGE_UPLOAD_DIR, fileName);
    if (file.isDirectory()) {
      return R.fail(ResultCode.Fail);
    }
    boolean del = FileUtil.del(file);
    if (del) {
      log.info("文件删除成功，{}", fileName);
    }
    return R.ok();
  }
  
  private String createNewFileName(String originalFilename) {
    // 获取后缀
    String suffix = StrUtil.subAfter(originalFilename, ".", true);
    // 生成目录
    String name = UUID.randomUUID().toString();
    int hash = name.hashCode();
    int d1 = hash & 0xF;
    int d2 = (hash >> 4) & 0xF;
    // 判断目录是否存在
    File dir = new File(SystemConstants.IMAGE_UPLOAD_DIR, StrUtil.format("/{}/{}", d1, d2));
    if (!dir.exists()) {
      dir.mkdirs();
    }
    // 生成文件名
    return StrUtil.format("/{}/{}/{}.{}", d1, d2, name, suffix);
  }
}
