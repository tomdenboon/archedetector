package com.rug.archedetector.lucene;

import com.rug.archedetector.model.Email;
import com.rug.archedetector.model.EmailThread;
import com.rug.archedetector.model.MailingList;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.IOUtils;

import java.io.BufferedReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MailingListIndexer {
    final private String indexDir = "src/main/resources/index/mailingList/";

    private Document getEmailDocument(Email email) {
        Document doc = new Document();
        doc.add(new Field("id", Long.toString(email.getId()), TextField.TYPE_STORED));
        doc.add(new Field("sentFrom", email.getSentFrom(), TextField.TYPE_STORED));
        doc.add(new Field("subject", email.getSubject(), TextField.TYPE_STORED));
        doc.add(new Field("body", email.getBody(), TextField.TYPE_STORED));
        return doc;
    }

    private Document getEmailThreadDocument(EmailThread emailThread, List<Email> emails) {
        Document doc = new Document();
        StringBuilder sentFrom = new StringBuilder();
        StringBuilder subject = new StringBuilder();
        StringBuilder body = new StringBuilder();
        for(Email email : emails){
            sentFrom.append(email.getSentFrom()).append("\n");
            subject.append(email.getSubject()).append("\n");
            body.append(email.getBody()).append("\n");
        }
        doc.add(new Field("id", Long.toString(emailThread.getId()), TextField.TYPE_STORED));
        doc.add(new Field("sentFrom", sentFrom.toString(), TextField.TYPE_STORED));
        doc.add(new Field("subject", subject.toString(), TextField.TYPE_STORED));
        doc.add(new Field("body", body.toString(), TextField.TYPE_STORED));
        return doc;
    }

    public void index(MailingList mailingList, List<EmailThread> threads, List<Email> emails) {
        try {
            if(!Files.exists(Path.of("src/main/resources/index/"))){
                Files.createDirectory(Path.of("src/main/resources/index/"));
            }
            if(!Files.exists(Path.of(indexDir))){
                Files.createDirectory(Path.of(indexDir));
            }
            Files.createDirectory(Path.of(indexDir + mailingList.getId()));
            Path indexPathEmail = Files.createDirectory(Path.of(indexDir + mailingList.getId() + "/email"));
            Path indexPathThreads = Files.createDirectory(Path.of(indexDir + mailingList.getId() + "/emailThread"));
            Directory directoryEmail = FSDirectory.open(indexPathEmail);
            Directory directoryThreads = FSDirectory.open(indexPathThreads);
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config1 = new IndexWriterConfig(analyzer);
            IndexWriterConfig config2 = new IndexWriterConfig(analyzer);
            IndexWriter writerEmail = new IndexWriter(directoryEmail, config1);
            IndexWriter writerThreads = new IndexWriter(directoryThreads, config2);

            int i = 0;
            for(EmailThread thread : threads){
                List<Email> threadEmails = new ArrayList<>();
                while(i < emails.size() && emails.get(i).getEmailThread().getId() == thread.getId()){
                    Email email = emails.get(i);
                    threadEmails.add(email);
                    writerEmail.addDocument(getEmailDocument(email));
                    i++;
                }
                writerThreads.addDocument(getEmailThreadDocument(thread, threadEmails));
            }
            writerThreads.close();
            writerEmail.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteIndex(MailingList mailingList) {
        try {
            Path indexPath = Path.of(indexDir + mailingList.getId());
            IOUtils.rm(indexPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Index not deleting");
        }
    }
}
