package org.polkadot.types.type;

import com.google.common.primitives.UnsignedBytes;
import org.apache.commons.lang3.ArrayUtils;
import org.polkadot.types.Types;
import org.polkadot.types.codec.*;
import org.polkadot.types.metadata.latest.MetadataLatest;
import org.polkadot.types.metadata.latest.Events;
import org.polkadot.types.metadata.latest.Modules;
import org.polkadot.types.primitive.Null;
import org.polkadot.utils.MapUtils;
import org.polkadot.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * A representation of a system event. These are generated via the Metadata interfaces and
 * specific to a specific Substrate runtime
 */
public class Event extends Struct {
    private static final Logger logger = LoggerFactory.getLogger(Event.class);

    public static final Map<String, Types.ConstructorCodec<EventData>> EventTypes = new ConcurrentHashMap<>();

    /**
     * Wrapper for the actual data that forms part of an Event
     */
    public static class EventData extends Tuple {
        private Events.EventMetadataLatest meta;
        private String method;
        private String section;
        private List<CreateType.TypeDef> typeDef;

        public EventData(List<Types.ConstructorCodec> types,
                         byte[] value,
                         List<CreateType.TypeDef> typeDef,
                         Events.EventMetadataLatest meta,
                         String section, String method) {
            super(new Types.ConstructorDef(types), value);

            System.out.println("EventData " + method + " : " + section);

            this.meta = meta;
            this.method = method;
            this.section = section;
            this.typeDef = typeDef;
        }


        public Events.EventMetadataLatest getMeta() {
            return meta;
        }

        public String getMethod() {
            return method;
        }

        public String getSection() {
            return section;
        }

        public List<CreateType.TypeDef> getTypeDef() {
            return typeDef;
        }

        public static class Builder implements Types.ConstructorCodec<EventData> {

            private List<Types.ConstructorCodec> types;
            private Events.EventMetadataLatest meta;
            private String method;
            private String section;
            private List<CreateType.TypeDef> typeDef;


            public Builder(List<Types.ConstructorCodec> types,
                           List<CreateType.TypeDef> typeDef,
                           Events.EventMetadataLatest meta,
                           String section, String method) {
                this.types = types;
                this.meta = meta;
                this.method = method;
                this.section = section;
                this.typeDef = typeDef;
            }

            @Override
            public EventData newInstance(Object... values) {
                return new EventData(types, (byte[]) values[0], typeDef, meta, section, method);
            }

            @Override
            public Class<EventData> getTClass() {
                return EventData.class;
            }
        }
    }

    /**
     * This follows the same approach as in {@link org.polkadot.types.primitive.Method}, we have the `[sectionIndex, methodIndex]` pairing
     * that indicates the actual event fired
     */
    public static class EventIndex extends U8aFixed {

        public EventIndex(Object value) {
            super(value, 16);
        }
    }

    // Currently we _only_ decode from Uint8Array, since we expect it to
    // be used via EventRecord
    public Event(byte[] value) {
        super((Types.ConstructorDef) decodeEvent(value)[0], decodeEvent(value)[1]);
    }

    public static Object[] decodeEvent(byte[] value) {
        if (value == null) {
            value = new byte[0];
        }

        byte[] index = new byte[0];
        if (value.length >= 2) {
            index = ArrayUtils.subarray(value, 0, 2);
        }

        Types.ConstructorCodec<EventData> dataType = EventTypes.get(Arrays.toString(index));

        Types.ConstructorDef constructorDef = new Types.ConstructorDef();
        constructorDef.add("index", EventIndex.class);

        Object[] ret = new Object[]{constructorDef, null};

        if (dataType == null) {
            logger.error("Unable to decode event for index {}", Utils.u8aToHex(index));

            constructorDef.add("data", Null.class);
            return ret;
        }

        constructorDef.add("data", dataType);
        Map<String, byte[]> values = MapUtils.ofMap("index", index, "data", ArrayUtils.subarray(value, 2, value.length));

        ret[1] = value;
        return ret;
    }

    // This is called/injected by the API on init, allowing a snapshot of
    // the available system events to be used in lookups
    public static void injectMetadata(MetadataLatest metadata) {
        for (int sectionIndex = 0; sectionIndex < metadata.getModules().size(); sectionIndex++) {
            Modules.ModuleMetadataLatest section = metadata.getModules().get(sectionIndex);
            String sectionName = Utils.stringCamelCase(section.getName().toString());

            if(section.getEvents().isSome())
            {
                Vector<Events.EventMetadataLatest> eventMetadataLatest = section.getEvents().unwrap();
                for (int methodIndex = 0; methodIndex < eventMetadataLatest.size(); methodIndex++) {
                    Events.EventMetadataLatest meta = eventMetadataLatest.get(methodIndex);
                    String methodName = meta.getName().toString();

                    byte[] eventIndex = new byte[]{UnsignedBytes.checkedCast(sectionIndex), UnsignedBytes.checkedCast(methodIndex)};
                    List<CreateType.TypeDef> typeDef = meta.getArguments().stream().map(
                            arg -> CreateType.getTypeDef(arg.toString())
                    ).collect(Collectors.toList());
                    List<Types.ConstructorCodec> types = typeDef.stream().map(def -> CreateType.getTypeClass(def)).collect(Collectors.toList());
                    EventTypes.put(Arrays.toString(eventIndex), new EventData.Builder(types, typeDef, meta, sectionName, methodName));
                }
            }
        }
    }


    /**
     * The wrapped EventData
     */
    public EventData getData() {
        return this.getField("data");
    }

    /**
     * The EventIndex, identifying the raw event
     */
    public EventIndex getIndex() {
        return this.getField("index");
    }

    /**
     * The EventMetadata with the documentation
     */
    public Events.EventMetadataLatest getMeta() {
        return this.getData().meta;
    }

    /**
     * The method string identifying the event
     */
    public String getMethod() {
        return this.getData().method;
    }

    /**
     * The section string identifying the event
     */
    public String getSection() {
        return this.getData().section;
    }

    /**
     * The TypeDef for the event
     */
    public List<CreateType.TypeDef> getTypeDef() {
        return this.getData().typeDef;
    }
}
