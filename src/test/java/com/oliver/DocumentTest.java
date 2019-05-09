package com.oliver;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Validate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Author: Oliver
 */
@Slf4j
public class DocumentTest {
    private static final Logger logger = LoggerFactory.getLogger(DocumentTest.class);

    @BeforeAll
    static void setUp() {
        logger.info("Test Begin!\n");
    }

    @Test
    void testCreatingOneDocument() {
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
        Document.DocumentFormatter documentFormatter = new Document.DocumentFormatter(formatterYYYYMMdd, zoneOffsetToronto, map4ParsingSize);
        Document document = new Document(documentFormatter, "Andy Andrews", "Bobby Timmons Biography", "TOO_LONG_Expect ..._Truncation An exhaustive look at the TOO_LONG_Expect ..._Truncation", "233 mb", "2013-05-09", "2013-05-14");
        Validate.isTrue(document.toString().equals("Document{'Andy Andrews','Bobby Timmons Biography','TOO_LONG_Expect ..._Truncation...',233 mb,2013-05-09,2013-05-14}"));

        Document document2 = new Document(documentFormatter, "Boy Andrews", "Apple Sauce", "SHORT_NO_Truncation", "87 gb", "2013-05-10", "2013-05-10");
        Validate.isTrue(document2.toString().equals("Document{'Boy Andrews','Apple Sauce','SHORT_NO_Truncation',87 gb,2013-05-10,2013-05-10}"));

        Document document3 = new Document(documentFormatter, "Cat Andrews", "Zed", "Last_WORD TOO_Long_keep_it_whole_right? these_will_not_show_since_too_long", "924 k", "2013-05-12", "2013-05-12");
        System.out.println(document3.toString());
        Validate.isTrue(document3.toString().equals("Document{'Cat Andrews','Zed','Last_WORD TOO_Long_keep_it_whole_right?...',924 k,2013-05-12,2013-05-12}"));
    }

    @AfterAll
    static void cleanUp() {
        logger.info("Test End!\n");
    }
}
