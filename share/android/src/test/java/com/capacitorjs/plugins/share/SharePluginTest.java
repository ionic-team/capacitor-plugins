package com.capacitorjs.plugins.share;

import static org.junit.Assert.*;

import org.junit.Test;

public class SharePluginTest {

    @Test
    public void getCommonMimeType_keepsMatchingType() {
        assertEquals("image/jpeg", SharePlugin.getCommonMimeType("image/jpeg", "image/jpeg"));
    }

    @Test
    public void getCommonMimeType_usesWildcardSubtypeForSameTopLevelType() {
        assertEquals("image/*", SharePlugin.getCommonMimeType("image/jpeg", "image/png"));
        assertEquals("image/*", SharePlugin.getCommonMimeType("image/*", "image/png"));
        assertEquals("video/*", SharePlugin.getCommonMimeType("video/mp4", "video/webm"));
    }

    @Test
    public void getCommonMimeType_returnsNullForDifferentOrUnknownTypes() {
        assertNull(SharePlugin.getCommonMimeType("image/jpeg", "video/mp4"));
        assertNull(SharePlugin.getCommonMimeType("image/jpeg", null));
        assertNull(SharePlugin.getCommonMimeType(null, "image/jpeg"));
        assertNull(SharePlugin.getCommonMimeType("imagex/png", "image/png"));
    }
}
