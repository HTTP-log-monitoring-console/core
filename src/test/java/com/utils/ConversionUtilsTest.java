package com.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConversionUtilsTest {
    @Test
    public void extractNullSectionTests() {
        assertEquals(null, ConversionUtils.extractHTTPSectionFromRequest(null));
        assertEquals(null, ConversionUtils.extractHTTPSectionFromRequest(""));
        assertEquals(null, ConversionUtils.extractHTTPSectionFromRequest("garbate_without_slash_first//"));
    }
    @Test
    public void extractSimpleValidSectionTests() {
        assertEquals("/", ConversionUtils.extractHTTPSectionFromRequest("//report"));
        assertEquals("/report", ConversionUtils.extractHTTPSectionFromRequest("/report"));
        assertEquals("/report", ConversionUtils.extractHTTPSectionFromRequest("/report/1"));
        assertEquals("/report", ConversionUtils.extractHTTPSectionFromRequest("/report/report"));
        assertEquals("/report", ConversionUtils.extractHTTPSectionFromRequest("/report/report/"));
        assertEquals("/report", ConversionUtils.extractHTTPSectionFromRequest("/report/test"));
        assertEquals("/report", ConversionUtils.extractHTTPSectionFromRequest("/report/test/"));
        assertEquals("/report", ConversionUtils.extractHTTPSectionFromRequest("/report/test/test"));
        assertEquals("/report", ConversionUtils.extractHTTPSectionFromRequest("/report/test1/test2"));
    }
    @Test
    public void extractRequirementsSectionsTest() {
        assertEquals("/pages", ConversionUtils.extractHTTPSectionFromRequest("/pages/create"));
        assertEquals("/api", ConversionUtils.extractHTTPSectionFromRequest("/api/user"));
    }
}