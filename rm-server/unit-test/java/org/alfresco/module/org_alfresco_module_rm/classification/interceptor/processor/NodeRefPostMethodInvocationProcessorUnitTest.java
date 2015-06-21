/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_rm.classification.interceptor.processor;

import static com.google.common.collect.ImmutableList.copyOf;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.unmodifiableCollection;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.extensions.webscripts.GUID.generate;

import java.util.ArrayList;

import org.alfresco.module.org_alfresco_module_rm.classification.ContentClassificationService;
import org.alfresco.module.org_alfresco_module_rm.test.util.BaseUnitTest;
import org.alfresco.service.cmr.repository.NodeRef;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

/**
 * NodeRef Post Method Invocation Processor Unit Test
 *
 * @author Tuna Aksoy
 * @since 3.0
 */
public class NodeRefPostMethodInvocationProcessorUnitTest extends BaseUnitTest
{
    @InjectMocks private NodeRefPostMethodInvocationProcessor nodeRefPostMethodInvocationProcessor;
    @Mock private ContentClassificationService mockedContentClassificationService;

    @Test
    public void testProcessingNonExistingNode()
    {
        NodeRef nodeRef = new NodeRef(generate() + "://" + generate() + "/");

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef), TYPE_CONTENT)).thenReturn(true);
        when(mockedContentClassificationService.hasClearance(nodeRef)).thenReturn(true);

        assertEquals(nodeRef, nodeRefPostMethodInvocationProcessor.process(nodeRef));
    }

    @Test
    public void testProcessingNonContent()
    {
        NodeRef nodeRef = generateNodeRef();

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef), TYPE_CONTENT)).thenReturn(false);
        when(mockedContentClassificationService.hasClearance(nodeRef)).thenReturn(true);

        assertEquals(nodeRef, nodeRefPostMethodInvocationProcessor.process(nodeRef));
    }

    @Test
    public void testExistingNodeWithUserClearance()
    {
        NodeRef nodeRef = generateNodeRef();

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef), TYPE_CONTENT)).thenReturn(true);
        when(mockedContentClassificationService.hasClearance(nodeRef)).thenReturn(true);

        assertEquals(nodeRef, nodeRefPostMethodInvocationProcessor.process(nodeRef));
    }

    @Test
    public void testExistingNodeWithNoUserClearance()
    {
        NodeRef nodeRef = generateNodeRef();

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef), TYPE_CONTENT)).thenReturn(true);
        when(mockedContentClassificationService.hasClearance(nodeRef)).thenReturn(false);

        assertEquals(null, nodeRefPostMethodInvocationProcessor.process(nodeRef));
    }

    @Test
    public void testCollection_bothNodesConent_userClearedForBoth()
    {
        NodeRef nodeRef1 = generateNodeRef();
        NodeRef nodeRef2 = generateNodeRef();
        ArrayList<NodeRef> nodeRefs = newArrayList(nodeRef1, nodeRef2);

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef1), TYPE_CONTENT)).thenReturn(true);
        when(mockedContentClassificationService.hasClearance(nodeRef1)).thenReturn(true);

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef2), TYPE_CONTENT)).thenReturn(true);
        when(mockedContentClassificationService.hasClearance(nodeRef2)).thenReturn(true);

        assertEquals(nodeRefs, nodeRefPostMethodInvocationProcessor.process(nodeRefs));
    }

    @Test
    public void testCollection_bothNodesContent_userClearedForOne()
    {
        NodeRef nodeRef1 = generateNodeRef();
        NodeRef nodeRef2 = generateNodeRef();
        ArrayList<NodeRef> nodeRefs = newArrayList(nodeRef1, nodeRef2);

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef1), TYPE_CONTENT)).thenReturn(true);
        when(mockedContentClassificationService.hasClearance(nodeRef1)).thenReturn(true);

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef2), TYPE_CONTENT)).thenReturn(true);
        when(mockedContentClassificationService.hasClearance(nodeRef2)).thenReturn(false);

        assertEquals(newArrayList(nodeRef1), nodeRefPostMethodInvocationProcessor.process(nodeRefs));
    }

    @SuppressWarnings("rawtypes")
    @Test
    public void testCollection_bothNodesContent_userClearedForNone()
    {
        NodeRef nodeRef1 = generateNodeRef();
        NodeRef nodeRef2 = generateNodeRef();
        ArrayList<NodeRef> nodeRefs = newArrayList(nodeRef1, nodeRef2);

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef1), TYPE_CONTENT)).thenReturn(true);
        when(mockedContentClassificationService.hasClearance(nodeRef1)).thenReturn(false);

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef2), TYPE_CONTENT)).thenReturn(true);
        when(mockedContentClassificationService.hasClearance(nodeRef2)).thenReturn(false);

        assertEquals(new ArrayList(), nodeRefPostMethodInvocationProcessor.process(unmodifiableCollection(nodeRefs)));
    }

    @Test
    public void testCollection_onlyOneNodeContent_userClearedForBoth()
    {
        NodeRef nodeRef1 = generateNodeRef();
        NodeRef nodeRef2 = generateNodeRef();
        ArrayList<NodeRef> nodeRefs = newArrayList(nodeRef1, nodeRef2);

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef1), TYPE_CONTENT)).thenReturn(false);
        when(mockedContentClassificationService.hasClearance(nodeRef1)).thenReturn(true);

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef2), TYPE_CONTENT)).thenReturn(true);
        when(mockedContentClassificationService.hasClearance(nodeRef2)).thenReturn(true);

        assertEquals(nodeRefs, nodeRefPostMethodInvocationProcessor.process(copyOf(nodeRefs)));
    }

    @Test
    public void testCollection_bothNodesNotContent_userClearedForBoth()
    {
        NodeRef nodeRef1 = generateNodeRef();
        NodeRef nodeRef2 = generateNodeRef();
        ArrayList<NodeRef> nodeRefs = newArrayList(nodeRef1, nodeRef2);

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef1), TYPE_CONTENT)).thenReturn(false);
        when(mockedContentClassificationService.hasClearance(nodeRef1)).thenReturn(true);

        when(mockedDictionaryService.isSubClass(mockedNodeService.getType(nodeRef2), TYPE_CONTENT)).thenReturn(false);
        when(mockedContentClassificationService.hasClearance(nodeRef2)).thenReturn(true);

        assertEquals(nodeRefs, nodeRefPostMethodInvocationProcessor.process(copyOf(nodeRefs)));
    }
}
