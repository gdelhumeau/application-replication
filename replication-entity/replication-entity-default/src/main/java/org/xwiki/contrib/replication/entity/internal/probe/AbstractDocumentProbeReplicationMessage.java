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
package org.xwiki.contrib.replication.entity.internal.probe;

import java.util.Collection;
import java.util.Map;

import org.xwiki.contrib.replication.entity.internal.AbstractEntityReplicationMessage;
import org.xwiki.contrib.replication.entity.internal.AbstractNoContentDocumentReplicationMessage;
import org.xwiki.model.reference.DocumentReference;

/**
 * @version $Id$
 * @since 1.12.0
 */
public abstract class AbstractDocumentProbeReplicationMessage extends AbstractNoContentDocumentReplicationMessage
{
    /**
     * The message type for these messages.
     */
    public static final String TYPE_PREFIX = AbstractEntityReplicationMessage.TYPE_PREFIX + "probe_";

    /**
     * @param documentReference the reference of the document affected by this message
     * @param receivers the instances which are supposed to handler the message
     * @param extraMetadata custom metadata to add to the message
     */
    @Override
    public void initialize(DocumentReference documentReference, Collection<String> receivers,
        Map<String, Collection<String>> extraMetadata)
    {
        super.initialize(documentReference, receivers, extraMetadata);
    }
}
