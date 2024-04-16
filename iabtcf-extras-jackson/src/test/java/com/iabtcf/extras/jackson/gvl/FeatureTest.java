package com.iabtcf.extras.jackson.gvl;

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

import com.iabtcf.extras.gvl.Feature;
import com.iabtcf.extras.jackson.Loader;
import com.iabtcf.extras.jackson.TestUtil;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class FeatureTest {

    private static Feature featureTwo;
    private static Feature featureTwoV3;
    private static final int FEATURE_ID_SELECTED_FOR_TEST = 2;

    @BeforeClass
    public static void setupBeforeClass() throws IOException {
        Loader loader = new Loader();
        List<Feature> features = loader.globalVendorList(TestUtil.getGlobalVendorList()).getFeatures();
        featureTwo = features.stream().filter(o -> o.getId() == FEATURE_ID_SELECTED_FOR_TEST).findFirst().orElse(null);

        features = loader.globalVendorList(TestUtil.getGlobalVendorListV3()).getFeatures();
        featureTwoV3 =
            features.stream().filter(o -> o.getId() == FEATURE_ID_SELECTED_FOR_TEST).findFirst().orElse(null);
    }

    @Test
    public void testGetId() {
        Assert.assertEquals(2, featureTwo.getId());
    }

    @Test
    public void testGetName() {
        String expectedName = "Link different devices";
        Assert.assertEquals(expectedName, featureTwo.getName());
    }

    @Test
    public void testGetDescription() {
        String expectedDescription =
                "Different devices can be determined as belonging to you or your household in support of one or more of purposes.";
        Assert.assertEquals(expectedDescription, featureTwo.getDescription());
    }

    @Test
    public void testGetDescriptionLegal() {
        String expectedDescriptionLegal =
                "Vendors can:\n* Deterministically determine that two or more devices belong to the same user or household\n* Probabilistically determine that two or more devices belong to the same user or household\n* Actively scan device characteristics for identification for probabilistic identification if users have allowed vendors to actively scan device characteristics for identification (Special Feature 2)";
        Assert.assertEquals(expectedDescriptionLegal, featureTwo.getDescriptionLegal().get());
    }

    @Test
    public void testFeaturesV3() {
        Assert.assertEquals(2, featureTwoV3.getId());
        Assert.assertEquals("Link different devices", featureTwoV3.getName());
        Assert.assertEquals("In support of the purposes explained in this notice, your device might be considered as likely linked to other devices that belong to you or your household (for instance because you are logged in to the same service on both your phone and your computer, or because you may use the same Internet connection on both devices).", featureTwoV3.getDescription());
        Assert.assertFalse(featureTwoV3.getDescriptionLegal().isPresent());
        Assert.assertEquals(2, featureTwoV3.getIllustrations().get().size());
        Assert.assertEquals("Test Illustration 1", featureTwoV3.getIllustrations().get().get(0));
        Assert.assertEquals("Test Illustration 2", featureTwoV3.getIllustrations().get().get(1));
    }
}
