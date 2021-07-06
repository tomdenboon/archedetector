package com.rug.archedetector.lucene;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MailingListSearcher {
    final private String indexDir = "src/main/resources/index/mailingList/";

    public List<Long> searchInMultiple(String query, List<Long> mailingListIds, int startIndex, int endIndex)
            throws ParseException, IOException {
        List<Long> emailIds = new ArrayList<>();
        StandardAnalyzer analyzer = new StandardAnalyzer();
        MultiFieldQueryParser queryParser = new MultiFieldQueryParser(
                new String[] {"sentFrom", "subject", "body" },
                analyzer);
        Query q = queryParser.parse(query);
        System.out.println(q);
        List<IndexReader> readers = new ArrayList<>();
        for(int i = 0; i < mailingListIds.size(); i++){
            Path path = Path.of(indexDir+mailingListIds.get(i));
            if (Files.exists(path)) {
                Directory indexDirectory =
                        FSDirectory.open(path);
                readers.add(DirectoryReader.open(indexDirectory));
            }
        }
        if(readers.size() > 0) {
            IndexReader[] indexReaders = new IndexReader[readers.size()];
            for (int i = 0; i < readers.size(); i++)
            {
                indexReaders[i] = readers.get(i);
            }
            MultiReader multiReader = new MultiReader(indexReaders);
            IndexSearcher searcher = new IndexSearcher(multiReader);

            TopDocs docs = searcher.search(q, endIndex);
            ScoreDoc[] hits = docs.scoreDocs;
            for (int i = startIndex; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                emailIds.add(Long.parseLong(d.get("id")));
            }

            multiReader.close();
        }
        return emailIds;
    }
}
