package com.smaato.iabtcf.extras.jackson.gvl;

/*-
 * #%L
 * IAB TCF Java GVL Jackson
 * %%
 * Copyright (C) 2020 IAB Technology Laboratory, Inc
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.smaato.iabtcf.extras.gvl.Gvl;
import com.smaato.iabtcf.extras.gvl.Vendor;
import com.smaato.iabtcf.extras.jackson.Loader;
import com.smaato.iabtcf.extras.jackson.TestUtil;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;

public class VendorTest {

    private static Vendor vendorEight;
    private static Vendor vendorTwo;
    private static Vendor vendorV3Two;
    private static final int VENDOR_ID_SELECTED_FOR_TEST = 8;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Loader loader = new Loader();
        Gvl gvl = loader.globalVendorList(TestUtil.getGlobalVendorList());
        vendorEight = gvl.getVendor(VENDOR_ID_SELECTED_FOR_TEST);
        vendorTwo = gvl.getVendor(2);

        gvl = loader.globalVendorList(TestUtil.getGlobalVendorListV3());
        vendorV3Two = gvl.getVendor(2);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(8, vendorEight.getId());
    }

    @Test
    public void testGetName() {
        String expectedName = "Emerse Sverige AB";
        Assert.assertEquals(expectedName, vendorEight.getName());
    }

    @Test
    public void testGetPurposes() {
        Assert.assertNotNull(vendorEight.getPurposes());
        Assert.assertEquals(3, vendorEight.getPurposes().size());
        Assert.assertEquals(Arrays.asList(1, 3, 4), vendorEight.getPurposes());
    }

    @Test
    public void testGetLegIntPurposes() {
        Assert.assertNotNull(vendorEight.getLegIntPurposes());
        Assert.assertEquals(4, vendorEight.getLegIntPurposes().size());
        Assert.assertEquals(Arrays.asList(2, 7, 8, 9), vendorEight.getLegIntPurposes());
    }

    @Test
    public void testGetFlexiblePurposes() {
        Assert.assertNotNull(vendorEight.getFlexiblePurposes());
        Assert.assertEquals(2, vendorEight.getFlexiblePurposes().size());
        Assert.assertEquals(Arrays.asList(2, 9), vendorEight.getFlexiblePurposes());
    }

    @Test
    public void testGetSpecialPurposes() {
        Assert.assertNotNull(vendorEight.getSpecialPurposes());
        Assert.assertEquals(2, vendorEight.getSpecialPurposes().size());
        Assert.assertEquals(Arrays.asList(1, 2), vendorEight.getSpecialPurposes());
    }

    @Test
    public void testGetFeatures() {
        Assert.assertNotNull(vendorEight.getFeatures());
        Assert.assertEquals(2, vendorEight.getFeatures().size());
        Assert.assertEquals(Arrays.asList(1, 2), vendorEight.getFeatures());
    }

    @Test
    public void testGetSpecialFeatures() {
        Assert.assertNotNull(vendorEight.getSpecialFeatures());
        Assert.assertEquals(0, vendorEight.getSpecialFeatures().size());
    }

    @Test
    public void testGetPolicyUrl() {
        String expectedPolicyUrl = "https://www.emerse.com/privacy-policy/";
        Assert.assertEquals(expectedPolicyUrl, vendorEight.getPolicyUrl().get());
    }

    @Test
    public void testGetDeletedDate() {
        Assert.assertEquals(Instant.parse("2020-06-28T00:00:00Z"), vendorEight.getDeletedDate().get());
    }

    @Test
    public void testGetOverflow() {
        Assert.assertNotNull(vendorEight.getOverflow());
    }

    @Test
    public void testIsDeleted() {
        Assert.assertTrue(vendorEight.isDeleted());
    }

    @Test
    public void testCookieMaxAgeSeconds() {
        long expectedCookieMaxAgeSeconds = 31557600000L;
        Assert.assertTrue(vendorEight.getCookieMaxAgeSeconds().isPresent());
        Assert.assertEquals(expectedCookieMaxAgeSeconds, vendorEight.getCookieMaxAgeSeconds().get().longValue());
    }

    @Test
    public void testUsesCookies() {
        Assert.assertTrue(vendorEight.getUsesCookies().get());
    }

    @Test
    public void testCookieRefresh() {
        Assert.assertFalse(vendorEight.getCookieRefresh().get());
    }

    @Test
    public void testUsesNonCookieAccess() {
        Assert.assertTrue(vendorEight.getUsesNonCookieAccess().get());
    }

    @Test
    public void testNullDeviceStorageDisclosureUrl() {
        Assert.assertFalse(vendorEight.getDeviceStorageDisclosureUrl().isPresent());
    }

    @Test
    public void testNullCookieMaxAgeSeconds() {
        Assert.assertFalse(vendorTwo.getUsesCookies().get());
        Assert.assertFalse(vendorTwo.getCookieMaxAgeSeconds().isPresent());
    }

    @Test
    public void testDeviceStorageDisclosureUrl() {
        String expectedDeviceStorageDisclosureUrl = "https://privacy.blismedia.com/.well-known/deviceStorage.json";
        Assert.assertTrue(vendorTwo.getDeviceStorageDisclosureUrl().isPresent());
        Assert.assertEquals(expectedDeviceStorageDisclosureUrl, vendorTwo.getDeviceStorageDisclosureUrl().get());
    }

    @Test
    public void testVendorV3() {
        Assert.assertEquals(2, vendorV3Two.getId());
        Assert.assertEquals("Captify Technologies Limited", vendorV3Two.getName());
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 7, 9, 10), vendorV3Two.getPurposes());
        Assert.assertEquals(Collections.emptyList(), vendorV3Two.getLegIntPurposes());
        Assert.assertEquals(Collections.emptyList(), vendorV3Two.getFlexiblePurposes());
        Assert.assertEquals(Arrays.asList(1, 2), vendorV3Two.getSpecialPurposes());
        Assert.assertEquals(Collections.singletonList(2), vendorV3Two.getFeatures());
        Assert.assertEquals(Collections.singletonList(2), vendorV3Two.getSpecialFeatures());
        Assert.assertEquals("https://static.cpx.to/gvl/deviceStorageDisclosure.json", vendorV3Two.getDeviceStorageDisclosureUrl().get());
        Assert.assertFalse(vendorV3Two.getPolicyUrl().isPresent());
        Assert.assertFalse(vendorV3Two.getDeletedDate().isPresent());
        Assert.assertFalse(vendorV3Two.isDeleted());
        Assert.assertTrue(vendorV3Two.getUsesCookies().isPresent());
        Assert.assertTrue(vendorV3Two.getUsesCookies().get());
        Assert.assertTrue(vendorV3Two.getCookieRefresh().isPresent());
        Assert.assertTrue(vendorV3Two.getCookieRefresh().get());
        Assert.assertTrue(vendorV3Two.getUsesNonCookieAccess().isPresent());
        Assert.assertTrue(vendorV3Two.getUsesNonCookieAccess().get());
        Assert.assertTrue(vendorV3Two.getCookieMaxAgeSeconds().isPresent());
        Assert.assertEquals(31536000L, vendorV3Two.getCookieMaxAgeSeconds().get().longValue());
        Assert.assertTrue(vendorV3Two.getDataRetention().isPresent());
        Assert.assertTrue(vendorV3Two.getDataRetention().get().getStdRetention().isPresent());
        Assert.assertEquals(365, vendorV3Two.getDataRetention().get().getStdRetention().get().intValue());
        Assert.assertEquals(0, vendorV3Two.getDataRetention().get().getPurposes().size());
        Assert.assertEquals(0, vendorV3Two.getDataRetention().get().getSpecialPurposes().size());
        Assert.assertTrue(vendorV3Two.getUrls().isPresent());
        Assert.assertEquals(1, vendorV3Two.getUrls().get().size());
        Assert.assertEquals("en", vendorV3Two.getUrls().get().get(0).getLangId());
        Assert.assertEquals("https://www.captifytechnologies.com/privacy-notice/",
                            vendorV3Two.getUrls().get().get(0).getPrivacy());
        Assert.assertTrue(vendorV3Two.getUrls().get().get(0).getLegIntClaim().isPresent());
        Assert.assertEquals("https://www.captifytechnologies.com/privacy-notice/",
                            vendorV3Two.getUrls().get().get(0).getLegIntClaim().get());
        Assert.assertTrue(vendorV3Two.getDataDeclaration().isPresent());
        Assert.assertEquals(5, vendorV3Two.getDataDeclaration().get().size());
        Assert.assertEquals(Arrays.asList(1, 2, 4, 6, 11), vendorV3Two.getDataDeclaration().get());
        Assert.assertEquals("https://static.cpx.to/gvl/deviceStorageDisclosure.json",
                            vendorV3Two.getDeviceStorageDisclosureUrl().get());
    }
}
