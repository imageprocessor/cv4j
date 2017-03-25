package com.cv4j.core.filters;

import com.cv4j.core.datamodel.ImageProcessor;
import com.cv4j.image.util.TaskUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;

/**
 * Created by gloomy fish on 2017/3/21.
 */

public class GaussianBlurFilter implements CommonFilter {

    private int radius=5;
    private ExecutorService mExecutor;
    CompletionService<byte[]> service;

    public GaussianBlurFilter(int radius) {
        this.radius = radius;
        mExecutor = TaskUtils.newFixedThreadPool("cv4j",3);
        service = new ExecutorCompletionService<>(mExecutor);
    }

    public GaussianBlurFilter() {
        this(5);
    }

    @Override
    public ImageProcessor filter(ImageProcessor src) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        final byte[] R = src.getChannel(0);
        final byte[] G = src.getChannel(1);
        final byte[] B = src.getChannel(2);

        final GaussianByteProcessor byteProcessor = new GaussianByteProcessor(radius);
        service.submit(new Callable<byte[]>() {
            public byte[] call() throws Exception {
                return byteProcessor.process(R, width, height);
            }
        });
        service.submit(new Callable<byte[]>() {
            public byte[] call() throws Exception {
                return byteProcessor.process(G, width, height);
            }
        });
        service.submit(new Callable<byte[]>() {
            public byte[] call() throws Exception {
                return byteProcessor.process(B, width, height);
            }
        });

        for(int i=0; i<3; i++){
            try {
                service.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mExecutor.shutdown();
        return src;
    }
}
