package com.oliver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Author: Oliver
 */
@Slf4j
class DocumentTest {
    // Classic test cases
    static final String SHORT_NO_TRUNCATION = "SHORT_NO_TRUNCATION";
    static final String TOO_LONG_EXPECT_TRUNCATION = "TOO_LONG_Expect ..._Truncation Inventory list of TOO_LONG_Expect ..._Truncation";
    static final String TOO_LONG_BUT_DONT_CHOP_THE_WORD = "Last_WORD TOO_Long_BUT_Don't_Chop_keep_it_whole_right? these_will_not_show_since_too_long";
    static final String TOO_LONG_FIRST_WORD_CHOP_IT = "1st_WORD_Too_Long_Chop_It_TAIL_lost!";
    // Truncated
    static final String TOO_LONG_EXPECT_TRUNCATION_TRUNCATED = "TOO_LONG_Expect ..._Truncation...";
    static final String TOO_LONG_BUT_DONT_CHOP_THE_WORD_TRUNCATED = "Last_WORD TOO_Long_BUT_Don't_Chop_keep_it_whole_right?...";
    static final String TOO_LONG_FIRST_WORD_CHOP_IT_TRUNCATED = "1st_WORD_Too_Long_Chop_It...";
    // constants
    static final String TIME_1300 = "1300-01-01";
    static final String TIME_1501 = "1501-01-01";
    static final String TIME_2000 = "2000-01-01";
    static final String TIME_2002 = "2002-01-01";
    static final String TIME_2100 = "2100-01-01";
    static final String TIME_2202 = "2202-01-01";
    static final String TIME_3000 = "3000-01-01";
    static final String TIME_3003 = "3003-01-01";

    @BeforeAll
    static void setUp() {
        log.info("Test Begin!\n");
    }

    @Test
    void test_WhenPrintDocumentsReportHelper_TheContentsAreAsExpected() {
        Document document = new Document();
        // corner cases
        Validate.isTrue(document.printDocumentsReportHelper(null).toString().equals(""));
        Validate.isTrue(document.printDocumentsReportHelper(new LinkedList<>()).toString().equals(""));

        // prepare the formatter first
        DocumentFormatter documentFormatter = new DocumentFormatter();

        // actual test data
        List<Document> lst = new LinkedList<>();
        lst.add(new Document(documentFormatter, "Janet Smith", "Janet Xray", TOO_LONG_EXPECT_TRUNCATION, "48 mb", TIME_2202, TIME_2202));
        lst.add(new Document(documentFormatter, "Bobby Andrews", "Bobby Timmons Biography", TOO_LONG_EXPECT_TRUNCATION, "233 mb", TIME_2000, TIME_2002));
        lst.add(new Document(documentFormatter, "Zoo", "Zoo Sauce", SHORT_NO_TRUNCATION, "87 gb", TIME_3000, TIME_3003));
        lst.add(new Document(documentFormatter, "Janet Smith", "Janet Computers", TOO_LONG_BUT_DONT_CHOP_THE_WORD, "423 bytes", TIME_2100, TIME_2100));
        lst.add(new Document(documentFormatter, "Andy Andrews", "Andy Sauce", SHORT_NO_TRUNCATION, "87 gb", TIME_1501, TIME_1501));
        lst.add(new Document(documentFormatter, "Boy", "Boy Sauce", SHORT_NO_TRUNCATION, "87 gb", TIME_1300, TIME_1300));
        lst.add(new Document(documentFormatter, "aoy", "aoy Sauce", TOO_LONG_FIRST_WORD_CHOP_IT, "87 gb", TIME_1300, TIME_1300));
        lst.add(new Document(documentFormatter, "Andy Andrews", "Andy Zed", TOO_LONG_BUT_DONT_CHOP_THE_WORD, "924 k", TIME_1300, TIME_1300));
        log.debug("sizeList = {}", lst);

        // run the core function
        document.printDocumentsReport(lst);
        Validate.isTrue(document.printDocumentsReportHelper(lst).toString().equals("Andy Andrews\n" +
                "Document{'Andy Zed','Last_WORD TOO_Long_BUT_Don't_Chop_keep_it_whole_right?...',924 k,1300-01-01,1300-01-01}\n" +
                "Document{'Andy Sauce','SHORT_NO_TRUNCATION',87 gb,1501-01-01,1501-01-01}\n" +
                "aoy\n" +
                "Document{'aoy Sauce','1st_WORD_Too_Long_Chop_It...',87 gb,1300-01-01,1300-01-01}\n" +
                "Bobby Andrews\n" +
                "Document{'Bobby Timmons Biography','TOO_LONG_Expect ..._Truncation...',233 mb,2000-01-01,2002-01-01}\n" +
                "Boy\n" +
                "Document{'Boy Sauce','SHORT_NO_TRUNCATION',87 gb,1300-01-01,1300-01-01}\n" +
                "Janet Smith\n" +
                "Document{'Janet Computers','Last_WORD TOO_Long_BUT_Don't_Chop_keep_it_whole_right?...',423 bytes,2100-01-01,2100-01-01}\n" +
                "Document{'Janet Xray','TOO_LONG_Expect ..._Truncation...',48 mb,2202-01-01,2202-01-01}\n" +
                "Zoo\n" +
                "Document{'Zoo Sauce','SHORT_NO_TRUNCATION',87 gb,3000-01-01,3003-01-01}\n"));
    }

    @Test
    void test_WhenCreatingOneDocument_TheContentsAreAsExpected() {
        DocumentFormatter documentFormatter = new DocumentFormatter();
        Document document = new Document(documentFormatter, "Andy Andrews", "Bobby Timmons Biography", TOO_LONG_BUT_DONT_CHOP_THE_WORD, "233 mb", TIME_1300, TIME_1300);
        Validate.isTrue(document.toStringBeautify().equals("Document{'Bobby Timmons Biography','" + TOO_LONG_BUT_DONT_CHOP_THE_WORD_TRUNCATED + "',233 mb," + TIME_1300 + "," + TIME_1300 + "}"));

        Document document2 = new Document(documentFormatter, "Boy Andrews", "Apple Sauce", SHORT_NO_TRUNCATION, "87 gb", TIME_1501, TIME_1501);
        Validate.isTrue(document2.toStringBeautify().equals("Document{'Apple Sauce','" + SHORT_NO_TRUNCATION + "',87 gb," + TIME_1501 + "," + TIME_1501 + "}"));

        Document document3 = new Document(documentFormatter, "Cat Andrews", "Zed", TOO_LONG_FIRST_WORD_CHOP_IT, "924 k", TIME_2202, TIME_2202);
        log.debug("document3.toString() = {}", document3.toString());
        Validate.isTrue(document3.toStringBeautify().equals("Document{'Zed','" + TOO_LONG_FIRST_WORD_CHOP_IT_TRUNCATED + "',924 k," + TIME_2202 + "," + TIME_2202 + "}"));
    }

    @Test
    void test_WhenFormatDescription_TheTruncatedAsExpected() {
        // corner cases
        Validate.isTrue(DocumentFormatter.formatDescription(null).equals(""));
        Validate.isTrue(DocumentFormatter.formatDescription("").equals(""));

        // format description
        Validate.isTrue(DocumentFormatter.formatDescription(TOO_LONG_EXPECT_TRUNCATION).equals(TOO_LONG_EXPECT_TRUNCATION_TRUNCATED));
        Validate.isTrue(DocumentFormatter.formatDescription(TOO_LONG_BUT_DONT_CHOP_THE_WORD).equals(TOO_LONG_BUT_DONT_CHOP_THE_WORD_TRUNCATED));
        Validate.isTrue(DocumentFormatter.formatDescription(TOO_LONG_FIRST_WORD_CHOP_IT).equals(TOO_LONG_FIRST_WORD_CHOP_IT_TRUNCATED));
        Validate.isTrue(DocumentFormatter.formatDescription(SHORT_NO_TRUNCATION).equals(SHORT_NO_TRUNCATION));
    }

    @Test
    void test_WhenFormatSizeAndTime_TheResultsAsExpected() {
        // format size - parse back and forth should still equal
        DocumentFormatter documentFormatter = new DocumentFormatter();
        List<String> listDocuments = new LinkedList<>(Arrays.asList("423 bytes", "924 k", "233 mb", "48 mb", "87 gb", "233 tb", "233 pb"));
        for (String oneDocument : listDocuments) {
            Validate.isTrue(DocumentFormatter.formatSize(DocumentFormatter.parseSize(oneDocument)).equals(oneDocument));
        }
        // corner cases
        Validate.isTrue(DocumentFormatter.formatSize(DocumentFormatter.parseSize(null)).equals(""));
        Validate.isTrue(DocumentFormatter.formatSize(DocumentFormatter.parseSize("")).equals(""));

        // format time - parse back and forth should still equal
        List<String> listDateTime = new LinkedList<>(Arrays.asList("1900-01-01", "2013-01-01", "2013-05-09", "2013-05-10", "2013-05-12", "2019-03-03", "2099-12-31"));
        for (String oneDateTime : listDateTime) {
            Validate.isTrue(documentFormatter.formatTime(documentFormatter.parseTime(oneDateTime)).equals(oneDateTime));
        }
        // corner cases
        Validate.isTrue(!documentFormatter.formatTime(documentFormatter.parseTime("2012-02-31")).equals("2012-02-31"));
        Validate.isTrue(documentFormatter.formatTime(documentFormatter.parseTime(null)).equals(""));
        Validate.isTrue(documentFormatter.formatTime(documentFormatter.parseTime("")).equals(""));
    }

    @AfterAll
    static void cleanUp() {
        log.info("Test End!\n");
    }
}
