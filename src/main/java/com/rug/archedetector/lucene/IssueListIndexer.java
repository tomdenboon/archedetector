package com.rug.archedetector.lucene;

import com.rug.archedetector.model.Comment;
import com.rug.archedetector.model.Issue;
import com.rug.archedetector.model.IssueList;
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
    final static public String indexDir = "src/main/resources/index/issueList/";

    /**
     * This function will create a lucene document for an issue
     *
     * @param issue a jira issue
     * @param comments the comments belonging to that issue
     * @return a lucene document of this object
     */
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

    /**
     * This will add an Issue list index to the index folder. Make sure the indexDir exists in your filesystem.
     * This function needs the issues and its comments to be in the same order otherwise it will produces
     * faulty indices. Needs refactoring.
     *
     * @param issueList a jira issue list
     * @param issues the issues belonging to that list
     * @param comments the comments that belong to the issues
     */
    public void index(IssueList issueList, List<Issue> issues, List<Comment> comments) {
        try {
            if(!Files.exists(Path.of("src/main/resources/index/"))){
                Files.createDirectory(Path.of("src/main/resources/index/"));
            }
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
