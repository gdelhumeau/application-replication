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
package org.xwiki.contrib.replication.entity.internal.repair;

import java.util.Collection;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.replication.ReplicationException;
import org.xwiki.contrib.replication.ReplicationReceiverMessage;
import org.xwiki.contrib.replication.entity.internal.update.DocumentUpdateConflictResolver;
import org.xwiki.contrib.replication.entity.internal.update.DocumentUpdateReplicationReceiver;
import org.xwiki.model.reference.DocumentReference;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

/**
 * @version $Id$
 */
@Component
@Singleton
@Named(DocumentRepairReplicationMessage.TYPE)
public class DocumentRepairReplicationReceiver extends DocumentUpdateReplicationReceiver
{
    @Inject
    private DocumentUpdateConflictResolver conflict;

    /**
     * Only the owner is allowed to send this type of messages.
     */
    public DocumentRepairReplicationReceiver()
    {
        this.ownerOnly = true;
    }

    @Override
    protected void receiveDocument(ReplicationReceiverMessage message, DocumentReference documentReference,
        XWikiContext xcontext) throws ReplicationException
    {
        super.receiveDocument(message, documentReference, xcontext);

        // Get the author involved in the conflict
        Collection<String> authors =
            message.getCustomMetadata().get(DocumentRepairReplicationMessage.METADATA_CONFLICT_AUTHORS);

        if (authors != null) {
            try {
                // Notify about the conflict
                this.conflict.notifyConflict(xcontext.getWiki().getDocument(documentReference, xcontext), authors);
            } catch (XWikiException e) {
                this.logger.error("Failed to generate conflict notification for document [{}]", documentReference, e);
            }
        }
    }
}
