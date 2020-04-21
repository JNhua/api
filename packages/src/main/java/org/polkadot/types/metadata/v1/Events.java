package org.polkadot.types.metadata.v1;

import org.polkadot.types.metadata.v0.Events.EventMetadataV0;

public interface Events {

    /**
     * The definition of an event
     *
     * @author Jenner
     */
    class EventMetadataV1 extends EventMetadataV0 {

        public EventMetadataV1(Object value) {
            super(value);
        }
    }

}
