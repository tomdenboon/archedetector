package com.rug.archedetector.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.rug.archedetector.dao.EmailRepository;
import com.rug.archedetector.dao.EmailThreadRepository;
import com.rug.archedetector.dao.MailingListRepository;
import com.rug.archedetector.dao.QueryCollectionRepository;
import com.rug.archedetector.exceptions.ResourceNotFoundException;
import com.rug.archedetector.model.EmailThread;
import com.rug.archedetector.model.IssueList;
import com.rug.archedetector.model.MailingList;
import com.rug.archedetector.model.QueryCollection;
import com.rug.archedetector.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.lingala.zip4j.ZipFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueryCollectionService {
    private final QueryCollectionRepository queryCollectionRepository;
    private final MailingListRepository mailingListRepository;
    private final EmailThreadRepository emailThreadRepository;
    private final EmailRepository emailRepository;

    @Transactional(readOnly = true)
    public List<QueryCollection> getAll(){
        return queryCollectionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public QueryCollection get(Long id){
        return this.queryCollectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @Transactional
    public QueryCollection save(QueryCollection queryCollection){
        return queryCollectionRepository.save(queryCollection);
    }

    @Transactional
    public ResponseEntity<?> delete(Long id) {
        return queryCollectionRepository.findById(id).map(queryCollection -> {
            queryCollectionRepository.delete(queryCollection);
            return ResponseEntity.ok().build();
        }).orElseThrow(() -> new ResourceNotFoundException("id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public void export(long id, HttpServletResponse response) throws IOException {
        var qc = this.queryCollectionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss"));
        Path qcExportPath = Path.of("__query-collection-export", String.valueOf(qc.getId()));
        Files.createDirectories(qcExportPath);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ObjectNode qcNode = mapper.createObjectNode()
                .put("name", qc.getName());
        qcNode.put("name", qc.getName());
        Path qcFile = qcExportPath.resolve("queryCollection.json");

        ArrayNode mailingListsNode = qcNode.putArray("mailingLists");
        Path mailingListsDir = qcExportPath.resolve("mailingLists");
        for (var mailingList : qc.getMailingLists()) {
            this.exportMailingList(mailingList, mailingListsNode, mailingListsDir, mapper);
        }

        ArrayNode issueListsNode = qcNode.putArray("issueLists");
        Path issueListsDir = qcExportPath.resolve("issueLists");
        for (var issueList : qc.getIssueLists()) {
            this.exportIssueList(issueList, issueListsNode, issueListsDir);
        }

        mapper.writeValue(Files.newOutputStream(qcFile), qcNode);

        Path zipFilePath = qcExportPath.resolve("export_" + timestamp + ".zip");
        ZipFile zip = new ZipFile(zipFilePath.toFile());
        zip.addFile(qcFile.toFile());
        zip.addFolder(mailingListsDir.toFile());
        zip.addFolder(issueListsDir.toFile());

        response.setHeader("Content-Disposition", "attachment; filename=" + zipFilePath.getFileName());
        response.setContentType("application/zip");
        response.setStatus(HttpStatus.OK.value());
        try (var in = Files.newInputStream(zipFilePath)) {
            in.transferTo(response.getOutputStream());
        }
        response.flushBuffer();
    }

    private void exportMailingList(MailingList list, ArrayNode arrayNode, Path mailingListsDir, ObjectMapper mapper) throws IOException {
        log.info("Exporting mailing list {}.", list.getName());
        final String id = StringUtils.random(32);
        arrayNode.addObject().put("id", id).put("url", list.getUrl()).put("name", list.getName());
        Path thisListDir = mailingListsDir.resolve(id);
        Files.createDirectories(thisListDir);

        Pageable pageable = PageRequest.of(0, 100);
        Page<EmailThread> threadsPage = this.emailThreadRepository.findByMailingListId(list.getId(), pageable);
        Path pageDir = thisListDir.resolve("page-" + threadsPage.getNumber());
        AtomicLong threadsExported = new AtomicLong(0);
        AtomicLong emailsExported = new AtomicLong(0);
        while (!threadsPage.isEmpty()) {
            final Path currentPageDir = pageDir;
            Files.createDirectories(currentPageDir);
            threadsPage.getContent().parallelStream().forEach(thread -> {
                log.info("Exporting email thread {}.", thread.getId());
                var threadNode = mapper.createObjectNode()
                        .put("date", thread.getDate().toString())
                        .put("size", thread.getSize())
                        .put("subject", thread.getSubject());
                var threadTagsArray = threadNode.withArray("tags");
                thread.getTags().forEach(tag -> threadTagsArray.add(tag.getName()));
                var emailsArray = threadNode.withArray("emails");
                var emails = this.emailRepository.findByEmailThreadId(thread.getId(), Sort.by("date"));
                for (var email : emails) {
                    var emailNode = emailsArray.addObject()
                            .put("date", email.getDate().toString())
                            .put("body", email.getBody())
                            .put("inReplyTo", email.getInReplyTo())
                            .put("messageId", email.getMessageId())
                            .put("raw", email.getRaw())
                            .put("sentFrom", email.getSentFrom())
                            .put("subject", email.getSubject());
                    var emailTagsArray = emailNode.withArray("tags");
                    email.getTags().forEach(tag -> emailTagsArray.add(tag.getName()));
                    emailsExported.incrementAndGet();
                }
                try {
                    mapper.writeValue(Files.newOutputStream(currentPageDir.resolve("thread-" + thread.getId() + ".json")), threadNode);
                    threadsExported.incrementAndGet();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            log.info("Exported {}/{} threads, {} total emails.", threadsExported, threadsPage.getTotalElements(), emailsExported);
            if (!threadsPage.hasNext()) break;
            threadsPage = this.emailThreadRepository.findByMailingListId(list.getId(), threadsPage.nextPageable());
            pageDir = thisListDir.resolve("page-" + threadsPage.getNumber());
        }
    }

    private void exportIssueList(IssueList list, ArrayNode arrayNode, Path issueListsDir) throws IOException {
        final String id = StringUtils.random(32);
        arrayNode.addObject().put("id", id).put("name", list.getName()).put("key", list.getKey());
        Path thisListDir = issueListsDir.resolve(id);
        Files.createDirectories(thisListDir);

        // TODO: Export issue list.
    }
}
