package org.polkadot.example.rx;

import io.reactivex.Observable;
import org.polkadot.api.SubmittableExtrinsic;
import org.polkadot.api.rx.ApiRx;
import org.polkadot.common.keyring.Keyring;
import org.polkadot.common.keyring.Types;
import org.polkadot.rpc.provider.ws.WsProvider;


public class E06_MakeTransfer {
    static String Alice = "5GrwvaEF5zXb26Fz9rcQpDWS57CtERHpNehXCPcNoHGKutQY";

    //static String endPoint = "wss://poc3-rpc.polkadot.io/";
    //static String endPoint = "wss://substrate-rpc.parity.io/";
    //static String endPoint = "ws://45.76.157.229:9944/";
    static String endPoint = "ws://127.0.0.1:9944";

    static void initEndPoint(String[] args) {
        if (args != null && args.length >= 1) {
            endPoint = args[0];
            System.out.println(" connect to endpoint [" + endPoint + "]");
        } else {
            System.out.println(" connect to default endpoint [" + endPoint + "]");
        }
    }

    static {
        System.loadLibrary("jni");
        System.out.println("load ");
    }

    //-Djava.library.path=./libs
    public static void main(String[] args) throws InterruptedException {
        initEndPoint(args);

        WsProvider wsProvider = new WsProvider(endPoint);

        String BOB = "5FHneW46xGXgs5mUiveU4sbTyGBzmstUspZC92UhjJM694ty";

        Observable<ApiRx> apiRxObservable = ApiRx.create(wsProvider);

        apiRxObservable.flatMap((apiRx) -> {
            Types.KeyringOptions options = new Types.KeyringOptions(org.polkadot.utils.crypto.Types.KeypairType_SR);
            Keyring keyring = new Keyring(options);
            Types.KeyringPair alice = keyring.addFromUri("//Alice", null, options.getType());
            org.polkadot.api.Types.SubmittableExtrinsicFunction function = apiRx.tx().section("balances").function("transfer");
            SubmittableExtrinsic<Observable> transfer = function.call(BOB, 111);
            try {
                return transfer.signAndSend(alice, new org.polkadot.types.Types.SignatureOptions());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return null;
        }).subscribe((result) -> {
            System.out.println("Transfer sent with hash " + result);
        });

    }
}
