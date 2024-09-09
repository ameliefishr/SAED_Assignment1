package edu.curtin.saed.assignment1;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

// NamedThreadFactory Class
// this is just a class to help me label my thread pools for debugging, it is not used at all in the simulation
// code was adapted from: https://stackoverflow.com/questions/6113746/naming-threads-and-thread-pools-of-executorservice
public class NamedThreadFactory implements ThreadFactory
{
    private final String baseName;
    private final AtomicInteger threadCount = new AtomicInteger(1);

    public NamedThreadFactory(String baseName)
    {
        this.baseName = baseName;
    }

    @Override
    public Thread newThread(Runnable r)
    {
        Thread t = new Thread(r);
        t.setName(baseName + "-thread-" + threadCount.getAndIncrement());
        return t;
    }
}

