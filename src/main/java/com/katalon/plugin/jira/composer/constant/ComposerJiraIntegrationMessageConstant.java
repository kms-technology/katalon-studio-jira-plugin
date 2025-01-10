package com.katalon.plugin.jira.composer.constant;

import org.eclipse.osgi.util.NLS;

public class ComposerJiraIntegrationMessageConstant extends NLS {
    private static final String MESSAGE_FILE_NAME = "ComposerJiraIntegrationMessage";
    
    static {
        // initialize resource bundle
        NLS.initializeMessages("com.katalon.plugin.jira.composer.constant." + MESSAGE_FILE_NAME, ComposerJiraIntegrationMessageConstant.class);
    }

    private ComposerJiraIntegrationMessageConstant() {
    }

    public static String PREF_CHCK_ENABLE_INTEGRATION;

    public static String LBL_CUSTOM_FIELD;

    public static String PREF_CHCK_ENABLE_FETCHING_OPTIONS;

    public static String PREF_TITLE_AUTHENTICATION;

    public static String PREF_TITLE_SUBMIT_OPTIONS;

    public static String PREF_TITLE_FETCH_OPTIONS;

    public static String PREF_LBL_SERVER_URL;

    public static String PREF_LBL_USERNAME;

    public static String PREF_LBL_PASSWORD;

    public static String PREF_LBL_API_TOKEN;
    
    public static String PREF_CHCK_SHOW_PASSWORD;

    public static String PREF_CHCK_ENCRYPT_PASSWORD;

    public static String PREF_LBL_CONNECT;

    public static String PREF_LBL_FETCH_CUSTOM_FIELDS;

    public static String PREF_LBL_DF_JIRA_PROJECT;

    public static String PREF_LBL_DF_JIRA_ISSUE_TYPE;

    public static String PREF_CHCK_USE_TEST_CASE_NAME_AS_SUMMARY;

    public static String PREF_CHCK_ATTACH_SCREENSHOT_TO_JIRA_TICKET;

    public static String PREF_CHCK_ATTACH_LOG_TO_JIRA_TICKET;

    public static String PREF_CHCK_AUTO_SUBMIT_TEST_RESULT;

    public static String PREF_MSG_ACCOUNT_CONNECTED;

    public static String JOB_TASK_JIRA_CONNECTION;

    public static String JOB_SUB_TASK_VALIDATING_ACCOUNT;

    public static String JOB_SUB_TASK_FETCHING_PROJECTS;

    public static String JOB_SUB_TASK_FETCHING_ISSUE_TYPES;

    public static String JOB_SUB_TASK_FETCHING_JIRA_FIELDS;
    
    public static String JOB_SUB_TASK_FETCHING_FIELDS;

    public static String TOOLTIP_CLICK_TO_MANAGE_JIRA_ISSUES;

    public static String DIA_TITLE_LINKED_JIRA_ISSUES;

    public static String DIA_ITEM_CREATE_NEW_JIRA_ISSUE;

    public static String DIA_ITEM_LINK_TO_JIRA_ISSUE;

    public static String DIA_ITEM_CREATE_AS_SUB_TASK;

    public static String DIA_LBL_SUMMARY;

    public static String DIA_ISSUE_BROWSE_NOTIFICATION;

    public static String DIA_TITLE_CREATE_NEW_AS_SUB_TASK;

    public static String DIA_MESSAGE_CREATE_NEW_AS_SUB_TASK;

    public static String DIA_LBL_CREATE_NEW_AS_SUB_TASK;

    public static String DIA_TITLE_LINK_TO_EXISTING_ISSUE;

    public static String DIA_MESSAGE_LINK_TO_EXISTING_ISSUE;

    public static String DIA_LBL_LINK_TO_EXISTING_ISSUE;

    public static String JOB_TASK_LINK_TO_JIRA_ISSUE;

    public static String JOB_TASK_UPDATE_JIRA_ISSUE;

    public static String JOB_TASK_VALIDATE_JIRA_ISSUE;

    public static String JOB_MSG_INVALID_JIRA_ISSUE_KEY;

    public static String CLMN_TOOLTIP_CLICK_TO_QUICK_CREATE_ISSUE;
    
    // ImportJiraJQLHandler
    public static String JOB_TASK_IMPORTING_ISSUES;

    public static String JOB_SUB_TASK_IMPORTING_ISSUE;
    
    public static String JOB_SUB_TASK_FETCHING_KATALON_FIELD;

    // IssueSelectionDialog
    public static String CLMN_STATUS_IMPORTED;

    public static String CLMN_STATUS_NEW;

    public static String CM_ISSUE;

    public static String DIA_TITLE_JIRA_ISSUES;

    public static String DIA_LBL_CHOOSE_DESTINATION;

    public static String DIA_JOB_REFRESHING;

    // ImportJiraJQLDialog
    public static String DIA_TITLE_IMPORT_FROM_JQL;

    public static String DIA_JOB_FETCH_ISSUES;

    public static String DIA_LBL_JIRA_JQL;

    public static String DIA_DOCUMENT_URL_IMPORT_TEST_CASE_FROM_JIRA;

    // JiraReportTestLogView
    public static String VIEW_MSG_UNABLE_TO_UPDATE_REPORT;
    
    // JiraTestCaseIntegrationView
    public static String VIEW_LBL_KEY;

    public static String VIEW_TOOLTIP_VIEW_ISSUE_ON_JIRA;

    public static String URL_SETTINGS_JIRA;
    
    public static String URL_TEST_CASE_INTEGRATION_JIRA;

    public static String DOCUMENT_URL_JIRA_CLOUD_FETCH_CONTENT;
    
    public static String ERROR_UNABLE_TO_SAVE_JIRA_SETTING;

    public static String ERROR_CUSTOM_FIELD_NOT_FOUND;

    public static String ERROR_GET_JIRA_ISSUE_WITH_WRONG_TEST_CASE_INDEX;

    public static String BTN_LINK_JIRA_ISSUE_LABEL;

    public static String BTN_EDIT_JIRA_ISSUE_LINK_LABEL;

    public static String BTN_REMOVE_JIRA_ISSUE_LINK_LABEL;

    public static String TXT_JIRA_ISSUE_KEY_PLACEHOLDER;

    public static String BTN_OVERRIDE_TEST_CASE_DESCRIPTION_LABEL;

    public static String LBL_LINKING_JIRA_ISSUE_TEXT;

    public static String LBL_JIRA_ISSUE_NOT_EXISTING_TEXT;
}
