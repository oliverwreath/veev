package com.oliver;

import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Author: Oliver
 */
@Slf4j
public class Document {
    String name;
    String description;// (#2.3 format: max length 25, don't truncate any words unless the first word > 25, display "..." if truncated(these 3 do not count as part of 25))
    String createdBy;// #1.1. Group by document.createdBy, sort ascending.
    String lastModifiedBy;
    Long sizeInBytes;// format to 50 mb, 900 k, 342 bytes
    Long createdTime;// #1.2. sort ascending; (#2.2 format: yyyy-MM-dd)
    Long modifiedTime;// (#2.2 format: yyyy-MM-dd)

    public static void main(String[] args) {
        System.out.println("Hello!");
    }

    /**
     * Prints a report of the list of documents in the following format:
     * <p>
     *     #1
     * Group by document.createdBy
     * Sort the groups using document.createdBy ascending, case insensitive
     * Sort each sub list of documents by document.createdTime ascending
     * <p>
     *     #2
     * Format the output of document.size to be a more friendly format. Ex.  50 mb, 900 k, 342 bytes, etc...
     * Format the dates using the format: yyyy-MM-dd
     *       #2.3
     * Format the output of document.description such that
     * - no more than the first 25 characters of the description are displayed
     * - don't truncate any words unless the first word is longer than 25 characters
     * - display "..." at the end of the description to indicate that it has been truncated
     * (these three characters do not count as part of the 25 character limit)
     * <p>
     * Example:
     * Andy Andrews
     * "Bobby Timmons Biography","An exhaustive look at the ...",233 mb,2013-05-09,2013-05-14
     * "Apple Sauce","Study of apple sauces.”,87 gb,2013-05-10,2013-05-10
     * "Zed","All matters, A to Zed”,924 k,2013-05-12,2013-05-12
     * Janet Smith
     * "Xray","How the Xray shows your ...",48 mb,2010-10-22,2010-12-02
     * "Computers","Inventory list of ...",423 bytes,2013-03-01,2013-02-17
     *
     * @param documents not null
     */
    public void printDocumentsReport(List<Document> documents) {

    }

}
