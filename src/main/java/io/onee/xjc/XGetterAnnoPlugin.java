package io.onee.xjc;

import com.sun.codemodel.*;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import org.xml.sax.ErrorHandler;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by admin on 2020/4/19 22:29:38.
 */
public class XGetterAnnoPlugin extends Plugin {
    
    @Override
    public String getOptionName() {
        return "XGetterAnnoPlugin";
    }
    
    @Override
    public String getUsage() {
        return "  -" + "XGetterAnnoPlugin" + "    : enable rewriting of classes to set default values for fields as specified in XML schema";
    }
    
    @Override
    public boolean run(Outline outline, Options opt, ErrorHandler errorHandler) {
        Optional<String> propertyaccessors = opt.activePlugins
                .stream()
                .map(Plugin::getOptionName)
                .filter(x -> x.equals("Xpropertyaccessors"))
                .findAny();
        if (!propertyaccessors.isPresent()) {
            throw new RuntimeException("Please enable propertyaccessors plugin. make sure Xpropertyaccessors is in front of XGetterAnnoPlugin ");
        }
        //此处将field上的注解移动至getter
        //outline.getclasses()
        //outline.getModel()
        //outline.getEnums()
        
        
        System.out.println("XGetterAnnoPlugin => Run => move field annotations to getter method");
        Collection<? extends ClassOutline> classes = outline.getClasses();
        for (ClassOutline co : outline.getClasses()) {
            System.out.println();
            // check all Fields in Class
            for (FieldOutline f : co.getDeclaredFields()) {
                LogUtil.Console(opt, "Processing...  XGetterAnnoPlugin =>  " + f.getPropertyInfo().displayName());
                JFieldVar field = co.ref.fields().get(f.getPropertyInfo().getName(false));
                String getterName = this.getGetterMethod(f, field);
                JMethod getter = co.ref.getMethod(getterName, new JType[]{});
                //交换annotations
                for (JAnnotationUse fieldAnno : field.annotations()) {
                    
                    JAnnotationUse getterAnno = getter.annotate(fieldAnno.getAnnotationClass());
                    Map<String, JAnnotationValue> mem = fieldAnno.getAnnotationMembers();
                    for (Map.Entry<String, JAnnotationValue> memEntry : mem.entrySet()) {
                        getterAnno.getAnnotationMembers().size();
                        this.addAnnotationToJMethod(getterAnno, memEntry.getKey(), memEntry.getValue());
                    }
                }
                //移除 field上的注解
                for (JAnnotationUse rmAnno : field.annotations().stream().collect(Collectors.toList())) {
                    LogUtil.Console(opt, "Removing...  " + f.getPropertyInfo().displayName() + " @" + rmAnno
                            .getAnnotationClass()
                            .name());
                    field.removeAnnotation(rmAnno);
                }
                if (field.annotations().size() > 0) {
                    throw new RuntimeException(f
                            .getPropertyInfo()
                            .displayName() + " still have one annotation at least; annoSize: " + field
                            .annotations()
                            .size());
                }
            }
        }
        //[class com.sun.codemodel.JAnnotationArrayMember, class com.sun.codemodel.JAnnotationStringValue]
        LogUtil.Console(opt, "XGetterAnnoPlugin => Run => all annotations have been moved successfully");
        return true;
    }
    
    protected String getGetterMethod(FieldOutline fieldOutline, JFieldVar fieldVar) {
        Options options = fieldOutline.parent().parent().getModel().options;
        JType fieldType = fieldVar.type();
        if (options.enableIntrospection) {
            String name = ((fieldType.isPrimitive() && fieldType
                    .boxify()
                    .getPrimitiveType() == fieldType.owner().BOOLEAN) ? "is" : "get");
            name = name + fieldOutline.getPropertyInfo().getName(true);
            return name;
        } else {
            String name = fieldType.boxify().getPrimitiveType() == fieldType.owner().BOOLEAN ? "is" : "get";
            name = name + fieldOutline.getPropertyInfo().getName(true);
            return name;
        }
    }
    
    /**
     * 反射，设置 annotations
     *
     * @param annotation
     * @param name
     * @param memberValue
     */
    public void addAnnotationToJMethod(JAnnotationUse annotation, String name, JAnnotationValue memberValue) {
        try {
            Class<JAnnotationUse> jMethodClass = JAnnotationUse.class;
            Method addValueMethod = jMethodClass.getDeclaredMethod("addValue", String.class, JAnnotationValue.class);
            addValueMethod.setAccessible(true);
            addValueMethod.invoke(annotation, name, memberValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
