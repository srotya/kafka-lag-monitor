/**
 * Copyright 2016 Symantec Corporation.
 * 
 * Licensed under the Apache License, Version 2.0 (the “License”); 
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.symantec.cpe.analytics.core.kafka;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Broker {
    @JsonProperty
    String host;

    @JsonProperty
    Integer port;

    @JsonProperty
    Integer id;

    public Broker(String host, Integer port, Integer id) {
        this.host = host;
        this.port = port;
        this.id = id;
    }

    public Broker() {
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Broker{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", id=" + id +
                '}';
    }
}
