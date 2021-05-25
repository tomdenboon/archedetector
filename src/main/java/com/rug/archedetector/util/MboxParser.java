package com.rug.archedetector.util;

import com.rug.archedetector.model.Mail;
import com.rug.archedetector.model.MailingList;
import org.apache.james.mime4j.mboxiterator.CharBufferWrapper;
import org.apache.james.mime4j.mboxiterator.MboxIterator;
import org.apache.james.mime4j.parser.MimeStreamParser;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MboxParser {
    public ArrayList<Mail> parseFileAndDelete(File mbox, MailingList mailingList){
        ArrayList<Mail> mails = new ArrayList<>();
        try{
            MboxContentHandler handler = new MboxContentHandler();
            MimeStreamParser parser = new MimeStreamParser();
            parser.setContentHandler(handler);
            MboxIterator mboxIterator = MboxIterator.fromFile(mbox).charset(StandardCharsets.ISO_8859_1).build();
            for (CharBufferWrapper s : mboxIterator) {
                Mail mail = new Mail();
                mail.setRaw(s.toString());
                mail.setMailingList(mailingList);
                handler.setMail(mail);
                InputStream is = s.asInputStream(Charset.defaultCharset());
                parser.parse(is);
                is.close();
                if(mail.getMessageId() != null){
                    mails.add(handler.getMail());
                }
            }
            mboxIterator.close();
        }catch(Exception e){
            e.printStackTrace();
            System.out.println(mbox.getAbsolutePath());
        }
        System.gc();
        if(!mbox.delete()){
            System.out.print("files not deleting");
        }
        return mails;
    }
}
