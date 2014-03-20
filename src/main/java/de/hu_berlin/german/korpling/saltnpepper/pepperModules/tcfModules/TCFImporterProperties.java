package de.hu_berlin.german.korpling.saltnpepper.pepperModules.tcfModules;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class TCFImporterProperties extends PepperModuleProperties {
	/** property that sais if spans are always used in tagging of tokens or only if necessary. Value of String has to be "true" or "false". */
	public static final String PROP_SHRINK_TOKEN_ANNOTATIONS = "shrinkTokenAnnotations";
	
	public TCFImporterProperties(){
		addProperty(new PepperModuleProperty<Boolean>(PROP_SHRINK_TOKEN_ANNOTATIONS, Boolean.class, "property that sais if spans are always used in tagging of tokens or only if necessary. Value of String has to be \"true\" or \"false\".", true, true));
	}
	
	public boolean isShrinkTokenAnnotation(){
		boolean retVal = false;
		String prop = getProperty(PROP_SHRINK_TOKEN_ANNOTATIONS).getValue().toString();
		if((prop!=null)&&(!prop.isEmpty())){
			retVal = Boolean.valueOf(prop);
		}
		return retVal;
	}
}
