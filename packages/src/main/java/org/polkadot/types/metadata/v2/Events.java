package org.polkadot.types.metadata.v2;

import org.polkadot.types.metadata.v1.Events.EventMetadataV1;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/15 10:58 上午
 */
public interface Events {
    class EventMetadataV2 extends EventMetadataV1 {

        public EventMetadataV2(Object value) {
            super(value);
        }
    }
}
