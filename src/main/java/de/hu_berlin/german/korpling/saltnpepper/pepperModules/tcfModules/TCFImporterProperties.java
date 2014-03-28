package de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class TCFImporterProperties extends PepperModuleProperties {
	/** property that sais if spans are always used in tagging of tokens or only if necessary. Value of String has to be "true" or "false". */
	public static final String PROP_SHRINK_TOKEN_ANNOTATIONS = "shrinkTokenAnnotations";
	/** if this property is "true" spans nodes are reused by {@link TCFMapperImport} and all annotations are stored at a common span. In case "false" on each level tcf annotation level a new span is created. **/
	public static final String PROP_USE_COMMON_ANNOTATED_ELEMENT = "useCommonAnnotatedElement";
	
	public TCFImporterProperties(){
		addProperty(new PepperModuleProperty<Boolean>(PROP_SHRINK_TOKEN_ANNOTATIONS, Boolean.class, "property that sais if spans are always used in tagging of tokens or only if necessary. Value of String has to be \"true\" or \"false\".", true, true));
		addProperty(new PepperModuleProperty<Boolean>(PROP_SHRINK_TOKEN_ANNOTATIONS, Boolean.class, "if this property is \"true\" spans nodes are reused by TCFMapperImport and all annotations are stored at a common span. In case \"false\" on each level tcf annotation level a new span is created. Value of String has to be \"true\" or \"false\".", false, false));
	}
	
	public boolean isShrinkTokenAnnotation(){
		boolean retVal = false;
		String prop = getProperty(PROP_SHRINK_TOKEN_ANNOTATIONS).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
	
	public boolean isUseCommonAnnotatedElement(){
		boolean retVal = false;
		String prop = getProperty(PROP_USE_COMMON_ANNOTATED_ELEMENT).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
}
