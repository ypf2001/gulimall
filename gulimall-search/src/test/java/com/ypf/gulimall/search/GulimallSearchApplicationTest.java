package com.ypf.gulimall.search;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.aggregations.AggregateBuilders;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.util.ObjectBuilder;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ypf.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.concurrent.*;


/**
 * @program: gulimall
 * @author: yanpengfan
 * @create: 2022-06-09 21:26
 **/
@SpringBootTest

public class GulimallSearchApplicationTest {
    @Autowired
    private ElasticsearchClient client;

    @Test
    public void test1() {
        System.out.println(client);
    }
    @Test
    public void test2() throws IOException {
        user user = new user();
        user.age = "1";
        user.name = "ypf";
        user.gender = "男";
        IndexRequest request = IndexRequest.of(i->
                     i.index("user").document(user)
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(user);
        IndexResponse response = client.index(request);
        System.out.println("Indexed with version " + response.version());
    }
    @JsonIgnoreProperties(value = { "hibernateLazyInitializer"})
    class user {
        String name;
        String age;
        String gender;
    }
    @Test
    public  void searchData() throws IOException {
        Query query =Query.of(f->
            f.queryString(q->
                q.fields("address").query("mill")
            )
        );
        Aggregation aggregation1 = Aggregation.of(f ->
            f.terms(t->t.field("age").size(10))
        );
        Aggregation aggregation2 = Aggregation.of(f ->
                f.avg(t->t.field("age"))
        );
        SearchRequest searchRequest =SearchRequest.of(item->
            item.query(query)
                    .aggregations("ageA",aggregation1).aggregations("avg",aggregation2)
        );


        SearchResponse<Object> search = client.search(searchRequest, Object.class);

        System.out.println(search.took());
       search.hits().hits().forEach(item->{
           System.out.println(item.source());
       });

    }
    @Test
    public  void  testThread() throws ExecutionException, InterruptedException {
        CompletableFuture future = CompletableFuture.supplyAsync(()->{
            return  1;
        }).whenComplete((res,e)->{
            System.out.println(res);
        }).exceptionally((throwable)->{
            System.out.println(throwable.getMessage());
            return 1;
        });

//线程池初始化
//        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(15, 10,
//                0L, TimeUnit.MILLISECONDS,
//                new LinkedBlockingQueue<Runnable>(10));//添加容量大小);
//        CompletableFuture future1 = CompletableFuture.supplyAsync(()->{
//            return  1;
//        },threadPoolExecutor).thenApplyAsync(res->{
//            return res+1;
//        },threadPoolExecutor);
        ExecutorService executorService= Executors.newSingleThreadExecutor();
        Future<Double> cf = executorService.submit(()->{
            System.out.println(Thread.currentThread()+" start,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            if(false){
                throw new RuntimeException("test");
            }else{
                System.out.println(Thread.currentThread()+" exit,time->"+System.currentTimeMillis());
                return 1.2;
            }
        });
        System.out.println("main thread start,time->"+System.currentTimeMillis());
        //等待子任务执行完成,如果已完成则直接返回结果
        //如果执行任务异常，则get方法会把之前捕获的异常重新抛出
        System.out.println("run result->"+cf.get());
        //通常的线程池接口类ExecutorService，其中execute方法的返回值是void，
        // 即无法获取异步任务的执行状态，3个重载的submit方法的返回值是Future，
        // 可以据此获取任务执行的状态和结果，示例如下：

    }
    @Test
    public void ExecutorServiceTest() throws ExecutionException, InterruptedException {
            ExecutorService es = Executors.newSingleThreadExecutor();
            Future<Double> cf =  es.submit(()->{
                System.out.println(Thread.currentThread()+" start,time->"+System.currentTimeMillis());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {

                }
                if(false){
                    throw new RuntimeException("test");
                }else {
                    System.out.println(Thread.currentThread() + " exit,time->" + System.currentTimeMillis());
                    return 1.2;
                }
            });
        System.out.println("异步结果为"+cf.get());
    }
    @Test
    public void asyncCallTest(){
        ForkJoinPool pool=new ForkJoinPool();
        // 创建异步执行任务:
        CompletableFuture<Double> cf = (CompletableFuture<Double>) CompletableFuture.supplyAsync(()->{
            System.out.println(Thread.currentThread()+" start job1,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job1,time->"+System.currentTimeMillis());
            return 1.2;
        },pool).thenApply((res)->{
            System.out.println(Thread.currentThread()+" start job2,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job2,time->"+System.currentTimeMillis());
            return res;

        });


    }

    public void threadHandleTest(){
        ForkJoinPool pool=new ForkJoinPool();
        // 创建异步执行任务:
        CompletableFuture<Double> cf = CompletableFuture.supplyAsync(()->{
            System.out.println(Thread.currentThread()+" start job1,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job1,time->"+System.currentTimeMillis());
            return 1.2;
        });
        CompletableFuture<Double> cf2 = CompletableFuture.supplyAsync(()->{
            System.out.println(Thread.currentThread()+" start job2,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job2,time->"+System.currentTimeMillis());
            return 3.2;
        });
        //cf和cf2的异步任务都执行完成后，会将其执行结果作为方法入参传递给cf3,且有返回值
        CompletableFuture<Double> cf3=cf.thenCombine(cf2,(a,b)->{
            System.out.println(Thread.currentThread()+" start job3,time->"+System.currentTimeMillis());
            System.out.println("job3 param a->"+a+",b->"+b);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job3,time->"+System.currentTimeMillis());
            return a+b;
        });
        //cf和cf2的异步任务都执行完成后，会将其执行结果作为方法入参传递给cf3,无返回值
        CompletableFuture cf4=cf.thenAcceptBoth(cf2,(a,b)->{
            System.out.println(Thread.currentThread()+" start job4,time->"+System.currentTimeMillis());
            System.out.println("job4 param a->"+a+",b->"+b);
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            System.out.println(Thread.currentThread()+" exit job4,time->"+System.currentTimeMillis());
        });

        //cf4和cf3都执行完成后，执行cf5，无入参，无返回值
        CompletableFuture cf5=cf4.runAfterBoth(cf3,()->{
            System.out.println(Thread.currentThread()+" start job5,time->"+System.currentTimeMillis());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("cf5 do something");
            System.out.println(Thread.currentThread()+" exit job5,time->"+System.currentTimeMillis());
        });


    }
}
