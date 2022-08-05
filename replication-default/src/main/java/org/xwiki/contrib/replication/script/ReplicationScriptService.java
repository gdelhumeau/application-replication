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
package org.xwiki.contrib.replication.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.replication.ReplicationException;
import org.xwiki.contrib.replication.ReplicationInstance;
import org.xwiki.contrib.replication.ReplicationInstanceManager;
import org.xwiki.contrib.replication.ReplicationSender;
import org.xwiki.contrib.replication.ReplicationSenderMessage;
import org.xwiki.contrib.replication.internal.instance.DefaultReplicationInstance;
import org.xwiki.contrib.replication.internal.message.DefaultReplicationSender;
import org.xwiki.contrib.replication.internal.message.ReplicationSenderMessageQueue;
import org.xwiki.contrib.replication.internal.sign.SignatureManager;
import org.xwiki.script.service.ScriptService;
import org.xwiki.script.service.ScriptServiceManager;

/**
 * Entry point of replication related script services.
 * 
 * @version $Id$
 */
@Component
@Named(ReplicationScriptService.ROLEHINT)
@Singleton
public class ReplicationScriptService implements ScriptService
{
    /**
     * The role hint of this component.
     */
    public static final String ROLEHINT = "replication";

    @Inject
    private ScriptServiceManager scriptServiceManager;

    @Inject
    private ReplicationInstanceManager instances;

    @Inject
    private ReplicationSender sender;

    @Inject
    private SignatureManager signatureManager;

    /**
     * @param <S> the type of the {@link ScriptService}
     * @param serviceName the name of the sub {@link ScriptService}
     * @return the {@link ScriptService} or null of none could be found
     */
    @SuppressWarnings("unchecked")
    public <S extends ScriptService> S get(String serviceName)
    {
        return (S) this.scriptServiceManager.get(ReplicationScriptService.ROLEHINT + '.' + serviceName);
    }

    /**
     * @return all instances which been validated on both ends
     * @throws ReplicationException when failing to access instances
     */
    public Collection<ReplicationInstance> getRegisteredInstances() throws ReplicationException
    {
        return this.instances.getRegisteredInstances().stream().map(DefaultReplicationInstance::new)
            .collect(Collectors.toList());
    }

    /**
     * @return the current instance representation
     * @throws ReplicationException when failing to resolve the create the current instance
     */
    public ReplicationInstance getCurrentInstance() throws ReplicationException
    {
        return this.instances.getCurrentInstance();
    }

    /**
     * @param uri the uri of the instance
     * @return the instance
     * @throws ReplicationException when failing to load the instances
     */
    public ReplicationInstance getInstanceByURI(String uri) throws ReplicationException
    {
        return this.instances.getInstanceByURI(uri);
    }

    private ReplicationSenderMessageQueue getQueue(ReplicationInstance instance)
    {
        if (this.sender instanceof DefaultReplicationSender) {
            return ((DefaultReplicationSender) this.sender).getQueue(instance);
        }

        return null;
    }

    /**
     * @param instance the replication instance
     * @return the messages queued to be sent to the provided instance
     */
    public List<ReplicationSenderMessage> getQueueMessages(ReplicationInstance instance)
    {
        List<ReplicationSenderMessage> messages = new ArrayList<>();

        ReplicationSenderMessageQueue queue = getQueue(instance);
        if (queue != null) {
            messages.addAll(queue.getMessages());
        }

        return messages;
    }

    /**
     * @param instance the replication instance
     * @return the message currently being handled
     */
    public ReplicationSenderMessage getQueueMessage(ReplicationInstance instance)
    {
        ReplicationSenderMessageQueue queue = getQueue(instance);
        if (queue != null) {
            return queue.getCurrentMessage();
        }

        return null;
    }

    /**
     * @param instance the replication instance
     * @return the last error thrown while trying to send the last message
     */
    public Throwable getQueueError(ReplicationInstance instance)
    {
        ReplicationSenderMessageQueue queue = getQueue(instance);
        if (queue != null) {
            return queue.getLastError();
        }

        return null;
    }

    /**
     * @param instance the replication instance
     * @return the next date when to try to send the last erroring message
     */
    public Date getQueueNextTry(ReplicationInstance instance)
    {
        ReplicationSenderMessageQueue queue = getQueue(instance);
        if (queue != null) {
            return queue.getNextTry();
        }

        return null;
    }

    /**
     * @param instance the replication instance
     */
    public void wakeUpQueue(ReplicationInstance instance)
    {
        ReplicationSenderMessageQueue queue = getQueue(instance);
        if (queue != null) {
            queue.wakeUp();
        }
    }

    /**
     * @param instance the instance to send data to
     * @return the fingerprint of the public key used to validate signatures sent to the passed instance
     * @throws IOException when failing to calculate the fingerprint
     * @throws ReplicationException when failing to calculate the fingerprint
     */
    public String getSendFingerprint(ReplicationInstance instance) throws IOException, ReplicationException
    {
        return this.signatureManager.getFingerprint(this.signatureManager.getSendKey(instance));
    }

    /**
     * @param instance the instance from which to receive data
     * @return the fingerprint of the public key used to validate signatures sent by the passed instance
     * @throws IOException when failing to calculate the fingerprint
     * @throws ReplicationException when failing to calculate the fingerprint
     */
    public String getReceiveFingerprint(ReplicationInstance instance) throws IOException, ReplicationException
    {
        return this.signatureManager.getFingerprint(instance.getReceiveKey());
    }
}
