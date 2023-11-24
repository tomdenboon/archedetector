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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MailingListIndexer {
    private final String indexDir = "src/main/resources/index/mailingList/";
    final static public String threadIndexDir = "src/main/resources/index/mailingList/emailThread/";
    final static public String mailIndexDir = "src/main/resources/index/mailingList/email/";
    /**
     * We have to create a document for the email to be able to index with lucene
     *
     * @param email an email that is not null
     * @return a lucene document of the email object
     */
    private Document getEmailDocument(Email email) {
        Document doc = new Document();
        doc.add(new Field("id", Long.toString(email.getId()), TextField.TYPE_STORED));
        doc.add(new Field("sentFrom", email.getSentFrom(), TextField.TYPE_STORED));
        doc.add(new Field("subject", email.getSubject(), TextField.TYPE_STORED));
        doc.add(new Field("body", email.getBody(), TextField.TYPE_STORED));
        return doc;
    }

    /**
     * We also have to create a document of the email thread.
     *
     * @param emailThread an email thread
     * @param emails and its emails
     * @return the document of the email thread
     */
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

    /**
     * Index a mailing list. By adding both the threads and emails to their respective index directory.
     * This function needs the threads and emails to be in the same order otherwise it will produces
     * faulty indices. Needs refactoring.
     *
     * @param mailingList an apache mailing list
     * @param threads ordered list of the threads that belong to the mailing list
     * @param emails ordered list of the emails
     */
    public void index(MailingList mailingList, List<EmailThread> threads, List<Email> emails) {
        try {
            if(!Files.exists(Path.of("src/main/resources/index/"))){
                Files.createDirectory(Path.of("src/main/resources/index/"));
            }
            if(!Files.exists(Path.of(indexDir))){
                Files.createDirectory(Path.of(indexDir));
            }
            if(!Files.exists(Path.of(mailIndexDir))){
                Files.createDirectory(Path.of(mailIndexDir));
            }
            if(!Files.exists(Path.of(threadIndexDir))){
                Files.createDirectory(Path.of(threadIndexDir));
            }
            Path indexPathEmail = Files.createDirectory(Path.of(mailIndexDir + mailingList.getId()));
            Path indexPathThreads = Files.createDirectory(Path.of(threadIndexDir + mailingList.getId()));
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
