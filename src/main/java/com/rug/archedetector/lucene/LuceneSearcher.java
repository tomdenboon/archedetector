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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class LuceneSearcher {
    public final static MultiFieldQueryParser issueQueryParser = new MultiFieldQueryParser(
            new String[] {"summary", "description", "comments" },
            new StandardAnalyzer());
    public final static MultiFieldQueryParser emailQueryParser = new MultiFieldQueryParser(
            new String[] {"sentFrom", "subject", "body" },
            new StandardAnalyzer());
    public final static MultiFieldQueryParser threadQueryParser = new MultiFieldQueryParser(
            new String[] {"sentFrom", "subject", "body" },
            new StandardAnalyzer());

    /**
     * This is the main search function currently used. This function can be used to search all existing indices
     * created. Make sure that when you are using this function you are using an appropriate query parser with a
     * correct index. It is also only possible to search in 1 object index at a time (issue, mail, thread)
     *
     * @param queryParser what query parser the search engine has to use
     * @param indexDir specifies which index directory you want to search in.
     * @param query a string in the format of a lucene query
     * @param ids ids of the entities you want to search in
     * @param startIndex determines the start of the results returned
     * @param endIndex determines the end of the results returned
     * @return an id list of top results found by the search
     */
    public List<Long> searchInMultipleIndices(MultiFieldQueryParser queryParser, String indexDir,
                                              String query, List<Long> ids, int startIndex, int endIndex)
            throws ParseException, IOException {
        List<Long> resultIds = new ArrayList<>();
        Query q = queryParser.parse(query);
        List<IndexReader> readers = new ArrayList<>();
        for(int i = 0; i < ids.size(); i++){
            Path path = Path.of(indexDir, String.valueOf(ids.get(i)));
            if (Files.exists(path)) {
                Directory indexDirectory =
                        FSDirectory.open(path);

                readers.add(DirectoryReader.open(indexDirectory));
            }
        }
        if(readers.size()>0) {
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
                resultIds.add(Long.parseLong(d.get("id")));
            }

            multiReader.close();
        }
        return resultIds;
    }
}

