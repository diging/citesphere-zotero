package edu.asu.diging.citesphere.zotero.service.iterator.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.PageRequest;

import edu.asu.diging.citesphere.data.bib.GroupCitationMappingRepository;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.IGrouping;
import edu.asu.diging.citesphere.model.bib.impl.CitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.GroupCitationMapping;
import edu.asu.diging.citesphere.zotero.service.iterator.CitationIterator;

public class GroupCitationIterator implements Iterator<ICitation>, CitationIterator {
    
    private GroupCitationMappingRepository repo;
    private Iterator<GroupCitationMapping> mappings;
    private long totalPages;
    private int currentPage;
    private int pageSize;
    private ICitationGroup group;
    
    public GroupCitationIterator(GroupCitationMappingRepository repo, ICitationGroup group, int pageSize) {
        this.repo = repo;
        this.pageSize = pageSize;
        this.currentPage = 0;
        this.group = group;
        long totalCount = repo.countByGroup((CitationGroup)group);
        totalPages = totalCount/pageSize + (totalCount%pageSize > 0 ? 1 : 0);
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.exporter.core.service.iterator.impl.CitationIteratorX#hasNext()
     */
    @Override
    public boolean hasNext() {
        if ((mappings == null || !mappings.hasNext()) && currentPage>=totalPages) {
            return false;
        }
        
        if (mappings == null || !mappings.hasNext()) {
            List<GroupCitationMapping> ccMappings = repo.findByGroup((CitationGroup)group, PageRequest.of(currentPage, pageSize));
            currentPage += 1;
            mappings = ccMappings.iterator();
        }
        
        return mappings.hasNext();
    }

    /* (non-Javadoc)
     * @see edu.asu.diging.citesphere.exporter.core.service.iterator.impl.CitationIteratorX#next()
     */
    @Override
    public ICitation next() {
        GroupCitationMapping mapping = mappings.next();
        return mapping.getCitation();
    }

    @Override
    public IGrouping getGrouping() {
        return group;
    }

}
