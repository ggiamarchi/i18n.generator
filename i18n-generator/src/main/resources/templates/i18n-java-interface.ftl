<#if interfacePackageName != "">package ${interfacePackageName};</#if>

/**
 * Messages for resource bundle ${bundleName}
 */
public interface ${interfaceName} {

<#list methods as method>
    /**
     * Value for property "${method.property}"
     *
<#list method.parameters as p>
     * @param ${p}
</#list>
     */
    public String ${method.name}(<#list method.parameters as p>String ${p}<#if p_has_next>, </#if></#list>);

</#list>
}
