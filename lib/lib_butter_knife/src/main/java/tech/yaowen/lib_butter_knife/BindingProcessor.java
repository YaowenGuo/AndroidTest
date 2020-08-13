package tech.yaowen.lib_butter_knife;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.util.Collections;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.TypeElement;

import tech.yaowen.test_annotation.Binding;
import tech.yaowen.test_annotation.ButterKnife;


public class BindingProcessor extends AbstractProcessor {
    Filer filer;
    @Override
    public synchronized void init(ProcessingEnvironment pe) {
        super.init(pe);
        filer = pe.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        System.out.println("Processing");
        ClassName className = ClassName.get("tech.yaowen.lib_butter_knife", "Test");
        TypeSpec builtClass = TypeSpec.classBuilder(className).build();
        try {
            JavaFile.builder("tech.yaowen.lib_butter_knife", builtClass)
                    .build()
                    .writeTo(filer);
        } catch (Exception e) {

        }
        return false;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(ButterKnife.class.getCanonicalName());
    }
}
