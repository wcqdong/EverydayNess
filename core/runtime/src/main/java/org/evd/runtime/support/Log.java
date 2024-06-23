package org.evd.runtime.support;


import org.evd.runtime.Service;

public class Log {
    public static void console(String str, Object... args){
        Service service = Service.GetCurrent();
        if (service != null){
            System.out.println("[" + Thread.currentThread().getName() +"]  [ " + service.getName() + " ]  "+ Utils.createStr(str, args));
        }else{
            System.out.println("[" + Thread.currentThread().getName() +"]  no service  "+ Utils.createStr(str, args));
        }
    }

    public static void warning(String str, Object... args){
        console(str, args);
    }

    public static void error(String str, Object... args){
        console(str, args);
    }
}
