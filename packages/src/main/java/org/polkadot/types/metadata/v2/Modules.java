package org.polkadot.types.metadata.v2;

import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.primitive.Text;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/15 10:42 上午
 */
public interface Modules {
    class ModuleMetadataV2 extends Struct {
        public ModuleMetadataV2(Object value) {
            super(new org.polkadot.types.Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("prefix", Text.class)
                            .add("storage", Option.with(Vector.with(TypesUtils.getConstructorCodec(Storage.MetadataStorageV2.class))))
                            .add("calls", Option.with(Vector.with(TypesUtils.getConstructorCodec(Calls.FunctionMetadataV2.class))))
                            .add("events", Option.with(Vector.with(TypesUtils.getConstructorCodec(Events.EventMetadataV2.class))))
                    , value);
        }


        /**
         * the module calls
         */
        public Option<Vector<Calls.FunctionMetadataV2>> getCalls() {
            return this.getField("calls");
        }

        /**
         * the module events
         */
        public Option<Vector<Events.EventMetadataV2>> getEvents() {
            return this.getField("events");
        }

        /**
         * the module name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * the module prefix
         */
        public Text getPrefix() {
            return this.getField("prefix");
        }

        /**
         * the associated module storage
         */
        public Option<Vector<Storage.MetadataStorageV2>> getStorage() {
            return this.getField("storage");
        }
    }


}
