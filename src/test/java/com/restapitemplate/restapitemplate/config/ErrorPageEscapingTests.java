package com.restapitemplate.restapitemplate.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The HTML error page is the one place an error title becomes markup, so the title must be escaped before it lands in the page.
 *
 * @author KatariinaJ
 * @version 2026-10-03
 */
class ErrorPageEscapingTests {

    @Test
    void markupInTitleIsEscaped() {

        String page = ProblemDetailErrorController.errorPage(
            500, "<script>alert(1)</script>");

        assertTrue(page.contains("&lt;script&gt;alert(1)&lt;/script&gt;"),
            "the title should arrive as escaped text");
        assertFalse(page.contains("<script>"),
            "the page should never carry raw markup from the title");
    }

    @Test
    void ampersandsInTitleAreEscaped() {

        String page = ProblemDetailErrorController.errorPage(400, "Tom & Jerry");

        assertTrue(page.contains("Tom &amp; Jerry"),
            "the ampersand should be escaped");
    }
}