package edu.asu.diging.citesphere.zotero.service;

import org.springframework.social.zotero.api.Collection;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.ZoteroResponse;

import edu.asu.diging.citesphere.zotero.exception.ZoteroHttpStatusException;

public interface IZoteroConnector {

    ZoteroResponse<Item> getGroupItems(String zoteroUserId, String token, String groupId, int page, String sortBy, Long lastGroupVersion)
            throws ZoteroHttpStatusException;

    Item getItem(String zoteroUserId, String token, String groupId, String itemKey) throws ZoteroHttpStatusException;

    ZoteroResponse<Item> getGroupItemsWithLimit(String zoteroUserId, String token, String groupId, int limit, String sortBy,
            Long lastGroupVersion);

    Group getGroup(String zoteroUserId, String token, String groupId, boolean forceRefresh);

    ZoteroResponse<Item> getCollectionItems(String zoteroUserId, String token, String groupId, String collectionId, int page, String sortBy,
            Long lastGroupVersion) throws ZoteroHttpStatusException;

    Collection getCitationCollection(String zoteroUserId, String token, String groupId, String collectionId);

}