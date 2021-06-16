package com.rug.archedetector.util;

import com.rug.archedetector.model.Email;
import org.apache.commons.io.IOUtils;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;

import javax.mail.internet.MimeUtility;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class MboxContentHandler extends AbstractContentHandler {
    private Email email;

    private ZonedDateTime stringToDate(String dateString) {
        ZonedDateTime date = null;
        try {
            dateString = dateString.trim();
            if (dateString.charAt(4) == ' ' && dateString.charAt(5) == ' ') {
                dateString = dateString.substring(0, 4) + dateString.substring(5);
            }
            if (dateString.matches(".*[ ]\\(.*\\)$")) {
                dateString = dateString.substring(0, dateString.indexOf("(")).trim();
            }
            if (dateString.startsWith("RANDOM_")){
                dateString = dateString.replaceAll("RANDOM_", "");
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
        try {
            body = MimeUtility.decodeText(body);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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
        if(bd.getMimeType().equals("text/plain")) {
            String transferEncoding = bd.getTransferEncoding();
            Charset charset = Charset.forName(bd.getCharset());
            try {
                is = MimeUtility.decode(is, transferEncoding);
                String body = IOUtils.toString(is, charset).replaceAll("^\\[LineReaderInputStreamAdaptor: \\[pos: \\d+]\\[limit: \\d+]\\[", "");
                if(body.length() > 2) {
                    email.setBody(email.getBody() + "\n" + body.substring(0, body.length() - 2));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    public Email getMail() {
        return email;
    }

    public void setMail(Email email) {
        this.email = email;
    }
}