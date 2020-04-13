package edu.asu.diging.citesphere.zotero.service.iterator;

import java.util.Iterator;

import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.IGrouping;

public interface CitationIterator extends Iterator<ICitation> {
    
    IGrouping getGrouping();

    boolean hasNext();

    ICitation next();

}