package com.dbTool.util;

import com.dbTool.exception.MsgException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.List;

public class FileUtil {


  public static final String WEB_INF_STATIC = "/WEB-INF/static";
  public static final String SITE_VIEW_PATH = "/WEB-INF/views";
  private static String appHome = System.getProperty("user.dir");

  private static Log LOG = LogFactory.getLog(FileUtil.class);


  public static List<String> readViews(File file) {
    try {
      return IOUtils.readLines(new FileInputStream(file), "utf-8");
    } catch (IOException e) {
      throw new MsgException("read file error:" + file, e);
    }
  }

  public static String readFile(File file) {
    try {
      return FileUtils.readFileToString(file, "UTF-8");
    } catch (IOException e) {
      throw new MsgException("read file error:" + file, e);
    }
  }

  public static boolean hasJsp(String viewName) {
    return getViewsFile(viewName + ".jsp", SITE_VIEW_PATH).exists();
  }

  public static File getViewsFile(String viewName, String rootPath) {

    String pathname = appHome + rootPath +
        (viewName.charAt(0) == '/' ? "" : "/") + viewName;
    LOG.info("SpringUtil load view:" + pathname);
    return new File(pathname);
  }



  public static List<String> readViewFile(String fileName) {
    return readViews(getStaticFile(fileName));
  }

  public static String readFile(String fileName) {
    return readFile(getStaticFile(fileName));
  }

  public static File getStaticFile(String fileName) {
    return getViewsFile(fileName, WEB_INF_STATIC);
  }

  public static String readFileByNamePath(String fileName, String rootPath) {
    return readFile(getViewsFile(fileName, rootPath));
  }

  public static String readFileByNamePathIgnoreExist(String fileName, String rootPath) {
    if(!hasFile(fileName,rootPath)){
      return "";
    }
    return readFile(getViewsFile(fileName, rootPath));
  }

  public static void createDir(String dirPath){
    File chunkDir = new File(appHome+dirPath);
    if (!chunkDir.exists()) {
      chunkDir.mkdirs();
    }
  }

  public static boolean fileWrite(String viewName, String rootPath, String str) {
    File file = getViewsFile(viewName,rootPath);
    return writeFile(file,str);
  }



  public static boolean writeFile(File file, String str) {
    boolean result = false;
    Writer writer = null;
    try {
      writer = new FileWriter(file);
      writer.write(str);
      writer.flush();
      result = true;
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        writer.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return result;
  }

  public static boolean hasFile(String viewName, String rootPath){
    File file = getViewsFile(viewName,rootPath);
    return isFileExists(file);
  }

   // 判断文件是否存在
   public static boolean isFileExists(File file) {

       if (file.exists()) {
         return true;
       } else {
         return false;
       }
   }

   // 判断文件夹是否存在
   public static boolean isDirExists(File file) {

       if (file.exists()) {
           if (file.isDirectory()) {
             return true;
           } else {
             return false;
           }
       }
       return false;
   }


  public void download(HttpServletRequest request, HttpServletResponse response){
    String fileName = request.getParameter("fileid");
    try {
      request.setCharacterEncoding("utf-8");
      fileName = new String(fileName.getBytes("iso-8859-1"), "utf-8");
      //获取文件路径
      String filePath = "E:\\THSPlatform\\projects\\serviceGuide-archetype\\src\\main\\webapp\\WEB-INF\\conf\\res\\servicecontent\\baseword\\"+fileName;
      filePath = filePath == null ? "" : filePath;
      //设置向浏览器端传送的文件格式
      response.setContentType("application/x-download");

      fileName = URLEncoder.encode(fileName, "UTF-8");
      response.addHeader("Content-Disposition", "attachment;filename="
          + fileName);
      FileInputStream fis = null;
      OutputStream os = null;
      try {
        os = response.getOutputStream();
        fis = new FileInputStream(filePath);
        byte[] b = new byte[1024 * 10];
        int i = 0;
        while ((i = fis.read(b)) > 0) {
          os.write(b, 0, i);
        }
        os.flush();
        os.close();
      } catch (Exception e) {
        e.printStackTrace();
      } finally {
        if (fis != null) {
          try {
            fis.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        if (os != null) {
          try {
            os.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }


  /**
   * 删除空目录
   * @param dir 将要删除的目录路径
   */
  public static void doDeleteEmptyDir(String dir) {
    boolean success = (new File(dir)).delete();
    if (success) {
      System.out.println("Successfully deleted empty directory: " + dir);
    } else {
      System.out.println("Failed to delete empty directory: " + dir);
    }
  }

  /**
   * 递归删除目录下的所有文件及子目录下所有文件
   * @param dir 将要删除的文件目录
   * @return boolean Returns "true" if all deletions were successful.
   *                 If a deletion fails, the method stops attempting to
   *                 delete and returns "false".
   */
  public static boolean deleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      //递归删除目录中的子目录下
      for (int i=0; i<children.length; i++) {
        boolean success = deleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    // 目录此时为空，可以删除
    return dir.delete();
  }

   public static void main(String args []){
     System.out.println(hasFile("feifeitest.com.csr","/ssl/feifeitest.com/"));
   }

}
