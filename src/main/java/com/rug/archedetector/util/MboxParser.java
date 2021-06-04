package com.rug.archedetector.util;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.MailingList;
import org.apache.james.mime4j.mboxiterator.CharBufferWrapper;
import org.apache.james.mime4j.mboxiterator.MboxIterator;
import org.apache.james.mime4j.parser.MimeStreamParser;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MboxParser {
    public ArrayList<Email> parseFile(File mbox, MailingList mailingList) {
        ArrayList<Email> emails = new ArrayList<>();
        try {
            MboxContentHandler handler = new MboxContentHandler();
            MimeStreamParser parser = new MimeStreamParser();
            parser.setContentHandler(handler);
            MboxIterator mboxIterator = MboxIterator.fromFile(mbox).charset(StandardCharsets.ISO_8859_1).build();
            for (CharBufferWrapper s : mboxIterator) {
                Email email = new Email();
                email.setRaw(s.toString());
                email.setMailingList(mailingList);
                handler.setMail(email);
                InputStream is = s.asInputStream(Charset.defaultCharset());
                parser.parse(is);
                is.close();
                if (email.getMessageId() != null) {
                    emails.add(handler.getMail());
                }
            }
            mboxIterator.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(mbox.getAbsolutePath());
        }
        return emails;
    }
}
