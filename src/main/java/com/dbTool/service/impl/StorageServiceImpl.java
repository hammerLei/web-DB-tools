package com.dbTool.service.impl;

import com.dbTool.UserHolder;
import com.dbTool.param.MultipartFileParam;
import com.dbTool.service.StorageService;
import com.dbTool.util.FileUtil;
import com.dbTool.util.ReturnJacksonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class StorageServiceImpl implements StorageService {

  private final Logger logger = LoggerFactory.getLogger(StorageServiceImpl.class);
  // 保存文件的根目录
  private Path rootPaht;

  @Autowired
  private UserHolder userHolder;

  //    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//  @Autowired
//  EhCacheCacheManager ehCacheCacheManager;

  //这个必须与前端设定的值一致
  @Value("${breakpoint.upload.chunkSize}")
  private long CHUNK_SIZE;

  @Value("${breakpoint.upload.dir}")
  private String finalDirPath;

  @Autowired
  public StorageServiceImpl(@Value("${breakpoint.upload.dir}") String location) {
    this.rootPaht = Paths.get(location);
  }

//  @Override
//  public void deleteAll() {
//    logger.info("开发初始化清理数据，start");
//    FileSystemUtils.deleteRecursively(rootPaht.toFile());
////        stringRedisTemplate.delete(Constant.FILE_UPLOAD_STATUS);
////        stringRedisTemplate.delete(Constant.FILE_MD5_KEY);
//    logger.info("开发初始化清理数据，end");
//  }

//  @Override
//  public void init() {
//    try {
//      Files.createDirectory(rootPaht);
//    } catch (FileAlreadyExistsException e) {
//      logger.error("文件夹已经存在了，不用再创建。");
//    } catch (IOException e) {
//      logger.error("初始化root文件夹失败。", e);
//    }
//  }

//  @Override
//  public void uploadFileRandomAccessFile(MultipartFileParam param) throws IOException {
//    String fileName = param.getName();
//    String tempDirPath = finalDirPath + param.getMd5();
//    String tempFileName = fileName + "_tmp";
//    File tmpDir = new File(tempDirPath);
//    File tmpFile = new File(tempDirPath, tempFileName);
//    if (!tmpDir.exists()) {
//      tmpDir.mkdirs();
//    }
//
//    RandomAccessFile accessTmpFile = new RandomAccessFile(tmpFile, "rw");
//    long offset = CHUNK_SIZE * param.getChunk();
//    //定位到该分片的偏移量
//    accessTmpFile.seek(offset);
//    //写入该分片数据
//    accessTmpFile.write(param.getFile().getBytes());
//    // 释放
//    accessTmpFile.close();
//
//    boolean isOk = checkAndSetUploadProgress(param, tempDirPath);
//    if (isOk) {
//      boolean flag = renameFile(tmpFile, fileName);
//      System.out.println("upload complete !!" + flag + " name=" + fileName);
//    }
//  }

//  /**
//   * 检查并修改文件上传进度
//   *
//   * @param param
//   * @param uploadDirPath
//   * @return
//   * @throws IOException
//   */
//  private boolean checkAndSetUploadProgress(MultipartFileParam param, String uploadDirPath) throws IOException {
//    String fileName = param.getName();
//    File confFile = new File(uploadDirPath, fileName + ".conf");
//    RandomAccessFile accessConfFile = new RandomAccessFile(confFile, "rw");
//    //把该分段标记为 true 表示完成
//    System.out.println("set part " + param.getChunk() + " complete");
//    accessConfFile.setLength(param.getChunks());
//    accessConfFile.seek(param.getChunk());
//    accessConfFile.write(Byte.MAX_VALUE);
//
//    //completeList 检查是否全部完成,如果数组里是否全部都是(全部分片都成功上传)
//    byte[] completeList = FileUtils.readFileToByteArray(confFile);
//    byte isComplete = Byte.MAX_VALUE;
//    for (int i = 0; i < completeList.length && isComplete == Byte.MAX_VALUE; i++) {
//      //与运算, 如果有部分没有完成则 isComplete 不是 Byte.MAX_VALUE
//      isComplete = (byte) (isComplete & completeList[i]);
//      System.out.println("check part " + i + " complete?:" + completeList[i]);
//    }
//
//    accessConfFile.close();
//    if (isComplete == Byte.MAX_VALUE) {
////            stringRedisTemplate.opsForHash().put(Constant.FILE_UPLOAD_STATUS, param.getMd5(), "true");
////            stringRedisTemplate.opsForValue().set(Constant.FILE_MD5_KEY + param.getMd5(), uploadDirPath + "/" + fileName);
//      return true;
//    } else {
////            if (!stringRedisTemplate.opsForHash().hasKey(Constant.FILE_UPLOAD_STATUS, param.getMd5())) {
////                stringRedisTemplate.opsForHash().put(Constant.FILE_UPLOAD_STATUS, param.getMd5(), "false");
////            }
////            if (stringRedisTemplate.hasKey(Constant.FILE_MD5_KEY + param.getMd5())) {
////                stringRedisTemplate.opsForValue().set(Constant.FILE_MD5_KEY + param.getMd5(), uploadDirPath + "/" + fileName + ".conf");
////            }
//      return false;
//    }
//  }

  /**
   * 文件重命名
   *
   * @param toBeRenamed   将要修改名字的文件
   * @param toFileNewName 新的名字
   * @return
   */
  public boolean renameFile(File toBeRenamed, String toFileNewName) {
    //检查要重命名的文件是否存在，是否是文件
    if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
      logger.info("File does not exist: " + toBeRenamed.getName());
      return false;
    }
    String p = toBeRenamed.getParent();
    File newFile = new File(p + File.separatorChar + toFileNewName);
    //修改文件名
    return toBeRenamed.renameTo(newFile);
  }


//  @Override
//  public void uploadFileByMappedByteBuffer(MultipartFileParam param) throws IOException {
//    String fileName = param.getName();
//    String uploadDirPath = finalDirPath + param.getMd5();
//    String tempFileName = fileName + "_tmp";
//    File tmpDir = new File(uploadDirPath);
//    File tmpFile = new File(uploadDirPath, tempFileName);
//    if (!tmpDir.exists()) {
//      tmpDir.mkdirs();
//    }
//
//    RandomAccessFile tempRaf = new RandomAccessFile(tmpFile, "rw");
//    FileChannel fileChannel = tempRaf.getChannel();
//
//    //写入该分片数据
//    long offset = CHUNK_SIZE * param.getChunk();
//    byte[] fileData = param.getFile().getBytes();
//    MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, fileData.length);
//    mappedByteBuffer.put(fileData);
//    // 释放
//    FileMD5Util.freedMappedByteBuffer(mappedByteBuffer);
//    fileChannel.close();
//
//    boolean isOk = checkAndSetUploadProgress(param, uploadDirPath);
//    if (isOk) {
//      boolean flag = renameFile(tmpFile, fileName);
//      System.out.println("upload complete !!" + flag + " name=" + fileName);
//    }
//  }

  /**
   * 文件上传
   *
   * @return
   */
  @Override
  public String upload(MultipartFileParam param) {
    int chunks = param.getChunks();
    if (chunks <= 1) {// 没有分片
      return UnChunkUpload(param);
    } else {// 分片
      return ChunkUpload(param);
    }
  }

  /**
   * 分片上传
   *
   * @return
   */
  public String ChunkUpload(MultipartFileParam param) {
    String fileName = param.getName();
    int chunk = param.getChunk();// 当前片

    // 分片目录创建
    String chunkDirPath = finalDirPath + File.separator + userHolder.getUserInfo().getAccount() + File.separator + fileName + "_fragment";
    File chunkDir = new File(chunkDirPath);
    if (!chunkDir.exists()) {
      chunkDir.mkdirs();
    }
    // 分片文件上传
    String chunkFileName = chunkDirPath + File.separator + String.valueOf(chunk);
//    String chunkFilePath = chunkDir + File.separator + chunkFileName;
    File chunkFile = new File(chunkFileName);
    try {
      param.getFile().transferTo(chunkFile);
      return ReturnJacksonUtil.resultOk(String.format("%s:分片%s上传成功！", fileName, chunk));
    } catch (Exception e) {
      logger.error(String.format("%s分片%s上传出错：", fileName, chunk), e);
      return ReturnJacksonUtil.resultFail(String.format("分片%s上传出错", chunk));
    }
  }

  /**
   * 未分片上传
   *
   * @return
   */
  public String UnChunkUpload(MultipartFileParam param) {
    String fileName = param.getName();
    // String fileMd5 = uploadVO.getFileMd5();
    // 文件上传
    File destFile = new File(finalDirPath + File.separator + userHolder.getUserInfo().getAccount() + File.separator + fileName);
    if (param.getFile() != null && !param.getFile().isEmpty()) {
      // 上传目录
      File fileDir = new File(finalDirPath);
      if (!fileDir.exists()) {
        fileDir.mkdirs();
      }
      if (destFile.exists()) {
        destFile.delete();
      }
      try {
        param.getFile().transferTo(destFile);
        return ReturnJacksonUtil.resultOk(String.format("%s上传成功！", fileName));
      } catch (Exception e) {
        logger.error(String.format("%s上传出错：", fileName), e);
        return ReturnJacksonUtil.resultFail(String.format("%s上传出错", fileName));
      }
    }
    return ReturnJacksonUtil.resultFail(String.format("%s上传出错", fileName));
  }

  public String mergeFile(MultipartFileParam param) throws Exception {


    String fileName = param.getName();
    int chunks = param.getChunks();// 总共多少片

    // 分片目录创建
    String chunkDirPath = finalDirPath + File.separator + userHolder.getUserInfo().getAccount() + File.separator + fileName + "_fragment";
    File chunkDir = new File(chunkDirPath);
    if (!chunkDir.exists()) {
      return ReturnJacksonUtil.resultFail(String.format("%s分片不存在", fileName));
    }

    Set<File> files = new LinkedHashSet<File>();
    for (int i = 0; i < chunks; i++) {
      File chunkFile = new File(chunkDirPath + File.separator + i);
      if (chunkFile.exists()) {
        files.add(chunkFile);
      }
    }

    String filePath = finalDirPath + File.separator + userHolder.getUserInfo().getAccount();

    if (chunks == files.size()) { //文件合并
      logger.info("begin merge file " + fileName);
      File mergeFile = new File(filePath + File.separator + fileName);
      FileOutputStream writer = new FileOutputStream(mergeFile);
      long time = new Date().getTime();
      int length = 2097152;
      byte buffer[] = new byte[length];
      for (File chunkFile : files) {
        FileInputStream reader = new FileInputStream(chunkFile);
        boolean flag = true;
        while (flag) {
          int ins = reader.read(buffer);
          if (ins == -1) {
            reader.close();
            writer.flush();
            time = new Date().getTime() - time;
            flag = false;
          } else {
            writer.write(buffer, 0, ins);
          }
        }
      }
      writer.close();
      logger.info("end merge file " + fileName + " time = " + time);

      boolean deleteFile = FileUtil.deleteDir(chunkDir);
//      if (deleteFile) {
//        renameFile(mergeFile, fileName);
//      }
      return ReturnJacksonUtil.resultOk(String.format("%s分片合并成功", fileName));
    } else {
      return ReturnJacksonUtil.resultFail(String.format("%s分片数量缺失", fileName));
    }
  }

}
