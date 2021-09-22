package com.rug.archedetector.util;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.MailingList;
import org.apache.lucene.util.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApacheMailingListParser {
    private static final Path MBOX_DIR = Path.of("mbox");

    /**
     * This function takes a mailing list object and finds all the mail from the apache mail repository.
     *
     * @param mailingList this mailing list has to be a mailing list from the apache foundation
     * @return All the emails found within that mailingList
     */
    public List<Email> getMailFromMailingList(MailingList mailingList) {
        MboxParser mboxParser = new MboxParser();
        Path mailingListDir = MBOX_DIR.resolve(String.valueOf(mailingList.getId()));
        try {
            Files.createDirectories(mailingListDir);
        } catch (IOException e) {
            System.err.println("Could not create " + mailingListDir);
            return List.of();
        }
        getMboxFilesFromApacheMailingList(mailingList.getUrl(), mailingListDir);
        List<Email> emails = new ArrayList<>();
        try (var stream = Files.list(mailingListDir)) {
            stream.filter(Files::isRegularFile).forEach(file -> emails.addAll(mboxParser.parseFile(file.toFile(), mailingList)));
        } catch (IOException e) {
            System.err.println("Could not obtain list of files under " + mailingListDir);
            return List.of();
        }
        try {
            System.gc();
            IOUtils.rm(mailingListDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return emails;
    }

    /**
     *  This function uses Jsoup to find all links in the html doc that end with mbox
     *  it will then remove all duplicates and download all the mbox files in to the specified
     *  folder.
     *
     * @param url an url to an apache mailing list example: http://mail-archives.apache.org/mod_mbox/hawq-dev/
     * @param outDir will create a folder filled with the mbox files found on the url
     */
    private void getMboxFilesFromApacheMailingList(String url, Path outDir) {
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
                FileOutputStream fos = new FileOutputStream(outDir.resolve(link).toFile());
                fos.getChannel().transferFrom(mboxRbc, 0, Long.MAX_VALUE);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
