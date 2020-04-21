package org.polkadot.types.metadata.latest;

import org.polkadot.types.Types;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.v11.Constants;
import org.polkadot.types.metadata.v11.Errors;
import org.polkadot.types.metadata.v11.StorageV11;
import org.polkadot.types.primitive.Text;

import java.util.Arrays;

/**
 * @author Jenner
 * @version V1.0
 * @Date 2020/4/16 3:56 下午
 */
public interface Modules {
    /**
     * The definition of a module in the system
     */
    class ModuleMetadataLatest extends Struct {

        public ModuleMetadataLatest(Object value) {
            super(new Types.ConstructorDef()
                            .add("name", Text.class)
                            .add("storage", Option.with(TypesUtils.getConstructorCodec(Storage.StorageMetadataLatest.class)))
                            .add("calls", Option.with(Vector.with(TypesUtils.getConstructorCodec(Calls.FunctionMetadataLatest.class))))
                            .add("events", Option.with(Vector.with(TypesUtils.getConstructorCodec(Events.EventMetadataLatest.class))))
                            .add("constants", Vector.with(TypesUtils.getConstructorCodec(Constants.ModuleConstantMetadataV11.class)))
                            .add("errors", Vector.with(TypesUtils.getConstructorCodec(Errors.ErrorMetadataV11.class)))
                    , value);
        }

        /**
         * the module calls
         */
        public Option<Vector<Calls.FunctionMetadataLatest>> getCalls() {
            return this.getField("calls");
        }

        /**
         * the module events
         */
        public Option<Vector<Events.EventMetadataLatest>> getEvents() {
            return this.getField("events");
        }

        /**
         * the associated module storage
         */
        public Option<Storage.StorageMetadataLatest> getStorage() {
            return this.getField("storage");
        }

        /**
         * the module name
         */
        public Text getName() {
            return this.getField("name");
        }

        /**
         * the module constants
         */
        public Vector<Constants.ModuleConstantMetadataV11> getConstants() {
            return this.getField("constants");
        }

        /**
         * the module errors
         */
        public Vector<Errors.ErrorMetadataV11> getErrors() {
            return this.getField("errors");
        }
    }
}
