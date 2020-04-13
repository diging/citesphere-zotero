package edu.asu.diging.citesphere.zotero.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.zotero.api.Collection;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.stereotype.Service;

import edu.asu.diging.citesphere.factory.ICitationCollectionFactory;
import edu.asu.diging.citesphere.factory.ICitationFactory;
import edu.asu.diging.citesphere.factory.IGroupFactory;
import edu.asu.diging.citesphere.model.bib.ICitation;
import edu.asu.diging.citesphere.model.bib.ICitationCollection;
import edu.asu.diging.citesphere.model.bib.ICitationGroup;
import edu.asu.diging.citesphere.model.bib.impl.CitationResults;
import edu.asu.diging.citesphere.zotero.exception.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.zotero.service.IZoteroConnector;
import edu.asu.diging.citesphere.zotero.service.IZoteroManager;

@Service
public class ZoteroManager implements IZoteroManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private IZoteroConnector zoteroConnector;

    @Autowired
    private ICitationFactory citationFactory;

    @Autowired
    private IGroupFactory groupFactory;

    @Autowired
    private ICitationCollectionFactory collectionFactory;

    public CitationResults getGroupItems(String zoteroUserId, String token, String groupId, int page, String sortBy, Long lastGroupVersion) throws ZoteroHttpStatusException {
        ZoteroResponse<Item> response = zoteroConnector.getGroupItems(zoteroUserId, token, groupId, page, sortBy, lastGroupVersion);
        return createCitationResults(response);
    }

    @Override
    public ICitationGroup getGroup(String zoteroUserId, String token, String groupId, boolean forceRefresh) {
        Group group = zoteroConnector.getGroup(zoteroUserId, token, groupId, forceRefresh);
        ZoteroResponse<Item> groupItems = zoteroConnector.getGroupItemsWithLimit(zoteroUserId, token, group.getId() + "", 1, null,
                null);
        ICitationGroup citGroup = groupFactory.createGroup(group);
        citGroup.setNumItems(groupItems.getTotalResults());
        return citGroup;
    }

    @Override
    public ICitationCollection getCitationCollection(String zoteroUserId, String token, String groupId, String collectionId) {
        Collection collection = zoteroConnector.getCitationCollection(zoteroUserId, token, groupId, collectionId);
        return collectionFactory.createCitationCollection(collection);
    }

    @Override
    public CitationResults getCollectionItems(String zoteroUserId, String token, String groupId, String collectionId, int page, String sortBy,
            Long lastGroupVersion) throws ZoteroHttpStatusException {
        ZoteroResponse<Item> response = zoteroConnector.getCollectionItems(zoteroUserId, token, groupId, collectionId, page, sortBy,
                lastGroupVersion);
        return createCitationResults(response);
    }

    private CitationResults createCitationResults(ZoteroResponse<Item> response) {
        List<ICitation> citations = new ArrayList<>();
        if (response.getResults() != null) {
            for (Item item : response.getResults()) {
                citations.add(citationFactory.createCitation(item));
            }
        }
        CitationResults results = new CitationResults();
        results.setTotalResults(response.getTotalResults());
        if (response.getNotModified() != null && response.getNotModified()) {
            results.setNotModified(true);
            return results;
        }
        results.setCitations(citations);
        return results;
    }
}
