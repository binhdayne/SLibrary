package com.qlthuvien.utils;

import javafx.concurrent.Task;

import java.util.concurrent.Callable;
import java.util.function.Consumer;

public class DatabaseTask<T> extends Task<T> {

    private final Callable<T> databaseQuery; // Callable để thực hiện truy vấn
    private final Consumer<T> onSuccess; // Hàm xử lý khi truy vấn thành công
    private final Consumer<Throwable> onError; // Hàm xử lý khi có lỗi

    public DatabaseTask(Callable<T> databaseQuery, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        this.databaseQuery = databaseQuery;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    protected T call() throws Exception {
        // trễ 1s
        Thread.sleep(1000);
        // Thực hiện truy vấn cơ sở dữ liệu trong luồng riêng
        return databaseQuery.call();
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        // Khi thành công, gọi hàm onSuccess để cập nhật giao diện
        onSuccess.accept(getValue());
    }

    @Override
    protected void failed() {
        super.failed();
        // Khi thất bại, gọi hàm onError để xử lý lỗi
        onError.accept(getException());
    }

    public static <T> void run(Callable<T> query, Consumer<T> onSuccess, Consumer<Throwable> onError) {
        DatabaseTask<T> task = new DatabaseTask<>(query, onSuccess, onError);
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
