package codepath.com.cn.rxlifecyclenon_invasive;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import cn.nekocode.rxlifecycle.LifecyclePublisher;
import cn.nekocode.rxlifecycle.RxLifecycle;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tellLifecycleState();
    }

    private void tellLifecycleState() {
        RxLifecycle.bind(this)
                .asFlowable()
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(@LifecyclePublisher.Event Integer event) throws Exception {
                        switch (event) {
                            case LifecyclePublisher.ON_START:
                                toast("Your activity is started.");
                                break;

                            case LifecyclePublisher.ON_STOP:
                                toast("Your activity is stopped.");
                                break;
                        }
                    }
                });
    }

    void testFlowableClick(View view) {
        testFlowable();
        view.setEnabled(false);
    }

    void testObservableClick(View view) {
        testObservable();
        view.setEnabled(false);
    }

    void testCompletableClick(View view) {
        testCompletable();
    }

    void testSingleClick(View view) {
        testSingle();
    }

    void testMaybeClick(View view) {
        testMaybe();
    }


    private void testFlowable() {
        Flowable.interval(0, 2, TimeUnit.SECONDS)
                .compose(RxLifecycle.bind(this).<Long>withFlowable())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long n) throws Exception {
                        toast("Flowable -> " + n.toString());
                    }
                });
    }

    private void testObservable() {
        Observable.interval(0, 2, TimeUnit.SECONDS)
                .compose(RxLifecycle.bind(this).<Long>withObservable())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long n) throws Exception {
                        toast("Observable -> " + n.toString());
                    }
                });

    }

    private void testCompletable() {
        Completable.timer(3, TimeUnit.SECONDS)
                .compose(RxLifecycle.bind(this).withCompletable())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        toast("Completable -> onComplete()");
                    }
                });
    }

    private void testSingle() {
        Single.timer(3, TimeUnit.SECONDS)
                .compose(RxLifecycle.bind(this).<Long>withSingle())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long n) throws Exception {
                        toast("Single -> onSuccess()");
                    }
                });
    }

    private void testMaybe() {
        Maybe.timer(3, TimeUnit.SECONDS)
                .compose(RxLifecycle.bind(this).<Long>withMaybe())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long n) throws Exception {
                        toast("Maybe -> onSuccess()");
                    }
                });
    }

    private void toast(String text) {
        Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT).show();
    }
}
