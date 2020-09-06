package org.zakharov.springboot.google.appe.javaspringbootgoogleappe.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductFilterModel {
    public enum Order {Asc, Desc}
    public List<Long> categories;
    public String orderBy;
    public Order sortingDirection;
}
