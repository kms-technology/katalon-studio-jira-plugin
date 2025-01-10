package com.katalon.plugin.jira.core;

import org.apache.commons.lang3.StringUtils;

public class JiraCredential {
    private String serverUrl;

    private String username;

    private String password;

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isJiraCloud() {
        if (StringUtils.isEmpty(serverUrl)) {
            throw new IllegalStateException("The server URL is empty");
        }

        return serverUrl.contains(".atlassian.net") || serverUrl.contains(".jira.com");
    }
}
