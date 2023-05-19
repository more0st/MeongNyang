package com.mail;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
	private String mailType;
	private String encType;
	
	public MailSender() {
		mailType="text/html; charset=utf-8";
		encType="utf-8";
	}
	
	private class SMTPAuthenticator extends javax.mail.Authenticator{
		@Override
		protected PasswordAuthentication getPasswordAuthentication() {
			//권한 설정
			String username="sunyou51";			 //gmail은 아이디만 기술
			//String username="아이디@naver.com";	 //네이버
			//네이버는 메일 환경 설정 아래부분에서 POP3 허용
			String password="cfwzygeyjqjgqmhk";
			
			return new PasswordAuthentication(username, password);
		}
	}
	
	public boolean mailSend(Mail dto) {
		boolean b= false;
		
		Properties p=new Properties();
		
		//SMTP 서버의 계정 설정
		p.put("mail.smtp.super", "sunyou51");//아이디
		
		//SMTP 서버 정보 설정
		String host="smtp.gmail.com";
		//String host="smtp.naver.com";//네이버
		p.put("mail.smtp.host", host);
		
		//기타 설정
		p.put("mail.smtp.port","465");
		p.put("mail.smtp.starttls.enable","true");
		p.put("mail.smtp.auth","true");
		p.put("mail.smtp.socketFactory.port","465");
		p.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");
		p.put("mail.smtp.socketFactory.fallback","false");
		
		p.put("mail.smtp.ssl.enable","true");
		p.put("mail.smtp.ssl.trust",host);
		
		try {
			Authenticator auth=new SMTPAuthenticator();
			Session session=Session.getDefaultInstance(p, auth);
			
			//메일 전송시 상세 정보 콘솔에 출력 여부
			session.setDebug(true);
			
			Message msg=new MimeMessage(session);
			
			//보내는 사람
			if(dto.getSenderName()==null || dto.getSenderName().equals("")) {
				msg.setFrom(new InternetAddress(dto.getSenderName()));
			} else {
				msg.setFrom(new InternetAddress(dto.getSenderEmail(), 
						dto.getSenderName(), encType));
			}
			//받는 사람
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(dto.getReceiverEmail()));
			
			//메일 타입
			msg.setHeader("Content-Type", mailType);
			
			//보내는 사람 이름
	         msg.setHeader("X-Mailer", dto.getSenderName());
	         
	         //메일 보낸 날짜
	         msg.setSentDate(new Date());
	         
	         //제목 
	         msg.setSubject(dto.getSubject());
	         
	         //내용 
	         msg.setContent(dto.getContent(), "text/html; charset=utf-8");
	         
	         //메일 전송
	         Transport.send(msg);
	         
	         b = true;
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return b;
	}
	
}
