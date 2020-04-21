package io.onee.xjc;

import com.sun.tools.xjc.Options;

import java.time.LocalDateTime;

/**
 * Created by admin on 2020/4/21 16:22:42.
 */
public class LogUtil {
    
    public static void Console(Options opt, String detail) {
        if (opt.debugMode) {
            System.out.println(LocalDateTime.now() + "： "+detail);
        }
    }
}
