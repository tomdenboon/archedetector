package com.rug.archedetector.service.issue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.sisyphsu.dateparser.DateParser;
import com.rug.archedetector.dao.CommentRepository;
import com.rug.archedetector.dao.IssueRepository;
import com.rug.archedetector.lucene.IssueListIndexer;
import com.rug.archedetector.model.Comment;
import com.rug.archedetector.model.Issue;
import com.rug.archedetector.model.IssueList;
import com.rug.archedetector.util.UriBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class ApacheJiraIssueFetcher implements IssueFetcher {
	private static final String APACHE_JIRA_API_URL = "https://issues.apache.org/jira/rest/api/2/search";
	private final IssueRepository issueRepository;
	private final CommentRepository commentRepository;

	@Value("${apache-issues.username}")
	private String apacheIssuesUsername;

	@Value("${apache-issues.password}")
	private String apacheIssuesPassword;

	private final HttpClient httpClient = HttpClient.newHttpClient();
	private final ObjectMapper mapper = new ObjectMapper();
	private final IssueListIndexer issueListIndexer = new IssueListIndexer();

	/**
	 * Fetches and saves issues for the given issue list.
	 *
	 * @param list The list to fetch and save issues for.
	 */
	@Override
	public void fetchIssues(IssueList list) {
		int startAt = 0;
		int maxResults = 200;
		var response = this.sendRequest(list.getKey(), startAt, maxResults).join();
		// TODO: Fix this somehow.
		System.out.println(response.toPrettyString());
	}

	/**
	 * Requests a set of issues from the Jira API.
	 * @param projectKey The identifier for the project to fetch issues from.
	 * @param startAt The offset for search pagination.
	 * @param step The number of items to fetch.
	 * @return A future that, when complete, returns the JSON object that
	 * contains the response from the API.
	 */
	private CompletableFuture<ObjectNode> sendRequest(String projectKey, int startAt, int step) {
		final String authHeader = "Basic " + Base64.getEncoder().encodeToString((apacheIssuesUsername + ":" + apacheIssuesPassword).getBytes());
		var uri = UriBuilder.buildParams(APACHE_JIRA_API_URL, Map.of(
				"jql", "project = " + projectKey,
				"startAt", startAt,
				"maxResults", step,
				"fields", "description,comment,created,summary"
		));
		var request = HttpRequest.newBuilder(uri)
				.header("Authorization", authHeader)
				.header("Accept", "application/json")
				.timeout(Duration.ofSeconds(5))
				.GET().build();
		return this.httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
				.thenApply(in -> {
					try {
						return this.mapper.readValue(in.body(), ObjectNode.class);
					} catch (IOException e) {
						e.printStackTrace();
						return null;
					}
				});
	}

	private void parseAndSaveIssues(ObjectNode obj, IssueList issueList, List<String> filterUsers) {
		List<Issue> issues = new ArrayList<>();
		List<Comment> comments = new ArrayList<>();
		DateParser parser = DateParser.newBuilder().build();
		ArrayNode issuesArray = obj.withArray("issues");
		for (var issueObj : issuesArray) {
			Issue issue = new Issue();
			issue.setIssueList(issueList);
			issue.setKey(issueObj.get("key").asText());

			var fields = issueObj.get("fields");
			if(!fields.get("description").isNull()){
				issue.setDescription(fields.get("description").asText());
			}else{
				issue.setDescription("");
			}
			issue.setDate(parser.parseOffsetDateTime(fields.get("created").asText()).toZonedDateTime());
			issue.setSummary(fields.get("summary").asText());
			var commentsInfo = fields.get("comment");
			if (commentsInfo == null || commentsInfo.isNull()) continue;
			ArrayNode commentsJson = commentsInfo.withArray("comments");
			for (var commentObj : commentsJson) {
				var authorJson = commentObj.get("author");
				String author = authorJson.get("displayName").asText();
				if(!filterUsers.contains(author)) {
					String body = commentObj.get("body").asText();
					OffsetDateTime date = parser.parseOffsetDateTime(commentObj.get("created").asText());
					comments.add(new Comment(
							issue,
							author,
							date.toZonedDateTime(),
							body
					));
				}
			}
			issues.add(issue);
		}
		issueRepository.saveAll(issues);
		commentRepository.saveAll(comments);
		issueListIndexer.index(issueList, issues, comments);
	}
}
