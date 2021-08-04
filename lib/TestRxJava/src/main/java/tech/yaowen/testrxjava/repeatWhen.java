package tech.yaowen.testrxjava;

import com.sun.tools.javac.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;
import io.reactivex.rxjava3.functions.BooleanSupplier;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.functions.Predicate;

public class repeatWhen {
    public static void main(String[] args) {
        List<Integer> list = new ArrayList<Integer>();
        final int[] time = {0};
//        Observable<List<Integer>> data = Observable.create(new ObservableOnSubscribe<List<Integer>>() {
//            @Override
//            public void subscribe(@NonNull ObservableEmitter<List<Integer>> emitter) throws Throwable {
//                if (time[0] > 0) {
//                    list.add(time[0]);
//                }
//                time[0] += 1;
//                emitter.onNext(list);
//                emitter.onComplete();
//            }
//        }).repeat();
//        Observable.zip(Observable.interval(200, TimeUnit.MICROSECONDS), data, new BiFunction<Long, List<Integer>, Pair<Long, List<Integer>>>() {
//            @Override
//            public Pair<Long, List<Integer>> apply(Long aLong, List<Integer> integers) throws Throwable {
//                Pair<Long, List<Integer>> pair = new Pair<>();
//                if (aLong > 1000) {
//
//                }
//                return ;
//            }
//        });


//        Observable.zip(Observable.range(1, 5), );
        Observable.create(new ObservableOnSubscribe<List<Integer>>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<List<Integer>> emitter) throws Throwable {
                if (time[0] > 0) {
                    list.add(time[0]);
                }
                time[0] += 1;
                emitter.onNext(list);
                emitter.onComplete();
            }
        })
                .repeatUntil(() -> {
                    System.out.println("apply");
//                    if (list.size() == 0) {
//                        return completed.delay(200, TimeUnit.MICROSECONDS);
//                    } else {
//                        return null;
//                    }
                    Thread.sleep(200);
                    return  list.size() > 0;
//                    return completed.zipWith(Observable.range(3, 5), (result, retryCount) -> retryCount)
//                            .delay(retryCount -> Observable.timer((long) Math.pow(5, retryCount), TimeUnit.MICROSECONDS));
                })
//                .repeatUntil(new BooleanSupplier() {
//                    @Override
//                    public boolean getAsBoolean() throws Throwable {
//                        return list.size() > 0 && repeatTime < 10;
//                    }
//                })
                .retry(5, new Predicate<Throwable>() {
                    @Override
                    public boolean test(Throwable throwable) throws Throwable {
                        return false;
                    }
                })
                .flatMap(Observable::fromIterable)
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        System.out.println("onSubscribe");

                    }

                    @Override
                    public void onNext(@NonNull Integer o) {
                        System.out.println("onNext " + o);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        System.out.println("onError");
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("onComplete");

                    }
                });
//        Observable<String> myObservable = Observable.just("a", "b", "c");
//        myObservable.repeatWhen(completed -> completed.delay(30, TimeUnit.MICROSECONDS))
//                .subscribe(System.out::println);

        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
