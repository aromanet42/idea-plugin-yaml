package fr.aromanet.idea.plugin.yaml;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class CopyYamlPropertyPathTest {

    CopyYamlPropertyPath action = new CopyYamlPropertyPath();

    @Test
    public void isCommentOrBlankLine() {
        assertTrue(action.isCommentOrBlankLine("# ceci est un commentaire"));
        assertTrue(action.isCommentOrBlankLine("     # ceci est un commentaire"));
        assertFalse(action.isCommentOrBlankLine("paybox:"));
        assertFalse(action.isCommentOrBlankLine("      payboxUrl: http://localhost/paybox"));
        assertTrue(action.isCommentOrBlankLine(""));
        assertTrue(action.isCommentOrBlankLine("    "));
    }

    @Test
    public void extractPropertyName_aloneInLine() {
        assertEquals("paybox", action.extractPropertyNameFromLine("paybox:"));
    }

    @Test
    public void extractPropertyName_withBlank() {
        assertEquals("service", action.extractPropertyNameFromLine("    service:"));
    }

    @Test
    public void extractPropertyName_withValue() {
        assertEquals("payboxUrl", action.extractPropertyNameFromLine("            payboxUrl: http://10.199.54.202/pollux/stubs/debitCarte"));
    }

    @Test(expected = RuntimeException.class)
    public void extractPropertyName_noKey() {
        action.extractPropertyNameFromLine("            payboxUrl");
    }

    @Test
    public void countIndent() {
        assertEquals(0, action.countIndent("paybox:"));
        assertEquals(0, action.countIndent("paybox: "));
        assertEquals(4, action.countIndent("    service:"));
    }

    @Test
    public void buildPropertyKeyFromList() {
        assertEquals("paybox", action.buildPropertyKeyFromList(Arrays.asList("paybox")));
        assertEquals("paybox.service.debitCarte.payboxUrl", action.buildPropertyKeyFromList(Arrays.asList("payboxUrl", "debitCarte", "service", "paybox")));
    }

    @Test
    public void retrieveParentLineNumber() {
        List<String> lines = Arrays.asList(
                "server:",
                "    tomcat:",
                "        access_log_enabled: true",
                "        basedir: target/tomcat",
                "    port: 8180",
                "",
                "paybox:",
                "    service:",
                "        debitCarte:",
                "            payboxUrl: http://10.199.54.202/pollux/stubs/debitCarte",
                "            localServicePath: /paybox/debitCarte",
                "        pppsurl:",
                "            # url de pppsUrl",
                "            payboxUrl: http://10.199.54.202/pollux/stubs/ppps",
                "            localServicePath: /PPPS"
        );

        LineInfos lineInfos = action.retrieveParentLineNumber(lines, new LineInfos("", 12, 10));
        assertEquals(8, lineInfos.getLineNumber());
        assertEquals(8, lineInfos.getIndent());

    }

    @Test
    public void getPropertyAtCursor() {
        List<String> lines = Arrays.asList(
                "server:",
                "    tomcat:",
                "        access_log_enabled: true",
                "        basedir: target/tomcat",
                "    port: 8180",
                "",
                "paybox:",
                "    service:",
                "        debitCarte:",
                "            payboxUrl: http://10.199.54.202/pollux/stubs/debitCarte",
                "            localServicePath: /paybox/debitCarte",
                "        pppsurl:",
                "            # url de pppsUrl",
                "            payboxUrl: http://10.199.54.202/pollux/stubs/ppps",
                "            localServicePath: /PPPS"
        );

        assertEquals("paybox.service.debitCarte.localServicePath", action.getPropertyAtCursor(lines, 10));

        assertEquals("paybox.service.pppsurl.localServicePath", action.getPropertyAtCursor(lines, 14));

        assertEquals("server.port", action.getPropertyAtCursor(lines, 4));

        assertEquals("server", action.getPropertyAtCursor(lines, 0));
    }
}