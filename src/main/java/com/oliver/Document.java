package com.oliver;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Author: Oliver
 */
@Slf4j
@Data
@NoArgsConstructor
public class Document {
    String name;
    String description;// (#2.3 format: max length 25, don't truncate any words unless the first word > 25, display "..." if truncated(these 3 do not count as part of 25))
    String createdBy;// #1.1. Group by document.createdBy, sort ascending.
    String lastModifiedBy;
    Long sizeInBytes;// format to 50 mb, 900 k, 342 bytes
    Long createdTime;// #1.2. sort ascending; (#2.2 format: yyyy-MM-dd)
    Long modifiedTime;// (#2.2 format: yyyy-MM-dd)

    /**
     * constructor
     *
     * @param name
     * @param description
     * @param createdBy
     * @param sizeInBytes
     * @param createdTime
     * @param modifiedTime
     */
    public Document(String createdBy, String name, String description, Long sizeInBytes, Long createdTime, Long modifiedTime) {
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.sizeInBytes = sizeInBytes;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
    }

    public Document(DocumentFormatter documentFormatter, String createdBy, String name, String description, String sizeString, String createdTime, String modifiedTime) {
        this.createdBy = createdBy;
        this.name = name;
        this.description = description;
        this.sizeInBytes = documentFormatter.parseSizeString2Long(sizeString);
        this.createdTime = documentFormatter.parseDateTimeString2Long(createdTime);
        this.modifiedTime = documentFormatter.parseDateTimeString2Long(modifiedTime);
    }

    @Deprecated
    public String toStringDeprecated() {
        return "Document{" +
                "'" + createdBy + '\'' +
                ",'" + name + '\'' +
                ",'" + description + '\'' +
                "," + sizeInBytes +
                "," + createdTime +
                "," + modifiedTime +
                '}';
    }

    @Override
    public String toString() {
        return "Document{" +
                "'" + createdBy + '\'' +
                ",'" + name + '\'' +
                ",'" + formatDescription(description) + '\'' +
                "," + formatSize(sizeInBytes) +
                "," + formatTime(createdTime) +
                "," + formatTime(modifiedTime) +
                '}';
    }

    protected static final String truncatedIndication = "...";

    private String formatDescription(String description) {
        // NOT truncated
        if (description.length() <= 25) {
            return description;
        }
        // IS truncated?
        boolean isFirstWordTooLong = true;
        for (int i = 0; i < 25; i++) {
            if (!Character.isAlphabetic(description.charAt(i))) {
                isFirstWordTooLong = false;
                break;
            }
        }

        if (isFirstWordTooLong) {
            // Still NOT truncated
            return description;
        } else {
            // Now we MUST truncate and keep the last word.
            int endIndex = 25;
            while (endIndex < description.length()) {
                if (' ' == (description.charAt(endIndex))) {
                    break;
                }
                endIndex++;
            }

            String truncated = description.substring(0, endIndex);
            return truncated + truncatedIndication;
        }
    }

    private String formatTime(Long timeToFormat) {
        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
        String dateText = df2.format(new Date(timeToFormat));
        log.debug("formatTime: {} to {}", timeToFormat, dateText);
        return dateText;
    }

    private static final List<Pair<Long, String>> sizeList;

    static {
        sizeList = new LinkedList<>();
//        sizeList.add(Pair.of(1208925819614629174706176L, "yb"));
//        sizeList.add(Pair.of(1180591620717411303424L, "zb"));
        sizeList.add(Pair.of(1152921504606846976L, "eb"));
        sizeList.add(Pair.of(1125899906842624L, "pb"));
        sizeList.add(Pair.of(1099511627776L, "tb"));
        sizeList.add(Pair.of(1073741824L, "gb"));
        sizeList.add(Pair.of(1048576L, "mb"));
        sizeList.add(Pair.of(1024L, "k"));
        sizeList.add(Pair.of(1L, "bytes"));
    }

    private String formatSize(Long sizeInBytes) {
        for (Pair<Long, String> longStringPair : sizeList) {
            if (sizeInBytes > longStringPair.getLeft()) {
                return sizeInBytes / longStringPair.getLeft() + " " + longStringPair.getRight();
            }
        }

        return "0 " + Sizes.bytes;
    }

    @NoArgsConstructor
    protected static class DocumentFormatter {
        private DateTimeFormatter formatterYYYYMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CANADA);
        private ZoneOffset zoneOffsetToronto = ZoneOffset.of("-05:00");
        private static Map<String, Long> map4ParsingSize = new HashMap<>();

        static {
            map4ParsingSize.put("bytes", 1L);
            map4ParsingSize.put("k", 1024L);
            map4ParsingSize.put("mb", 1048576L);
            map4ParsingSize.put("gb", 1073741824L);
            map4ParsingSize.put("tb", 1099511627776L);
            map4ParsingSize.put("pb", 1125899906842624L);
            map4ParsingSize.put("eb", 1152921504606846976L);
        }

        public DocumentFormatter(DateTimeFormatter formatterYYYYMMdd, ZoneOffset zoneOffsetToronto) {
            this.formatterYYYYMMdd = formatterYYYYMMdd;
            this.zoneOffsetToronto = zoneOffsetToronto;
        }

        public DocumentFormatter(DateTimeFormatter formatterYYYYMMdd, ZoneOffset zoneOffsetToronto, Map<String, Long> map4ParsingSize) {
            this.formatterYYYYMMdd = formatterYYYYMMdd;
            this.zoneOffsetToronto = zoneOffsetToronto;
            this.map4ParsingSize = map4ParsingSize;
        }

        private long parseDateTimeString2Long(String dateTimeString, DateTimeFormatter formatter, ZoneOffset zoneOffset) {
            return LocalDateTime.of(LocalDate.parse(dateTimeString, formatter), LocalTime.of(0, 0)).toInstant(zoneOffset).toEpochMilli();
        }

        private long parseDateTimeString2Long(String dateTimeString) {
            return LocalDateTime.of(LocalDate.parse(dateTimeString, formatterYYYYMMdd), LocalTime.of(0, 0)).toInstant(zoneOffsetToronto).toEpochMilli();
        }

        /**
         * parse String represented size 2 long represented in the unit of Byte.
         *
         * @param size
         * @return
         */
        private long parseSizeString2Long(String size) {
            // Validate preconditions
            Validate.notBlank(size);
            String[] s = size.trim().split(" ");
            Validate.isTrue(s.length == 2);
            Long resultLong = Long.valueOf(s[0]) * map4ParsingSize.get(s[1]);
            Validate.notNull(resultLong);
            return resultLong.longValue();
        }
    }

    public static void main(String[] args) {
        Document document = new Document();
        document.test();
    }

    private void test() {
        System.out.println("Begin ---------------------- !\n");
        // prepare the formatter first
        DateTimeFormatter formatterYYYYMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CANADA);
        ZoneOffset zoneOffsetToronto = ZoneOffset.of("-05:00");
        Map<String, Long> map4ParsingSize = new HashMap<>();
        map4ParsingSize.put("bytes", 1L);
        map4ParsingSize.put("k", 1024L);
        map4ParsingSize.put("mb", 1048576L);
        map4ParsingSize.put("gb", 1073741824L);
        map4ParsingSize.put("tb", 1099511627776L);
        map4ParsingSize.put("pb", 1125899906842624L);
        map4ParsingSize.put("eb", 1152921504606846976L);
//        map4ParsingSize.put("zb", 1180591620717411303424L);
//        map4ParsingSize.put("yb", 1208925819614629174706176L);
        DocumentFormatter documentFormatter = new DocumentFormatter(formatterYYYYMMdd, zoneOffsetToronto, map4ParsingSize);
        log.debug("new document = {}", new Document(documentFormatter, "Andy Andrews", "Bobby Timmons Biography", "TOO_LONG_Expect ..._Truncation An exhaustive look at the TOO_LONG_Expect ..._Truncation", "233 mb", "2013-05-09", "2013-05-14"));

        // actual test data
        List<Document> lst = new LinkedList<>();
        lst.add(new Document(documentFormatter, "Andy Andrews", "Bobby Timmons Biography", "TOO_LONG_Expect ..._Truncation An exhaustive look at the TOO_LONG_Expect ..._Truncation", "233 mb", "2013-05-09", "2013-05-14"));
        lst.add(new Document(documentFormatter, "Andy Andrews", "Apple Sauce", "Study of apple sauces.", "87 gb", "2013-05-10", "2013-05-10"));
        lst.add(new Document(documentFormatter, "Andy Andrews", "Zed", "All matters, A to Zed", "924 k", "2013-05-12", "2013-05-12"));
        lst.add(new Document(documentFormatter, "Janet Smith", "Xray", "TOO_LONG_Expect ..._Truncation How the Xray shows your TOO_LONG_Expect ..._Truncation", "48 mb", "2013-05-09", "2013-05-14"));
        lst.add(new Document(documentFormatter, "Janet Smith", "Computers", "TOO_LONG_Expect ..._Truncation Inventory list of TOO_LONG_Expect ..._Truncation", "423 bytes", "2013-03-01", "2013-02-17"));
        log.debug("sizeList = {}", lst);

        // run the core function
        printDocumentsReport(lst);
        System.out.println("\nEnd ------------------------ !");
    }

    public enum Sizes {
        bytes, k, mb, gb, tb, pb, eb
    }

    /**
     * Prints a report of the list of documents in the following format:
     * <p>
     * #1
     * Group by document.createdBy
     * Sort the groups using document.createdBy ascending, case insensitive
     * Sort each sub list of documents by document.createdTime ascending
     * <p>
     * #2
     * Format the output of document.size to be a more friendly format. Ex.  50 mb, 900 k, 342 bytes, etc...
     * Format the dates using the format: yyyy-MM-dd
     * #2.3
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
        // Validate preconditions
        Validate.notEmpty(documents);

        log.info("\n\nprintDocumentsReport(): documents = {}\n", documents);
        Map<String, List<Document>> mapString2Documents = new HashMap<>();
        for (Document document : documents) {
            String key = document.getCreatedBy();
            if (!mapString2Documents.containsKey(key)) {
                mapString2Documents.put(key, new LinkedList<>());
            }
            mapString2Documents.get(key).add(document);
        }

        log.info("mapString2Documents.keySet().size() = {}", mapString2Documents.keySet().size());
        for (String key : mapString2Documents.keySet()) {
            log.info("key = {}; \nmapString2Documents.get(key).size() = {}", key, mapString2Documents.get(key).size());
            for (Document document : mapString2Documents.get(key)) {
                log.info("document = {}", document);
            }
        }
    }
}
