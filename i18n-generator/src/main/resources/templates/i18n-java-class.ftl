<#if classPackageName != "">package ${classPackageName};</#if>

import java.util.Locale;

import com.github.ggiamarchi.i18n.runtime.I18NSupport;

<#if classPackageName != interfacePackageName>
import ${interfacePackageName}.${interfaceName};
</#if>

public class ${className} extends I18NSupport implements ${interfaceName} {

    // Messages corresponding to properties file

<#list methods as method>
    @Override
    public String ${method.name}(<#list method.parameters as p>String ${p}<#if p_has_next>, </#if></#list>) {
        return getMessage("${method.property}"<#list method.parameters as p>, ${p}</#list>);
    }

</#list>

    // Technical stufs

    private static final String BUNDLE_NAME = "${bundleName}";

    @Override
    protected String getBundleName() {
        return BUNDLE_NAME;
    }

    public ${className}() {

    }

    public ${className}(Locale locale) {
        super(locale);
    }

}
