package com.vas.springbatchexample.config;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;

import com.vas.springbatchexample.models.Log;

public class RecordFieldSetMapper implements FieldSetMapper<Log> {

	public Log mapFieldSet(FieldSet fieldSet) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		Date formattedDate;
		try {
			formattedDate = dateFormat.parse(fieldSet.readString(0));
		} catch (ParseException e) {
			throw new RuntimeException("Error while parsing 'data'");
		}
		return new Log(formattedDate, fieldSet.readString(1),
				fieldSet.readString(2),
				fieldSet.readShort(3), fieldSet.readString(4));
	}
}
