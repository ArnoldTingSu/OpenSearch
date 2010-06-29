/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action.admin.indices.alias;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.action.TransportActions;
import org.elasticsearch.action.support.master.TransportMasterNodeOperationAction;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.block.ClusterBlockLevel;
import org.elasticsearch.cluster.metadata.AliasAction;
import org.elasticsearch.cluster.metadata.MetaDataService;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.threadpool.ThreadPool;
import org.elasticsearch.transport.TransportService;

/**
 * @author kimchy (shay.banon)
 */
public class TransportIndicesAliasesAction extends TransportMasterNodeOperationAction<IndicesAliasesRequest, IndicesAliasesResponse> {

    private final MetaDataService metaDataService;

    @Inject public TransportIndicesAliasesAction(Settings settings, TransportService transportService, ClusterService clusterService,
                                                 ThreadPool threadPool, MetaDataService metaDataService) {
        super(settings, transportService, clusterService, threadPool);
        this.metaDataService = metaDataService;
    }

    @Override protected String transportAction() {
        return TransportActions.Admin.Indices.ALIASES;
    }

    @Override protected IndicesAliasesRequest newRequest() {
        return new IndicesAliasesRequest();
    }

    @Override protected IndicesAliasesResponse newResponse() {
        return new IndicesAliasesResponse();
    }

    @Override protected void checkBlock(IndicesAliasesRequest request, ClusterState state) {
        for (AliasAction aliasAction : request.aliasActions()) {
            state.blocks().indexBlockedRaiseException(ClusterBlockLevel.METADATA, aliasAction.index());
        }
    }

    @Override protected IndicesAliasesResponse masterOperation(IndicesAliasesRequest request) throws ElasticSearchException {
        MetaDataService.IndicesAliasesResult indicesAliasesResult = metaDataService.indicesAliases(request.aliasActions());
        return new IndicesAliasesResponse();
    }
}
