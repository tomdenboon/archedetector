package com.rug.archedetector.util;

import com.rug.archedetector.model.Email;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MboxContentHandler extends AbstractContentHandler {
    Email email;

    private ZonedDateTime stringToDate(String dateString) {
        ZonedDateTime date = null;
        try {
            dateString = dateString.trim();
            if (dateString.charAt(4) == ' ' && dateString.charAt(5) == ' ') {
                dateString = dateString.substring(0, 4) + dateString.substring(5);
            }
            if (dateString.matches(".*[ ]\\(\\w\\w\\w\\)$")) {
                dateString = dateString.substring(0, dateString.length() - 6);
            }
            DateTimeFormatter dateFormat = DateTimeFormatter.RFC_1123_DATE_TIME;
            date = ZonedDateTime.parse(dateString, dateFormat);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public void field(Field field) {
        String name = field.getName().toLowerCase();
        String body = field.getBody();
        switch (name) {
            case "from" -> email.setSentFrom(body);
            case "subject" -> email.setSubject(body);
            case "date" -> email.setDate(stringToDate(body));
            case "message-id" -> email.setMessageId(body);
            case "in-reply-to" -> email.setInReplyTo(body);
        }
    }

    @Override
    public void body(BodyDescriptor bd, InputStream is){
        String body = is.toString().replaceAll("^\\[LineReaderInputStreamAdaptor: \\[pos: \\d+]\\[limit: \\d+]\\[", "");
        email.setBody(body.substring(0, body.length() - 2));
    }

    public Email getMail() {
        return email;
    }

    public void setMail(Email email) {
        this.email = email;
    }
}