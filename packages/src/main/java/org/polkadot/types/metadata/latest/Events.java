package org.polkadot.types.metadata.latest;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/20 3:54 下午
 */
public interface Events {
    class EventMetadataLatest extends org.polkadot.types.metadata.v11.Events.EventMetadataV11 {

        public EventMetadataLatest(Object value) {
            super(value);
        }
    }
}
