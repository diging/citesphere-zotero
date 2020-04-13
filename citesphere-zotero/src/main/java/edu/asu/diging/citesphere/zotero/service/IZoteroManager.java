package edu.asu.diging.citesphere.zotero.service;

import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.zotero.exception.ZoteroHttpStatusException;

public interface IZoteroManager {

    CitationResults getGroupItems(String zoteroUserId, String token, String groupId, int page, String sortBy, Long lastGroupVersion)
            throws ZoteroHttpStatusException;

    ICitationGroup getGroup(String zoteroUserId, String token, String groupId, boolean refresh);

    CitationResults getCollectionItems(String zoteroUserId, String token, String groupId, String collectionId, int page, String sortBy,
            Long lastGroupVersion) throws ZoteroHttpStatusException;

    ICitationCollection getCitationCollection(String zoteroUserId, String token, String groupId, String collectionId);

}