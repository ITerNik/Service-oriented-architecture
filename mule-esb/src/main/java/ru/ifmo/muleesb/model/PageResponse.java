package ru.ifmo.muleesb.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.xml.bind.annotation.*;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
        name = "PageResponse",
        propOrder = {"content", "totalElements", "totalPages", "currentPage", "pageSize"})
@XmlRootElement(name = "pageResponse")
public class PageResponse {

    @XmlElementWrapper(name = "content", required = true)
    @XmlElement(name = "city")
    private List<City> content;

    private Long totalElements;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;

    public PageResponse() {}

    public List<City> getContent() {
        return content;
    }

    public void setContent(List<City> content) {
        this.content = content;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
