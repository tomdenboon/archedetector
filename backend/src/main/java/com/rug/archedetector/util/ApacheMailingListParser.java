package com.rug.archedetector.util;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.MailingList;
import org.apache.lucene.util.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApacheMailingListParser {
    /**
     * This function takes a mailing list object and finds all the mail from the apache mail repository.
     *
     * @param mailingList this mailing list has to be a mailing list from the apache foundation
     * @return All the emails found within that mailingList
     */
    public List<Email> getMailFromMailingList(MailingList mailingList) {
        MboxParser mboxParser = new MboxParser();
        String uri = "src/main/resources/mbox/" + mailingList.getId();
        Path mboxPath = Paths.get(uri);
        try {
            if (!Files.exists(mboxPath)) {
                Files.createDirectory(mboxPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not create folder");
        }
        getMboxFilesFromApacheMailingList(mailingList.getUrl(), uri);
        File mboxFolder = new File(uri);
        List<Email> emails = new ArrayList<>();
        for (File file : Objects.requireNonNull(mboxFolder.listFiles())) {
            if (file.isFile()) {
                emails.addAll(mboxParser.parseFile(file, mailingList));
            }
        }
        try {
            System.gc();
            IOUtils.rm(mboxPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("not deleting");
        }
        return emails;
    }

    /**
     *  This function uses Jsoup to find all links in the html doc that end with mbox
     *  it will then remove all duplicates and download all the mbox files in to the specified
     *  folder.
     *
     * @param url an url to an apache mailing list example: http://mail-archives.apache.org/mod_mbox/hawq-dev/
     * @param outFolder will create a folder filled with the mbox files found on the url
     */
    private void getMboxFilesFromApacheMailingList(String url, String outFolder) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements links = doc.select("td.links").select("a[href]");
            ArrayList<String> mboxList = new ArrayList<>();
            for (Element link : links) {
                Pattern pattern = Pattern.compile("\\d+.mbox");
                Matcher matcher = pattern.matcher(link.toString());
                if (matcher.find()) {
                    mboxList.add(matcher.group(0));
                }
            }
            Set<String> mboxLinks = new LinkedHashSet<>(mboxList);
            for (String link : mboxLinks) {
                URL mbox = new URL(url + link);
                ReadableByteChannel mboxRbc = Channels.newChannel(mbox.openStream());
                FileOutputStream fos = new FileOutputStream(outFolder + "/" + link);
                fos.getChannel().transferFrom(mboxRbc, 0, Long.MAX_VALUE);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
