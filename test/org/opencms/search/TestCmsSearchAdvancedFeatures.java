/*
 * File   : $Source: /alkacon/cvs/opencms/test/org/opencms/search/TestCmsSearchAdvancedFeatures.java,v $
 * Date   : $Date: 2005/03/26 11:36:35 $
 * Version: $Revision: 1.2 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (C) 2002 - 2005 Alkacon Software (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.search;

import org.opencms.file.CmsObject;
import org.opencms.file.CmsProperty;
import org.opencms.main.I_CmsConstants;
import org.opencms.main.OpenCms;
import org.opencms.report.CmsShellReport;
import org.opencms.test.OpenCmsTestCase;
import org.opencms.test.OpenCmsTestProperties;
import org.opencms.util.CmsDateUtil;
import org.opencms.util.CmsStringUtil;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Unit test for advanced search features.<p>
 * 
 * @author Alexander Kandzior (a.kandzior@alkacon.com)
 * @version $Revision: 1.2 $
 */
public class TestCmsSearchAdvancedFeatures extends OpenCmsTestCase {

    /** Name of the index used for testing. */
    public static final String INDEX_OFFLINE = "Offline project (VFS)";
    
    /** The index used for testing. */
    public static final String INDEX_ONLINE = "Online project (VFS)";

    /**
     * Default JUnit constructor.<p>
     * 
     * @param arg0 JUnit parameters
     */
    public TestCmsSearchAdvancedFeatures(String arg0) {

        super(arg0);
    }

    /**
     * Test suite for this test class.<p>
     * 
     * @return the test suite
     */
    public static Test suite() {

        OpenCmsTestProperties.initialize(org.opencms.test.AllTests.TEST_PROPERTIES_PATH);

        TestSuite suite = new TestSuite();
        suite.setName(TestCmsSearchAdvancedFeatures.class.getName());
        
        suite.addTest(new TestCmsSearchAdvancedFeatures("testSortSearchResults"));
        suite.addTest(new TestCmsSearchAdvancedFeatures("testSearchCategories"));

        TestSetup wrapper = new TestSetup(suite) {

            protected void setUp() {

                setupOpenCms("simpletest", "/sites/default/");
            }

            protected void tearDown() {

                removeOpenCms();
            }
        };

        return wrapper;
    }
    
    /**
     * Tests search category grouping.<p>
     * 
     * @throws Exception if the test fails
     */
    public void testSearchCategories() throws Exception {
        
        CmsObject cms = getCmsObject();
        echo("Testing searching for categories");        
        
        // perform a search on the newly generated index
        CmsSearch searchBean = new CmsSearch();
        List searchResult;               
        String query = "OpenCms";
        
        // apply search categories to some folders
        
        CmsProperty cat1 = new CmsProperty(I_CmsConstants.C_PROPERTY_SEARCH_CATEGORY, "category_1", null, true);
        CmsProperty cat2 = new CmsProperty(I_CmsConstants.C_PROPERTY_SEARCH_CATEGORY, "category_2", null, true);
        CmsProperty cat3 = new CmsProperty(I_CmsConstants.C_PROPERTY_SEARCH_CATEGORY, "category_3", null, true);
        
        cms.lockResource("/folder1/");
        cms.writePropertyObject("/folder1/", cat1);
        cms.unlockResource("/folder1/");
        cms.lockResource("/folder2/");
        cms.writePropertyObject("/folder2/", cat2);
        cms.unlockResource("/folder2/");      
        cms.lockResource("/types/");
        cms.writePropertyObject("/types/", cat3);
        cms.unlockResource("/types/");             
        
        // update the search index used
        OpenCms.getSearchManager().updateIndex(INDEX_OFFLINE, new CmsShellReport());       
        
        searchBean.init(cms);
        searchBean.setIndex(INDEX_OFFLINE);                        
        searchBean.setQuery(query);
        searchBean.setCalculateCategories(true);
        
        // first run is default sort order
        searchResult = searchBean.getSearchResult();        
        Iterator i = searchResult.iterator();
        System.out.println("Result sorted by relevance:");       
        while (i.hasNext()) {
            CmsSearchResult res = (CmsSearchResult)i.next();
            System.out.print(CmsStringUtil.padRight(cms.getRequestContext().removeSiteRoot(res.getPath()), 50));            
            System.out.print(CmsStringUtil.padRight(res.getTitle(), 40));              
            System.out.println("  score: " + res.getScore());               
        }
        
        Map categories = searchBean.getSearchResultCategories();
        // make sure categories where found
        assertNotNull(categories);
        // print the categories 
        System.out.println(CmsSearchCategoryCollector.formatCategoryMap(categories));
        // make sure the results are as expected
        assertTrue(categories.containsKey(cat1.getValue()));
        assertTrue(categories.containsKey(cat2.getValue()));
        assertTrue(categories.containsKey(cat3.getValue()));
        assertTrue(categories.containsKey(CmsSearchCategoryCollector.UNKNOWN_CATEGORY));
        // result must be all 3 categories plus 1 for "unknown"
        assertEquals(4, categories.size());        
        // for "category_3" (/types folder) there must be exactly 1 result
        assertEquals(new Integer(1), categories.get(cat3.getValue()));
        // for "unknown" there must be exactly 1 result
        assertEquals(new Integer(1), categories.get(CmsSearchCategoryCollector.UNKNOWN_CATEGORY));
    }
 
    /**
     * Tests sorting of search results.<p>
     * 
     * @throws Exception if the test fails
     */
    public void testSortSearchResults() throws Exception {
        
        CmsObject cms = getCmsObject();
        echo("Testing sorting of search results");        
        
        // perform a search on the newly generated index
        CmsSearch searchBean = new CmsSearch();
        List searchResult;               
        String query = "OpenCms";
                
        // update the search index used
        OpenCms.getSearchManager().updateIndex(INDEX_OFFLINE, new CmsShellReport());       
        
        searchBean.init(cms);
        searchBean.setIndex(INDEX_OFFLINE);                        
        searchBean.setQuery(query);
                
        // first run is default sort order
        searchResult = searchBean.getSearchResult();        
        Iterator i = searchResult.iterator();
        System.out.println("Result sorted by relevance:");       
        while (i.hasNext()) {
            CmsSearchResult res = (CmsSearchResult)i.next();
            System.out.print(CmsStringUtil.padRight(cms.getRequestContext().removeSiteRoot(res.getPath()), 50));                   
            System.out.print(CmsStringUtil.padRight(res.getTitle(), 40));               
            System.out.print(CmsDateUtil.getHeaderDate(res.getDateLastModified().getTime()));               
            System.out.println("  score: " + res.getScore());               
        }
        
        // second run use Title sort order
        String lastTitle = null;
        searchBean.setSortOrder(CmsSearch.SORT_TITLE);
        searchResult = searchBean.getSearchResult();        
        i = searchResult.iterator();
        System.out.println("Result sorted by title:");       
        while (i.hasNext()) {
            CmsSearchResult res = (CmsSearchResult)i.next();
            System.out.print(CmsStringUtil.padRight(cms.getRequestContext().removeSiteRoot(res.getPath()), 50));                      
            System.out.print(CmsStringUtil.padRight(res.getTitle(), 40));               
            System.out.print(CmsDateUtil.getHeaderDate(res.getDateLastModified().getTime()));               
            System.out.println("  score: " + res.getScore());
            if (lastTitle != null) {
                // make sure result is sorted correctly
                assertTrue(lastTitle.compareTo(res.getTitle()) <= 0);
            }
            lastTitle = res.getTitle();
        }
        
        // third run use date last modified
        long lastTime = 0;
        searchBean.setSortOrder(CmsSearch.SORT_DATE_LASTMODIFIED);
        searchResult = searchBean.getSearchResult();        
        i = searchResult.iterator();
        System.out.println("Result sorted by date last modified:");       
        while (i.hasNext()) {
            CmsSearchResult res = (CmsSearchResult)i.next();
            System.out.print(CmsStringUtil.padRight(cms.getRequestContext().removeSiteRoot(res.getPath()), 50));         
            System.out.print(CmsStringUtil.padRight(res.getTitle(), 40));               
            System.out.print(CmsDateUtil.getHeaderDate(res.getDateLastModified().getTime()));               
            System.out.println("  score: " + res.getScore());
            if (lastTime > 0) {
                // make sure result is sorted correctly
                assertTrue(lastTime >= res.getDateLastModified().getTime());
                assertTrue(res.getScore() <= 100);
            }
            lastTime = res.getDateLastModified().getTime();
        }
        
        assertNull(searchBean.getSearchResultCategories());
    }
    
}