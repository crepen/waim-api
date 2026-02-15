package com.waim.api.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"empty", "first", "last"})
public class CommonPageable {

    private int page;
    private int size;

    @JsonProperty("page_element")
    private int pageElement;

    @JsonProperty("total_page")
    private int totalPage;

    @JsonProperty("total_element")
    private long totalElement;

    @JsonProperty("is_empty")
    private boolean isEmpty;

    @JsonProperty("is_first")
    private boolean isFirst;

    @JsonProperty("is_last")
    private boolean isLast;


    public static CommonPageable cast(Page<?> pageable){
        return CommonPageable.builder()
                .page(pageable.getNumber())
                .size(pageable.getSize())
                .pageElement(pageable.getNumberOfElements())
                .isEmpty(pageable.isEmpty())
                .isFirst(pageable.isFirst())
                .isLast(pageable.isLast())
                .totalPage(pageable.getTotalPages())
                .totalElement(pageable.getTotalElements())
                .build();
    }
}
