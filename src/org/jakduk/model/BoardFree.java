package org.jakduk.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class BoardFree {

	@Id  @GeneratedValue(strategy=GenerationType.AUTO)
	private String id;
	
//	@NotNull
	@Size(min = 1, message="Input writer")
	private String writer;
	
	@NotNull
	@Size(min = 1, message="Input subject")
	private String subject;
	
	@NotNull
	@Size(min = 1, message="Input contents")
	private String content;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "BoardFree [id=" + id + ", writer=" + writer + ", subject=" + subject + ", content=" + content + "]"; 
	}
}
