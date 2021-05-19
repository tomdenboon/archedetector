package com.rug.archedetector.util;

import com.rug.archedetector.model.Mail;
import org.apache.james.mime4j.MimeException;
import org.apache.james.mime4j.parser.AbstractContentHandler;
import org.apache.james.mime4j.stream.BodyDescriptor;
import org.apache.james.mime4j.stream.Field;

import java.io.IOException;
import java.io.InputStream;

public class MboxContentHandler extends AbstractContentHandler {
    Mail mail;

    @Override
    public void field(Field field) throws MimeException {
        String name = field.getName().toLowerCase();
        String body = field.getBody();
        switch (name) {
            case "from" -> mail.setSentFrom(body);
            case "to" -> mail.setSentTo(body);
            case "subject" -> mail.setSubject(body);
            case "date" -> mail.setDate(body);
            case "message-id" -> mail.setMessageId(body);
            case "in-reply-to" -> mail.setInReplyTo(body);
        }
    }

    @Override
    public void body(BodyDescriptor bd, InputStream is) throws MimeException, IOException {
        String body = is.toString().replaceAll("^\\[LineReaderInputStreamAdaptor: \\[pos: \\d+]\\[limit: \\d+]\\[", "");
        mail.setBody(body.substring(0, body.length()-2));
    }

    public Mail getMail() {
        return mail;
    }

    public void setMail(Mail mail) {
        this.mail = mail;
    }
}