package com.rug.archedetector.util;

import com.rug.archedetector.model.Mail;
import com.rug.archedetector.model.MailingList;
import org.apache.james.mime4j.MimeException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApacheMailingListParser {
    public List<Mail> getMailFromMailingList(MailingList mailingList){
        MboxParser mboxParser = new MboxParser();
        String mboxPath = "src/main/resources/mbox/";
        getMboxFilesFromApacheMailingList(mailingList.getUrl(), mboxPath);
        File mboxFolder = new File(mboxPath);
        List<Mail> mail = new ArrayList<>();
        for (File file : Objects.requireNonNull(mboxFolder.listFiles())) {
            if (file.isFile()) {
                mail.addAll(mboxParser.parseFileAndDelete(file, mailingList));
            }
        }
        return mail;
    }

    public void getMboxFilesFromApacheMailingList(String url, String outFolder){
        try{
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("td.links").select("a[href]");
            ArrayList<String> mboxList = new ArrayList<>();
            for(Element link : links){
                Pattern pattern = Pattern.compile("\\d+.mbox");
                Matcher matcher = pattern.matcher(link.toString());
                if (matcher.find()) {
                    mboxList.add(matcher.group(0));
                }
            }
            Set<String> mboxLinks = new LinkedHashSet<>(mboxList);
            for(String link : mboxLinks) {
                URL mbox = new URL(url+link);
                ReadableByteChannel mboxRbc = Channels.newChannel(mbox.openStream());
                FileOutputStream fos = new FileOutputStream(outFolder+link);
                fos.getChannel().transferFrom(mboxRbc, 0, Long.MAX_VALUE);
                fos.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }
}
