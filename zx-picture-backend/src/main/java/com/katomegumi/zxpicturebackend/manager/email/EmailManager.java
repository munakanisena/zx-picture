package com.katomegumi.zxpicturebackend.manager.email;

import com.katomegumi.zxpicturebackend.common.constant.CacheConstant;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * @author : lr
 * @description : 邮件发送客户端
 * @createDate : 2025/5/3 下午7:37
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailManager {

    private final JavaMailSender javaMailSender;

    private final TemplateEngine  templateEngine;


    //发件人名称
    @Value("${spring.mail.nickname}")
    private String nickname;
    //发件人地址
    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮件注册验证码
     * @param to 收件人
     */
    @Async(value = "emailThreadPool")
    public void sendEmailCaptcha(String to,String captcha) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(nickname + "<" + from + ">");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("惠眸图界 - 注册验证码");
            //获取邮件模版
            Context context = new Context();
            context.setVariable(CacheConstant.EMAIL.GET_CAPTCHA, captcha);
            String htmlContent = templateEngine.process("RegisterTemplate.html", context);
            mimeMessageHelper.setText(htmlContent,true);
            javaMailSender.send(mimeMessage);
            log.info("邮件发送成功: to={} captcha={}",to,captcha);
        } catch (MessagingException e) {
            //考虑补偿机制
            log.error("邮件发送失败: to={}, error={}", to, e.getMessage());
        }
    }

    /**
     * 重置用户密码
     * @param to 收件人
     */
    @Async(value = "emailThreadPool")
    public void sendEmailForgotPassword(String to,String captcha) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(nickname + "<" + from + ">");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("惠眸图界 - 重置密码");
            //获取邮件模版
            Context context = new Context();
            context.setVariable(CacheConstant.EMAIL.GET_CAPTCHA, captcha);
            String htmlContent = templateEngine.process("ForgotPasswordTemplate.html", context);
            mimeMessageHelper.setText(htmlContent,true);
            javaMailSender.send(mimeMessage);
            log.info("邮件发送成功: to={} captcha={}",to,captcha);
        } catch (MessagingException e){
            log.error("邮件发送失败: to={}, error={}", to, e.getMessage());
    }
    }
}

