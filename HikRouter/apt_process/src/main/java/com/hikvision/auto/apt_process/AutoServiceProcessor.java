package com.hikvision.auto.apt_process;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.annotation.processing.Messager;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

@SupportedAnnotationTypes(value = {"com.hikvision.auto.apt_process.Describe","com.hikvision.auto.apt_process.TypeDefine","com.hikvision.auto.apt_process.DataSource"})
@SupportedSourceVersion(SourceVersion.RELEASE_7)
public class AutoServiceProcessor extends AbstractProcessor {

    private Messager messager;
    private Filer mFilerUtils;
    private Map<String,Sence> senceMap = new HashMap<>();
    private Map<String,DataFile> dataMap = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager();
        mFilerUtils = processingEnvironment.getFiler();
        senceMap.clear();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {

        if(set != null){
            if(set.isEmpty()){
                return true;
            }
        }

        Set<? extends Element> set0 = roundEnvironment.getElementsAnnotatedWith(DataSource.class);
        for(Element element : set0){

            if(element instanceof TypeElement){
                TypeElement typeElement = (TypeElement) element;
                String qualifiedName = typeElement.getQualifiedName().toString();
                DataFile file = dataMap.get(qualifiedName);
                if(file == null){
                    file = new DataFile();
                }

                DataSource source = typeElement.getAnnotation(DataSource.class);
                file.uri = source.uri();
                file.tableName = source.table();
                dataMap.put(qualifiedName, file);
            } else if(element instanceof VariableElement){
                VariableElement variableElement = (VariableElement)element;
                String name = variableElement.getEnclosingElement().toString();

                DataFile file = dataMap.get(name);
                if(file.fields == null){
                    file.fields = new HashMap<>();
                }

                String simpleName = variableElement.getSimpleName().toString();
                DataSource source = variableElement.getAnnotation(DataSource.class);
                file.fields.put(simpleName, source.type());
            }
        }

        Set<? extends Element> set1 = roundEnvironment.getElementsAnnotatedWith(Describe.class);
        for(Element element : set1){
//            messager.printMessage(Diagnostic.Kind.NOTE, "Describe element " + element.toString());

            TypeElement typeElement = (TypeElement) element;
            String qualifiedName = typeElement.getQualifiedName().toString();
//            messager.printMessage(Diagnostic.Kind.NOTE,"qulifiedName : "+ qualifiedName + ", "+ typeElement.getAnnotation(Describe.class).value() + ", "+ typeElement.getAnnotation(Describe.class).path());
//            messager.printMessage(Diagnostic.Kind.NOTE, "closeElement : "+ typeElement.getEnclosingElement().toString());

            if(!qualifiedName.equals("")){
                Sence sence = senceMap.get(qualifiedName);
                if(sence == null){
                    sence = new Sence();
                }

                sence.describe = typeElement.getAnnotation(Describe.class).value();
                sence.invoker_method = typeElement.getAnnotation(Describe.class).invoke();
                sence.routerFile = typeElement.getAnnotation(Describe.class).path();
                sence.returnType = typeElement.getAnnotation(Describe.class).returnType();
                senceMap.put(qualifiedName,sence);
            }
        }

        Set<? extends Element> set2 = roundEnvironment.getElementsAnnotatedWith(TypeDefine.class);
        for(Element element : set2){
            VariableElement variableElement = (VariableElement) element;
//            messager.printMessage(Diagnostic.Kind.NOTE, "variableElement : name"+ variableElement.getSimpleName()+ ", "+ variableElement.getEnclosingElement() + ", "+ variableElement.getAnnotation(TypeDefine.class).value());

            String name = variableElement.getEnclosingElement().toString();
            if(!name.equals("")){
                Sence sence = senceMap.get(name);
                if(sence != null){

                    String repeat = variableElement.getAnnotation(TypeDefine.class).repeat();
                    if(!repeat.equals("")){

                        Map<String, Map<String, String>> maps = sence.arrays;
                        Map<String,String> object = maps.get(repeat);
                        if(object == null){
                            object = new HashMap<>();
                        }

                        object.put(variableElement.getSimpleName().toString(), "(" + variableElement.getAnnotation(TypeDefine.class).value() +")" + " : " + variableElement.getAnnotation(TypeDefine.class).define());
                        maps.put(repeat, object);
                    } else {
                        Map<String, String> maps = sence.fileds;
                        maps.put(variableElement.getSimpleName().toString(), "(" + variableElement.getAnnotation(TypeDefine.class).value() +")" + " : " + variableElement.getAnnotation(TypeDefine.class).define());
                    }
                }
            }
        }

//        messager.printMessage(Diagnostic.Kind.NOTE, senceMap.toString());
        createDataFile();
        createFile();

        return true;
    }

    private void createDataFile(){

        try {
            FileObject fileObject = mFilerUtils.createResource(StandardLocation.SOURCE_OUTPUT, "data", "data");
            Writer writer = fileObject.openWriter();
            writer.write("################ CREATE BY YANGHUI11, DON'T MODIFY #################\n\n");

            Iterator<Map.Entry<String, DataFile>> it = dataMap.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<String, DataFile> entry = it.next();
                writer.write("数据类型定义： " + entry.getKey() + "\n");
                DataFile file = entry.getValue();
                writer.write("表名：" + file.tableName + "\n");
                writer.write("Uri : "+ file.uri + "\n");
                Map<String, String> fields = file.fields;
                writer.write("字段： " + "\n");
                Iterator<Map.Entry<String, String>> fieldIt = fields.entrySet().iterator();
                while (fieldIt.hasNext()){
                    Map.Entry<String, String> fieldEntry = fieldIt.next();
                    writer.write(fieldEntry.getKey() + " : "+ fieldEntry.getValue() + "\n");
                }
                writer.write("======================================================================================\n\n");
            }

            writer.flush();
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createFile(){

        Map<String, Map<String,Sence>> maps = new HashMap<>();

        Iterator<Map.Entry<String, Sence>> it = senceMap.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Sence> entry = it.next();
            String routerFile = entry.getValue().routerFile;
            Map<String,Sence> source = maps.get(routerFile);
            if(source == null){
                source = new HashMap<>();
            }
            source.put(entry.getKey(),entry.getValue());
            maps.put(routerFile,source);
        }

        Iterator<Map.Entry<String, Map<String, Sence>>> lastIt = maps.entrySet().iterator();
        while (lastIt.hasNext()){

            Map.Entry<String, Map<String, Sence>> entry = lastIt.next();
            String routerFile = entry.getKey();
            Map<String,Sence> sence = entry.getValue();
            writeEntry(routerFile, sence);
        }
    }


    private void writeEntry(String routerFile, Map<String,Sence> sence){

        try {
            String[] path = routerFile.split("/");
            if(path.length != 2){
                messager.printMessage(Diagnostic.Kind.ERROR,"path error, create router failed.");
                return ;
            }

            messager.printMessage(Diagnostic.Kind.NOTE,"dir : "+path[0] + ", file : "+ path[1]);
            FileObject fileObject = mFilerUtils.createResource(StandardLocation.SOURCE_OUTPUT, path[0], path[1]);
            Writer writer = fileObject.openWriter();

            writer.write("################ CREATE BY YANGHUI11, DON'T MODIFY #################\n\n");

            Iterator<Map.Entry<String, Sence>> it = sence.entrySet().iterator();
            while (it.hasNext()){
                Map.Entry<String, Sence> entry = it.next();
                String businessSence = entry.getKey();
                Sence val = entry.getValue();
                writer.write("业务场景： " + businessSence + "\n");
                writer.write("描述： " + val.describe + "\n");
                writer.write("业务组件调用接口： "+val.invoker_method + "\n");
                writer.write("入参： \n");

                Iterator<Map.Entry<String, String>> filedIt = val.fileds.entrySet().iterator();
                while (filedIt.hasNext()){
                    Map.Entry<String, String> filedEntry = filedIt.next();
                    writer.write(filedEntry.getKey() + filedEntry.getValue() +  "\n");
                }

                Iterator<Map.Entry<String, Map<String, String>>> arrayIt = val.arrays.entrySet().iterator();
                while (arrayIt.hasNext()){
                    Map.Entry<String, Map<String, String>> arrayEntry = arrayIt.next();
                    String arrayName = arrayEntry.getKey();
                    Map<String, String> arrayMap = arrayEntry.getValue();

                    writer.write("array : "+ arrayName + "[");
                    Iterator<Map.Entry<String, String>> innerIt = arrayMap.entrySet().iterator();
                    while (innerIt.hasNext()){
                        Map.Entry<String, String> innerEntry = innerIt.next();
                        writer.write("{" + innerEntry.getKey() + innerEntry.getValue() + "};");
                    }
                    writer.write("]\n");
                }
                if(val.returnType != null && !val.returnType.equals("")){
                    writer.write("返回类型： " + val.returnType + "\n");
                } else {
                    writer.write("返回类型: " + "无" + "\n");
                }
                writer.write("======================================================================================\n\n");
            }

            writer.flush();
            writer.close();

        } catch (IOException e) {
            messager.printMessage(Diagnostic.Kind.ERROR, "create router file : "+ routerFile + " failed.");
        }
    }

}
