package org.polkadot.types.metadata.v3;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/15 11:24 上午
 */
public interface Events {
    class EventMetadataV3 extends org.polkadot.types.metadata.v2.Events.EventMetadataV2 {

        public EventMetadataV3(Object value) {
            super(value);
        }
    }
}
