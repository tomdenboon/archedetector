package com.rug.archedetector.lucene;

import com.rug.archedetector.model.Email;
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
import java.util.List;

public class MailingListIndexer {
    final private String indexDir = "src/main/resources/index/mailingList/";

    private Document getDocument(Email email) {
        String body = email.getBody();
        try {
            Reader inputString = new StringReader(body);
            BufferedReader br = new BufferedReader(inputString);
            String line = "";
            StringBuilder finalBody = new StringBuilder();
            while ((line = br.readLine()) != null) {
                if(!line.startsWith(">")){
                    finalBody.append(line);
                }
            }
            body = finalBody.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        Document doc = new Document();
        doc.add(new Field("id", Long.toString(email.getId()), TextField.TYPE_STORED));
        doc.add(new Field("sentFrom", email.getSentFrom(), TextField.TYPE_STORED));
        doc.add(new Field("subject", email.getSubject(), TextField.TYPE_STORED));
        doc.add(new Field("body", body, TextField.TYPE_STORED));
        return doc;
    }

    public void index(MailingList mailingList, List<Email> emails) {
        try {
            Path indexPath = Files.createDirectory(Path.of(indexDir + mailingList.getId()));
            Directory directory = FSDirectory.open(indexPath);
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(directory, config);
            for (Email e : emails) {
                writer.addDocument(getDocument(e));
            }
            writer.close();
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
