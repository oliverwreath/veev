package com.oliver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.oliver.Document.Sizes;

/**
 * Author: Oliver
 */
@Slf4j
class DocumentTest {
    // Classic test cases
    private static final String SHORT_NO_TRUNCATION = "SHORT_NO_TRUNCATION";
    private static final String TOO_LONG_EXPECT_TRUNCATION = "TOO_LONG_Expect ..._Truncation Inventory list of TOO_LONG_Expect ..._Truncation";
    private static final String TOO_LONG_BUT_DONT_CHOP_THE_WORD = "Last_WORD TOO_Long_BUT_Don't_Chop_keep_it_whole_right? these_will_not_show_since_too_long";
    private static final String TOO_LONG_FIRST_WORD_CHOP_IT = "1st_WORD_Too_Long_Chop_It_TAIL_lost!";
    // Truncated
    private static final String TOO_LONG_EXPECT_TRUNCATION_TRUNCATED = "TOO_LONG_Expect ..._Truncation...";
    private static final String TOO_LONG_BUT_DONT_CHOP_THE_WORD_TRUNCATED = "Last_WORD TOO_Long_BUT_Don't_Chop_keep_it_whole_right?...";
    private static final String TOO_LONG_FIRST_WORD_CHOP_IT_TRUNCATED = "1st_WORD_Too_Long_Chop_It...";

    @BeforeAll
    static void setUp() {
        log.info("Test Begin!\n");
    }

    @Test
    void test_WhenPrintDocumentsReportHelper_TheContentsAreAsExpected() {
        // prepare the formatter first
        DateTimeFormatter formatterYYYYMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CANADA);
        ZoneOffset zoneOffsetToronto = ZoneOffset.of("-05:00");
        Map<String, Long> map4ParsingSize = new HashMap<>();
        map4ParsingSize.put(Sizes.bytes.toString(), 1L);
        map4ParsingSize.put(Sizes.k.toString(), 1024L);
        map4ParsingSize.put(Sizes.mb.toString(), 1048576L);
        map4ParsingSize.put(Sizes.gb.toString(), 1073741824L);
        map4ParsingSize.put(Sizes.tb.toString(), 1099511627776L);
        map4ParsingSize.put(Sizes.pb.toString(), 1125899906842624L);
//        map4ParsingSize.put(Sizes.eb.toString(), 1152921504606846976L);
//        map4ParsingSize.put(Sizes.zb.toString(), 1180591620717411303424L);
//        map4ParsingSize.put(Sizes.yb.toString(), 1208925819614629174706176L);
        Document.DocumentFormatter documentFormatter = new Document.DocumentFormatter(formatterYYYYMMdd, zoneOffsetToronto, map4ParsingSize);

        // actual test data
        List<Document> lst = new LinkedList<>();
        lst.add(new Document(documentFormatter, "Janet Smith", "Janet Xray", TOO_LONG_EXPECT_TRUNCATION, "48 mb", "2013-05-09", "2013-05-14"));
        lst.add(new Document(documentFormatter, "Bobby Andrews", "Bobby Timmons Biography", TOO_LONG_EXPECT_TRUNCATION, "233 mb", "2000-05-09", "2013-05-14"));
        lst.add(new Document(documentFormatter, "Zoo", "Zoo Sauce", SHORT_NO_TRUNCATION, "87 gb", "2019-05-10", "2013-05-10"));
        lst.add(new Document(documentFormatter, "Janet Smith", "Janet Computers", TOO_LONG_BUT_DONT_CHOP_THE_WORD, "423 bytes", "3000-03-01", "2013-02-17"));
        lst.add(new Document(documentFormatter, "Andy Andrews", "Andy Sauce", SHORT_NO_TRUNCATION, "87 gb", "1000-05-10", "2013-05-10"));
        lst.add(new Document(documentFormatter, "Boy", "Boy Sauce", SHORT_NO_TRUNCATION, "87 gb", "2000-05-10", "2013-05-10"));
        lst.add(new Document(documentFormatter, "aoy", "aoy Sauce", TOO_LONG_FIRST_WORD_CHOP_IT, "87 gb", "2000-05-10", "2013-05-10"));
        lst.add(new Document(documentFormatter, "Andy Andrews", "Andy Zed", TOO_LONG_BUT_DONT_CHOP_THE_WORD, "924 k", "3000-05-12", "4000-05-12"));
        log.debug("sizeList = {}", lst);

        // run the core function
        new Document().printDocumentsReport(lst);
        Validate.isTrue(new Document().printDocumentsReportHelper(lst).toString().equals("Andy Andrews\n" +
                "Document{'Andy Sauce','SHORT_NO_TRUNCATION',87 gb,1000-05-04,2013-05-10}\n" +
                "Document{'Andy Zed','Last_WORD TOO_Long_BUT_Don't_Chop_keep_it_whole_right?...',924 k,3000-05-12,4000-05-12}\n" +
                "aoy\n" +
                "Document{'aoy Sauce','1st_WORD_Too_Long_Chop_It...',87 gb,2000-05-10,2013-05-10}\n" +
                "Bobby Andrews\n" +
                "Document{'Bobby Timmons Biography','TOO_LONG_Expect ..._Truncation...',233 mb,2000-05-09,2013-05-14}\n" +
                "Boy\n" +
                "Document{'Boy Sauce','SHORT_NO_TRUNCATION',87 gb,2000-05-10,2013-05-10}\n" +
                "Janet Smith\n" +
                "Document{'Janet Xray','TOO_LONG_Expect ..._Truncation...',48 mb,2013-05-09,2013-05-14}\n" +
                "Document{'Janet Computers','Last_WORD TOO_Long_BUT_Don't_Chop_keep_it_whole_right?...',423 bytes,3000-03-01,2013-02-17}\n" +
                "Zoo\n" +
                "Document{'Zoo Sauce','SHORT_NO_TRUNCATION',87 gb,2019-05-10,2013-05-10}\n"));
    }

    @Test
    void test_WhenCreatingOneDocument_TheContentsAreAsExpected() {
        DateTimeFormatter formatterYYYYMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CANADA);
        ZoneOffset zoneOffsetToronto = ZoneOffset.of("-05:00");
        Map<String, Long> map4ParsingSize = new HashMap<>();
        map4ParsingSize.put(Sizes.bytes.toString(), 1L);
        map4ParsingSize.put(Sizes.k.toString(), 1024L);
        map4ParsingSize.put(Sizes.mb.toString(), 1048576L);
        map4ParsingSize.put(Sizes.gb.toString(), 1073741824L);
        map4ParsingSize.put(Sizes.tb.toString(), 1099511627776L);
        map4ParsingSize.put(Sizes.pb.toString(), 1125899906842624L);
//        map4ParsingSize.put(Sizes.eb.toString(), 1152921504606846976L);
//        map4ParsingSize.put(Sizes.zb.toString(), 1180591620717411303424L);
//        map4ParsingSize.put(Sizes.yb.toString(), 1208925819614629174706176L);
        Document.DocumentFormatter documentFormatter = new Document.DocumentFormatter(formatterYYYYMMdd, zoneOffsetToronto, map4ParsingSize);
        Document document = new Document(documentFormatter, "Andy Andrews", "Bobby Timmons Biography", TOO_LONG_BUT_DONT_CHOP_THE_WORD, "233 mb", "2013-05-09", "2013-05-14");
        Validate.isTrue(document.toStringBeautify().equals("Document{'Bobby Timmons Biography','" + TOO_LONG_BUT_DONT_CHOP_THE_WORD_TRUNCATED + "',233 mb,2013-05-09,2013-05-14}"));

        Document document2 = new Document(documentFormatter, "Boy Andrews", "Apple Sauce", SHORT_NO_TRUNCATION, "87 gb", "2013-05-10", "2013-05-10");
        Validate.isTrue(document2.toStringBeautify().equals("Document{'Apple Sauce','" + SHORT_NO_TRUNCATION + "',87 gb,2013-05-10,2013-05-10}"));

        Document document3 = new Document(documentFormatter, "Cat Andrews", "Zed", TOO_LONG_FIRST_WORD_CHOP_IT, "924 k", "2013-05-12", "2013-05-12");
        log.debug("document3.toString() = {}", document3.toString());
        Validate.isTrue(document3.toStringBeautify().equals("Document{'Zed','" + TOO_LONG_FIRST_WORD_CHOP_IT_TRUNCATED + "',924 k,2013-05-12,2013-05-12}"));
    }

    @Test
    void test_WhenFormatDescription_TheTruncatedAsExpected() {
        Document document = new Document();
        // format description
        Validate.isTrue(document.formatDescription(TOO_LONG_EXPECT_TRUNCATION).equals(TOO_LONG_EXPECT_TRUNCATION_TRUNCATED));
        Validate.isTrue(document.formatDescription(TOO_LONG_BUT_DONT_CHOP_THE_WORD).equals(TOO_LONG_BUT_DONT_CHOP_THE_WORD_TRUNCATED));
        Validate.isTrue(document.formatDescription(TOO_LONG_FIRST_WORD_CHOP_IT).equals(TOO_LONG_FIRST_WORD_CHOP_IT_TRUNCATED));
        Validate.isTrue(document.formatDescription(SHORT_NO_TRUNCATION).equals(SHORT_NO_TRUNCATION));
    }

    @Test
    void test_WhenFormatSizeAndTime_TheResultsAsExpected() {
        Document document = new Document();
        // format size - parse back and forth should still equal
        Document.DocumentFormatter documentFormatter = new Document.DocumentFormatter();
        List<String> listDocuments = new LinkedList<>(Arrays.asList("233 mb", "87 gb", "924 k", "48 mb", "423 bytes", "233 tb", "233 pb"));
        for (String oneDocument : listDocuments) {
            Validate.isTrue(document.formatSize(documentFormatter.parseSizeString2Long(oneDocument)).equals(oneDocument));
        }

        // format time - parse back and forth should still equal
        List<String> listDateTime = new LinkedList<>(Arrays.asList("2012-02-28", "2013-01-01", "2013-05-09", "2013-05-10", "2013-05-12", "2019-03-03"));
        for (String oneDateTime : listDateTime) {
            Validate.isTrue(document.formatTime(documentFormatter.parseDateTimeString2Long(oneDateTime)).equals(oneDateTime));
        }
        Validate.isTrue(!document.formatTime(documentFormatter.parseDateTimeString2Long("2012-02-31")).equals("2012-02-31"));
    }

    @AfterAll
    static void cleanUp() {
        log.info("Test End!\n");
    }
}
