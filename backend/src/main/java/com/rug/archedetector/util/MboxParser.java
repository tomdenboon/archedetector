package com.rug.archedetector.util;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.MailingList;
import org.apache.james.mime4j.mboxiterator.CharBufferWrapper;
import org.apache.james.mime4j.mboxiterator.MboxIterator;
import org.apache.james.mime4j.parser.MimeStreamParser;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MboxParser {
    /**
     * Returns a list of Email parsed from the specified mbox file it will
     * also set the mailing list within the email.
     *
     * @param mbox only an mbox file will work
     * @param mailingList specifies to which mailing list these emails belong
     * @return a list of Email parsed from an mbox file
     * @see Email
     */
    public List<Email> parseFile(File mbox, MailingList mailingList) {
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
                email.setBody("");
                handler.setMail(email);
                InputStream is = s.asInputStream(Charset.defaultCharset());
                parser.parse(is);
                is.close();
                email = handler.getMail();
                if(email.getSubject() == null){
                    email.setSubject("[No Subject]");
                }
                if (email.getMessageId() != null &&
                    email.getDate() != null) {
                    if(email.getBody().contains("\u0000")){
                        System.out.println(email.getDate());
                        System.out.println();
                    }
                    email.setBody(email.getBody().replaceAll("\u0000", ""));
                    emails.add(email);
                } else {
                    System.out.println("No date or message id");
                    System.out.println(email.getRaw());
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
