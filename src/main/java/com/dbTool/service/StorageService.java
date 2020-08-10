package com.dbTool.service;

import com.dbTool.param.MultipartFileParam;

/**
 * 存储操作的service
 */
public interface StorageService {

//  /**
//   * 删除全部数据
//   */
//  void deleteAll();
//
//  /**
//   * 初始化方法
//   */
//  void init();
//
//  /**
//   * 上传文件方法1
//   *
//   * @param param
//   * @throws IOException
//   */
//  void uploadFileRandomAccessFile(MultipartFileParam param) throws IOException;
//
//  /**
//   * 上传文件方法2
//   * 处理文件分块，基于MappedByteBuffer来实现文件的保存
//   *
//   * @param param
//   * @throws IOException
//   */
//  void uploadFileByMappedByteBuffer(MultipartFileParam param) throws IOException;


  String upload(MultipartFileParam param);


  String mergeFile(MultipartFileParam param) throws Exception;

}
