package edu.asu.diging.citesphere.zotero.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.zotero.api.Collection;
import org.springframework.social.zotero.api.Group;
import org.springframework.social.zotero.api.Item;
import org.springframework.social.zotero.api.Zotero;
import org.springframework.social.zotero.api.ZoteroResponse;
import org.springframework.social.zotero.connect.ZoteroConnectionFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import edu.asu.diging.citesphere.zotero.exception.AccessForbiddenException;
import edu.asu.diging.citesphere.zotero.exception.ZoteroHttpStatusException;
import edu.asu.diging.citesphere.zotero.service.IZoteroConnector;

@Component
@PropertySource("classpath:/config.properties")
public class ZoteroConnector implements IZoteroConnector {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${_zotero_page_size}")
    private Integer zoteroPageSize;

    @Autowired
    private ZoteroConnectionFactory zoteroFactory;

    /*
     * (non-Javadoc)
     * 
     * @see
     * edu.asu.diging.citesphere.core.service.impl.IZoteroConnector#getGroupItems(
     * edu.asu.diging.citesphere.core.model.IUser, java.lang.String, int)
     */
    @Override
    @Cacheable(value = "groupItems", key = "#user.username + '_' + #groupId + '_' + #page + '_' + #sortBy + '_' + #lastGroupVersion")
    public ZoteroResponse<Item> getGroupItems(String zoteroUserId, String token, String groupId, int page, String sortBy,
            Long lastGroupVersion) throws ZoteroHttpStatusException {
        Zotero zotero = getApi(zoteroUserId, token);
        if (page < 1) {
            page = 0;
        } else {
            page = page - 1;
        }
        try {
            return zotero.getGroupsOperations().getGroupItemsTop(groupId, page * zoteroPageSize, zoteroPageSize, sortBy,
                    lastGroupVersion);
        } catch (HttpClientErrorException ex) {
            throw createException(ex.getStatusCode(), ex);
        }
    }

    

    @Override
    @Cacheable(value = "groupItemsLimit", key = "#user.username + '_' + #groupId + '_' + #limit + '_' + #sortBy")
    public ZoteroResponse<Item> getGroupItemsWithLimit(String zoteroUserId, String token, String groupId, int limit, String sortBy,
            Long lastGroupVersion) {
        Zotero zotero = getApi(zoteroUserId, token);
        if (limit < 1) {
            limit = 1;
        }
        return zotero.getGroupsOperations().getGroupItemsTop(groupId, 0, limit, sortBy, lastGroupVersion);
    }

    @Override
    @Cacheable(value = "groupCache", key = "#user.username + '_' + #groupId", condition = "#forceRefresh==false")
    public Group getGroup(String zoteroUserId, String token, String groupId, boolean forceRefresh) {
        Zotero zotero = getApi(zoteroUserId, token);
        return zotero.getGroupsOperations().getGroup(groupId);
    }

    @Override
    public Item getItem(String zoteroUserId, String token, String groupId, String itemKey) throws ZoteroHttpStatusException {
        Zotero zotero = getApi(zoteroUserId, token);
        try  {
            return zotero.getGroupsOperations().getGroupItem(groupId, itemKey);
        } catch(HttpClientErrorException ex) {
            throw createException(ex.getStatusCode(), ex);
        }
    }

    @Override
    @Cacheable(value = "singleCollections", key = "#user.username + '_' + #collectionId + '_' + #groupId")
    public Collection getCitationCollection(String zoteroUserId, String token, String groupId, String collectionId) {
        return getApi(zoteroUserId, token).getGroupCollectionsOperations().getCollection(groupId, collectionId);
    }

    @Override
    @Cacheable(value = "collectionItems", key = "#user.username + '_' + #groupId + '_' + #collectionId + '_' + #page + '_' + #sortBy + '_' + #lastGroupVersion")
    public ZoteroResponse<Item> getCollectionItems(String zoteroUserId, String token, String groupId, String collectionId, int page,
            String sortBy, Long lastGroupVersion) throws ZoteroHttpStatusException {
        Zotero zotero = getApi(zoteroUserId, token);
        if (page < 1) {
            page = 0;
        } else {
            page = page - 1;
        }
        try {
        return zotero.getGroupCollectionsOperations().getItems(groupId, collectionId, page * zoteroPageSize,
                zoteroPageSize, sortBy, lastGroupVersion);
        } catch(HttpClientErrorException ex) {
            throw createException(ex.getStatusCode(), ex);
        }
    }

    private Zotero getApi(String zoteroUserId, String tokenSecret) {
        Zotero zotero = zoteroFactory.createConnection(new OAuthToken(tokenSecret, tokenSecret)).getApi();
        zotero.setUserId(zoteroUserId);
        return zotero;
    }
    
    private ZoteroHttpStatusException createException(HttpStatus status, Exception cause) {
        if (status == HttpStatus.FORBIDDEN) {
            return new AccessForbiddenException(cause);
        }
        return new ZoteroHttpStatusException(cause);
    }
}
