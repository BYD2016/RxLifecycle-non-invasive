package codepath.com.cn.rxlifecyclenon_invasive;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.trello.navi2.Event;
import com.trello.navi2.NaviComponent;
import com.trello.navi2.component.support.NaviAppCompatActivity;
import com.trello.navi2.rx.RxNavi;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.Nullable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public final class NaviSimpleActivity extends NaviAppCompatActivity {

    /**
     * This demonstrates we don't necessarily need a reference to the <code>Activity</code> itself in order
     * to hook into its lifecycle; the <code>NaviActivity</Code> could be provided by anyone.
     */
    private final NaviComponent naviComponent = this;

    TextView counter;

    public static void launchActivity(Context context) {
        context.startActivity(new Intent(context, NaviSimpleActivity.class));
    }

    public NaviSimpleActivity() {

        // Instead of using onCreate, we can use Observables

        RxNavi.observe(naviComponent, Event.CREATE)
                .subscribe(new Consumer<Bundle>() {
                    @Override
                    public void accept(@Nullable Bundle bundle) throws Exception {
                        setContentView(R.layout.activity_navi_simple);
                        counter = (TextView) findViewById(R.id.counter);
                    }
                });

        // Counter that operates on screen only while resumed; automatically ends itself on destroy

        RxNavi.observe(naviComponent, Event.RESUME)
                .flatMap(new Function<Object, Observable<Long>>() {
                    @Override
                    public Observable<Long> apply(Object ignore) throws Exception {
                        return Observable.interval(1, TimeUnit.SECONDS)
                                .takeUntil(RxNavi.observe(naviComponent, Event.PAUSE));
                    }
                })
                .takeUntil(RxNavi.observe(naviComponent, Event.DESTROY))
                .startWith(-1L)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long count) throws Exception {
                        counter.setText("Num seconds resumed: " + (count + 1));
                    }
                });
    }
}
