package com.rug.archedetector.lucene;

import com.rug.archedetector.model.MailingList;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MailingListSearcher {
    public List<Long> keyWordSearch(String query, Long mailingListId, Pageable pageable) throws ParseException, IOException {
        List<Long> emailIds = new ArrayList<>();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                new String[] {"sentFrom", "subject", "body" },
                analyzer);
        Query q = queryParser.parse(query);

        Directory indexDirectory =
                FSDirectory.open(Path.of("src/main/resources/index/"+mailingListId));
        IndexReader reader = DirectoryReader.open(indexDirectory);
        IndexSearcher searcher = new IndexSearcher(reader);

        int startIndex = pageable.getPageNumber() * pageable.getPageSize();
        int endIndex = (pageable.getPageNumber()+1) * pageable.getPageSize();

        TopDocs docs = searcher.search(q, endIndex);

        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");

        for(int i=startIndex;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            emailIds.add(Long.parseLong(d.get("id")));
        }
        reader.close();
        return emailIds;
    }
}
