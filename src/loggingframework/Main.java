package loggingframework;

import loggingframework.entity.Logger;
import loggingframework.entity.LoggerFactory;
import loggingframework.entity.LoggingConfiguration;
import loggingframework.enums.Level;
import loggingframework.format.PlainMessageFormatter;
import loggingframework.output.ConsoleOutput;
import loggingframework.service.LogManager;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {
        LoggingConfiguration configuration = new LoggingConfiguration();
        configuration.setLevel(Level.INFO);
        configuration.setDestinations(List.of(new ConsoleOutput(new PlainMessageFormatter())));

        LogManager manager = LogManager.getINSTANCE();
        manager.updateConfig(configuration);


        Logger logger = LoggerFactory.getLogger(Main.class);


        ThreadPoolExecutor executor = new ThreadPoolExecutor(3,5,5, TimeUnit.SECONDS, new ArrayBlockingQueue<>(5));


        for(int i = 0; i< 10; i++){
            try{
                executor.submit(()->{
                    logger.log(Level.INFO, "Hello Word");
                    logger.log(Level.DEBUG, "this is a debug msg");

                    LoggingConfiguration config = new LoggingConfiguration();
                    config.setLevel(Level.WARNING);
                    manager.updateConfig(config);
                    logger.info("this is an info log");
                    logger.warn("this is a warning log");
                    logger.error("this is an error log");
                });

                logger.info("提交任务成功");
            }catch (RejectedExecutionException e){
                logger.error("提交任务失败");
            }
        }

        executor.shutdown();
        try {
           if(! executor.awaitTermination(10, TimeUnit.SECONDS)){
               logger.error("main executor did not terminate in time");
           }

        }catch ( InterruptedException e){
            executor.shutdown();
            Thread.currentThread().interrupt();
        }
        manager.shutdown();

    }

}
