package org.polkadot.types.metadata.v11;

import com.google.common.collect.Lists;
import org.polkadot.types.Types.ConstructorDef;
import org.polkadot.types.TypesUtils;
import org.polkadot.types.codec.Option;
import org.polkadot.types.codec.Struct;
import org.polkadot.types.codec.Vector;
import org.polkadot.types.metadata.MagicNumber;
import org.polkadot.types.metadata.MetadataUtils;
import org.polkadot.types.metadata.Types;
import org.polkadot.types.metadata.v1.Calls;
import org.polkadot.types.metadata.v1.Events;
import org.polkadot.types.primitive.Text;
import org.polkadot.types.primitive.U32;
import org.polkadot.types.primitive.U8;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MetadataV11 extends Struct implements Types.MetadataInterface {


    /**
     * The definition of a module in the system
     */
    public static class MetadataModule extends Struct {
        public MetadataModule(Object value) {
            super(new ConstructorDef()
                            .add("name", Text.class)
                            .add("storage", Option.with(TypesUtils.getConstructorCodec(Storage.MetadataStorageV11.class)))
                            .add("calls", Option.with(Vector.with(TypesUtils.getConstructorCodec(Calls.MetadataCall.class))))
                            .add("events", Option.with(Vector.with(TypesUtils.getConstructorCodec(Events.MetadataEvent.class))))
                            .add("constants", Vector.with(TypesUtils.getConstructorCodec(Constants.MetadataConstant.class)))
                            .add("errors", Vector.with(TypesUtils.getConstructorCodec(Errors.MetadataError.class)))
                    , value);
        }


        /**
         * the module calls
         */
        public Option<Vector<Calls.MetadataCall>> getCalls() {
            return this.getField("calls");
        }

        /**
         * the module events
         */
        public Option<Vector<Events.MetadataEvent>> getEvents() {
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
        public Option<Vector<Storage.MetadataStorageV11>> getStorage() {
            return this.getField("storage");
        }

        /**
         * the module constants
         */
        public Option<Vector<Constants.MetadataConstant>> getConstants() {
            return this.getField("constants");
        }

        /**
         * the module errors
         */
        public Option<Vector<Errors.MetadataError>> getErrors() {
            return this.getField("errors");
        }
    }

    public static class MetadataExtrinsic extends Struct {
        public MetadataExtrinsic(Object value) {
            super(new ConstructorDef()
                            .add("version", U8.class)
                            .add("signedExtensions", Vector.with(TypesUtils.getConstructorCodec(Text.class)))
                    , value);
        }

        /**
         * the version
         */
        public U32 getVersion() {
            return this.getField("version");
        }

        /**
         * the signedExtensions
         */
        public Option<Vector<Text>> getSignedExtensions() {
            return this.getField("signedExtensions");
        }
    }


    public MetadataV11(Object value) {
        super(new ConstructorDef()
                        .add("modules", Vector.with(TypesUtils.getConstructorCodec(MetadataModule.class)))
                        .add("extrinsic", TypesUtils.getConstructorCodec(MetadataExtrinsic.class))
                , value);
    }

    /**
     * The associated modules for this structure
     */
    Vector<MetadataModule> getModules() {
        return this.getField("modules");
    }

    private List getCallNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getCalls().isNone()
                            ? Lists.newArrayList()
                            : mod.getCalls().unwrap().stream().map(
                            (fn) -> {
                                return fn.getArgs().stream().map(
                                        (arg) -> {
                                            return arg.getType().toString();
                                        }
                                ).collect(Collectors.toList());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }

    private List getEventNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getEvents().isNone()
                            ? Lists.newArrayList()
                            : mod.getEvents().unwrap().stream().map(
                            (event) -> {
                                return event.getArgs().stream().map(
                                        (arg) -> arg.toString()
                                ).collect(Collectors.toList());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }


    private List getStorageNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getStorage().isNone()
                            ? Lists.newArrayList()
                            : mod.getStorage().unwrap().stream().map(
                            (storage) -> {
                                return storage.getItems().stream().map(
                                        (fn) -> {
                                            return fn.getType().isMap()
                                                    ? Lists.newArrayList(fn.getType().asMap().getKey().toString(), fn.getType().asMap().getValue().toString())
                                                    : Lists.newArrayList(fn.getType().asType().toString());
                                        }
                                ).collect(Collectors.toList());
                            }
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }

    private List getConstantNames() {
        return this.getModules().stream().map(
                (mod) -> {
                    return mod.getConstants().isNone()
                            ? Lists.newArrayList()
                            : mod.getConstants().unwrap().stream().map(
                            (constant) -> constant.getType().toString()
                    ).collect(Collectors.toList());
                }
        ).collect(Collectors.toList());
    }


    @Override
    public List<String> getUniqTypes(boolean throwError) {

        List<Object> types = MetadataUtils.flattenUniq(Lists.newArrayList(
                this.getStorageNames(),
                this.getCallNames(),
                this.getEventNames(),
                this.getConstantNames()
        ));
        List<String> ret = new ArrayList<>();
        types.forEach(type -> ret.add((String) type));

        MetadataUtils.validateTypes(ret, throwError);

        return null;
    }
}
