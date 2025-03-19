package org.eclipse.tractusx.traceability.common.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.eclipse.tractusx.traceability.common.model.*;
import org.eclipse.tractusx.traceability.common.request.exception.InvalidFilterException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetRequest {
    private int page;
    private int size;
    private String operator;
    private List<String> sort = new ArrayList<>();
    private AssetFilter filter;

    public SearchCriteria toSearchCriteria(BaseRequestFieldMapper fieldMapper) {
        List<SearchCriteriaFilter> filterList = new ArrayList<>();

        if (filter != null) {
            extractFilters(filterList, filter, fieldMapper);
        }

        return SearchCriteria.builder()
                .searchCriteriaFilterList(filterList)
                .build();
    }

    private void extractFilters(List<SearchCriteriaFilter> filterList, AssetFilter filter, BaseRequestFieldMapper fieldMapper) {
        for (Field field : AssetFilter.class.getDeclaredFields()) {
            try {
                FilterAttribute filterAttribute = (FilterAttribute) field.get(filter);
                if (filterAttribute != null && filterAttribute.getValue() != null && !filterAttribute.getValue().isEmpty()) {
                    addMappedFieldToFilterList(filterList, fieldMapper, field, filterAttribute);
                }
            } catch (IllegalAccessException e) {
                throw new InvalidFilterException(String.format("Invalid filter param provided filter={%s}", filter));
            }
        }
    }

    private void addMappedFieldToFilterList(List<SearchCriteriaFilter> filterList, BaseRequestFieldMapper fieldMapper, Field field, FilterAttribute filterAttribute) {
        String mappedFieldName = fieldMapper.mapRequestFieldName(field.getName());

        for (FilterValue filterValue : filterAttribute.getValue()) {
            if (filterValue.getValue() != null && !filterValue.getValue().isBlank()) {
                filterList.add(createFilter(mappedFieldName, filterValue));
            }
        }
    }

    private SearchCriteriaFilter createFilter(String key, FilterValue filterValue) {
        return SearchCriteriaFilter.builder()
                .key(key)
                .strategy(SearchCriteriaStrategy.fromValue(filterValue.getStrategy()))
                .value(filterValue.getValue())
                .operator(SearchCriteriaOperator.fromValue(operator))
                .build();
    }

    @Data
    static class FilterValue {
        private String value;
        private String strategy;
    }

    @Data
    static class FilterAttribute {
        private List<FilterValue> value = new ArrayList<>();
        private String operator;
    }

    @Data
    static class AssetFilter {
        private FilterAttribute id;
        private FilterAttribute idShort;
        private FilterAttribute name;
        private FilterAttribute manufacturerName;
        private FilterAttribute businessPartner;
        private FilterAttribute partId;
        private FilterAttribute manufacturerPartId;
        private FilterAttribute customerPartId;
        private FilterAttribute contractAgreementId;
        private FilterAttribute classification;
        private FilterAttribute nameAtCustomer;
        private FilterAttribute semanticModelId;
        private List<String> semanticDataModel;
        private FilterAttribute manufacturingDate;
        private FilterAttribute manufacturingCountry;
        private FilterAttribute manufacturerId;
        private FilterAttribute productType;
        private FilterAttribute tractionBatteryCode;
        private FilterAttribute owner;
        private FilterAttribute qualityType;
        private FilterAttribute nameAtManufacturer;
        private FilterAttribute van;
        private FilterAttribute sentQualityAlertIdsInStatusActive;
        private FilterAttribute sentQualityInvestigationIdsInStatusActive;
        private FilterAttribute receivedQualityAlertIdsInStatusActive;
        private FilterAttribute receivedQualityInvestigationIdsInStatusActive;
        private FilterAttribute importState;
        private FilterAttribute importNote;
        private FilterAttribute validityPeriodFrom;
        private FilterAttribute validityPeriodTo;
        private FilterAttribute functionValidUntil;
        private FilterAttribute functionValidFrom;
        private FilterAttribute function;
        private FilterAttribute catenaxSiteId;
    }
}
