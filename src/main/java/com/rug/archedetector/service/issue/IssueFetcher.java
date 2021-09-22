package com.rug.archedetector.service.issue;

import com.rug.archedetector.model.IssueList;

import java.util.concurrent.CompletableFuture;

/**
 * Issue fetchers are responsible for fetching and saving information about
 * issues and their comments, from some source (i.e. GitHub, Jira, GitLab).
 */
public interface IssueFetcher {
	/**
	 * Fetches and saves issues for the given issue list.
	 * @param list The list to fetch and save issues for.
	 */
	CompletableFuture<Void> fetchIssues(IssueList list);
}
