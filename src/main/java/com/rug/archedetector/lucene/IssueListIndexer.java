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
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Component
public class IssueListIndexer {
    /**
     * The directory in which all indexes are stored.
     */
    private static final Path INDEX_DIR = Path.of("index");

    /**
     * The directory in which issue list indexes are stored.
     */
    public static final Path ISSUES_LIST_INDEX_DIR = INDEX_DIR.resolve("issueList");

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
            Path issueListDir = ISSUES_LIST_INDEX_DIR.resolve(String.valueOf(issueList.getId()));
            Files.createDirectories(issueListDir);
            Directory directory = FSDirectory.open(issueListDir);
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
            IOUtils.rm(ISSUES_LIST_INDEX_DIR.resolve(String.valueOf(issueList.getId())));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
