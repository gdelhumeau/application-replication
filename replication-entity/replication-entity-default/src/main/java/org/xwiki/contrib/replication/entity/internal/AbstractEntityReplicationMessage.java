/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.replication.entity.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.inject.Inject;

import org.xwiki.bridge.DocumentAccessBridge;
import org.xwiki.contrib.replication.AbstractReplicationSenderMessage;
import org.xwiki.model.reference.AbstractLocalizedEntityReference;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.user.UserReferenceSerializer;

/**
 * @param <E> the type of reference
 * @version $Id$
 */
public abstract class AbstractEntityReplicationMessage<E extends EntityReference>
    extends AbstractReplicationSenderMessage
{
    /**
     * The type of message supported by this receiver.
     */
    public static final String TYPE_PREFIX = "entity_";

    /**
     * The prefix in front of all entity metadata properties.
     */
    public static final String METADATA_PREFIX = TYPE_PREFIX.toUpperCase();

    /**
     * The name of the metadata containing the reference of the entity in the message.
     */
    public static final String METADATA_REFERENCE = METADATA_PREFIX + "REFERENCE";

    /**
     * The name of the metadata containing the locale of the entity in the message.
     */
    public static final String METADATA_LOCALE = METADATA_PREFIX + "LOCALE";

    /**
     * The name of the metadata containing the reference of the user in the context.
     */
    public static final String METADATA_CONTEXT_USER = METADATA_PREFIX + "CONTEXT_USER";

    /**
     * The name of the metadata containing the creator of the document.
     */
    public static final String METADATA_CREATOR = METADATA_PREFIX + "CREATOR";

    /**
     * The name of the metadata used to group various types of messages for recovering needs.
     * 
     * @since 1.1
     */
    public static final String METADATA_RECOVER_TYPE = METADATA_PREFIX + "RECOVER_TYPE";

    @Inject
    protected UserReferenceSerializer<String> userReferenceSerializer;

    @Inject
    protected DocumentAccessBridge documentAccessBridge;

    protected E entityReference;

    /**
     * @param entityReference the reference of the document affected by this message
     * @param receivers the instances which are supposed to handler the message
     * @param extraMetadata custom metadata to add to the message
     * @since 1.1
     */
    protected void initialize(E entityReference, Collection<String> receivers,
        Map<String, Collection<String>> extraMetadata)
    {
        initialize();

        this.entityReference = entityReference;

        if (extraMetadata != null) {
            this.modifiableMetadata.putAll(extraMetadata);
        }

        // Make sure to use the EntityReference converter (otherwise it won't unserialize to the right type)
        putCustomMetadata(METADATA_REFERENCE, entityReference.getClass() == EntityReference.class ? entityReference
            : new EntityReference(entityReference));

        if (entityReference instanceof AbstractLocalizedEntityReference) {
            putCustomMetadata(METADATA_LOCALE, ((AbstractLocalizedEntityReference) entityReference).getLocale());
        }

        putCustomMetadata(METADATA_CONTEXT_USER, this.documentAccessBridge.getCurrentUserReference());

        this.receivers = receivers != null ? Collections.unmodifiableCollection(receivers) : null;
    }
}
