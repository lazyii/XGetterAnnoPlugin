package io.onee.xjc;

import com.sun.tools.xjc.Driver;
import org.junit.Test;

/**
 * Created by admin on 2020/4/20 13:09:53.
 */
public class XjcTest {
    @Test
    public void sd() throws Exception {
        //java -Dfile.encoding=UTF-8 -cp
        // "C:\Program Files\Java\jdk8u212-b04\lib\tools.jar"
        // com.sun.tools.internal.xjc.Driver
        // -p io.onee
        // -d d:\srv
        // C:\Users\admin\Desktop\ofd_reader\xsd\*.xsd
        
        //,"-fullversion"
        //"-classpath", "d:/srv/plugin.jar",
//        System.setProperty("com.sun.tools.internal.xjc.Driver.noThreadSwap", "true");
//        System.setProperty("com.sun.tools.xjc.Options.findServices", "true");
        //jdk bug，此属性必须设置
        System.setProperty("javax.xml.accessExternalSchema", "all");
        String[] args = {"-p", "io.onee", "-d", "d:/srv", "-Xpropertyaccessors", "-XexplicitAnnotation", "-XGetterAnnoPlugin", "-debug", "-verbose", "C:/Users/admin/Desktop/ofd_reader/xsd"};
        Driver.main(args);//执行完成后退出 jvm
    }
    
}
