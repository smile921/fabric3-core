/*
 * Fabric3
 * Copyright (c) 2009-2015 Metaform Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fabric3.monitor.appender.console;

import java.io.IOException;
import java.nio.ByteBuffer;

import org.fabric3.monitor.spi.appender.Appender;

/**
 * Writes monitor events to the console.
 */
public class ConsoleAppender implements Appender {

    public void write(ByteBuffer buffer) throws IOException {
        for (int i = 0; i < buffer.limit(); i++) {
            System.out.write(buffer.get(i));
        }
    }

    public void start() throws IOException {
        // no-op
    }

    public void stop() throws IOException {
        // no-op
    }

}
