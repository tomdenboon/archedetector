package com.rug.archedetector.lucene;

import com.rug.archedetector.model.*;
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

public class IssueListIndexer {
    final private String indexDir = "src/main/resources/index/issueList/";

    private Document getDocument(Issue issue, List<Comment> comments) {
        Document doc = new Document();
        StringBuilder commentBodies = new StringBuilder();
        for(Comment c : comments){
            commentBodies.append(c.getBody()).append(" ");
        }
        doc.add(new Field("id", Long.toString(issue.getId()), TextField.TYPE_STORED));
        doc.add(new Field("summary", issue.getSummary(), TextField.TYPE_STORED));
        doc.add(new Field("description", issue.getDescription(), TextField.TYPE_STORED));
        doc.add(new Field("comments", commentBodies.toString(), TextField.TYPE_STORED));
        return doc;
    }

    public void index(IssueList issueList, List<Issue> issues, List<Comment> comments) {
        try {
            Path indexPath = Path.of(indexDir + issueList.getId());
            if (!Files.exists(indexPath)) {
                Files.createDirectory(indexPath);
            }
            Directory directory = FSDirectory.open(indexPath);
            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            IndexWriter writer = new IndexWriter(directory, config);
            int j = 0;
            for (Issue issue : issues) {
                List<Comment> issueComments = new ArrayList<>();
                while (j < comments.size() && comments.get(j).getIssue().getId() == issue.getId()) {
                    issueComments.add(comments.get(j));
                    j++;
                }
                writer.addDocument(getDocument(issue, issueComments));
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteIndex(IssueList issueList) {
        try {
            Path indexPath = Path.of(indexDir + issueList.getId());
            IOUtils.rm(indexPath);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Index not deleting");
        }
    }
}
