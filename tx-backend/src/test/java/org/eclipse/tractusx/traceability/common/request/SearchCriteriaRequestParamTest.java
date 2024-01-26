package org.eclipse.tractusx.traceability.common.request;

import org.eclipse.tractusx.traceability.common.model.SearchCriteria;
import org.eclipse.tractusx.traceability.qualitynotification.domain.base.model.QualityNotificationSide;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchCriteriaRequestParamTest {

//    @Test
//    void testToSearchCriteria() {
//
//        List<String> filter = List.of("status,EQUAL,RECEIVED");
//        String operator = "AND";
//
//        SearchCriteriaRequestParam searchCriteriaRequestParam = new SearchCriteriaRequestParam(filter, operator);
//
//        SearchCriteria searchCriteria = searchCriteriaRequestParam.toSearchCriteria();
//
//        assertEquals("AND", searchCriteria.getSearchCriteriaOperator().toString());
//        assertEquals("status", searchCriteria.getSearchCriteriaFilterList().get(0).getKey());
//        assertEquals("EQUAL", searchCriteria.getSearchCriteriaFilterList().get(0).getStrategy().toString());
//        assertEquals("RECEIVED", searchCriteria.getSearchCriteriaFilterList().get(0).getValue());
//    }

//    @Test
//    void testToSearchCriteriaWithSide() {
//
//        List<String> filter = List.of("status,EQUAL,RECEIVED");
//        String operator = "AND";
//
//        SearchCriteriaRequestParam searchCriteriaRequestParam = new SearchCriteriaRequestParam(filter, operator);
//
//        SearchCriteria searchCriteria = searchCriteriaRequestParam.toSearchCriteria(QualityNotificationSide.RECEIVER);
//
//        assertEquals(operator, searchCriteria.getSearchCriteriaOperator().toString());
//        assertEquals("status", searchCriteria.getSearchCriteriaFilterList().get(0).getKey());
//        assertEquals("EQUAL", searchCriteria.getSearchCriteriaFilterList().get(0).getStrategy().toString());
//        assertEquals("RECEIVED", searchCriteria.getSearchCriteriaFilterList().get(0).getValue());
//        assertEquals("side", searchCriteria.getSearchCriteriaFilterList().get(1).getKey());
//        assertEquals("EQUAL", searchCriteria.getSearchCriteriaFilterList().get(1).getStrategy().toString());
//        assertEquals("RECEIVER", searchCriteria.getSearchCriteriaFilterList().get(1).getValue());
//    }

//    @Test
//    void testToSearchCriteriaWithSideOnly() {
//
//        SearchCriteriaRequestParam searchCriteriaRequestParam = new SearchCriteriaRequestParam(null, null);
//
//        SearchCriteria searchCriteria = searchCriteriaRequestParam.toSearchCriteria(QualityNotificationSide.SENDER);
//
//        assertEquals("AND", searchCriteria.getSearchCriteriaOperator().toString());
//        assertEquals("side", searchCriteria.getSearchCriteriaFilterList().get(0).getKey());
//        assertEquals("EQUAL", searchCriteria.getSearchCriteriaFilterList().get(0).getStrategy().toString());
//        assertEquals("SENDER", searchCriteria.getSearchCriteriaFilterList().get(0).getValue());
//    }
}
