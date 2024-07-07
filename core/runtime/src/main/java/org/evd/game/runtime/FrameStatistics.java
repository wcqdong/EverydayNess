package org.evd.game.runtime;

import org.evd.game.runtime.support.LogCore;

public class FrameStatistics {

    /** 帧频重计算间隔 */
    private final static long RESET_INTERVAL = 3 * TimeUtils.SEC;
    /** 打印线程帧频间隔 */
    private final static long PRINT_INTERVAL = 2 * TimeUtils.MIN;

    private TickCase tickCase;
    /** 平均时间 */
    private double avgTime;
    /** 帧频 */
    private double frameFrequency;
    /** 次数 */
    private int n;

    /** 平均时间 */
    private double print_avgTime;
    /** 次数 */
    private int print_n;

    private long resetTime = System.currentTimeMillis() + RESET_INTERVAL;
    private long printTime = System.currentTimeMillis() + PRINT_INTERVAL;

    FrameStatistics(TickCase tickCase){
        this.tickCase = tickCase;
    }

    public void tick_t(long timeCur, double tick) {
        ++n;
        avgTime = ((n - 1) * avgTime + tick) / n;

        // 每一段时间重新计算，避免时间过久的帧频影响近期的平均帧频
        if(timeCur > resetTime){
            resetTime += RESET_INTERVAL;
            frameFrequency = avgTime;
            n = 0;
            avgTime = 0;
        }

        // 统计一段时间内的平均帧频
        ++print_n;
        print_avgTime = ((print_n - 1) * print_avgTime + tick) / print_n;

        if(timeCur > printTime){
            printTime += PRINT_INTERVAL;
            LogCore.statistics.info("【{}】线程心跳频率 {}ms", tickCase.getId(), (int)print_avgTime);
            print_n = 0;
            print_avgTime = 0;
        }
    }

    int getFrameFrequency(){
        return (int)frameFrequency;
    }

//    public static void main(String[] args) {
//        FrameStatistics f = new FrameStatistics(null);
//
//        List<Double> list = new ArrayList<>();
//        for(int i=0; i<10; i++){
//            double a = RandomUtils.nextDouble(10, 100);
//            list.add(a);
//        }
//
//        double avg = list.stream().reduce(Double::sum).get() / list.size();
//        System.out.println("普通计算" + avg);
//
//        list.forEach(v -> f.tick(System.currentTimeMillis(), v));
//
//        System.out.println(f.print_avgTime);
//
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//    }

}
