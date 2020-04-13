package edu.asu.diging.citesphere.zotero.service;

import edu.asu.diging.citesphere.zotero.exception.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.zotero.service.impl.ZoteroInfo;
import edu.asu.diging.citesphere.zotero.service.iterator.CitationIterator;

public interface ICitationManager {

    CitationIterator getCitations(ZoteroInfo info) throws ZoteroHttpStatusException;

}