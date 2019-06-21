package com.vas.springbatchexample.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Log {
	private Date data;
	private String ip;
	private String request;
	private short status;
	private String userAgent;
}
