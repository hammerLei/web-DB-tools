package com.dbTool.util;

import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

@Component
public class MailUtil {
  //以下常量配置自行更改
  private static final String HOST = "mail.xxx.com";
  private static final Integer PORT = 465;
  private static final String USERNAME = "xxx";
  private static final String PASSWORD = "xxxx";
  private static final String EMAILFORM = "xxxx@xxx.com";
  private static JavaMailSenderImpl mailSender = createMailSender();

  /**
   * 邮件发送器
   *
   * @return 配置好的工具
   */
  private static JavaMailSenderImpl createMailSender() {
    JavaMailSenderImpl sender = new JavaMailSenderImpl();
    sender.setHost(HOST);
    sender.setPort(PORT);
    sender.setUsername(USERNAME);
    sender.setPassword(PASSWORD);
    sender.setDefaultEncoding("Utf-8");
    Properties p = new Properties();
    p.setProperty("mail.smtp.timeout", "10000");
    p.setProperty("mail.smtp.auth", "true");
    sender.setJavaMailProperties(p);
    return sender;
  }

  /**
   * 发送邮件
   *
   * @param to      接受人
   * @param subject 主题
   * @param html    发送内容
   * @throws MessagingException           异常
   * @throws UnsupportedEncodingException 异常
   */
  public static void sendHtmlMail(String to[], String subject, String fromName, String html) throws MessagingException, UnsupportedEncodingException {

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    // 设置utf-8或GBK编码，否则邮件会有乱码
    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    messageHelper.setFrom(EMAILFORM, fromName);
    messageHelper.setTo(to);
    messageHelper.setSubject(subject);
    messageHelper.setText(html, true);
    mailSender.send(mimeMessage);
  }

  /**
   * 发送邮件
   *
   * @param mailMap 收件人与邮件内容集合
   * @throws MessagingException 异常
   */
  public static void sendHtmlMail(Map<String, String> mailMap) throws MessagingException {
    MimeMessage mimeMessage = mailSender.createMimeMessage();
    // 设置utf-8编码
    MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
    messageHelper.setFrom(EMAILFORM);
    Iterator<String> iterator = mailMap.keySet().iterator();
    while (iterator.hasNext()) {
      messageHelper.setTo(iterator.next());
      //messageHelper.setSubject(subject);
      messageHelper.setText(mailMap.get(iterator.next()), true);
      mailSender.send(mimeMessage);
    }
  }
}
